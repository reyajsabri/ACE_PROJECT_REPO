package com.sapient;

import java.util.concurrent.Callable;

/**
 * @author msabri
 *
 * @param <V>
 */
public interface StatusAwareCallable<V> extends Callable<V> {
	public int getCompletionStatus();
	public String getName();
}
