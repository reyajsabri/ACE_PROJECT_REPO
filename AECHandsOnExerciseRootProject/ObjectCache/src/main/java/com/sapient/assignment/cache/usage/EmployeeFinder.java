package com.sapient.assignment.cache.usage;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.sapient.assignment.cache.ObjectFinder;

/**
 * @author msabri
 *
 * @param <A>
 * @param <V>
 */
public class EmployeeFinder<A, V> implements ObjectFinder<A, V> {
	
	private static SessionFactory factory;
	
	static {
		try{
	         factory = new Configuration().configure().buildSessionFactory();
	      }catch (Throwable ex) { 
	         System.err.println("Failed to create sessionFactory object." + ex);
	         throw new ExceptionInInitializerError(ex); 
	      }
	}

	@Override
	public V findObject(A arg) throws InterruptedException {
		
		System.out.println("Remote Fetch occured for :"+arg);
		
		Session session = factory.openSession();
	      Transaction tx = null;
	      try{
	         tx = session.beginTransaction();
	         V v = (V)session.get(Employee.class, (Integer)arg); 
	         tx.commit();
	         return v;
	      }catch (HibernateException e) {
	         if (tx!=null) tx.rollback();
	         e.printStackTrace(); 
	      }finally {
	         session.close(); 
	      }
		return null;
	}

	@Override
	public void shutdown() {
		factory.close();
		
	}

}
