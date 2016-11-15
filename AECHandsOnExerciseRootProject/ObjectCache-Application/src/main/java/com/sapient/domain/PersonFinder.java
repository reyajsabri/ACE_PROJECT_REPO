package com.sapient.domain;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

import com.sapient.assignment.cache.ObjectFinder;
import com.sapient.ripository.Repository;

public class PersonFinder<L extends BlockingQueue<String>, R extends Repository<Integer, Person>> implements ObjectFinder<Integer, Person> {
 
	private final L console;
	private final R repo;
	public PersonFinder(L l, R r){
		this.console = l;
		this.repo = r;
	}
	
	@Override
	public Person findObject(Integer a) throws InterruptedException {
		System.out.println("Cache Miss occured for ID: "+a+". So, fetching from Repository");
		console.add("Cache Miss occured for ID: "+a+". So fetching from Repository");
		Person p = repo.getFromDatabase(a);
		p.setCachedTime(new Date().toString());
		return p;
	}

	@Override
	public void shutdown() {
		// Destroy database connections
		
	}

	public L getConsole() {
		return console;
	}

}
