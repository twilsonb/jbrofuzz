/**
 * Time.java 0.6
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
package org.owasp.jbrofuzz.ver;

import java.text.*;
import java.util.*;
/**
 * <p>This class gives back the current date and time in a 
 * number of predefined
 * formats.</p>
 * <p>All available public methods are static.</p>
 *
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class Time {

  private static Date currentDate;
  private static Date currentTime;

  /**
   * Gives the date in yyyy.MM.dd format
   *
   * @return String
   */
  public static String dateToday() {
    currentDate = new Date();
    final SimpleDateFormat justDate = new SimpleDateFormat("dd.MM.yyyy",
      new Locale("en"));
    return justDate.format(currentDate);
  }

  /**
   * Gives date in yyyy.MM.dd.HH:mm:ss format
   * @return String
   */
  public static String dateAndTime() {
    currentTime = new Date();
    final SimpleDateFormat dateTime = new SimpleDateFormat(
      "dd.MM.yyyy HH:mm:ss", new Locale("en"));
    return dateTime.format(currentTime);
  }

  /**
   * Returns a date object that represents todays date and the time in HH:mm
   * that we pass as a parameter
   *
   * @param hoursMinutes String
   * @return Date
   */
  public static Date dateTimeAt(final String hoursMinutes) {
    final String usingToday = dateToday() + "." + hoursMinutes;
    final SimpleDateFormat converter = new SimpleDateFormat("dd.MM.yyyy.HH:mm",
      new Locale("en"));
    final ParsePosition pos = new ParsePosition(0);
    return converter.parse(usingToday, pos);
  }

  /**
   * We give a long number and get back a Date object
   * @param date long
   * @return Date
   */
  public static Date getUsingLong(final long date) {
    return new Date(date);
  }

  /**
   * Given a Date, returns the long number representing it.
   *
   * @param dateTime Date
   * @return long
   */
  public static long getUsingDate(final Date dateTime) {
    long relativeTime;
    relativeTime = dateTime.getTime();
    return relativeTime;
  }

  /**
   * Returns a formated string(HH:mm:ss) when given the date as a long number
   * @param date long
   * @return String
   */
  public static String hourMinSec(final long date) {
    final Date temp = Time.getUsingLong(date);
    final SimpleDateFormat dateTime = new SimpleDateFormat("HH:mm:ss",
      new Locale("en"));
    return dateTime.format(temp);
  }
}
