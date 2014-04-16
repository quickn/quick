package cn.quickj.filter;

import java.util.HashMap;

import cn.quickj.action.Action;

public interface ActionFilter {
	/**
	 * filter后还需要继续处理。
	 */
	public final static int NEED_PROCESS = 0;
	/**
	 * 已经直接返回给浏览器了，不需要继续。
	 */
	public final static int NO_PROCESS = 1;

	public int after(Action action);

	public int before(Action action);

	public void init(HashMap<String, String> params);
}
