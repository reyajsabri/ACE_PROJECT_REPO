package com.sapient.download;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;

import com.sapient.StatusAwareCallable;

/**
 * @author msabri
 *
 */
public class DownLoadTaskImpl implements StatusAwareCallable<TaskDesc> {

	private volatile int completionProgress;
	private final TaskDesc statusDesc;
	private final String downloadDir;
	private URLConnection con;
	private int contentLength;
	
	private byte[] buffer = new byte[1024];
	private byte[] content;
	public DownLoadTaskImpl(String downloadDir, TaskDesc statusDesc){
		this.statusDesc = statusDesc;
		this.downloadDir = downloadDir;
	}
	
	@Override
	public TaskDesc call() throws Exception {
		
		try{ 
            con = new URL(statusDesc.getUrl()).openConnection();
            contentLength = con.getContentLength(); 
            String contentType= con.getContentType();
            String contentEncoding = con.getContentEncoding();
            //contentLength = contentLength < buffer.length ? buffer.length : contentLength;
            //contentLength = contentLength % buffer.length > 0 ? (buffer.length - (contentLength % buffer.length) +contentLength) : contentLength;
        }catch (Exception e){
            e.printStackTrace();
            contentLength = 0; 
        }
		
		content = new byte[con.getContentLength()];
		byte[] secondContent = null;
		
		BufferedInputStream in = null;
		ByteArrayOutputStream out = null;
		OutputStream output = null;
		
		int currentPos = 0;
		int byteRead = 0;
	    try {
	        in = new BufferedInputStream(new URL(statusDesc.getUrl()).openStream());
	        out = new ByteArrayOutputStream(contentLength);
	        
	        while ((byteRead = in.read(buffer)) != -1) { 
	        	
	        	System.arraycopy(buffer, 0, content, currentPos, byteRead);
	        	out.write(buffer, 0,byteRead);
	        	currentPos = currentPos + byteRead;
	        	BigInteger tempPose = BigInteger.valueOf(currentPos);
	        	
	        	completionProgress = currentPos == 0 ? 0 : tempPose.multiply(BigInteger.valueOf(100)).divide(BigInteger.valueOf(contentLength)).intValue();
	        	
	        	System.out.println(Thread.currentThread().getName() + " Completed: "+completionProgress +"%");
	        	
	        	Thread.sleep(5);
	        	
	        }
	        String[] urlArr = statusDesc.getUrl().split("/");
	        output = new FileOutputStream(downloadDir + "/"+urlArr[urlArr.length-1]);
	        
	        
	        
	        secondContent = out.toByteArray();
	        
	        output.write(secondContent);
	        output.flush();
	        statusDesc.setData(secondContent);
	    } 
	    catch(Exception e){
	    	e.printStackTrace();
	    }finally {
	        if (in != null) {
	            in.close();
	        }
	        if(out != null){
	        	out.close();
	        }
	        if(output != null){
	        	output.close();
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
