package org.owasp.jbrofuzz.fuzz.ui;



/**
 * <p>
 * An Encoders row which represents values of the data help within each row.
 * </p>
 * 
 * @author ragreen
 * @since 2.3
 *
 */
public class EncodersRow {
	
	private String encoder;
	private String prefixOrMatch;
	private String suffixOrReplace;

	
	public EncodersRow(String encoder, String prefix, String suffix){
		this.setEncoder(encoder);
		this.setPrefixOrMatch(prefix);
		this.setSuffixOrReplace(suffix);
	}


	public void setEncoder(String encoder) {
		this.encoder = encoder;
	}


	public String getEncoder() {
		return encoder;
	}


	public void setPrefixOrMatch(String prefixOrMatch) {
		this.prefixOrMatch = prefixOrMatch;
	}


	public String getPrefixOrMatch() {
		return prefixOrMatch;
	}


	public void setSuffixOrReplace(String suffixOrReplace) {
		this.suffixOrReplace = suffixOrReplace;
	}


	public String getSuffixOrReplace() {
		return suffixOrReplace;
	}
	
	
}
