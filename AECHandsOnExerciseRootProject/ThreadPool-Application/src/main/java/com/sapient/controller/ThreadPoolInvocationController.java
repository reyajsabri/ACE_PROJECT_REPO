package com.sapient.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sapient.StatusAwareCallable;
import com.sapient.StatusAwareFutureTask;
import com.sapient.ThreadPoolImpl;
import com.sapient.download.DownLoadTaskImpl;
import com.sapient.download.TaskDesc;


/**
 * @author msabri
 *
 */
@Controller
public class ThreadPoolInvocationController {
	
	private static final Logger logger = Logger.getLogger(ThreadPoolInvocationController.class);
	
	private final ThreadPoolImpl<TaskDesc> pool = new ThreadPoolImpl<>(3, 8);
	private final ConcurrentHashMap<Integer, List<Future<TaskDesc>>> resultMap = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Integer, List<DownLoadTaskImpl>> taskToSchedule = new ConcurrentHashMap<>();
	

	@RequestMapping(value = { "/"}, method = RequestMethod.GET)
	public ModelAndView welcomePage() {
		ModelAndView model = new ModelAndView();
		model.setViewName("static/pages/index");
		return model;
	}

	@RequestMapping(value = "/prepareDownloadTask", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody Map<Integer, List<TaskDesc>> prepareDownloadTask(@RequestBody final List<String> downloadURLs) {
		
		List<TaskDesc> taskList = new ArrayList<>();
		List<DownLoadTaskImpl> downloadList = new ArrayList<>();
		String key = "";
		for(String url : downloadURLs){
			int id = IDGenerator.getNextID();
			key = key+id;
			TaskDesc task = new TaskDesc();
			task.setId(""+id);
			task.setUrl(url);
			taskList.add(task);
			DownLoadTaskImpl download = new DownLoadTaskImpl(task);
			downloadList.add(download);
		}
		//FIXME separate class for key need to be implemented
		int mapedId = IDGenerator.getNextID();
		taskToSchedule.put(mapedId, downloadList);
		Map<Integer, List<TaskDesc>> returnedMap = new HashMap<>();
		returnedMap.put(mapedId,taskList);
		return returnedMap;
	}
	
	@RequestMapping(value = "/startDownload/{key}", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	public @ResponseBody boolean startDownload(@PathVariable Integer key) {
		
		List<DownLoadTaskImpl> downloadList = taskToSchedule.get(key);
		
		try {
			List<Future<TaskDesc>> futures = pool.submitAll(downloadList);
			resultMap.put(key, futures);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getTasksProgress/{key}", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	public @ResponseBody List<TaskDesc> getTasksProgress(@PathVariable Integer key) {
		List<TaskDesc> taskDescList = new ArrayList<>();
		List<Future<TaskDesc>> futures = resultMap.get(key);
		StatusAwareFutureTask<StatusAwareCallable<TaskDesc>, TaskDesc> ststusAwareFutureTask = null;
		for(Future<TaskDesc> future : futures){
			ststusAwareFutureTask = (StatusAwareFutureTask<StatusAwareCallable<TaskDesc>, TaskDesc>)future ;
			String urlDesc = ststusAwareFutureTask.getName();
			String idUrl = urlDesc.split(":")[0];
			TaskDesc tdesc = new TaskDesc();
			tdesc.setId(idUrl);
			
			tdesc.setProgress(ststusAwareFutureTask.getCompletionProgress()+"%");
			tdesc.setUrl(urlDesc.substring(idUrl.length()+1));
			taskDescList.add(tdesc);
		}
//		if(pool.isCompleted())
//			pool.shutdown();
		return taskDescList;
	}
	
	@RequestMapping(value = "/isTasksCompleted", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	public @ResponseBody boolean isTasksCompleted() {
		return pool.isCompleted();
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getDownloadData/{key}/{taskId}", method = RequestMethod.GET, consumes = "application/json", produces = "application/octet-stream")
	public @ResponseBody byte[] getDownloadData(@PathVariable Integer key, @PathVariable Integer taskId) {
		
		List<Future<TaskDesc>> futures = resultMap.get(key);
		StatusAwareFutureTask<StatusAwareCallable<TaskDesc>, TaskDesc> ststusAwareFutureTask = null;
		//Matched Future
		
		for(Future<TaskDesc> future : futures){
			ststusAwareFutureTask = (StatusAwareFutureTask<StatusAwareCallable<TaskDesc>, TaskDesc>)future ;
			String[] idUrl = ststusAwareFutureTask.getName().split(":");
			if(idUrl[0].equals(""+taskId)){
				break;
			}
		}
		TaskDesc resultTask;
		try {
			resultTask = ststusAwareFutureTask.get();
			return resultTask.getData();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private static final class IDGenerator {
		private static int id = 0;
		public static int getNextID(){
			return ++id;
		}
	}
}
