package cn.quickj.jobs.service;

import java.util.concurrent.ArrayBlockingQueue;

import cn.quickj.test.QuickjBaseTestCase;

public class JobServiceTest extends QuickjBaseTestCase {
	private static int waitCount=0;
	private JobService jobService;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		jobService = getInstance(JobService.class);
	}
	public synchronized static void incWaitCount(){
		waitCount++;
	}
	/**
	 * 高并发测试。
	 * @throws InterruptedException 
	 */
	public void testConcurrentClaimJob() throws InterruptedException{
		int testJobCount = 100000;
		//加入100000个Job到队列中。
		for(int i=0;i<testJobCount;i++){
			jobService.addDelayedJob("TestDelayedQueue", String.valueOf(i),1);
		}
		ArrayBlockingQueue<String> result = new ArrayBlockingQueue<String>(testJobCount+1000);
		Thread.sleep(1);
		//开启4个线程，进行并发测试.
		DelayedClaimJobThread t1 = new DelayedClaimJobThread("TestDelayedQueue", jobService, result);
		DelayedClaimJobThread t2 = new DelayedClaimJobThread("TestDelayedQueue", jobService, result);
		DelayedClaimJobThread t3 = new DelayedClaimJobThread("TestDelayedQueue", jobService, result);
		DelayedClaimJobThread t4 = new DelayedClaimJobThread("TestDelayedQueue", jobService, result);
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		while(waitCount<4)
			Thread.sleep(1000);
		//判断最后的结果有没有重复。
		assertEquals(result.size(), testJobCount);
		for(int i=0;i<testJobCount;i++){
			if(i!=Integer.valueOf(result.take())){
				System.out.println("获取的Job存在顺序不一致的问题。");
			}
		}
		
	}
	/**
	public void testAddJob() {
		String data =  "TestJob'''\\\"\n\r:"+RandomStringUtils.randomNumeric(10);
		jobService.addJob("test",data);
		Job job = jobService.claimJob("test");
		assertEquals(data,job.getData());
		try {
			Thread.sleep(1000*30);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		job = jobService.claimJob("test");
		assertEquals(data,job.getData());
		jobService.finishedJob("test", job);
	}
	public void testDelayJob(){
		String data =  "TestJob'''\\\"\n\r:"+RandomStringUtils.randomNumeric(10);
		jobService.addDelayedJob("delayTest",data,30);
		Job job = jobService.claimJobWithoutWait("delayTest");
		assertEquals(job, null);
		try {
			Thread.sleep(1000*20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		job = jobService.claimJob("delayTest");
		assertEquals(data,job.getData());
		jobService.finishedJob("delayTest", job);
	}*/
}
