package cn.quickj.guice;

import static cn.quickj.guice.ServletScopes.REQUEST;
import static cn.quickj.guice.ServletScopes.SESSION;
import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SessionFactory;

import cn.quickj.Setting;
import cn.quickj.annotation.Transaction;
import cn.quickj.dispatcher.FilterDispatcher;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;

/**
 * guice module缺省注入Hibernate Session,HttpSession，Request,Response.
 * 
 * @author Administrator
 * 
 */
public class QuickjModule extends AbstractModule {
	private boolean transcatable;

	public QuickjModule() {
		transcatable = true;
	}

	public QuickjModule(boolean transcatable) {
		this.transcatable = transcatable;
	}

	@Override
	protected void configure() {
		// Scopes.
		bindScope(RequestScoped.class, REQUEST);
		bindScope(SessionScoped.class, SESSION);
		// Bind request.
		Provider<HttpServletRequest> requestProvider = new Provider<HttpServletRequest>() {
			public HttpServletRequest get() {
				return FilterDispatcher.getRequest();
			}

			public String toString() {
				return "RequestProvider";
			}
		};
		bind(HttpServletRequest.class).toProvider(requestProvider);
		bind(ServletRequest.class).toProvider(requestProvider);
		if (transcatable)
			bindInterceptor(any(), annotatedWith(Transaction.class),
					new TransactionInterceptor());
		// Bind response.
		Provider<HttpServletResponse> responseProvider = new Provider<HttpServletResponse>() {
			public HttpServletResponse get() {
				return FilterDispatcher.getResponse();
			}

			public String toString() {
				return "ResponseProvider";
			}
		};
		bind(HttpServletResponse.class).toProvider(responseProvider);
		bind(ServletResponse.class).toProvider(responseProvider);
		// Bind session.
/*		Provider<Session> sessionProvider = new Provider<Session>() {
			public Session get() {
				return FilterDispatcher.getSession();
			}

			public String toString() {
				return "SessionProvider";
			}
		};
		bind(Session.class).toProvider(sessionProvider);*/
		if (Setting.usedb) {
			bind(SessionFactory.class).toProvider(
					new Provider<SessionFactory>() {
						public SessionFactory get() {
							return Setting.sessionFactory;
						}

						public String toString() {
							return "SessionFactoryProver";
						}
					});
		}
	}
}
