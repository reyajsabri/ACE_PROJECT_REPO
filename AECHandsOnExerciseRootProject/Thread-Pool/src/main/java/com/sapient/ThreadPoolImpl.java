package com.sapient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author msabri
 * @param <T>
 *
 */
public class ThreadPoolImpl<T> extends AbstractThreadPool<T>{
	protected final BlockingQueue<Worker<T>> idleWorkerSnapshot = new LinkedBlockingDeque<>();
	protected final Lock lock = new ReentrantLock();
	protected final int defaultWorkerCreationSize = minCore;
	
	private volatile boolean isStarted = false;
	private Thread taskScheduler = null;

	public ThreadPoolImpl(int minCore, int maxCore) {
		super(minCore, maxCore);
	}

	public void shutdown() {
		workerCmmand = WorkerCmmand.SHUTDOWN;
		
	}

	@Override
	public boolean isShutdown() {
		// TODO Auto-generated method stub
		return workerCmmand == WorkerCmmand.SHUTDOWN;
	}
	
	@Override
	public Future<T> submit(Callable<T> task) {
		if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = new StatusAwareFutureTask<Callable<T>, T>(task);
        taskQueue.add(ftask);
        startScheduling();
        return ftask;
	}

	@Override
	public List<Future<T>> submitAll(Collection<? extends Callable<T>> tasks)
			throws InterruptedException {
		List<Future<T>> submittedTasks = new ArrayList<>();
		for(Callable<T> task : tasks){
			if (task == null) throw new NullPointerException();
			RunnableFuture<T> ftask = new StatusAwareFutureTask<Callable<T>, T>(task);
	        submittedTasks.add(ftask);
	        taskQueue.add(ftask);
	        startScheduling();
		}
		return submittedTasks;
	}
	
	private void idleWorkerSnapshot(){
		lock.lock();
		try{
			idleWorkerSnapshot.clear();
			for(Worker<T> worker : workerQueue){
				if(worker.getStatus() == WorkerStatus.NEW 
						|| worker.getStatus() == WorkerStatus.COMPLETED){
					idleWorkerSnapshot.offer(worker);
				}
					
			}
		}finally{
			lock.unlock();
		}
	}
	
	private void startScheduling(){
		if(!isStarted){
			taskScheduler = new Thread(new Collector());
			taskScheduler.setDaemon(true);
			taskScheduler.start();
		}
	}
	
	
	private class Collector implements Runnable{
		// Collects idle workers and submitted task and schedule them for execution
		@Override
		public void run() {
			for(;;){
				if(workerCmmand == WorkerCmmand.SHUTDOWN){
					break;
				}
				RunnableFuture<T> task = null;
				Worker<T> worker = null;
				if(!taskQueue.isEmpty()){
					
					if((!idleWorkerSnapshot.isEmpty()) || (!scanAndCheckEmpty())){
						worker = idleWorkerSnapshot.poll();
					}else {
						// no idle worker try to create additional worker 
						createMoreCore(defaultWorkerCreationSize);
						if(!scanAndCheckEmpty()){
							worker = idleWorkerSnapshot.poll();
						}
						else {
							// maxCore number of worker busy. so continue to discover available worker
							continue;
							//throw new RuntimeException("Task can not be schedule any way");
						}
						// worker available, now poll a task
						
					}
					// Idle worker must be set at this point
					task =  taskQueue.poll();
					if(!(worker == null || task == null)){
						worker.scheduleExecute(task);
					}
				}else{
					// No Task remaining here. So check if all worker completed 
					if(checkIfAllWorkersDone()){
						isCompleted = true;
					}
				}
				
			}
			
		}
		
		private boolean checkIfAllWorkersDone(){
			lock.lock();
			boolean isAllDone = true;
			try{
				
				//idleWorkerSnapshot.clear();
				for(Worker<T> worker : workerQueue){
					if(worker.getStatus() == WorkerStatus.RUNNING){
						isAllDone = false;
					}
						
				}
			}finally{
				lock.unlock();
			}
			return isAllDone;
		}
		
		private boolean scanAndCheckEmpty(){
			idleWorkerSnapshot();
			return idleWorkerSnapshot.isEmpty();
		}
		
	}

}
