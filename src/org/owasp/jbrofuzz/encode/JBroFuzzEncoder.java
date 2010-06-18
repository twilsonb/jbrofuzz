package org.owasp.jbrofuzz.encode;

import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>Filename: JBroFuzzEncoder.java</p>
 * 
 * <p>Description: This class launches JBroFuzzEncoder.</p>
 * 
 * @author subere@uncon.org
 * @version 2.3
 * @since 2.3
 */
public class JBroFuzzEncoder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				new JBroFuzzFormat();
				new EncoderHashFrame();

			}

		});
	}

}
