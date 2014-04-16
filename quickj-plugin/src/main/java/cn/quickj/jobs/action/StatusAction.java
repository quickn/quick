package cn.quickj.jobs.action;

import java.util.Collection;

import cn.quickj.action.Action;
import cn.quickj.jobs.bean.Job;
import cn.quickj.jobs.bean.RedisQueue;
import cn.quickj.jobs.service.JobService;

import com.google.inject.Inject;

/**
 * 查看JobQueue的状态。 TODO 可以看到每个队列的中的数据，执行情况。还可以暂停执行队列，也可以清除某个特定的Job。同时对整个队列可以清除。
 * 
 * @author yxq
 * 
 */
public class StatusAction extends Action {
	@Inject
	private JobService jobService;
	@Inject
	private Job job;

	private Collection<RedisQueue> queues;
	private Collection<Job> jobs;
	private RedisQueue queue;
	private String queueType;
	private String jobType;

	/**
	 * 列出所有队列
	 */
	public void queueList() {
		queues = jobService.getAllQueues();
		render("queueList.html");
	}

	/**
	 * 列出队列中的所有job
	 * 
	 * @param queueName
	 */
	public void jobList(String queueName) {
		queue = jobService.getQueue(queueName);
		if (jobType != null && queue != null) {
			if ("todo".equals(jobType))
				jobs = queue.getAllJobs();
			else
				jobs = queue.getAllDoingJobs();
		}
		render("jobList.html");
	}

	/**
	 * 清空队列数据
	 * 
	 * @param queueName
	 * @return
	 */
	public String cleanupQueue(String queueName) {
		RedisQueue queue = jobService.getQueue(queueName);
		if (queue != null) {
			queue.cleanupQueue();
			return "success";
		}
		return "未找到名称为" + queueName + "的队列";
	}

	/**
	 * 新增一个job
	 * 
	 * @param queueName
	 * @param jobData
	 * @return
	 */
	public String addJob(String queueName) {
		jobService.addJob(queueName, job.getData());
		return "success";
	}

	/**
	 * 根据jobKey删除job
	 * 
	 * @param queueName
	 * @param jobKey
	 * @return
	 */
	public String deleteJob(String queueName, String jobKey) {
		RedisQueue queue = jobService.getQueue(queueName);
		if (queue != null) {
			queue.deleteJob(jobKey);
			return "success";
		}
		return "未找到名称为" + queueName + "的队列中jobKey为" + jobKey + "的job";
	}

	public Collection<RedisQueue> getQueues() {
		return queues;
	}

	public Collection<Job> getJobs() {
		return jobs;
	}

	public RedisQueue getQueue() {
		return queue;
	}

	public Job getJob() {
		return job;
	}

	public String getQueueType() {
		return queueType;
	}

	public String getJobType() {
		return jobType;
	}
}
