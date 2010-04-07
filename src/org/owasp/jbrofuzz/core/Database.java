/**
 * JBroFuzz 2.0
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
package org.owasp.jbrofuzz.core;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.system.Logger;

/**
 * <p>
 * A database is a collection of prototypes, as loaded from the internal file
 * <code>fuzzers.jbrofuzz</code> residing within the JBroFuzz.jar file.
 * </p>
 * 
 * <p>
 * Once a database instance has been created, you can obtain any known fuzzer
 * through the factory method {@link #createFuzzer(String, int)}.
 * </p>
 * 
 * <p>
 * This class involves a number of further methods for querying the number of
 * prototypes available, their corresponding IDs, names, as well as payload
 * values.
 * </p>
 * 
 * 
 * @author subere@uncon.org
 * @version 1.9
 * @since 1.2
 */
public class Database {


	final private Map<String, Prototype> prototypes;

	private final static String ZERO_FUZZER_CATEGORY = "Zero Fuzzers";

	/**
	 * <p>Constructs a database of fuzzers, by loading
	 * the corresponding prototypes from file
	 * and adding zero fuzzers.</p> 
	 * 
	 * @see #createFuzzer(String, int)
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 1.2
	 */
	public Database() {

		prototypes = Verifier.loadFile("fuzzers.jbrf");
		addZeroFuzzers();
		Logger.log("Database loaded with " + prototypes.size() + " fuzzers", 0);

	}
	
	/**
	 * <p>Constructs a database of fuzzers, by loading
	 * the corresponding prototypes from any external
	 * file location and adding zero fuzzers.</p> 
	 * 
	 * <p>In the event of an error, default towards 
	 * loading the in-build "fuzzers.jbrf" file, in a
	 * similar way as that of invoking the parameterless
	 * Database() constructor.</p>
	 * 
	 * @see #createFuzzer(String, int)
	 * @param fuzzersFilePath The absolute file path
	 * of the .jbrf file
	 *  
	 * @author subere@uncon.org
	 * @version 2.1
	 * @since 2.1 
	 */
	public Database(String fuzzersFilePath) {
		
		prototypes = Verifier.loadAnyFile(fuzzersFilePath);
		addZeroFuzzers();
		Logger.log("Database loaded with " + prototypes.size() + " fuzzers", 0);
		
	}
	
	/**
	 * <p>Method for adding the zero fuzzers to the 
	 * database.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.1
	 * @since 2.1
	 */
	private void addZeroFuzzers() {
		
		Logger.log("Adding 5 Zero Fuzzers to the fuzzing Database", 0);

		// Add the Zero Fuzzers
		final Prototype pt0 = new Prototype('Z', "000-ZER-10K", "10000 Plain Requests");
		final Prototype pt1 = new Prototype('Z', "000-ZER-1KI", "1000 Plain Requests");
		final Prototype pt2 = new Prototype('Z', "000-ZER-100", "100 Plain Requests");
		final Prototype pt3 = new Prototype('Z', "000-ZER-TEN", "10 Plain Requests");
		final Prototype pt4 = new Prototype('Z', "000-ZER-ONE", "1 Plain Request");

		pt0.addCategory(ZERO_FUZZER_CATEGORY);
		pt1.addCategory(ZERO_FUZZER_CATEGORY);
		pt2.addCategory(ZERO_FUZZER_CATEGORY);
		pt3.addCategory(ZERO_FUZZER_CATEGORY);
		pt4.addCategory(ZERO_FUZZER_CATEGORY);

		for (int i = 0; i < 10000; i++) {

			pt0.addPayload("");

			if(i < 1000) {
				pt1.addPayload("");
			}
			if(i < 100) {
				pt2.addPayload("");
			}
			if(i < 10) {
				pt3.addPayload("");
			}
			if(i < 1) {
				pt4.addPayload("");
			}
		}

		prototypes.put("000-ZER-10K", pt0);
		prototypes.put("000-ZER-1KI", pt1);
		prototypes.put("000-ZER-100", pt2);
		prototypes.put("000-ZER-TEN", pt3);
		prototypes.put("000-ZER-ONE", pt4);
	}

