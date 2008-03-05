package org.owasp.jbrofuzz.core;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.commons.io.*;
import org.apache.commons.lang.*;

public class Database {

	private HashMap<String, Generator> generators;

	public Database() {

		final int maxLines = 1024;
		final int maxLineLength = 256;
		final int maxNumberOfPayloads = 32;

		int line_counter = 0;
		BufferedReader in = null;

		final StringBuffer fileContents = new StringBuffer();

		// Attempt to read from the jar file
		final URL fileURL = ClassLoader.getSystemClassLoader().getResource("generators.jbrofuzz");

		try {
			final URLConnection connection = fileURL.openConnection();
			connection.connect();

			in = new BufferedReader(new InputStreamReader(connection
					.getInputStream()));
			String line = in.readLine();
			line_counter++;
			while ((line != null) && (line_counter < maxLines)) {

				if (line.length() > maxLineLength) {
					line = line.substring(0, maxLineLength);
				}
				fileContents.append(line + "\n");
				line = in.readLine();
			}
			in.close();
		} 
		catch (final IOException e1) {
			System.out.println("Directories file (inside jar): "
					+ fileURL.toString() + " could not be found");
		} 
		finally {
			IOUtils.closeQuietly( in );
		}

		// Parse the contents of the StringBuffer to the array of generators

		final String[] fileInput = fileContents.toString().split("\n");
		final int len = fileInput.length;

		generators = new HashMap<String, Generator>(len);

		for (int i = 0; i < len; i++) {

			// the number of payloads identified for each category
			int numberOfPayloads = 0;


			final String line = fileInput[i];
			if ( (!line.startsWith("#")) && (line.length() > 5)) {
				// "P:ABC-DEF:" or "P:ABC-DEF-GHI:"
				if ((line.charAt(1) == ':') && ( (line.charAt(9) == ':') || (line.charAt(13) == ':' ) )) {
					final String[] firstLineArray = line.split(":");
					// Check that there are four fields of : in the first line
					if (firstLineArray.length == 4) {
						// Check that the name of the identifier is less than 24 characters
						if ((firstLineArray[2].length() < 25) && (firstLineArray[2].length() > 0)) {
							// Check that the first character is either a P or an R
							if (("P".equals(firstLineArray[0]))	|| ("R".equals(firstLineArray[0]))) {
								try {
									numberOfPayloads = Integer.parseInt(firstLineArray[3]);
								} catch (final NumberFormatException e) {
									numberOfPayloads = 0;
								}
							}
						}
					}
				} // First line check

				// If a positive number of payloads is aclaimed in the first line and the first line is ok
				if ((numberOfPayloads > 0) && (numberOfPayloads <= maxNumberOfPayloads)) {
					final String[] firstArray = line.split(":");
					// final int generatorLength = Integer.parseInt(firstArray[3]);
					// Check that there remaining element in the generator Vector
					if (i < len - numberOfPayloads - 1) {
						// Check that the second line starts with a >
						String line2 = fileInput[i + 1];
						if (line2.startsWith(">")) {
							line2 = line2.substring(1);
							// Check to see that the Generator name is unique
							// if (!isGeneratorNameUsed(firstArray[1])) {
							
							// Finally create the generator if all the checks pass
							final Generator myGen = new Generator(firstArray[0].charAt(0), firstArray[1], StringUtils.rightPad(firstArray[2], 24));

							// Add the values for each element
							for (int j = 1; j <= numberOfPayloads; j++) {

								final StringBuffer myBuffer = new StringBuffer();
								myBuffer.append(fileInput[i + 1 + j]);
								myGen.addPayload(myBuffer.toString());

							}
							// Finally add the generator to the Vector of generators
							generators.put(firstArray[1], myGen);
							//}
						}
					}
				}
			}
		}

		// generators.trimToSize();


	} // constructor


	public int size() {

		return generators.size();

	}


	public String[] getAllIds() {
		
		Set<String> set = generators.keySet();
		final String [] output = new String[set.size()];
		return set.toArray(output);

	}

	public String[] getAllNames() {

		StringBuffer output = new StringBuffer();
		
		Set<String> set = generators.keySet();
		final String [] input = new String[set.size()];
		set.toArray(input);
		
		for(String key : input) {
			output.append(generators.get(key).getName() + "\n");
		}
		return output.toString().split("\n");
	}

	public String[] getPayloads(String Id) {

		Generator g = generators.get(Id);
		final String [] output = new String[g.size()];
		return g.getPayloads().toArray(output);


	}
	
	public Generator getGenerator(String Id) {

		return generators.get(Id);
		
	}
	
	public boolean containsGenerator(String Id) {
		
		return generators.containsKey(Id);
		
	}

	
	public void test() {
		
		/*
		System.out.println("\n\nFound a total of : " + this.size() + " Generators in the Database.");


		for(String s : this.getAllIds()) {
			System.out.println(s);
		}

		for(String t : this.getAllNames()) {
			System.out.println(t);
		}

		for(String u : this.getAllIds()) {
			System.out.println("\n\nGenerator ID: " + u + "  (" + (this.getPayloads(u).length - 1) + ")");
			for(String v : this.getPayloads(u)) {
				System.out.println("\t" + "\t\t" + v.length());
			}

		}
		*/
		
		/*
		System.out.println("\n\nGenerator ID: BFO  (" + this.getPayloads("BFO").length + ")");

		String [] payloads = this.getPayloads("BFO");
		System.out.print(payloads[this.getPayloads("BFO").length - 1]);
		*/
		
		System.out.println("\n\nFound a total of : " + this.size() + " Generators in the Database.");
		
		String [] keys = this.getAllIds();
		for(String key : keys) {
			System.out.println(" " + key + "\t\t" + this.getGenerator(key).getId() + "\t\t" + this.getGenerator(key).getName() + "\t\t" + this.getGenerator(key).isReplasive());
		}
		
		
	}
	
}
