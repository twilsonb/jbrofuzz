/**
 * FileHandler.java 0.4
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
package org.owasp.jbrofuzz.io;

import java.io.*;
import java.util.*;

import javax.swing.*;

import org.owasp.jbrofuzz.ui.*;
import org.owasp.jbrofuzz.ver.*;
/**
 * <p>Class responsible for all File Creation.</p>
 *
 * @author subere (at) uncon . org
 * @version 0.5
 */
public class FileHandler {
  private static FrameWindow g;
  // The current file used for creation
  private static File currentFile;
  // The fuzz directory of operation
  private static File fuzzDirectory;
  // The snif directory of operation
  private static File snifDirectory;
  // The info directory of operation
  private static File webEnumDirectory;
  // A constant for counting file IO errors
  private static int errors;
  // Global constants
  private static final int FUZZ_FILE = 0;
  private static final int SNIF_FILE = 1;
  private static final int WEBD_FILE = 2;
  // The date from the version
  private static String runningDate;
  // A counter for the number of sessions being run to generate a file
  private static int session;
  /**
   * <p>Constructor responsible for generating the necessary directories and
   * files for the correct operation of JBroFuzz.</p>
   * @param g MainWindow
   */
  public FileHandler(FrameWindow g) {
    FileHandler.g = g;
    // Set the number of errors that can potentially occur
    errors = 0;
    // Set the current working session
    session = 1;
    // Get the date
    runningDate = Format.DATE;
    //g.getJBroFuzz().getVersion().getDate();

    String baseDir = System.getProperty("user.dir");
    // try {

    // Create the necessary directory with the obtained timestamp
    fuzzDirectory = new File(baseDir + File.separator + "jbrofuzz" +
                             File.separator + "fuzzing" + File.separator +
                             runningDate);

    snifDirectory = new File(baseDir + File.separator + "jbrofuzz" +
                             File.separator + "sniffing" + File.separator +
                             runningDate);

    webEnumDirectory = new File(baseDir + File.separator + "jbrofuzz" +
                                File.separator + "web-dir" + File.separator +
                                runningDate);

    int failedDirCounter = 0;

    if (!fuzzDirectory.exists()) {
      boolean success = fuzzDirectory.mkdirs();
      if (!success) {
        g.log("Failed to create \"fuzzing\" directory");
        failedDirCounter++;
      }
    }
    if (!snifDirectory.exists()) {
      boolean success = snifDirectory.mkdirs();
      if (!success) {
        g.log("Failed to create \"sniffing\" directory");
        failedDirCounter++;
      }

    }
    if (!webEnumDirectory.exists()) {
      boolean success = webEnumDirectory.mkdirs();
      if (!success) {
        g.log("Failed to create \"web-dir\" directory");
        failedDirCounter++;
      }
    }

    if (failedDirCounter >= 3) {
      g.log("\tToo many directories could not be created! Are you launching me through your browser?");
      g.log("\tTry \"java -jar jbrofuzz-" + Format.VERSION +
            ".jar\" on command line...");
      failedDirCounter = 0;
    }
  }

  private static void appendFile(File fileName, String content) {
    String file = fileName.toString();
    try {
      if (errors < 3) {
        content += "\r\n";
        final boolean append = true;
        OutputStream output = new FileOutputStream(file, append);
        byte buffer[] = content.getBytes();
        output.write(buffer);
        output.close();
      }
    }
    catch (FileNotFoundException e) {
      g.log("Cannot find " + file + "Unable to Update");
      errors++;
    }
    catch (IOException e) {
      g.log("Cannot Save to File" + file + "A File Write Error Occured");
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
      JOptionPane.showMessageDialog(f,
                                    "Cannot Find Location" + "\n" + fileName + "\nA File Read Error Occured",
                                    "JBroFuzz File Read Error",
                                    JOptionPane.ERROR_MESSAGE);
      return new StringBuffer("");
    }
    try {
      FileReader input = new FileReader(file);
      BufferedReader bufRead = new BufferedReader(input);
      String line;
      line = bufRead.readLine();
      while (line != null) {
        out.append(line + "\n");
        line = bufRead.readLine();
      }
      bufRead.close();
    }
    catch (ArrayIndexOutOfBoundsException e) {
      JOptionPane.showMessageDialog(f,
                                    "Cannot Find Location" + "\n" + fileName + "\nAn Array Error Occured",
                                    "JBroFuzz File Read Error",
                                    JOptionPane.ERROR_MESSAGE);
      return new StringBuffer("");

    }
    catch (IOException e) {
      JOptionPane.showMessageDialog(f,
                                    "Cannot Read Location" + "\n" + fileName + "\nA File Read Error Occured",
                                    "JBroFuzz File Read Error",
                                    JOptionPane.ERROR_MESSAGE);
      return new StringBuffer("");
    }
    return out;
  }

