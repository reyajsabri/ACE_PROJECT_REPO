package com.sapient;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author msabri
 *
 * @param <T>
 */
public interface ThreadPool<T> {
	void shutdown();
	boolean isShutdown();
	Future<T> submit(Callable<T> task);
	List<Future<T>> submitAll(Collection<? extends Callable<T>> tasks)
	        throws InterruptedException;
}
