package org.owasp.jbrofuzz.db.dto;

/**
 * DTO for connection table
 * @author daemonmidi@gmail.com
 * @since version 2.5
 */
public class ConnectionDTO{
	private long connectionId;
	private long sessionId;
	private String urlString;
	
	public long getConnectionId(){
		return this.connectionId;
	}
	
	public void setConnectionId(long connectionId){
		this.connectionId = connectionId;
	}
	
	public long getSessionId(){
		return this.sessionId;
	}
	
	public void setSessionid(long sessionId){
		this.sessionId = sessionId;
	}
	
	public String getUrlString(){
		return this.urlString;
	}
	
	public void setUrlString(String urlString){
		this.urlString = urlString;
	}
}