package cn.quickj.jobs.service;

import java.util.concurrent.ArrayBlockingQueue;

import cn.quickj.jobs.bean.Job;

public class DelayedClaimJobThread extends Thread {
	private ArrayBlockingQueue<String> result;
	private JobService jobService;
	private String queueName;

	public DelayedClaimJobThread(String queueName, JobService jobService,
			ArrayBlockingQueue<String> result) {
		this.jobService = jobService;
		this.result = result;
		this.queueName = queueName;
	}

	@Override
	public void run() {
		Job job = null;
		do {
			job = jobService.claimJob(queueName);
			try {
				if (job != null) {
					System.out.println(job.getJobKey());
					result.put(job.getJobKey());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			jobService.finishedJob(queueName, job);
		} while (job != null);
		System.out.println("执行完毕!");
		JobServiceTest.incWaitCount();
	}
}
