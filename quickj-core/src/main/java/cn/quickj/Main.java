package cn.quickj;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.AbstractSessionManager;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;

public class Main {
	private static final String VERSION = "3.2 build 9362";

	public static void runserver(String context, String[] args)
			throws Exception {
		runserver(context, -1);
	}

	public static void runserver(String context, int port) throws Exception {
		// 如果命令行定义了当前运行模式为production模式，则设为production模式，否则从main里进入的均定义为调试模式。
		String runMode = System.getProperty("run.mode");
		if ("production".equalsIgnoreCase(runMode) == true)
			Setting.runMode = Setting.PROD_MODE;
		else
			Setting.runMode = Setting.DEV_MODE;

		String strGracefulTime = System.getProperty("graceful.time");
		int gracefulTime = 10000;
		if(strGracefulTime!=null)
			gracefulTime = Integer.parseInt(strGracefulTime);
		
		if (getPort() != null)
			port = getPort();
		long start = System.currentTimeMillis();
		Server server = new Server();
		if(Setting.runMode==Setting.PROD_MODE){
			QueuedThreadPool pool = new QueuedThreadPool(200);
			pool.setMinThreads(20);
			pool.setLowThreads(20);
			pool.setSpawnOrShrinkAt(2);
			server.setThreadPool(pool);
		}
		
		Connector connector = new SelectChannelConnector();
		connector.setPort(port);
		server.setConnectors(new Connector[] { connector });
		WebAppContext webapp = new WebAppContext(getWebRoot(), context);
		webapp.setDefaultsDescriptor("cn/quickj/webdefault.xml");
		//禁用jetty的session cookie。
		AbstractSessionManager sm = (AbstractSessionManager) webapp.getSessionHandler().getSessionManager();
		sm.setUsingCookies(false);
		server.addHandler(webapp);

		// 在生产环境下，允许所有的请求在完成后再退出。
		if (Setting.runMode == Setting.PROD_MODE) {
			server.setGracefulShutdown(gracefulTime);
			server.setStopAtShutdown(true);
		}
		server.start();
		System.out.println("Welcome use quickj framework, version:" + VERSION+". The system run in "+Setting.getRunMode()+" mode!");
		System.out.println("Server startup in "
				+ (System.currentTimeMillis() - start) + " ms");
		System.out.println("System ready.....!");
		server.join();
	}

	private static String getWebRoot() {
		// 获得WebApplication 的路径，并得到Setting.xml的目录。
		String classPath = Thread.currentThread().getContextClassLoader()
				.getResource("").getPath();
		Pattern p = Pattern.compile("([\\S]+/)WEB-INF[\\S]+");
		Matcher m = p.matcher(classPath);

		if (m.find())
			return m.group(1);
		return "./src/main/webapp";
	}

	private static Integer getPort() {
		String strPort = System.getProperty("jetty.port");
		try {
			return Integer.parseInt(strPort);
		} catch (Exception e) {
			return null;
		}
	}

}
