package org.owasp.jbrofuzz.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.owasp.jbrofuzz.db.dto.ConnectionDTO;
import org.owasp.jbrofuzz.db.dto.MessageDTO;
import org.owasp.jbrofuzz.db.dto.ResponseDTO;
import org.owasp.jbrofuzz.db.dto.SessionDTO;

public class SQLLiteHandler {
	/**
	 * @author daemonmidi@gmail.com
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @since version 2.5 Setting up the DB for usage.
	 */
	public void setUpDB() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
		Statement stat = conn.createStatement();
		stat.executeUpdate("drop table if exists session;");
		stat.executeUpdate("drop table if exists connection;");
		stat.executeUpdate("drop table if exists message;");
		stat.executeUpdate("drop table if exists response;");

		stat.executeUpdate("create table session (sessionId, timestamp, jVersion, Os);");
		stat.executeUpdate("create table connection (connectionId, sessionId, urlString);");
		stat.executeUpdate("create table message (messageId, connectionId, textRequest, encoding, payload, start, end);");
		stat.executeUpdate("create table response (responseId, connectionId, statusCode, responseHeader, responseBody);");
		conn.close();
	}

	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @return Connection
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
		return conn;
	}
	
	public int store(SessionDTO session, Connection conn) throws SQLException{
		int returnValue = 0;
		try{
		MessageDTO[] message = session.getMessage();
		for (int i = 0; i < message.length; i++){
			returnValue = insertOrUpdateMessageTable(	conn, 
										message[i].getMessageId(), 
										message[i].getConnectionId(),
										message[i].getTextRequest(), 
										message[i].getEncoding(),
										message[i].getPayload(),
										message[i].getStart(), 
										message[i].getEnd()
										);	
		}
		
		ResponseDTO[] response = session.getResponse();
		for (int i = 0; i < response.length; i++){
			returnValue = insertOrUpdateReponseTable(	conn, 
										response[i].getResponseId(), 
										response[i].getConnectionId(), 
										response[i].getStatusCode(), 
										response[i].getResponseHeader(), 
										response[i].getResponseBody()
										);	
		}
		
		returnValue = insertOrUpdateSessionTable(conn, 
									session.getSessionId(),
									session.getTimestamp(), 
									session.getJVersion(), 
									session.getOs()
									);
		
		ConnectionDTO connection = session.getConnectionDTO();
		returnValue = insertOrUpdateConnectionTable(conn, 
										connection.getConnectionId(), 
										connection.getSessionId(), 
										connection.getUrlString()
										);
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
		return returnValue;
	}
	
	
	/**
	 * insert or update the table session - based on the parameter provided.
	 * will return integer as status: >0 OK | <0 failed.
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param conn
	 * @param sessionId
	 * @param timestamp
	 * @param jVersion
	 * @param Os
	 * @return returnValue
	 * @throws SQLException
	 */
	private int insertOrUpdateSessionTable(Connection conn, long sessionId, String timestamp, String jVersion, String Os) throws SQLException{
		int returnValue = 1;
		String sqlString1 = "";
		if (sessionId > 0 ) {
			PreparedStatement st0 = conn
					.prepareStatement("select count (*) from session where sessionId = ?");
			st0.setLong(1, sessionId);
			ResultSet rs0 = st0.executeQuery();
			while (rs0.next()) {
				int count = rs0.getInt(1);
				if (count > 0) {
					// update
					sqlString1 = "update session (timestamp, jVersion, Os) values (?, ?, ?) where sessionId = ?;";
					PreparedStatement st1 = conn.prepareStatement(sqlString1);
					st1.setString(1, timestamp);
					st1.setString(2, jVersion);
					st1.setString(3, Os);
					st1.setLong(4, sessionId);
					returnValue = st1.executeUpdate();
				} else {
					// new row
					sqlString1 = "insert into session (sessionId, timestamp, jVersion, Os) values (?,?,?,?);";
					PreparedStatement st1 = conn.prepareStatement(sqlString1);
					st1.setLong(1, sessionId); 
					st1.setString(2, timestamp);
					st1.setString(3, jVersion);
					st1.setString(4, Os);
					returnValue = st1.executeUpdate();
				}
			}
		}
		return returnValue;
	}

	/**
	 * inserts or update table Connection
	 * return int as result Code: >0 OK | < 0 failed
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param conn
	 * @param connectionId
	 * @param sessionId
	 * @param urlString
	 * @param messageId
	 * @return returnValue
	 * @throws SQLException 
	 */
	private int insertOrUpdateConnectionTable(Connection conn, long connectionId, long sessionId, String urlString) throws SQLException{
		int returnValue = 1;
		String sqlString1 = "";
		if (sessionId > 0) {
			PreparedStatement st0 = conn.prepareStatement("select count(*) from connection where connectionId = ?");
			st0.setLong(1, sessionId);
			ResultSet rs0 = st0.executeQuery();
			while (rs0.next()) {
				int count = rs0.getInt(1);
				if (count > 0) {
					// update					
					sqlString1 = "update connection (sessionId, urlString) values (?, ?) where connectionId = ?;";
					PreparedStatement st1 = conn.prepareStatement(sqlString1);
					st1.setLong(1, sessionId);
					st1.setString(2, urlString);
					st1.setLong(3, connectionId);
					returnValue = st1.executeUpdate();
				} else {
					// new row
					sqlString1 = "insert into connection (connectionId, sessionId, urlString) values (?,?,?);";
					PreparedStatement st1 = conn.prepareStatement(sqlString1);
					st1.setLong(1, connectionId); 
					st1.setLong(2, sessionId);
					st1.setString(3, urlString);
					returnValue = st1.executeUpdate();
				}
			}
		}
		return returnValue;
	}
	
	
	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param conn
	 * @param messageId
	 * @param textRequest
	 * @param encoding
	 * @param payload
	 * @param start
	 * @param end
	 * @return
	 * @throws SQLException 
	 */
	private int insertOrUpdateMessageTable(Connection conn, long messageId, long connectionId, String textRequest, 
											String encoding, String payload, int start, int end) throws SQLException{
		int returnValue = 1;
		String sqlString1 = "";
		if (messageId >= 0) {
			PreparedStatement st0 = conn.prepareStatement("select count(*) from message where messageId = ?");
			st0.setLong(1, messageId);
			ResultSet rs0 = st0.executeQuery();
			while (rs0.next()) {
				int count = rs0.getInt(1);
				if (count > 0) {
					// update
					sqlString1 = "update message (connectionId, textRequest, encoding, payload, start, end) values (?, ?, ?, ?, ?, ?) where messageId = ?;";
					PreparedStatement st1 = conn.prepareStatement(sqlString1);
					st1.setLong(1, connectionId);
					st1.setString(2, textRequest);
					st1.setString(3, encoding);
					st1.setString(4, payload);
					st1.setInt(5, start);
					st1.setInt(6, end);
					st1.setLong(7, messageId);
					returnValue = st1.executeUpdate();
				} else {
					// new row
					sqlString1 = "insert into message (messageId, connectionId, textRequest, encoding, payload, start, end) values (?,?,?,?,?,?,?);";
					PreparedStatement st1 = conn.prepareStatement(sqlString1);
					st1.setLong(1, messageId);
					st1.setLong(2, connectionId);
					st1.setString(3, textRequest);
					st1.setString(4, encoding);
					st1.setString(5, payload);
					st1.setInt(6, start);
					st1.setInt(7,end);
					returnValue = st1.executeUpdate();
				}
			}
		}
		return returnValue;
	}
	
	
	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param conn
	 * @param connectionId
	 * @param statusCode
	 * @param responseHeader
	 * @param responseBody
	 * @return returnValue int > 0 OK | < 0 failed
	 * @throws SQLException 
	 */
	private int insertOrUpdateReponseTable(Connection conn, long responseId, long connectionId, int statusCode, String responseHeader, String responseBody) throws SQLException{
		int returnValue = 1;
		String sqlString1 = "";
		if (responseId >= 0) {
			PreparedStatement st0 = conn.prepareStatement("select count(*) from response where responseId = ?");
			st0.setLong(1, responseId);
			ResultSet rs0 = st0.executeQuery();
			while (rs0.next()) {
				int count = rs0.getInt(1);
				if (count > 0) {
					// update
					sqlString1 = "update response (connectionId, statusCode, responseHeader, responseBody) values (?, ?, ?, ?) where responseId = ?;";
					PreparedStatement st1 = conn.prepareStatement(sqlString1);
					st1.setLong(1, connectionId);
					st1.setInt(2, statusCode);
					st1.setString(3, responseHeader);
					st1.setString(4, responseBody);
					st1.setLong(5, responseId);
					returnValue = st1.executeUpdate();
				} else {
					// new row
					sqlString1 = "insert into response (responseId, connectionId, statusCode, responseHeader, responseBody) values (?,?,?,?,?);";
					PreparedStatement st1 = conn.prepareStatement(sqlString1);
					st1.setLong(1, responseId); 
					st1.setLong(2, connectionId);
					st1.setInt(3, statusCode);
					st1.setString(4, responseHeader);
					st1.setString(5, responseBody);
					returnValue = st1.executeUpdate();
				}
			}
		}
		return returnValue;
	}
	
	
	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @return int; > 0 -> ok ; < 0 -> failed
	 */
	public SessionDTO read(Connection conn, long sessionId) {
		String sql1 = "select count(*) from session where sessionId = ?;";
		SessionDTO session = new SessionDTO();
		try {
			PreparedStatement st1 = conn.prepareStatement("select count(*) from session where sessionId = ?;");
			ResultSet rs1 = st1.executeQuery();
			while (rs1.next()){
				if (rs1.getInt(1) > 1){
					throw new Exception("More than one record found"); 
				}
				else{
					session = readSession(conn, sessionId);
				}
			}
			String sql2 = "Select count(*) from connection where sessionId = ?";
			PreparedStatement st2 = conn.prepareStatement(sql2);
			st2.setLong(1, sessionId);
			ResultSet rs2 = st2.executeQuery();
			while (rs2.next()){
				if (rs2.getInt(1) > 1){
					throw new Exception("More than one record found");
				}
				else{
					session.setConnectionDTO(readConnection(conn, sessionId));
				}
			}
			session.setMessage(readMessage(conn, session.getConnectionDTO().getConnectionId()));
			session.setResponse(readResponse(conn, session.getConnectionDTO().getConnectionId()));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return session;
	}
	
	/**
	 * read session from DB
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param conn
	 * @param sessionId
	 * @return
	 * @throws SQLException
	 */
	private SessionDTO readSession(Connection conn, long sessionId) throws SQLException {
		SessionDTO session = new SessionDTO();
		String sql1 = "select timestamp, jVersion, Os from session where sessionId = ?";
		PreparedStatement st1 = conn.prepareStatement(sql1);
		st1.setLong(1, sessionId);
		ResultSet rs1 = st1.executeQuery();
		while (rs1.next()){
			session.setSessionId(sessionId);
			session.setTimestamp(rs1.getString(1));
			session.setJVersion(rs1.getString(2));
			session.setOs(rs1.getString(3));
		}
		session.setConnectionDTO(readConnection(conn, sessionId));
		return session;
	}
	
	/**
	 * read connection
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param conn
	 * @param sessionId
	 * @return
	 * @throws SQLException
	 */
	private ConnectionDTO readConnection(Connection conn, long sessionId) throws SQLException {
		ConnectionDTO connection = new ConnectionDTO();
		String sql1 = "select connectionId, urlString from connection where sessionId = ?";
		PreparedStatement st1 = conn.prepareStatement(sql1);
		st1.setLong(1, sessionId);
		ResultSet rs1 = st1.executeQuery();
		while (rs1.next()){
			connection.setConnectionId(rs1.getLong(1));
			connection.setSessionid(sessionId);
			connection.setUrlString(rs1.getString(2));
		}
		return connection;
	}
	
	/**
	 * read messages from DB
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param conn
	 * @param connectionId
	 * @return MessageDTO[]
	 * @throws SQLException
	 */
	private MessageDTO[] readMessage(Connection conn, long connectionId) throws SQLException {
		Vector<MessageDTO> messages = new Vector<MessageDTO>();
		String sql1 = "select messageId from message where connectionId = ?";
		PreparedStatement st1 = conn.prepareStatement(sql1);
		st1.setLong(1, connectionId);
		ResultSet rs1 = st1.executeQuery();
		
		while (rs1.next()){
			MessageDTO message = new MessageDTO();
			String sql2 = "Select textRequest, encoding, payload, start, end from message where messageId = ?";
			PreparedStatement st2 = conn.prepareStatement(sql2);
			st2.setLong(1, rs1.getLong(1));
			ResultSet rs2 = st2.executeQuery();
			while (rs2.next()){
				message.setConnectionId(connectionId);
				message.setMessageId(rs1.getLong(1));
				message.setTextRequest(rs2.getString(1));
				message.setEncoding(rs2.getString(2));
				message.setPayload(rs2.getString(3));
				message.setStart(rs2.getInt(4));
				message.setEnd(rs2.getInt(5));
			}
			messages.add(message);
		}
		
		MessageDTO[] retVal = new MessageDTO[messages.size()];
		for (int i = 0; i < messages.size(); i++){
			retVal[i] = messages.get(i);
		}
		
		return retVal;
	}
	
	/**
	 * read Responses from DB
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param conn
	 * @param connectionId
	 * @return ResponseDTO[]
	 * @throws SQLException
	 */
	private ResponseDTO[] readResponse(Connection conn, long connectionId) throws SQLException {
		Vector<ResponseDTO> responses = new Vector<ResponseDTO>();
		String sql1 = "select responseId from response where connectionId = ?";
		PreparedStatement st1 = conn.prepareStatement(sql1);
		st1.setLong(1, connectionId);
		ResultSet rs1 = st1.executeQuery();
		
		while (rs1.next()){
			ResponseDTO response = new ResponseDTO();
			String sql2 = "Select statusCode, responseHeader, responseBody from response where responseId = ?";
			PreparedStatement st2 = conn.prepareStatement(sql2);
			st2.setLong(1, rs1.getLong(1));
			ResultSet rs2 = st2.executeQuery();
			while (rs2.next()){
				response.setConnectionId(connectionId);
				response.setResponseId(rs1.getLong(1));
				response.setStatusCode(rs2.getInt(1));
				response.setResponseHeader(rs2.getString(2));
				response.setResponseBody(rs2.getString(3));
			}
			responses.add(response);
		}
		ResponseDTO[] retVal = new ResponseDTO[responses.size()];
		for (int i = 0; i < responses.size(); i++){
			retVal[i] = responses.get(i);
		}
		return retVal;
	}
	
	public long getLastId(Connection conn, String tableName) throws SQLException{
		long lastId =  -1;
		String sql1 = "select count(*) from ?";
		PreparedStatement pst1 = conn.prepareStatement(sql1);
		pst1.setString(1, tableName);
		ResultSet rs1 = pst1.executeQuery();
		lastId = rs1.getLong(1);
		conn.close();
		return lastId;
	}
}