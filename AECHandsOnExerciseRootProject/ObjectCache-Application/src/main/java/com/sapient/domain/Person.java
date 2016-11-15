package com.sapient.domain;

public class Person {
	private final String id;
	private final String firstName;
	private final String lastName;
	private final String creationTime;
	private volatile String cachedTime;
	
	public Person(String id, String firstName, String lastName, String creationTime){
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.creationTime = creationTime;
	}

	public String getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getCreationTime() {
		return creationTime;
	}
	
	public String getCachedTime() {
		return cachedTime;
	}

	public void setCachedTime(String cachedTime) {
		this.cachedTime = cachedTime;
	}

	public String toString(){
		return id+":"+firstName+" "+lastName+" CreationTime: "+creationTime;
		
	}
}
