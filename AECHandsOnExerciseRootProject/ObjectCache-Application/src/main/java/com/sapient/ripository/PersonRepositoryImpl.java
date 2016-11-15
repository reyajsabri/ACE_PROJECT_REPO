package com.sapient.ripository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sapient.domain.Person;

public class PersonRepositoryImpl implements Repository<Integer, Person> {
	
	private final Map<Integer, Person> repo = new ConcurrentHashMap<>();

	@Override
	public void addToDatabase(Person v) {
		repo.put(Integer.valueOf(v.getId()), v);
		
	}

	@Override
	public Person getFromDatabase(Integer a) {
		return repo.get(a);
	}
	
	public List<Person> getAllPerson(){
		return new ArrayList<Person>(repo.values());
	}

}
