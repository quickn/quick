package cn.quickj.guice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cn.quickj.AbstractApplication;
import cn.quickj.hibernate.HibernateTemplate;

public class TransactionInterceptor implements MethodInterceptor {
	private static Log log = LogFactory.getLog(TransactionInterceptor.class);

	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object result = null;
		if (log.isDebugEnabled())
			log.debug(invocation.getThis().getClass().getName()+",Method:" + invocation.getMethod().getName()
					+ ",transaction begin!");
		HibernateTemplate ht = AbstractApplication.injector
				.getInstance(HibernateTemplate.class);
		Session session = ht.getSession();
		
		//如果事务已经启动，则不再前套启动事务。
		if(session.getTransaction().isActive())
			result = invocation.proceed();
		else{
			Transaction tx= session.beginTransaction();
			try {
				result = invocation.proceed();
				tx.commit();
			} catch (Exception e) {
				tx.rollback();
				throw e;
			}
		}
		if (log.isDebugEnabled())
			log.debug(invocation.getThis().getClass().getName()+",Method:" + invocation.getMethod().getName()
					+ ",transaction end!");
		return result;
	}
}
