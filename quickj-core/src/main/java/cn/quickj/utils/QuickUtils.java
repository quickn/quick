package cn.quickj.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.text.CharacterIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.quickj.Setting;

public class QuickUtils {
	private static Log log = LogFactory.getLog(QuickUtils.class);

	/**
	 * 将一个单词的首字母大写。
	 * 
	 * @param name
	 * @return
	 */
	public static String capitalName(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	/**
	 * 把形式为aaa_bbb_ccc的形式转化为AaaBbbCcc的形式。
	 * 
	 * @param fieldName
	 *            需要转换的字符串
	 * @return
	 */
	public static String getCapitalName(String fieldName) {
		String[] words = fieldName.split("_");
		String result = "";
		for (int i = 0; i < words.length; i++) {
			result += capitalName(words[i]);
		}
		return result;
	}

	/**
	 * 分析表格的格式。
	 * 
	 * @param tf
	 * @return
	 */
	public static Map<String, String> parserTableFormat(String tf) {
		Map<String, String> result = new HashMap<String, String>();
		CharacterIterator ci = new StringCharacterIterator(tf);
		char ch;
		boolean quotaStart = false;
		StringBuffer sb = new StringBuffer();
		String key = "";
		String value = "";
		ch = ci.first();
		while (ch != CharacterIterator.DONE) {
			if (quotaStart && ch != '"')
				sb.append(ch);
			else {
				switch (ch) {
				case '"':
					quotaStart = !quotaStart;
					break;
				case '=':
					key = sb.toString();
					sb = new StringBuffer();
					break;
				case ':':
					value = sb.toString();
					result.put(key.trim(), value.trim());
					sb = new StringBuffer();
					break;
				default:
					sb.append(ch);
				}
			}
			ch = ci.next();
		}
		value = sb.toString();
		result.put(key.trim(), value.trim());
		return result;
	}

	public static ArrayList<Map<String, String>> parserColumnFormat(String s) {

		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		CharacterIterator ci = new StringCharacterIterator(s);
		Map<String, String> v = new HashMap<String, String>();
		char ch;
		boolean quotaStart = false;
		StringBuffer sb = new StringBuffer();
		String key = "";
		String value = "";
		ch = ci.first();
		while (ch != CharacterIterator.DONE) {
			if (quotaStart && ch != '"')
				sb.append(ch);
			else {
				switch (ch) {
				case ',':
					value = sb.toString();
					v.put(key.trim(), value.trim());
					result.add(v);
					v = new HashMap<String, String>();
					key = value = "";
					sb = new StringBuffer();
					break;
				case '"':
					quotaStart = !quotaStart;
					break;
				case '=':
					key = sb.toString();
					sb = new StringBuffer();
					break;
				case ':':
					value = sb.toString();
					v.put(key.trim(), value.trim());
					sb = new StringBuffer();
					break;
				default:
					sb.append(ch);
				}
			}
			ch = ci.next();
		}
		value = sb.toString();
		v.put(key.trim(), value.trim());
		result.add(v);
		return result;
	}

	public static String getDisplayName(String fieldName) {
		String[] words = fieldName.split("_");
		String result = "";
		for (int i = 0; i < words.length; i++) {
			result += capitalName(words[i]) + " ";
		}
		return result.trim();
	}

	public static String getPropertyName(String fieldName) {
		fieldName = fieldName.toLowerCase();
		String[] words = fieldName.split("_");
		String result = words[0];
		for (int i = 1; i < words.length; i++) {
			result += capitalName(words[i]);
		}
		return result;
	}

	public static String getterMethodName(String property) {
		return "get" + capitalName(property);
	}

	public static String md5(String str) {
		return org.apache.commons.codec.digest.DigestUtils.md5Hex(str);
	}

	/**
	 * 打印Request对象的Cookie和header
	 * 
	 * @param request
	 */
	@SuppressWarnings("unchecked")
	public static void printRequest(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				System.out.println("name:" + cookie.getName() + "   value:" + cookie.getValue());
			}
		}
		System.out.println("headers:");

