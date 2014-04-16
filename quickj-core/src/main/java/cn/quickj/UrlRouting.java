package cn.quickj;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.quickj.action.Action;
import cn.quickj.annotation.Filter;
import cn.quickj.plugin.Plugin;
import cn.quickj.utils.QuickUtils;

public class UrlRouting {
	private static Log log = LogFactory.getLog(UrlRouting.class);

	/**
	 * 根据URL来构造一个UrlMapping.
	 * 
	 * @param plugin
	 * 
	 * @param url
	 * @return
	 */
	public static UrlRouting createRoute(Plugin plugin, String url) {
		UrlRouting ur = null;
		try {
			ur = new UrlRouting(plugin, url);
			ur.parserFilter(plugin);
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.fillInStackTrace());
		}
		return ur;
	}

	private FastClass clazz;
	private ArrayList<Class<?>> filterClasses = new ArrayList<Class<?>>();
	private ArrayList<HashMap<String, String>> filterParams = new ArrayList<HashMap<String, String>>();
	private FastMethod method;
	private int methodParamCount;
	private ArrayList<Field> entities;
	private String name;

	/**
	 * 创建URLMapping，如果是 /action/view/参数 的模式，则className = action,methodName=view
	 * 如果是 /action/参数 的模式，则className=action,methodName=index(构造的时候为参数的值)。
	 * 取用的时候先用action来找UrlMapping，找到就用，找不到的话用前2个参数进行查找mapping。
	 * 
	 * @param plugin
	 * 
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("rawtypes")
	public UrlRouting(Plugin plugin, String url) throws Exception {
		log.debug("正在创建UrlRouting.....");
		String rootPackage = Setting.packageRoot;
		String pluginId = "default";
		if (plugin != null) {
			pluginId = plugin.getId();
			rootPackage = plugin.getRootPackage();
		}

		String[] s = url.split("/");
		String className = rootPackage + ".action."
				+ QuickUtils.capitalName(s[1]) + "Action";
		String oldMethodName = s[2];
		String methodName = s[2];
		clazz = FastClass.create(Class.forName(className));
		Class[] oldParameters;
		Class[] parameters = createParameter(s.length - 3);
		oldParameters = parameters;
		methodParamCount = s.length - 3;
		try {
			this.method = clazz.getMethod(methodName, parameters);
			name = pluginId + "__" + s[1] + "__" + s[2] + "__" + s.length;
		} catch (NoSuchMethodError e) {
			name = pluginId + "__" + s[1];
			methodName = "index";
			parameters = createParameter(s.length - 2);
			methodParamCount = s.length - 2;
			try {
				this.method = clazz.getMethod(methodName, parameters);
				name = name + "__" + s.length;
			} catch (NoSuchMethodError e2) {

			}
		}
		if (method == null) {
			throw new RuntimeException("URL:"
					+ url
					+ "存在问题，解析结果为：class："
					+ className
					+ "中没有"
					+ (oldParameters == null ? "无参数的" : oldParameters.length
							+ "个参数的") + oldMethodName + "方法，也没有"
					+ (parameters == null ? "无参数" : parameters.length + "个参数的")
					+ methodName + "的方法");
		}
		log.info("创建UrlRouting成功，name为:" + name);
	}

	@SuppressWarnings("unchecked")
	public Class<Action> getClazz() {
		return clazz.getJavaClass();
	}

	public ArrayList<Class<?>> getFilterClasses() {
		return filterClasses;
	}

	public ArrayList<HashMap<String, String>> getFilterParams() {
		return filterParams;
	}

	public FastMethod getMethod() {
		return method;
	}

	public int getMethodParamCount() {
		return methodParamCount;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("rawtypes")
	private Class[] createParameter(int i) {
		if (i <= 0)
			return null;
		Class[] parameters = new Class[i];
		for (int j = 0; j < parameters.length; j++) {
			parameters[j] = String.class;
		}
		return parameters;
	}

	/**
	 * 分析具体的Action Class和method的Filter标注。
	 * 
	 * @param plugin
	 * 
	 * @throws ClassNotFoundException
	 */
	private void parserFilter(Plugin plugin) {
		String rootPackage = Setting.packageRoot;
		if (plugin != null) {
			rootPackage = plugin.getRootPackage();
		}

		Class<?> actionClass = clazz.getJavaClass();
		Collection<String> filterNames = getFilterNames(actionClass);
		filterParams.addAll(DefaultApplication.filterParams);
		filterClasses.addAll(DefaultApplication.filterClasses);
		for (String filterName : filterNames) {
			String[] s = filterName.split(":");
			Class<?> c = null;
			try {
				c = Class.forName(rootPackage + ".filter." + s[0]);
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

		Filter filter = method.getJavaMethod().getAnnotation(Filter.class);
		if (filter != null && filter.name() != null) {
			String[] ff = filter.name().split(";");
			for (String filterName : ff) {
				String[] s = filterName.split(":");
				Class<?> c = null;
				try {
					c = Class.forName(rootPackage + ".filter." + s[0]);
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

	/**
	 * 取指定类的所有Filter，顺序是从本类到父类，依次装载。
	 * 
	 * @param actionClass
	 * @return
	 */
	private Collection<String> getFilterNames(Class<?> actionClass) {
		Collection<String> result = new ArrayList<String>();
		if (!actionClass.equals(Action.class)) {
			Filter filter = actionClass.getAnnotation(Filter.class);
			if (filter != null && filter.name() != null) {
				String[] filters = filter.name().split(";");
				for (String ff : filters) {
					result.add(ff);
				}
			}
			result.addAll(getFilterNames(actionClass.getSuperclass()));
		}
		return result;
	}

	public ArrayList<Field> getEntities() {
		return entities;
	}

}
