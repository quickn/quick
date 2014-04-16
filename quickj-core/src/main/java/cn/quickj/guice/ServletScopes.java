package cn.quickj.guice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.quickj.dispatcher.FilterDispatcher;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * Servlet scopes.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public class ServletScopes {

  private ServletScopes() {}

  /**
   * HTTP servlet request scope.
   */
  public static final Scope REQUEST = new Scope() {
    public <T> Provider<T> scope(Key<T> key, final Provider<T> creator) {
      final String name = key.toString();
      return new Provider<T>() {
        public T get() {
          HttpServletRequest request = FilterDispatcher.getRequest();
          synchronized (request) {
            @SuppressWarnings("unchecked")
            T t = (T) request.getAttribute(name);
            if (t == null) {
              t = creator.get();
              request.setAttribute(name, t);
            }
            return t;
          }
        }
      };
    }

    public String toString() {
      return "ServletScopes.REQUEST";
    }
  };

  /**
   * HTTP session scope.
   */
  public static final Scope SESSION = new Scope() {
    public <T> Provider<T> scope(Key<T> key, final Provider<T> creator) {
      final String name = key.toString();
      return new Provider<T>() {
        public T get() {
          HttpSession session = FilterDispatcher.getRequest().getSession();
          synchronized (session) {
            @SuppressWarnings("unchecked")
            T t = (T) session.getAttribute(name);
            if (t == null) {
              t = creator.get();
              session.setAttribute(name, t);
            }
            return t;
          }
        }
      };
    }

    public String toString() {
      return "ServletScopes.SESSION";
    }
  };
}