		Enumeration<String> s = request.getHeaderNames();
		if (s != null) {
			while (s.hasMoreElements()) {
				String name = (String) s.nextElement();
				System.out.println("name:" + name + "   value:" + request.getHeader(name));
			}
		}
	}

	/**
	 * 通过直接访问Field来设置值，而不是通过Setter方法，所以可以少写Setter方法，缺省使用此方法来设置值。
	 * 
	 * @param obj
	 *            需要设置的对象，一般为Action对象。
	 * @param fieldName
	 *            字段名称，可以有多个级联，比如aa__bb__cc__dd，setFieldValue2会自动找到最后的字段值。
	 * @param value
	 *            所需要设置的值，一般为String,也可以是File对象。
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings("unchecked")
	public static void setFieldValue2(Object obj, String fieldName, Object value) throws NoSuchMethodException {
		String[] fieldNames = fieldName.split("__|\\.");
		obj = parseField(obj, fieldNames, 0);
		fieldName = fieldNames[fieldNames.length - 1];
		if (obj == null) {
			System.out.println(fieldName + " is null");
			return;
		}
		// 如果是一个Map的话，则直接使用把放入到Map中。
		if (obj instanceof Map) {
			Map map = (Map) obj;
			map.put(fieldName, value);
		} else {
			Class<?> fc = Object.class;
			try {
				Field f = getField(obj.getClass(), fieldName);
				fc = f.getType();
				f.setAccessible(true);
				// 如果Field不是String类型，并且输入的值是String的话，则需要进行转换
				if (value instanceof String && !fc.equals(String.class)) {
					value = convertStringToObject(fc, value);
				}
				f.set(obj, value);
			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.debug("不能设置Field:" + fieldName + "的值,类型为:" + fc.getName() + "值为：" + value);
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Object getFieldValue(Object o, String fieldName) {
		if (o instanceof Map) {
			return ((Map) o).get(fieldName);
		} else {
			try {
				String[] fieldNames = fieldName.split("__|\\.");
				fieldName = fieldNames[fieldNames.length - 1];
				o = parseField(o, fieldNames, 0);
				Field f = getField(o.getClass(), fieldName);
				f.setAccessible(true);
				return f.get(o);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	public static String getFieldString(Object o, String fieldName) {
		Object ret = getFieldValue(o, fieldName);
		if (ret != null)
			return ret.toString();
		else
			return "";
	}

	private static Field getField(Class<? extends Object> c, String fieldName) throws NoSuchFieldException {
		if (c.equals(Object.class))
			throw new NoSuchFieldException(fieldName);
		try {
			return c.getDeclaredField(fieldName);
		} catch (Exception e) {
			return getField(c.getSuperclass(), fieldName);
		}
	}

	private static Double[] parserDoubleArray(String value) {
		String[] strs = parserStringArray(value);
		if (strs != null) {
			Double[] result = new Double[strs.length];
			for (int i = 0; i < result.length; i++) {
				try {
					result[i] = Double.parseDouble(strs[i]);
				} catch (Exception e) {
					log.debug(strs[i] + "不能解析为整数!");
					result[i] = 0D;
				}
			}
			return result;
		}
		return null;
	}

	private static double[] parserdoubleArray(String value) {
		String[] strs = parserStringArray(value);
		if (strs != null) {
			double[] result = new double[strs.length];
			for (int i = 0; i < result.length; i++) {
				try {
					result[i] = Double.parseDouble(strs[i]);
				} catch (Exception e) {
					log.debug(strs[i] + "不能解析为整数!");
					result[i] = 0D;
				}
			}
			return result;
		}
		return null;
	}

	private static float[] parserfloatArray(String value) {
		String[] strs = parserStringArray(value);
		if (strs != null) {
			float[] result = new float[strs.length];
			for (int i = 0; i < result.length; i++) {
				try {
					result[i] = Float.parseFloat(strs[i]);
				} catch (Exception e) {
					log.debug(strs[i] + "不能解析为整数!");
					result[i] = 0F;
				}
			}
			return result;
		}
		return null;
	}

	private static Float[] parserFloatArray(String value) {
		String[] strs = parserStringArray(value);
		if (strs != null) {
			Float[] result = new Float[strs.length];
			for (int i = 0; i < result.length; i++) {
				try {
					result[i] = Float.parseFloat(strs[i]);
				} catch (Exception e) {
					log.debug(strs[i] + "不能解析为整数!");
					result[i] = 0F;
				}
			}
			return result;
		}
		return null;
	}

	private static int[] parserIntArray(String value) {
		String[] strs = parserStringArray(value);
		if (strs != null) {
			int[] result = new int[strs.length];
			for (int i = 0; i < result.length; i++) {
				try {
					result[i] = Integer.parseInt(strs[i]);
				} catch (Exception e) {
					log.debug(strs[i] + "不能解析为整数!");
					result[i] = 0;
				}
			}
			return result;
		}
		return null;
	}

	private static Integer[] parserIntegerArray(String value) {
		String[] strs = parserStringArray(value);
		if (strs != null) {
			Integer[] result = new Integer[strs.length];
			for (int i = 0; i < result.length; i++) {
				try {
					result[i] = Integer.parseInt(strs[i]);
				} catch (Exception e) {
					log.debug(strs[i] + "不能解析为整数!");
					result[i] = 0;
				}
			}
			return result;
		}
		return null;
	}

	private static String[] parserStringArray(String value) {
		if (value != null)
			return value.split(",");
		return null;
	}

	private static Long[] parserLongArray(String value) {
		String[] strs = parserStringArray(value);
		if (strs != null) {
			Long[] result = new Long[strs.length];
			for (int i = 0; i < result.length; i++) {
				try {
					result[i] = Long.parseLong(strs[i]);
				} catch (Exception e) {
					log.debug(strs[i] + "不能解析为长整数!");
					result[i] = 0L;
				}
			}
			return result;
		}
		return null;
	}

	private static long[] parserlongArray(String value) {
		String[] strs = parserStringArray(value);
		if (strs != null) {
			long[] result = new long[strs.length];
			for (int i = 0; i < result.length; i++) {
				try {
					result[i] = Long.parseLong(strs[i]);
				} catch (Exception e) {
					log.debug(strs[i] + "不能解析为长整数!");
					result[i] = 0;
				}
			}
			return result;
		}
		return null;

	}

	/**
	 * 递归分析指定名称aa__bb__cc，并获取倒数第二个对象。
	 * 
	 * @param obj
	 * @param fieldNames
	 * @param level
	 * @return
	 * @throws NoSuchMethodException
	 */
	private static Object parseField(Object obj, String[] fieldNames, int level) throws NoSuchMethodException {
		if (fieldNames.length == (level + 1))
			return obj;
		else {
			obj = QuickUtils.getFieldValue(obj, fieldNames[level]);
			return parseField(obj, fieldNames, level + 1);
		}
	}

	/**
	 * 通过访问Setter方法来设置值，可以通过setting.xml文件来指定这种方法设置值。
	 * 
	 * @param obj
	 *            需要设置的对象，一般为Action对象。
	 * @param fieldName
	 *            字段名称，可以有多个级联，比如aa__bb__cc__dd，setFieldValue2会自动找到最后的字段值。
	 * @param value
	 *            所需要设置的值，一般为String,也可以是File对象。
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings("unchecked")
	public static void setFieldValue(Object obj, String fieldName, Object value) throws NoSuchMethodException {
		String[] fieldNames = fieldName.split("__|\\.");
		obj = parseField(obj, fieldNames, 0);
		fieldName = fieldNames[fieldNames.length - 1];
		if (obj == null) {
			System.out.println(fieldName + " is null");
			return;
		}
		// 如果是一个Map的话，则直接使用把放入到Map中。
		if (obj instanceof Map) {
			Map map = (Map) obj;
			map.put(fieldName, value);
		} else {
			Field f;
			try {
				f = getField(obj.getClass(), fieldName);
				Class<?> fc = f.getType();
				Method method = obj.getClass().getMethod(QuickUtils.setterMethodName(fieldName), new Class<?>[] { fc });
				// 如果Field不是String类型，并且输入的值是String的话，则需要进行转换
				if (value instanceof String && !fc.equals(String.class)) {
					value = convertStringToObject(fc, value);
				}
				method.invoke(obj, new Object[] { value });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static Object convertStringToObject(Class<?> fc, Object value) {
		try {
			if (fc.equals(Integer.class) || fc.equals(int.class)) {
				if (value == null || ((String) value).length() == 0)
					return null;
				value = Integer.valueOf((String) value);
			} else if (fc.equals(Boolean.class) || fc.equals(boolean.class)) {
				if ("1".equals(value)) {
					value = true;
				}
				if ("on".equals(value)) {
					value = true;
				} else
					value = Boolean.valueOf((String) value);
			} else if (fc.equals(Float.class) || fc.equals(float.class)) {
				value = Float.parseFloat((String) value);
			} else if (fc.equals(Double.class) || fc.equals(double.class)) {
				value = Double.parseDouble((String) value);
			} else if (fc.equals(Long.class) || fc.equals(long.class)) {
				value = Long.parseLong((String) value);
			} else if (fc.equals(Short.class) || fc.equals(short.class)) {
				value = Short.parseShort((String) value);
			} else if (fc.equals(char.class) || fc.equals(Character.class)) {
				value = ((String) value).charAt(0);
			} else if (fc.equals(Date.class)) {
				value = parserDateTime((String) value);
			} else if (fc.equals(int[].class)) {
				value = parserIntArray((String) value);
			} else if (fc.equals(String[].class)) {
				value = parserStringArray((String) value);
			} else if (fc.equals(Integer[].class)) {
				value = parserIntegerArray((String) value);
			} else if (fc.equals(long[].class)) {
				value = parserlongArray((String) value);
			} else if (fc.equals(Long[].class)) {
				value = parserLongArray((String) value);
			} else if (fc.equals(Float[].class)) {
				value = parserFloatArray((String) value);
			} else if (fc.equals(float[].class)) {
				value = parserfloatArray((String) value);
			} else if (fc.equals(Double[].class)) {
				value = parserDoubleArray((String) value);
			} else if (fc.equals(double[].class)) {
				value = parserdoubleArray((String) value);
			}
			return value;
		} catch (Exception e) {
			if (log.isDebugEnabled())
				e.printStackTrace();
			return null;
		}
	}

	private static Date parserDateTime(String value) {
		if (value == null || value.length() < 8)
			return null;
		try {
			value = value.trim();
			if (value.length() > 10)
				return (new SimpleDateFormat(Setting.longDateFormat)).parse(value);
			else
				return (new SimpleDateFormat(Setting.shortDateFormat)).parse(value);
		} catch (ParseException e) {
			log.info("输入的日期格式不被支持" + value);
		}
		return null;
	}

	public static String setterMethodName(String property) {
		return "set" + capitalName(property);
	}

	/**
	 * 字符串数字相减。
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static int strSub(String str1, String str2) {
		try {
			int i1 = Integer.parseInt(str1);
			int i2 = Integer.parseInt(str2);
			return i1 - i2;
		} catch (Exception e) {
			return 0;
		}
	}

	public static String[] findAll(String content, String regex) {
		Vector<String> result = new Vector<String>();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		while (m.find()) {
			result.add(content.substring(m.start(0), m.end(0)));
		}
		return result.toArray(new String[0]);
	}

	/**
	 * 获取指定package下的所有带指定annotation的class。
	 * 
	 * @param pckgname
	 * @param filterClass
	 * @param annotation
	 * @return
	 */
	public static ArrayList<Class<?>> getPackageClasses(String pckgname, Class<?> filterClass, Class<?> annotation) {
		ArrayList<Class<?>> results = new ArrayList<Class<?>>();
		String name = new String(pckgname);
		name = name.replace('.', '/');
		HashSet<String> classPathes = getAllPossibleClassPath();
		// Get a File object for the package
		String dirpath = "";
		for (String classpath : classPathes) {
			try {
				dirpath = URLDecoder.decode(classpath + name, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			File directory = new File(dirpath);
			if (directory.exists()) {
				getModelClass(results, directory, pckgname, filterClass, annotation);
			}
		}
		return results;
	}

	private static HashSet<String> getAllPossibleClassPath() {
		HashSet<String> results = new HashSet<String>();
		String dirpath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		// 处理windows目录下第一个字符是/的情况，比如/d:/abc/...变成d:/abc/...
		if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1 && dirpath.startsWith("/"))
			dirpath = dirpath.substring(1);
		results.add(dirpath);
		String split = (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) ? ";" : ":";
		// 取class path中的路径
		String[] classpathes = System.getProperty("java.class.path", "").split(split);
		for (String classpath : classpathes) {
			if (!classpath.endsWith(".jar")) {
				classpath = classpath.replace("\\", "/");
				if (!classpath.endsWith("/"))
					classpath += "/";
				results.add(classpath);
			}
		}
		return results;
	}

	/**
	 * 递归获取指定package下的所有带指定annotation的class。
	 * 
	 * @param results
	 * @param directory
	 * @param pckgname
	 * @param filterClass
	 * @param annotation
	 */
	@SuppressWarnings("unchecked")
	private static void getModelClass(ArrayList<Class<?>> results, File directory, String pckgname, Class<?> filterClass,
			Class<?> annotation) {
		// Get the list of the files contained in the package
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			// we are only interested in .class files
			String filename = files[i].getName();
			if (filename.endsWith(".class")) {
				// removes the .class extension
				String classname = filename.substring(0, filename.length() - 6);
				try {
					// Try to create an instance of the object
					Class clazz = Class.forName(pckgname + "." + classname);
					boolean isok = false;
					if (filterClass != null)
						isok = clazz.getSuperclass().equals(filterClass);
					if (annotation != null)
						isok = (clazz.getAnnotation(annotation) != null);

					if (isok)
						results.add(clazz);
				} catch (ClassNotFoundException cnfex) {
					System.err.println(cnfex);
				}
			} else if (files[i].isDirectory()) {
				getModelClass(results, files[i], pckgname, filterClass, annotation);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Class<?>> getPackageClassInJar(String jarName, String pckgname, Class<?> filterClass,
			Class<?> annotation) {
		ArrayList<Class<?>> results = new ArrayList<Class<?>>();
		pckgname = pckgname.replaceAll("\\.", "/");
		try {
			JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
			JarEntry jarEntry;

			while (true) {
				jarEntry = jarFile.getNextJarEntry();
				if (jarEntry == null) {
					break;
				}
				if ((jarEntry.getName().startsWith(pckgname)) && (jarEntry.getName().endsWith(".class"))) {
					String classname = jarEntry.getName().replaceAll("/", "\\.");
					classname = classname.substring(0, classname.length() - 6);
					// Try to create an instance of the object
					Class clazz = Class.forName(classname);
					boolean isok = false;
					if (filterClass != null)
						isok = clazz.getSuperclass().equals(filterClass);
					if (annotation != null)
						isok = (clazz.getAnnotation(annotation) != null);

					if (isok)
						results.add(clazz);
				}
			}
		} catch (FileNotFoundException e) {
		} catch (Exception cnfex) {
			cnfex.printStackTrace();
		}
		return results;
	}

	/**
	 * 通过JDBC驱动程序来智能获取需要的dialect。
	 * 
	 * @param jdbcDriver
	 * @return
	 */
	public static String getDialectByDriver(String jdbcDriver) {
		if (jdbcDriver.contains("mysql"))
			return "org.hibernate.dialect.MySQL5InnoDBDialect";
		else if (jdbcDriver.contains("sqlserver") || jdbcDriver.contains("jtds"))
			return "org.hibernate.dialect.SQLServerDialect";
		else if (jdbcDriver.contains("postgresql")) {
			return "org.hibernate.dialect.PostgreSQLDialect";
		} else if (jdbcDriver.contains("oracle")) {
			return "org.hibernate.dialect.Oracle10gDialect";
		} else if (jdbcDriver.contains("hsqldb")) {
			return "org.hibernate.dialect.HSQLDialect";
		} else if (jdbcDriver.contains("derby")) {
			return "org.hibernate.dialect.DerbyDialect";
		}
		log.info(jdbcDriver + "没有找到对应的Dialect,请在Setting.xml中手工指定");
		return "";
	}

	public static String getExtname(String filename) {
		if (filename != null) {
			int pos = filename.lastIndexOf(".");
			if (pos != -1) {
				return filename.substring(pos + 1);
			}
		}
		return "";
	}

	/**
	 * 创建文件路径所需的目录结构,和Java自带的mkdirs不同之处在于可以传入一个文件的绝对 路径进行目录创建。
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean mkdirs(String filename) {
		if (filename != null) {
			int pos = filename.lastIndexOf("/");
			if (pos == -1)
				pos = filename.lastIndexOf("\\");
			if (pos != -1) {
				return new File(filename.substring(0, pos)).mkdirs();
			}
		}
		return false;
	}

	public static String StackTraceToString(Exception e) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		PrintStream p = new PrintStream(b);
		e.printStackTrace(p);
		p.flush();
		return b.toString();
	}

	public static int[] splitIdsToIntArray(String ids) {
		if (ids != null) {
			String[] tmpIds = ids.split(",");
			int[] result = new int[tmpIds.length];
			int i = 0;
			for (String id : tmpIds) {
				try {
					result[i] = Integer.parseInt(id);
					i++;
				} catch (Exception e) {
				}
			}
			if (i == 0)
				return null;
			int[] result2 = new int[i];
			System.arraycopy(result, 0, result2, 0, i);
			return result2;
		}
		return null;
	}

	public static HashMap<String, String> parserFilterParams(String strParams) {
		HashMap<String, String> params = new HashMap<String, String>();
		if (strParams != null) {
			Pattern p = Pattern.compile("([A-z\\d_]+)[\\s]*=[\\s]*([A-z\\d_\\./|]+)[\\s]*");
			Matcher m = p.matcher(strParams);
			while (m.find()) {
				int count = m.groupCount();
				if (count == 2) {
					params.put(m.group(1), m.group(2));
				}
			}
		}
		return params;
	}
}
