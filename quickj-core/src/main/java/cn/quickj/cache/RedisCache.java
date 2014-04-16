package cn.quickj.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import cn.quickj.utils.StringUtil;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RedisCache implements ICache {
	private JedisPool jedisPool;
	private static Log log = LogFactory.getLog(RedisCache.class);

	/**
	 * 分析配置格式，获得JedisPoolConfig对象。<br />
	 * min:最小空闲的对象数，以保证服务可用。<br />
	 * max:最多的对象数。<br />
	 * maxWait: 获取资源的最大等待时间 timeout: 读写时间，缺省为5000毫秒。
	 * 
	 * TODO 增加多个Redis的冗余处理程序。
	 * 
	 * @param param
	 *            参数为json格式
	 *            {min:5,max:100,idle:10000,maxWait,host:"192.168.0.247"
	 *            ,port:6379,master:true}
	 * @return
	 */
	public static JedisPool buildJedisConnectionPool(String param) {
		try {
			JedisPoolConfig poolConfig = new JedisPoolConfig();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,
					true);
			mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			@SuppressWarnings("rawtypes")
			HashMap params = mapper.readValue(param, HashMap.class);
			int min = 5, max = 20, maxWait = 50000, port = 6379, timeout = 10000;
			String host = "127.0.0.1";
			if (params.get("min") != null)
				min = (Integer) params.get("min");
			if (params.get("max") != null)
				max = (Integer) params.get("max");
			if (params.get("maxWait") != null)
				maxWait = (Integer) params.get("maxWait");
			if (params.get("host") != null)
				host = (String) params.get("host");
			if (params.get("port") != null)
				port = (Integer) params.get("port");
			if (params.get("timeout") != null) {
				timeout = (Integer) params.get("timeout");
			}
			// 不管怎么样至少有min个连接资源是空闲的
			poolConfig.setMinIdle(min);
			// 设置最多允许的空闲资源
			poolConfig.setMaxIdle(min);
			// 只有在连接检测的时候进行连接是否正常的检测，其他情况一律不检测，以提高性能。
			poolConfig.setTestWhileIdle(false);
			poolConfig.setTestOnBorrow(true);
			poolConfig.setTestOnReturn(false);
			// 当资源耗尽的时候我们返回fail
			poolConfig.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
			// 每分钟检测一次连接是否空闲，如果空闲，则要根据min和max之间的值释放相关链接。
			poolConfig.setTimeBetweenEvictionRunsMillis(60000);
			// 最多的链接数，如果链接数用完，则后面的需要等待。
			poolConfig.setMaxActive(max);
			// 获取连接资源的最大等待时间。
			poolConfig.setMaxWait(maxWait);
			// 数据读写超时时间
			return new JedisPool(poolConfig, host, port, timeout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void createCache(String param) {
		jedisPool = RedisCache.buildJedisConnectionPool(param);
	}

	public static Jedis getJedisConnection(JedisPool jedisPool) {
		while (true) {
			try {
				Jedis jedis = jedisPool.getResource();
				if (jedis != null)
					return jedis;
			} catch (JedisConnectionException e) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
			if (log.isDebugEnabled()) {
				StackTraceElement[] elements = Thread.currentThread()
						.getStackTrace();
				for (int i = 0; i < elements.length; i++) {
					System.out.println(elements[i]);
				}
			}
			log.error("jedis 连接资源获取失败，请检查网络情况或者增大配置的连接池数量!2秒钟后将尝试获取链接");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public static void closeJedisConnection(JedisPool jedisPool, Jedis jedis) {
		if (jedis != null)
			jedisPool.returnResource(jedis);
	}

	public void delete(String key) {
		Jedis jedis = RedisCache.getJedisConnection(jedisPool);
		try {
			jedis.del(key.getBytes());
		} catch (Exception e) {
			log.error(StringUtil.exceptionMsg(e));
		} finally {
			RedisCache.closeJedisConnection(jedisPool, jedis);
		}
	}

	public Object get(String key) {
		return get(key, Object.class);
	}

	public void set(String key, int timeOut, Object value) {
		Jedis jedis = RedisCache.getJedisConnection(jedisPool);
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(value);
			jedis.set(key.getBytes(), bos.toByteArray());
			if (timeOut > 0)
				jedis.expire(key.getBytes(), timeOut);
		} catch (Exception e) {
			log.error(StringUtil.exceptionMsg(e));
		} finally {
			RedisCache.closeJedisConnection(jedisPool, jedis);
		}
	}

	public void flushAll() {
		Jedis jedis = RedisCache.getJedisConnection(jedisPool);
		try {
			jedis.flushAll();
		} catch (Exception e) {
			log.error(StringUtil.exceptionMsg(e));
		} finally {
			closeJedisConnection(jedisPool, jedis);
		}
	}

	public void shutdown() {
		// 危险操作不实现。
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> clazz) {
		T obj = null;
		Jedis jedis = RedisCache.getJedisConnection(jedisPool);
		try {
			byte[] buffer = jedis.get(key.getBytes());
			if (buffer != null) {
				ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
				ObjectInputStream ois = new ObjectInputStream(bis);
				obj = (T) ois.readObject();
			}
		} catch (Exception e) {
			log.error(StringUtil.exceptionMsg(e));
		} finally {
			RedisCache.closeJedisConnection(jedisPool, jedis);
		}
		return obj;
	}

	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public String info() {
		Jedis jedis = RedisCache.getJedisConnection(jedisPool);
		try {
			return jedis.info();
		} finally {
			RedisCache.closeJedisConnection(jedisPool, jedis);
		}
	}
}
