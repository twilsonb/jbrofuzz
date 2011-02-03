package org.owasp.jbrofuzz.fuzz.ui;

import org.owasp.jbrofuzz.encode.EncoderHashCore;



/**
 * <p>
 * An Encoders row which represents values of the data help within each row.
 * </p>
 * 
 * @author ragreen
 * @since 2.3
 *s
 */
public class TransformsRow {
	
	private String encoder;
	private String prefixOrMatch;
	private String suffixOrReplace;
	
	public TransformsRow(String encoder, String prefix, String suffix){
		this.setEncoder(encoder);
		this.setPrefixOrMatch(prefix);
		this.setSuffixOrReplace(suffix);
	}
	
	public TransformsRow(){
		encoder = EncoderHashCore.CODES[0];
		prefixOrMatch = "";
		suffixOrReplace = "";
		
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
	
	public String toString(){
		return "Encoder: " + encoder + " Preffix/Match: " + prefixOrMatch + " Suffix/Replace: " + suffixOrReplace;
	}
	
	
}
