package cn.quickj.jobs;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import redis.clients.jedis.JedisPool;
import cn.quickj.cache.ICache;
import cn.quickj.cache.RedisCache;
import cn.quickj.manager.CacheManager;
import cn.quickj.plugin.AbstractPlugin;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Job队列插件，提供Job的生命周期的管理功能，并由一个Action实现Job队列的状态统计工作。
 * redis连接配置可以从plugin中配置，如果plugin中没有配置，则会读取全局的CacheManager的JRedisPool
 * 
 * @author jekkro
 * 
 */

public class JobsPlugin extends AbstractPlugin {
	public static ObjectMapper jsonObjectMapper = new ObjectMapper();
	public static JedisPool jedisPool;

	public Map<String, Class<?>> depend() {
		return null;
	}

	public String getId() {
		return "jobs";
	}

	public ArrayList<Class<?>> getModels() {
		return null;
	}

	public String getName() {
		return "分布式队列插件";
	}

	public String getRootPackage() {
		return "cn.quickj.jobs";
	}

	public void init(Configuration c) {
		jsonObjectMapper.configure(
				JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		jsonObjectMapper
				.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

		ICache cache = CacheManager.getCacheManager().getCache();
		if (cache instanceof RedisCache) {
			jedisPool = ((RedisCache) cache).getJedisPool();
		} else {
			jedisPool = RedisCache.buildJedisConnectionPool(c
					.getString("param"));
		}
	}

}
