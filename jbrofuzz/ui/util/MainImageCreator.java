/**
 * MainImageCreator.java
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
package jbrofuzz.ui.util;

import javax.swing.ImageIcon;

import jbrofuzz.ui.*;

/**
 * <p>Description: The main menu bar attached to the main window.</p>
 *
 * @author subere@uncon.org
 * @version 0.2
 */
public class MainImageCreator {
  // The frame that the image creator attached
  private MainWindow mainWindow;

  /**
   * The frame icon (png file) displayed on the top left of the frame.
   * @since 0.3
   */
  private static final String frameIconText =
   "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAALGPC/xhBQAAAp1J" +
   "REFUOE+lk1tPE1EUhaHQltJ22tI77fReoFfoDVoEKRQoFEEFEhRBTDDEEKM+iUiAakj8ASYm" +
   "JiQmvvjqP/zcrYThWSfZM8nMnO+svfY6ve12m57/uToA+3AOb3yW6MQ2zsAExdU2Nn+enl5d" +
   "B/63evtQnAlKiycMKH5ihS26m3duerMLxZ1kKFjEMhQjPrlHsrSLzjBwA+jFYHWSmTnCH6li" +
   "tgfQW70aQPGnUFwJgvFp3IEiRouTUvNMdlK7gD6DhXB2nUh+C59aJFU9QDdg1QD9gw4MJhue" +
   "cAmzK0You0Gs/JTA2Cr6ARv+eB2zKDPKf5HsGpHMBkYlqAFS1UPsvjG8kWksDpVgaoVaq004" +
   "0yKQXMAdqlJpXeKJVnF4R9DpTfT0GzRA7cEVpeUz0tOHeEJTzO58Y1R8mHr4hXT1JY39n+LJ" +
   "EwrSlsHikvKIZ6N3AOtX1G4qO/+WdOU5jd1rFvZ/ECtuU1m5YO3oN2abXxQ1SMi0RqZeaIDC" +
   "yiehfyC//J7szDGp2iHh/AaJ4hYLosYh7RUFXGyeUm5d3NSVBgiNbxIYbeAS+UarW2CnTDY/" +
   "Ut/5jjNYoLJ2xeL+L9zRGqXW+a3a2xyY7Sr9JgfuSIXK+mdi+UfMbX6lsnpJrv6Gxb1rUiJ5" +
   "ZPIAr7SQm33FpHy7BXRnbbIyVt7DIiHxxO6TLD8jmJxHzayQESPH517jE5VqZklG6MWXqGsA" +
   "iyPUTZpHLaN4EpicITyRe13T3GqJspioNymSCUVaXSI41sRou5NE1/AEvvCUZD1Kvv5OEreN" +
   "0eykR6JsHgqTm++828IwaCcuz9LSCXZvRlMQTq9jFenu4Djp+8dCD0hQ5Bzo+mSREzX3mErz" +
   "nKFAAV+khksO33B0RgN0zPjX+gPK6Z2eb2W0vwAAAABJRU5ErkJggg==";

