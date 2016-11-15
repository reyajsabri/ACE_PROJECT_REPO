package com.sapient.ripository;

public interface Repository<A, V> {
	public void addToDatabase(V v);
	public V getFromDatabase(A a);
}
