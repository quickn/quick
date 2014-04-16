package cn.quickj.manager;

import cn.quickj.Setting;
import cn.quickj.cache.ICache;

public class CacheManager {
	private final static CacheManager manager = new CacheManager();
	private ICache cache;
	private boolean initialized;

	public ICache getCache() {
		return cache;
	}
	public static CacheManager getCacheManager() {
		if (!manager.initialized) {
			manager.buildCacheManager();
		}
		return manager;
	}

	private synchronized void buildCacheManager() {
		try {
			if (!initialized) {
				cache = (ICache) Class.forName(Setting.cacheClass)
						.newInstance();
				cache.createCache(Setting.cacheParam);
				initialized = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete(String key) {
		cache.delete(key);
	}

	public void flushAll() {
		cache.flushAll();
	}

	public Object get(String key) {
		return cache.get(key);
	}

	public void set(String key, int timeOut, Object value) {

		cache.set(key, timeOut, value);
	}

	public void shutdownManager() {
		cache.shutdown();
	}
}
