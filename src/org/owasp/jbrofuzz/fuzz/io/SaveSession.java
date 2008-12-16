package org.owasp.jbrofuzz.fuzz.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.ExampleFileFilter;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

public class SaveSession {

	public SaveSession(final JBroFuzzWindow mWindow) {

		mWindow.log("Save Fuzzing Session");

		ExampleFileFilter filter = new ExampleFileFilter("jbrofuzz");
		filter.setDescription("JBroFuzz File Format");

		JFileChooser fc = new JFileChooser(System.getProperty("user.home"));
		fc.setFileFilter(filter);

		int returnVal = fc.showSaveDialog(mWindow);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			File file = fc.getSelectedFile();
			mWindow.log("Saving: " + file.getName());

			String path = file.getAbsolutePath ().toLowerCase ();
			if (!path.endsWith(".jbrofuzz") )
				file = new File (path += ".jbrofuzz");

			if (file.exists()) {
				int choice =  JOptionPane.
				showConfirmDialog (fc,
						"File already exists. Do you \nwant to replace it?",
						"Save",
						JOptionPane.YES_NO_OPTION);

				if (choice == JOptionPane.NO_OPTION)
					return;
			}
			
			// Get the values from the frame
			String _url = mWindow.getPanelFuzzing().getTextURL();
			String _req = mWindow.getPanelFuzzing().getTextRequest();
			String _pld = mWindow.getPanelFuzzing().getTextPayloads();
			
			// Write the file
			try {
				
				PrintWriter out = new PrintWriter(file);
				
				out.print("[JBroFuzz]\n");
				out.print(JBroFuzzFormat.VERSION + "\n");
				out.print("[Fuzzing]\n");
				out.print(JBroFuzzFormat.DATE + "\n");
				out.print("[URL]\n");
				out.print(_url + "\n");
				out.print("[Request]\n");
				out.print(_req + "\n");
				out.print("[Payloads]\n");
				out.print(_pld + "\n");
				out.print("[End]\n");
				
				if(out.checkError()) {
					mWindow.log("An Error Occured while saving");
				}
				
				out.close();
				
			} catch (FileNotFoundException e) {
				mWindow.log("FileNotFoundException");
			} catch (SecurityException e) {
				mWindow.log("SecurityException");
			}
		}
			

	}
}
