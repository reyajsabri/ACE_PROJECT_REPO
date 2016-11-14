package com.sapient.assignment.cache.usage;

import com.sapient.assignment.cache.CacheEvictionNotifier;

/**
 * @author msabri
 *
 * @param <O> Object that have been removed
 * @param <R> Removal Reason
 */
public class CacheEvictionNotifierImpl<O, R> implements CacheEvictionNotifier<O, R> {

	@Override
	public boolean nitifyUser(O o, R r) {
		System.out.println("Eciction_Notifier:: Object: " + o + " Removed from Cache due to: \""+ r +"\"");
		return true;
	}

}
