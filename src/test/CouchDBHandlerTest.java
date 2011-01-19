package test;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.owasp.jbrofuzz.db.CouchDBHandler;
import org.owasp.jbrofuzz.db.CouchDBMapper;
import org.owasp.jbrofuzz.fuzz.MessageContainer;

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
		MessageContainer session = null;
		MessageContainer connection = null;
		MessageContainer response = null;
		MessageContainer message = null;
		connection = TestUtils.fillConnection(connection, connectionId, sessionId);
		response = TestUtils.fillResponse(response, connectionId);
		message = TestUtils.fillMessage(message, connectionId);
		
		// dbHandler.createDocument(dbName, documentId);
		CouchDBMapper couchMapper = new CouchDBMapper();
		// JSONObject document  = couchMapper.toCouch(session);
		// dbHandler.store(dbName, documentId, document);
	
		String doc = dbHandler.getDocument(dbName, documentId);
		JSONObject readDoc = new JSONObject();
		try{
			readDoc = new JSONObject(doc);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		MessageContainer readSession = null;
		TestUtils.compareSessionDTOs(session, readSession);
	}
		
	@After
	public void cleanUp(){
		dbHandler.removeDB("testdb");
	}	
}