package org.owasp.jbrofuzz.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.fuzz.MessageContainer;
import org.owasp.jbrofuzz.fuzz.ui.FuzzingPanel;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

public class SQLiteHandler {
	private static final SimpleDateFormat SD_FORMAT = new SimpleDateFormat(
			"zzz-yyyy-MM-dd-HH-mm-ss-SSS", Locale.ENGLISH);

	/**
	 * @author daemonmidi@gmail.com
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @since version 2.5 Setting up the DB for usage.
	 */

	public String setUpDB() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		String dbName = "";
		dbName = JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[12].getId(), "");
		Logger.log("Setting up Database: " + dbName, 0);
		if (dbName.length() <= 0 || dbName.equals("")) {
			Date dat = new Date();
			dbName = String.valueOf(dat.getTime());
		}
		String connectionString = "jdbc:sqlite:" + dbName + ".db";
		Connection conn = DriverManager.getConnection(connectionString);
		conn.setAutoCommit(false);
		Statement stat = conn.createStatement();
		stat.executeUpdate("drop table if exists session;");
		stat.executeUpdate("drop table if exists message;");

		stat.executeUpdate("create table session (sessionId, timestamp, jVersion, Os, url);");
		stat.executeUpdate("create table message (messageId, sessionId, fileName, textRequest, payload, reply, start, end, status);");
		conn.commit();
		conn.setAutoCommit(true);
		conn.close();
		return dbName;
	}

	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @return Connection
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public Connection getConnection(String dbName) {
		Date dat = new Date();
		if (dbName.length() == 0 && dbName.equals("")) {
			dbName = SD_FORMAT.format(dat);
		}
		Connection conn = null;
		try {
			Class.forName("org.sqlite.JDBC");
			String connectionString = "jdbc:sqlite:" + dbName + ".db";
			conn = DriverManager.getConnection(connectionString);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * get all sessionIds of a speficied database
	 * 
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param conn
	 * @return String[] result -- sessionIds
	 * @throws SQLException
	 */
	public String[] getSessionIds(Connection conn) throws SQLException {
		PreparedStatement st1 = conn.prepareStatement("Select sessionId from session");
		ResultSet rs1 = st1.executeQuery();
		Vector<String> data = new Vector<String>();
		while (rs1.next()) {
			data.add(rs1.getString(1));
		}
		String[] result = new String[data.size()];
		for (int i = 0; i < data.size(); i++) {
			result[i] = data.get(i);
		}
		return result;
	}

	

	
	/**
	 * write content of DTO to database
	 * 
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param session
	 * @param conn
	 * @return int returncode > 0 -- OK | < 0 failed.
	 * @throws SQLException
	 */
	public int store(MessageContainer outputMessage, Connection conn, String sessionId) {
		int returnValue = 0;
		Date date = new Date();
		if (sessionId.length() == 0){
			sessionId = getNewId("session");
		}
		
		try {
			long messageId = -1;
			try{
				messageId = getLastId(conn, "message") + 1;
			}
			catch (Exception ex){
				Logger.log("Empty file or file of same name like db exists - replacing it with new DB!", 3);
				conn.close();
				conn = getConnection(JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[12].getId(), ""));
				try {
					setUpDB();
					Logger.log("New DB created!",3);
					sessionId = getNewId("session") + 1;
					messageId = getLastId(conn, "message") + 1;
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
			String end = SD_FORMAT.format(date);
			String jVersion = System.getProperty("java.version");
			String os = System.getProperty("os.name") + " "
					+ System.getProperty("os.arch") + " "
					+ System.getProperty("os.version");

			returnValue = insertOrUpdateSessionTable(conn, 
													 sessionId,
													 outputMessage.getStartDateFull(), 
													 jVersion, 
													 os,
													 outputMessage.getTextURL());

			returnValue = insertOrUpdateMessageTable(conn, 
													 messageId,
													 sessionId, 
													 outputMessage.getFileName(),
													 outputMessage.getTextRequest(), 
													 outputMessage.getEncodedPayload(),
													 outputMessage.getReply(),
													 outputMessage.getStartDateFull(),
													 end,
													 outputMessage.getStatus());

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return returnValue;
	}

	/**
	 * insert or update the table session - based on the parameter provided.
	 * will return integer as status: >0 OK | <0 failed.
	 * 
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
	private int insertOrUpdateSessionTable(Connection conn, String sessionId,
			String timestamp, String jVersion, String Os, String url) throws SQLException {
		int returnValue = 1;
		String sqlString1 = "";
		
			PreparedStatement st0 = conn.prepareStatement("select count (*) from session where sessionId = ?");
			st0.setString(1, sessionId);
			ResultSet rs0 = st0.executeQuery();
			while (rs0.next()) {
				int count = rs0.getInt(1);
				PreparedStatement st1;
				if (count > 0) {
					// update
					sqlString1 = "update session set timestamp = ?, jVersion = ?, Os = ?, url =? where sessionId = ?;";
					st1 = conn.prepareStatement(sqlString1);
					st1.setString(1, timestamp);
					st1.setString(2, jVersion);
					st1.setString(3, Os);
					st1.setString(4, url);
					st1.setString(5, sessionId);
				} else {
					// new row
					sqlString1 = "insert into session (sessionId, timestamp, jVersion, Os, url) values (?,?,?,?,?);";
					st1 = conn.prepareStatement(sqlString1);
					st1.setString(1, sessionId);
					st1.setString(2, timestamp);
					st1.setString(3, jVersion);
					st1.setString(4, Os);
					st1.setString(5, url);
				}
				returnValue = st1.executeUpdate();
			}
		return returnValue;
	}

	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param conn
	 * @param messageId
	 * @param textRequest
	 * @param payload
	 * @param start
	 * @param end
	 * @return
	 * @throws SQLException
	 */
	private int insertOrUpdateMessageTable(Connection conn, long messageId,
			String connectionId, String fileName, String textRequest, 
			String payload, String reply, String start, String end, String status) throws SQLException {

		if (reply == null) reply = new String("--- none ---");
		int returnValue = 1;
		String sqlString1 = "";
		if (messageId >= 0) {
			PreparedStatement st0 = conn
					.prepareStatement("select count(*) from message where messageId = ?");
			st0.setLong(1, messageId);
			ResultSet rs0 = st0.executeQuery();
			while (rs0.next()) {
				int count = rs0.getInt(1);
				PreparedStatement st1;
				if (count > 0) {
					// update
					sqlString1 = "update message (connectionId, fileName, textRequest, textRequest, payload, reply, start, end, status) values (?,?, ?, ?, ?, ?, ?) where messageId = ?;";
					st1 = conn.prepareStatement(sqlString1);
					st1.setString(1, connectionId);
					st1.setString(2, fileName);
					st1.setString(3, textRequest);
					st1.setString(4, payload);
					st1.setString(5, reply);
					st1.setString(6, start);
					st1.setString(7, end);
					st1.setString(8, status);
					st1.setLong(9, messageId);
				} else {
					// new row
					sqlString1 = "insert into message (messageId, sessionId, fileName, textRequest, payload, reply, start, end, status) values (?,?,?,?,?,?,?,?,?);";
					st1 = conn.prepareStatement(sqlString1);
					st1.setLong(1, messageId);
					st1.setString(2, connectionId);
					st1.setString(3, fileName);
					st1.setString(4, textRequest);
					st1.setString(5, payload);
					st1.setString(6, reply);
					st1.setString(7, start);
					st1.setString(8, end);
					st1.setString(9, status);
				}
				returnValue = st1.executeUpdate();
			}
		}
		return returnValue;
	}

	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @return MessageContainer data from DB
	 */
	public Vector<MessageContainer> read(Connection conn, String sessionId, String fileName, FuzzingPanel fp) {
		Vector<MessageContainer> session = null;
		
		try {
			PreparedStatement st1 = conn.prepareStatement("select count(*) from session where sessionId = ?;");
			st1.setString(1, sessionId);
			
			ResultSet rs1 = st1.executeQuery();
			while (rs1.next()) {
				if (rs1.getInt(1) > 1) {
					Logger.log("More than one record found", 3);
					throw new Exception("More than one record found");
				} else {
					session = readSession(conn, sessionId, fileName, fp);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
	 * 
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param conn
	 * @param sessionId
	 * @return MessageContainer outputMessage
	 * @throws SQLException
	 */
	private Vector<MessageContainer> readSession(Connection conn, String sessionId, String fileName, FuzzingPanel fp)
			throws SQLException {
		Vector<MessageContainer> returnValue = new Vector<MessageContainer>();
		
		String sqlStatement = "select url from session where sessionId = ?";
		String url = new String();
		PreparedStatement st1 = conn.prepareStatement(sqlStatement);
		st1.setString(1, sessionId);
		ResultSet rs1 = st1.executeQuery();
		while(rs1.next()){
			url = rs1.getString(1);
		}
		
		String sql2 = "";
		if (fileName == null) {
			sql2 = "Select textRequest, payload, reply, start, end, status, filename from message where sessionId = ?";
		
		}
		else{
			sql2 = "Select textRequest, payload, reply, start, end, status, filename from message where sessionId = ? and filename = ?";
		}
		
		PreparedStatement st2 = conn.prepareStatement(sql2);
		st2.setString(1, sessionId);
		
		if (fileName != null) st2.setString(2, fileName);
		
		ResultSet rs2 = st2.executeQuery();

		while (rs2.next()){
			MessageContainer mc = new MessageContainer(fp);
			mc.setTextURL(url);
			mc.setPayload(rs2.getString(1));
			mc.setEncodedPayload(rs2.getString(2));
			mc.setReply(rs2.getString(3));
			mc.setStartDate(rs2.getDate(4));
			mc.setEnd(rs2.getDate(5));
			mc.setStatus(rs2.getString(6));
			mc.setFileName(rs2.getString(7));
			
			returnValue.add(mc);
		}
		return returnValue;
	}
	
	
	
	

/**
 * determines last used Id
 * @param conn
 * @param tableName
 * @return String newId
 * @throws SQLException
 */
	public String getNewId(String tableName) {
		Date date = new Date();
		return SD_FORMAT.format(date);
	}
	
	
	public long getLastId(Connection conn, String tableName) throws SQLException{
		long lastId = -1;
		String sql1 = "select count(*) from " + tableName;
		PreparedStatement pst1 = conn.prepareStatement(sql1);
		ResultSet rs1 = pst1.executeQuery();
		lastId = rs1.getLong(1);
		return lastId;
	}
	
	
	
	/**
	 * do query against db
	 * @param conn
	 * @param sql
	 * @return String[] results
	 */
	public String[] executeQuery(Connection conn, String sql){
		Vector<String> result = new Vector<String>();
		//TODO Sanatize input from sql!!!!
		try {
			PreparedStatement st1 = conn.prepareStatement(sql);
			ResultSet rs1 = st1.executeQuery();
			while(rs1.next()){
				//TODO a more generic way would be great here!!!
				result.add(rs1.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] returnValue = result.toArray(new String[result.size()]);
		return returnValue;
	}
}