package org.owasp.jbrofuzz3.message;

import java.util.ArrayList;

import org.owasp.jbrofuzz.core.Fuzzer;

public class FuzzRequest extends Request {

	public FuzzRequest(String message) {
		super(message);
	}
	
	public FuzzRequest(String message, ArrayList<FuzzPoint> fuzzPoints) {
		super(message);
	}
	
	public String getRequest(FuzzList playList) {
		return " ";
	}
}
