package cn.quickj.manager;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.quickj.Setting;
import cn.quickj.dispatcher.FilterDispatcher;
import cn.quickj.session.HttpSessionListner;
import cn.quickj.session.Session;

public class SessionManager extends Thread {
	private static Log log = LogFactory.getLog(SessionManager.class);

	private HashMap<HttpSessionListner, HttpSessionListner> listeners = new HashMap<HttpSessionListner, HttpSessionListner>();

	@SuppressWarnings("rawtypes")
	private Class sessionClass;

	private ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();

	public SessionManager() {

	}
	public void init(){
		try {
			sessionClass = Class.forName(Setting.sessionClass);
		} catch (ClassNotFoundException e) {
			log.info("cannot load the class " + Setting.sessionClass
					+ "! using MemHttpSession instead!");
			try {
				sessionClass = Class
						.forName("cn.quickj.session.MemHttpSession");
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}	
		start();
	}
	public void destory(Session session){
		if(session!=null){
			sessionMap.remove(session.getSessionId());
			log.info("SESSION expired,SessionID:"+session.getSessionId());
			for (HttpSessionListner listener : listeners.values()) {
				listener.expired(session);
			}					
			session = null;
		}
	}
	public Session getSession(String sessionId, HttpServletRequest request,HttpServletResponse response) {
		Session s = null;
		if (sessionId != null)
			s = sessionMap.get(sessionId);
		if (s == null) {
			s = createSession(sessionId,request,response);
			sessionMap.put(s.getSessionId(), s);
		}
		s.updateAccessTime();
		return s;
	}

	public void registerListner(HttpSessionListner listner) {
		listeners.put(listner, listner);
	}

	public void removeListner(HttpSessionListner listener) {
		listeners.remove(listener);
	}

	/**
	 * 检查Session中超时的部分，更新超时时间。
	 */
	@Override
	public void run() {
		while(true){
			for(Session session :sessionMap.values()){
				if(session.isexpired()){
					session.destroy();
					sessionMap.remove(session.getSessionId());
				}
			}
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建Session，使用配置文件中指定的Session类，创建成功后通知Listener。
	 * @param response 
	 * 
	 * @return
	 */
	private Session createSession(String sessionId,HttpServletRequest request, HttpServletResponse response) {
		Session session = null;
		try {
			session = (Session) sessionClass.newInstance();
			session.create(sessionId,request);
			Cookie cookie = new Cookie(FilterDispatcher.QUICKJ_SESSION_COOKIE_KEY, session
					.getSessionId());
			cookie.setPath("/");
			//如果指定了session的作用域，则在cookie里进行指定域，这样可以实现跨子域的功能，比如
			// a.domain.com和b.domain.com希望能实现session共享，则可以指定session的domain。
			if(Setting.sessionDomain!=null)
				cookie.setDomain(Setting.sessionDomain);
			cookie.setMaxAge(-1);
			response.addCookie(cookie);
			log.info("SESSION created，sessionId:"+session.getSessionId());
			for (HttpSessionListner listener : listeners.values()) {
				listener.created(session);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return session;
	}

}
