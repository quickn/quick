package cn.quickj;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

import cn.quickj.plugin.Plugin;

public class Setting {
	private static boolean initFinished = false;
	final public static int DEV_MODE = 0;
	final public static int PROD_MODE = 1;
	final public static int TEST_MODE = 2;
	public static String cacheClass;
	public static String cacheParam;
	public static String DEFAULT_CHARSET;
	public static Locale locale;
	public static String theme;
	public static int initActive;

	public static String jdbcDriver;

	public static String jdbcPassword;

	public static String jdbcUrl;

	public static String jdbcUser;

	public static int maxActive;

	public static int maxIdle;

	public static String packageRoot;

	public static int runMode = PROD_MODE;
	public static String license;

	public static String sessionClass;
	public static String sessionDomain;
	public static int sessionTimeOut;

	public static String tablePrefix = "";

	public static String uploadDir;
	public static int uploadMaxSize = 2048; // 上传文件大小，最大为2M
	public static String webRoot;
	public static String dialect;
	public static SessionFactory sessionFactory;
	public static boolean usedb;
	public static String longDateFormat;
	public static String shortDateFormat;
	public static String defaultUri;
	public static ArrayList<Plugin> plugins = new ArrayList<Plugin>();

	public static boolean fieldBySetter;

	private static Log log = LogFactory.getLog(Setting.class);
	public static ApplicationConfig appconfig;
	public static String queueParam;
	public static boolean queueEnabled;

	/**
	 * 从setting.xml中读取配置信息。
	 * 
	 * @param rootPath
	 * @throws Exception
	 * 
	 */
	public static void load(String rootPath) throws Exception {
		if (initFinished == true)
			return;
		XMLConfiguration config;
		String settingPath = "";
		if (rootPath == null) {
			// 获得WebApplication 的路径，并得到Setting.xml的目录。
			String classPath = Thread.currentThread().getContextClassLoader()
					.getResource("").getPath();
			Pattern p = Pattern.compile("([\\S]+/)WEB-INF[\\S]+");
			Matcher m = p.matcher(classPath);

			if (m.find())
				webRoot = m.group(1);
		} else
			webRoot = rootPath;
		try {
			webRoot = URLDecoder.decode(webRoot, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(webRoot.endsWith(".xml")){
			settingPath = webRoot;
			int lastPos = webRoot.lastIndexOf('\\');
			if(lastPos==-1)
				lastPos = webRoot.lastIndexOf('/');
			webRoot = webRoot.substring(0,lastPos);
		}else{
			if (!webRoot.endsWith("/") && !webRoot.endsWith("\\"))
				webRoot = webRoot + "/";
			settingPath = webRoot + "WEB-INF/setting.xml";
		}

		log.info("Loading setting file:" + settingPath);
		config = new XMLConfiguration();
		// 不分析把逗号变成数组的形式。
		config.setDelimiterParsingDisabled(true);
		config.load(settingPath);

		/*
		 * @Deprecated
		 * 因为运行模式不需要手工指定，当跑unit测试的时候，自动设定test运行环境，当从runserver的时候自动使用开发环境
		 * 当被当作war包调用的时候缺省就是生产环境，所以去掉这个XML配置。 String strRunMode =
		 * config.getString("runmode", "development"); if
		 * (strRunMode.equals("production")) runMode = PROD_MODE; else if
		 * (strRunMode.equals("test")) runMode = TEST_MODE; else runMode =
		 * DEV_MODE;
		 */
		String strRunMode = getRunMode();

		packageRoot = config.getString("package", packageRoot);
		fieldBySetter = config.getBoolean("web.fieldBySetter", false);
		DEFAULT_CHARSET = config.getString("web.charset", "utf-8");
		longDateFormat = config.getString("web.long-dateformat",
				"yyyy-MM-dd HH:mm:ss");
		license = config.getString("license");
		shortDateFormat = config
				.getString("web.short-dateformat", "yyyy-MM-dd");
		String strLocale = config.getString("web.locale", Locale.getDefault()
				.getDisplayName());
		theme = config.getString("web.theme");
		locale = new Locale(strLocale);
		sessionClass = config.getString("web.session.class",
				"cn.quickj.session.MemHttpSession");
		sessionDomain = config.getString("web.session.domain", null);
		sessionTimeOut = config.getInt("web.session.timeout", 30 * 60);
		defaultUri = config.getString("web.defaultUri");
		uploadDir = config.getString("web.upload.directory",
				System.getProperty("java.io.tmpdir"));
		uploadMaxSize = config.getInt("web.upload.max-size", 4096);
		jdbcDriver = config.getString("database." + strRunMode + ".driver", "");
		usedb = (jdbcDriver.length() != 0);
		jdbcUser = config.getString("database." + strRunMode + ".user", "root");
		jdbcUrl = config.getString("database." + strRunMode + ".url", "");
		jdbcPassword = config.getString("database." + strRunMode + ".password",
				"");
		maxActive = config.getInt("database." + strRunMode + ".pool.maxActive",
				10);
		initActive = config.getInt("database." + strRunMode
				+ ".pool.initActive", 2);
		maxIdle = config.getInt("database." + strRunMode + ".pool.maxIdle",
				1800);
		dialect = config.getString("database." + strRunMode + ".dialect", null);
		tablePrefix = config.getString("database.prefix", "");

		cacheClass = config.getString("cache.class",
				"cn.quickj.cache.SimpleCache");
		cacheParam = config.getString("cache.param", "capacity=50000");
		// 获取分布式队列的值。
		queueEnabled = "true".equalsIgnoreCase(config.getString("queue.enable",
				"false"));
		queueParam = config.getString("queue.param", "capacity=50000");
		loadPlugin(config);
		loadApplicationConfig();
		initFinished = true;
	}

	/**
	 * 将运行模式从数字转换为文字串。
	 * 
	 * @return
	 */
	public static String getRunMode() {
		switch (runMode) {
		case PROD_MODE:
			return "production";
		case TEST_MODE:
			return "test";
		default:
			return "development";
		}
	}

	private static void loadApplicationConfig() {
		XMLConfiguration config;
		try {
			config = new XMLConfiguration();
			// 不分析把逗号变成数组的形式。
			config.setDelimiterParsingDisabled(true);
			String appconfigPath = webRoot + "WEB-INF/appconfig.xml";
			config.load(appconfigPath);
		} catch (Exception e) {
			config = null;
		}
		String className = null;
		if (config != null)
			className = config.getString("class", null);
		if (className != null) {
			try {
				Class<?> clazz = Class.forName(className);
				appconfig = (ApplicationConfig) clazz.newInstance();
				appconfig.init(config);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("读取appconfig.xml的时候发生错误!" + e.getCause());
				appconfig = null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void loadPlugin(XMLConfiguration config)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		Object property = config.getProperty("plugins.plugin.class");
		if (property instanceof Collection) {
			Collection<String> pluginClazzs = (Collection<String>) property;
			int i = 0;
			for (String pluginClazz : pluginClazzs) {
				createPlugin(config, pluginClazz, i);
				i++;
			}
		} else {
			if (property != null)
				createPlugin(config, (String) property, 0);
		}
	}

	private static void createPlugin(Configuration config, String pluginClazz,
			int i) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		Configuration c = config.subset("plugins.plugin(" + i + ")");
		Class<?> clazz = Class.forName(pluginClazz);
		Plugin plugin = (Plugin) clazz.newInstance();
		plugin.init(c);
		log.info("正在加载插件：" + plugin.getName() + "   id:" + plugin.getId());
		plugins.add(plugin);
	}

}
