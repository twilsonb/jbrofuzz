package org.owasp.jbrofuzz.core;

public class NoSuchFuzzerException extends Exception {

	
	public NoSuchFuzzerException(String message) {

       super(message);
       
    }
 }