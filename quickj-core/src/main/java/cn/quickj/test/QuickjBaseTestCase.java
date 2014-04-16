package cn.quickj.test;

import java.io.File;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.quickj.AbstractApplication;
import cn.quickj.Application;
import cn.quickj.Setting;
import cn.quickj.utils.Dom4jUtils;

public class QuickjBaseTestCase extends TestCase {
	protected static Application app;

	public <T extends Object> T getInstance(Class<T> clazz) {
		return AbstractApplication.injector.getInstance(clazz);
	}

	public QuickjBaseTestCase() {
		if (app == null) {
			try {
				Setting.runMode = Setting.TEST_MODE;
				Setting.load("src/main/webapp/");
				SAXReader reader = new SAXReader(false);
				reader
						.setFeature(
								"http://apache.org/xml/features/nonvalidating/load-external-dtd",
								false);
				Document doc = reader.read(new File(
						"src/main/webapp/WEB-INF/web.xml"));
				Element e = Dom4jUtils
						.getElement(
								doc.getRootElement(),
								"/web-app/filter[filter-name='quickj']/init-param[param-name='application']/param-value");
				String clazz = "cn.quickj.WebApplication";
				if (e != null)
					clazz = e.getTextTrim();
				app = (Application) Class.forName(clazz).newInstance();
				app.init("src/main/webapp/");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
