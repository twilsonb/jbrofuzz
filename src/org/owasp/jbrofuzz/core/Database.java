/**
 * JBroFuzz 1.2
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007, 2008, 2009 subere@uncon.org
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
package org.owasp.jbrofuzz.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;

public class Database {

	// The maximum number of chars to be read from file, regardless
	private static final int MAX_CHARS = Short.MAX_VALUE;
	// The maximum number of lines allowed to be read from the file
	private static final int MAX_LINES = 1024;
	// The maximum length of a line allowed
	private static final int MAX_LINE_LENGTH = 512;

	// The maximum name length for a prototype
	private static final int MAX_PROTO_NAME_LENGTH = Byte.MAX_VALUE;
	// The maximum number of payloads in a prototype
	private static final int MAX_NO_OF_PAYLOADS = Byte.MAX_VALUE;
	// The maximum number of categories of a prototype
	private static final int MAX_NO_OF_CATEGORIES = Byte.MAX_VALUE;


	private HashMap<String, Prototype> prototypes;

	public Database() {

		prototypes = new HashMap<String, Prototype>();
		load();

	} 

	/**
	 * <p>Method called from the constructor to load the prototype 
	 * definitions of fuzzers from file.</p>
	 * 
	 * <p>This method has a considerable number of checks for
	 * loading prototypes from the internal file, which can
	 * be removed, to speed up reading the file.</p>
	 * 
	 * @return 0 if all ok, negative values in case of an error
	 *
	 * @see #Database()
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.3
	 */
	private int load() {


		final StringBuffer fileContents = new StringBuffer();

		// Attempt to read from the jar file
		final URL fileURL = ClassLoader.getSystemClassLoader().getResource("fuzzers.jbrofuzz");

		if(fileURL == null) {
			// -1 File not found
			return -1;
		}

		// Read the characters from the file
		BufferedReader in = null;
		try {
			final URLConnection connection = fileURL.openConnection();
			connection.connect();

			in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));

			int counter = 0;
			int c;
			while( ((c = in.read()) > 0) && (counter < MAX_CHARS) ) {
				// Allow the character only if its printable ascii or \n
				if( (CharUtils.isAsciiPrintable((char) c)) || ( ((char) c)=='\n' ) ) {
					fileContents.append((char) c);
				}
				counter++;
			}

			in.close();

		} 
		catch (final IOException e1) {
			// -2 IOException while reading the file
			return -2;
		} 
		finally {
			IOUtils.closeQuietly(in);
		}

		// Break down the file contents into lines
		final String[] fileInput = fileContents.toString().split("\n");

		if(fileInput.length > MAX_LINES) {
			// -3 More lines than you can load
			return -3;
		}

		
		for (int i = 0; i < fileInput.length; i++) {

			// Ignore comment lines starting with '#'
			if(fileInput[i].startsWith("#")) {
				continue;
			}

			// Ignore lines of length greater than MAX_LINE_LENGTH
			if(fileInput[i].length() > MAX_LINE_LENGTH) {
				continue;
			}

			// Check 1 indicating a likely prototype candidate
			try {
				if(fileInput[i].charAt(1) != ':') {
					continue;
				}
				if( (fileInput[i].charAt(9) != ':') ) {
					if(fileInput[i].charAt(13) != ':') {
						continue;
					}
				}
			} catch (IndexOutOfBoundsException e1) {
				continue;
			}
			
			// [0] -> P || R || X
			// [1] -> HTT-PMT-EDS
			// [2] -> Uppercase HTTP Methods
			// [3] -> 8
			final String[] _fla = fileInput[i].split(":");

			// Check that there are four fields separated by : 
			if(_fla.length != 4) {
				continue;
			}
 
			// Check [0] -> P || R || X
			if(! "P".equals(_fla[0]) ) {
				if(! "R".equals(_fla[0]) ) {
					if(! "X".equals(_fla[0]) ) {
						continue;						
					}
				}
			}


			// The Id: SQL-INJ cannot be empty
			if(_fla[1].isEmpty()) {
				continue;
			}

			// The name: "SQL Injection" cannot be empty
			if(_fla[2].isEmpty()) {
				continue;
			}

			// Check the prototype name length
			if (_fla[2].length() > MAX_PROTO_NAME_LENGTH) {
				continue;
			}

			int noPayloads = 0;			
			try {
				
				noPayloads = Integer.parseInt(_fla[3]);
				
			} catch (final NumberFormatException e) {
				continue;
			}

			// Check how many payloads this prototype has
			if(noPayloads > MAX_NO_OF_PAYLOADS) {
				continue;
			}
			if(noPayloads == 0) {
				continue;
			}

			// Check we have that many payloads left in file
			if(i + noPayloads > fileInput.length) {
				continue;
			}

			try {
				if(!fileInput[i + 1].startsWith(">")) {
					continue;
				}
			} catch (IndexOutOfBoundsException e) {
				continue;
			}

			String line2 = ""; 
			try {
				line2 = fileInput[i + 1].substring(1);
			} catch (IndexOutOfBoundsException e) {
				continue;
			}

			// [0] -> HTTP Methods 
			// [1] -> Replacive Fuzzers 
			// [2] -> Uppercase Fuzzers
			final String[] _sla = line2.split("\\|");
			if(_sla.length > MAX_NO_OF_CATEGORIES) {
				continue;
			}

			// Alas! Finally create a prototype
			final Prototype proto = new Prototype(_fla[0].charAt(0), _fla[1], _fla[2]);

			// If categories do exist in the second line
			if (line2.contains("|")) {

				for (String categ_ry : _sla) {
					// add the category to the prototype
					proto.addCategory(
						StringUtils.stripStart(
							StringUtils.stripEnd(
								categ_ry, " "
							),
						" ")
					);

				}
			}
			// If no categories have been identified, 
			// add a default category
			else {

				proto.addCategory("Default");

			}

			// Add the values for each element
			for (int j = 1; j <= noPayloads; j++) {

				try {
					proto.addPayload(fileInput[i + 1 + j]);
				} catch (IndexOutOfBoundsException e) {
					continue;
				}
			}
			
			// Finally add the prototype to the database
			prototypes.put(_fla[1], proto);
			
		}

		return 0;

	}
	
	/**
	 * <p>Checks if the {@link #Database()} contains a Prototype
	 * with the given id
	 * 
	 * @param prototypeId e.g HTT-PMT-EDS, INT-LOW, or SQL-INJ
	 * 
	 * @return true if a Prototype with that prototypeId exists 
	 *
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public boolean containsPrototype(String prototypeId) {

		return prototypes.containsKey(prototypeId);

	}

	//TODO
	/**
	 *
	public boolean addGenerator(Generator g) {

		if(this.containsGenerator(g.getId())) {
			return false;
		} else {
			g.addCategory("Default");
			if(!g.getPayloads().isEmpty()) {
				generators.put(g.getId(), g);
				return true;
			}
			return false;
		}

	}
	 */

	/**
	 * <p>Return all the unique categories found across prototypes that 
	 * are loaded into the database.</p>
	 * <p>Category examples include: "Replacive Fuzzers", "Exploits", etc.
	 * 
	 * @return String[] uniqueCategories
	 */
	public String[] getAllCategories() {

		HashSet<String> o = new HashSet<String>();

		String[] ids = getAllIds();
		for (String id : ids) {

			ArrayList<String> categoriesArrayList = prototypes.get(id)
			.getCategories();
			String[] categoriesArray = new String[categoriesArrayList.size()];
			categoriesArrayList.toArray(categoriesArray);

			for (String cCategory : categoriesArray) {

				o.add(cCategory);

			}

		}

		String[] uniqueCategoriesArray = new String[o.size()];
		o.toArray(uniqueCategoriesArray);

		return uniqueCategoriesArray;

	}

	/**
	 * <p>Get all the unique Prototype IDs that are loaded in the 
	 * database.</p>
	 * 
	 * @return String[] e.g. ["FSE-UPP", "HTT-PMT-EDS", ...]
	 *
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public String[] getAllIds() {

		Set<String> set = prototypes.keySet();
		final String[] output = new String[set.size()];
		return set.toArray(output);

	}

	/**
	 * <p>Get all the names of the Prototypes that are loaded 
	 * in the database.</p>
	 * <p>The names are not required to be unique, if that is 
	 * required, use {@link #getAllIds()}
	 * 
	 * @return String[] e.g. ["Uppercase HTTP Methods", ...
	 *
	 * @see #getAllIds()
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public String[] getAllNames() {

		StringBuffer output = new StringBuffer();

		Set<String> set = prototypes.keySet();
		final String[] input = new String[set.size()];
		set.toArray(input);

		for (String key : input) {
			output.append(prototypes.get(key).getName() + "\n");
		}

		return output.toString().split("\n");

	}

	/**
	 * <p>Return the Prototype, based on the prototypeID given.</p>
	 * 
	 * @param prototypeId e.g. "HTT-PMT-EDS"
	 * @return Prototype The Prototype for the given prototypeID
	 *
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public Prototype getPrototype(String prototypeId) {

		return prototypes.get(prototypeId);

	}

	public String[] getGenerators(String category) {

		HashSet<String> o = new HashSet<String>();
		String[] ids = getAllIds();

		for (String id : ids) {

			Prototype g = prototypes.get(id);
			if (g.isAMemberOfCategory(category)) {
				o.add(g.getName());
			}
		}

		String[] uniqueCategoriesArray = new String[o.size()];
		o.toArray(uniqueCategoriesArray);

		return uniqueCategoriesArray;
	}

	public String getIdFromName(String name) {

		String[] ids = getAllIds();
		for (String id : ids) {
			Prototype g = prototypes.get(id);
			// System.out.println("In getAllIds() input name is: -" + name + "-
			// current name is: -" + g.getName() + "-");
			if (name.equalsIgnoreCase(g.getName())) {
				// System.out.println("Found Match! input -" + name + "- output
				// is: -" + g.getName() + "-");
				return id;
			}
		}
		return "";
	}

	public String getName(String id) {

		return prototypes.get(id).getName();

	}

	public String[] getPayloads(String id) {

		if (containsPrototype(id)) {
			Prototype g = prototypes.get(id);
			final String[] output = new String[g.size()];
			return g.getPayloads().toArray(output);
		} else {
			return new String[0];
		}

	}

	public int getSize(String id) {

		if (containsPrototype(id)) {
			Prototype g = prototypes.get(id);
			return g.size();
		} else {
			return 0;
		}

	}

	public int size() {

		return prototypes.size();

	}
	
	/**
	 * <p>Method responsible for creating a Fuzzer, based on an existing prototypeId
	 * and length specified.</p>
	 * 
	 * @param prototypeId prototypeId e.g HTT-PMT-EDS, INT-LOW, or SQL-INJ
	 * @param len The length of the fuzzer, used for recursive fuzzers
	 * @return org.owasp.jbrofuzz.core#Fuzzer()
	 * @throws NoSuchFuzzerException
	 *
	 * @see {@link #containsPrototype(String)}
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public Fuzzer createFuzzer(String prototypeId, int len) throws NoSuchFuzzerException {
		
		if(!this.containsPrototype(prototypeId)) {

			throw new NoSuchFuzzerException(
					StringUtils.abbreviate(prototypeId, 10) + 
					" : No Such Fuzzer Found in the Database ");

		}
		
		return new Fuzzer(getPrototype(prototypeId), len);
	}

}
