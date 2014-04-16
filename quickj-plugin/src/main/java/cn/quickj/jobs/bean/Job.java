package cn.quickj.jobs.bean;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * job代表一个等待完成的任务，它有JobId（resque队列中的id），priority(优先级),data(job相关的数据)
 * 
 * @author jekkro
 * 
 */
public class Job {
	private long jobId;
	private int priority;
	private String data;
	private long timestamp;

	public long getJobId() {
		return jobId;
	}

	@JsonProperty("id")
	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public int getPriority() {
		return priority;
	}

	@JsonProperty("p")
	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getData() {
		return data;
	}

	@JsonProperty("d")
	public void setData(String data) {
		this.data = data;
	}

	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * 
	 * @param timestamp
	 */
	@JsonProperty("t")
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * 不同的Job类型可以override这个方法，比如可以实现唯一Job（把内容当作key）。
	 * 
	 * @return
	 */
	@JsonIgnore
	public String getJobKey() {
		return String.valueOf(jobId);
	}

	/**
	 * job加入队列时间
	 * 
	 * @return
	 */
	@JsonIgnore
	public Date getJoinTime() {
		Date date = new Date(timestamp * 1000);
		return date;
	}

}
