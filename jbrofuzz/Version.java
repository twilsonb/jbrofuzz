/**
 * Version.java 0.3
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2006 yns000 (at) users. sourceforge. net
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
package jbrofuzz;

import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
/**
 * <p>Title: Java Bro Fuzzer</p>
 *
 * <p>Description: Class responsible for the versioning of the application,
 * including the about text, as well as the code name. This class carries the
 * date format used to create respective files as well as the ISO language code
 * used.</p>
 * <p>The current version only support version numbers of [0,99].</p>
 *
 * @author subere@uncon.org
 * @version 0.3
 */
public class Version {

  private final int ver_int;
  private final String aboutText, disclaimerText;
  private static String runningDate;

  private static final String DATE_FORMAT = "DDD yyyy-MM-dd HH-mm-ss";
  private static final String ISO_LAN_CODE = "en";
  /**
   * <p>The main constructor for this class creates an instance of the version
   * number for JBroFuzz.</p>
   * @param ver_int int
   */
  public Version(int ver_int) {

    // Format date in ISO8601 format
    Date currentTime = new Date();
    Locale currentLocale = new Locale(ISO_LAN_CODE);
    SimpleDateFormat dateTime = new SimpleDateFormat(DATE_FORMAT, currentLocale);
    runningDate = dateTime.format(currentTime);

    if((ver_int > 0) && (ver_int < 100)) {
      this.ver_int = ver_int;
    }
    else {
      this.ver_int = 0;
    }

    aboutText =
            "Java Bro Fuzzer Version:  " + this.toString() + "\n" +
            "Codename:  " + getCodeName() + "\n" +
            "____________________________________________________________\n" +
            "Running Under:  Java " + System.getProperty("java.version") + "\n" +
            "____________________________________________________________\n" +
            "subere@uncon.org   Copyright (C) 2006 \n" +
            "____________________________________________________________\n" +
            "JBroFuzz comes with ABSOLUTELY NO WARRANTY. This is free software\n" +
            "and you are welcome to redistribute it under the GNU GPL license";

      disclaimerText =
          "Disclaimer: You should only use this software to test the security\n" +
          "of your own network protocol application or those you are authorised\n" +
          "to do so. The author(s) of JBroFuzz take no legal or other\n" +
          "responsibility for any problems that might occur while running\n" +
          "JBroFuzz on a particular application or network protocol.\n";
  }
  /**
   * <p>Return the current Version as a String, typically in the form of 1.2</p>
   * @return String
   */
  public String toString() {
    final StringBuffer output = new StringBuffer(3);
    if(ver_int > 10) {
      output.append("0.");
      output.append(ver_int);
    }
    else {
      output.append((ver_int/ 10) % 10);
      output.append('.');
      output.append(ver_int % 10);
    }
    return output.toString();
  }
  /**
   * <p>Return the code name of the current version, based on the version
   * number.</p>
   * @return String the codename
   */
  private String getCodeName() {
    String codeName = "Plutus "; /* - God of Wealth (see Pluto) */
    if (ver_int == 1) {
      codeName = "Vesta "; /* - Goddess of the Home */
    }
    if (ver_int == 2) {
      codeName = "Jupiter "; /* - King of the Gods */
    }
    if (ver_int == 3) {
      codeName = "Juno "; /* - Queen of the Gods */
    }
    if (ver_int == 4) {
      codeName = "Neptune "; /* - God of the Sea */
    }
    if (ver_int == 5) {
      codeName = "Pluto "; /* - God of Death */
    }
    if (ver_int == 6) {
      codeName = "Apollo "; /* - God of the Sun */
    }
    if (ver_int == 7) {
      codeName = "Diana "; /* - Goddess of the Moon */
    }
    if (ver_int == 8) {
      codeName = "Mars "; /* - God of War */
    }
    if (ver_int == 9) {
      codeName = "Venus "; /* - Goddess of Love */
    }
    if (ver_int == 10) {
      codeName = "Cupid "; /* - God of Love */
    }
    if (ver_int == 11) {
      codeName = "Mercury "; /* - Messenger of the Gods */
    }
    if (ver_int == 12) {
      codeName = "Minerva "; /* - Goddess of Wisdom */
    }
    if (ver_int == 13) {
      codeName = "Ceres "; /* - The Earth Goddess */
    }
    if (ver_int == 14) {
      codeName = "Proserpine "; /* - Goddess of the Underworld */
    }
    if (ver_int == 15) {
      codeName = "Vulcan "; /* - The Smith God */
    }
    if (ver_int == 16) {
      codeName = "Bacchus "; /* - God of Wine */
    }
    if (ver_int == 17) {
      codeName = "Saturn "; /* - God of Time */
    }
    if (ver_int == 18) {
      codeName = "Janus "; /* - God of Doors */
    }
    if (ver_int == 19) {
      codeName = "Uranus "; /* - Father of Saturn */
    }
    if (ver_int == 20) {
      codeName = "Maia "; /* - Goddess of Growth */
    }
    if (ver_int == 21) {
      codeName = "Flora "; /* - Goddess of Flowers (see Maia) */
    }
    return codeName;
  }
  /**
   * Return the String to be displayed in the about box
   * @return String
   */
  public String getAboutText() {
    return aboutText;
  }
  /**
   * Return the String to be displayed in the disclaimer box
   * @return String
   */
  public String getDisclaimerText() {
    return disclaimerText;
  }
  /**
   * <p>Return the Date of the current launch time as a String. The date is
   * returned in the following format: DDD yyyy-MM-dd HH-mm-ss.</p>
   * @return String
   */
  public String getDate() {
    return runningDate;
  }

}
