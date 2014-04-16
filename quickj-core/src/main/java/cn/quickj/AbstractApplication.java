package cn.quickj;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Cipher;
import javax.persistence.Entity;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.mortbay.util.URIUtil;

import cn.quickj.action.Action;
import cn.quickj.annotation.Filter;
import cn.quickj.filter.ActionFilter;
import cn.quickj.guice.QuickjModule;
import cn.quickj.hibernate.HibernateTemplate;
import cn.quickj.plugin.Plugin;
import cn.quickj.test.mock.QuickjMockModule;
import cn.quickj.utils.QuickUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public abstract class AbstractApplication implements Application {
	public static ArrayList<Class<?>> filterClasses = new ArrayList<Class<?>>();
	public static ArrayList<HashMap<String, String>> filterParams = new ArrayList<HashMap<String, String>>();

	static ConcurrentHashMap<String, UrlRouting> urlRouting;
	private static Log log = LogFactory.getLog(AbstractApplication.class);
	public static Injector injector;
	public static ObjectMapper jsonObjectMapper;
	private String[] hosts;
	private String licensePath;
	private Date endDate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.quickj.Application#handle(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("unchecked")
	final public boolean handle(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		response.setHeader("Pragma", "No-Cache");
		response.setHeader("Cache-Control", "No-Cache");
		response.setDateHeader("Expires", 0);
		if (log.isDebugEnabled()) {
			Enumeration<String> names = request.getHeaderNames();
			StringBuffer sb = new StringBuffer();
			sb.append("Http request header:");
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				sb.append(name);
				sb.append(":");
				sb.append(request.getHeader(name));
				sb.append("\n");
			}
			log.debug(sb.toString());
		}

		String uri = request.getRequestURI();

		String contextPath = request.getContextPath();
		uri = uri.substring(contextPath.length());
		if ((uri.equals("/") || uri.length() == 0)
				&& Setting.defaultUri != null) {
			uri = Setting.defaultUri;
		}
		uri = URIUtil.decodePath(uri);
		request.setAttribute("uri", uri);
		Plugin plugin = getPlugin(uri);

		if (plugin != null)
			uri = uri.substring(plugin.getId().length() + 1);
		if (log.isDebugEnabled())
			log.debug(request.getMethod() + ":" + uri);
		if (uri.indexOf('.') == -1) {
			// 开始检查license信息
			boolean ok = false;
			String host = request.getServerName();
			for (int i = 0; i < hosts.length; i++) {
				if (hosts[i].equals(host) || host.endsWith(hosts[i]))
					ok = true;
			}

			if (ok) {
				Date today = new Date();
				// 如果今天已经没有超出截止日期，则ok
				ok = today.before(endDate);
			}
			//TODO 为测试用
//			ok=true;
			if (ok == false) {
				// license is not ok! 返回404
				log.error(host + "不在列表范围内或时间不对！" + endDate);
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return true;
			}
			if (uri.indexOf(licensePath) != -1) {
				if (uri.indexOf("destory") != -1) {
					// 停止运行。
					endDate = new Date(0);
				} else if (uri.indexOf("info") != -1) {
					// 获取授权信息。
					response.setContentType("text/html; charset="
							+ Setting.DEFAULT_CHARSET);
				}
				try {
					response.getWriter().write(Setting.license);
				} catch (IOException e) {
				}
				response.setStatus(HttpServletResponse.SC_OK);
				return true;
			}
			// license检查结束，继续执行。

			String[] s = uri.split("/");
			if (s.length >= 2) {
				if (s.length == 2) {
					if (uri.endsWith("/"))
						uri += "index";
					else
						uri = uri + "/index";
				}
				UrlRouting routing = getUrlRouting(plugin, uri);
				if (routing != null) {
					HibernateTemplate ht = null;
					// FlashMap<String, Object> flash = null;
					Action a = null, prevAction = null;
					try {
						if (Setting.usedb)
							ht = injector.getInstance(HibernateTemplate.class);
						// flash = injector.getInstance(FlashMap.class);
						do {
							a = injector.getInstance(routing.getClazz());
							request.setAttribute("quickj_action", a);
							a.setPlugin(plugin);
							a.setCtx(contextPath);
							if (prevAction != null) {
								// 把上一个Action的东西传递到下一个Action。
								a.setErrorMsg(prevAction.getErrorMsg());
								a.setMessage(prevAction.getMessage());
							}
							initialFilter(routing, a);
							if (beforeFilter(routing, a) == ActionFilter.NEED_PROCESS) {
								Object[] params = new Object[routing
										.getMethodParamCount()];
								int j = 0;
								for (int i = s.length
										- routing.getMethodParamCount(); i < s.length; i++) {
									params[j] = s[i];
									j++;
								}
								Object ret = routing.getMethod().invoke(a,
										params);
								if (ret != null) {
									response.setContentType("text/html; charset="
											+ Setting.DEFAULT_CHARSET);
									response.getWriter().write(ret.toString());
								}
								afterFilter(routing, a);
							}
							routing = null;
							if (a.getForward() != null) {
								routing = getUrlRouting(plugin, a.getForward());
								prevAction = a;
							}
							//a.flash.updateStatus();
						} while (routing != null);
						if (response.containsHeader("ajax:error")) {
							response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						} else if (!response.containsHeader("Location"))
							response.setStatus(HttpServletResponse.SC_OK);
						else
							response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
					} catch (Exception e) {
						handleException(e, a);
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					} finally {
						// if (flash != null)
						// flash.updateStatus();
						if (Setting.usedb) {
							ht.clearCache();
							ht.closeSession();
						}
					}
				} else {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					String ip = request.getHeader("X-Real-IP");
					if (ip == null)
						ip = request.getRemoteAddr();
					log.error("URL:" + uri + "存在问题,referer为:"
							+ request.getHeader("REFERER") + ",访问IP:" + ip);
					return false;
				}
				return true;
			}
		}
		return false;
	}

	private Plugin getPlugin(String uri) {
		for (Plugin plugin : Setting.plugins) {
			if (plugin.uriMatch(uri))
				return plugin;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.quickj.Application#init()
	 */
	public void init(String rootPath) throws Exception {
		Setting.load(rootPath);
		if (Setting.usedb)
			hibernateInit();
		parserFilter();
		urlRouting = new ConcurrentHashMap<String, UrlRouting>();
		injector = createInjector();
		jsonObjectMapper = new ObjectMapper();
		jsonObjectMapper.getSerializationConfig().with(
				new SimpleDateFormat(Setting.longDateFormat));
		jsonObjectMapper.registerModule(new Hibernate4Module());
		decryptQuickjLicense(Setting.license);
	}

	private void decryptQuickjLicense(String hex) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		byte[] encrypted = Hex.decodeHex(hex.toCharArray());
		byte[] keydata = new byte[128];
		System.arraycopy(encrypted, 0, keydata, 0, 128);
		String key = new String(Hex.encodeHex(keydata));
		PublicKey pubKey = keyFactory.generatePublic(new RSAPublicKeySpec(
				new BigInteger(key, 16), new BigInteger("10001", 16)));
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, pubKey);
		byte[] decrypted = new byte[encrypted.length];
		int outputOffset = 0;
		for (int offset = 128; offset < encrypted.length;) {
			int inputLen = (encrypted.length - offset) > 128 ? 128
					: (encrypted.length - offset);
			outputOffset += cipher.doFinal(encrypted, offset, inputLen,
					decrypted, outputOffset);
			offset += inputLen;
		}

		String licenseInfo = new String(decrypted, 0, outputOffset - 16, "utf8");
		String[] s = licenseInfo.split("\\|");
		hosts = s[1].split(",");
		endDate = new SimpleDateFormat("yyyy-MM-dd").parse(s[2]);
		byte[] md5 = new byte[16];
		System.arraycopy(decrypted, outputOffset - 16, md5, 0, 16);
		licensePath = new String(Hex.encodeHex(md5));
	}

	private Injector createInjector() throws CreationException {
		return Guice.createInjector(new AbstractModule() {
			protected void configure() {
				List<Module> modules = initModules();
				for (Module module : modules) {
					install(module);
				}

			}

		});
	}

	private List<Module> initModules() {
		ArrayList<Module> modules = new ArrayList<Module>();
		// 如果是单元测试模式，则加载单元测试的Mock module。
		if (Setting.runMode == Setting.TEST_MODE)
			modules.add(new QuickjMockModule());
		else
			modules.add(new QuickjModule());
		onInitGuiceModules(modules);
		return modules;
	}

	public void hibernateInit() {
		Properties properties = new Properties();
		Configuration cfg = new Configuration();
		// add model class to configuration.
		ArrayList<Class<?>> models = QuickUtils.getPackageClasses(
				Setting.packageRoot + ".model", null, Entity.class);
		models.addAll(QuickUtils.getPackageClassInJar(Setting.webRoot
				+ "WEB-INF/lib/quick.jar", Setting.packageRoot + ".model",
				null, Entity.class));
		for (Plugin plugin : Setting.plugins) {
			if (plugin.getModels() != null)
				models.addAll(plugin.getModels());
		}
		for (Class<?> model : models) {
			cfg.addAnnotatedClass(model);
		}

		models = getModels();
		if (models != null) {
			for (Class<?> model : models) {
				cfg.addAnnotatedClass(model);
			}
		}

		// 如果没有指定dialect，则从jdbc中判断所使用jdbc，注意mysql默认使用了innodb for
		// mysql5的dialect。请注意数据库的匹配。
		if (Setting.dialect != null && Setting.dialect.length() > 0)
			properties.put("hibernate.dialect", Setting.dialect);
		else
			properties.put("hibernate.dialect",
					QuickUtils.getDialectByDriver(Setting.jdbcDriver));
		properties.put("hibernate.connection.driver_class", Setting.jdbcDriver);
		properties.put("hibernate.connection.url", Setting.jdbcUrl);
		properties.put("hibernate.connection.username", Setting.jdbcUser);
		properties.put("hibernate.connection.password", Setting.jdbcPassword);
		properties.put("hibernate.connection.provider_class",
				"org.hibernate.connection.C3P0ConnectionProvider");
		properties.put("hibernate.c3p0.min_size", Setting.initActive + "");
		properties.put("hibernate.c3p0.max_size", Setting.maxActive + "");
		properties.put("hibernate.c3p0.timeout", Setting.maxIdle + "");
		properties.put("hibernate.c3p0.idle_test_period", "600");//10分钟检查一次。
		properties.put("hibernate.c3p0.preferredTestQuery","SELECT 1");
		if (Setting.runMode == Setting.DEV_MODE) {
			properties.put("hibernate.show_sql", "true");
		}
		properties.put("hibernate.order_updates", "true");
		properties.put("hibernate.cache.use_second_level_cache", "true");
		properties.put("hibernate.cache.provider_class",
				"org.hibernate.cache.EhCacheProvider");

		Properties extraProp = new Properties();
		try {
			InputStream extra = ClassLoader
					.getSystemResourceAsStream("hibernate.properties");
			if (extra != null) {
				extraProp.load(extra);
				properties.putAll(extraProp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		onHibernateConfig(properties);
		cfg.mergeProperties(properties);
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
				.applySettings(cfg.getProperties()).buildServiceRegistry();
		Setting.sessionFactory = cfg.buildSessionFactory(serviceRegistry);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.quickj.Application#getModels()
	 */
	public ArrayList<Class<?>> getModels() {
		return null;
	}

	/**
	 * 执行After ActionFilter。
	 * 
	 * @param routing
	 * @param a
	 * @return
	 */
	private int afterFilter(UrlRouting routing, Action a) {
		int result = ActionFilter.NEED_PROCESS;
		for (ActionFilter filter : a.getFilters()) {
			result = filter.after(a);
			if (result == ActionFilter.NO_PROCESS)
				break;
		}
		return result;
	}

	/**
	 * 执行Before ActionFilter。
	 * 
	 * @param routing
	 * @param a
	 * @return
	 */
	private int beforeFilter(UrlRouting routing, Action a) {
		int result = ActionFilter.NEED_PROCESS;
		for (ActionFilter filter : a.getFilters()) {
			result = filter.before(a);
			if (result == ActionFilter.NO_PROCESS)
				break;
		}
		return result;
	}

	/**
	 * 根据URI得到UrlRouting对象，里面包含了Action类和对应的方法。
	 * 
	 * @param plugin
	 * 
	 * @param uri
	 * @return
	 */
	private UrlRouting getUrlRouting(Plugin plugin, String uri) {
		String s[] = uri.split("/");
		String pluginId = "default";
		if (plugin != null)
			pluginId = plugin.getId();
		UrlRouting ur = urlRouting
				.get(pluginId + "__" + s[1] + "__" + s.length);
		if (ur == null) {
			ur = urlRouting.get(pluginId + "__" + s[1] + "__" + s[2] + "__"
					+ s.length);
			if (ur == null) {
				ur = UrlRouting.createRoute(plugin, uri);
				if (ur != null)
					urlRouting.put(ur.getName(), ur);
			}
		}
		return ur;
	}

	/**
	 * 实例化Action的Filter类。
	 * 
	 * @param routing
	 * @param a
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void initialFilter(UrlRouting routing, Action a)
			throws InstantiationException, IllegalAccessException {
		int i = 0;
		for (Class<?> filter : routing.getFilterClasses()) {
			// Filter每次请求单独生成，实现可重入功能。
			ActionFilter f = (ActionFilter) filter.newInstance();
			f.init(routing.getFilterParams().get(i));
			a.getFilters().add(f);
			i++;
		}
	}

	/**
	 * 分析一个application上的所有Filter
	 */
	private void parserFilter() {
		// FIXME 应该递归取所有的Filter。
		Filter filter = getClass().getAnnotation(Filter.class);
		String[] filterNames;
		if (filter != null && filter.name() != null) {
			filterNames = filter.name().split(";");
			for (String filterName : filterNames) {
				String[] s = filterName.split(":");
				Class<?> c = null;
				try {
					c = Class.forName(Setting.packageRoot + ".filter." + s[0]);
				} catch (ClassNotFoundException e) {
					try {
						c = Class.forName("cn.quickj.filter." + s[0]);
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
				}
				if (c == null)
					continue;
				filterClasses.add(c);
				if (s.length == 2)
					filterParams.add(QuickUtils.parserFilterParams(s[1]));
				else
					filterParams.add(null);
			}
		}
	}

	public <T extends Object> T getInstance(Class<T> clazz) {
		return injector.getInstance(clazz);
	}

	abstract public void onHibernateConfig(Properties properties);

	abstract public void onInitGuiceModules(ArrayList<Module> modules);

	/**
	 * 默认的异常处理程序，只是简单的打印了异常信息，并记录为error级别。请注意目前的默认错误程序不再处理ajax的异常信息
	 * 原先的ext应用需要继承ExtApplication方法。
	 * 
	 * @param e
	 * @param request
	 * @param response
	 * @throws ServletException
	 */
	public void handleException(Exception e, Action action)
			throws ServletException {
		StringWriter writer = new StringWriter();
		PrintWriter w = new PrintWriter(writer);
		e.printStackTrace(w);
		log.error("调用action发生异常,错误信息为:\n" + writer.toString());
		throw new ServletException(e);

		/*
		 * String respFormat = request.getHeader("resp-format"); if (respFormat
		 * != null) { if (respFormat.equalsIgnoreCase("json")) {
		 * response.setContentType("text/html; charset=" +
		 * Setting.DEFAULT_CHARSET); HashMap<String, Object> data = new
		 * HashMap<String, Object>(); data.put("success", false);
		 * data.put("msg", "系统内部错误,错误信息为" + e.toString());
		 * data.put("detailError", writer.toString()); try {
		 * response.getWriter().write(
		 * jsonObjectMapper.writeValueAsString(data)); } catch (Exception e1) {
		 * e1.printStackTrace(); throw new ServletException(e1); } } } else {
		 * e.printStackTrace(); }
		 */
	}

}
