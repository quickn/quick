package cn.quickj.session;

import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import cn.quickj.manager.CacheManager;

/**
 * 使用MemCached实现的分布式session,已经被废除建议使用<code>RedisHttpSession</code>
 * 在实现过程中，我们把所有这个session用到的key都保存到了Memcache中，它的key为session_key_sessionid，并设置和Session的Setting.sessionTimeout相同的过时时间。
 * 
 * @author lbj
 * 
 */
@Deprecated
@SuppressWarnings("unchecked")
public class MemcacheHttpSession extends AbstractHttpSession {
	private HashSet<String>keys;
	/**
	 * 重载创建session的方法，首选从cache中恢复原先的sessionId的session，如果没有，则调用抽象类的create方法
	 */
	@Override
	public void create(String sessionId,HttpServletRequest request) {
		keys = (HashSet<String>) CacheManager.getCacheManager().get("session_key" + sessionId);
		if(keys==null || keys.isEmpty()){
			super.create(sessionId, request);
			keys = new HashSet<String>();
			CacheManager.getCacheManager().set("session_key" + this.sessionId, 3600*24, keys);
		}else{
			this.sessionId = sessionId;
			//更新失效时间。
			CacheManager.getCacheManager().set("session_key" + this.sessionId, 3600*24, keys);
		}
	}
	public void destroy() {
		CacheManager manager = CacheManager.getCacheManager();
		if (keys != null) {
			for (String key : keys) {
				manager.delete(key);
			}
			keys.clear();
		}
		manager.delete("session_key" + sessionId);
	}

	/**
	 * 先从本地缓存中取数据，如果没有，则到远程memcache上取。
	 */
	public Object get(String key) {
		return 	CacheManager.getCacheManager().get(sessionId + key);
	}

	public void set(String key, Object value) {
		if(value==null){
			//如果设置为空，则从cache中删除。
			remove(key);
			return;
		}
		CacheManager manager = CacheManager.getCacheManager();
		if(keys.contains(key)){
			//如果session中已经有该值，则直接替换
			manager.set("session_key" + sessionId, 3600*24, keys);
			manager.set(sessionId + key, (int) 3600*24, value);
		}else{
			keys.add(key);
			manager.set("session_key" + sessionId, 3600*24, keys);
			manager.set(sessionId + key, 3600*24, value);
		}
	}

	public void update() {
	}

	public void remove(String key) {
		CacheManager manager = CacheManager.getCacheManager();
		keys.remove(key);
		manager.delete(sessionId+key);
	}
}
