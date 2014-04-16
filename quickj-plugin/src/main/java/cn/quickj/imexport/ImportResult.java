package cn.quickj.imexport;

import java.util.List;

public class ImportResult {
	private int success;
	private int failure;
	private List<String> failures;
	
	public ImportResult(int success, int failure, List<String> failures) {
		this.success = success;
		this.failure = failure;
		this.failures = failures;
	}
	public ImportResult() {
		// TODO Auto-generated constructor stub
	}
	public int getSuccess() {
		return success;
	}
	public void setSuccess(int success) {
		this.success = success;
	}
	public int getFailure() {
		return failure;
	}
	public void setFailure(int failure) {
		this.failure = failure;
	}
	public List<String> getFailures() {
		return failures;
	}
	public void setFailures(List<String> failures) {
		this.failures = failures;
	}
	
}
