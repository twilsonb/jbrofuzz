package org.owasp.jbrofuzz.core;

import java.util.*;

public class Generator {
	
	private char type;
	
	private String id;
	
	private String name;
		
	private ArrayList<String> categories;
	
	private ArrayList<String> payloads;
	
	public Generator(char type, String id, String name) {
		
		this(type, id, name, new ArrayList<String>(), new ArrayList<String>());
		
	}
	
	public Generator(char type, String id, String name, ArrayList<String> categories, ArrayList<String> payloads) {
	
		this.type = type;
		this.id = id;
		this.name = name;
		this.categories = categories;
		this.payloads = payloads;
		
	}
	
	public boolean isRecursive() {
		if(type == 'R')
			return true;
		return false;
	}
	
	public boolean isReplasive() {
		if(type == 'P')
			return true;
		return false;
	}


	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int size() {
		return payloads.size();
	}

	public ArrayList<String> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<String> categories) {
		categories.trimToSize();
		this.categories = categories;
	}

	public ArrayList<String> getPayloads() {
		payloads.trimToSize();
		return payloads;
	}

	public void setPayloads(ArrayList<String> payloads) {
		this.payloads = payloads;
	}
	
	
	public void addPayload(String value) {
		
		payloads.add(calculatePayload(value));
		payloads.trimToSize();
		
	}
	
	private static String calculatePayload(String param) {
		
		String beginning;
		try {
			beginning = param.substring(0, 5);
		} catch (IndexOutOfBoundsException e1) {
			return param;
		}
		if (!beginning.startsWith("f(x)=")) {
			return param;
		}

		// Get rid of the first characters
		param = param.substring(5);
		// Chop at x, the variable of f(x)
		final String[] paramArray = param.toString().split(" x ");

		// Check to see if you have two elements
		if (paramArray.length != 2) {
			return param;
		}
		// Define the input string
		final String input = paramArray[0];
		// Define the number of times
		int times;
		try {
			times = Integer.parseInt(paramArray[1]);
		} catch (final NumberFormatException e) {
			times = 1;
		}

		// Check that times is positive
		if (times <= 0) {
			return param;
		}

		final int len = input.length() * times;

		final StringBuffer newBuffer = new StringBuffer(len);
		for (int i = 0; i < times; i++) {
			newBuffer.append(input);
		}
		return newBuffer.toString();
	}




}
