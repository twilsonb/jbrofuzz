package org.owasp.jbrofuzz.db.dto;

/**
 * DTO for session table
 * @author dameonmidi@gmail.com
 * @since version 2.5
 */
public class SessionDTO{
	private long sessionId;
	private String timestamp;
	private String jVersion;
	private String os;
	private ConnectionDTO connection;
	private MessageDTO[] message;
	private ResponseDTO[] response;
	
	public long getSessionId(){
		return this.sessionId;
	}
	
	public void setSessionId(long sessionId){
		this.sessionId = sessionId;
	}
	
	public String getTimestamp(){
		return timestamp;
	}
	
	public void setTimestamp(String timestamp){
		this.timestamp = timestamp;
	}
	
	public String getJVersion(){
		return this.jVersion;
	}
	
	public void setJVersion(String jVersion){
		this.jVersion = jVersion;
	}
	
	
	public String getOs(){
		return this.os;
	}
	
	public void setOs(String os){
		this.os = os;
	}
	
	public ConnectionDTO getConnectionDTO(){
		return this.connection;
	}
	
	public void setConnectionDTO(ConnectionDTO connection){
		this.connection = connection;
	}
	
	public void setMessage(MessageDTO[] message){
		this.message = message;
	}
	
	public MessageDTO[] getMessage(){
		return this.message;
	}
	
	public void setResponse(ResponseDTO[] response){
		this.response = response;
	}
	
	public ResponseDTO[] getResponse(){
		return this.response;
	}
}