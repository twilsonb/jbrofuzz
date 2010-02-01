package org.owasp.jbrofuzz.encode;

public final class EncoderHash {
	
	private String id, comment;
	
	private boolean decodable;

	public EncoderHash(String id, String comment, boolean decodable) {
		super();
		this.id = id;
		this.comment = comment;
		this.decodable = decodable;
	}

	public String getId() {
		return id;
	}

	public String getComment() {
		return comment;
	}

	public boolean isDecodable() {
		return decodable;
	}
	
	
	
}
