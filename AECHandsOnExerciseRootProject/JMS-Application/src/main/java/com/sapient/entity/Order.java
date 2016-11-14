package com.sapient.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;



public class Order implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer orderId;
	private OrderStatus status;	
	private String itemId;
	private String itemDescription;
	private String price;
	private String quantity;
	private String errorClass;
	

     public Order()
     {
    	 
     }


     @JsonProperty("orderId")
	public Integer getOrderId() {
		return orderId;
	}


	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	@JsonProperty("status")
	public OrderStatus getStatus() {
		return status;
	}


	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	@JsonProperty("itemId")
	public String getItemId() {
		return itemId;
	}


	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	@JsonProperty("itemDescription")
	public String getItemDescription() {
		return itemDescription;
	}


	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	@JsonProperty("price")
	public String getPrice() {
		return price;
	}


	public void setPrice(String price) {
		this.price = price;
	}

	@JsonProperty("quantity")
	public String getQuantity() {
		return quantity;
	}


	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	@JsonProperty("errorClass")
	public String getErrorClass() {
		return errorClass;
	}


	public void setErrorClass(String errorClass) {
		this.errorClass = errorClass;
	}

	
}
