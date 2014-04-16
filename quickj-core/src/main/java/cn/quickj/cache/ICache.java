package cn.quickj.cache;


public interface ICache {
	/**
	 * 删除某个key的cache
	 * @param key 要删除key
	 */
	public void delete(String key);
	/**
	 * 获取某个key的cache值
	 * @param key
	 * @return
	 */
	public Object get(String key);
	/**
	 * 按照制定类型获取对象。
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T extends Object> T get(String key,Class<T>clazz);
	/**
	 * 设置key，value，并带有超时时间（单位秒）
	 * @param key 
	 * @param timeOut 超时时间，单位为秒，如果设为-1，则表示不设置超时时间。
	 * @param value
	 */
	public void set(String key, int timeOut, Object value);
	/**
	 * 创建cache对象
	 * @param param cache所使用的参数，格式要和具体所使用的cache客户端相适应
	 */
	public void createCache(String param);
	/**
	 * 关闭cache服务器。
	 */
	public void shutdown();
	/**
	 * 废除所有cache
	 */
	public void flushAll();
	/**
	 * 获取cache的状态信息。返回为 key:value的形式。
	 * @return
	 */
	public String info();
}
