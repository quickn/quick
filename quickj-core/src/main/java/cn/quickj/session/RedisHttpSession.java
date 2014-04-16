package cn.quickj.session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import cn.quickj.Setting;
import cn.quickj.cache.ICache;
import cn.quickj.cache.RedisCache;
import cn.quickj.manager.CacheManager;

/**
 * 使用Redis的Hash数据结构实现的分布式session。<br /> 
 * TODO 需要进一步改进，增加一个本地缓存的session，并以PubSub的方式订阅Session的变化通知
 * 。当一个节点的session变化后，其他节点快速得到通知，并完成本地缓存的更新。
 * 
 * @author lbj
 * 
 */
public class RedisHttpSession extends AbstractHttpSession {
	public JedisPool jedisPool;

	private static Log log = LogFactory.getLog(RedisHttpSession.class);

	public RedisHttpSession() {
		ICache cache = CacheManager.getCacheManager().getCache();
		if (cache instanceof RedisCache)
			jedisPool = ((RedisCache) cache).getJedisPool();
		else
			log.fatal("The system enabled the RedisHttpSession but doesn't enable the RedisCache!Please update the setting.xml file to enabled it first.");
	}

	/**
	 * 重载创建session的方法，首选从cache中恢复原先的sessionId的session，如果没有，则调用抽象类的create方法
	 */
	@Override
	public void create(String sessionId, HttpServletRequest request) {
		Jedis jedis = RedisCache.getJedisConnection(jedisPool);
		try {
			this.sessionId = sessionId;
			if (sessionId == null
					|| jedis.hexists(this.sessionId.getBytes(),
							"session_id".getBytes()) == false) {
				super.create(this.sessionId, request);
				jedis.hset(this.sessionId.getBytes(), "session_id".getBytes(),
						this.sessionId.getBytes());
			}
			jedis.expire(this.sessionId.getBytes(), Setting.sessionTimeOut);
		} finally {
			RedisCache.closeJedisConnection(jedisPool, jedis);
		}
	}

	public void destroy() {
		Jedis jedis = RedisCache.getJedisConnection(jedisPool);
		try {
			jedis.del(sessionId.getBytes());
		} finally {
			RedisCache.closeJedisConnection(jedisPool, jedis);
		}
	}

	/**
	 * 先从本地缓存中取数据，如果没有，则到远程memcache上取。
	 */
	public Object get(String key) {
		Jedis jedis = RedisCache.getJedisConnection(jedisPool);
		try {
			byte[] buffer = jedis.hget(sessionId.getBytes(), key.getBytes());
			jedis.expire(this.sessionId.getBytes(), Setting.sessionTimeOut);
			if (buffer != null) {
				ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
				ObjectInputStream ois = new ObjectInputStream(bis);
				return ois.readObject();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			RedisCache.closeJedisConnection(jedisPool, jedis);
		}
		return null;
	}

	public void set(String key, Object value) {
		if (value == null) {
			// 如果设置为空，则从cache中删除。
			remove(key);
			return;
		}
		Jedis jedis = RedisCache.getJedisConnection(jedisPool);
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(value);
			jedis.hset(sessionId.getBytes(), key.getBytes(), bos.toByteArray());
			jedis.expire(sessionId.getBytes(),  Setting.sessionTimeOut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			RedisCache.closeJedisConnection(jedisPool, jedis);
		}
	}

	public void update() {
		//TODO 进行session缓存的时候需要。
	}

	public void remove(String key) {
		Jedis jedis = RedisCache.getJedisConnection(jedisPool);
		try {
			jedis.hdel(sessionId.getBytes(), key.getBytes());
		} finally {
			RedisCache.closeJedisConnection(jedisPool, jedis);
		}
	}
}
