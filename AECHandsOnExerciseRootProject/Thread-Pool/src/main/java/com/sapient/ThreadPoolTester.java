package com.sapient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;


/**
 * @author msabri
 *
 */
public class ThreadPoolTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//
		new ThreadPoolTester().testThreadPool();
		
	}
	
	public void testThreadPool() {
		StatusAwareTaskImpl<String> task1 = new StatusAwareTaskImpl<String>("task1");
		StatusAwareTaskImpl<String> task2 = new StatusAwareTaskImpl<String>("task2");
		StatusAwareTaskImpl<String> task3 = new StatusAwareTaskImpl<String>("task3");
		StatusAwareTaskImpl<String> task4 = new StatusAwareTaskImpl<String>("task4");
		StatusAwareTaskImpl<String> task5 = new StatusAwareTaskImpl<String>("task5");


		List<StatusAwareTaskImpl<String>> taskList = new ArrayList<>();
		taskList.add(task1);
		taskList.add(task2);
		taskList.add(task3);
		taskList.add(task4);
		taskList.add(task5);
		
		ThreadPoolImpl<String> pool = new ThreadPoolImpl<>(2, 6);

		
//		@SuppressWarnings({ "unused", "unchecked" })
//		StstusAwareFutureTask<StatusAwareCallable<String>, String> future =  (StstusAwareFutureTask<StatusAwareCallable<String>, String>) pool.submit(task1);
//		while(future.getCompletionProgress() <100){
//			System.out.println(""+ future.getCompletionProgress());
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		
		
		@SuppressWarnings({ "unused", "unchecked" })
		StatusAwareFutureTask<StatusAwareCallable<String>, String> ststusAwareFutureTask = null;
		
		try {
			List<Future<String>> futures = pool.submitAll(taskList);
			int i = 0;
			while(i<100){
				for(Future<String> future : futures){
					ststusAwareFutureTask = (StatusAwareFutureTask<StatusAwareCallable<String>, String>)future ;
					i = ststusAwareFutureTask.getCompletionProgress();
					System.out.println(ststusAwareFutureTask.getName()+" Completed: "+ i);
				}
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	class StatusAwareTaskImpl<V> implements StatusAwareCallable<V>{
		private volatile int completionProgress;
		private final String name;

		public StatusAwareTaskImpl(String name){
			this.name = name;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public V call() throws Exception {
			
			while(completionProgress < 100){
				completionProgress++;
				System.out.println(Thread.currentThread().getName() + " Completed: "+completionProgress +"%");
				Thread.sleep(100);
			}
			return (V) ("Data:"+ completionProgress);
		}

		@Override
		public int getCompletionStatus() {
			return completionProgress;
		}

		@Override
		public String getName() {
			return name;
		}
		
		
	}
	

}
