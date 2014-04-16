package cn.quickj.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.quickj.utils.QuickUtils;

/**
 * 简单的Hashmap cache,在set的时候不会更新原先过期的定时时间。
 * 
 * @author Administrator
 * 
 */
public class SimpleCache implements ICache {
	private ConcurrentHashMap<String, Object> cache;
	private ConcurrentHashMap<String, Long> cacheTimeout;
	private TimeoutCleanThread cleanThread;

	private class TimeoutCleanThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					long currentTime = System.currentTimeMillis();
					for (String s : cacheTimeout.keySet()) {
						if (cacheTimeout.get(s) < currentTime) {
							cache.remove(s);
							cacheTimeout.remove(s);
						}
					}

					sleep(1000);
				} catch (InterruptedException e) {
					break;
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	};

	public void createCache(String param) {
		String[] result= null;
		if(param!=null)
			 result= QuickUtils.findAll(param, "([\\d]+)");
		int capacity = 5000;
		try {
			if (result !=null && result.length > 0)
				capacity = Integer.parseInt(result[0]);
			cache = new ConcurrentHashMap<String, Object>(capacity);
			cacheTimeout = new ConcurrentHashMap<String, Long>();
			cleanThread = new TimeoutCleanThread();
			cleanThread.setName("SimpleCache 缓存定时清理线程!");
			cleanThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete(String key) {
		cache.remove(key);
		cacheTimeout.remove(key);
	}

	public Object get(String key) {
		return cache.get(key);
	}

	public Map<String, Object> getBulk(ArrayList<String> keys) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		for (String key : keys) {
			Object value = cache.get(key);
			if (value != null)
				result.put(key, value);
		}
		return result;
	}

	public void replace(String key, int timeOut, Object value) {
		cache.replace(key, value);
	}

	public void set(String key, int timeOut, Object value) {
		cache.put(key, value);
		if(timeOut!=-1)
			cacheTimeout.put(key, System.currentTimeMillis()+timeOut*1000);
	}

	public void shutdown() {
		cache.clear();
	}

	public void flushAll() {
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> clazz) {
		return (T) get(key);
	}

	public String info() {
		return "count:" + cache.size();
	}
}