  private static void createFile(String fileName, String content, int fileType) {

    if (fileType == FileHandler.FUZZ_FILE) {
      try {
        if (errors < 3) {
          currentFile = new File(fuzzDirectory, fileName);
          if (!currentFile.exists()) {
            boolean success = currentFile.createNewFile();
            if (!success) {
              g.log("Failed to create file");
            }
          }
          appendFile(currentFile, content);
        }
      }
      catch (IOException e) {
        g.log("Cannot Create File" + "\n" + fileName + " A File Error Occured");
        errors++;
      }


    }

    if (fileType == FileHandler.SNIF_FILE) {
      try {
        if (errors < 3) {
          currentFile = new File(snifDirectory, fileName);
          if (!currentFile.exists()) {
            boolean success = currentFile.createNewFile();
            if (!success) {
              g.log("Failed to create file");
            }
          }
          appendFile(currentFile, content);
        }
      }
      catch (IOException e) {
        g.log("Cannot Create File" + "\n" + fileName + " A File Error Occured");
        errors++;
      }
    }

    if (fileType == FileHandler.WEBD_FILE) {
      try {
        if (errors < 3) {
          currentFile = new File(webEnumDirectory, fileName);
          if (!currentFile.exists()) {
            boolean success = currentFile.createNewFile();
            if (!success) {
              g.log("Failed to create file");
            }
          }
          appendFile(currentFile, content);
        }
      }
      catch (IOException e) {
        g.log("Cannot Create File" + "\n" + fileName + " A File Error Occured");
        errors++;
      }
    }
  }

  /**
   * <p>Method for writting a new fuzz file within the created fuzzing
   * directory. The content of the file is specified as a String input to the
   * method.
   * The location where this file is saved is within the directory jbrofuzz\
   * fuzzing\[session date]\ . </p>
   * <p>The two long values being passed are responsible for the file name.</p>
   * <p>If the file exists, the content is simply appended to the end of the
   * file.</p>
   *
   * @param content String
   * @param name String
   */
  public static void writeFuzzFile(String content, String name) {
    // Actually create the file
    createFile(name + ".txt", content, FileHandler.FUZZ_FILE);
  }

  /**
   * <p>Method for writting a new snif file within the created sniffing
   * directory. The file name and content is specified as a string input to the
   * method. The location where this file is saved is within the directory
   * jbrofuzz\sniffing\[session date]\ . </p>
   * <p>If the file exists, the content is simply appended to the end of the
   * file.</p>
   *
   * @param name String
   * @param content String
   */
  public static void writeSnifFile(String name, String content) {
    // Actually create the file
    createFile(name + ".txt", content, FileHandler.SNIF_FILE);
  }

  /**
   * <p>Method for writting a new web directories file wtin the created
   * web-dir directory. The file name and content is specified as a string
   * input to the method. </p>
   * <p>The location where this file is saved is within the directory
   * jbrofuzz\web-dir\[session date]\ . </p>
   * <p>If the file exists, the content is simply appended to the end of the
   * file.</p>
   *
   * @param name String The name of the file
   * @param content String The content to be written to disk
   */
  public static void writeWebDirFile(String name, String content) {
    createFile(name + ".csv",content, FileHandler.WEBD_FILE);
  }

  /**
   * <p>Method for returning the contents of a generator file as a Vector
   * @param generatorFile String
   * @return Vector
   */
  public static Vector readGenerators(String generatorFile) {
    final int maxLines = 1024;
    final int maxLineLength = 256;
    int line_counter = 0;
    Vector file = new Vector();
    try {
      BufferedReader in = new BufferedReader(new FileReader(generatorFile));
      String line = in.readLine();
      line_counter++;
      while ((line != null) && (line_counter < maxLines)) {
        if (line.length() > maxLineLength) {
          line = line.substring(0, maxLineLength);
        }
        file.add(line);
        line = in.readLine();
        line_counter++;
      }
      in.close();
    }
    catch (IOException e1) {
      g.log("Generator file: " + generatorFile + " could not be found");
    }
    file.trimToSize();
    return file;
  }
}
