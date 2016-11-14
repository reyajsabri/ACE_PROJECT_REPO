package com.sapient.assignment.cache;

public enum EvictionReason {

	SIZE_OVERFLOW ("Size Overflow"),
	TIME_EXPIRED ("Time Expired");
	
	private final String reason;
	
	EvictionReason(String reason) {
		this.reason = reason;
	}
	
	public String getReason() {
		return reason;
	}
}
