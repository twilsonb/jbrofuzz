package org.owasp.jbrofuzz.io;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.db.DBAdaptor;
import org.owasp.jbrofuzz.db.DBAdaptorFactory;
import org.owasp.jbrofuzz.fuzz.MessageWriter;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

public class StorageHandler implements StorageInterface {

	private FileHandler mFileHandler;
	private DBAdaptor mdbAdaptor;
		
	public StorageHandler() {
	
		final String dbType = JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[11].getId(), "-1");
		
		if(dbType.equals("SQLite") || dbType.equals("CouchDB")){
			mdbAdaptor =  DBAdaptorFactory.getInstance();
			}
		else /*(dbType.equals("None") ) */{
			mFileHandler = new FileHandler();
		}
		
	}
	
	@Override
	public void createNewLocation() {
		// TODO Auto-generated method stub
		if(mFileHandler != null) {
			mFileHandler.createNewLocation();
		}

	}

	@Override
	public String getLocationCanonicalPath() {
		String canonicalPath = "";
		if (mFileHandler != null){
			canonicalPath =  mFileHandler.getLocationCanonicalPath() ;
		}
		return canonicalPath;
	}

	@Override
	public String getFuzzURIString(String fileName) {
		// TODO Auto-generated method stub
		String fuzzerURI = "";
		if (mFileHandler != null){
			fuzzerURI =  mFileHandler.getFuzzURIString(fileName);
		}
		return fuzzerURI;
	}

	@Override
	public String getLocationURIString() {
		String locationURI = "";
		if (mFileHandler != null){
			locationURI = mFileHandler.getLocationURIString();
		}
		return locationURI;
	}

	@Override
	public void writeFuzzFile(MessageWriter outputMessage) {
		if (mFileHandler != null) {
			Logger.log("storing data to file", 0);
			mFileHandler.writeFuzzFile(outputMessage) ;
		}
		if (mdbAdaptor != null){
			Logger.log("stroring data to database", 0);
			mdbAdaptor.store(outputMessage);
		}
	}
}