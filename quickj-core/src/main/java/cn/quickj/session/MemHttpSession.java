package cn.quickj.session;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import cn.quickj.dispatcher.FilterDispatcher;

public class MemHttpSession extends AbstractHttpSession {
	ConcurrentHashMap<String, Object> session;

	public void create(String sessionId,HttpServletRequest request) {
		super.create(sessionId, request);
		session = new ConcurrentHashMap<String, Object>();
	}

	public void destroy() {
		if (session != null) {
			session.clear();
			FilterDispatcher.sessionManager.destory(this);
		}
		session = null;
	}

	public Object get(String key) {
		updateAccessTime();
		return session.get(key);
	}

	public void set(String key, Object value) {
		updateAccessTime();
		session.put(key, value);
	}

	public void update() {
		// Nothing to do.
	}

	public void remove(String key) {
		session.remove(key);
	}
}
