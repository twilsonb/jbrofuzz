/**
 * StringArrayStack.java 0.5
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
package org.owasp.jbrofuzz.util;

/**
 * <p>Implementation of a String Stack Array operating in true FILO style.</p>
 *
 * @author subere (at) uncon . org
 * @since 0.5
 * @version 0.5
 */
public class StringArrayStack {

  // The array of elements
  private String filo[];
  // The pointer used
  private int pointer;

  /**
   * Constructor for the FILO stack array, passing as argument the capacity.
   *
   * @param capacity int
   */
  public StringArrayStack(int capacity) {
    filo = new String[capacity];
    pointer = -1;
  }

  /**
   * Method for checking if the String Stack Array is empty.
   *
   * @return boolean
   */
  public boolean isEmpty() {
    return pointer == -1;
  }

  /**
   * Method for adding an element to the Stack Array. This will be a push
   * action.
   *
   * @param element String
   */
  public void push(String element) {
    if (pointer + 1 < filo.length) {
      filo[++pointer] = element;
    }
  }

  /**
   * Method for popping an element from the Stack Array
   *
   * @return String
   */
  public String pop() {
    if (isEmpty()) {
      return "";
    }
    return filo[pointer--];
  }
}
