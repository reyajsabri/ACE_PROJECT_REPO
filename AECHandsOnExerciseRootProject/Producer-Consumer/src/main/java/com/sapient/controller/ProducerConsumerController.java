package com.sapient.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sapient.domain.ProducerConsumer;

/**
 * @author msabri
 *
 */
@Controller
public class ProducerConsumerController {
	
	private static final Logger logger = Logger.getLogger(ProducerConsumerController.class);

	@RequestMapping(value = { "/"}, method = RequestMethod.GET)
	public ModelAndView welcomePage() {
		ModelAndView model = new ModelAndView();
		model.setViewName("static/pages/index");
		return model;
	}

	@RequestMapping(value = "/start", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody List<String> start(@RequestBody final List<String> toBeproduce) {
		final ProducerConsumer producerConsumer = new ProducerConsumer(4);
		//creating producer thread and current thread will behave as consumer
		Thread producer = new Thread("Producer Thread"){
			public void run(){
				producerConsumer.produce(toBeproduce);
			}
		};
		producer.setDaemon(true);
		producer.start();
		producerConsumer.consume();
		List<String> consumedItems = producerConsumer.getConsumedItems();
		return producerConsumer.getConsole();
		
	}
	
	
	
	private static final class IDGenerator {
		private static int id = 0;
		public static int getNextID(){
			return ++id;
		}
	}
}
