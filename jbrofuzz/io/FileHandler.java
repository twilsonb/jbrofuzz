/**
 * FileHandler.java
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
package jbrofuzz.io;

import javax.swing.*;
import java.io.*;

import jbrofuzz.ui.*;
import jbrofuzz.*;
/**
 * <p>Title: Java Browser Fuzzer</p>
 *
 * <p>Description: A simple application for fuzzing URLs</p>
 *
 * @author subere@uncon.org
 * @version 0.3
 */
public class FileHandler {

  private static MainWindow g;
  // The current file used for creation
  private static File currentFile;
  // The console file used to log errors
  private static File consoleFile;
  // The file used to log exceptions
  private static File exceptionsFile;
  // The fuzz directory of operation
  private static File fuzzDirectory;
  // The snif directory of operation
  private static File snifDirectory;
  // The info directory of operation
  private static File systemDirectory;
  // A constant for counting file IO errors
  private static int errors;
  // Global constants
  private static final int FUZZ_FILE = 0;
  private static final int SNIF_FILE = 1;
  private static final int INFO_FILE = 2;
  private static final int EXCP_FILE = 3;
  // The date from the version
  private static String runningDate;
  /**
   * <p>Constructor responsible for generating the necessary directories and
   * files for the correct operation of JBroFuzz.</p>
   * @param g MainWindow
   */
  public FileHandler(MainWindow g) {
    this.g = g;
    // Set the number of errors that can potentially occur
    errors = 0;
    // Get the date
    runningDate = g.getJBroFuzz().getVersion().getDate();

    String baseDir = System.getProperty("user.dir");
    try {

      // Create the necessary directory with the obtained timestamp
      fuzzDirectory = new File(baseDir + File.separator + "jbrofuzz" +
                               File.separator + "fuzzing" + File.separator +
                               runningDate);

      snifDirectory = new File(baseDir + File.separator + "jbrofuzz" +
                               File.separator + "sniffing" + File.separator +
                               runningDate);

      systemDirectory = new File(baseDir + File.separator + "jbrofuzz" +
                               File.separator + "system");

      if (!fuzzDirectory.exists()) {
        fuzzDirectory.mkdirs();
      }
      if (!snifDirectory.exists()) {
        snifDirectory.mkdirs();
      }
      if (!systemDirectory.exists()) {
        systemDirectory.mkdirs();
      }

      consoleFile = new File(systemDirectory, "console.log");
      if (!consoleFile.exists()) {
        consoleFile.createNewFile();
      }
      appendFile(this.g, consoleFile, getFileText(INFO_FILE));

      exceptionsFile = new File(systemDirectory, "exceptions.log");
      if (!exceptionsFile.exists()) {
        exceptionsFile.createNewFile();
      }
      appendFile(this.g, exceptionsFile, getFileText(EXCP_FILE));

    } // try block
    catch (IOException e1) {

      JOptionPane.showMessageDialog(this.g,
                                    "Could not create the necessary " +
                                    "directories.\n" +
                                    "Please unsure you have write access to " +
                                    "the \ndirectory in which you are " +
                                    "running JBroFuzz.\n" + e1.getMessage(),
                                    "JBroFuzz Directory Error",
                                    JOptionPane.ERROR_MESSAGE);
      errors++;
    }
  }

  private static String getFileText(int type) {
    StringBuffer output = new StringBuffer();
        output.append("------------ ");
        output.append(runningDate);
        output.append(" ---");
    if(type == INFO_FILE) {
      output.append("[Console Information]-------------------\r\n");
      output.append(Format.SYSTEM_INFO);
      output.append("\r\n\r\n");
    }
    if(type == EXCP_FILE) {
      output.append("[Exceptions Thrown and Caught]----------\r\n");
    }
    return output.toString();
  }

  private static void appendFile(JFrame f, File fileName, String content) {
    String file = fileName.toString();
    try {
      if(errors < 3) {
        content += "\r\n";
        final boolean append = true;
        OutputStream output = new FileOutputStream(file, append);
        byte buffer[] = content.getBytes();
        output.write(buffer);
        output.close();
      }
    }
    catch (FileNotFoundException e) {
      JOptionPane.showMessageDialog(f,
                                    "Cannot find " + file
                                    + "\nUnable to Update.\n",
                                    "JBroFuzz File Save Error",
                                    JOptionPane.ERROR_MESSAGE);
      errors++;
    }
    catch (IOException e) {
      JOptionPane.showMessageDialog(f, "Cannot Save to File"
                                    + "\n" + file
                                    + "\nA File Write Error Occured",
                                    "JBroFuzz File Save Error",
                                    JOptionPane.ERROR_MESSAGE);
      errors++;
    }
  }
  /**
   * <p>Method for reading snif files that have been generated within a sniffing
   * session. Typically, the contents of the file are returned within the
   * StringBuffer. In the event of an error, the StringBuffer returned is set to
   * "". </p>
   *
   * @param f JFrame The frame within which the file is read
   * @param fileName String The string filename of the file
   * @return StringBuffer
   * @since 0.2
   */
  public static StringBuffer readSnifFile(JFrame f, String fileName) {
    StringBuffer out = new StringBuffer();
    File file;
    try {
       file = new File(snifDirectory, fileName);
    }
    catch (NullPointerException e) {
      JOptionPane.showMessageDialog(f, "Cannot Find Location"
                                    + "\n" + fileName
                                    + "\nA File Read Error Occured",
                                    "JBroFuzz File Read Error",
                                    JOptionPane.ERROR_MESSAGE);
      return new StringBuffer("");
    }
    try {
      FileReader input = new FileReader(file);
      BufferedReader bufRead = new BufferedReader(input);
      String line;
      line = bufRead.readLine();
      while(line != null) {
        out.append(line + "\n");
        line = bufRead.readLine();
      }
      bufRead.close();
    }
    catch(ArrayIndexOutOfBoundsException e) {
      JOptionPane.showMessageDialog(f, "Cannot Find Location"
                              + "\n" + fileName
                              + "\nAn Array Error Occured",
                              "JBroFuzz File Read Error",
                              JOptionPane.ERROR_MESSAGE);
      return new StringBuffer("");

    }
    catch(IOException e) {
      JOptionPane.showMessageDialog(f, "Cannot Read Location"
                                    + "\n" + fileName
                                    + "\nA File Read Error Occured",
                                    "JBroFuzz File Read Error",
                                    JOptionPane.ERROR_MESSAGE);
      return new StringBuffer("");
    }
    return out;
  }

