package cn.quickj.manager;

import junit.framework.TestCase;
import redis.clients.jedis.Jedis;

public class RedisTest extends TestCase {
	public static void main(String[] args) {
		Jedis jedis = new Jedis("192.168.0.247");
		jedis.set("foo", "bar");
		String value = jedis.get("foo");
		System.out.println(value);
		jedis.set("foo", "bar2");
		System.out.println(jedis.get("foo"));
	}
	public void testRedis(){
		
	}
}
