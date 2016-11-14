package com.sapient.download;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import com.sapient.StatusAwareCallable;

/**
 * @author msabri
 *
 */
public class DownLoadTaskImpl implements StatusAwareCallable<TaskDesc> {

	private volatile int completionProgress;
	private final TaskDesc statusDesc;
	private URLConnection con;
	private int contentLength;
	
	private byte[] buffer = new byte[1024];
	private byte[] content;
	public DownLoadTaskImpl(TaskDesc statusDesc){
		this.statusDesc = statusDesc;
	}
	
	@Override
	public TaskDesc call() throws Exception {
		
		try{ 
            con = new URL(statusDesc.getUrl()).openConnection();
            contentLength = con.getContentLength(); 
            String contentType= con.getContentType();
            String contentEncoding = con.getContentEncoding();
            contentLength = contentLength < buffer.length ? buffer.length : contentLength;
            contentLength = contentLength % buffer.length > 0 ? (buffer.length - (contentLength % buffer.length) +contentLength) : contentLength;
        }catch (Exception e){
            e.printStackTrace();
            contentLength = 0; 
        }
		
		content = new byte[contentLength];
		
		BufferedInputStream in = null;
		int currentPos = 0;
	    try {
	        in = new BufferedInputStream(new URL(statusDesc.getUrl()).openStream());
	        
	        while (in.read(buffer, 0, 1024) != -1) {
	        	System.arraycopy(buffer, 0, content, currentPos, buffer.length);
	        	currentPos = currentPos + buffer.length;
	        	completionProgress = currentPos == 0 ? 0 : (currentPos*100)/contentLength;
	        	
	        	System.out.println(Thread.currentThread().getName() + " Completed: "+completionProgress +"%");
	        	
	        	Thread.sleep(100);
	        	
	        }
	        statusDesc.setData(content);
	    } 
	    catch(Exception e){
	    	e.printStackTrace();
	    }finally {
	        if (in != null) {
	            in.close();
	        }
	        
	    }
		
//		while(completionProgress < 100){
//			completionProgress++;
//			System.out.println(Thread.currentThread().getName() + " Completed: "+completionProgress +"%");
//			Thread.sleep(100);
//		}
		return statusDesc;
	}

	@Override
	public int getCompletionStatus() {
		return completionProgress;
	}

	@Override
	public String getName() {
		return statusDesc.toString();
	}
	

}
