package org.owasp.jbrofuzz.core;

import java.util.*;
import java.math.*;

import org.apache.commons.lang.*;

public class Fuzzer implements Iterator<String> {

	private int len;
	
	private Database database;
	
	private Generator generator;
	
	private ArrayList<String> payloads;
	
	private BigInteger cValue, maxValue;

	
	public Fuzzer(String generator, int len) throws NoSuchFuzzerException {
		
		this.database = new Database();
		if( !database.containsGenerator(generator) ) {
			
			throw new NoSuchFuzzerException(StringUtils.abbreviate(generator, 10) + " : No Such Fuzzer Found in the Database ");
			
		}
		this.generator = database.getGenerator(generator);
		
		if(generator != null) {
			
			System.out.println("Generator Found!");
			
			payloads = this.generator.getPayloads();
			if(this.generator.isReplasive()) {
				maxValue = new BigInteger("" + payloads.size());
			}
			else {
				maxValue = new BigInteger("" + payloads.size());
				maxValue = maxValue.pow(len < 0 ? 0 : len);
			}
			
		}
		else {
			maxValue = new BigInteger("0");
		}
		
		cValue = new BigInteger("1");
		this.len = len;
		
	}
	
	public String getName() {
		
		return generator.getName();
		
	}
	
	public String getId() {
		
		return generator.getId();
		
	}
	
	public boolean hasNext() {
		
		if(cValue.compareTo(maxValue) < 0) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
	public String next() {
		
		StringBuffer output = new StringBuffer("");
		
		// Replasive Generator
		if(maxValue.compareTo(new BigInteger("" + payloads.size() )) == 0) {
			
			output.append(payloads.get( cValue.intValue() ));
			cValue = cValue.add(new BigInteger("1"));
			
		}
		// Recursive Generator
		else {
			
			BigInteger val = cValue;
			// Perform division on a stack
			Stack<BigInteger> stack = new Stack<BigInteger>();
			while(val.compareTo(new BigInteger("" + payloads.size())) >= 0) {
			
			stack.push(new BigInteger("" + (val.mod(new BigInteger("" + payloads.size())) )));
			val = val.divide(new BigInteger("" + payloads.size()));
			
			}
			// Append the relevant empty positions with the first element identified
			output.append( StringUtils.leftPad(payloads.get((val.intValue()) ), len - stack.size(), payloads.get(0)) );
			while(!stack.isEmpty())
				output.append(payloads.get( (stack.pop()).intValue()));
			
			cValue = cValue.add(new BigInteger("1"));
			
		}
	
		return output.toString();
		
	}
	
	public void remove() {
		
		cValue = cValue.add(new BigInteger("1"));
		
	}
	
	public String getCurrectValue() {
		
		return cValue.toString();
		
	}
	
	public String getMaximumValue() {
		
		return maxValue.toString();
		
	}
		
}
