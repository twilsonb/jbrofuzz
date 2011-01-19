package org.owasp.jbrofuzz.db;

import java.sql.Connection;

import org.json.JSONObject;
import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.fuzz.MessageContainer;
import org.owasp.jbrofuzz.system.Logger;
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
		Logger.log("Using DBName: " + dbName, 0);
		Logger.log("DBHandlerType: " + this.dbHandler.getClass(), 0);
		int returnCode = 0;
		
		if (dbHandler.getClass().getName().equals(CouchDBHandler.class)){
			Logger.log("Storing to CouchDB", 0);
			CouchDBMapper couchMapper = new CouchDBMapper();	
			JSONObject document = couchMapper.toCouch2(outputMessage);
			returnCode = ((CouchDBHandler) dbHandler).store(dbName, "", document);
		}

		else{
			Logger.log("Storing to SQLite", 0);
				SQLiteHandler sqlH = (SQLiteHandler) dbHandler;
				try {
					Logger.log("Setting up connection", 0);
					Connection conn = sqlH.getConnection(dbName);
					if (conn == null) Logger.log("Connection = null", 0);
					Logger.log("executing store", 0);
					returnCode = sqlH.store(outputMessage, conn);
					Logger.log("done", 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		
		return returnCode;
	}
	
}