	/**
	 * <p>
	 * Checks if the {@link #Database()} contains a Prototype with the given id
	 * 
	 * @param prototypeId
	 *            e.g 001-HTT-MTH or 032-SQL-INJ
	 * 
	 * @return true if a Prototype with that prototypeId exists
	 * 
	 * @author subere@uncon.org
	 * @version 1.8
	 * @since 1.2
	 */
	public boolean containsPrototype(final String prototypeId) {

		return prototypes.containsKey(prototypeId);

	}

	/**
	 * <p>
	 * Method responsible for creating a Fuzzer, based on an existing
	 * prototypeId and length specified.
	 * </p>
	 * 
	 * @param prototypeId
	 *            prototypeId e.g 001-HTT-MTH or 032-SQL-INJ
	 * @param len
	 *            The length of the fuzzer, used for recursive fuzzers
	 * @return org.owasp.jbrofuzz.core#Fuzzer()
	 * @throws NoSuchFuzzerException
	 * 
	 * @see #containsPrototype(String)
	 * 
	 * @author subere@uncon.org
	 * @version 1.8
	 * @since 1.2
	 */
	public Fuzzer createFuzzer(final String prototypeId, final int len)
	throws NoSuchFuzzerException {

		if (!containsPrototype(prototypeId)) {

			throw new NoSuchFuzzerException(StringUtils.abbreviate(prototypeId,
					10) + " : No Such Fuzzer Found in the Database ");

		}

		return new Fuzzer(getPrototype(prototypeId), len);
	}

	public PowerFuzzer createPowerFuzzer(final String prototypeId, final int len, final int power) 
	throws NoSuchFuzzerException {

		if (!containsPrototype(prototypeId)) {

			throw new NoSuchFuzzerException(StringUtils.abbreviate(prototypeId, 
					10) + "No Such Fuzzer Found in the Database ");

		}

		return new PowerFuzzer(getPrototype(prototypeId), len, power);
	}

	public DoubleFuzzer createDoubleFuzzer(String id1, int length1,  
			String id2, int length2) throws NoSuchFuzzerException {

		if( containsPrototype(id1) && containsPrototype(id2) ) {

			return new DoubleFuzzer(getPrototype(id1), length1, getPrototype(id2), length2);

		} else {

			throw new NoSuchFuzzerException(
					StringUtils.abbreviate(id1, 10) + " or " +
					StringUtils.abbreviate(id2, 10) + 
			" Not Found in the Database ");
		}
	}

	public CrossProductFuzzer createCrossFuzzer(String id1, int length1,  
			String id2, int length2) throws NoSuchFuzzerException {

		if( containsPrototype(id1) && containsPrototype(id2) ) {

			return new CrossProductFuzzer(getPrototype(id1), length1, getPrototype(id2), length2);

		} else {

			throw new NoSuchFuzzerException(
					StringUtils.abbreviate(id1, 10) + " or " +
					StringUtils.abbreviate(id2, 10) + 
			" Not Found in the Database ");
		}
	}

