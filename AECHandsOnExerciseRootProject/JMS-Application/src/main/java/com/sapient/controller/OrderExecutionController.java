package com.sapient.controller;

import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sapient.entity.Order;
import com.sapient.jms.OrderSender;
import com.sapient.repository.OrderFutureStatus;
import com.sapient.repository.OrderRepository;

/**
 * @author msabri
 *
 */
@Controller
public class OrderExecutionController {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(OrderExecutionController.class);

	@Autowired
	OrderSender<Order> orderSender;
	@Autowired
	private OrderRepository<OrderFutureStatus, Integer, String> repository;

	@RequestMapping(value = { "/"}, method = RequestMethod.GET)
	public ModelAndView welcomePage() {
		ModelAndView model = new ModelAndView();
		model.setViewName("static/pages/index");
		return model;
	}

	@RequestMapping(value = "/order", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody Integer placeOrder(@RequestBody Order order) {
		int genOrderId = IDGenerator.getNextID();
		order.setOrderId(genOrderId);
		
		if(repository.getOrderStatus(order.getOrderId())== null){
			// making sure OrderStatus initialized [by putIfabsent()] and here we get the value 
			
		}
		@SuppressWarnings("unused")
		OrderFutureStatus future = repository.getOrderStatus(order.getOrderId());
		orderSender.sendOrder(order);
		
		return order.getOrderId();
		
	}
	
	@RequestMapping(value = "/getOrderStatus/{orderId}", method = RequestMethod.GET, consumes = "text/html", produces = "application/json")
	public @ResponseBody Order getOrderStatus(@PathVariable Integer orderId) throws InterruptedException, ExecutionException {
		return repository.getOrderStatus(orderId).get();
	}
	
	private static final class IDGenerator {
		private static volatile int id = 0;
		public static int getNextID(){
			return ++id;
		}
	}
}
