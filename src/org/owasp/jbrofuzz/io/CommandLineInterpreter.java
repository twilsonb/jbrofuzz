package org.owasp.jbrofuzz.io;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.core.Database;
import org.owasp.jbrofuzz.fuzz.io.OpenSession;
import org.owasp.jbrofuzz.fuzz.io.SaveSession;
import org.owasp.jbrofuzz.ui.AbstractPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

public class CommandLineInterpreter{
	private FileHandler mHandler;
	
	private JBroFuzzFormat mFormat;
	
	private JBroFuzzWindow mWindow;
	
	private Database mDatabase;

	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.4
	 * @param String [] args   - commandline arguments
	 * @return int resultCode  - > 0 == ok and < 0 == failed
	 */
	public int process(String [] args){
		String inputFileName = "";
		String outputFileName = "";
		int returnValue = 1; // everything went fine
		boolean result = false;

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
				outputFileName = args[j+1];
			}
			else if (args[j].equals("-s") || args[j].equals("--showResults")){
				result = true;
			}
			j++;
		}
		
		mDatabase = new Database();
		mHandler = new FileHandler();
		
		mFormat = new JBroFuzzFormat();
		mWindow = new JBroFuzzWindow(new JBroFuzz());
		
		if (inputFileName.length() > 0 && !inputFileName.equals("")){
			// setup new session
			OpenSession os = new OpenSession(mWindow, inputFileName);
		
			// start fuzzing
			final int c = mWindow.getTp().getSelectedIndex();
			AbstractPanel p = (AbstractPanel) mWindow.getTp().getComponent(c);
			p = (AbstractPanel) mWindow.getTp().getComponent(c);
			p.start();
			p.stop();
			if (mWindow.getPanelFuzzing().isStopped()){
				p.stop();
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
	 * @author daemonmidi@gmail.com
	 * @verision 2.4
	 * @since 2.4
	 * @param mWindow
	 * @param fileName
	 * @return returnCode int => > 0 == ok and < 0 == failed
	 */
	public int writeOutputFile(JBroFuzzWindow mWindow, String fileName){
		// Write requests and response into seperate file
		// tbd.
		
		
		// write Session to File.
		mWindow.setTabShow(JBroFuzzWindow.ID_PANEL_SYSTEM);
		if (mWindow.getPanelFuzzing().isStopped()){
			new SaveSession(mWindow, fileName);
		}
		
		System.out.println("Fuzzing Session finished. Output written to: " + fileName);
		return 0;
	}
	
	
	/**
	 * Print commandline help
	 * @author daemonmidi
	 * @version 2.4
	 * @since 2.4
	 */
	public void printHelp(){
		// help - should be replaced by printing content of a file.
		System.out.println("\n");
		System.out.println("\n");
		System.out.println("\n");
		System.out.println("OWASP                                 JBroFuzz                                            OWASP");
		
		System.out.println("Name");
		System.out.println("            JBroFuzz - Open Wep Application Security Project (OWASP) - Fuzzer");
		System.out.println("");
		System.out.println("Synopsis");
		System.out.println("          java -jar JBroFuzz.jar [option]");
		System.out.println("");
		System.out.println("Description");
		System.out.println(" A simple asynchronous fuzzer. See website for details.");
		System.out.println("Options");
		System.out.println("  OptionSyntax");
		System.out.println("    -i | --input  INPUTFILE    read session parameters from file and execute session.");
		System.out.println("                               Without the -s flag no GUI will be opened.");
		System.out.println("                               Responses of the system fuzzed will be stored in usual way.");
		System.out.println("");
		System.out.println("    -s | --show                show GUI at the end of the fuzzing session");
		System.out.println("");
		System.out.println("    -h | --help                show this help");
		System.out.println("");
		System.out.println("Examples");
		System.out.println("");
		System.out.println(" java -jar JBroFuzz.jar -h                               print this output");
		System.out.println(" ");
		System.out.println(" java -jar JBroFuzz.jar -i Session.jbrofuzz              replay session as saved to Session.jbrofuzz ");
		System.out.println("                                                         without starting the GUI.");
		System.out.println(" ");
		System.out.println(" java -jar JBroFuzz.jar -i Session.jbrofuzz -s           replay session as saved to Session.jbrofuzz ");
		System.out.println("                                                         and open GUI when finished fuzzing.");
		System.out.println("");
		System.out.println(" java -jar JBroFuzz.jar (-s)                             Start JBroFuzz in the usual manner.");
		System.out.println("                                                         The -s is optional.");
		System.out.println("");
	}
}