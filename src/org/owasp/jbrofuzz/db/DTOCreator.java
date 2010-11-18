package org.owasp.jbrofuzz.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.jbrofuzz.db.dto.ConnectionDTO;
import org.owasp.jbrofuzz.db.dto.MessageDTO;
import org.owasp.jbrofuzz.db.dto.ResponseDTO;
import org.owasp.jbrofuzz.db.dto.SessionDTO;
import org.owasp.jbrofuzz.fuzz.io.Save;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;


public class DTOCreator {

	/**
	 * @author daemonmidi@gmail.com
	 * @since verison 2.5
	 * @param mWindow
	 * @return SessionDTO filled
	 */
	public SessionDTO createSessionDTO(JBroFuzzWindow mWindow, long sessionId) {
		SessionDTO session = new SessionDTO();

		session.setJVersion(System.getProperty("java.version"));
		session.setOs(System.getProperty("os.name"));
		session.setTimestamp(Calendar.getInstance().getTime().toGMTString());
		if (sessionId >= 0) {
			session.setSessionId(sessionId);
		} else {
			session.setSessionId(getMaxId("session"));
		}

		session.setConnectionDTO(fillConnection(mWindow, session.getSessionId()));
		session.setMessage(fillMessages(mWindow, session.getConnectionDTO()
				.getConnectionId()));
		session.setResponse(fillResponse(mWindow, session.getConnectionDTO()
				.getConnectionId()));

		return session;
	}

	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param mWindow
	 * @param sessionId
	 * @return ConnectionDTO filled
	 */
	private ConnectionDTO fillConnection(JBroFuzzWindow mWindow, long sessionId) {
		ConnectionDTO connection = new ConnectionDTO();
		long connectionId = getPosMaxId(Long.valueOf(sessionId));
		connection.setConnectionId(connectionId);
		connection.setSessionid(sessionId);
		connection.setUrlString(mWindow.getPanelFuzzing().getTextURL());

		return connection;
	}

	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param mWindow
	 * @param sessiondId
	 * @return MessageDTO[] filled
	 */
	private MessageDTO[] fillMessages(JBroFuzzWindow mWindow, long connectionId) {
		MessageDTO[] messages = new MessageDTO[] {};

		MessageDTO message = new MessageDTO();
		int i = 0;

		message.setConnectionId(connectionId);
		message.setEncoding(String.valueOf(mWindow.getPanelFuzzing()
				.getEncodersTableList().getEncoderCount(i)));
		message.setEnd(mWindow.getPanelFuzzing().getEncodersTableList()
				.getSize());
		message.setMessageId(getMaxId("message"));
		message.setPayload(mWindow.getPanelFuzzing().getEncodedPayload());
		message.setStart(i);
		message.setTextRequest(mWindow.getPanelFuzzing().getTextRequest());

		return messages;
	}

	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param mWindow
	 * @param sessionId
	 * @return ResponseDTO[] filled
	 */
	private ResponseDTO[] fillResponse(JBroFuzzWindow mWindow, long connectionId) {
		final JSONArray output = Save.getTableDataInJSON(mWindow
				.getPanelFuzzing().getOutputTable().getModel());
		ResponseDTO[] responses = new ResponseDTO[output.length()];

		for (int i = 0; i < output.length(); i++) {
			JSONObject json;
			try {
				json = output.getJSONObject(i);
				ResponseDTO response = new ResponseDTO();
				response.setConnectionId(connectionId);
				response.setResponseBody(json.getString("responseBody"));
				response.setResponseHeader(json.getString("responseHeader"));
				response.setResponseId(getMaxId("response"));
				response.setStatusCode(json.getInt("statusCode"));
				responses[i] = response;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return responses;
	}

	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param sessionId
	 * @return PosMaxConnectionId
	 */
	private long getPosMaxId(long sessionId) {
		long connectionId = 0;
		Connection con = null;
		try {
			SQLLiteHandler dbHandler = new SQLLiteHandler();
			con = dbHandler.getConnection();
			String sql = "select connectionId from session where sessionId = ?";
			PreparedStatement prep;
			prep = con.prepareStatement(sql);
			prep.setLong(1, sessionId);
			ResultSet rs = prep.executeQuery();
			while (rs.next()) {
				connectionId = rs.getLong(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return connectionId;
	}

	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @return get new connectionId
	 */
	private long getMaxId(String tableName) {
		long connectionId = 0;
		Connection con = null;
		try {
			SQLLiteHandler dbHanlder = new SQLLiteHandler();
			String sql = "select max(connectionId) from ?;";
			con = dbHanlder.getConnection();
			PreparedStatement prep = con.prepareStatement(sql);
			prep.setString(1, tableName);
			ResultSet rs = prep.executeQuery();
			while (rs.next()) {
				connectionId = rs.getLong(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		connectionId++;
		return connectionId;

	}
}