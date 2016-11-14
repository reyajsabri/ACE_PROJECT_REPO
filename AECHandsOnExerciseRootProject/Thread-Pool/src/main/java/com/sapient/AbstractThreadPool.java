package com.sapient;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author msabri
 *
 * @param <T>
 */
public abstract class AbstractThreadPool<T> implements ThreadPool<T>{

	protected final BlockingQueue<RunnableFuture<T>> taskQueue = new LinkedBlockingDeque<>();
	protected final BlockingQueue<Worker<T>> workerQueue = new LinkedBlockingDeque<>();
	
	protected final int minCore; //minimum number of worker thread
	protected final int maxCore; //Maximum number of worker thread
	
	protected volatile boolean isShutdown; 
	protected WorkerCmmand workerCmmand;
	
	protected volatile boolean isCompleted;
	
	public boolean isCompleted() {
		return isCompleted;
	}

	public AbstractThreadPool(int minCore, int maxCore){
		this.minCore = minCore;
		this.maxCore = maxCore;
		createMinnimumCore();
	}
	
	private void createMinnimumCore(){
		while(workerQueue.size() < (minCore + 1)){
			Worker<T> worker = new Worker<>();
			workerQueue.offer(worker);
		}
	}
	
	public void createMoreCore(int additionalCore){
		if((workerQueue.size()+additionalCore) <= maxCore){
			//request served as asked
			for (int i = 0; i < additionalCore; i++){
				Worker<T> worker = new Worker<>();
				workerQueue.offer(worker);
			}
		}
		else if(workerQueue.size() <= maxCore){
			// request served up to maximum possible size
			for (int j = 0; j < maxCore - workerQueue.size(); j++){
				Worker<T> worker = new Worker<>();
				workerQueue.offer(worker);
			}
		}
		
	}
	
	@Override
	public abstract void shutdown() ;

	@Override
	public abstract boolean isShutdown();

	@Override
	public abstract Future<T> submit(Callable<T> task);

	@Override
	public abstract List<Future<T>> submitAll(Collection<? extends Callable<T>> tasks)
			throws InterruptedException ;
	
	enum WorkerStatus{
		NEW(0), STARTED(1), RUNNING(2), COMPLETED(3);
		private int status = -1;
		public int getStatus() {
			return status;
		}
		WorkerStatus(int st){
			this.status = st;
		}
	}
	
	enum WorkerCmmand{
		SHUTDOWN, HOLT, RESUME, RESTART;
	}
	
	/**
	 * @author msabri
	 *
	 * @param <V>
	 */
	protected final class Worker<V> implements Runnable{
		private WorkerStatus status;
		private String workerName;
		private BlockingDeque<RunnableFuture<V>> taskHolder = new LinkedBlockingDeque<>();
		private Lock lock = new ReentrantLock();
		
		private volatile WorkerCmmand workerCmmand = null;
		
		Thread runner;
		private RunnableFuture<V> task;
		Worker(){
			workerName = "Worker:#"+ IDGenerator.getNextID();
			status = WorkerStatus.NEW;
		}
		
		public RunnableFuture<V> scheduleExecute(RunnableFuture<V> task){
			
			if(status == WorkerStatus.NEW){
				start();
				taskHolder.offer(task);
				return task;
			}
			if(status == WorkerStatus.COMPLETED ){
				taskHolder.offer(task);
				return task;
			}
			return null;
		}
		
		protected void start(){
			lock.lock();
			try{
				if(status == WorkerStatus.NEW){
					runner = new Thread(this, workerName);
					runner.setDaemon(true);
					runner.start();
					status = WorkerStatus.STARTED;
				}
			}finally{
				lock.unlock();
			}
			
		}

		@Override
		public void run() {
			if(status == WorkerStatus.NEW)
				throw new RuntimeException("Can not run before worker is started");
			if(Thread.currentThread() != runner)
				throw new RuntimeException("Worker can not start from out sider");
			
			
			for (;;) {
				
				if(workerCmmand == WorkerCmmand.SHUTDOWN){
					// cleanup
					runner = null;
					task = null;
					taskHolder.clear();
					taskHolder = null;
					break;
				}
				
				try {
					task =  taskHolder.take();// blocked till new task is arrived
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
				if(task !=null){
					status = WorkerStatus.RUNNING; // task running
					try{
						task.run();
					}finally{
						status = WorkerStatus.COMPLETED;
					}
				}
			}
			
		}
		
		public WorkerCmmand getWorkerCmmand() {
			return workerCmmand;
		}

		public void setWorkerCmmand(WorkerCmmand workerCmmand) {
			this.workerCmmand = workerCmmand;
		}

		public WorkerStatus getStatus() {
			return status;
		}

	}
	
	private static final class IDGenerator {
		private static volatile int id = 0;
		public static int getNextID(){
			return ++id;
		}
	}

}
