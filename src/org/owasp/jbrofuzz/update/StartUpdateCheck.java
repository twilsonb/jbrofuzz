package org.owasp.jbrofuzz.update;

import javax.swing.SwingWorker;

import org.owasp.jbrofuzz.ui.JBroFuzzWindow;

//Check for an updated version
public final class StartUpdateCheck extends SwingWorker<String, Object> {

	private JBroFuzzWindow mWindow;
	
	public StartUpdateCheck(final JBroFuzzWindow mWindow) {
		this.mWindow = mWindow;
	}
	
	@Override
	public String doInBackground() {

		new StartUpdateChecker(mWindow);
		return "done-checking-updates";

	}

	@Override
	protected void done() {

	}
}