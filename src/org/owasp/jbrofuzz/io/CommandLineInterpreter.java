/**
 * JBroFuzz 2.4
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007 - 2010 subere@uncon.org
 *
 * This file is part of JBroFuzz.
 * 
 * JBroFuzz is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JBroFuzz is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JBroFuzz.  If not, see <http://www.gnu.org/licenses/>.
 * Alternatively, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * Verbatim copying and distribution of this entire program file is 
 * permitted in any medium without royalty provided this notice 
 * is preserved. 
 * 
 */
package org.owasp.jbrofuzz.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.core.Database;
import org.owasp.jbrofuzz.core.NoSuchFuzzerException;
import org.owasp.jbrofuzz.fuzz.io.OpenSession;
import org.owasp.jbrofuzz.ui.AbstractPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;

public class CommandLineInterpreter{

	private JBroFuzzWindow mWindow;
	private OpenSession os;
	
	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.4
	 * @param String [] args   - commandline arguments
	 * @return int resultCode  - > 0 == ok and < 0 == failed
	 * @throws NoSuchFuzzerException 
	 */
	public int process(String [] args) throws NoSuchFuzzerException{
		String inputFileName = "";
		String url = "";
		String request = "";
		String fuzzers = "";
		String encoder = "";
		String prefix = "";
		String suffix = "";
		int fuzzersStart = -1;
		int fuzzersEnd = -1;
		int fuzzerNumber = -1;
		int returnValue = -1; // everything went wrong
		boolean result = false;
		boolean doNotFuzz = true; // stealth mode :-)

		/*
		 * Linux commandline interface style 
		 * arguments must start with a '-' followed
		 * by the shortcut for the argument
		 * -i = --input == inputFileName will follow
		 * order of arguments doesn't matter
		 */
		int j = 0;
		while (j < args.length){
			if (args[j].equals("-dDB") || args[j].equals("--dumpDB")){
				//TODO Output as one as plain text followed by xml later on
			}
			if (args[j].equals("-e") || args[j].equals("--encoder")){
				encoder = args[j +1];
			}
			if (args[j].equals("-f") || args[j].equals("--fuzzer")){
				fuzzers = args[j + 1];
			}
			if (args[j].equals("-fe") || args[j].equals("--fuzzerEnd")){
				try{
					fuzzersEnd = Integer.valueOf(args[j +1]);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
			if (args[j].equals("-fs") || args[j].equals("--fuzzerStart")){
				try{
					fuzzersStart = Integer.valueOf(args[j + 1]);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
			if (args[j].equals("-fn") || args[j].equals("--fuzzerNumber")){
				try{
					fuzzerNumber = Integer.valueOf(args[j +1]);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			if (args[j].equals("-h") || args[j].equals("--help")){
				printHelp();
				System.exit(0);
			}
			else if (args[j].equals("-i") || args[j].equals("--input")){
				inputFileName = args[j+1];
			}
			else if (args[j].equals("-n") || args[j].equals("--no-execute")){
				returnValue = -1;
				return returnValue;
			}
			else if (args[j].equals("-p") || args[j].equals("--perfix")){
				prefix = args[j +1];
			}
			else if (args[j].equals("-r") || args[j].equals("--request")){
				request = args[j + 1 ];
			}
			else if (args[j].equals("-R") || args[j].equals("--run")){
				doNotFuzz = false;
			}
			else if (args[j].equals("-s") || args[j].equals("--showResults")){
				result = true;
			}
			else if (args[j].equals("-su") || args[j].equals("--suffix")){
				suffix = args[j + 1];
			}
			else if (args[j].equals("-u") || args[j].equals("--url")){
				url = args[j + 1];
			}
			else{
				if(j>0){
					if(!(args[j-1].equals("-i") || args[j-1].equals("--input") || args[j-1].equals("-o") || args[j-1].equals("--output"))){
						System.out.println("jbrofuzz: Unrecognized option '"+args[j]+"'");
						printHelp();
						System.exit(0);
					}
				}
			}
			j++;
		}

		mWindow = new JBroFuzzWindow(new JBroFuzz());

		if (inputFileName.length() > 0 && !inputFileName.equals("")){
			// setup new session
			os = new OpenSession(mWindow, inputFileName);

		}	

		// need new session but do not have an inputfile
		
		if (url.length() > 0 || !url.endsWith("")){
			mWindow.getPanelFuzzing().setTextURL(url);
		}
		
		if (request.length() > 0 || !request.endsWith("")){
			mWindow.getPanelFuzzing().setTextRequest(request);
		}
		
		if (fuzzers.length() > 0 || !fuzzers.endsWith("") && (fuzzersStart >= 0 && fuzzersEnd >= 0 && fuzzersEnd >= fuzzersStart)){
			Database db = new Database();
			mWindow.getJBroFuzz().setDatabase(db);
			String fuzzerId = mWindow.getJBroFuzz().getDatabase().getIdFromName(fuzzers);
			if (fuzzerId.length() == 0){
				throw new NoSuchFuzzerException(fuzzers);
			}
			mWindow.getPanelFuzzing().addFuzzer(fuzzerId, fuzzersStart, fuzzersEnd);
		}
		
		if (fuzzerNumber > 0 && encoder.length() > 0 && suffix.length() > 0 && prefix.length() >0){
			mWindow.getPanelFuzzing().addTransform(fuzzerNumber, encoder, prefix, suffix);
		}

		// start fuzzing
		if(!doNotFuzz){
			final int c = mWindow.getTp().getSelectedIndex();
			AbstractPanel p = (AbstractPanel) mWindow.getTp().getComponent(c);
			p = (AbstractPanel) mWindow.getTp().getComponent(c);
			p.start();
			p.stop();
			if (mWindow.getPanelFuzzing().isStopped()){
				p.stop();
			}
		}
		else{
			returnValue = -1;
		}
		
		// write the results to output file
		// tbd. see writeOutputfile() for further info;
		// if (outputFileName.length() > 0 || !outputFileName.equals("")) returnValue = writeOutputFile(mWindow, outputFileName);	
		// open result window for further analysis
		if (result){ 
			JBroFuzzWindow.createAndShowGUI(os.getmWindow());
		}
		else{
			// no commandline options
			JBroFuzzWindow.createAndShowGUI(mWindow);
		}
		return returnValue;
	}

	/**
	 * Print commandline help
	 * @author daemonmidi
	 * @version 2.4
	 * @since 2.4
	 */
	public void printHelp(){
		String helpfileloc = "help/command-line-help.txt";
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			BufferedReader a = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(helpfileloc)));
			while((line = a.readLine()) != null){
				sb.append(line+"\n");
			}
			System.out.println(sb.toString());
		} catch (IOException e) {
			System.out.println("Help file not found");
			System.exit(0);
		}
	}
}