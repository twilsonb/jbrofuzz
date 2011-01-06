package org.owasp.jbrofuzz3.message;

import java.util.Iterator;

import org.owasp.jbrofuzz.core.Fuzzer;

/**
 * <p>A fuzz list is an ordered collection of 
 * fuzzer.</p>
 * 
 * <p>It has a type of iteration; it also has
 * a type, in terms of selecting how you 
 * iterate through the list.</p>
 * 
 * @author subere@uncon.org
 * @version 2.5
 * @since 2.5
 *
 */
public class FuzzList implements Iterator<Fuzzer[]> {

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Fuzzer [] next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

}
