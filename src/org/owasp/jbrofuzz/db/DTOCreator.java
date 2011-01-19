package org.owasp.jbrofuzz.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.db.dto.ConnectionDTO;
import org.owasp.jbrofuzz.db.dto.MessageDTO;
import org.owasp.jbrofuzz.db.dto.ResponseDTO;
import org.owasp.jbrofuzz.db.dto.SessionDTO;
import org.owasp.jbrofuzz.fuzz.MessageContainer;
import org.owasp.jbrofuzz.fuzz.ui.EncodersRow;
import org.owasp.jbrofuzz.fuzz.ui.EncodersTable;
import org.owasp.jbrofuzz.fuzz.ui.EncodersTableModel;
import org.owasp.jbrofuzz.fuzz.ui.FuzzerTable;
import org.owasp.jbrofuzz.fuzz.ui.FuzzersTableModel;
import org.owasp.jbrofuzz.fuzz.ui.OutputTable;
import org.owasp.jbrofuzz.fuzz.ui.OutputTableModel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

public class DTOCreator {

	/**
	 * @author daemonmidi@gmail.com
	 * @since verison 2.5
	 * @param mWindow
	 * @return SessionDTO filled
	 */
	public SessionDTO createSessionDTO(MessageContainer outputMessage, long sessionId) {
		SessionDTO session = new SessionDTO();

		session.setJVersion(System.getProperty("java.version"));
		session.setOs(System.getProperty("os.name"));
		session.setTimestamp(Calendar.getInstance().getTime().toGMTString());
		System.out.println("Markus: " + outputMessage.getMessage());
		
		/*
		session.setConnectionDTO(fillConnection(mWindow,
				session.getSessionId(), -1));
		session.setMessage(fillMessages(mWindow, session.getConnectionDTO()
				.getConnectionId()));
		session.setResponse(fillResponse(mWindow, session.getConnectionDTO()
				.getConnectionId()));
		 */
		return session;
	}

	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param mWindow
	 * @param sessionId
	 * @return ConnectionDTO filled
	 */
	private ConnectionDTO fillConnection(JBroFuzzWindow mWindow,
			long sessionId, long connectionId) {
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
	private MessageDTO[] fillMessages(JBroFuzzWindow mWindow, long connectionId) {
		int i = 0;
		Vector<MessageDTO> mesV = new Vector<MessageDTO>();

		for (i = 0; i < mWindow.getPanelFuzzing().getOutputTable().getModel()
				.getRowCount(); i++) {
			MessageDTO message = new MessageDTO();

			message.setConnectionId(connectionId);
			if (i < mWindow.getPanelFuzzing().getEncodersTableList().getSize() - 1) {
				message.setEncoding(mWindow
						.getPanelFuzzing()
						.getEncoders(
								Integer.valueOf(mWindow.getPanelFuzzing()
										.getOutputTable().getModel()
										.getValueAt(i, 0).toString()))
						.toString());
				message.setEnd(Integer.valueOf(mWindow.getPanelFuzzing()
						.getFuzzersTable().getModel().getValueAt(i, 2)
						.toString()));
				message.setStart(Integer.valueOf(mWindow.getPanelFuzzing()
						.getFuzzersTable().getModel().getValueAt(i, 1)
						.toString()));
			} else {
				message.setEncoding("todo");
				message.setEnd(-1);
				message.setStart(-1);
			}
			message.setMessageId(i);
			message.setPayload(mWindow.getPanelFuzzing().getEncodedPayload());
			message.setTextRequest(mWindow.getPanelFuzzing().getTextRequest());
			mesV.add(message);
		}

		MessageDTO[] messages = new MessageDTO[mesV.size()];
		for (int j = 0; j < mesV.size(); j++) {
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
	private ResponseDTO[] fillResponse(JBroFuzzWindow mWindow, long connectionId) {

		int row = 0;
		Vector<ResponseDTO> resV = new Vector<ResponseDTO>();

		while (row < mWindow.getPanelFuzzing().getOutputTable().getModel()
				.getRowCount()) {
			ResponseDTO response = new ResponseDTO();
			response.setConnectionId(connectionId);
			response.setResponseBody(mWindow.getPanelFuzzing().getOutputTable()
					.getModel().getValueAt(row, 2).toString());
			response.setResponseHeader(mWindow.getPanelFuzzing()
					.getOutputTable().getModel().getValueAt(row, 3).toString());
			response.setResponseId(row);
			try {
				response.setStatusCode(Integer.valueOf(mWindow
						.getPanelFuzzing().getOutputTable().getModel()
						.getValueAt(row, 4).toString()));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				response.setStatusCode(-1);
			}
			resV.add(response);
			row++;
		}

		ResponseDTO[] responses = new ResponseDTO[resV.size()];
		for (int j = 0; j < resV.size(); j++) {
			responses[j] = resV.get(j);
		}
		return responses;
	}

	/**
	 * go from DTO to mWindow
	 * 
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param session
	 * @param mWindow
	 * @return filled mWindow
	 */
	public JBroFuzzWindow fillWindow(SessionDTO session, JBroFuzzWindow mWindow) {

		mWindow.getPanelFuzzing().setTextURL(
				session.getConnectionDTO().getUrlString());

		EncodersTableModel etM = new EncodersTableModel();
		FuzzersTableModel ftM = new FuzzersTableModel();

		for (int row = 0; row < session.getMessage().length; row++) {

			// add Encoder
			etM.addRow(session.getMessage()[row].getEncoding(), "", "");

			// Start, END
			ftM.addRow("name", "type", "id",
					session.getMessage()[row].getStart(),
					session.getMessage()[row].getEnd());

			// PAYLOAD
			mWindow.getPanelFuzzing().setEncodedPayload(
					session.getMessage()[row].getPayload());

			// TextRequest
			mWindow.getPanelFuzzing().setTextRequest(
					session.getMessage()[row].getTextRequest());
		}

		EncodersTable et = new EncodersTable(etM);
		mWindow.getPanelFuzzing().updateEncoderPanel(et);

		FuzzerTable ft = new FuzzerTable(ftM);
		mWindow.getPanelFuzzing().updateFuzzerTable(ft);

		OutputTableModel otM = new OutputTableModel();

		for (int row = 0; row < session.getResponse().length; row++) {
			// Response Body
			Object[] obj = new Object[] {

					"TODO", // FileName
					session.getConnectionDTO().getUrlString(),
					StringUtils.abbreviate(
							session.getResponse()[row].getResponseBody(), 50),
					StringUtils.abbreviate(
							session.getResponse()[row].getResponseHeader(), 50),
					session.getResponse()[row].getStatusCode(), "TODO", // StringUtils.leftPad(""
																		// +
																		// outputMessage.getResponseTime(),
																		// 5,
																		// '0'),
					"TODO" // StringUtils.leftPad("" +
							// outputMessage.getByteCount(), 8, '0')
			};
			otM.addRow(obj);
		}
		OutputTable ot = new OutputTable(otM);
		mWindow.getPanelFuzzing().updateOutputTable(ot);
		return mWindow;
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