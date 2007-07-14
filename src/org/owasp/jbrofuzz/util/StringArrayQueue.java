/**
 * StringArrayQueue.java 0.6
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
 * <p>Implementation of a String Queue Array operating in true 
 * FIFO style.</p>
 *
 * @author subere (at) uncon (dot) org
 * @since 0.5
 * @version 0.6
 */
public class StringArrayQueue {

  // The String array holding the elements
  private String[] fifo;
  // The beginning and end of the queue
  private int start, end;
  // A boolean to check if the queue is full
  private boolean full;

  /**
   * Constructor for the FIFO stack queue, passing as argument the capacity.
   *
   * @param capacity int
   */
  public StringArrayQueue(final int capacity) {
    this.fifo = new String[capacity];
    this.start = this.end = 0;
    this.full = false;
  }

  /**
   * Method checking if the array has reached maximum capacity, or has no
   * elements.
   *
   * @return boolean
   */
  public boolean isEmpty() {
    return ((this.start == this.end) && !this.full);
  }

  /**
   * Method returns true if array is full
   *
   * @return boolean
   */
  public boolean isFull() {
    return this.full;
  }

  /**
   * Method for adding an element to the array queue.
   *
   * @param s String
   */
  public void push(final String s) {
    if (!this.full) {
      this.fifo[this.start = (++this.start % this.fifo.length)] = s;
    }
    if (this.start == this.end) {
      this.full = true;
    }
  }

  /**
   * Method for popping an element from the list.
   * @return String
   */
  public String pop() {
    if (this.full) {
      this.full = false;
    }
    else if (this.isEmpty()) {
      return "";
    }
    return this.fifo[this.end = (++this.end % this.fifo.length)];
  }
}
