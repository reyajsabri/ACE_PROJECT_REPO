package com.sapient.repository;

import java.util.concurrent.Exchanger;


/**
 * @author msabri
 *
 */
public interface OrderRepository<O,I,S> {
	O getOrderStatus(I i);
	I getAvailableQuantity(S s);
	Exchanger<O> getExchanger();
}
