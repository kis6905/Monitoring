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
		logger.setAdditivity(false); // appender ����� ���� �ʵ��� �Ѵ�.
		logger.addAppender(consolAppender);
		
		Logger loggerNetty = Logger.getLogger("io.netty"); // thread-safe

		loggerNetty.setLevel(Config.getLogLevel());
		loggerNetty.setAdditivity(false); // appender ����� ���� �ʵ��� �Ѵ�.
		loggerNetty.addAppender(consolAppender);
		
		try {
			DailyRollingFileAppender fileAppender = new DailyRollingFileAppender(); // ���� ���ο� �α����Ϸ� ��ü�ϴ� appender �̴�.

			fileAppender.setName("FileAppender");
			fileAppender.setLayout(layout);
			fileAppender.setFile(Config.getLogFile()); // ���� �̸��� ��ΰ� ���ԵǸ� �����ڿ� �����̸��� �ѱ�� �������Ѵ�. �⺻ �����ڷ� ����� setFile()�� ȣ���ϴ� ������� �ؾ� �Ѵ�.
			fileAppender.activateOptions(); // ���� �̸��� �����ϸ� ���־�� �Ѵ�.
			fileAppender.setDatePattern("'.'yyyy-MM-dd");

			logger.addAppender(fileAppender);
			loggerNetty.addAppender(fileAppender);
		} catch (Exception e) {
			e.printStackTrace();

			// appender�� ������ ��������.
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
		
		// ���� ���������� ������ �����ϱ����� ������Ƽ ��ü ����.
		Properties props = new Properties();
		
		String title = mail.getTitle();
		String contents = mail.getContents();

		// smtp ȣ��Ʈ �ּ� ����
		props.put("mail.smtp.host", smtpHost);

		// ���ϼ��������� ������ ���� ������Ƽ ��ü������ ���� ��ü ����.
		Session session = Session.getDefaultInstance(props);
		// ���ǿ� ���� ������ ���� MimeMessage ��ü ����.
		MimeMessage ms = new MimeMessage(session);

		try {
			// �� ������ , ���� , ���� ������ �߰�
			ms.setSubject(title);
			ms.setText(contents);
			ms.setFrom(new InternetAddress(sender));
			ms.addRecipient(Message.RecipientType.TO, new InternetAddress(principal));
			if (deputy != null && !deputy.isEmpty())
				ms.addRecipient(Message.RecipientType.TO, new InternetAddress(deputy));

			// �߼� ó��
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
		
		// ������ ���μ����� ������ �������̽��� ����.
		// �����Ϸ��� ���â���� kill �� ��...
	}
}
