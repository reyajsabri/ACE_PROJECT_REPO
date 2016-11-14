package com.sapient;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * @author msabri
 *
 * @param <E>
 * @param <V>
 */
public class StatusAwareFutureTask<E extends Callable<V>, V> extends FutureTask<V> implements RunnableFuture<V>{

	private E task;
	public StatusAwareFutureTask(E callable) {
		super( callable);
		task = callable;
	}
	
	public int getCompletionProgress(){
		return ((StatusAwareCallable<V>) task).getCompletionStatus();
	}
	
	public String getName(){
		return ((StatusAwareCallable<V>) task).getName();
	}
	
	
}
