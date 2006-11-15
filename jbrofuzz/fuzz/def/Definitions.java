/**
 * Definitions.java
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

import java.util.Vector;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

import jbrofuzz.*;
/**
 * <p>Description: Class holding the definitions of the generators. </p>
 *
 * <p>Not used in current version</p>
 *
 * <p>Company: </p>
 *
 * @author subere@uncon.org
 * @version 0.1
 */
public class Definitions {
  private final JBroFuzz jbrofuzz;

  private Vector definitions;
  private Vector file;

  public Definitions(JBroFuzz jbrofuzz) {
    this.jbrofuzz = jbrofuzz;

    definitions = new Vector();

    file = new Vector();
    String fileName = "def/jbrofuzz-generators";
    try {
      BufferedReader in = new BufferedReader(new FileReader(fileName));
      String line = in.readLine();
      while (line != null) {
        file.add(line);
        line = in.readLine();
      }
      in.close();
    }
    catch (IOException e1) {
      System.out.println("An IO Exception was thrown");
    }
    for (int j = 0; j < file.size(); j++) {
        String s = (String) file.elementAt(j);
        if(s.startsWith("Generator")) {
          String temp1 = s.substring(10);
          s = (String) file.elementAt(j+1);
          if(s.startsWith("Category")) {
            String temp2 = s.substring(9);
            s = (String) file.elementAt(j+2);
              String tempArray [] = {temp1, temp2, s};
              definitions.add(tempArray);
              j = j + 2;
          }
        }
    }
    getAllNames();
  }
  /**
   * Get all the names within the definitions
   */
  public void getAllNames() {
    for (int i = 0; i < definitions.size(); i++) {
      String temp [] = (String []) definitions.elementAt(i);
      // System.out.println(temp[0]);
    }
  }
  /**
   * Get fields from name
   * @return JBroFuzz
   */
  public Vector getFields(String name) {
    Vector fields = new Vector();
    for (int i = 0; i < definitions.size(); i++) {
      String temp [] = (String []) definitions.elementAt(i);
      if(temp[0].equals(name)) {
        String f [] = temp[2].split(" ");
        for(int j = 0; j < f.length; j++) {
          fields.add(f[j]);
          System.out.println(f[j]);
        }
      }
    }
    return fields;
  }

  public JBroFuzz getJBroFuzz() {
    return jbrofuzz;
  }
}
