package org.owasp.jbrofuzz.db.dto;

/**
 * 
 * DTO for message table
 * @author daemonmidi@gmail.com
 * @since version 2.5
 *
 */
public class MessageDTO{
	private long messageId;
	private long connectionId;
	private String textRequest;
	private String encoding;
	private String payload; 
	private int start;
	private int end; 
	
	public long getMessageId(){
		return this.messageId;
	}
	
	public void setMessageId(long messageId){
		this.messageId = messageId;
	}
	public long getConnectionId(){
		return this.connectionId;
	}
	public void setConnectionId(long connectionId){
		this.connectionId = connectionId;
	}
	public String getTextRequest(){
		return this.textRequest;
	}
	
	public void setTextRequest(String textRequest){
		this.textRequest = textRequest;
	}
	
	public String getEncoding(){
		return this.encoding;
	}
	
	public void setEncoding(String encoding){
		this.encoding = encoding;
	}
	
	public String getPayload(){
		return this.payload;
	}
	
	public void setPayload(String payload){
		this.payload = payload;
	}
	
	public int getStart(){
		return this.start;
	}
	
	public void setStart(int start){
		this.start = start;
	}
	
	public int getEnd(){
		return this.end;
	}
	
	public void setEnd(int end){
		this.end = end;
	}
}