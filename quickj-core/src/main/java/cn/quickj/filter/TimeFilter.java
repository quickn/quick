package cn.quickj.filter;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.quickj.action.Action;

/**
 * 用于计算action执行时间的Filter，该Filter体现了本架构中的Filter是 可以重入的，即每次执行建立了一个Filter实例。
 * 参数time可以定义超过多长的Action执行时间才会显示，默认是0，这样所有的action执行时间都会被打印出来。
 * 通过这个参数可以记录特别慢的一些Action操作，用于系统优化。
 * 
 * @author lbj
 * 
 */
public class TimeFilter implements ActionFilter {
	private static Log log = LogFactory.getLog(TimeFilter.class);
	long startTime;
	private long time;

	public int after(Action action) {
		long executeTime = System.currentTimeMillis() - startTime;
		if (executeTime > time)
			log.info(action.getRequest().getRequestURL().toString()+" execute action time:" + executeTime);
		return 0;
	}

	public int before(Action action) {
		startTime = System.currentTimeMillis();
		return 0;
	}

	public void init(HashMap<String, String> hashMap) {
		time = 0;
		if (hashMap != null) {
			String strTime = hashMap.get("time");
			if (strTime != null)
				time = Long.valueOf(strTime);
		}
	}
}
