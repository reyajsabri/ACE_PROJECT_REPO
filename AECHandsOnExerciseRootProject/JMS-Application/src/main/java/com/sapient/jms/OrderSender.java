package com.sapient.jms;




/**
 * @author msabri
 *
 */
public interface OrderSender<T> {

	public void sendOrder(T t);

	public void executeOrder(T t);
}
