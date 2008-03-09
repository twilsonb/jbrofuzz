package org.owasp.jbrofuzz.core;

public class NoSuchFuzzerException extends Exception {

	private static final long serialVersionUID = 8529955831182129925L;

	public NoSuchFuzzerException(String message) {

       super(message);
       
    }
	
 }