package cn.quickj.hibernate;

import java.io.Serializable;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class HibernateTemplate {
	private static SessionFactory sessionFactory;
	public static final ThreadLocal<Session> session = new ThreadLocal<Session>();

	@Inject
	public void setSessionFactory(SessionFactory sf) {
		sessionFactory = sf;
	}

	public void save(Serializable entity) {
		getSession().saveOrUpdate(entity);
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T  load(Class<T> clazz, Serializable id) {
		return (T) getSession().get(clazz, id);
	}

	public void delete(Class<?> clazz, Serializable id) {
		Object entity = getSession().get(clazz, id);
		getSession().delete(entity);
	}

	public void delete(Serializable entity) {
		getSession().delete(entity);
	}


	public void update(Serializable entity) {
		getSession().saveOrUpdate(entity);
	}

	/**
	 * TODO 完成动态Finder操作,类似于ActiveRecord中的find_by_sql。
	 * 
	 * @param sql
	 * @return
	 */
	public SQLQuery querySql(String sql) {
		return getSession().createSQLQuery(sql);
	}

	/**
	 * TODO 完成动态Finder功能，类似于ActiveRecord的Find命令。
	 * 
	 * @param hql
	 * @return
	 */
	public Query query(String hql) {
		return getSession().createQuery(hql);
	}

	public Session getSession() {
		Session s = session.get();
		if (s == null) {
			s = sessionFactory.openSession();
			session.set(s);
		}
		return session.get();
	}

	/**
	 * @deprecated 请使用closeSession()
	 * @param s
	 */
	public void closeSession(Session s) {
		s.close();
		session.set(null);
	}

	public void closeSession() {
		Session s = session.get();
		if (s != null) {
			s.close();
			session.set(null);
		}
	}

	public void clearCache() {
		Session s = session.get();
		if(s!=null)
			s.clear();
	}
}
