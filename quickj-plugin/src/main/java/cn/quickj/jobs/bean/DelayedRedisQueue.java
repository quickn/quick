package cn.quickj.jobs.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import cn.quickj.cache.RedisCache;
import cn.quickj.jobs.JobsPlugin;
import cn.quickj.utils.QuickUtils;

/**
 * 延迟队列的功能
 * 
 * @author jekkro
 * 
 */
public class DelayedRedisQueue extends RedisQueue {

	private long delayTime;
	private Log log = LogFactory.getLog(DelayedRedisQueue.class);
	public DelayedRedisQueue(String queueName, long timeout, long delayTime) {
		super(queueName, (int) timeout);
		todo = "quickj:queue:" + queueName + ":delayedTodo";
		this.delayTime = delayTime;
	}

	public boolean addJob(String data, long delayTime) {
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			UniqueJob job = new UniqueJob();
			job.setData(data);
			job.setJobId(createJobId());
			job.setPriority(0);
			job.setTimestamp(System.currentTimeMillis() / 1000);
			Transaction t = jedis.multi();
			String key = job.getJobKey();
			t.hsetnx(this.todoData, key, job2Json(job));
			t.zcard(todo);
			t.zadd(todo, job.getTimestamp() + delayTime, key);
			t.exec();
			// set内容成功
			return true;

		} catch(Exception e){
			log.info(QuickUtils.StackTraceToString(e));
		}
		finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
		return false;
	}

	/**
	 * 增加一个Job，并且以key作为Job去重的关键字，如果系统已经存在该Job并且没有处理，则创建Job失败。
	 * 
	 * @param data
	 * @param key
	 * @return
	 */
	public boolean addJob(String data) {
		return addJob(data, this.delayTime);
	}

	/**
	 * 申索（获得）一个Job
	 * 
	 * @param block
	 * @return
	 */
	public Job claimJob(boolean block) {
		String key = null;
		int count = 0;
		do {
			Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
			try {

				Set<String> keys = jedis.zrangeByScore(todo, 0,
						System.currentTimeMillis() / 1000, 0, 1);
				if (keys != null && keys.size() == 1) {
					key = keys.iterator().next();
					// 移除操作为原子操作，只有成功移除的客户端才真正获得该Job，其他人需要重新获取。
					if (jedis.zrem(todo, key) == 1) {
						jedis.lpush(doing, key);
						UniqueJob job = json2Job(jedis.hget(todoData, key),
								UniqueJob.class);
						if (job == null) {
							// 异常数据的Job，直接删除掉。
							jedis.lrem(doing, -1, key);
							jedis.hdel(todoData, key);
						} else
							jedis.setex(lease + key, reservedTimeout, key);
						return job;
					}
				}
			}finally {
				RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
			}
			// 20秒钟后如果还取不到内容，则返回null；
			count++;
			if (count > 10)
				break;
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				break;
			}
		} while (block);
		return null;
	}

	/**
	 * 将Job返回到工作队列中。
	 * 
	 * @param jedis
	 * 
	 * @param job
	 */
	@Override
	protected void recallJob(String jobKey, Jedis jedis) {
		Transaction t = jedis.multi();
		t.lrem(doing, -1, jobKey);
		t.zadd(todo, System.currentTimeMillis() / 1000, jobKey);
		t.exec();
	}

	@Override
	public void finishJob(Job job) {
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			Transaction t = jedis.multi();
			t.lrem(doing, -1, job.getJobKey());
			t.del(lease + job.getJobKey());
			t.exec();
			// 如果在处理过程中加入了新的Job，并且Job Key和正在处理过程中的Job是一致的，则不删除数据。
			if (jedis.zrank(todo, job.getJobKey()) == null)
				t.hdel(todoData, job.getJobKey());
		} finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
	}

	/**
	 * 清理整个队列。
	 */
	public void cleanupQueue() {
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			jedis.del(todo, doing, todoData);
			Set<String> keys = jedis.keys(lease + "*");
			if (keys.size() > 0)
				jedis.del(keys.toArray(new String[0]));
		} finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
	}

	/**
	 * 根据Jobkey删除Job
	 * 
	 * @param jobKey
	 */
	public void deleteJob(String jobKey) {
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			Transaction t = jedis.multi();
			t.zrem(todo, jobKey);
			t.lrem(doing, -1, jobKey);
			t.hdel(todoData, jobKey);
			t.del(lease + jobKey);
			t.exec();
		} finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
	}

	/**
	 * 列出所有等待处理的Job，最多返回1000个。
	 * 
	 * @return
	 */
	public List<Job> getAllJobs() {
		List<Job> jobs = new ArrayList<Job>();
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			Set<String> jobKeys = jedis.zrange(todo, 0, 1000);
			for (String jobKey : jobKeys) {
				Job job = json2Job(jedis.hget(todoData, jobKey),
						UniqueJob.class);
				jobs.add(job);
			}
		} finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
		return jobs;
	}

	/**
	 * 列出所有处理中的Job。
	 * 
	 * @return
	 */
	public List<Job> getAllDoingJobs() {
		List<Job> jobs = new ArrayList<Job>();
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			List<String> jobKeys = jedis.lrange(doing, 0, -1);
			for (String jobKey : jobKeys) {
				Job job = json2Job(jedis.hget(todoData, jobKey),
						UniqueJob.class);
				jobs.add(job);
			}
		} finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
		return jobs;
	}

	@Override
	public long getNumInQueue() {
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			return jedis.zcard(todo);
		} finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
	}

	@Override
	public String getQueueType() {
		return "延时队列";
	}
}
