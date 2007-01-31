/**
 * Time.java 0.4
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
package org.owasp.jbrofuzz.ver;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * This class gives back the current date and time in a number of predefined
 * formats.
 *
 * @author subere@uncon.org
 * @version 0.4
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
    SimpleDateFormat justDate = new SimpleDateFormat("dd.MM.yyyy");
    String today = justDate.format(currentDate);
    return today;
  }

  /**
   * Gives date in yyyy.MM.dd.HH:mm:ss format
   * @return String
   */
  public static String dateAndTime() {
    currentTime = new Date();
    SimpleDateFormat dateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    String today = dateTime.format(currentTime);
    return today;
  }

  /**
   * Returns a date object that represents todays date and the time in HH:mm
   * that we pass as a parameter
   *
   * @param hoursMinutes String
   * @return Date
   */
  public static Date dateTimeAt(String hoursMinutes) {
    try {
      String usingToday = dateToday() + "." + hoursMinutes;
      SimpleDateFormat converter = new SimpleDateFormat("dd.MM.yyyy.HH:mm");
      ParsePosition pos = new ParsePosition(0);
      Date dateTime = converter.parse(usingToday, pos);
      return dateTime;
    }
    catch (NullPointerException e) {
      //Logger.logError(
      //  "Timer reports: Illegal format of input time\nfor Date conversion.");
      return null;
    }
  }

  /**
   * We give a long number and get back a Date object
   * @param date long
   * @return Date
   */
  public static Date getUsingLong(long date) {
    try {
      Date dateAt = new Date(date);
      return dateAt;
    }
    catch (NullPointerException e) {
    //  Logger.logError(
     //   "Timer reports: Illegal length of long time\nfor Date conversion.");
      return null;
    }
  }

  /**
   * Given a Date, returns the long number representing it.
   *
   * @param dateTime Date
   * @return long
   */
  public static long getUsingDate(Date dateTime) {
    long relativeTime;
    try {
      relativeTime = dateTime.getTime();
      return relativeTime;
    }
    catch (NullPointerException e) {
    //  Logger.logError(
    //    "Timer reports: Empty Date object\npassed for Date conversion.");
      relativeTime = 0;
      return relativeTime;
    }
  }

  /**
   * Returns a formated string(HH:mm:ss) when given the date as a long number
   * @param date long
   * @return String
   */
  public static String hourMinSec(long date) {
    Date temp = Time.getUsingLong(date);
    SimpleDateFormat dateTime = new SimpleDateFormat("HH:mm:ss");
    String now = dateTime.format(temp);
    return now;
  }
}
