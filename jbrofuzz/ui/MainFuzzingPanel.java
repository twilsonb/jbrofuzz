/**
 * MainFuzzingPanel.java
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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import jbrofuzz.*;
import jbrofuzz.ui.util.*;
import jbrofuzz.fuzz.*;
/**
 * <p>The main fuzzing panel, displayed within the MainWindow.</p>
 * @author subere@uncon.org
 * @version 0.3
 */
public class MainFuzzingPanel extends JPanel {

  // The frame that the sniffing panel is attached
  private final MainWindow m;
  // The JPanels
  private final JPanel outputPanel;
  // The JTextArea
  private final JTextArea target, port, message, generatorTable, outputTable;
  // The JButtons
  private final JButton buttonGeneneratorAdd, buttonGeneneratorRemove;
  private final JButton buttonFuzzStart, buttonFuzzStop;
  // The swing worker used when the button "fuzz" is pressed
  private SwingWorker worker;
  // A counter for the number of times fuzz has been clicked
  private int counter;

  private final String ADD_GEN_STR = "Add Generator";

  public MainFuzzingPanel(MainWindow m) {
    super();
    setLayout(null);
    setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(" Fuzzing "),
        BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    this.m = m;
    // The counter being set
    counter = 0;


    // The target panel
    JPanel targetPanel = new JPanel();
    targetPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(" Target "),
        BorderFactory.createEmptyBorder(1, 1, 1, 1)));

    target = new JTextArea(1, 1);

    target.setEditable(true);
    target.setVisible(true);
    target.setFont(new Font("Verdana", Font.PLAIN, 12));
    target.setLineWrap(false);
    target.setWrapStyleWord(true);
    target.setMargin(new Insets(1, 1, 1, 1));

    JScrollPane targetScrollPane = new JScrollPane(target);
    targetScrollPane.setVerticalScrollBarPolicy(
        JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    targetScrollPane.setHorizontalScrollBarPolicy(
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    targetScrollPane.setPreferredSize(new Dimension(220, 20));
    targetPanel.add(targetScrollPane);


    targetPanel.setBounds(10, 20, 240, 60);
    add(targetPanel);
    // The port panel
    JPanel portPanel = new JPanel();
    portPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(" Port "),
        BorderFactory.createEmptyBorder(1, 1, 1, 1)));

    port = new JTextArea(1, 1);

    port.setEditable(true);
    port.setVisible(true);
    port.setFont(new Font("Verdana", Font.PLAIN, 12));
    port.setLineWrap(false);
    port.setWrapStyleWord(true);
    port.setMargin(new Insets(1, 1, 1, 1));

    JScrollPane portScrollPane = new JScrollPane(port);
    portScrollPane.setVerticalScrollBarPolicy(
        JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    portScrollPane.setHorizontalScrollBarPolicy(
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    portScrollPane.setPreferredSize(new Dimension(50, 20));
    portPanel.add(portScrollPane);

    portPanel.setBounds(250, 20, 60, 60);
    add(portPanel);
    // The message panel
    JPanel requestPanel = new JPanel();
    requestPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(" Request "),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    message = new JTextArea();

    message.setEditable(true);
    message.setVisible(true);
    message.setFont(new Font("Verdana", Font.PLAIN, 12));
    message.setLineWrap(true);
    message.setWrapStyleWord(true);
    message.setMargin(new Insets(1, 1, 1, 1));

    JScrollPane messageScrollPane = new JScrollPane(message);
    messageScrollPane.setVerticalScrollBarPolicy(
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    messageScrollPane.setPreferredSize(new Dimension(480, 160));
    requestPanel.add(messageScrollPane);

    requestPanel.setBounds(10, 80, 500, 200);
    add(requestPanel);
    // The top buttons
    buttonGeneneratorAdd = new JButton(ADD_GEN_STR);
    buttonGeneneratorAdd.setBounds(550, 30, 130, 20);
    add(buttonGeneneratorAdd);
    buttonGeneneratorAdd.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        generatorAdd();
      }
    }
    );
    buttonGeneneratorRemove = new JButton("Remove Generator");
    buttonGeneneratorRemove.setBounds(700, 30, 150, 20);
    add(buttonGeneneratorRemove);
    buttonGeneneratorRemove.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        generatorRemove();
      }
    }
    );

    // The generator panel
    JPanel generatorPanel = new JPanel();
    generatorPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(" Generator (See Definitions)"),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    generatorTable = new JTextArea();
    generatorTable.setEditable(false);
    generatorTable.setFont(new Font("Monospaced", Font.PLAIN, 14));

    JScrollPane generatorScrollPane = new JScrollPane(generatorTable);
    generatorScrollPane.setVerticalScrollBarPolicy(
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    generatorScrollPane.setPreferredSize(new Dimension(180, 100));
    generatorPanel.add(generatorScrollPane);

    generatorPanel.setBounds(650, 60, 200, 150);
    add(generatorPanel);
    // The fuzz buttons
    buttonFuzzStart = new JButton("Fuzz!");
    buttonFuzzStart.setBounds(700, 220, 70, 20);
    add(buttonFuzzStart);
    buttonFuzzStart.addActionListener
        (
            new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        worker = new SwingWorker() {
          public Object construct() {
            buttonFuzzStart.setEnabled(false);
            buttonFuzzStop.setEnabled(true);
            getMainMenuBar().setFuzzStartEnabled(false);
            getMainMenuBar().setFuzzStopEnabled(true);
            getMainWindow().setTabSniffingEnabled(false);
            fuzzStart();
            return "start-window-return";
          }
          public void finished() {
            buttonFuzzStart.setEnabled(true);
            buttonFuzzStop.setEnabled(false);
            getMainMenuBar().setFuzzStartEnabled(true);
            getMainMenuBar().setFuzzStopEnabled(false);
            getMainWindow().setTabSniffingEnabled(true);
          }
        };
        worker.start();
      }
    }
    );
    buttonFuzzStop = new JButton("Stop");
    buttonFuzzStop.setEnabled(false);
    buttonFuzzStop.setBounds(780, 220, 70, 20);
    add(buttonFuzzStop);
    buttonFuzzStop.addActionListener(
        new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        fuzzStop();
        buttonFuzzStart.setEnabled(true);
        buttonFuzzStop.setEnabled(false);
        getMainMenuBar().setFuzzStartEnabled(true);
        getMainMenuBar().setFuzzStopEnabled(false);
        getMainWindow().setTabSniffingEnabled(true);
      }
    });
    // The output panel
    outputPanel = new JPanel();
    outputPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(" Output (Last 1000 Lines) "),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    outputTable = new JTextArea();

    outputTable.setEditable(false);
    outputTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
    outputTable.setLineWrap(false);
    outputTable.setWrapStyleWord(true);
    outputTable.setMargin(new Insets(3, 3, 3, 3));


    JScrollPane outputScrollPane = new JScrollPane(outputTable);
    outputScrollPane.setVerticalScrollBarPolicy(
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    outputScrollPane.setPreferredSize(new Dimension(820, 140));
    outputPanel.add(outputScrollPane);

    outputPanel.setBounds(10, 280, 840, 180);
    add(outputPanel);

    // Some value defaults
    target.setText("127.0.0.1");
    port.setText("80");
    message.setText("GET / HTTP/1.0\n\n");
  }

  public void fuzzStart() {
    // Check to see if a message is present
    if(message.getText().equals("")) {
      JOptionPane.showMessageDialog(this,
                                    "The request field is blank.\n" +
                                    "Specify a request\n",
                                    "Empty Request Field",
                                    JOptionPane.INFORMATION_MESSAGE);
      return;
    }
    // Increment the counter
    counter++;
    counter %= 100;
    // Update the border of the output panel
    outputPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(" Output (Last 1000 Lines)  " +
                                         "Loggin in folder (" +
                                         getJBroFuzz().getVersion().getDate() +
                                         ") Session " + counter),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)));


    // Clear the text of the output pane
    outputTable.setText("");
    final String original = generatorTable.getText();


    // If no generator fuzz points exist, just send a single request
    if(!(original.contains("\n"))) {
      getJBroFuzz().setGenerator(message.getText(), 0, 0, "ZER");
      getJBroFuzz().runGenerator();
    }
    else {
      // Get the fuzz points
      final String[] fuzzPoints = original.split("\n");
      for (int i = 0; i < fuzzPoints.length; i++) {
        final String[] fuzzEntry = fuzzPoints[i].split(" : ");

        final int start = Integer.parseInt(fuzzEntry[0]);
        final int finish = Integer.parseInt(fuzzEntry[1]);

        getJBroFuzz().setGenerator(message.getText(), start, finish,
                                   fuzzEntry[2]);
        // Run the generator, that also performs the connection requests
        getJBroFuzz().runGenerator();
      }
    }
  }

  public void fuzzStop() {
    getJBroFuzz().stopGenerator();
  }

  public String getTargetText() {
    return target.getText();
  }

  public String getPortText() {
    return port.getText();
  }

  public String getMessageText() {
    return message.getText();
  }

  public int getFuzzCount() {
    return counter;
  }

  public void setOutputText(String s) {
    final int lines = outputTable.getLineCount();
    // Refresh after 1000 lines
    if(lines > 1000) {
      outputTable.setText("");
    }
    outputTable.append(s);

    final int caret = outputTable.getText().length();
    outputTable.setCaretPosition(caret);
  }

  public void generatorAdd() {
    // Check to see what text has been selected
    String selectedText;
    try {
      selectedText = message.getSelectedText();
    }
    catch(IllegalArgumentException e) {
      JOptionPane.showInputDialog(this,
          "An exception was thrown while attempting to get the selected text",
          ADD_GEN_STR,
          JOptionPane.ERROR_MESSAGE);
      selectedText = "";
    }
    // If no text has been selected, prompt the user to select some text
    if(selectedText == null) {
      JOptionPane.showMessageDialog(this,
                              "Select (highlight) a text range \nfrom the Request field",
                              ADD_GEN_STR,
                              JOptionPane.ERROR_MESSAGE);
    }
    // Else find out the location of where the text has been selected
    else {
      final int sPoint = message.getSelectionStart();
      final int fPoint = message.getSelectionEnd();

      // Then prompt the user for the type of fuzzer
      String selectedValue = (String) JOptionPane.showInputDialog(this,
          "Select the type \nof fuzzing generator:", ADD_GEN_STR,
          JOptionPane.INFORMATION_MESSAGE, null,
          Format.GENERATORS, Format.GENERATORS[0]);
      // And finally add the fuzzing point
      if(selectedValue != null) {
        selectedValue = selectedValue.substring(0,3);

        generatorTable.append(sPoint + " : " + fPoint + " : ");
        generatorTable.append(selectedValue + "\n");

      }
    }
  }

  public void generatorRemove() {
    String s = generatorTable.getText();
    if(! (s.contains("\n"))) {
      return;
    }
    final String [] fuzzPoints = s.split("\n");

    final String selectedFuzzPoint = (String) JOptionPane.showInputDialog(this,
        "Select the fuzz point to remove:", "Remove Generator",
        JOptionPane.INFORMATION_MESSAGE, null,
        fuzzPoints, fuzzPoints[0]);

    if(selectedFuzzPoint != null) {
      final int start = s.indexOf(selectedFuzzPoint);
      final int end = start + selectedFuzzPoint.length();
      final int total = s.length();

      s = s.substring(0, start) + s.substring(end + 1, total);
      generatorTable.setText(s);
    }
  }

  public void setFuzzStart(final boolean b) {
    buttonFuzzStart.setEnabled(b);
  }

  public void setFuzzStop(final boolean b) {
    buttonFuzzStop.setEnabled(b);
  }

  public MainMenuBar getMainMenuBar() {
    return m.getMainMenuBar();
  }

  public JBroFuzz getJBroFuzz() {
    return m.getJBroFuzz();
  }

  public MainWindow getMainWindow() {
    return m;
  }
}
