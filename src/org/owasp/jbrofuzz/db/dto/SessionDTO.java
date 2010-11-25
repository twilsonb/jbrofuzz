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
	
	public void showForDebug(){
		System.out.println("-------------------------------------------------------");
		System.out.println("JVersion: " + this.jVersion);
		System.out.println("OS: " + this.os);
		System.out.println("SessionId: " + this.sessionId);
		System.out.println("TimeStamp: " + this.timestamp);
		System.out.println("-------------------------------------------------------");
		ConnectionDTO connection = this.getConnectionDTO();
		System.out.println("ConnectionId: " + connection.getConnectionId());
		System.out.println("SessionId: " + connection.getSessionId());
		System.out.println("URL: " + connection.getUrlString());
		System.out.println("-------------------------------------------------------");
		ResponseDTO[] responses = this.getResponse();
		for (int i = 0; i < responses.length; i++){
			ResponseDTO response = this.getResponse()[i];
			System.out.println("ConncetionId: " + response.getConnectionId());
			System.out.println("ResponseBody: " + response.getResponseBody());
			System.out.println("ResponseHeader: " + response.getResponseHeader());
			System.out.println("ResponseId: " + response.getResponseId());
			System.out.println("StatusCode: " + response.getStatusCode());
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		}
		System.out.println("-------------------------------------------------------");
		MessageDTO[] messages = this.getMessage();
		for (int i = 0; i < messages.length; i++){
			MessageDTO message = this.getMessage()[i];
			System.out.println("ConnectionId: " + message.getConnectionId());
			System.out.println("Encodig: " + message.getEncoding());
			System.out.println("End: " + message.getEnd());
			System.out.println("Start: " + message.getStart());
			System.out.println("MessageId: " +  message.getMessageId());
			System.out.println("Payload: " + message.getPayload());
			System.out.println("Textrequest: " + message.getTextRequest());
			System.out.println("-------------------------------------------------------");
		}
		System.out.println("-------------------------------------------------------");
	}
}