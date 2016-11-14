package com.sapient.jms;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Service;

import com.sapient.entity.Order;

@Service
public class OrderMessageSenderImpl implements OrderSender<Order> {
	private QueueSession ses;
	private Queue queue;
	private QueueSender sender;
	@SuppressWarnings("static-access")
	public OrderMessageSenderImpl() throws JMSException {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        QueueConnection con= connectionFactory.createQueueConnection();  
        con.start(); 
        ses=con.createQueueSession(false, ses.AUTO_ACKNOWLEDGE);  
        //3) get the Queue object  
        
        queue =ses.createQueue("orderQueue");  
        //4)create QueueSender object         
        sender=ses.createSender(queue);
        
	}

	public void sendOrder(Order order) {
		ObjectMessage objMessage;
		try {
			objMessage = ses.createObjectMessage();
			objMessage.setObject(order);
			sender.send(objMessage);
			
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
		

	}

	public void executeOrder(Order order) {
		// Do nothing
		

	}


}
