package cn.quickj.cache;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 基于Memcache的缓存实现机制。已经被废除建议使用<code>RedisCache</code>
 * @author jekkro
 *
 */
@Deprecated
public class MemCache implements ICache {
	private MemcachedClient memcachedClient;
	private static Log log = LogFactory.getLog(MemCache.class);

	public void createCache(String param) {
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(
				AddrUtil.getAddresses(param));

		try {
			builder.setConnectionPoolSize(5);
			memcachedClient = builder.build();
			memcachedClient.setConnectTimeout(5000L);
			memcachedClient.setOpTimeout(5000L);
		} catch (IOException e) {
			StringWriter writer = new StringWriter();
			PrintWriter w = new PrintWriter(writer);
			e.printStackTrace(w);
			log.error(writer.toString());
		}
	}

	public void delete(String key) {
		try {
			memcachedClient.deleteWithNoReply(key);
		} catch (Exception e) {
			StringWriter writer = new StringWriter();
			PrintWriter w = new PrintWriter(writer);
			e.printStackTrace(w);
			log.error(writer.toString());
		}
	}

	public Object get(String key) {
		Object obj = null;
		try {
			obj = memcachedClient.get(key, 3000);
		} catch (Exception e) {
			StringWriter writer = new StringWriter();
			PrintWriter w = new PrintWriter(writer);
			e.printStackTrace(w);
			log.error(writer.toString());
			obj = null;
		}
		return obj;
	}


	public void set(String key, int timeOut, Object value) {
		try {
			memcachedClient.set(key, timeOut, value, 10000);
		} catch (Exception e) {
			StringWriter writer = new StringWriter();
			PrintWriter w = new PrintWriter(writer);
			e.printStackTrace(w);
			log.error(writer.toString());
		}
	}

	public void flushAll() {
		try {
			memcachedClient.flushAll();
		} catch (Exception e) {
			StringWriter writer = new StringWriter();
			PrintWriter w = new PrintWriter(writer);
			e.printStackTrace(w);
			log.error(writer.toString());
		}
	}

	public void shutdown() {
		try {
			memcachedClient.shutdown();
		} catch (IOException e) {
			StringWriter writer = new StringWriter();
			PrintWriter w = new PrintWriter(writer);
			e.printStackTrace(w);
			log.error(writer.toString());
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> clazz) {
		return (T) get(key);
	}
	
	public String info(){
		//TODO 完成memcache的status命令
		//memcachedClient.stats(arg0)
		return "";
	}

}
