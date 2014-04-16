package cn.quickj.extui.action;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.TimeZone;

public class ViewAction extends ExtBaseAction {

	/**
	 * 查看系统信息
	 */
	public String index() {
		Properties temp = System.getProperties();
		HashMap<String, Properties> infos = new HashMap<String, Properties>();
		Properties sysproperties = new Properties();
		sysproperties.put("Date:", DateFormat.getDateInstance(DateFormat.LONG,
				getRequest().getLocale()).format(new Date()));
		sysproperties
				.put("Time:", DateFormat.getDateInstance(DateFormat.MEDIUM,
						getRequest().getLocale()).format(new Date()));
		sysproperties.put("Timezone:", TimeZone.getDefault().getDisplayName(
				getRequest().getLocale()));
		sysproperties.put("Java Version:", temp.getProperty("java.version"));
		sysproperties.put("JVM Version:", temp.getProperty("java.vm.version"));
		sysproperties.put("JVM Vendor:", temp.getProperty("java.vm.vendor"));
		sysproperties.put("JVM Runtime:", temp.getProperty("java.vm.name"));
		sysproperties.put("Username:", temp.getProperty("user.name"));
		sysproperties.put("Operating System:", temp.getProperty("os.name")
				+ " " + temp.getProperty("os.version"));
		sysproperties.put("Architecture:", temp.getProperty("os.arch"));
		infos.put("sysinfo", sysproperties);
		Properties memproperties = new Properties();
		memproperties.put("Total Memory:", String.valueOf(Runtime.getRuntime()
				.maxMemory()
				/ (1024 * 1024))
				+ "MB");
		memproperties.put("Used Memory:", String.valueOf((Runtime.getRuntime()
				.maxMemory() - Runtime.getRuntime().freeMemory())
				/ (1024 * 1024))
				+ "MB");
		memproperties.put("Free Memory:", String.valueOf(Runtime.getRuntime()
				.freeMemory()
				/ (1024 * 1024))
				+ "MB");
		infos.put("jvminfo", memproperties);
		return toJson(infos);
	}

}
