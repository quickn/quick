package cn.quickj.manager;

import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.KestrelCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.quickj.Setting;

/**
 * 使用twitter的 kestrel队列，用xmemcached的客户端进行分布式队列的存取。
 * 
 * @author lbj
 * 
 */
public class QueueManager {
	private final static QueueManager manager = new QueueManager();
	private boolean initialized;
	private static Log log = LogFactory.getLog(Setting.class);
	private MemcachedClient memcachedClient;

	public static QueueManager getQueueManager() {
		if (!manager.initialized) {
			manager.buildQueueManager();
		}
		return manager;
	}

	private synchronized void buildQueueManager() {
		try {
			if (!initialized) {
				if (Setting.queueEnabled) {
					MemcachedClientBuilder builder = new XMemcachedClientBuilder(
							AddrUtil.getAddresses(Setting.queueParam));
					builder.setCommandFactory(new KestrelCommandFactory());
					memcachedClient = builder.build();
					memcachedClient.setPrimitiveAsString(true);
					memcachedClient.setOpTimeout(5000);
					memcachedClient.setMergeFactor(50);
					memcachedClient.setOptimizeGet(false);
					initialized = true;
				} else {
					log.warn("配置文件禁止了分布式队列的使用，但是应用调用了getQueueManager方法");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String get(String queue) throws InterruptedException {
		return get(queue, String.class);
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T get(String queue, Class<T> clazz) throws InterruptedException {
		try {
			T r = (T) memcachedClient.get(queue);
			if(r==null)
				Thread.sleep(500);
			return r;
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void put(String queue, Object v) {
		try {
			memcachedClient.set(queue, 0, v);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
