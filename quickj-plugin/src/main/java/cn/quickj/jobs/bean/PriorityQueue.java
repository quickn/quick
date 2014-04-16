package cn.quickj.jobs.bean;

/**
 * 具有优先级的队列，队列根据addJob时候指定的优先级来排序，并且根据内容去重。
 * @author jekkro
 *
 */
public class PriorityQueue extends RedisQueue {

	public PriorityQueue(String queueName, int timeout) {
		super(queueName, timeout);
	}

}
