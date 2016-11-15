package com.sapient.assignment.cache;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author msabri
 *
 * @param <K>
 * @param <V>
 */
public class LRUConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private LinkedList<K> lruKeyList;
	
	private int maxSize;
	
	private final CacheNotifier<V, EvictionReason> notifier;
	
	private Lock lock;
	
	public LRUConcurrentHashMap(int maxSize, CacheNotifier<V, EvictionReason> notifier) {
		this.maxSize = maxSize;
		this.notifier = notifier;
		lruKeyList = new LinkedList<K>();
		lock = new ReentrantLock();
	}
	
	public V put(K key, V value) {
		lruKeyList.add(key);
		if(lruKeyList.size() > maxSize){
			lock.lock();
			
				K k = lruKeyList.removeFirst();
				remove(k);
				notifier.notifyEvictionToUser(value, EvictionReason.SIZE_OVERFLOW);
			lock.unlock();
		}
		return super.put(key, value);
	}
	
	public V putIfAbsent(K key, V value) {
		
		lruKeyList.add(key);
		if(lruKeyList.size() > maxSize){
			lock.lock();
				K k = lruKeyList.removeFirst();
				remove(k);
				notifier.notifyEvictionToUser(value, EvictionReason.SIZE_OVERFLOW);
			lock.unlock();
		}
		return super.putIfAbsent(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public V get(Object key) {
		V v = super.get(key);
		if(v != null) {
			// add most recent used key to last
			lock.lock();
				lruKeyList.remove(key);
				lruKeyList.add((K) key);
			lock.unlock();
			
		}
		return v;
	}

}
