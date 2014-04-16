package cn.quickj.session;

import javax.servlet.http.HttpServletRequest;

public interface Session {
	public void create(String sessionId,HttpServletRequest request);

	public void destroy();

	public Object get(String key);

	/**
	 * 取sessionid，一般使用cookie中的sessionid作为key.
	 * 
	 * @return
	 */
	public String getSessionId();

	public boolean isexpired();

	public void set(String key, Object value);
	public void remove(String key);

	/**
	 * @see getSessionId
	 * @param sessionId
	 */
	public void setSessionId(String sessionId);

	public void update();

	public void updateAccessTime();
}
