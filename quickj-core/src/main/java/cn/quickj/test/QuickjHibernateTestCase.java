package cn.quickj.test;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cn.quickj.hibernate.HibernateTemplate;

public class QuickjHibernateTestCase extends QuickjBaseTestCase {
	
	protected HibernateTemplate ht ;
	
	protected Session session ;
	
	protected Transaction tx ;


	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ht = getInstance(HibernateTemplate.class);
		session = ht.getSession();
		tx = session.beginTransaction();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if(!tx.wasRolledBack())
			tx.rollback();
		ht.closeSession();
	}
	
	
	
	
}
