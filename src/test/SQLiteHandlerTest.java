package test;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.owasp.jbrofuzz.db.SQLiteHandler;
import org.owasp.jbrofuzz.fuzz.MessageContainer;

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
		MessageContainer session = null;
		MessageContainer connection = null;
		MessageContainer response = null;
		MessageContainer message = null;
		connection = TestUtils.fillConnection(connection, connectionId, sessionId);
		response = TestUtils.fillResponse(response, connectionId);
		message = TestUtils.fillMessage(message, connectionId);

			conn = dbHandler.getConnection("jbrofuzzTestDB");
			// dbHandler.store(session, conn);
		

		MessageContainer sessionRead = null;
		conn = null;
			conn = dbHandler.getConnection("jbrofuzzTestDB");
		// sessionRead = dbHandler.read(conn, sessionId);
		
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