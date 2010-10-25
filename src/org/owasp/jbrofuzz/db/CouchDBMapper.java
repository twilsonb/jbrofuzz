package org.owasp.jbrofuzz.db;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.jbrofuzz.db.dto.ConnectionDTO;
import org.owasp.jbrofuzz.db.dto.MessageDTO;
import org.owasp.jbrofuzz.db.dto.ResponseDTO;
import org.owasp.jbrofuzz.db.dto.SessionDTO;

public class CouchDBMapper {

	/**
	 * maps content from JSONObject for CouchDB into SessionDTO
	 * 
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param couch
	 * @return SessionDTO
	 */
	public SessionDTO toDTO(JSONObject couch) {
		SessionDTO session = new SessionDTO();
		try {
			//SessioDTO
			if (couch.has("jVersion")) session.setJVersion(couch.getString("jVersion"));
			if (couch.has("os")) session.setOs(couch.getString("os"));
			if (couch.has("sessionId")) session.setSessionId(Long.valueOf(couch.getString("sessionid")));
			if (couch.has("timestamp")) session.setTimestamp(couch.getString("timestamp"));
			
			//connectionDTO
			if (couch.has("connection")){
				JSONObject connection = couch.getJSONObject("connection");
				ConnectionDTO conn = new ConnectionDTO();
				if (connection.has("connectionId")) conn.setConnectionId(Long.valueOf(connection.getString("connectionId")));
				if (connection.has("sessionId")) conn.setSessionid(Long.valueOf(connection.getString("sessionId")));
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
					if (inhalt.has("responesId")) response.setResponseId(Long.valueOf(inhalt.getString("responseId")));
					if (inhalt.has("statusCode")) response.setStatusCode(Integer.valueOf(inhalt.getString("statusCode")));
					respAr[i] = response;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return session;
	}

	/**
	 * maps content from SessionDTO into JSONObject for CouchDB
	 * 
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param session
	 * @return JSONObject
	 */
	public JSONObject toCouch(SessionDTO session) {
		JSONObject couch = new JSONObject();
		try {
			couch.append("jVersion", session.getJVersion());
			couch.append("os", session.getOs());
			couch.append("sessionId", String.valueOf(session.getSessionId()));
			couch.append("timeStamp", session.getTimestamp());
			
		//connectionDTO
			ConnectionDTO conn = new ConnectionDTO();
			conn = session.getConnectionDTO();
			JSONObject connection = new JSONObject();
			connection.append("connectionId" ,conn.getConnectionId());
			connection.append("sessionId", conn.getSessionId());
			connection.append("urlString", conn.getUrlString());
			couch.append("connection", connection);
			
		//messageDTO
			int laenge = session.getMessage().length;
			MessageDTO[] message = session.getMessage();
			JSONArray messAr = new JSONArray();
			for (int i = 0; i < laenge; i++){
				JSONObject mess = new JSONObject();
				MessageDTO nachricht = new MessageDTO();
				nachricht = message[i];
				mess.append("connectionId", nachricht.getConnectionId());
				mess.append("encoding", nachricht.getEncoding());
				mess.append("end", String.valueOf(nachricht.getEnd()));
				mess.append("messageId", String.valueOf(nachricht.getMessageId()));
				mess.append("payload", nachricht.getPayload());
				mess.append("start", String.valueOf(nachricht.getStart()));
				mess.append("textRequest", nachricht.getTextRequest());
				messAr.put(mess);
			}
			couch.append("message", messAr);
			
		//responseDTO
			int laeng = session.getResponse().length;
			ResponseDTO[] response = session.getResponse();
			JSONArray responseAr = new JSONArray();
			for (int i = 0; i < laeng; i++){
				JSONObject resp = new JSONObject();
				ResponseDTO nachricht = new ResponseDTO();
				nachricht = response[i];
				resp.append("connectionId", String.valueOf(nachricht.getConnectionId()));
				resp.append("responseBody", nachricht.getResponseBody());
				resp.append("responseHeader", nachricht.getResponseHeader());
				resp.append("responseId", String.valueOf(nachricht.getResponseId()));
				resp.append("statusCode", String.valueOf(nachricht.getStatusCode()));
				responseAr.put(resp);
			}
			couch.append("response", responseAr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return couch;
	}
}