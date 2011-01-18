package test;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.*;
import org.owasp.jbrofuzz.db.SQLiteHandler;
import org.owasp.jbrofuzz.db.dto.*;

/**
 * 
 * simple SQLite functionality test.
 * @author daemonmidi@gmail.com
 * @since Version 2.5
 *
 */
public class SQLiteHandlerTest{
	private SQLiteHandler dbHandler = null;
	private Connection conn = null;
	private long connectionId = 12;
	private long sessionId = 12;
	private String os = "windoof";
	private String jVersion = "1.6";
	private String timeStamp = "now";

	@Before
	public void setUp(){
		dbHandler = new SQLiteHandler();
		try {
			dbHandler.setUpDB();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void simpleReadWriteTest(){
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

			conn = dbHandler.getConnection("jbrofuzzTestDB");
			// dbHandler.store(session, conn);
		

		SessionDTO sessionRead = new SessionDTO();
		conn = null;
			conn = dbHandler.getConnection("jbrofuzzTestDB");
		sessionRead = dbHandler.read(conn, sessionId);
		
		TestUtils.compareSessionDTOs(session, sessionRead);
	}

	@After
	public void cleanUp(){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}