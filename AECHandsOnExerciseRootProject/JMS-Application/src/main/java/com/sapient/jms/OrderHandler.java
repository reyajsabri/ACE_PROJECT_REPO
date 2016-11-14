package com.sapient.jms;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.sapient.entity.Order;
import com.sapient.entity.OrderStatus;
import com.sapient.repository.OrderFutureStatus;
import com.sapient.repository.OrderRepository;

/**
 * @author msabri
 * 
 */
@Component
public class OrderHandler {

	private QueueConnection con;
	private QueueSession ses;
	private Queue executionQueue;
	private QueueSender executionSender;
	
	@Autowired
	private OrderRepository<OrderFutureStatus, Integer, String> repository;
	
	OrderHandler() throws JMSException{
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"tcp://localhost:61616");
		con = connectionFactory.createQueueConnection();
		con.start();
		ses = con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		executionQueue =ses.createQueue("executedOrderQueue");  
		executionSender=ses.createSender(executionQueue);  
	}

	@JmsListener(destination = "orderQueue")
	public void receiveOrder(ObjectMessage message) throws JMSException {
		Order order = (Order) message.getObject();
		if(validateOrder(order)){
			order.setStatus(OrderStatus.PROCESSED);
			
			executionSender.send(message);
			
		}else {
			
			
			order.setStatus(OrderStatus.REJECTED);
			// any thread waiting on future associated orderId will be released
			OrderFutureStatus furture = repository.getOrderStatus(order.getOrderId());
			furture.set(order);
		}
	}

	@JmsListener(destination = "executedOrderQueue")
	public void executeOrder(ObjectMessage message) throws JMSException, InterruptedException {
		Order order = (Order) message.getObject();
		order.setStatus(OrderStatus.EXECUTED);
		Thread.sleep(10000);
		OrderFutureStatus future = repository.getOrderStatus(order.getOrderId());
		future.set(order);
	}
	
	private boolean validateOrder(Order order){
		Integer price;
		Integer quantity;
		try{
			price = Integer.valueOf(order.getPrice());
			quantity = Integer.valueOf(order.getQuantity());
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
		if(price > 0 && quantity < repository.getAvailableQuantity(order.getItemId())){
			return true;
		}
		return false;
	}

}