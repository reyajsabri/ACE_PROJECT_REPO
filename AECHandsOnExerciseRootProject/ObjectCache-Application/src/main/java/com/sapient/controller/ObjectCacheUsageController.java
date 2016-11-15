package com.sapient.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sapient.assignment.cache.CacheNotifier;
import com.sapient.assignment.cache.CacheProvider;
import com.sapient.assignment.cache.EvictionReason;
import com.sapient.assignment.cache.ObjectFinder;
import com.sapient.domain.CacheEvictionReporter;
import com.sapient.domain.Person;
import com.sapient.domain.PersonFinder;
import com.sapient.ripository.PersonRepositoryImpl;
import com.sapient.ripository.Repository;

@Controller
public class ObjectCacheUsageController {
	
	private final Repository<Integer, Person> repository = new PersonRepositoryImpl();
	private final BlockingQueue<String> console = new LinkedBlockingQueue<>();
	
	
	private final ObjectFinder<Integer, Person> finder = new PersonFinder<>(console, repository);
	private final CacheNotifier<Future<Person>, EvictionReason> notifier = new CacheEvictionReporter<>(console);
	
	private volatile long timeToLive;
	private volatile int cacheSize;
	private volatile CacheProvider<Integer, Person> provider;
	
	@RequestMapping(value = { "/"}, method = RequestMethod.GET)
	public ModelAndView welcomePage() {
		ModelAndView model = new ModelAndView();
		model.setViewName("static/pages/index");
		return model;
	}

	@RequestMapping(value = "/createCache/{cacheSize}/{timeToLive}", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	public @ResponseBody boolean createCache(@PathVariable String cacheSize, @PathVariable String timeToLive) {
		this.cacheSize = Integer.valueOf(cacheSize);
		this.timeToLive = 1000*Integer.valueOf(timeToLive);
		provider = new CacheProvider<Integer, Person>(finder,this.cacheSize, this.timeToLive, notifier);
		return true;
	}
	
	@RequestMapping(value = "/createPerson/{firstName}/{lastName}", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	public @ResponseBody Person createPerson(@PathVariable String firstName, @PathVariable String lastName) {
		
		Person person = new Person(""+IDGenerator.getNextID(), firstName, lastName, new Date().toString());
		repository.addToDatabase(person);
		return person;
	}
	
	@RequestMapping(value = "/findPersonAndReport", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody Map<String, Object> findPersonAndReport(@RequestBody Integer id) throws InterruptedException, ExecutionException {
		//find object in cache. If not found, find in repository.
		//returns states of cache
		Person person = provider.findObject(id);
		List<Person> cachedList = new ArrayList<>();
		Map<Integer, Future<Person>> cacheReplication = provider.getCachedObjects();
		for(Future<Person> fp: cacheReplication.values()){
			cachedList.add(fp.get());
		}
		Map<String, Object> report = new HashMap<>();
		report.put("repository", ((PersonRepositoryImpl)repository).getAllPerson());
		report.put("cached", cachedList);
		report.put("outputConsole", console);
		return report;
	}
	
	private static final class IDGenerator {
		private static volatile int id = 0;
		public static int getNextID(){
			return ++id;
		}
	}
	
	
}
