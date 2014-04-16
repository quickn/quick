package cn.quickj.simpleui.model;

public class ClickPath {
	String name;
	String url;
	public ClickPath(String name, String url) {
		super();
		this.name = name;
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public String getUrl() {
		return url;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
