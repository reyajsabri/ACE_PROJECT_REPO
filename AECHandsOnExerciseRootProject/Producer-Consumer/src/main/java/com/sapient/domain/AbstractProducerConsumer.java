package com.sapient.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author msabri
 *
 */
public abstract class AbstractProducerConsumer<V> {

	protected final int bufferSize;
	//private final Lock lock = new ReentrantLock();
	private final Exchanger<List<V>> exchanger = new Exchanger<List<V>>();

	private final List<V> bufferOne;
	private final List<V> bufferTwo;
	
	public List<V> getBufferOne() {
		return bufferOne;
	}
	
	public List<V> getBufferTwo() {
		return bufferTwo;
	}

	public AbstractProducerConsumer(int bufferSize){
		this.bufferSize = bufferSize;
		this.bufferOne = new ArrayList<V>(bufferSize);
		this.bufferTwo = new ArrayList<V>(bufferSize);
	}
	
	public Exchanger<List<V>> getExchanger() {
		return exchanger;
	}

}
