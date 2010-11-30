package org.owasp.jbrofuzz.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import org.owasp.jbrofuzz.db.dto.ConnectionDTO;
import org.owasp.jbrofuzz.db.dto.MessageDTO;
import org.owasp.jbrofuzz.db.dto.ResponseDTO;
import org.owasp.jbrofuzz.db.dto.SessionDTO;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;

public class DTOCreator {

	/**
	 * @author daemonmidi@gmail.com
	 * @since verison 2.5
	 * @param mWindow
	 * @return SessionDTO filled
	 */
	public SessionDTO createSessionDTO(JBroFuzzWindow mWindow, long sessionId,
			Connection con) {
		SessionDTO session = new SessionDTO();

		session.setJVersion(System.getProperty("java.version"));
		session.setOs(System.getProperty("os.name"));
		session.setTimestamp(Calendar.getInstance().getTime().toGMTString());
		if (sessionId >= 0) {
			session.setSessionId(sessionId);
		} else {
			Date dat = new Date();
			sessionId = dat.getTime();
			session.setSessionId(sessionId);
		}

		session.setConnectionDTO(fillConnection(mWindow,
				session.getSessionId(), con, -1));
		session.setMessage(fillMessages(mWindow, session.getConnectionDTO()
				.getConnectionId(), con));
		session.setResponse(fillResponse(mWindow, session.getConnectionDTO()
				.getConnectionId(), con));

		return session;
	}
	
	/**
	 * go from DTO to mWindow
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param session
	 * @param mWindow
	 * @return
	 */
	public JBroFuzzWindow fillWindow(SessionDTO session, JBroFuzzWindow mWindow){
		//TODO
		return mWindow; 
	}

	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param mWindow
	 * @param sessionId
	 * @return ConnectionDTO filled
	 */
	private ConnectionDTO fillConnection(JBroFuzzWindow mWindow,
			long sessionId, Connection con, long connectionId) {
		ConnectionDTO connection = new ConnectionDTO();

		// connectionId = getPosMaxId(Long.valueOf(sessionId), con);
		Date dat = new Date();
		connectionId = dat.getTime();	
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
	private MessageDTO[] fillMessages(JBroFuzzWindow mWindow,
			long connectionId, Connection con) {
		int i = 0;
		Vector<MessageDTO> mesV = new Vector<MessageDTO>();
		
		
		for (i = 0; i < mWindow.getPanelFuzzing().getOutputTable().getModel().getRowCount(); i++) {
			MessageDTO message = new MessageDTO();

			message.setConnectionId(connectionId);
			if (i < mWindow.getPanelFuzzing().getEncodersTableList().getSize() -1){
				message.setEncoding(mWindow.getPanelFuzzing().getEncoders(Integer.valueOf(mWindow.getPanelFuzzing().getOutputTable().getModel().getValueAt(i, 0).toString())).toString());
				message.setEnd(Integer.valueOf(mWindow.getPanelFuzzing().getFuzzersTable().getModel().getValueAt(i, 2).toString()));
				message.setStart(Integer.valueOf(mWindow.getPanelFuzzing().getFuzzersTable().getModel().getValueAt(i, 1).toString()));
			}
			else{
				message.setEncoding("todo");
				message.setEnd(-1);
				message.setStart(-1);
			}
			message.setMessageId(i);
			message.setPayload(mWindow.getPanelFuzzing().getEncodedPayload());
			message.setTextRequest(mWindow.getPanelFuzzing().getTextRequest());
			mesV.add(message);
		}
		
		MessageDTO[] messages = new MessageDTO[mesV.size()] ;
		for (int j = 0; j < mesV.size(); j++){
			messages[j] = mesV.get(j);
		}
		
		System.out.println(i + " Messages created: " + messages.length);
		return messages;
	}

	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param mWindow
	 * @param sessionId
	 * @return ResponseDTO[] filled
	 */
	private ResponseDTO[] fillResponse(JBroFuzzWindow mWindow,
			long connectionId, Connection con) {

		int row = 0;
		Vector<ResponseDTO> resV = new Vector<ResponseDTO>();
		
		while (row < mWindow.getPanelFuzzing().getOutputTable().getModel().getRowCount()) {
			ResponseDTO response = new ResponseDTO();
			response.setConnectionId(connectionId);
			response.setResponseBody(mWindow.getPanelFuzzing().getOutputTable().getModel().getValueAt(row, 2).toString());
			response.setResponseHeader(mWindow.getPanelFuzzing().getOutputTable().getModel().getValueAt(row, 3).toString());
			response.setResponseId(row);
			try{
				response.setStatusCode(Integer.valueOf(mWindow.getPanelFuzzing().getOutputTable().getModel().getValueAt(row, 4).toString()));
			}
			catch (NumberFormatException e){
				e.printStackTrace();
				response.setStatusCode(-1);
			}
			resV.add(response);
			row++;
		}
		
		ResponseDTO[] responses = new ResponseDTO[resV.size()];
		for (int j = 0; j < resV.size(); j++){
			responses[j] = resV.get(j);
		}
		
		System.out.println(row + " Response to create - " + responses.length + " repsonses created");
		return responses;
	}

	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param sessionId
	 * @return PosMaxConnectionId
	 */
	private long getPosMaxId(long sessionId, Connection con) {
		long connectionId = 0;
		try {
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
	private long getMaxId(String tableName, Connection con) {
		long connectionId = 0;
		try {
			String sql = "select max(connectionId) from ?;";
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