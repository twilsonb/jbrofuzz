package org.owasp.jbrofuzz.encode;

import javax.swing.JPanel;
import javax.swing.JLabel;

public class CommentJPanel extends JPanel {
	private JLabel comment;
	
	public CommentJPanel() {
		comment = new JLabel();
		add(comment);
	}
}
