package com.sapient.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author msabri
 *
 */
public class ProducerConsumer extends AbstractProducerConsumer<String> implements Producer<String>, Consumer<String> {
	
	private boolean stopConsumption = false;
	private volatile List<String> tobeProduce;
	private volatile Queue<String> console = new LinkedBlockingDeque<String>();

	private List<String> consumedItems = new LinkedList<String>();

	public ProducerConsumer(int bufferSize){
		super(bufferSize);
	}

	public void consume() {
		List<String> consumeBuffer = getBufferTwo();
		while(!stopConsumption){
			try {
				consumeBuffer = getExchanger().exchange(consumeBuffer);
				console.add("Consumed: "+consumeBuffer.toString());
				consumedItems.addAll(consumeBuffer);
				consumeBuffer.clear();
				if(consumedItems.size() == tobeProduce.size())
					stopConsumption = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void produce(List<String> items) {
		tobeProduce = items;
		List<String> producerBuffer = getBufferOne();
		for(String item : items){
			if(producerBuffer.size()< bufferSize){
				producerBuffer.add(item);
			}else{
				try {
					// buffer full, so exchange
					console.add("Producing: "+producerBuffer.toString());
					producerBuffer = getExchanger().exchange(producerBuffer);
					producerBuffer.clear();
					producerBuffer.add(item);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if(producerBuffer.size()>0){
			// incase end item reaches but buffer still not full
			
			try {
				console.add("Producing: "+producerBuffer.toString());
				producerBuffer = getExchanger().exchange(producerBuffer);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			producerBuffer.clear();
		}
		
	}

	public List<String> getConsole() {
		return new ArrayList<String>(console);
	}

	public List<String> getConsumedItems() {
		return consumedItems;
	}
}
