package org.owasp.jbrofuzz.ui.headers;

public class Header {
	
	private int id, noOfFields;
	
	private String header, comment;

	/**
	 * @param id
	 * @param noOfFields
	 * @param header
	 * @param comment
	 */
	public Header(int id, int noOfFields, String header, String comment) {
		this.id = id;
		this.noOfFields = noOfFields;
		this.header = header;
		this.comment = comment;
	}
	
	/**
	 * @param id
	 */
	public Header(int id) {
		this.id = id;
		this.noOfFields = 0;
		this.header = "";
		this.comment = "";
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the noOfFields
	 */
	public int getNoOfFields() {
		return noOfFields;
	}

	/**
	 * @param noOfFields the noOfFields to set
	 */
	public void setNoOfFields(int noOfFields) {
		this.noOfFields = noOfFields;
	}

	/**
	 * @return the value
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @param value the value to set
	 */
	public void setHeader(String value) {
		this.header = value;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	protected void setComment(String comment) {
		this.comment = comment;
	}
	
	protected String getInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nTotal No of Characters: ");
		sb.append(header.length());
		sb.append("\nTotal No of Fields (lines): ");
		sb.append(header.split("\n").length);
		return sb.toString();
	}

}
