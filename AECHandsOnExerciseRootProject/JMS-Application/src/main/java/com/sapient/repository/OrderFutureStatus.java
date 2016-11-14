package com.sapient.repository;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.sapient.entity.Order;

public class OrderFutureStatus implements Future<Order> {

	private  volatile Order order;
	
	private ReentrantLock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	
	
	public boolean cancel(boolean mayInterruptIfRunning) {
		 throw new RuntimeException("This operation not supported");
	}

	public boolean isCancelled() {
		throw new RuntimeException("This operation not supported");
	}

	public boolean isDone() {
		return order != null;
	}

	public Order get() throws InterruptedException, ExecutionException {
		lock.lock();
		try{
		if(order == null)
			condition.await();
		}finally{
			lock.unlock();
		}
		return order;
	}

	public Order get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		lock.lock();
		try{
		if(order == null)
			condition.await(timeout, unit);
		}finally{
			lock.unlock();
		}
		return order;
	}
	
	public void set(Order order){
		lock.lock();
		try{
			if(this.order == null)
				this.order = order;
			condition.signalAll();
		}finally{
			lock.unlock();
		}
	}

}