  /**
   * The exit image is a gif file in base 64.
   * @since 0.1
   */
  private static final String exitImageText =
      "R0lGODlhDwAPAOYAAP////f//+///+/39/fv7+/v7+/v3u/m5t7m5tbm5ube3tbe3s7Ozta9" +
      "vea1tcW1tf+UlOacnLWtrdacnP+EhPeEhP9zc/dzc+9zc/9ra/dra+9ra/9jY85zc8Vzc9Zr" +
      "a+ZjY71zc/daWv9SUtZjY/dSUt5aWu9SUuZKSu9CQrVaWv86OuZCQt5CQu86OrVSUv8xMeY6" +
      "Or1KSsVCQv8pKc46OqVKSrVCQv8hIc4pKe8ZGbUxMXtCQu8QEIw6OvcICKUpKd4QEKUhId4I" +
      "CLUZGe8AAIQpKc4ICMUICNYAAK0QEL0ICM4AAKUQEHshIWspIcUAAJwQEL0AAJQQELUAAIwQ" +
      "EJwICK0AAKUAAJwAAJQAAGsQEIwAAFoQEFoICAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
      "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
      "AAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAAAwALAAAAAAPAA8AAAjKABkIFHiiYMGBCBkUrEAB" +
      "goULGE4kPLEhAwQIFCxkGHGQoIgRG0SsoEFjRQoYLCQqhHHiQYciMF9IQKFjB4MPLXCwUCDA" +
      "Q5IXCQCAGJLDCIkgOnq0OBCggQGhSY4g8fEBSZAhSWoUeGqCCRMkS4x8WILkCJQXWwF4kAKF" +
      "yhUnH4iwtRE0wgEBIdw2ecLgxhUgCwCQoCKDqYosVQTeaGJlwocrkGU4ADKly8AbUbRk2ZxF" +
      "C5cqWxLeqDKFi+kqVSwnFMhjy5YuqgcGBAA7";
  /**
   * The stop image is a gif file in base 64.
   * @since 0.1
   */
  private static final String stopImageText =
      "R0lGODlhDwAPAHAAACH5BAEAAHoALAAAAAAPAA8AhgAAAFu83Ve93QGgwgB2kABcdwBPalXB" +
      "40C43ACWtgBviQBYcAuu1xWu2ACSsgFddgBQYA+75w6s0wCIqABwiABZcgCdvQCevwCJqABv" +
      "iABddgCpzQCcvACDoABpggCMrwCHqgB9mQBpgQBVbwCVvACHqAB5kwBjegBUbACHpgB+lwBu" +
      "kQBmfQBUbQBQYgCNqgB8lwBsjABieQBSawB6jQBtjABnfwBbdQBUaABQWACDlwBtiwBlfQBY" +
      "cQBSYQRuiABhfgBefABabQBSYwBGWANvjABdegBYaQhneQBacwBaagBSZQBQWwBDVQZqgABa" +
      "cgBZagBRYgBNWQhjewBXZwBNWwBBUAZnfgBOWABKVxBZcwBQYQBLWQBEUwJBUAtcdQBQXgBK" +
      "WgBBUhBaaQBKWQBLUwdBSAteawBIWQBJUAFCUgBBUQdCSw82SAtabABHVwtBSRpSZAJDUgFD" +
      "UgpDSxI7SxRSZANDUAtCSxI8SwAAAAAAAAAAAAAAAAAAAAAAAAeEgHqCg4SFegICAzA3gwcI" +
      "HB6MegwNHzQngxEMIJGCHBycknobHDqdeh8gMKd6oDAnOIKqK7CDqh61ejSXN7GCpie9gh5B" +
      "N8KCNMU4BoLBODhNg5HP0XqwODk5g9dN2npaOdDVer05TdV25uaDdt1NZoLY5/DN5u+CcfNt" +
      "7O9mcIYABwUCADs=";
  /**
   * The start image is a gif file in base 64.
   * @since 0.1
   */
  private static final String startImageText =
      "R0lGODlhDwAPAHAAACH5BAEAAGQALAAAAAAPAA8AhgAAAFnE3LPX10u1zkHQ8kvS8aLAykOm" +
      "tzOw0TOwyCmguzift7XBwjyWpiGWsSiWsiGOpyKFmRl2iEmKmqe3ujqGlhuHmRt9lhl8kB12" +
      "iRpuhg5kdgxZaTx1gLG7ujl9jhl3hxluhxRtgBNtfxBbbBBTZA5RYwxIVWqNla27uUN7gw9j" +
      "ew9new9ichBacxBcbBBSZBBSYRBNWRBJWQw/TgxDUFuCi0NyhA9aahBfbhBWaBBSYxBSYBBQ" +
      "XBBIWQ5JVg9BUQc4RUFeYpuhoENzeg9SXxBSXxBRWxBIWhBKWgxKWAo5RktpbomTkEJ0ew9K" +
      "Wg5KWAxJVgw8S0lma6qqsEtyeRBCURBBUQw/UBFGVjhRU7y8u0tscwxATxJBTkVaYre0tEVk" +
      "bFxkZcbGxgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
      "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAdwgGSCg4SFZAEChooDBAQG" +
      "ioQHCAkKC2OQZA0OChAWEhOQFZ4XEiIbHB+GH6WmLhwcJycohCobIi4uG0g6sUEnNoIqOhw6" +
      "R8VZJ1lBX0OCRCbFR1nIQWGFRMnIJ9WKRNNYWVoMkFxLS1+YgmFi6e2GgQA7";
  /**
   * The add image is a png file in base 64.
   * @since 0.1
   * @version 0.3
   */
  private static final String addImageText =
      "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAACVUlEQVR4nH2SO2tUYRCGn/nO" +
      "dy7Zs4ludKNbGFBZoqiohWAjCAp2FgpWIuJfEEQs7cTSfyC2KlgIlsHCSyXiBS/BIImbjcaN" +
      "ezY5e87u941FFFYR327gnWFm3ke894yoOcj7pz7dvHkrnp1Fu13CTZt53W6zevFC89zly3OA" +
      "/jZb/pKqN2ltE9sbDUySIFMN2iiLef9vKxZojtTHjTF71FqcDVHnsc7hnCKhLQD5o7n/9dv1" +
      "7MuXF6CPJAjuF1nWcL0ewfgEfmkJREmiCP+9cyxrte5qUez33pdBtfpaPty+o/FwQJAkIKBl" +
      "zoQxpO/nGD59hqlvI6sktJzDbpsiqtcZ5DmPP85hRYTGoYME49WNm9fXkK9LuIUFsBanjkqa" +
      "0pzaim9sJ5jewWA9p3j1BosIviwIyghU8cUAKQcwlkIUg1c2AhGIQggtRDFBXMFu7Aqogv76" +
      "iIKJQjSOEfUoiurvkAREQBSjziFGwBiwAWIEEUFsgFRi1G0MNHi0KKAsoSgZ5jnWVhI6S8uE" +
      "qxlqwPX7xN9XGA8EKmOYXkm2lrP4YY60HCBZj/Vul9pkDSmz7FrZzfYBj8UGYdnNTv64d/f0" +
      "9OQE/u07zOIyL1dWeH740PL5q1cOFL3ejbLf3zVer5+wNk1f2TSdBVaBGVutPsmi5DRDj0Qx" +
      "iFAWfexk7WhSq3WSWu3SKGEPRqCZVO+PGGPAhoi1EAQYMagbTgPz/2O7JyKDtc4K7XaBX1jA" +
      "f/5Mu9UC5yv/YntU38KxsQ47d7E4P4+Z2YvubrIF5cDZMw8BM2r+CQbeCNwWeEAbAAAAAElF" +
      "TkSuQmCC";

