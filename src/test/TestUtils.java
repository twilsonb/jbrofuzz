package test;

import org.junit.Assert;
import org.owasp.jbrofuzz.db.dto.ConnectionDTO;
import org.owasp.jbrofuzz.db.dto.MessageDTO;
import org.owasp.jbrofuzz.db.dto.ResponseDTO;
import org.owasp.jbrofuzz.db.dto.SessionDTO;

public class TestUtils{
	
	
	/**
	 * assert that read == written DTO
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param session1
	 * @param session2
	 */
	public static void compareSessionDTOs(SessionDTO session1, SessionDTO session2){
		Assert.assertEquals(session1.getJVersion(), session2.getJVersion());
		Assert.assertEquals(session1.getOs(), session2.getOs());
		Assert.assertEquals(session1.getSessionId(), session2.getSessionId());
		Assert.assertEquals(session1.getTimestamp(), session2.getTimestamp());
		Assert.assertEquals(session1.getMessage().length, session2.getMessage().length);
		Assert.assertEquals(session1.getResponse().length, session2.getResponse().length);
		for (int i = 0; i < session1.getMessage().length; i++){
			MessageDTO message1 = session1.getMessage()[i];
			MessageDTO message2 = session2.getMessage()[i];
			
			Assert.assertEquals(message1.getConnectionId(), message2.getConnectionId());
			Assert.assertEquals(message1.getEncoding(), message2.getEncoding());
			Assert.assertEquals(message1.getMessageId(), message2.getMessageId());
			Assert.assertEquals(message1.getPayload(), message2.getPayload());
			Assert.assertEquals(message1.getTextRequest(), message2.getTextRequest());
		}
		for (int i = 0; i < session1.getResponse().length; i++){
			ResponseDTO response1 = session1.getResponse()[i];
			ResponseDTO response2 = session2.getResponse()[i];

			Assert.assertEquals(response1.getConnectionId(), response2.getConnectionId());
			Assert.assertEquals(response1.getResponseBody(), response2.getResponseBody());
			Assert.assertEquals(response1.getResponseHeader(), response1.getResponseHeader());
			Assert.assertEquals(response1.getResponseId(), response2.getResponseId());
			Assert.assertEquals(response1.getStatusCode(), response2.getStatusCode());
		}
	}
	
	
	/**
	 * fill ConnectionDTO
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param connection
	 * @param sessionId
	 * @param connectionId
	 * @return ConnectionDTO
	 */
	public static ConnectionDTO fillConnection(ConnectionDTO connection, long connectionId, long sessionId){
		connection.setConnectionId(connectionId);
		connection.setSessionid(sessionId);
		connection.setUrlString("http://www.this.is.my.test.com");
		return connection;
	}
	
	
	/**
	 * fill ResponseDTO with data
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param response
	 * @param connectionId
	 * @return ResponseDTO
	 */
	public static ResponseDTO[] fillResponse(ResponseDTO[] response, long connectionId){
		for (int i = 0; i < 10; i++){
			ResponseDTO resp = new ResponseDTO();
			resp.setConnectionId(connectionId);
			resp.setResponseId(i);
			resp.setStatusCode(200);
			resp.setResponseBody("ResponseBody of Test: " + i + " . ");
			resp.setResponseHeader("ResponseHeader of test: " + i + " . ");
			response[i] = resp;
		}
		return response;
	}
	
	/**
	 * fill MessageDTO with data
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param message
	 * @param connectionId
	 * @return MessageDTO
	 */
	public static MessageDTO[] fillMessage(MessageDTO[] message, long connectionId){
		for (int i = 0; i < 10; i++){
			MessageDTO mesg = new MessageDTO();
			mesg.setConnectionId(connectionId);
			mesg.setEncoding("testEncoding");
			mesg.setEnd(i);
			mesg.setStart(i-1);
			mesg.setMessageId(i);
			mesg.setPayload("TestPayload");
			mesg.setTextRequest("testTextRequest");
			message[i] = mesg;
		}
		
		return message;
	}
}