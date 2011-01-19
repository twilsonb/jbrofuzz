package org.owasp.jbrofuzz.db;

import java.io.File;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

public class DBAdaptorFactory {
	private static DBAdaptor dbAdaptor;
	
	/**
	 * create DBAdaptor and choose dbHanlder based on prefence settings at time of creation
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @return DBApaptor -> Implements mehtod store(SessionDTO session);
	 */
	public static DBAdaptor getInstance(){
		if (JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[11].getId(), "").toLowerCase().trim().equals("couchdb")){
			if (dbAdaptor == null){
				dbAdaptor= new DBAdaptor(new CouchDBHandler());
			}
		}
		else{
			if (dbAdaptor == null){
				SQLiteHandler sqlH = new SQLiteHandler();
				try{
					String fileName = JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[12].getId(), "") + ".db";
					File test = new File(fileName);
					if (!test.exists()){
						sqlH.setUpDB();
					}
					else{
						Logger.log("DB exists", 0);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				dbAdaptor = new DBAdaptor(sqlH);
			}
		}
		return dbAdaptor;
	}
	
	
	/**
	 *  set internal DBAdaptor to null - so next the db will be choosen based on the actual preference settings at time of creation
	 *  @author daemonmidi@gmail.com
	 *  @since version 2.5
	 */
	public void resetDBAdaptor(){
		dbAdaptor = null;
	}
}
