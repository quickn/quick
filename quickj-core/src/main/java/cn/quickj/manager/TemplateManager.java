package cn.quickj.manager;

import java.io.File;
import java.io.IOException;

import cn.quickj.Setting;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;

public class TemplateManager {
	static TemplateManager engine;

	public static TemplateManager getEngine() {
		if (engine == null) {
			engine = new TemplateManager();
			engine.createEngine();
		}
		return engine;
	}

	public Configuration cfg = new Configuration();

	private void createEngine() {
		try {
			if(Setting.webRoot.endsWith("/")||Setting.webRoot.endsWith("\\"))
				cfg.setDirectoryForTemplateLoading(new File(Setting.webRoot+"templates"));
			else
				cfg.setDirectoryForTemplateLoading(new File(Setting.webRoot+"/templates"));
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			cfg.setOutputEncoding(Setting.DEFAULT_CHARSET);
			//TODO 如何做国际化。
			cfg.setDefaultEncoding(Setting.DEFAULT_CHARSET);
			cfg.setNumberFormat("#");
			cfg.setCacheStorage(new freemarker.cache.MruCacheStorage(20, 250));
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
