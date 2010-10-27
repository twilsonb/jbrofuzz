package org.owasp.jbrofuzz.db;

import java.io.IOException;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.encode.EncoderHashCore;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

/**
 * connector to a couchDB instance.
 * 
 * @author daemonmidi@gmail.com
 * @since version 2.5
 *
 */
public class CouchDBHandler{
	private String protocol = "";
	private String host = "";
	private int port = -1;
	
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
	
	
	/**
	 * http-GET for rest interface to couchDB
	 * @author daemonmidi@gmail.com
	 * @since version 2.6
	 * @param url
	 * @return String responseBody
	 */
	private String sendGet(String url){
		String responseBody = "";
		HttpState state = new HttpState();
		org.apache.commons.httpclient.HttpConnection conn =  createConnection();
		GetMethod method = new GetMethod();
		try {
			method.execute(state, conn);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
	    return responseBody;
	  }
	
	
	/**
	 * put-method for rest interface to couchdb
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param url
	 * @return string responesBody
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("deprecation")
	private String sendPut(String url, String requestBody){
		String responseBody = "";
		HttpState state = new HttpState();
		org.apache.commons.httpclient.HttpConnection conn =  createConnection();
		PutMethod method = new PutMethod(url);
	    method.getParams().setParameter("retryHandler", new DefaultHttpRequestRetryHandler(3, false));
	    method.setRequestBody(requestBody);
	   	try {
			method.execute(state, conn);
			responseBody = method.getResponseBodyAsString();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			conn.close();
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
		HttpState state = new HttpState();
		org.apache.commons.httpclient.HttpConnection conn =  createConnection();
	    DeleteMethod method = new DeleteMethod(url);
	    method.getParams().setParameter("retryHandler", new DefaultHttpRequestRetryHandler(3, false));
	   	try {
			method.execute(state, conn);
			responseBody = method.getResponseBodyAsString();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			conn.close();
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
	 * proxy integration for connection to couchdb
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @return HttpConnection
	 */
	private org.apache.commons.httpclient.HttpConnection  createConnection(){
		org.apache.commons.httpclient.HttpConnection connection = null;
		
			final boolean proxyEnabled = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.DBSETTINGS[0].getId(), false);
			if(proxyEnabled) {
				
				final String proxy = JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[1].getId(), "");
				final int port = JBroFuzz.PREFS.getInt(JBroFuzzPrefs.DBSETTINGS[2].getId(), -1);
				
				HostConfiguration hc = new HostConfiguration();
				hc.setHost(getHost(), getPort());
				hc.setProxy(proxy, port);
				connection = new org.apache.commons.httpclient.HttpConnection(hc);
				
				// Username:Password, yawn
				final boolean proxyReqAuth = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.UPDATE[3].getId(), false);
				if(proxyReqAuth) {
					final String user = JBroFuzz.PREFS.get(JBroFuzzPrefs.UPDATE[5].getId(), "");
					final String pass = JBroFuzz.PREFS.get(JBroFuzzPrefs.UPDATE[6].getId(), "");
					final String encodedPassword = EncoderHashCore.encode(user + ":" + pass, "Base64");
					HttpConnectionParams params = new HttpConnectionParams();
					params.setParameter("Proxy-Authorization", "Basic " + encodedPassword );
					connection.setParams(params);	
				}
			} else {
				connection = new org.apache.commons.httpclient.HttpConnection(getHost(), getPort());
			}
		return connection;
	}
	
	
	/**
	 * creates initial DB
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param dbName
	 * @return resultCode | > 0 OK | < 0 failed.
	 * @throws Exception
	 */
	public int createDB(String dbName){
		int returnCode = 0;
		String response = "";
		if (evaluateConfiguration() > 0){
			String url = getProtocol() + "://" + getHost() + ":" + getPort() + "/" + dbName;
			response = sendPut(url, "");
		}
		else{
			returnCode = -1;
		}
		if (response.length() == 0 || response.toLowerCase().contains("error")) returnCode = -1;
		return returnCode;
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
	 * create initial document to store data in
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param documentId
	 * @return int | > 0 OK | < 0 failed
	 */
	public int createDocument(String dbName, String documentId){
		int returnValue = 0;
		String response = "";
		if (evaluateConfiguration() > 0){
			String url = getProtocol() + "://" + getHost() + ":" + getPort() + "/" + dbName + "/" + documentId;
			response = sendPut(url, "");
		}
		if (response.length() == 0 || response.toLowerCase().contains("error")) returnValue = -1;
		return returnValue;
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
	 * get UUID for documentId
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @return int | > 0 OK | < 0 failed
	 */
	public String getUUID(){
		String uuid = "";
		if (evaluateConfiguration() > 0){
			String url = getProtocol() + "://" + getHost() + ":" + getPort() + "/_uuids";
			uuid = sendPut(url, "");
		}
		if (uuid.toLowerCase().contains("error")) uuid = "";
		return uuid;
	}
	
	
	/**
	 * update document with new data
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param documentId
	 * @return response from DB as String
	 */
	public String updateDocument(String dbName, String documentId, JSONObject document){
		String response = "";
		String body = "";
		if (evaluateConfiguration() > 0){
			String revision = getDocumentRevision(dbName, documentId);
			String url = getProtocol() + "://" + getHost() + ":" + getPort() + "/" + dbName;
			try {
				document.append("_rev", revision);
				body = document.toString(); 
			} catch (JSONException e) {
				e.printStackTrace();
			}
			response = sendPut(url, body);
		}
		return response;
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
			String url = getProtocol() + "://" + getHost() + ":" + getPort() + dbName + "/" + documentId; 
			response = sendGet(url);
			try {
				JSONObject jObject = new JSONObject(response);
				if (jObject.get("_rev") != null){
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