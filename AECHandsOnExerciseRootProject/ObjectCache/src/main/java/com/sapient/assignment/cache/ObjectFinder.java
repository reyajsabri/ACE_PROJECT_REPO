package com.sapient.assignment.cache;

/**
 * @author msabri
 *
 * @param <A>
 * @param <V>
 */
public interface ObjectFinder<A, V> {
	V findObject(A arg) throws InterruptedException;
	void shutdown();
}
