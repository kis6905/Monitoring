package com.btb.mmm;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.btb.mmm.netty.MMMServerHandler;
import com.btb.mmm.vo.Mail;

/**
 * @author iskwon
 */
public class MonitoringMailManager {

	// Global static members.
	public static final Logger logger = Logger.getLogger("MonitoringMailManager"); // thread-safe
	
	private static int port = 8000; // default
	private static String sender;
	private static String password;
	private static String smtpHost;
	private static String principal;
	private static String deputy;
	
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
			port = Config.getPort();
			
			sender = Config.getSender();
			password = Config.getPassword();
			smtpHost = Config.getSmtpHost();
			principal = Config.getPrincipal();
			deputy = Config.getDeputy();
			
			return true;
		} catch (Exception e) {
			logger.error("An error occurred!", e);
			return false;
		}
	}
	
	private static void run() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup(5);
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap()
				.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast("decoder", new StringDecoder());
						ch.pipeline().addLast("encoder", new StringEncoder());
						ch.pipeline().addLast("handler", new MMMServerHandler());
					}
				})
				.childOption(ChannelOption.TCP_NODELAY, true);
			
			bootstrap.bind(port).sync().channel().closeFuture().sync();
		} catch (Exception e) {
		} finally {
//			bossGroup.shutdownGracefully();
//			workerGroup.shutdownGracefully();
		}
	}
	
	public static boolean sendMail(Mail mail) {
		boolean result = false;
		
		// 메일 서버에대한 정보를 저장하기위한 프로퍼티 객체 생성.
		Properties props = new Properties();
		
		String title = mail.getTitle();
		String contents = mail.getContents();

		// smtp 호스트 주소 설정
		props.put("mail.smtp.host", smtpHost);

		// 메일서버에대한 정보를 담은 프로퍼티 객체에대한 세션 객체 생성.
		Session session = Session.getDefaultInstance(props);
		// 세션에 대한 정보를 담은 MimeMessage 객체 생성.
		MimeMessage ms = new MimeMessage(session);

		try {
			// 송 수신자 , 제목 , 내용 정보를 추가
			ms.setSubject(title);
			ms.setText(contents);
			ms.setFrom(new InternetAddress(sender));
			ms.addRecipient(Message.RecipientType.TO, new InternetAddress(principal));
			if (deputy != null && !deputy.isEmpty())
				ms.addRecipient(Message.RecipientType.TO, new InternetAddress(deputy));

			// 발송 처리
			Transport transport = session.getTransport("smtps");
			transport.connect(smtpHost, sender, password);
			transport.sendMessage(ms, ms.getAllRecipients());
			transport.close();
			
			result = true;
		} catch (Exception e) {
			logger.error("An mail error occurred!", e);
			result = false;
		}
		return result;
	}
	
	public static void main(String[] args) {
		
		if (!Config.load())
			return;

		setupLogger();
	
		logger.info("Setup...");
		if (!setupServer())
			return;
		logger.info("Done Setup!");
		
		logger.info("Start MMM...");
		run();
		
		// 별도로 프로세스를 종료할 인터페이스는 없다.
		// 종료하려면 명령창에서 kill 할 것...
	}
}
