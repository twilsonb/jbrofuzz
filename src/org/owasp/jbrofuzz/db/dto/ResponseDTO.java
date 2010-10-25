package org.owasp.jbrofuzz.db.dto;

/**
 * DTO for response table
 * @author daemonmidi@gmail.com
 * @since version 2.5
 */
public class ResponseDTO{
	private long responseId;
	private long connectionId;
	private int statusCode;
	private String responseHeader;
	private String responseBody;
	
	public long getResponseId(){
		return this.responseId;
	}
	
	public void setResponseId(long responseId){
		this.responseId = responseId;
	}
	
	public long getConnectionId(){
		return this.connectionId;
	}
	
	public void setConnectionId(long connectionId){
		this.connectionId = connectionId;
	}
	public int getStatusCode(){
		return this.statusCode;
	}
	
	public void setStatusCode(int statusCode){
		this.statusCode = statusCode;
	}
	
	public String getResponseHeader(){
		return this.responseHeader;
	}
	
	public void setResponseHeader(String responseHeader){
		this.responseHeader = responseHeader;
	}
	
	public String getResponseBody(){
		return this.responseBody;
	}
	
	public void setResponseBody(String responseBody){
		this.responseBody = responseBody;
	}
}