  /**
   * The remove image is a png file in base 64.
   * @since 0.1
   */
  private static final String removeImageText =
      "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAGCAIAAACTu1PVAAAABGdBTUEAALGPC/xhBQAAARhJ" +
      "REFUKFNjfHH02PsLF5hZ2f///8/IyMDEyMzAyAAEIIKR8T8jwz8Ghv9Amonp35+/DOdaWx9V" +
      "VX6bMePLlMlfp035MXP6z1kzf86Z+X32TCD7x+RJn9vbP9XVfqmouBsXx3Curf1FSdH/KZP/" +
      "T+j739f9r6v1X2P9n/Ly3zm5vxITfoWF/fLx/+Xm+dfN876hEcO5lpbHHp7/U1N/Jyb+iY7+" +
      "FRz2yy/wt4/vL2/fXz6+v339fwUG/QkO+Rca/sDGluF0bd1NQ+NvAaGf/YO+BoX9jIj9FR33" +
      "My7uZ3wiEP2IjfsaHvk5JPJbRMxNKxuGS0uWbAgK3pOUvCc5eV96xqGs7EPZOXB0MDNrf1r6" +
      "nuQUINocHgkApraQtS2/P7kAAAAASUVORK5CYII=";

  /**
   * The help image is a gif file in base 64.
   * @since 0.1
   */
  private static final String helpImageText =
      "R0lGODlhDwAPAHAAACH5BAEAAEYALAAAAAAPAA8AhgAAAAAAOAAAPAAAMQAAcwAAYwAAPwAA" +
      "WgAA4gAA/wAA0gAAfwAcfgBOeAAAuFVV/3h4/wcH/wAA5wAAZAA8ngCl/1hY/3Z2/2lp/4uL" +
      "/wAAtAAAcQAnlABwxQBf/5aW/0tL/+Hh/wBarQCH/xoa/w0N/62t/zQ0/wAA8gAAhQBYqwCG" +
      "+wAG/8bG/w4O/wAAlgB4vABp/ygo/wAAqQAAJZ+f/wAAeAAAfQBarAB86gAf/wAA9gAAywAA" +
      "eQCWywBL/wAAxwAAvAAAtgA1mgAarQAAkQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
      "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
      "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
      "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAdggEaCg4SFhgOIhoQHBygJ" +
      "CIoMDUEJDxAJKAeGQxWVEBgZCUGKHTEfIAkhCTOKKiMuCSYnCQkphq60LbC1ijAxtDK0rIa+" +
      "tCa0CTaKOTrIPDyKRj4/QUHD0UNE0dvc3UaBADs=";
  /**
   * The about image is a gif file in base 64.
   * @since 0.1
   */
  private static final String aboutImageText =
      "R0lGODlhOQBYAPf/AP//////zP//mf//Zv//M///AP/M///MzP/Mmf/MZv/MM//MAP+Z//+Z" +
      "zP+Zmf+ZZv+ZM/+ZAP9m//9mzP9mmf9mZv9mM/9mAP8z//8zzP8zmf8zZv8zM/8zAP8A//8A" +
      "zP8Amf8AZv8AM/8AAMz//8z/zMz/mcz/Zsz/M8z/AMzM/8zMzMzMmczMZszMM8zMAMyZ/8yZ" +
      "zMyZmcyZZsyZM8yZAMxm/8xmzMxmmcxmZsxmM8xmAMwz/8wzzMwzmcwzZswzM8wzAMwA/8wA" +
      "zMwAmcwAZswAM8wAAJn//5n/zJn/mZn/Zpn/M5n/AJnM/5nMzJnMmZnMZpnMM5nMAJmZ/5mZ" +
      "zJmZmZmZZpmZM5mZAJlm/5lmzJlmmZlmZplmM5lmAJkz/5kzzJkzmZkzZpkzM5kzAJkA/5kA" +
      "zJkAmZkAZpkAM5kAAGb//2b/zGb/mWb/Zmb/M2b/AGbM/2bMzGbMmWbMZmbMM2bMAGaZ/2aZ" +
      "zGaZmWaZZmaZM2aZAGZm/2ZmzGZmmWZmZmZmM2ZmAGYz/2YzzGYzmWYzZmYzM2YzAGYA/2YA" +
      "zGYAmWYAZmYAM2YAADP//zP/zDP/mTP/ZjP/MzP/ADPM/zPMzDPMmTPMZjPMMzPMADOZ/zOZ" +
      "zDOZmTOZZjOZMzOZADNm/zNmzDNmmTNmZjNmMzNmADMz/zMzzDMzmTMzZjMzMzMzADMA/zMA" +
      "zDMAmTMAZjMAMzMAAAD//wD/zAD/mQD/ZgD/MwD/AADM/wDMzADMmQDMZgDMMwDMAACZ/wCZ" +
      "zACZmQCZZgCZMwCZAABm/wBmzABmmQBmZgBmMwBmAAAz/wAzzAAzmQAzZgAzMwAzAAAA/wAA" +
      "zAAAmQAAZgAAM+4AAN0AALsAAKoAAIgAAHcAAFUAAEQAACIAABEAAADuAADdAAC7AACqAACI" +
      "AAB3AABVAABEAAAiAAARAAAA7gAA3QAAuwAAqgAAiAAAdwAAVQAARAAAIgAAEe7u7t3d3bu7" +
      "u6qqqoiIiHd3d1VVVURERCIiIhEREQAAACH/C05FVFNDQVBFMi4wAwEAAAAh/h9HaWZCdWls" +
      "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";

