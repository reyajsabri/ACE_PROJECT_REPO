package com.sapient.assignment.cache.usage;

import java.util.concurrent.Future;

import com.sapient.assignment.cache.CacheEvictionNotifier;
import com.sapient.assignment.cache.CacheProvider;
import com.sapient.assignment.cache.EvictionReason;
import com.sapient.assignment.cache.ObjectFinder;

/**
 * This class demonstrate CacheProvider usage
 * 
 * @author msabri
 *
 */
public class CacheDemo {

	private static final CacheProvider<Integer, Employee> provider;
	private static final ObjectFinder<Integer, Employee> finder;
	
	private static final long timeToLive = 15*1000;
	private static final CacheEvictionNotifier<Future<Employee>, EvictionReason> notifier;
	
	static {
		finder = new EmployeeFinder<Integer, Employee>(); // "ObjectFinder" IMPL
		notifier = new CacheEvictionNotifierImpl<Future<Employee>, EvictionReason>(); // notifier IMPL
		provider = new CacheProvider<Integer, Employee>(finder,5, timeToLive, notifier);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		CacheDemo app = new CacheDemo();
		System.out.println("************ Search Usage *********************");
		app.searchTest();
		System.out.println("###########################################");
		System.out.println("************ Size Eviction *********************");
		app.sizeTest();
		
		finder.shutdown();
	}
	
	public void searchTest() {
		try {
			Employee emp = provider.findObject(7); // check your database and pick existing id for test
			System.out.println(emp);
			Thread.sleep(10000); // spend some time but within "timeToLive" of expiration
			System.out.println("Re Finding before expire");
			emp = provider.findObject(7);
			System.out.println(emp);
			Thread.sleep(20000); // time renewed, but again spend some time more than "timeToLive" of expiration
			System.out.println("Re Finding After expire");
			emp = provider.findObject(7); // re-initiate external search
			System.out.println(emp);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sizeTest() {
		try {
			Employee emp = provider.findObject(1);
			emp = provider.findObject(2);
			emp = provider.findObject(1);
			emp = provider.findObject(3);
			emp = provider.findObject(4);
			emp = provider.findObject(5);
			emp = provider.findObject(6);
			
			System.out.println("CACHE ITEMS ::" + provider.traceObjectGraph());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	

}
