package test;

import org.owasp.jbrofuzz.fuzz.MessageContainer;

public class TestUtils{
	
	
	/**
	 * assert that read == written DTO
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param session1
	 * @param session2
	 */
	public static void compareSessionDTOs(MessageContainer session1, MessageContainer session2){

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
	public static MessageContainer fillConnection(MessageContainer connection, long connectionId, long sessionId){
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
	public static MessageContainer fillResponse(MessageContainer response, long connectionId){
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
	public static MessageContainer fillMessage(MessageContainer  message, long connectionId){
		return message;
	}
}