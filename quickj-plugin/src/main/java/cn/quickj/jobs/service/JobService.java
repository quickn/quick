package cn.quickj.jobs.service;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;

import cn.quickj.cache.RedisCache;
import cn.quickj.jobs.JobsPlugin;
import cn.quickj.jobs.bean.DelayedRedisQueue;
import cn.quickj.jobs.bean.Job;
import cn.quickj.jobs.bean.PriorityQueue;
import cn.quickj.jobs.bean.RedisQueue;

import com.google.inject.Singleton;

/**
 * 实现分布式Job管理，包括队列的创建，addJob，召回Job等等。
 * 
 * @author jekkro
 * 
 *         使用方法
 */
@Singleton
public class JobService {
	public final static int NORMAL_QUEUE = 1;
	public final static int DELAY_QUEUE = 2;
	public final static int PRIORITY_QUEUE = 3;
	ConcurrentHashMap<String, RedisQueue> queues = new ConcurrentHashMap<String, RedisQueue>();
	private static Log log = LogFactory.getLog(JobService.class);

	/**
	 * 查询所有Redis中的队列。
	 * 
	 * @return
	 */
	public Collection<RedisQueue> getAllQueues() {
		Jedis jedis = RedisCache.getJedisConnection(JobsPlugin.jedisPool);
		try {
			Set<String> keys = jedis.keys("quickj:queue:*odo_jobid");
			for (String key : keys) {
				String queueName = key.replace("quickj:queue:", "")
						.replace(":todo_jobid", "")
						.replace(":delayedTodo_jobid", "")
						.replace(":priorityTodo_jobid", "");
				if (queues.get(queueName) == null) {
					if (key.indexOf("delayedTodo") != -1) {
						connectToQueue(queueName, JobService.DELAY_QUEUE);
					} else if (key.indexOf("priorityTodo") != -1) {
						connectToQueue(queueName, JobService.PRIORITY_QUEUE);
					} else {
						connectToQueue(queueName, JobService.NORMAL_QUEUE);
					}
				}
			}
			return queues.values();
		} finally {
			RedisCache.closeJedisConnection(JobsPlugin.jedisPool, jedis);
		}
	}

	/**
	 * 增加一个Job到队列中。
	 * 
	 * @param queueName
	 *            队列名称
	 * @param data
	 */
	public void addJob(String queueName, String data) {
		RedisQueue queue = queues.get(queueName);
		if (queue == null)
			queue = connectToQueue(queueName, NORMAL_QUEUE);
		queue.addJob(data);
	}

	/**
	 * 增加一个延时的Job，并以data去重，延时时间默认为10s
	 * 
	 * @param queueName
	 * @param data
	 */
	public void addDelayedJob(String queueName, String data) {
		addDelayedJob(queueName, data, 10);
	}

	/**
	 * 增加一个延时的Job，并以data去重，延时时间由参数<code>delayTime<code>指定。
	 * 
	 * @param queueName
	 * @param data
	 * @param delayTime
	 *            延时时间
	 */
	public void addDelayedJob(String queueName, String data, long delayTime) {
		DelayedRedisQueue queue = (DelayedRedisQueue) queues.get(queueName);
		if (queue == null)
			queue = (DelayedRedisQueue) connectToQueue(queueName, DELAY_QUEUE);
		// 加入到队列中，如果失败则多次尝试，直至成功或者尝试10次仍旧失败后，则放弃。
		
		int count = 1;
		while (queue.addJob(data, delayTime) == false && count < 10){
			try {
				log.info("加入队列失败!等待500ms后重新尝试加入!");
				count++;
				Thread.sleep(500 * count);
			} catch (InterruptedException e) {
				return ;
			}
		}
		if(count>=10)
			log.error("加入队列失败,并且已经尝试10次！");
		else
			log.info("Job加入队列成功!");

	}

	/**
	 * 连接到队列，调用其他方法前，必须先调用此方法。
	 * 
	 * @param queueName
	 * @param queueType
	 * @return
	 */
	public synchronized RedisQueue connectToQueue(String queueName,
			int queueType) {
		RedisQueue queue = null;
		switch (queueType) {
		case NORMAL_QUEUE:
			// 默认超时时间为5分钟，5分钟如果没有处理掉，则守护线程会回收处于doing中的Job。
			queue = new RedisQueue(queueName, 5 * 60);
			break;
		case DELAY_QUEUE:
			queue = new DelayedRedisQueue(queueName, 5 * 60, 20);
			break;
		case PRIORITY_QUEUE:
			queue = new PriorityQueue(queueName, 5 * 60);
			break;
		}
		queues.put(queueName, queue);
		return queue;
	}

	/**
	 * 根据队列名称获取队列对象。
	 * 
	 * @param queueName
	 *            队列名称。
	 * @return
	 */
	public RedisQueue getQueue(String queueName) {
		RedisQueue queue = queues.get(queueName);
		// FIXME 下面这行只是为了兼容老的代码而设定的，如果用到的不是Normal Queue，则会不正常。
		if (queue == null)
			queue = connectToQueue(queueName, NORMAL_QUEUE);
		return queue;
	}

	/**
	 * 队列中Job的数量
	 * 
	 * @param queueName
	 * @return
	 */
	public long numInQueue(String queueName) {
		RedisQueue queue = getQueue(queueName);
		return queue.getNumInQueue();
	}

	/**
	 * 获取（申索）一个Job，调用者在获取Job对象后执行该Job，完成的时候调用finishedJob方法。
	 * 
	 * @param queueName
	 * @return
	 */
	public Job claimJob(String queueName) {
		try {
			RedisQueue queue = getQueue(queueName);
			return queue.claimJob();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 不等待的获取（申索）一个Job，调用者在获取Job对象后执行该Job，完成的时候调用finishedJob方法。
	 * 如果队列为空，则返回一个null。
	 * 
	 * @param queueName
	 * @return
	 */
	public Job claimJobWithoutWait(String queueName) {
		try {
			RedisQueue queue = getQueue(queueName);
			return queue.claimJob(false);
		} catch (Exception e) {
			log.error("获取Job失败，错误原因为:" + e.getMessage());
		}
		return null;
	}

	/**
	 * 完成Job调用，清理相关的容错信息。
	 * 
	 * @param queueName
	 * @param job
	 */
	public void finishedJob(String queueName, Job job) {
		if (job != null) {
			try {
				RedisQueue queue = getQueue(queueName);
				queue.finishJob(job);
			} catch (Exception e) {
				log.error("job finished 失败 ,原因:" + e.getMessage());
			}
		}
	}

	/**
	 * 召回失败的Job，并返回到队列中重新执行，该方法一般不需要调用，由守护线程定时执行即可。
	 * 
	 * @param queueName
	 */
	public void recallJobs(String queueName) {
		RedisQueue queue = getQueue(queueName);
		queue.recallJobs();
	}
}
