package com.sapient.assignment.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This class represent an expirable task where result will expired in "timeToLive"
 * @author msabri
 *
 * @param <V>
 */
public class ExpirableTask<V> extends FutureTask<V> {
	private final long timeToLive;
	private volatile long expirationTime;

	public ExpirableTask(Callable<V> callable, long timeToLive) {
		super(callable);
		this.timeToLive = timeToLive;
		
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		V v = super.get();
		expirationTime = System.currentTimeMillis() + timeToLive;
		return v;
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		V v = super.get(timeout, unit);
		expirationTime = System.currentTimeMillis() + timeToLive;
		return v;
	}

	/**
	 * Method returns true if "timeToLive" has elapse and false other wise.
	 * 
	 * @return
	 */
	public boolean isResultExpired() {
		if (isDone()) {
			if (expirationTime < System.currentTimeMillis()) {
				return true;
			}
		}
		return false;
	}
}
