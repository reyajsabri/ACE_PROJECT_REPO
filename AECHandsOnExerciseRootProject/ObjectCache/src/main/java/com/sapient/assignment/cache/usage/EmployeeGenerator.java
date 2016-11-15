package com.sapient.assignment.cache.usage;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * Helper class to create Employee records in database
 * 
 * @author msabri
 *
 */
public class EmployeeGenerator {

	private static SessionFactory factory;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
	         factory = new Configuration().configure().buildSessionFactory();
	      }catch (Throwable ex) { 
	         System.err.println("Failed to create sessionFactory object." + ex);
	         throw new ExceptionInInitializerError(ex); 
	      }
		
		EmployeeGenerator eg = new EmployeeGenerator();
		
		/* Add few employee records in database */
	      Integer empID1 = eg.addEmployee("Reyaj", "Sabri", 1000);
	      Integer empID2 = eg.addEmployee("Gaurav", "Singh", 5000);
	      Integer empID3 = eg.addEmployee("Baivab", "Saxena", 10000);
	      Integer empID4 = eg.addEmployee("Rakesh", "Dhar", 20000);
	      Integer empID5 = eg.addEmployee("Preeti", "Gupta", 40000);
	      Integer empID6 = eg.addEmployee("Ashis", "Gupta", 60000);
	      Integer empID7 = eg.addEmployee("Avishek", "Gupta", 30000);
	      
	      factory.close();
	      

	}
	
	/* Method to CREATE an employee in the database */
	   public Integer addEmployee(String fname, String lname, int salary){
	      Session session = factory.openSession();
	      Transaction tx = null;
	      Integer employeeID = null;
	      try{
	         tx = session.beginTransaction();
	         Employee employee = new Employee(fname, lname, salary);
	         employeeID = (Integer) session.save(employee); 
	         tx.commit();
	      }catch (HibernateException e) {
	         if (tx!=null) tx.rollback();
	         e.printStackTrace(); 
	      }finally {
	         session.close(); 
	      }
	      return employeeID;
	   }

}