	/**
	 * <p>
	 * This factory method should be used instead of the #createFuzzer() if the
	 * recursive payloads are more than 16^16 long.
	 * </p>
	 * <p>
	 * As a Fuzzer is an iterator using a long datatype for counting, 
	 * FuzzerBigInteger is an iterator using the BigInteger datatype and thus not
	 * limited to 16^16 number of iterator payloads. 
	 * </p>
	 * <p>
	 * Method responsible for creating a FuzzerBigInteger, based on an existing
	 * prototypeId and length specified.
	 * </p>
	 * 
	 * @param prototypeId
	 *            prototypeId e.g 001-HTT-MTH or 032-SQL-INJ
	 * @param len
	 *            The length of the fuzzer, used for recursive fuzzers
	 * @return org.owasp.jbrofuzz.core#FuzzerBigInteger()
	 * @throws NoSuchFuzzerException
	 * 
	 * @see #containsPrototype(String)
	 * 
	 * @author subere@uncon.org
	 * @version 1.9
	 * @since 1.9
	 */
	public FuzzerBigInteger createFuzzerBigInteger(final String prototypeId, final int len) 
	throws NoSuchFuzzerException {

		if (!containsPrototype(prototypeId)) {

			throw new NoSuchFuzzerException(StringUtils.abbreviate(prototypeId,
					10)
					+ " : No Such Fuzzer Found in the Database ");

		}

		return new FuzzerBigInteger(getPrototype(prototypeId), len);
	}

	/**
	 * <p>Return all the unique categories found across prototypes that are loaded
	 * into the database.</p>
	 * 
	 * <p>Category examples include: "Replacive Fuzzers", "Exploits", etc.</p>
	 * 
	 * @return String[] uniqueCategories
	 * 
	 * @author subere@uncon.org
	 * @version 1.5
	 * @since 1.2
	 */
	public String[] getAllCategories() {

		final HashSet<String> o = new HashSet<String>();

		final String[] ids = getAllPrototypeIDs();
		for (String id : ids) {

			final List<String> catArrayList = prototypes.get(id)
			.getCategories();
			final String[] categoriesArray = new String[catArrayList.size()];
			catArrayList.toArray(categoriesArray);

			for (String cCategory : categoriesArray) {

				o.add(cCategory);

			}

		}

		final String[] uCategoriesArray = new String[o.size()];
		o.toArray(uCategoriesArray);

		return uCategoriesArray;

	}

	/**
	 * <p>Get all the unique Prototype IDs that are loaded in the database.</p>
	 * 
	 * @return String[] e.g. ["001-HTT-MTH", "032-SQL-INJ", ...]
	 * 
	 * @see #getAllFuzzerIDs()
	 * 
	 * @author subere@uncon.org
	 * @version 1.5
	 * @since 1.2
	 */
	public String[] getAllPrototypeIDs() {

		final Set<String> set = prototypes.keySet();
		final String[] output = new String[set.size()];
		return set.toArray(output);

	}

	/**
	 * <p>Get all the unique Fuzzer IDs that are loaded in the database.</p>
	 * 
	 * @return String[] e.g. ["FSE-UPP", "HTT-PMT-EDS", ...]
	 * 
	 * @see #getAllPrototypeIDs()
	 * 
	 * @author subere@uncon.org
	 * @version 1.5
	 * @since 1.5
	 */
	public String[] getAllFuzzerIDs() {

		return getAllPrototypeIDs();

	}

	/**
	 * <p>
	 * Get all the names of the Prototypes that are loaded in the database.
	 * </p>
	 * <p>
	 * The names are not required to be unique, if that is required, use
	 * {@link #getAllPrototypeIDs()}
	 * 
	 * @return String[] e.g. ["Uppercase HTTP Methods", ...
	 * 
	 * @see #getAllPrototypeIDs()
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public String[] getAllPrototypeNames() {

		final StringBuffer output = new StringBuffer();

		final Set<String> set = prototypes.keySet();
		final String[] input = new String[set.size()];
		set.toArray(input);

		for (String key : input) {
			output.append(prototypes.get(key).getName());
			output.append('\n');
		}

		return output.toString().split("\n");

	}

	/**
	 * <p>
	 * Returns the Id of a prototype, given its name.
	 * </p>
	 * 
	 * @param name
	 *            e.g. "Uppercase HTTP Methods"
	 * @return String the Id, or "" if the name is not found. e.g. "HTT-PMT-EDS"
	 * 
	 * @see #getName(String)
	 * 
	 * @author subere@uncon.org
	 * @version 1.9
	 * @since 1.2
	 */
	public String getIdFromName(final String name) {

		final String[] ids = getAllPrototypeIDs();
		for (String id : ids) {
			final Prototype cPrototype = prototypes.get(id);
			if (name.equalsIgnoreCase(cPrototype.getName())) {
				return id;
			}
		}
		return "";
	}

