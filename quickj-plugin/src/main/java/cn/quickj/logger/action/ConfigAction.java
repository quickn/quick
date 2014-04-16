package cn.quickj.logger.action;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import cn.quickj.action.Action;

public class ConfigAction extends Action {
	private Map<String, String> loggers = new HashMap<String, String>();
	private boolean save;

	@SuppressWarnings("rawtypes")
	public void index() {
		if (save)
			save();
		Enumeration e = LogManager.getCurrentLoggers();
		while (e.hasMoreElements()) {
			Logger t1Logger = (Logger) e.nextElement();
			String loggerName = t1Logger.getName();
			if (t1Logger.getLevel() != null)
				loggers.put(loggerName, t1Logger.getLevel().toString());
		}
		render("logger.html");
	}

	private void save() {
		Iterator<String> iter = loggers.keySet().iterator();
		while (iter.hasNext()) {
			String loggerName = iter.next();
			System.out.println(Level.toLevel(loggers.get(loggerName)));
			LogManager.getLogger(loggerName).setLevel(
					Level.toLevel(loggers.get(loggerName)));
		}
	}

	public Map<String, String> getLoggers() {
		return loggers;
	}
}
