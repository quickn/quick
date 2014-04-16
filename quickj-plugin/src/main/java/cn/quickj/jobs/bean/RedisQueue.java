package cn.quickj.jobs.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import cn.quickj.cache.RedisCache;
import cn.quickj.jobs.JobsPlugin;
import cn.quickj.utils.QuickUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class RedisQueue {
	/**
	 * 存放待做队列（只存放JobKey）。
	 */
	protected String todo;
	/**
	 * 存放Job的数据。
	 */
	protected String todoData;
	/**
	 * 存放正在做的队列（只存jobKey）
	 */
	protected String doing;
	/**
	 * 存放claimJob时的一个超时计时器，默认为5分钟失效，由守护线程检查doing中的
	 * 每个JobKey是否存在在lease队列中，如果不存在，则recall这个Job
	 */
	protected String lease;
	/**
	 * Job处理的超时时间。
	 */
	protected int reservedTimeout;
	/**
	 * 队列名称。
	 */
	private String queueName;
	private static Log log = LogFactory.getLog(RedisQueue.class);
	/**
	 * 当队列为空的时候，如果一个线程已经在等待获取队列了，则其余的线程就在外部等待，不再调用redis命令。
	 * 这样在队列为空的时候可以减轻redis服务器的压力而造成大量的空转。
	 */
	protected static Semaphore queueLock;

	public RedisQueue(String queueName, int timeout) {
		this.queueName = queueName;
		todo = "quickj:queue:" + queueName + ":todo";
		todoData = "quickj:queue:" + queueName + ":todo_data";
		doing = "quickj:queue:" + queueName + ":doing";
		lease = "quickj:queue:" + queueName + ":doing_lease";
		this.reservedTimeout = timeout;
	}

	/**
	 * 产生该队列唯一的job id.
	 * 
	 * @return
	 */
	protected long createJobId() {
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			return jedis.incr(this.todo + "_jobid");
		}catch(Exception e){
			log.info(QuickUtils.StackTraceToString(e));
			//如果Job生成失败，则随机生成一个JobId。
			return Long.parseLong(RandomStringUtils.randomNumeric(8));
		}finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
	}

	/**
	 * 增加一个Job，并且以key作为Job去重的关键字，如果系统已经存在该Job并且没有处理，则创建Job失败。
	 * 
	 * @param data
	 * @param key
	 * @return
	 */
	public boolean addJob(String data) {
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			Job job = new Job();
			job.setData(data);
			job.setJobId(createJobId());
			job.setPriority(0);
			job.setTimestamp(System.currentTimeMillis() / 1000);
			Transaction t = jedis.multi();
			String key = job.getJobKey();
			t.hsetnx(this.todoData, key, job2Json(job));
			t.llen(todo);
			t.lpush(this.todo, key);// 如果要实现一个DelayedJob或者具有优先级的队列，则使用SortedSet即可。
			List<Object> result = t.exec();
			long result1 = (Long) result.get(0);
			long result2 = (Long) result.get(1);
			long result3 = (Long) result.get(2);
			// set内容成功，没有重复值，并且push队列成功。
			return (result1 == 1) && (result3 > result2);
		} catch(Exception e){
			log.info(QuickUtils.StackTraceToString(e));
		}
		finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
		return false;
	}

	/**
	 * 查询队列状态，以json格式返回。 {name:'队列名称',numInQueue:20,claimInQueue:1,failure:0}
	 * 
	 * @return
	 */
	public String info() {
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			Pipeline p = jedis.pipelined();
			Response<Long> numInQueue = p.llen(todo);
			Response<Long> claimInQueue = p.llen(doing);
			Response<Long> numInLease = p.llen(lease);
			p.sync();
			return "{name:\"" + queueName + "\",numInQueue:" + numInQueue.get()
					+ ",claimInQueue:" + claimInQueue.get() + ",failure:"
					+ (claimInQueue.get() - numInLease.get()) + "}";
		} finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
	}

	/**
	 * 以默认阻塞的方式申索（获得）一个Job
	 * 
	 * @return
	 */
	public Job claimJob() {
		return claimJob(true);
	}

	/**
	 * 申索（获得）一个Job
	 * 
	 * @param block
	 * @return
	 */
	public Job claimJob(boolean block) {
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			String key = null;
			if (block)
				key = jedis.brpoplpush(todo, doing, 10);
			else
				key = jedis.rpoplpush(todo, doing);
			if (key != null) {
				Job job = json2Job(jedis.hget(todoData, key), Job.class);
				if (job == null) {
					// 异常数据的Job，直接删除掉。
					jedis.lrem(doing, -1, key);
					jedis.hdel(todoData, key);
				} else
					jedis.setex(lease + key, reservedTimeout, key);
				return job;
			}
			return null;
		} finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
	}

	/**
	 * 当任务完成以后，调用这个方法，以删除doing中的Job。
	 * 
	 * @param job
	 */
	public void finishJob(Job job) {
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			Transaction t = jedis.multi();
			t.lrem(doing, -1, job.getJobKey());
			t.hdel(todoData, job.getJobKey());
			t.del(lease + job.getJobKey());
			t.exec();
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
	 * 检查doing队列，将已经超时的doing队列中的Job返还到todo的队列中。
	 */
	public void recallJobs() {
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			List<String> jobKeys = jedis.lrange(doing, 0, -1);
			for (String jobKey : jobKeys) {
				if (jedis.exists(lease + jobKey)) {
					recallJob(jobKey, jedis);
				}
			}
		} finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
	}

	/**
	 * 将Job返回到工作队列中。
	 * 
	 * @param jedis
	 * 
	 * @param job
	 */
	protected void recallJob(String jobKey, Jedis jedis) {
		Transaction t = jedis.multi();
		t.lrem(doing, -1, jobKey);
		t.lpush(todo, jobKey);
		t.exec();
	}

	/**
	 * 将Job转换成json语句。
	 * 
	 * @param job
	 * @return
	 */
	protected String job2Json(Job job) {
		if (job != null) {
			StringBuffer sb = new StringBuffer();
			sb.append("{id:");
			sb.append(job.getJobId());
			sb.append(",t:");
			sb.append(job.getTimestamp());
			sb.append(",p:");
			sb.append(job.getPriority());
			sb.append(",d:\"");
			sb.append(escape(job.getData()));
			sb.append("\"}");
			return sb.toString();
		}
		return null;
	}

	private String escape(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				// Reference: http://www.unicode.org/versions/Unicode5.1.0/
				if ((ch >= '\u0000' && ch <= '\u001F')
						|| (ch >= '\u007F' && ch <= '\u009F')
						|| (ch >= '\u2000' && ch <= '\u20FF')) {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
			}
		}// for
		return sb.toString();
	}

	/**
	 * 将json转化成Job TODO 优化这部分代码，将其直接用拼接的方式来解析。
	 * 
	 * @param json
	 * @return
	 */
	protected <T extends Object> T json2Job(String json, Class<T> clazz) {
		try {
			return JobsPlugin.jsonObjectMapper.readValue(json, clazz);
		} catch (JsonParseException e) {
			System.out.println(json);
			e.printStackTrace();
		} catch (JsonMappingException e) {
			System.out.println(json);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
			t.lrem(todo, -1, jobKey);
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
			List<String> jobKeys = jedis.lrange(todo, 0, 1000);
			for (String jobKey : jobKeys) {
				Job job = json2Job(jedis.hget(todoData, jobKey), Job.class);
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
				Job job = json2Job(jedis.hget(todoData, jobKey), Job.class);
				jobs.add(job);
			}
		} finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
		return jobs;
	}

	/**
	 * 队列中等待完成的数量
	 * 
	 * @return
	 */
	public long getNumInQueue() {
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			return jedis.llen(todo);
		} finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
	}

	/**
	 * 队列中被认领处理的数量
	 * 
	 * @return
	 */
	public long getClaimInQueue() {
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			return jedis.llen(doing);
		} finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
	}

	/**
	 * 队列中正在处理中已超时的（处理失败）数量
	 * 
	 * @return
	 */
	public long getFailCount() {
		long failCount=0;
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			List<String> jobKeys = jedis.lrange(doing, 0, -1);
			for (String jobKey : jobKeys) {
				if (!jedis.exists(lease + jobKey)) {
					failCount++;
				}
			}
		} finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
		return failCount;
	}

	/**
	 * 队列名称
	 * 
	 * @return
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * 队列类型
	 * 
	 * @return
	 */
	public String getQueueType() {
		return "常规队列";
	}
}
