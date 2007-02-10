/**
 * Generator.java
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon . org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.owasp.jbrofuzz.fuzz.tcp;

import java.util.*;
/**
 * <p>Class responsible for creating the generators passed from the definitions.
 * </p>
 *
 * @author subere (at) uncon . org
 * @version 0.5
 */
public class Generator {
  // The String name of the generator
  private String name;
  // The String comment of the generator
  private String comment;
  // The type of generator
  private char type;
  // The alphabet of the generator
  private ArrayList alphabet;
  // The total number of elements within the alphabet
  private int alphabetSize;
  // The total additions that have been made to the alphabet
  private int totalAdds;
  // The String category where a generator might belong
  private String category;

  /**
   * <p>The type value for a generator to be replasive.</p>
   */
  public static final char REPLASIVE = 'P';
  /**
   * <p>The type value for a generator to be recursive.</p>
   */
  public static final char RECURSIVE = 'R';
  /**
   * <p>The type value for an unknown generator. This value should not occur
   * and is only there to show that an unknown generator type has been
   * specified.</p>
   */
  public static final char UNKNOWN = 'X';
  /**
   * <p>Generic constructor passing the value of the name of the Generator,
   * which is typically a three letter String, the comment of the generator (
   * what lies within the brackets), the type of obfuscator (mainly recursive
   * or replasive) as well as the size of the obfuscator, represented by the
   * alphabetSize. The final element that is presented is the category to which
   * the generator belong to.</p>
   *
   * @param type int
   * @param name String
   * @param comment String
   * @param alphabetSize int
   * @param category String
   */
  public Generator(char type, String name, String comment, int alphabetSize,
                   String category) {
    this.name = name;
    this.comment = comment;
    // Set the type
    this.type = UNKNOWN;
    if (type == RECURSIVE) {
      this.type = RECURSIVE;
    }
    if (type == REPLASIVE) {
      this.type = REPLASIVE;
    }

    this.alphabetSize = alphabetSize;
    this.category = category;
    totalAdds = 0;

    alphabet = new ArrayList(alphabetSize);
  }

  /**
   * <p>Add an element of String value to the alphabet for this Generator.
   * This method returns true if the element is successfully added or false
   * otherwise.</p>
   * <p>This method checks to see
   *
   * @param value String
   * @return output boolean
   */
  public boolean addAlphabetValue(StringBuffer value) {
    boolean output = false;
    if (totalAdds < alphabetSize) {
      alphabet.add(createStringBuffer(value));
      totalAdds++;
      output = true;
    }
    return output;
  }

  /**
   * <p>Method for creating one or more Strings of a given length. This method
   * is used when adding an alphabetValue, through addAlphabetValue.</p>
   *
   * @param param String
   * @return String
   */
  private static StringBuffer createStringBuffer(StringBuffer param) {
    // Check to see if the given String states that its a function
    if (param.length() < 5) {
      return param;
    }

    String beginning = param.substring(0, 5);
    if (!beginning.startsWith("f(x)=")) {
      return param;
    }

    // Get rid of the first characters
    param.delete(0, 5);
    // param = param.toString().substring(5);
    // Chop at x, the variable of f(x)
    String[] paramArray = param.toString().split(" x ");

    // Check to see if you have two elements
    if (paramArray.length != 2) {
      return param;
    }
    // Define the input string
    String input = paramArray[0];
    // Define the number of times
    int times;
    try {
      times = Integer.parseInt(paramArray[1]);
    }
    catch (NumberFormatException e) {
      times = 1;
    }

    // Check that times is positive
    if (times <= 0) {
      return param;
    }

    int len = input.length() * times;

    StringBuffer newBuffer = new StringBuffer(len);
    for (int i = 0; i < times; i++) {
      newBuffer.append(input);
    }
    return newBuffer;
  }

  /**
   * <p>Return the name of the generator.</p>
   * @return String
   */
  public String getName() {
    return name;
  }

  /**
   * <p>Returns the comment definition of the generator
   * @return String
   */
  public String getComment() {
    return comment;
  }

  /**
   * <p>Returns the type that the generator belongs to.</p>
   * @return int
   */
  public char getType() {
    return type;
  }

  /**
   * <p>Returns the type that the generator belongs to as a String value.</p>
   * @return String
   */
  public String getTypeAsString() {
    String output = "Unknown";

    if (type == RECURSIVE) {
      output = "Recursive";
    }
    if (type == REPLASIVE) {
      output = "Replasive";
    }

    return output;
  }

  /**
   * <p>Returns the size of the category representing the length of the Vector
   * alphabet holding the values.</p>
   * @return int
   */
  public int getSize() {
    return alphabetSize;
  }

  /**
   * <p>Returns the category description that the generator has been labelled to
   * belong to.</p>
   * @return String
   */
  public String getCategory() {
    return category;
  }

  /**
   * <p>Return the element of the alphabet at a given position.</p>
   * @param position int
   * @return String
   */
  public StringBuffer getElement(int position) {
    return (StringBuffer) alphabet.get(position % alphabetSize);
  }
}
