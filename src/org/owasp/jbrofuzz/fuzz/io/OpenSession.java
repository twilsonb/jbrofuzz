package org.owasp.jbrofuzz.fuzz.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.ExampleFileFilter;

public class OpenSession {

	// The maximum number of chars to be read from file, regardless
	private final static int MAX_CHARS = 16384;
		
	public OpenSession(JBroFuzzWindow mWindow) {
		
		mWindow.log("Open Fuzzing Session");

		ExampleFileFilter filter = new ExampleFileFilter("jbrofuzz");
		filter.setDescription("JBroFuzz File Format");

		JFileChooser fc = new JFileChooser(System.getProperty("user.home"));
		fc.setFileFilter(filter);

		int returnVal = fc.showOpenDialog(mWindow);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			File file = fc.getSelectedFile();
			mWindow.log("Opening: " + file.getName());

			String path = file.getAbsolutePath().toLowerCase();
			// If the file does not end in .jbrofuzz, return
			if (!path.endsWith(".jbrofuzz") ) {
				
				JOptionPane.showMessageDialog(fc,
						"The file selected is not a valid .jbrofuzz file",
						"Open", JOptionPane.WARNING_MESSAGE);
				return;
			}
		
			// Start opening the file 
			final StringBuffer fileContents = new StringBuffer();

			try {
				BufferedReader in = new BufferedReader(new FileReader(file));
				
				int counter = 0;
				int c;
				while( ((c = in.read()) > 0) && (counter < MAX_CHARS) ) {
					fileContents.append((char) c);
					counter++;
				}
				
			} catch (FileNotFoundException e) {
				mWindow.log("FileNotFoundException");
			} catch (IOException e) {
				mWindow.log("IOException");
			}
			
			// Validate it to extremes
			final String[] fileInput = fileContents.toString().split("\n");
			final int len = fileInput.length;
			
			// Check the number of lines
			if(len < 8) return;
			// Check the location of each of the fields
			if(!fileInput[0].equals("[JBroFuzz]")) return;
			if(!fileInput[2].equals("[Fuzzing]")) return;			
			if(!fileInput[4].equals("[URL]")) return;			
			if(!fileInput[6].equals("[Request]")) return;
			// Check that the file finishes with an 'End'
			if(!fileInput[len - 1].equals("[End]")) return;
			
			// Find the line where the 'Payloads' are
			int payloadsLine = 0;
			for(int i = len - 1; i >= 0; i--) {
				
				if(fileInput[i].equals("[Payloads]")) {
					// Check that there is only 1 instance
					if(payloadsLine != 0) {
						return;
					}
					else {
						payloadsLine = i;
					}
					
				}
				
			}
			
			// If you can't find the 'Payloads' line, return
			if(payloadsLine == 0) return;
			
			// Get the request from the file
			StringBuffer _reqBuffer = new StringBuffer();
			for(int i = 7; i < payloadsLine; i++) {
				_reqBuffer.append(fileInput[i] + "\n");
			}
			
			// If the number of available payload lines is greater than 1024, return
			if(len - 1 - payloadsLine - 1 > 1024) return;
			
			// Get the payloads from the file
			for(int i = payloadsLine + 1; i < len - 1; i++) {
				
				boolean fuzzer_happy = true;

				String [] payloadArray = fileInput[i].split(",");
				// Each line must have 3 elements
				if(payloadArray.length != 3) {
					return;
				}

				String fuzz_id = payloadArray[0];
				int start = 0;
				int end = 0;
				// The fuzzer id must also exist in the database
				if(!mWindow.getJBroFuzz().getDatabase().containsGenerator(fuzz_id)) {
					fuzzer_happy = false;
				}

				// The start and end integers should be happy
				try {
					start = Integer.parseInt(payloadArray[1]);
					end = Integer.parseInt(payloadArray[2]);
					// Numbers must be positive
					if( (start < 0) || (end < 0) ) {
						fuzzer_happy = false;
					}
					// Numbers must be less than the length of the request
					if( (start > _reqBuffer.length()) || (end > _reqBuffer.length()) ) {
						fuzzer_happy = false;
					}
				} catch (NumberFormatException e) {
					fuzzer_happy = false;
				}
				
				if(!fuzzer_happy) {
					mWindow.log("Could not open and add Fuzzer: " + fileInput[i]);
				}
				else {
					mWindow.getPanelFuzzing().addPayload(fuzz_id, start, end);
				}
			}
			
			// These max values of abbreviation are also used in the Fuzzing Panel geters
			String _req = StringUtils.abbreviate(_reqBuffer.toString(), 16384);
			String _url = StringUtils.abbreviate(fileInput[5], 1024);
			
			mWindow.getPanelFuzzing().setTextRequest(_req);
			mWindow.getPanelFuzzing().setTextURL(_url);
			
			System.out.println("Left to the field now, looking good!\n" + _req + "\n--\n" + _url + "\n--");
			
		}

	}
}
