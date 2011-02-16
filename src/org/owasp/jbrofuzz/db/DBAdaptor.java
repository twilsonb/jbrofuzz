package org.owasp.jbrofuzz.db;

import java.sql.Connection;

import org.json.JSONObject;
import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.fuzz.MessageContainer;
import org.owasp.jbrofuzz.graph.GraphingPanel;
import org.owasp.jbrofuzz.graph.utils.DBWalker;
import org.owasp.jbrofuzz.graph.utils.Walker;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;


public class DBAdaptor{
	private Object dbHandler;
	
	public DBAdaptor(Object dbHandler){
		this.dbHandler = dbHandler;
	}
	
	/**
	 * @author daemonmidi@gmail.com
	 * @param session SessionDTO - containing sessionData to be stored
	 * @return returnCode int - 0 == OK | 1 == failed.
	 */
	public int store(MessageContainer outputMessage){
		String dbName = JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[12].getId(), "");
		int returnCode = 0;
		
		if (dbHandler.getClass().getName().equals(CouchDBHandler.class)){
			Logger.log("Storing to CouchDB", 0);
			CouchDBMapper couchMapper = new CouchDBMapper();	
			JSONObject document = couchMapper.toCouch2(outputMessage);
			returnCode = ((CouchDBHandler) dbHandler).store(dbName, "", document);
		}

		else{
				SQLiteHandler sqlH = (SQLiteHandler) dbHandler;
				try {
					Connection conn = sqlH.getConnection(dbName);
					if (conn == null) Logger.log("Connection = null", 0);
					returnCode = sqlH.store(outputMessage, conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		
		return returnCode;
	}
	
	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param fileName
	 * @return MessageContainer - content read from db
	 */
	public MessageContainer read(String fileName, String sessionId, JBroFuzzWindow mWindow){
		MessageContainer mc = new MessageContainer(mWindow.getPanelFuzzing());

		if (dbHandler.getClass().getName().equals(CouchDBHandler.class)){
			Logger.log("TODO: reading from CouchDB", 0);
		}
		else{
			SQLiteHandler sqlH = (SQLiteHandler) dbHandler;
			Connection conn = sqlH.getConnection(fileName);
			mc = sqlH.read(conn, Long.valueOf(sessionId), mWindow.getPanelFuzzing());
		}
		return mc;
	}
	
	public String[] executeQuery(String sql){
		if (dbHandler.getClass().getName().equals(CouchDBHandler.class)){
			Logger.log("TODO: reading form CouchDB", 3);
		}
		else{
			SQLiteHandler sqlH = (SQLiteHandler) dbHandler;
			Connection conn = sqlH.getConnection(JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[12].getId(), ""));
			return sqlH.executeQuery(conn, sql);
		}
		return null;
	}
	
	public DBWalker getWalker(GraphingPanel gPanel){
		//TODO: Distinction between Couch and SQLite missing - only SQLite here and now.
		return new DBWalker(gPanel);
	}
	
}
