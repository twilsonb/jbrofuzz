package org.owasp.jbrofuzz.db;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.jbrofuzz.fuzz.MessageContainer;

public class CouchDBMapper {
	private static final SimpleDateFormat SD_FORMAT = new SimpleDateFormat(
			"zzz-yyyy-MM-dd-HH-mm-ss-SSS", Locale.ENGLISH);
	/**
	 * maps content from JSONObject for CouchDB into SessionDTO
	 * 
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param couch
	 * @return SessionDTO
	 */
	public MessageContainer toDTO(JSONObject couch) {
		return null;
	/*
		SessionDTO session = new SessionDTO();
		try {
			//SessioDTO
			if (couch.has("jVersion")) session.setJVersion(couch.getString("jVersion"));
			if (couch.has("os")) session.setOs(couch.getString("os"));
			if (couch.has("sessionId")) session.setSessionId(couch.getLong("sessionId"));
			if (couch.has("timeStamp")) session.setTimestamp(couch.getString("timeStamp"));
			
			//connectionDTO
			if (couch.has("connection")){
				JSONObject connection = couch.getJSONObject("connection");
				ConnectionDTO conn = new ConnectionDTO();
				if (connection.has("connectionId")) conn.setConnectionId(Long.valueOf(connection.getString("connectionId")));
				if (connection.has("sessionId")) conn.setSessionid(connection.getLong("sessionId"));
				if (connection.has("urlString")) conn.setUrlString(connection.getString("urlString"));
				session.setConnectionDTO(conn);
			}
			
			//messagesDTO
			if (couch.has("message")){
				JSONArray nachrichten = couch.getJSONArray("message");
				int laenge = nachrichten.length();
				MessageDTO[] messageAr = new MessageDTO[laenge]; 
				for (int i = 0; i < laenge; i++){
					JSONObject inhalt = nachrichten.getJSONObject(i);
					MessageDTO message = new MessageDTO();
					if (inhalt.has("connectionId")) message.setConnectionId(Long.valueOf(inhalt.getString("connectionId")));
					if (inhalt.has("encoding")) message.setEncoding(inhalt.getString("encoding"));
					if (inhalt.has("end")) message.setEnd(Integer.valueOf(inhalt.getString("end")));
					if (inhalt.has("messageId")) message.setMessageId(Long.valueOf(inhalt.getString("messageId")));
					if (inhalt.has("payload")) message.setPayload(inhalt.getString("payload"));
					if (inhalt.has("start")) message.setStart(Integer.valueOf(inhalt.getString("start")));
					if (inhalt.has("textRequest")) message.setTextRequest(inhalt.getString("textRequest"));
					messageAr[i] = message;
				}
				session.setMessage(messageAr);
			}
			
			//responsesDTO
			if (couch.has("response")){
				JSONArray antwort = couch.getJSONArray("response");
				int laeng = antwort.length();
				ResponseDTO[] respAr = new ResponseDTO[laeng];
				for (int i = 0; i < laeng; i++){
					JSONObject inhalt = antwort.optJSONObject(i);
					ResponseDTO response = new ResponseDTO();
					if (inhalt.has("connectionId")) response.setConnectionId(Long.valueOf(inhalt.getString("connectionId")));
					if (inhalt.has("responseBody")) response.setResponseBody(inhalt.getString("responseBody"));
					if (inhalt.has("responseHeader")) response.setResponseHeader(inhalt.getString("responseHeader"));
					if (inhalt.has("responseId")) response.setResponseId(Long.valueOf(inhalt.getString("responseId")));
					if (inhalt.has("statusCode")) response.setStatusCode(Integer.valueOf(inhalt.getString("statusCode")));
					respAr[i] = response;
				}
				session.setResponse(respAr);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return session;
		*/
	}

	
	/**
	 * intermediate method for moving from dto to messageWrite as data container.
	 * @param outputMessage
	 * @return
	 */
	public JSONObject toCouch2(MessageContainer outputMessage){
		JSONObject couch = new JSONObject();
		Date date = new Date();
		
			try {
				couch.accumulate("jVersion", System.getProperty("java.version"));
				couch.accumulate("os", System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version"));
				couch.accumulate("sessionId", "TODO");
				couch.accumulate("timeStamp", SD_FORMAT.format(date));
				
				JSONObject connection = new JSONObject();
				connection.accumulate("connectionId" ,"TODO");
				connection.accumulate("sessionId", "TODO");
				connection.accumulate("urlString", outputMessage.getTextURL());
				couch.accumulate("connection", connection);

				JSONObject mess = new JSONObject();
				mess.accumulate("connectionId", "TODO");
				mess.accumulate("encoding", "TODO");
				mess.accumulate("end", outputMessage.getResponseTime());
				mess.accumulate("messageId", "TODO");
				mess.accumulate("payload", outputMessage.getEncodedPayload());
				mess.accumulate("start", outputMessage.getStartDateFull());
				mess.accumulate("textRequest", outputMessage.getPayload());
				couch.put("message", mess);
				
				JSONObject resp = new JSONObject();
				resp.accumulate("connectionId", "TODO");
				resp.accumulate("responseId", "TODO");
				resp.accumulate("timeTaken", outputMessage.getResponseTime());
				resp.accumulate("status", outputMessage.getStatus());
				couch.put("response", resp);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		
		
		return couch;
	}
	
	
	/**
	 * maps content from SessionDTO into JSONObject for CouchDB
	 * 
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param session
	 * @return JSONObject
	 */
	public JSONObject toCouch(MessageContainer session) {
		JSONObject couch = new JSONObject();
		return couch;
		/*
		try {
			couch.accumulate("jVersion", session.getJVersion());
			couch.accumulate("os", session.getOs());
			couch.accumulate("sessionId", String.valueOf(session.getSessionId()));
			couch.accumulate("timeStamp", session.getTimestamp());
			
		//connectionDTO
			ConnectionDTO conn = new ConnectionDTO();
			conn = session.getConnectionDTO();
			JSONObject connection = new JSONObject();
			connection.accumulate("connectionId" ,conn.getConnectionId());
			connection.accumulate("sessionId", conn.getSessionId());
			connection.accumulate("urlString", conn.getUrlString());
			couch.accumulate("connection", connection);
			
		//messageDTO
			int laenge = session.getMessage().length;
			MessageDTO[] message = session.getMessage();
			JSONArray messAr = new JSONArray();
			for (int i = 0; i < laenge; i++){
				JSONObject mess = new JSONObject();
				MessageDTO nachricht = new MessageDTO();
				nachricht = message[i];
				mess.accumulate("connectionId", nachricht.getConnectionId());
				mess.accumulate("encoding", nachricht.getEncoding());
				mess.accumulate("end", String.valueOf(nachricht.getEnd()));
				mess.accumulate("messageId", String.valueOf(nachricht.getMessageId()));
				mess.accumulate("payload", nachricht.getPayload());
				mess.accumulate("start", String.valueOf(nachricht.getStart()));
				mess.accumulate("textRequest", nachricht.getTextRequest());
				messAr.put(mess);
			}
			couch.put("message", messAr);
			
		//responseDTO
			int laeng = session.getResponse().length;
			ResponseDTO[] response = session.getResponse();
			JSONArray responseAr = new JSONArray();
			for (int i = 0; i < laeng; i++){
				JSONObject resp = new JSONObject();
				ResponseDTO nachricht = new ResponseDTO();
				nachricht = response[i];
				resp.accumulate("connectionId", String.valueOf(nachricht.getConnectionId()));
				resp.accumulate("responseBody", nachricht.getResponseBody());
				resp.accumulate("responseHeader", nachricht.getResponseHeader());
				resp.accumulate("responseId", String.valueOf(nachricht.getResponseId()));
				resp.accumulate("statusCode", String.valueOf(nachricht.getStatusCode()));
				responseAr.put(resp);
			}
			couch.put("response", responseAr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return couch;
		 */
	}
}