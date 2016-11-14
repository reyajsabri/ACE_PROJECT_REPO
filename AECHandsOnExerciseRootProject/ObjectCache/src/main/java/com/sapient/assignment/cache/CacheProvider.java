package com.sapient.assignment.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * This class represent a Cache implementation using concurrent hash map. It uses "ExpirableTask" in order to initiate 
 * remote search as well as to determine expiration of already cached object. Expiration period is defined in the ExpirableTask.
 * 
 * This class performs following
 * 
 * Wrap the cache over slow-remote-calls
 * Check if key exists in cache , if Yes return cached value
 * If No, fetch data from external system.
 * Put data in cache and return value.
 * 
 * For each miss, it initiate external search through future task to get the object. Hence it enables concurrent search 
 * for multiple objects at the same time.
 * 
 * To initiate external search, this class relay over ObjectFinder which should be provided in its construction time.
 * Using "ObjectFinder", it decouple search strategy from the external class [that must implement same contract]
 * 
 *  This class maintains Cache as follows:
 *  
 *  If a HIT occur, it checks whether object has expired [object older than "timeToLive"]. If expired, it removes object
 *  [ of course its wrapper future task] and then re-initiate remote search. Otherwise return the cached object.
 *  
 *  If any point of time during "timeToLive" an object is accessed, its expiration time again renewed by another "timeToLive".
 *  
 *  LIMITATION:: Maintaining size of cached object is yet to be implemented.
 * 
 * @author msabri
 *
 * @param <A>
 * @param <V>
 */
public class CacheProvider<A, V> implements ObjectFinder<A, V> {

	private final LRUConcurrentHashMap<A, Future<V>> cache;
	private final ObjectFinder<A, V> finder;
	
	private final long timeToLive;
	private final CacheEvictionNotifier<Future<V>, EvictionReason> notifier;

	public CacheProvider(ObjectFinder<A, V> finder, int cacheSize, long timeToLive, CacheEvictionNotifier<Future<V>, EvictionReason> notifier) {
		this.finder = finder;
		this.timeToLive = timeToLive;
		this.notifier = notifier;
		cache = new LRUConcurrentHashMap<A, Future<V>>(cacheSize, notifier);
	}

	public V findObject(final A arg) throws InterruptedException {
		while (true) {
			Future<V> f = cache.get(arg);
			if (f == null) {
				// for all MISS happen here
				Callable<V> eval = new Callable<V>() {
					public V call() throws InterruptedException {
						return finder.findObject(arg);
					}
				};
				ExpirableTask<V> et = new ExpirableTask<V>(eval, timeToLive);
				
				// in race condition if a thread already triggered a remote search when another thread
				// searching for same object, should wait for the first thread to complete in order to 
				// get desire object. Hence it avoid re-triggering of remote search.
				f = cache.putIfAbsent(arg, et);
				if (f == null) {
					f = et;
					et.run();
				}
				
			}
			else if(((ExpirableTask<V>)f).isResultExpired()) {
				// If there is an HIT but, the object [enclosing task] is expired then remove the expired task from cache
				// and re-initiate remote search for this particular object.
				cache.remove(arg);
				notifier.nitifyUser(f, EvictionReason.TIME_EXPIRED);
				// re-initiate remote search
				continue;
			}
			try {
				// All HIT come here
				return f.get();
			} catch (CancellationException e) {
				cache.remove(arg, f);
			} catch (ExecutionException e) {
				//throw launderThrowable(e.getCause());
			}
		}
	}
	
	public String traceObjectGraph() {
		return cache.toString();
	}

	@Override
	public void shutdown() {
		finder.shutdown();
		
	}

}
