package org.owasp.jbrofuzz.io;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.db.SQLiteHandler;
import org.owasp.jbrofuzz.fuzz.MessageWriter;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

public class StorageHandler implements StorageInterface {

	private FileHandler mFileHandler;
	
	private SQLiteHandler mSQLiteHandler;
	
	private int whichOne;
	
	public StorageHandler() {
	
		final String dbType = JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[11].getId(), "-1");
		
		if(dbType.equals("SQLite")){
			whichOne = 1;
		}
		else if (dbType.equals("CouchDB")) {
			whichOne = 2;
		}
		else /*(dbType.equals("None") ) */{
			whichOne = 0;
			mFileHandler = new FileHandler();
		}
		
	}
	
	@Override
	public void createNewLocation() {
		// TODO Auto-generated method stub
		if(whichOne == 0) {
			mFileHandler.createNewLocation();
		}

	}

	@Override
	public String getLocationCanonicalPath() {
		// TODO Auto-generated method stub
		if(whichOne == 1) {
			// sql lite
			return null;
		}
		if(whichOne == 2) {
			return null;
		}
		else /* if(whichOne == 0) */ {
			return mFileHandler.getLocationCanonicalPath() ;
		}
	}

	@Override
	public String getFuzzURIString(String fileName) {
		// TODO Auto-generated method stub
		if(whichOne == 1) {
			// sql lite
			return null;
		}
		if(whichOne == 2) {
			return null;
		}
		else /* if(whichOne == 0) */ {
			return mFileHandler.getFuzzURIString(fileName);
		}
	}

	@Override
	public String getLocationURIString() {
		// TODO Auto-generated method stub
		if(whichOne == 1) {
			// sql lite
			return null;
		}
		if(whichOne == 2) {
			return null;
		}
		else /* if(whichOne == 0) */ {
			return mFileHandler.getLocationURIString();
		}
	}

	@Override
	public void writeFuzzFile(MessageWriter outputMessage) {
		// TODO Auto-generated method stub
		if(whichOne == 1) {
			// sql lite
			
		}
		if(whichOne == 2) {
			
		}
		else /* if(whichOne == 0) */ {
			mFileHandler.writeFuzzFile(outputMessage) ;
		}


	}

}
