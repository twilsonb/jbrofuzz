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
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
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
		stat.executeUpdate("drop table if exists response;");

		stat.executeUpdate("create table session (sessionId, timestamp, jVersion, Os, url);");
		stat.executeUpdate("create table message (messageId, sessionId, textRequest, payload, start, end, status);");
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
			dbName = dat.getYear() + "_" + dat.getMonth() + "_" + dat.getDay()
					+ "_" + dat.getHours() + ":" + dat.getMinutes();
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
	 * @return long[] result -- sessionIds
	 * @throws SQLException
	 */
	public long[] getSessionIds(Connection conn) throws SQLException {
		PreparedStatement st1 = conn
				.prepareStatement("Select sessionId from session");
		ResultSet rs1 = st1.executeQuery();
		Vector<Long> data = new Vector<Long>();
		while (rs1.next()) {
			data.add(rs1.getLong(1));
		}
		long[] result = new long[data.size()];
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
	public int store(MessageContainer outputMessage, Connection conn) {
		int returnValue = 0;
		try {
			Date date = new Date();
			long sessionId = getLastId(conn, "session") + 1;
			long messageId = getLastId(conn, "message") + 1;
			String end = SD_FORMAT.format(date);
			String jVersion = System.getProperty("java.version");
			String os = System.getProperty("os.name") + " "
					+ System.getProperty("os.arch") + " "
					+ System.getProperty("os.version");


			String logMessage = "Storing to sessionTable: sessionId: " + sessionId + " startDate: " + outputMessage.getStartDateFull() + " jVersion: " + jVersion + " os: " + os + 
			" urlString: " + outputMessage.getTextURL();
			Logger.log(logMessage, 0);
			returnValue = insertOrUpdateSessionTable(conn, 
													 sessionId,
													 outputMessage.getStartDateFull(), 
													 jVersion, 
													 os,
													 outputMessage.getTextURL());
			Logger.log("returnValue: " + returnValue, 0);

			logMessage = "Storing to MessageTable: messageId: "
					+ messageId + " sessionId: " + sessionId + " payload: " + outputMessage.getPayload()
			        + " encodedPayload: "
					+ outputMessage.getEncodedPayload() + " start: "
					+ outputMessage.getStartDateFull() + " end: " + end + " status: " + outputMessage.getStatus();
			Logger.log(logMessage, 0);
			returnValue = insertOrUpdateMessageTable(conn, 
													 messageId,
													 sessionId, 
													 outputMessage.getPayload(), 
													 outputMessage.getEncodedPayload(),
													 outputMessage.getStartDateFull(), 
													 end,
													 outputMessage.getStatus());
			Logger.log("result : " + returnValue, 0);

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
	private int insertOrUpdateSessionTable(Connection conn, long sessionId,
			String timestamp, String jVersion, String Os, String url) throws SQLException {
		int returnValue = 1;
		String sqlString1 = "";
		if (sessionId > 0) {
			PreparedStatement st0 = conn
					.prepareStatement("select count (*) from session where sessionId = ?");
			st0.setLong(1, sessionId);
			ResultSet rs0 = st0.executeQuery();
			while (rs0.next()) {
				int count = rs0.getInt(1);
				PreparedStatement st1;
				if (count > 0) {
					// update
					sqlString1 = "update session (timestamp, jVersion, Os, url) values (?, ?, ?, ?) where sessionId = ?;";
					st1 = conn.prepareStatement(sqlString1);
					st1.setString(1, timestamp);
					st1.setString(2, jVersion);
					st1.setString(3, Os);
					st1.setString(4, url);
					st1.setLong(5, sessionId);
				} else {
					// new row
					sqlString1 = "insert into session (sessionId, timestamp, jVersion, Os, url) values (?,?,?,?,?);";
					st1 = conn.prepareStatement(sqlString1);
					st1.setLong(1, sessionId);
					st1.setString(2, timestamp);
					st1.setString(3, jVersion);
					st1.setString(4, Os);
					st1.setString(5, url);
				}
				returnValue = st1.executeUpdate();
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
	 * @param payload
	 * @param start
	 * @param end
	 * @return
	 * @throws SQLException
	 */
	private int insertOrUpdateMessageTable(Connection conn, long messageId,
			long connectionId, String textRequest, 
			String payload, String start, String end, String status) throws SQLException {

		System.out.println("messageId: " + messageId + " connectionId: "
				+ connectionId);
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
					sqlString1 = "update message (connectionId, textRequest, encoding, payload, start, end, status) values (?, ?, ?, ?, ?) where messageId = ?;";
					st1 = conn.prepareStatement(sqlString1);
					st1.setLong(1, connectionId);
					st1.setString(2, textRequest);
					st1.setString(3, payload);
					st1.setString(4, start);
					st1.setString(5, end);
					st1.setString(6, status);
					st1.setLong(7, messageId);
				} else {
					// new row
					sqlString1 = "insert into message (messageId, sessionId, textRequest, payload, start, end, status) values (?,?,?,?,?,?,?);";
					st1 = conn.prepareStatement(sqlString1);
					st1.setLong(1, messageId);
					st1.setLong(2, connectionId);
					st1.setString(3, textRequest);
					st1.setString(4, payload);
					st1.setString(5, start);
					st1.setString(6, end);
					st1.setString(7, status);
				}
				returnValue = st1.executeUpdate();
			}
		}
		return returnValue;
	}

	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @return int; > 0 -> ok ; < 0 -> failed
	 */
	public MessageContainer read(Connection conn, long sessionId, FuzzingPanel fp) {
		String sql1 = "select count(*) from session where sessionId = ?;";
		MessageContainer session = null;
		try {
			PreparedStatement st1 = conn
					.prepareStatement("select count(*) from session where sessionId = ?;");
			ResultSet rs1 = st1.executeQuery();
			while (rs1.next()) {
				if (rs1.getInt(1) > 1) {
					throw new Exception("More than one record found");
				} else {
					session = readSession(conn, sessionId, fp);
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
	private MessageContainer readSession(Connection conn, long sessionId, FuzzingPanel fp)
			throws SQLException {
		//TODO
		MessageContainer returnValue = new MessageContainer(fp);
		String sqlStatement = "select url from session where session = ?";
		String url = new String();
		PreparedStatement st1 = conn.prepareStatement(sqlStatement);
		st1.setLong(1, sessionId);
		ResultSet rs1 = st1.executeQuery();
		while(rs1.next()){
			url = rs1.getString(1);
		}
		returnValue.setTextURL(url);
		
		//messageId, sessionId, textRequest, payload, start, end
		String sql2 = "Select textRequest, payload, start, end, status from message where sessionId = ?";
		PreparedStatement st2 = conn.prepareStatement(sql2);
		st2.setLong(1, sessionId);
		ResultSet rs2 = st2.executeQuery();
		while (rs2.next()){
			returnValue.setPayload(rs2.getString(1));
			returnValue.setEncodedPayload(rs2.getString(2));
			returnValue.setStartDate(new Date(rs2.getString(3)));
			returnValue.setEnd(new Date(rs2.getString(4)));
			returnValue.setStatus(rs2.getString(5));
		}
		
		return returnValue;
	}

/**
 * determines last used Id
 * @param conn
 * @param tableName
 * @return long lastUsedId
 * @throws SQLException
 */
	public long getLastId(Connection conn, String tableName)
			throws SQLException {
		long lastId = -1;
		String sql1 = "select count(*) from " + tableName;
		// TODO NullPointerException ??!!
		PreparedStatement pst1 = conn.prepareStatement(sql1);
		ResultSet rs1 = pst1.executeQuery();
		lastId = rs1.getLong(1);
		return lastId;
	}
}