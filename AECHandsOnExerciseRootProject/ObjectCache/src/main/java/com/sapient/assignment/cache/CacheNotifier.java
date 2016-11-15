package com.sapient.assignment.cache;

/**
 * @author msabri
 *
 * @param <O> Object that requested.
 * @param <R> Reason for Eviction. Either Time expired or Size overflow
 */
public interface CacheNotifier<O, R> {
	public boolean notifyEvictionToUser(O o, R r);
	public boolean notifyHitOrMissToUser(O o, boolean hitOrMiss);
}
