package com.sapient.domain;

import java.util.List;

public interface Producer<V> {
	public void produce(List<V> v);
}
