package com.btb.tlsm;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.btb.tlsm.event.Event;
import com.btb.tlsm.netty.MMMClientHandler;
import com.btb.tlsm.vo.Mail;

/**
 * 서비스 확인은 30분 마다 한다.
 * 서비스 상태 확인 -> 장애일 경우 Monitoring Mail Manager(MMM)에 메일을 보내라고 요청한다.
 * 장애 발생 후 처음 한번만 메일을 보내며, 정상으로 복구된 후 다시 장애가 발생해야 메일을 보낸다.
 * 
 * 서비스 상태 확인 -> 장애 -> 메일O -> 서비스 상태 확인 -> 장애 -> 메일X ...
 * 서비스 상태 확인 -> 장애 -> 메일O -> 서비스 상태 확인 -> 정상 -> 메일O ...
 * 
 * @author iskwon
 */
public class TLSM {

	// Global static members.
	public static final Logger logger = Logger.getLogger("TLSM"); // thread-safe

	private static String tlasAddr = null;
	private static String authUrl = null;
	private static String serialKey = null;
	private static String macAddress = null;
	private static String mmmIp = null;
	private static int mmmPort;
	
	private static int responseCode;
	private static boolean sentMail = false;
	private static boolean serviceError = false;
	
	public static Mail mail = new Mail();
	
	public static void eventHandler(Event msg) {
		switch (msg.id) {
		case Event.MMM_RESULT:
			setSentMail((Boolean) msg.param);
			break;
		default:
			break;
		}
	}
	
	private static void setSentMail(boolean result) {
		sentMail = result;
	}
	
	private static void setupLogger() {
		Layout layout = new PatternLayout("%d{yy/MM/dd HH:mm:ss,SSS} %5p - %m%n");
		ConsoleAppender consolAppender = new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT);

		logger.setLevel(Config.getLogLevel());
		logger.setAdditivity(false); // appender 상속을 받지 않도록 한다.
		logger.addAppender(consolAppender);
		
		Logger loggerNetty = Logger.getLogger("io.netty"); // thread-safe

		loggerNetty.setLevel(Config.getLogLevel());
		loggerNetty.setAdditivity(false); // appender 상속을 받지 않도록 한다.
		loggerNetty.addAppender(consolAppender);

		try {
			DailyRollingFileAppender fileAppender = new DailyRollingFileAppender(); // 매일 새로운 로그파일로 교체하는 appender 이다.

			fileAppender.setName("FileAppender");
			fileAppender.setLayout(layout);
			fileAppender.setFile(Config.getLogFile()); // 파일 이름에 경로가 포함되면 생성자에 파일이름을 넘기면 오동작한다. 기본 생성자로 만들고 setFile()을 호출하는 방식으로 해야 한다.
			fileAppender.activateOptions(); // 파일 이름을 설정하면 해주어야 한다.
			fileAppender.setDatePattern("'.'yyyy-MM-dd");

			logger.addAppender(fileAppender);
			loggerNetty.addAppender(fileAppender);
		} catch (Exception e) {
			e.printStackTrace();

			// appender가 없으면 꺼버리자.
			if (!logger.getAllAppenders().hasMoreElements())
				logger.setLevel(Level.OFF);
		}
	}

	private static boolean setupServer() {
		try {
			tlasAddr = Config.getTlasAddr();
			authUrl = Config.getAuthUrl();
			mmmIp = Config.getMmmIp();
			mmmPort = Config.getMmmPort();
			return true;
		} catch (Exception e) {
			logger.error("An error occurred!", e);
			return false;
		}
	}
	
	private static void run() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				checkService();
			}
		};
		new Timer().scheduleAtFixedRate(task, 0, 30 * 60000); // 30분 마다 서비스 상태 확인
	}
	
	static final TrustManager tm = new X509TrustManager() {
		public void checkClientTrusted(X509Certificate[] chain, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};
	
	static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
	
	/**
	 * 서비스의 상태를 확인하는 메소드이다.
	 * 다른 서비스를 모니터링 할 경우, 각 서비스에 맞게 이 메소드만 구현하면 된다.
	 */
	private static void checkService() {
		SSLContext sslCtx = null;
		try {
			sslCtx = SSLContext.getInstance("TLS");
			sslCtx.init(null, new TrustManager[] { tm }, new java.security.SecureRandom()); // TrustManager를 사용하도록 초기화
			HttpsURLConnection.setDefaultSSLSocketFactory(sslCtx.getSocketFactory());

			URL url = new URL(tlasAddr + authUrl);
			HttpURLConnection conn;
			if (url.getProtocol().toLowerCase().equals("https")) {
				HttpsURLConnection https = (HttpsURLConnection)url.openConnection(); 
				https.setHostnameVerifier(DO_NOT_VERIFY); 
				conn = https; 
			}
			else {
				conn = (HttpURLConnection)url.openConnection();
			}

			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			String bodyStr = "{\"serialKey\": \"" + serialKey + "\", \"macAddress\": \"" + macAddress + "\"}";
			
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			writer.write(bodyStr);
			writer.flush();
			
			responseCode = conn.getResponseCode();
			logger.info("responseCode: " + responseCode);
			
			if (responseCode == 200) { // 정상
				sentMail = false; // 서비스가 정상일 경우 이후 다시 에러 발생 시 메일을 보내기 위해 false로 변경
				serviceError = false;
				return;
			}
			else { // 서비스 에러
				serviceError = true;
			}
			
			if (serviceError && !sentMail) { // 이미 메일을 보냈으면 보내지 않는다.
				// 메일 발송
				mail.clean(); // 이전 메일 내용 삭제
				mail.setResponseCode(Integer.toString(responseCode));
				requestMail();
			}
		} catch (Exception e) {
			logger.error("An error occurred!", e);
			
			serviceError = true;
			if (serviceError && !sentMail) { // 이미 메일을 보냈으면 보내지 않는다.
				// 메일 발송
				mail.clean(); // 이전 메일 내용 삭제
				mail.setContents(e.getMessage());
				requestMail();
			}
		}
	}
	
	private static void requestMail() {
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap()
				.group(workerGroup)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel sc) throws Exception {
						ChannelPipeline pipeline = sc.pipeline();
						pipeline.addLast(new StringDecoder(), new MMMClientHandler());
						pipeline.addLast(new StringEncoder(), new MMMClientHandler());
					}
				});
			Channel channel = bootstrap.connect(mmmIp, mmmPort).sync().channel();
			logger.info(mail.toJSONObject().toString());
			channel.write(mail.toJSONObject().toString());
			channel.flush();
		} catch (Exception e) {
			logger.error("An error occurred!", e);
		} finally {
			// 서버(MMM)로부터 결과를 받아야하기 때문에 여기서 닫으면 안된다.
//			workerGroup.shutdownGracefully();
		}
		sentMail = true;
	}

	public static void main(String[] args) {
		if (!Config.load())
			return;

		setupLogger();
	
		logger.info("Setup...");
		if (!setupServer())
			return;
		logger.info("Done Setup!");
		
		logger.info("Start TLSM...");
		run();
		
		// 별도로 프로세스를 종료할 인터페이스는 없다.
		// 종료하려면 명령창에서 kill 할 것...
	}
}
