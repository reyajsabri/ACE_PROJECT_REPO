package com.sapient.repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Exchanger;

import org.springframework.stereotype.Component;

import com.sapient.entity.Order;


/**
 * @author msabri
 *
 */
@Component
public class OrderRepositoryImpl implements OrderRepository<OrderFutureStatus, Integer, String> {
	
	private ConcurrentHashMap<Integer, OrderFutureStatus> repo = new ConcurrentHashMap<>();
	private Exchanger<OrderFutureStatus> exchanger = new Exchanger<>();

	public Exchanger<OrderFutureStatus> getExchanger() {
		return exchanger;
	}

	public OrderFutureStatus getOrderStatus(Integer id) {
		return repo.putIfAbsent(id, new OrderFutureStatus());
	}

	public void storeOrder(Order order) {
		OrderFutureStatus future = new OrderFutureStatus();
		future.set(order);
		repo.put(order.getOrderId(), future);

	}

	public Integer getAvailableQuantity(String itemID) {
		// TODO Check in database for availibility
		return 3;
	}

}