	/**
	 * <p>
	 * Returns the name of a prototype, given its Id.
	 * </p>
	 * 
	 * @param uniqId
	 *            e.g. "001-HTT-MTH"
	 * @return String e.g. "Uppercase HTTP Methods"
	 * 
	 * @see #getIdFromName(String)
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public String getName(final String uniqId) {

		return prototypes.get(uniqId).getName();

	}

	/**
	 * <p>
	 * Returns the array of payloads attached to a given prototype Id.
	 * </p>
	 * 
	 * @param uniqId
	 *            e.g. "001-HTT-MTH"
	 * @return String[] or String[0] if the prototype does not exist in the
	 *         database
	 * 
	 * @see #getSize(String)
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public String[] getPayloads(final String uniqId) {

		if (containsPrototype(uniqId)) {
			Prototype cPrototype = prototypes.get(uniqId);
			final String[] output = new String[cPrototype.size()];
			return cPrototype.getPayloads().toArray(output);
		} else {
			return new String[0];
		}

	}

	/**
	 * <p>
	 * Return the Prototype, based on the prototypeID given.
	 * </p>
	 * 
	 * @param prototypeId
	 *            e.g. "001-HTT-MTH"
	 * @return Prototype The Prototype for the given prototypeID
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public Prototype getPrototype(final String prototypeId) {

		return prototypes.get(prototypeId);

	}

	/**
	 * <p>
	 * Given a category, return all prototype names that belong to that
	 * category.
	 * </p>
	 * 
	 * @param category
	 *            the category as a string to check
	 * @return String[] array of prototype names
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public String[] getPrototypeNamesInCategory(final String category) {

		final HashSet<String> o = new HashSet<String>();
		final String[] ids = getAllPrototypeIDs();

		for (String id : ids) {

			Prototype g = prototypes.get(id);
			if (g.isAMemberOfCategory(category)) {
				o.add(g.getName());
			}
		}

		String[] uCategotiesArray = new String[o.size()];
		o.toArray(uCategotiesArray);

		return uCategotiesArray;
	}

	/**
	 * <p>
	 * Get the number of payloads the prototype with the given Id has.
	 * </p>
	 * 
	 * @param fuzzerId
	 *            e.g. "001-HTT-MTH"
	 * @return int value of size, 0 if Id does not exist.
	 * 
	 * @see #getPayloads(String)
	 * @author subere@uncon.org
	 * @version 1.9
	 * @since 1.2
	 */
	public int getSize(final String fuzzerId) {

		int returnSize = 0;

		if (containsPrototype(fuzzerId)) {
			final Prototype g = prototypes.get(fuzzerId);
			returnSize = g.size();
		}

		return returnSize;

	}



	/**
	 * <p>
	 * Return the size of the database.
	 * </p>
	 * 
	 * @return int the size of the database.
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public int size() {

		return prototypes.size();

	}

	/**
	 * <p>Return the type of fuzzer e.g. Replacive, Recursive, etc.
	 * based on the name provided.</p>
	 * 
	 * @param name The fuzzer name
	 * @return e.g. Replacive
	 */
	public String getTypeFromName(final String name) {

		return prototypes.get(getIdFromName(name)).getType();

	}

	/**
	 * <p>Return the prototype type, based on the id value.</p>
	 * 
	 * @param fuzzerId
	 * @return e.g. "Recursive", "Cross Product"
	 * 
	 * @author subere@uncon.org
	 * @version 1.8
	 * @since 1.8
	 */
	public String getType(final String fuzzerId) {

		return prototypes.get(fuzzerId).getType();

	}

}
