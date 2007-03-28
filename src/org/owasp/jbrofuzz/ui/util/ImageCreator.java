/**
 * ImageCreator.java 0.6
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
package org.owasp.jbrofuzz.ui.util;

import javax.swing.*;

import org.owasp.jbrofuzz.util.*;
/**
 * <p>This static class holds the Base64 Strings corresponding to all the
 * images used by JBroFuzz. Each String is then parsed into an ImageIcon object
 * which can be publicly referenced from this class.</p>
 *
 * <p>To add an image to the list of images so that the image can be referenced
 * as an ImageIcon two steps must be followed. First you have to create the
 * Base64 String holding the image representation; typically this String is kept
 * private. Second, a new image icon should be created, which decodes the Base64
 * representation of the image.</p>
 *
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class ImageCreator {

  private static final String OWASP =
    "iVBORw0KGgoAAAANSUhEUgAAAGAAAABYCAMAAAA9bwLKAAAAAXNSR0ICQMB9xQAAAqxQTFRF" +
    "AAAAExMTGxsbCwsLFBkbFBUaCgkGDxASBggFBwgKAAETAAEaAwkUDhIbGBYYEBAOBgsZDhAP" +
    "CQwaGBkWCAcFCw0SAQIjExkiCAs1DBE8CxEhBgkvAwUrCAsuExUhGx0hDRREFR1WERZGHSIk" +
    "HCMqHiQwGSJaGCBHHCdlHy5xHzR3IBwbICAeOzs7KyssLjAuMjIzIiMiNTg2JSksKS0wNTk7" +
    "KScoMTEvKjAzNDU5KCgmJSUpODc8KzI4PD1BJTFrMDx2JjV4ISxqJC50KDyIJj6RMT2HPkM/" +
    "PUJEKkGMNEWMNEuYOVKcKkWVNU+jPFWgQD4/U1NTSktLQkNDWVdYW1tbWFZXUFJPRkhHTU5R" +
    "TVBTVFVZVlhbRkhKSEdMRUVJVlhXXVxgQE6VQ1abRFSPVF2cRFujXWBfXWBhXmObS2OnVWmr" +
    "W3CuWnSyWW2wTXGtTnSxXYG3YF5fc3JzY2Nja2tre3p7bnBvaGpnb3Fyd3h5ZWlrdXV4eXd4" +
    "aGZneHZ3YmqqZnm0dHy2YnOtdnarfoGAdoa6aoO5e5C9e4zAe5TDbYrBgH+Ai4uMg4OEmpqb" +
    "iYeIkpKTmJiWiomHkJCOlpiVkpiYlpaYjo6Qh4iKj5CRkI6PhIu8i5C4kZC6go3Bh5fFkZzI" +
    "nqCfi6HLl6bMmqvRnrHSjqXRoJ6foZ+goKCeu7u7q6uso6OjrbCvs7OzuLq3p6mmtri3p6ip" +
    "t7i5qKqnr7CxpqWosK+xoK3Ooa3Rp7XVs7vZo7DOucLcv8DBvMXgwL/C29vbw8PDy8vL09PT" +
    "0M7Q1tjXzNDR1tbYyczVxsbIx8jKyMbH2dnW2NbXyMfKw8rhy9Hk1t/w3d7h3uHg4d/g4ODe" +
    "8/Pz6+vr4+Pj8PDu+Pj29vb46efo6ubl7u7w6Ojm7vDu9vj3GyzEYgAAAAF0Uk5TAEDm2GYA" +
    "AAAJcEhZcwAACdgAAAnYAcegua0AAAAZdEVYdFNvZnR3YXJlAE1pY3Jvc29mdCBPZmZpY2V/" +
    "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";
  /**
   * <p>The owasp image being displayed in the about box. This image is
   * originally a png file.</p>.
   * @since 0.4
   */
  public static final ImageIcon OWASP_IMAGE = new ImageIcon(Base64.decode(OWASP));


  private static final String FRAMEICON =
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
   * <p>The image being displayed at the top left of the frame, when the UI
   * manager is windows based. This image is originally a png file.</p>.
   * @since 0.3
   */
  public static final ImageIcon FRAME_IMG = new ImageIcon(Base64.decode(
    FRAMEICON));

  private static final String EXIT =
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
   * <p>The exit image seen in the menu bar. This is an original gif file.</p>
   * @since 0.1
   */
  public static final ImageIcon EXIT_IMG = new ImageIcon(Base64.decode(EXIT));

  private static final String STOP =
    "R0lGODlhDwAPAHAAACH5BAEAAIsALAAAAAAPAA8AhwAAAEnF5UnV9UrW9kLP5zO4" +
    "2SiewByOpxSAmA9viA9ieQdZcQhRYghJWUO21zq+3ju/4DW42SmpySGYsRmIoA93" +
    "kA1rgAhgcwhYaQhKWjqmwCmlxiqoyCmgwSGYsxR/mRBviAdoewhgcDOWrxuQrxuQ" +
    "shuPpxqGoRJ+lg9zhxBpgQlgdAhYbAhSZQhLWwdKWjKPnhN+lhN+lxB4kRBwigdl" +
    "eAhacAhTaAhQYgFEUjKElxBzhxBzhhBthxBshQ5lfAhjdQhbcQhXaQhSYghIWAhK" +
    "WABCUjJ7jA9jehBngAtkdwtjdwdecwhabAhVaQhPYAhMXAhKWQdJUQBCTTJzhAda" +
    "bwdZaghWaAhQZwhQYQhMXQdJUgBBUQA+SjJregdRaAhXaAhSZAhSYwhQYAhPXwdJ" +
    "VwVHUQA+SQA6SjJseQdJWQhJWgZIVQBBUgBCTgA9SQA5QTJicwVIWAZIWAZIUwJD" +
    "UQBCUABASAA4SgA6QQA3QjJjawBIUgBCUQBBTQBBSQA5SgA6QgAxQjJjbQBCSgA7" +
    "SgBDSgA5RwA5QgAwQgAxOgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAiWABcJHEiw4CICAwYQKOCB" +
    "BIIQSBZcySHQQYGLEjwgkOGjxpWJAjmI5ODBBEePHykuGnGgxIGNOzp+JKJSx0YZ" +
    "HJGgxJKj5o6YPnRKvEJT5REkQofyJEJT4BGJQ1MW6SlIYJyPWHMw7XnnjsA0PbWK" +
    "5Sqo6qI4ObAUJZtDkNdFe8JyvdP2TlmBe7q6FYSnrF1FigwKHhgQADs=";
  /**
   * <p>The stop imager seen in the menu bar, as well as various buttons
   * within the main frame.</p>
   */
  public static final ImageIcon STOP_IMG = new ImageIcon(Base64.decode(STOP));

  private static final String PAUSE =
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
   * <p>The pause image seen in the menu bar. This is an original gif file.</p>
   * @since 0.1
   */
  public static final ImageIcon PAUSE_IMG = new ImageIcon(Base64.decode(PAUSE));

  private static final String START =
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
   * <p>The start image seen in the menu bar having the shape of a play button.
   * This is an original gif file.</p>
   * @since 0.1
   */
  public static final ImageIcon START_IMG = new ImageIcon(Base64.decode(START));

  private static final String ADD =
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
   * <p>The add image seen in the menu bar having the shape of red, transparent
   * cross. This is an original png file.</p>
   * @since 0.1
   */
  public static final ImageIcon ADD_IMG = new ImageIcon(Base64.decode(ADD));

  private static final String REMOVE =
    "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAGCAIAAACTu1PVAAAABGdBTUEAALGPC/xhBQAAARhJ" +
    "REFUKFNjfHH02PsLF5hZ2f///8/IyMDEyMzAyAAEIIKR8T8jwz8Ghv9Amonp35+/DOdaWx9V" +
    "VX6bMePLlMlfp035MXP6z1kzf86Z+X32TCD7x+RJn9vbP9XVfqmouBsXx3Curf1FSdH/KZP/" +
    "T+j739f9r6v1X2P9n/Ly3zm5vxITfoWF/fLx/+Xm+dfN876hEcO5lpbHHp7/U1N/Jyb+iY7+" +
    "FRz2yy/wt4/vL2/fXz6+v339fwUG/QkO+Rca/sDGluF0bd1NQ+NvAaGf/YO+BoX9jIj9FR33" +
    "My7uZ3wiEP2IjfsaHvk5JPJbRMxNKxuGS0uWbAgK3pOUvCc5eV96xqGs7EPZOXB0MDNrf1r6" +
    "nuQUINocHgkApraQtS2/P7kAAAAASUVORK5CYII=";
  /**
   * <p>The add image seen in the menu bar having the shape of red, transparent
   * minus sign.</p>
   * @since 0.1
   */
  public static final ImageIcon REMOVE_IMG = new ImageIcon(Base64.decode(REMOVE));

  private static final String DISCLAIMER =
    "R0lGODlhDwAPAHAAACH5BAEAACUALAAAAAAPAA8AhQAAABQAFSUAJ2YAaGwAbmAA" +
    "YT8PQE47UE8AUb5Blt9iplQjVqd8qdFUn/J1r3tTfZJJlPx/tLQ3kf/I10cASWdA" +
    "aZxjn3kAdf+Wv54hhjcAOf+tynpafJIVgTIANI9akXcKelUAWItzjYcviVILVAAA" +
    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAZVwJJwSCwaA0gjUSAYOJWa" +
    "Q2iQUDgFxgWDqkhEBgjlAxKRXCYDirJiuQwwGQLBY2TLNxc53TgmDDpyanwQchhy" +
    "c0ofeX4hIUolIiMICIKPC46PmZqbQQA7";
  /**
   * <p>The disclaimer image.</p>
   */
  public static final ImageIcon DISCLAIMER_IMG = new ImageIcon(Base64.decode(
    DISCLAIMER));

  private static final String TOPICS =
    "R0lGODlhDwAPAHAAACH5BAEAAB4ALAAAAAAPAA8AhAAAABUBASgCAm0HBzsEBH4w" +
    "MFwGBvU6Ov9TU3MfH/9lZf9oaFUGBr9ERNE/P+QyMoAJCf+fn0wFBZw1Ne9SUv96" +
    "er0iInQICDYDA/+Li6kZGdpLS+dcXLIrKwAAAAAAAAVOoCeOZGkGqEkKwuCqWMFc" +
    "B+IK5qUM9bEzqsZG8bhEBgTV5HAZVCyXC8Y0oUQzkOjUJGxqokmuY3CpRKWqTTbK" +
    "GKg8FA2DEX4ngO+8fh8CADs=";
  /**
   * <p>The topics help image.</p>
   */
  public static final ImageIcon TOPICS_IMG = new ImageIcon(Base64.decode(TOPICS));

  private static final String HELP =
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
   * <p>The help image icon seen in the menu bar. This is an original gif file.
   * </p>
   * @since 0.1
   */
  public static final ImageIcon HELP_IMG = new ImageIcon(Base64.decode(HELP));
}
