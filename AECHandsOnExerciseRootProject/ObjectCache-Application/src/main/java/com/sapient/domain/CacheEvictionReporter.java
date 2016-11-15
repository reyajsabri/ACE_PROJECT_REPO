package com.sapient.domain;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

import com.sapient.assignment.cache.ExpirableTask;
import com.sapient.assignment.cache.usage.CacheNotifierImpl;

public class CacheEvictionReporter<L extends BlockingQueue<String>, O, R> extends CacheNotifierImpl<O, R> {
	private final L objectHistory;
	
	public CacheEvictionReporter(L objectHistory){
		this.objectHistory = objectHistory;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean notifyEvictionToUser(O o, R r) {
		try {
			objectHistory.add("Notifier:: Object: " + ((ExpirableTask<Person>)o).get().toString() + " Removed from Cache due to: "+ r);
			System.out.println("Notifier:: Object: " + ((ExpirableTask<Person>)o).get().toString() + " Removed from Cache due to: "+ r);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		
		return true;
	}
	
	/**
	 * @param <O>
	 * @param hitOrMiss true for hit occured in cache. false for miss happened.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean notifyHitOrMissToUser(O o, boolean hitOrMiss) {
		String ObjectDesc = null;
		try {
			ObjectDesc = ((ExpirableTask<Person>)o).isDone() ? ((ExpirableTask<Person>)o).get().toString() : "";
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if(hitOrMiss){
			objectHistory.add("Cache_Notifier:: Object: " + ObjectDesc + " found in Cache");
			System.out.println("Cache_Notifier:: Object: " + ObjectDesc + " found in Cache");
		}else{
			// Object Finder will report miss
		}
		return true;
	}
}
