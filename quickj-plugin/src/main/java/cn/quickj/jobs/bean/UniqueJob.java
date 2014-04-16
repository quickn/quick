package cn.quickj.jobs.bean;

public class UniqueJob extends Job {
	@Override
	public String getJobKey() {
		return getData();
	}
}
