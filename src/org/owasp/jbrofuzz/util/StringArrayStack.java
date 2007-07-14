/**
 * StringArrayStack.java 0.6
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon (dot) org
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
 * <p>Implementation of a String Stack Array operating in true 
 * FILO style.</p>
 *
 * @author subere (at) uncon (dot) org
 * @since 0.5
 * @version 0.6
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
  public StringArrayStack(final int capacity) {
    this.filo = new String[capacity];
    this.pointer = -1;
  }

  /**
   * Method for checking if the String Stack Array is empty.
   *
   * @return boolean
   */
  public boolean isEmpty() {
    return this.pointer == -1;
  }

  /**
   * Method for adding an element to the Stack Array. This will be a push
   * action.
   *
   * @param element String
   */
  public void push(final String element) {
    if (this.pointer + 1 < this.filo.length) {
      this.filo[++this.pointer] = element;
    }
  }

  /**
   * Method for popping an element from the Stack Array
   *
   * @return String
   */
  public String pop() {
    if (this.isEmpty()) {
      return "";
    }
    return this.filo[this.pointer--];
  }
}
