package cn.quickj.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.quickj.action.Action;
import cn.quickj.session.MemHttpSession;
import cn.quickj.session.Session;

public class QuickjActionTestCase extends QuickjBaseTestCase {
	private static HttpServletRequest request;
	private static HttpServletResponse response;
	private static Session session;

	public static HttpServletRequest getRequest() {
		return request;
	}

	public static HttpServletResponse getResponse() {
		return response;
	}

	public static Session getSession() {
		if (session == null) {
			session = new MemHttpSession();
			// session.create(fName, request);
		}
		return session;
	}

	public Action doTest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		QuickjActionTestCase.request = request;
		QuickjActionTestCase.response = response;
		app.handle(request, response);
		return (Action) request.getAttribute("quickj_action");
	}
}
