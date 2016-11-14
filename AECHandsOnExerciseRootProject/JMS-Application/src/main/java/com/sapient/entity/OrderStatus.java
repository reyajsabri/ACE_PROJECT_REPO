package com.sapient.entity;

public enum OrderStatus {
	PROCESSED("Order Processed"),REJECTED("Invalid Order"), EXECUTED("Order Executed");
	private String statusDesc;
	OrderStatus(String status){
		this.statusDesc = status;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
}
