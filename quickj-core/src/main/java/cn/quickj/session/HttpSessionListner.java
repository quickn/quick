package cn.quickj.session;

public interface HttpSessionListner {
	/**
	 * Session创建的信息。
	 * 
	 * @param session
	 */
	public void created(Session session);

	/**
	 * Session过时的通知消息。
	 * 
	 * @param session
	 */
	public void expired(Session session);
}
