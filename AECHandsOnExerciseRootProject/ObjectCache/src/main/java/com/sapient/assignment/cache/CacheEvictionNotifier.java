package com.sapient.assignment.cache;

/**
 * @author msabri
 *
 * @param <O> Object that Evicted.
 * @param <R> Reason for Eviction. Either Time expired or Size overflow
 */
public interface CacheEvictionNotifier<O, R> {
	public boolean nitifyUser(O o, R r);
}
