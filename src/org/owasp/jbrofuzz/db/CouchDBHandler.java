package org.owasp.jbrofuzz.db;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;


/**
 * connector to a couchDB instance.
 * 
 * @author daemonmidi@gmail.com
 * @since version 2.5
 *
 */
public class CouchDBHandler{
	
	private String protocol = "http";
	private String host = JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[9].getId(), "");
	private int port = Integer.valueOf(JBroFuzz.PREFS.getInt(JBroFuzzPrefs.DBSETTINGS[10].getId(), 0));
	private static final SimpleDateFormat SD_FORMAT = new SimpleDateFormat(
			"zzz-yyyy-MM-dd-HH-mm-ss-SSS", Locale.ENGLISH);

	
	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param String protocol
	 */
	public void setProtocol(String protocol){
		this.protocol = protocol;
	}
	
	
	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param protocol
	 * @return String
	 */
	public String getProtocol(){
		return this.protocol;
	}
	
	
	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param String host - host to connect to
	 */
	public void setHost(String host){
		this.host = host;
	}
	
	
	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @return String host - host to connect to
	 */
	public String getHost(){
		return this.host;
	}
	
	
	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param int port- port to connect to
	 */
	public void setPort(int port){
		this.port = port;
	}
	
	
	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @return int port - port to connect to
	 */
	public int getPort(){
		return this.port;
	}
	
	
	public String[] getDocumentIds(String dbName){
		//TODO
		String[] documentIds = new String[]{};
		return documentIds;
	}
	
	/**
	 * http-GET for rest interface to couchDB
	 * @author daemonmidi@gmail.com
	 * @since version 2.6
	 * @param url
	 * @return String responseBody
	 */
	private String sendGet(String url){
		String responseBody = "";
		org.apache.commons.httpclient.HttpClient client = new HttpClient();
		client.getParams().setParameter("http.useragent", "jbrofuzz");
		GetMethod method = new GetMethod(url);
		try {
	//		int returnCode = client.executeMethod(method);
			responseBody = method.getResponseBodyAsString();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			client.getHttpConnectionManager().closeIdleConnections(0);
		}
	    return responseBody;
	  }
	
	
	/**
	 * put-method for rest interface to couchdb
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param url
	 * @return string responesBody
	 */
	private String sendPut(String url, String requestBody){
		String responseBody = "";
		org.apache.commons.httpclient.HttpClient client = new HttpClient();
		client.getParams().setParameter("http.useragent", "jbrofuzz");
				
		PutMethod method = new PutMethod(url);
	    // method.getParams().setParameter("retryHandler", new DefaultHttpRequestRetryHandler(3, false));
	    if (requestBody.length() > 0 ) method.setRequestBody(requestBody);
	    
	   	try {
	   		responseBody = method.getResponseBodyAsString();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			client.getHttpConnectionManager().closeIdleConnections(0);
		}
		return responseBody;
	} 
	
	
	/**
	 * http delete for rest interface to couchdb
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param url
	 * @return string repsonsebody
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private String sendDelete(String url){
		String responseBody = "";
	    DeleteMethod method = new DeleteMethod(url);
		org.apache.commons.httpclient.HttpClient client = new HttpClient();
	    client.getParams().setParameter("http.useragent", "jbrofuzz");
	   	try {
			client.executeMethod(method);
			responseBody = method.getResponseBodyAsString();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			client.getHttpConnectionManager().closeIdleConnections(0);
		}
	    return responseBody;
	  }
	
	
	/**
	 * ensure that host and port are set in advance
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @return int | > 0 OK | < 0 failed.
	 */
	private int evaluateConfiguration(){
		if (getHost().equals("") || getHost().length() == 0) return -1;
		if (getProtocol().equals("") || getProtocol().length() == 0) return -1;
		if (getPort() == 0 || getPort() < 0){ 
			return -1;
		}
		else{
			return 1;
		}
	}
	
	/**
	 * creates initial DB
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param dbName
	 * @return dbName | > 0 OK | < 0 failed.
	 * @throws Exception
	 */
	public String createDB(String dbName){
		String response = "";
		String dbNameReal = "";
		if (dbName.length() < 0 || dbName.equals("")){
			Date dat = new Date();
			dbNameReal = "jbrfuzz_" + dat.getTime(); 
		}
		else{
			dbNameReal = dbName;
		}
		if (evaluateConfiguration() > 0){
			String url = getProtocol() + "://" + getHost() + ":" + getPort() + "/" + dbNameReal;
			response = sendPut(url, "");
		}
		else{
			dbNameReal = "-1";
		}
		if (response.length() == 0 || response.toLowerCase().contains("error")) dbNameReal = "-1";
		return dbNameReal;
	}
	
	
	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param dbName
	 * @return int | > 0 OK | < 0 failed
	 */
	public int removeDB(String dbName){
		int returnCode = 1;
		String response = "";
		if (evaluateConfiguration() > 0){
			String url = getProtocol() + "://" + getHost() + ":" + getPort() + "/" + dbName;
			response = sendDelete(url);
			if (response.toLowerCase().contains("error")) returnCode =  -1;
		}
		return returnCode;
	}
	
	
	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param dbName
	 * @param documentId
	 * @return returCode | > 0 OK | < 0 failed.
	 */
	public int removeDocument(String dbName, String documentId){
		int returnCode = 0;
		String response ="";
		if (evaluateConfiguration() > 0){
			String url = getProtocol() + "://" + getHost() + ":" + getPort() + "/" + dbName + "/" + documentId;
			response = sendDelete(url);
		}
		if (response.toLowerCase().contains("error")) returnCode = -1;
		return returnCode;
	}
		
	/**
	 * update document with new data
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param documentId
	 * @return response from DB as String
	 */
	public int store(String dbName, String documentId, JSONObject document){
		int returnCode = 0;
		String response = "";
		String body = "";
		if (documentId.length() < 0 || documentId.equals("")){
			Date dat = new Date();
			documentId = SD_FORMAT.format(dat);
		}
		String url = getProtocol() + "://" + getHost() + ":" + getPort() + "/" + dbName + "/" + documentId;
		if (evaluateConfiguration() > 0){
			String revision = getDocumentRevision(dbName, documentId);
			try {
				if (revision.length() > 0){
					document.put("_rev", revision);
				}
				body = document.toString(); 
				response = sendPut(url, body);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (response.toLowerCase().contains("error")){
			returnCode = 1;
		}
		return returnCode;
	}
	
	
	/**
	 * return current document revision in DB
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param dbName
	 * @param documentId
	 * @return document revision as string
	 */
	private String getDocumentRevision(String dbName, String documentId){
		String response = "";
		String revId = "";
		if (evaluateConfiguration() > 0){
			String url = getProtocol() + "://" + getHost() + ":" + getPort() + "/" + dbName + "/" + documentId; 
			response = sendGet(url);
			try {
				JSONObject jObject = new JSONObject(response);
				if (jObject.has("_rev")){
					revId = jObject.getString("_rev");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return revId;
	}
	
	
	/**
	 * get whole document from db
	 * @author daemonmidi@gmail.com
	 * @param documentId
	 * @return String == document
	 */
	public String getDocument(String dbName, String documentId){
		String document = "";
		if (evaluateConfiguration() > 0){
			String url = getProtocol() + "://" + getHost() + ":" + getPort() + "/" + dbName + "/" + documentId;
			document = sendGet(url);
		}
		return document;
	}
	
	
	/**
	 * get result of querying view
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param viewURL
	 * @return String response, empty when failed
	 */
	public String getView(String viewName, String dbName, String documentId){
		String view = "";
		if (evaluateConfiguration() > 0){
			String url = getProtocol() + "://" + getHost() + ":" + getPort() + "/" + dbName + "/" + documentId + "/_view/" + viewName;
			view = sendGet(url);
		}
		return view;
	}
	
	
	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param listURL
	 * @return list response, empty when failed
	 */
	public String getList(String listName, String view, String dbName, String documentId){
		String list = "";
		if (evaluateConfiguration() > 0){
			String url = getProtocol() + "://" + getHost() + ":" + getPort() + "/" + dbName + "/" + documentId + "/_list/" + listName + "_view/" + view;
			list = sendGet(url);
		}
		return list;
	}
}