  private ImageIcon stopImageIcon;
  private ImageIcon exitImageIcon;
  private ImageIcon helpImageIcon;
  private ImageIcon aboutImageIcon;
  private ImageIcon startImageIcon;
  private ImageIcon addImageIcon;
  private ImageIcon removeImageIcon;
  private ImageIcon frameIcon;
  /**
   * <p>The main constructor creating an instance of all the image icons being
   * used to instantiate the various ImageIcons present.</p>
   * @param mainWindow MainWindow
   */
  public MainImageCreator(MainWindow mainWindow) {
    this.mainWindow = mainWindow;

    frameIcon = new ImageIcon(Base64.decode(frameIconText));
    exitImageIcon = new ImageIcon(Base64.decode(exitImageText));
    addImageIcon = new ImageIcon(Base64.decode(addImageText));
    removeImageIcon = new ImageIcon(Base64.decode(removeImageText));
    startImageIcon = new ImageIcon(Base64.decode(startImageText));
    stopImageIcon = new ImageIcon(Base64.decode(stopImageText));
    helpImageIcon = new ImageIcon(Base64.decode(helpImageText));
    aboutImageIcon = new ImageIcon(Base64.decode(aboutImageText));
  }

  /**
   * Method responsible for returning the ImageIcon for each of the images
   * on the Menu Bar.
   * Version 0.2 Supports the following Strings:
   * <pre>
   * stop.gif
   * exit.gif
   * help.gif
   * about.gif
   * start.gif
   * add.gif
   * remove.gif
   * </pre>
   * @param s String
   * @return ImageIcon
   */
  public ImageIcon getImageIcon(String s) {
    if (s.equals("stop.gif")) {
      return stopImageIcon;
    }
    if (s.equals("exit.gif")) {
      return exitImageIcon;
    }
    if (s.equals("help.gif")) {
      return helpImageIcon;
    }
    if (s.equals("about.gif")) {
      return aboutImageIcon;
    }
    if (s.equals("start.gif")) {
      return startImageIcon;
    }
    if (s.equals("add.gif")) {
      return addImageIcon;
    }
    if (s.equals("remove.gif")) {
      return removeImageIcon;
    }
    if (s.equals("icon.png")) {
      return frameIcon;
    }
    return frameIcon;
  }
}
