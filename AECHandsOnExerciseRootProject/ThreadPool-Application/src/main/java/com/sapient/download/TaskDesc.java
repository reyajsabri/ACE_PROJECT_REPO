package com.sapient.download;

/**
 * @author msabri
 *
 */
public class TaskDesc {
	private String id;
	private String url;
	private byte[] data;

	private volatile String progress = "0%";
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		
		return id+":"+url;
	}
}