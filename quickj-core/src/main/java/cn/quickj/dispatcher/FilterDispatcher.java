package cn.quickj.dispatcher;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.quickj.Application;
import cn.quickj.manager.SessionManager;
import cn.quickj.session.Session;

public class FilterDispatcher implements Filter {
	public static final String QUICKJ_SESSION_COOKIE_KEY = "q_session";

	Application app;
	static final ThreadLocal<Context> localContext = new ThreadLocal<Context>();
	static final ThreadLocal<Session> sessions = new ThreadLocal<Session>();
	public static final SessionManager sessionManager = new SessionManager();

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {		
		try {
			localContext.set(new Context((HttpServletRequest) request,
					(HttpServletResponse) response));
			if (!app.handle((HttpServletRequest) request,
					(HttpServletResponse) response))
				chain.doFilter(request, response);
		} finally {
			localContext.remove();
			sessions.remove();
		}

	}

	public void init(FilterConfig config) throws ServletException {

		try {
			String clazz = config.getInitParameter("application");
			if (clazz == null)
				clazz = "cn.quickj.WebApplication";
			app = (Application) Class.forName(clazz).newInstance();
			app.init(config.getServletContext().getRealPath("/"));
			sessionManager.init();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	static public HttpServletRequest getRequest() {
		return getContext().getRequest();
	}

	static public HttpServletResponse getResponse() {
		return getContext().getResponse();
	}

	static public Session getSession() {
		Session session = sessions.get();
		if (session == null) {
			Cookie[] cookies = getRequest().getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals(QUICKJ_SESSION_COOKIE_KEY)) {
						session = FilterDispatcher.sessionManager.getSession(
								cookie.getValue(), getRequest(), getResponse());
						break;
					}
				}
			}
			if (session == null) {
				session = FilterDispatcher.sessionManager.getSession(null,
						getRequest(), getResponse());
			}
			sessions.set(session);
		}
		return session;
	}

	static Context getContext() {
		Context context = localContext.get();
		if (context == null) {
			throw new RuntimeException("Please apply "
					+ FilterDispatcher.class.getName()
					+ " to any request which uses servlet scopes.");
		}
		return context;
	}

	static class Context {

		final HttpServletRequest request;
		final HttpServletResponse response;

		Context(HttpServletRequest request, HttpServletResponse response) {
			this.request = request;
			this.response = response;
		}

		HttpServletRequest getRequest() {
			return request;
		}

		HttpServletResponse getResponse() {
			return response;
		}
	}
}
