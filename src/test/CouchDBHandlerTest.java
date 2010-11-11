package test;

import org.json.JSONObject;
import org.junit.*;
import org.owasp.jbrofuzz.db.CouchDBHandler;
import org.owasp.jbrofuzz.db.CouchDBMapper;
import org.owasp.jbrofuzz.db.dto.*;

/**
 * 
 * cecking basic functionality of your couchdb connection
 * @author daemonmidi@gmail.com
 * @since Version 2.5
 * 
 *
 */
public class CouchDBHandlerTest{
	private CouchDBHandler dbHandler = new CouchDBHandler();
	private String protocol = "http";
	private String host = "192.168.56.102";
	private int port = 5984;
	private long connectionId = 12;
	private long sessionId = 12;
	private String os = "windoof";
	private String jVersion = "1.6";
	private String timeStamp = "now";
	private String dbName = "testdb";
	private String documentId = "test1";
	
	
	@Before
	public void setUp(){
		dbHandler = new CouchDBHandler();
		dbHandler.setProtocol(protocol);
		dbHandler.setHost(host);
		dbHandler.setPort(port);
		dbHandler.createDB(dbName);
	}
	
	
	@Test
	public void simpleReadWriteTst() {
		SessionDTO session = new SessionDTO();
		ConnectionDTO connection = new ConnectionDTO();
		ResponseDTO[] response = new ResponseDTO[10];
		MessageDTO[] message = new MessageDTO[10];
		connection = TestUtils.fillConnection(connection, connectionId, sessionId);
		response = TestUtils.fillResponse(response, connectionId);
		message = TestUtils.fillMessage(message, connectionId);
		session.setConnectionDTO(connection);
		session.setResponse(response);
		session.setMessage(message);
		session.setJVersion(jVersion);
		session.setOs(os);
		session.setTimestamp(timeStamp);
		session.setSessionId(sessionId);
		
		// dbHandler.createDocument(dbName, documentId);
		CouchDBMapper couchMapper = new CouchDBMapper();
		JSONObject document  = couchMapper.toCouch(session);
		dbHandler.createOrUpdateDocument(dbName, documentId, document);
	
		String doc = dbHandler.getDocument(dbName, documentId);
		JSONObject readDoc = new JSONObject();
		try{
			readDoc = new JSONObject(doc);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		SessionDTO readSession = couchMapper.toDTO(readDoc);
		TestUtils.compareSessionDTOs(session, readSession);
	}
		
	@After
	public void cleanUp(){
		dbHandler.removeDB("testdb");
	}	
}