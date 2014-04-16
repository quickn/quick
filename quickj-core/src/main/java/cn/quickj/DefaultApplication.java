package cn.quickj;

import java.util.ArrayList;
import java.util.Properties;

import com.google.inject.Module;


/**
 * 全局的Filter安排在这里。如果用户需要扩展Application，则建议继承
 * {@link AbstractApplication}
 * 
 * @author lbj
 * @see {@link AbstractApplication}
 */
@cn.quickj.annotation.Filter(name = "TimeFilter:time=100;StaticParamFilter")
public class DefaultApplication extends AbstractApplication {

	@Override
	public void onHibernateConfig(Properties properties) {
	}

	@Override
	public void onInitGuiceModules(ArrayList<Module> modules) {
	}

	public void handleException(Exception e) {
		
	}
}
