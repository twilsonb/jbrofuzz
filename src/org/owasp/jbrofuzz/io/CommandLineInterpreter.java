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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.fuzz.io.OpenSession;
import org.owasp.jbrofuzz.fuzz.io.Save;
import org.owasp.jbrofuzz.fuzz.io.SaveSession;
import org.owasp.jbrofuzz.ui.AbstractPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;

public class CommandLineInterpreter{

	private JBroFuzzWindow mWindow;

	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.4
	 * @param String [] args   - commandline arguments
	 * @return int resultCode  - > 0 == ok and < 0 == failed
	 */
	public int process(String [] args){
		String inputFileName = "";
		int returnValue = 1; // everything went fine
		boolean result = false;
		boolean doNotFuzz = false;

		/*
		 * Linux commandline interface style 
		 * arguments must start with a '-' followed
		 * by the shortcut for the argument
		 * -i = --input == inputFileName will follow
		 * -o = --output == outputFileName will follow
		 * order of arguments doesn't matter
		 */
		int j = 0;
		while (j < args.length){
			if (args[j].equals("-h") || args[j].equals("--help")){
				printHelp();
				System.exit(0);
			}
			else if (args[j].equals("-i") || args[j].equals("--input")){
				inputFileName = args[j+1];
			}
			else if (args[j].equals("-o") || args[j].equals("--output")){
//				outputFileName = args[j+1];
			}
			else if (args[j].equals("-s") || args[j].equals("--showResults")){
				result = true;
			}
			else if (args[j].equals("-n") || args[j].equals("--no-execute")){
				doNotFuzz = true;
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
			OpenSession os = new OpenSession(mWindow, inputFileName);

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
		

			// write the results to output file
			// tbd. see writeOutputfile() for further info;
			// if (outputFileName.length() > 0 || !outputFileName.equals("")) returnValue = writeOutputFile(mWindow, outputFileName);

			// open result window for further analysis
			if (result) JBroFuzzWindow.createAndShowGUI(os.getmWindow());
		}
		else{
			// no commandline options
			JBroFuzzWindow.createAndShowGUI(mWindow);
		}
		return returnValue;
	}

	/**
	 * <p>Method for writing a file, given the window and 
	 * corresponding filename.</p>
	 * <p>This method needs revisiting, as I don't understand it.</p>
	 * 
	 * @param mWindow
	 * @param fileName
	 * @return returnCode int => > 0 == ok and < 0 == failed
	 * 
	 * @author daemonmidi@gmail.com, subere@uncon.org
	 * @version 2.5
	 * @since 2.4
	 */
	public int writeOutputFile(JBroFuzzWindow mWindow, String fileName){
		// Write requests and response into seperate file
		// tbd.
		
		File myFile = new File(fileName);
		
		if (mWindow.getPanelFuzzing().isStopped()){
			Save.writeFile(myFile, mWindow);
		}

		System.out.println("jbrofuzz: Fuzzing Session finished. Output written to: " + fileName);
		return 0;
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