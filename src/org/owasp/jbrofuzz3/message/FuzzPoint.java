package org.owasp.jbrofuzz3.message;

/**
 * <p>A fuzz point is a position of set length
 * within a message.</p>
 * 
 * <p>A fuzz point indexes a message in the same
 * way as a String.charAt() indexes a String.
 *  
 * @author subere@uncon.org
 * @version 2.5
 * @since 2.5
 *
 */
public class FuzzPoint {
	
	private int start, end;

	/**
	 * <p>Create a fuzz point, which starts
	 * at position <start> within a message
	 * and ends at position <end>.</p>
	 * 
	 * <p>These two numbers must be greater 
	 * or equal to zero, cannot be equal
	 * and end has to be larger than start.</p>
	 * 
	 * {@link isValid}
	 */
	public FuzzPoint(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
	/**
	 * <p>These two numbers must be greater 
	 * or equal to zero, cannot be equal
	 * and end has to be larger than start.</p>
	 * 
	 */
	public boolean isValid() {
		
		if( (start >= 0) && (start < end) ) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <p>These two numbers must be greater 
	 * or equal to zero, cannot be equal
	 * and end has to be larger than start.</p>
	 * 
	 * <p>Also, end must be less than or equal
	 * to the total message length, as obtained
	 * by String.legth().</p> 
	 */
	public boolean isValid(int messageLength) {
		
		if( (start >= 0) && (start < end) && (end <= messageLength) ) {
			return true;
		} else {
			return false;
		}
	}
}
