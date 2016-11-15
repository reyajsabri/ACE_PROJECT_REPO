package com.sapient.assignment.cache.usage;

import com.sapient.assignment.cache.CacheNotifier;

/**
 * @author msabri
 *
 * @param <O> Object that requested
 * @param <R> Removal Reason
 */
public class CacheNotifierImpl<O, R> implements CacheNotifier<O, R> {

	@Override
	public boolean notifyEvictionToUser(O o, R r) {
		System.out.println("Cache_Notifier:: Object: " + o + " Removed from Cache due to: \""+ r +"\"");
		return true;
	}

	/**
	 * @param <O>
	 * @param hitOrMiss true for hit occur in cache. false for miss happened.
	 */
	@Override
	public boolean notifyHitOrMissToUser(O o, boolean hitOrMiss) {
		if(hitOrMiss)
			System.out.println("Cache_Notifier:: Object: " + o + " found in Cache");
		else
			System.out.println("Cache_Notifier:: Object: " + o + " Not found in Cache");
		return true;
	}

}