  private static void createFile(JFrame f, String fileName, String content,
                                int fileType) {

    if (fileType == FileHandler.FUZZ_FILE) {
      try {
        if(errors < 3) {
          currentFile = new File(fuzzDirectory, fileName);
          if (!currentFile.exists()) {
            currentFile.createNewFile();
          }
          appendFile(f, currentFile, content);
        }
      }
      catch (IOException e) {
        JOptionPane.showMessageDialog(f, "Cannot Create File"
                                      + "\n" + fileName
                                      + "\nA File Create Error Occured",
                                      "JBroFuzz File Create Error",
                                      JOptionPane.ERROR_MESSAGE);
        errors++;
      }


    }

    if (fileType == FileHandler.SNIF_FILE) {
      try {
        if(errors < 3) {
          currentFile = new File(snifDirectory, fileName);
          if (!currentFile.exists()) {
            currentFile.createNewFile();
          }
          appendFile(f, currentFile, content);
        }
      }
      catch (IOException e) {
        JOptionPane.showMessageDialog(f, "Cannot Create File"
                                      + "\n" + fileName
                                      + "\nA File Create Error Occured",
                                      "JBroFuzz File Create Error",
                                      JOptionPane.ERROR_MESSAGE);
        errors++;
      }
    }

    if (fileType == FileHandler.INFO_FILE) {
      try {
        if(errors < 3) {
          currentFile = new File(systemDirectory, fileName);
          if (!currentFile.exists()) {
            currentFile.createNewFile();
          }
          appendFile(f, currentFile, content);
        }
      }
      catch (IOException e) {
        JOptionPane.showMessageDialog(f, "Cannot Create File"
                                      + "\n" + fileName
                                      + "\nA File Create Error Occured",
                                      "JBroFuzz File Create Error",
                                      JOptionPane.ERROR_MESSAGE);
        errors++;
      }
    }

    if (fileType == FileHandler.EXCP_FILE) {
      try {
        if(errors < 3) {
          currentFile = new File(systemDirectory, fileName);
          if (!currentFile.exists()) {
            currentFile.createNewFile();
          }
          appendFile(f, currentFile, content);
        }
      }
      catch (IOException e) {
        JOptionPane.showMessageDialog(f, "Cannot Create File"
                                      + "\n" + fileName
                                      + "\nA File Create Error Occured",
                                      "JBroFuzz File Create Error",
                                      JOptionPane.ERROR_MESSAGE);
        errors++;
      }
    }

  }
  /**
   * <p>Method for writting a new fuzz file under the created fuzzing directory.
   * The file name and content is specified as a string input to the method.
   * The location where this file is saved is within the directory jbrofuzz\
   * fuzzing\[session date]\ . </p>
   * <p>If the file exists, the content is simply appended to the end of the
   * file.</p>
   *
   * @param name String The file name
   * @param content String The content of the file
   */
  public static void writeFuzzFile(String name, String content) {
    createFile(g, name, content, FileHandler.FUZZ_FILE);
  }
  /**
   * <p>Method for writting a new snif file under the created sniffing
   * directory. The file name and content is specified as a string input to the
   * method. The location where this file is saved is within the directory
   * jbrofuzz\sniffing\[session date]\ . </p>
   * <p>If the file exists, the content is simply appended to the end of the
   * file.</p>
   *
   * @param name String The file name
   * @param content String The content of the file
   */
  public static void writeSnifFile(String name, String content) {
    createFile(g, name, content, FileHandler.SNIF_FILE);
  }
  /**
   * <p>Method for appending content to the exceptions log. The content is
   * specified as a string input to the method. The location where this file is
   * saved is within the directory jbrofuzz\system\exceptions.log . </p>
   * <p>If the file does not exist, the file will firstly get created and then
   * data will be appended to the end of it.</p>
   *
   * @param content String The content of the file
   */
  public static void writeExceptionsFile(String content) {
    createFile(g, exceptionsFile.toString(), content, FileHandler.EXCP_FILE);
  }
  /**
   * <p>Method for appending content to the console log. The content is
   * specified as a string input to the method. The location where this file is
   * saved is within the directory jbrofuzz\system\console.log . </p>
   * <p>If the file does not exist, the file will firstly get created and then
   * data will be appended to the end of it.</p>
   *
   * @param content String The content of the file
   */
  public static void writeConsoleFile(String content) {
    createFile(g, consoleFile.toString(), content, FileHandler.INFO_FILE);
  }

}
