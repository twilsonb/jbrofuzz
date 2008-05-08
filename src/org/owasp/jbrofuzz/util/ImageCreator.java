/**
 * JBroFuzz 0.9
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007, 2008 subere@uncon.org
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
package org.owasp.jbrofuzz.util;

import javax.swing.ImageIcon;


/**
 * <p>
 * This static class holds the Base64 Strings corresponding to all the images
 * used by JBroFuzz. Each String is then parsed into an ImageIcon object which
 * can be publicly referenced from this class.
 * </p>
 * 
 * <p>
 * To add an image to the list of images so that the image can be referenced as
 * an ImageIcon two steps must be followed. First you have to create the Base64
 * String holding the image representation; typically this String is kept
 * private. Second, a new image icon should be created, which decodes the Base64
 * representation of the image.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class ImageCreator {

	private static final String OWASP = 
		"iVBORw0KGgoAAAANSUhEUgAAAGAAAABYCAMAAAA9bwLKAAAAAXNSR0ICQMB9xQAAAqxQTFRF"
		+ "AAAAExMTGxsbCwsLFBkbFBUaCgkGDxASBggFBwgKAAETAAEaAwkUDhIbGBYYEBAOBgsZDhAP"
		+ "CQwaGBkWCAcFCw0SAQIjExkiCAs1DBE8CxEhBgkvAwUrCAsuExUhGx0hDRREFR1WERZGHSIk"
		+ "HCMqHiQwGSJaGCBHHCdlHy5xHzR3IBwbICAeOzs7KyssLjAuMjIzIiMiNTg2JSksKS0wNTk7"
		+ "KScoMTEvKjAzNDU5KCgmJSUpODc8KzI4PD1BJTFrMDx2JjV4ISxqJC50KDyIJj6RMT2HPkM/"
		+ "PUJEKkGMNEWMNEuYOVKcKkWVNU+jPFWgQD4/U1NTSktLQkNDWVdYW1tbWFZXUFJPRkhHTU5R"
		+ "TVBTVFVZVlhbRkhKSEdMRUVJVlhXXVxgQE6VQ1abRFSPVF2cRFujXWBfXWBhXmObS2OnVWmr"
		+ "W3CuWnSyWW2wTXGtTnSxXYG3YF5fc3JzY2Nja2tre3p7bnBvaGpnb3Fyd3h5ZWlrdXV4eXd4"
		+ "aGZneHZ3YmqqZnm0dHy2YnOtdnarfoGAdoa6aoO5e5C9e4zAe5TDbYrBgH+Ai4uMg4OEmpqb"
		+ "iYeIkpKTmJiWiomHkJCOlpiVkpiYlpaYjo6Qh4iKj5CRkI6PhIu8i5C4kZC6go3Bh5fFkZzI"
		+ "nqCfi6HLl6bMmqvRnrHSjqXRoJ6foZ+goKCeu7u7q6uso6OjrbCvs7OzuLq3p6mmtri3p6ip"
		+ "t7i5qKqnr7CxpqWosK+xoK3Ooa3Rp7XVs7vZo7DOucLcv8DBvMXgwL/C29vbw8PDy8vL09PT"
		+ "0M7Q1tjXzNDR1tbYyczVxsbIx8jKyMbH2dnW2NbXyMfKw8rhy9Hk1t/w3d7h3uHg4d/g4ODe"
		+ "8/Pz6+vr4+Pj8PDu+Pj29vb46efo6ubl7u7w6Ojm7vDu9vj3GyzEYgAAAAF0Uk5TAEDm2GYA"
		+ "AAAJcEhZcwAACdgAAAnYAcegua0AAAAZdEVYdFNvZnR3YXJlAE1pY3Jvc29mdCBPZmZpY2V/"
		+ "7TVxAAAQ+ElEQVRo3s1Z+19TZ5oniY4nJxbdtkSozG6QGY1WzhLH67Q7GkioyclFYCJqLzub"
		+ "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";
	/**
	 * <p>
	 * The owasp image being displayed in the about box. This image is originally
	 * a png file.
	 * </p>.
	 * 
	 * @since 0.4
	 */
	public static final ImageIcon OWASP_IMAGE = new ImageIcon(Base64
			.decode(ImageCreator.OWASP));

	private static final String FRAMEICON = 
		"iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAALGPC/xhBQAAAp1J"
		+ "REFUOE+lk1tPE1EUhaHQltJ22tI77fReoFfoDVoEKRQoFEEFEhRBTDDEEKM+iUiAakj8ASYm"
		+ "JiQmvvjqP/zcrYThWSfZM8nMnO+svfY6ve12m57/uToA+3AOb3yW6MQ2zsAExdU2Nn+enl5d"
		+ "B/63evtQnAlKiycMKH5ihS26m3duerMLxZ1kKFjEMhQjPrlHsrSLzjBwA+jFYHWSmTnCH6li"
		+ "tgfQW70aQPGnUFwJgvFp3IEiRouTUvNMdlK7gD6DhXB2nUh+C59aJFU9QDdg1QD9gw4MJhue"
		+ "cAmzK0You0Gs/JTA2Cr6ARv+eB2zKDPKf5HsGpHMBkYlqAFS1UPsvjG8kWksDpVgaoVaq004"
		+ "0yKQXMAdqlJpXeKJVnF4R9DpTfT0GzRA7cEVpeUz0tOHeEJTzO58Y1R8mHr4hXT1JY39n+LJ"
		+ "EwrSlsHikvKIZ6N3AOtX1G4qO/+WdOU5jd1rFvZ/ECtuU1m5YO3oN2abXxQ1SMi0RqZeaIDC"
		+ "yiehfyC//J7szDGp2iHh/AaJ4hYLosYh7RUFXGyeUm5d3NSVBgiNbxIYbeAS+UarW2CnTDY/"
		+ "Ut/5jjNYoLJ2xeL+L9zRGqXW+a3a2xyY7Sr9JgfuSIXK+mdi+UfMbX6lsnpJrv6Gxb1rUiJ5"
		+ "ZPIAr7SQm33FpHy7BXRnbbIyVt7DIiHxxO6TLD8jmJxHzayQESPH517jE5VqZklG6MWXqGsA"
		+ "iyPUTZpHLaN4EpicITyRe13T3GqJspioNymSCUVaXSI41sRou5NE1/AEvvCUZD1Kvv5OEreN"
		+ "0eykR6JsHgqTm++828IwaCcuz9LSCXZvRlMQTq9jFenu4Djp+8dCD0hQ5Bzo+mSREzX3mErz"
		+ "nKFAAV+khksO33B0RgN0zPjX+gPK6Z2eb2W0vwAAAABJRU5ErkJggg==";
	/**
	 * <p>
	 * The image being displayed at the top left of the frame, when the UI manager
	 * is windows based. This image is originally a png file.
	 * </p>.
	 * 
	 * @since 0.3
	 */
	public static final ImageIcon FRAME_IMG = new ImageIcon(Base64
			.decode(ImageCreator.FRAMEICON));

	private static final String EXIT = 
		"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAACXBIWXMAAAsTAAALEwEAmpwY" +
		"AAAABGdBTUEAALGOfPtRkwAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAAdTAAAOpgAAA6mAAA" +
		"F2+SX8VGAAAA9klEQVR42mL8//8/A7kAIICYGCgAAAHEAmMwNjIkAKn5UO5HIFYA4g9Q/gQg" +
		"zoeyL/6vZzAAMQACCNnmBUB8EMrmh/JBwAFJI8jQAJgGgABCd3YCVAEI+EP5C9DkH8A4AAGE" +
		"rvkBVAEMgLwhD2UvBOINyIoBAghbgIEUbEQTewjEBegKAQIIV2hvQONfQAo8OAAIIGyaBaCh" +
		"iwz80bwDBgABhE3zAmhoMyCFPiy6FJAVAgQQuuYCqC3g+IRG00Ys0QcGAAGErBkU8Q1o0YIe" +
		"ffbIAQcQQCxIihWQ/HoBihmgARUAdQUKAAggRkoyBkAAUZQxAAIMANElKut+P5onAAAAAElF" +
		"TkSuQmCC";
	/**
	 * <p>
	 * The exit image seen in the menu bar. This is an original gif file.
	 * </p>
	 * 
	 * @since 0.1
	 */
	public static final ImageIcon EXIT_IMG = new ImageIcon(Base64
			.decode(ImageCreator.EXIT));

	private final static String cutImageText = 
		"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAACXBIWXMAAAsTAAALEwEAmpwY" +
		"AAAABGdBTUEAALGOfPtRkwAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAAdTAAAOpgAAA6mAAA" +
		"F2+SX8VGAAAA10lEQVR42mL8//8/A7kAIICYGCgAAAHEAiIYGxkOACl7EPt/PQMjkO8AZO6H" +
		"qmkEijUgqwECR6DYAYAAoshmgACiSDNAAFGkGSCAMDQD/SZAhL4HIAIggGCaLyBJNGAx0AAp" +
		"sECBCtYMEEAwzROA+COUnQ/EG5D0FgDxeSR+IYwBEEBMSCYFAPFBqDg/kmIY+yEQJwLVToBJ" +
		"AAQQI7YUBnQmyLZ+KHchyHagpg/o6gACCFdoI4fBA2waQQAggFjwhGYjlH0AV5ADBBAjJRkD" +
		"IIAoSiQAAQYAHREwlOPeTOgAAAAASUVORK5CYII=";

	private final static String copyImageText = 
		"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAACXBIWXMAAAsTAAALEwEAmpwY" +
		"AAAABGdBTUEAALGOfPtRkwAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAAdTAAAOpgAAA6mAAA" +
		"F2+SX8VGAAAA/UlEQVR42mL8//8/A7kAIICYGCgAAAHEAiIYG7HKwZx0EIgdMCTrGRgAAgjd" +
		"5gQgPgDEH5DE7IH4ARBvQDcEIIBYkNgXgFgfhwvlodgfiEHubAAJAgQQzOYCJI0XgdgRSeNF" +
		"qAYYADqYwQDEAAggmOYAJMkJUKfDwAeoTRuRxMCaAQIIW2g/wOF0ZHEFEAEQQExI/kUONHQg" +
		"gOY6sMsAAgimeQEQf4Sy49GcbQC1VR4pDMDyAAGEbDPI5IdI0QMD/FDMAPU3PLoAAogJzSkK" +
		"0JAuRBIHGRgIxIZQC+BpACCAWLD47wAUCyAF1AZsIQgQQIyUZAyAAKIoYwAEGAABuyyw6aZU" +
		"xAAAAABJRU5ErkJggg==";

	private final static String pasteImageText = 
		"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAACXBIWXMAAAsTAAALEwEAmpwY" +
		"AAAABGdBTUEAALGOfPtRkwAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAAdTAAAOpgAAA6mAAA" +
		"F2+SX8VGAAABDUlEQVR42mL8//8/A7kAIICYcEkwNjIoALEBPs0AAcSIbDNUcQMQ+6OpOwgS" +
		"/1/PcABZECCA4JqhGkGS/HgsSwQasADGAQggZGdvQNJ4EYgDgdgRiDciqZkAtEQAxgEIIBYk" +
		"CXkkdgLQhgtQF4Ho91BxkOEwFzIABBDWAINphLI/QP0MAx9gDIAAYkILFHhIo5kH439ENhgg" +
		"gJCdPQGI7WH+BxoQALVlAZKXFiCbCBBA6FHlgBRwjVDhepCNQFyAHNIgABBAYM1ATQmgQIKK" +
		"gUJTH03zQyB+AOVfABpSAGIABBALkp/s8cSvPFpsgAFAAME0H8Ci4QAOg2AuYAAIIEZKMgZA" +
		"ADExUAAAAgwAtRNGMVUn4dEAAAAASUVORK5CYII=";

	private final static String selectAllImageText = 
		"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAACXBIWXMAAAsTAAALEwEAmpwY" +
		"AAAABGdBTUEAALGOfPtRkwAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAAdTAAAOpgAAA6mAAA" +
		"F2+SX8VGAAAA0klEQVR42mL8//8/A7kAIICYGCgAAAHEgsxhbGQ4AKTs8ahv/F/P0ADjAAQQ" +
		"RTYDBBALGv8Cms2NQHwAif8AWTFAADGiBxjQ6QlAaj6SUCLQqQuw2QwQQBjOhip0BOKPUKH5" +
		"QAMnYNMMEEC4/HwBzYkK2BQBBBCGZqAtBlCN+lChjUCcgE0zQACxYNEICiB+qNBCoDcScIU2" +
		"QAChBBhQ8wckjSBwEE39AuTAAwgg9KjiR+OjJxjkaGMACCB0zY0E0gWKZoAAYqQkYwAEEEXJ" +
		"EyDAAL1bLDuzPKEfAAAAAElFTkSuQmCC";



	private static final String STOP = 
		"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAACXBIWXMAAAsTAAAL" +
		"EwEAmpwYAAAABGdBTUEAALGOfPtRkwAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAA" +
		"dTAAAOpgAAA6mAAAF2+SX8VGAAAAf0lEQVR42mL8//8/A7kAIICYGCgAAAHEAmMw" +
		"NjIoACkFIvRc+F/P8AHEAAggFiTBBCCuJ0KzIxAfADEAAogiZwMEEEWaAQKIIs0A" +
		"AUSRZoAAokgzQABRpBkggCjSDBBAFGkGCCDkRPIAiA8SoecDjAEQQIyUZAyAAKLI" +
		"2QABBgCrAA6ONiIoUgAAAABJRU5ErkJggg==";

	/**
	 * <p>
	 * The stop imager seen in the menu bar, as well as various buttons within the
	 * main frame.
	 * </p>
	 */
	public static final ImageIcon STOP_IMG = new ImageIcon(Base64
			.decode(ImageCreator.STOP));

	private static final String PAUSE = "R0lGODlhDwAPAHAAACH5BAEAAHoALAAAAAAPAA8AhgAAAFu83Ve93QGgwgB2kABcdwBPalXB"
		+ "40C43ACWtgBviQBYcAuu1xWu2ACSsgFddgBQYA+75w6s0wCIqABwiABZcgCdvQCevwCJqABv"
		+ "iABddgCpzQCcvACDoABpggCMrwCHqgB9mQBpgQBVbwCVvACHqAB5kwBjegBUbACHpgB+lwBu"
		+ "kQBmfQBUbQBQYgCNqgB8lwBsjABieQBSawB6jQBtjABnfwBbdQBUaABQWACDlwBtiwBlfQBY"
		+ "cQBSYQRuiABhfgBefABabQBSYwBGWANvjABdegBYaQhneQBacwBaagBSZQBQWwBDVQZqgABa"
		+ "cgBZagBRYgBNWQhjewBXZwBNWwBBUAZnfgBOWABKVxBZcwBQYQBLWQBEUwJBUAtcdQBQXgBK"
		+ "WgBBUhBaaQBKWQBLUwdBSAteawBIWQBJUAFCUgBBUQdCSw82SAtabABHVwtBSRpSZAJDUgFD"
		+ "UgpDSxI7SxRSZANDUAtCSxI8SwAAAAAAAAAAAAAAAAAAAAAAAAeEgHqCg4SFegICAzA3gwcI"
		+ "HB6MegwNHzQngxEMIJGCHBycknobHDqdeh8gMKd6oDAnOIKqK7CDqh61ejSXN7GCpie9gh5B"
		+ "N8KCNMU4BoLBODhNg5HP0XqwODk5g9dN2npaOdDVer05TdV25uaDdt1NZoLY5/DN5u+CcfNt"
		+ "7O9mcIYABwUCADs=";
	/**
	 * <p>
	 * The pause image seen in the menu bar. This is an original gif file.
	 * </p>
	 * 
	 * @since 0.1
	 */
	public static final ImageIcon PAUSE_IMG = new ImageIcon(Base64
			.decode(ImageCreator.PAUSE));

	private static final String START = 
		"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAACXBIWXMAAAsTAAAL" +
		"EwEAmpwYAAAABGdBTUEAALGOfPtRkwAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAA" +
		"dTAAAOpgAAA6mAAAF2+SX8VGAAAAzElEQVR42mL8//8/A7kAIIBYYAzGRgYBIAXC" +
		"Dwhp+l8PoQECiAlJzACILwBxA9QQggAggJjQ+PxAXA81JICQZoAAYsIhLg/E64H4" +
		"ANRFWAFAADERMNweiM8D8QRsXgEIICYiAzYfGpAFyIIAAcREQsyAwqMf6hUwAAgg" +
		"JhKjdiJyQAIEEAuRmg4CcQJ6GgAIIEI2PwTiQCB2wJZ4AAIIl+aPQNwIxApAvAGX" +
		"yQABhM3ZC6GpjGAyBQggZM0gxY7IoUkIAAQQIyW5CiDAAKusHj7ANrjHAAAAAElF" +
		"TkSuQmCC";

	/**
	 * <p>
	 * The start image seen in the menu bar having the shape of a play button.
	 * This is an original gif file.
	 * </p>
	 * 
	 * @since 0.1
	 */
	public static final ImageIcon START_IMG = new ImageIcon(Base64
			.decode(ImageCreator.START));

	private static final String ADD = "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAACVUlEQVR4nH2SO2tUYRCGn/nO"
		+ "dy7Zs4ludKNbGFBZoqiohWAjCAp2FgpWIuJfEEQs7cTSfyC2KlgIlsHCSyXiBS/BIImbjcaN"
		+ "ezY5e87u941FFFYR327gnWFm3ke894yoOcj7pz7dvHkrnp1Fu13CTZt53W6zevFC89zly3OA"
		+ "/jZb/pKqN2ltE9sbDUySIFMN2iiLef9vKxZojtTHjTF71FqcDVHnsc7hnCKhLQD5o7n/9dv1"
		+ "7MuXF6CPJAjuF1nWcL0ewfgEfmkJREmiCP+9cyxrte5qUez33pdBtfpaPty+o/FwQJAkIKBl"
		+ "zoQxpO/nGD59hqlvI6sktJzDbpsiqtcZ5DmPP85hRYTGoYME49WNm9fXkK9LuIUFsBanjkqa"
		+ "0pzaim9sJ5jewWA9p3j1BosIviwIyghU8cUAKQcwlkIUg1c2AhGIQggtRDFBXMFu7Aqogv76"
		+ "iIKJQjSOEfUoiurvkAREQBSjziFGwBiwAWIEEUFsgFRi1G0MNHi0KKAsoSgZ5jnWVhI6S8uE"
		+ "qxlqwPX7xN9XGA8EKmOYXkm2lrP4YY60HCBZj/Vul9pkDSmz7FrZzfYBj8UGYdnNTv64d/f0"
		+ "9OQE/u07zOIyL1dWeH740PL5q1cOFL3ejbLf3zVer5+wNk1f2TSdBVaBGVutPsmi5DRDj0Qx"
		+ "iFAWfexk7WhSq3WSWu3SKGEPRqCZVO+PGGPAhoi1EAQYMagbTgPz/2O7JyKDtc4K7XaBX1jA"
		+ "f/5Mu9UC5yv/YntU38KxsQ47d7E4P4+Z2YvubrIF5cDZMw8BM2r+CQbeCNwWeEAbAAAAAElF"
		+ "TkSuQmCC";
	/**
	 * <p>
	 * The add image seen in the menu bar having the shape of red, transparent
	 * cross. This is an original png file.
	 * </p>
	 * 
	 * @since 0.1
	 */
	public static final ImageIcon ADD_IMG = new ImageIcon(Base64
			.decode(ImageCreator.ADD));

	private final static String TXT_UPDATE = 
		"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAACXBIWXMAAAsTAAAL" +
		"EwEAmpwYAAAABGdBTUEAALGOfPtRkwAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAA" +
		"dTAAAOpgAAA6mAAAF2+SX8VGAAAAwElEQVR42mL8//8/A7kAIICYGCgAAAFEkWaA" +
		"AMLQzNjI4ECsZoAAYkHSxAWkpgGxAhATZQBAADHCAgyoeS6QSgLij0B8AYf64P/1" +
		"DG9hHIAAYkGSmAySBOIHQNyAQ/NnZA5AACFrvgB1LkjjATRNCUC8AN0kgABCDzCQ" +
		"AQFYbMSqGSCAkG0G2bofT/jYo3sJIIDQne2IQ+MENBoMAAIIWfMHLH6FgQNQGz8g" +
		"CwIEECMlaRsggChKngABRJFmgAADAIGBIrsWh+BkAAAAAElFTkSuQmCC";

	public static final ImageIcon IMG_UPDATE = new ImageIcon(Base64.decode(ImageCreator.TXT_UPDATE));


	private static final String REMOVE = 
		"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAGCAIAAACTu1PVAAAABGdBTUEAALGPC/xhBQAAARhJ" +
		"REFUKFNjfHH02PsLF5hZ2f///8/IyMDEyMzAyAAEIIKR8T8jwz8Ghv9Amonp35+/DOdaWx9V"
		+ "VX6bMePLlMlfp035MXP6z1kzf86Z+X32TCD7x+RJn9vbP9XVfqmouBsXx3Curf1FSdH/KZP/"
		+ "T+j739f9r6v1X2P9n/Ly3zm5vxITfoWF/fLx/+Xm+dfN876hEcO5lpbHHp7/U1N/Jyb+iY7+"
		+ "FRz2yy/wt4/vL2/fXz6+v339fwUG/QkO+Rca/sDGluF0bd1NQ+NvAaGf/YO+BoX9jIj9FR33"
		+ "My7uZ3wiEP2IjfsaHvk5JPJbRMxNKxuGS0uWbAgK3pOUvCc5eV96xqGs7EPZOXB0MDNrf1r6"
		+ "nuQUINocHgkApraQtS2/P7kAAAAASUVORK5CYII=";
	/**
	 * <p>
	 * The add image seen in the menu bar having the shape of red, transparent
	 * minus sign.
	 * </p>
	 * 
	 * @since 0.1
	 */
	public static final ImageIcon REMOVE_IMG = new ImageIcon(Base64
			.decode(ImageCreator.REMOVE));

	private static final String TXT_DISCLAIMER = 
		"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAACXBIWXMAAAsTAAALEwEAmpwY"
		+ "AAAABGdBTUEAALGOfPtRkwAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAAdTAAAOpgAAA6mAAA"
		+ "F2+SX8VGAAAAsklEQVR42mL8//8/A7kAIICYGCgAAAFEkWaAACJJM2MjQwEQB8D4AAHEQqQm"
		+ "ASC1AIgf/K9nmAATBwggJiI1HgDiDUCNBchyAAFEjLNBNl4AalyALgEQQAygqELGDA3/HYA4"
		+ "AcpuAOIL6GpgGCCAsPn5AxBPgDq3HogNcTkJIICwmgi18T8QL8BlKwgDBBAuP4NC9CEQN+AL"
		+ "DIAAYqQkeQIEEEUpDCCAKNIMEEAUaQYIMABFQmtvHnE2kgAAAABJRU5ErkJggg==";
	/**
	 * <p>
	 * The disclaimer image.
	 * </p>
	 */
	public static final ImageIcon IMG_DISCLAIMER = new ImageIcon(Base64
			.decode(ImageCreator.TXT_DISCLAIMER));

	private static final String TXT_TOPICS = 
		"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAACXBIWXMAAAsTAAALEwEAmpwY" +
		"AAAABGdBTUEAALGOfPtRkwAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAAdTAAAOpgAAA6mAAA" +
		"F2+SX8VGAAAA0UlEQVR42mL8//8/A7kAIIBY0AUYG1G4BkB8AVngfz2CDRBATFgMbADiByB1" +
		"QHweSoP4DugKAQIIm2aQInk0MRB/AbpCgADCphnkzEIgVgRiQyB+iGQAiu0AAcSCRXMBGv8B" +
		"FpeAAUAAMREIUJD/7aFskAsOIEsCBBALHo0fgJgfyj4IxAHoCgACCJ/NMI0ToX79gK4AIIDw" +
		"2XwQKQCxAoAAwqVZAd1/2ABAAOHTXI/kggXYFAEEEC7NoOhpRGJjBQABxEhJxgAIICYGCgBA" +
		"gAEAHtwgxMCFBqgAAAAASUVORK5CYII=";

	

	



	private final static String TXT_LKF = 
		"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAACXBIWXMAAAsTAAALEwEAmpwY" +
		"AAAABGdBTUEAALGOfPtRkwAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAAdTAAAOpgAAA6mAAA" +
		"F2+SX8VGAAABCklEQVR42mL8//8/A7kAIICYcEkwNjIIgDA+zQABxIhsM1TxBCCOR1LzEST2" +
		"v56hAV0zQACh27wASeNGIH4IxPxAXA80OABdM0AAoWtGdmYB1BUwYICuGSCAWND4D4DYHsre" +
		"gMVVKAAggLD5+QHUqTBwEYgTkFxxAeh/kKsYAAII3eYJaBpBQAFK26PbDBBATEi2GiAF1kEg" +
		"nghlgww7gKTnAowBEEBMOALrA9RpiUgGILsODAACCK4ZqPgANGpAwB/oElAAfYD6mQEtFsAA" +
		"IICY8IQoyAvrgVgfmlA+QsXzoQYzAAQQI3rahiaGAGhAPYD6cQGUj5xQFgAEECMlGQMggJgY" +
		"KAAAAQYAa/I8dLncHpcAAAAASUVORK5CYII=";

	private final static String TXT_PREFS = 
		"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAACXBIWXMAAAsTAAAL" +
		"EwEAmpwYAAAABGdBTUEAALGOfPtRkwAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAA" +
		"dTAAAOpgAAA6mAAAF2+SX8VGAAAA8UlEQVR42mL8//8/A7kAIICYGCgAAAGEopmx" +
		"kcEBiS1ASDNAADEiOxuoAcRxBOL9QLwRiC8AcT2yhv/1DIwwNkAAsWAxUAVKGwDx" +
		"AyBeCMQaUPYDZIUAAYRNswSUFoAaAOKrA7E2EC9BVggQQNgC7AiUBjl5AlQjCPAA" +
		"sSWyQoAAQre5EYldADVgItQFIFt1kBUDBBC6ZgFoYIHAeajGfCB+CbXZAFkxQACh" +
		"OzsfB18ciD3Q/QcQQOiaDaFRtRBKG0LFQTbvQNcMEEDozgYFkD2UHQ/EB5H8/AUa" +
		"BnAAEECMlKRtgACiKG0DBBBFmgECDADk8CgQHS3hUQAAAABJRU5ErkJggg==";
	
	private static final String TXT_HELP = 
		"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAACXBIWXMAAAsTAAALEwEAmpwY" +
		"AAAABGdBTUEAALGOfPtRkwAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAAdTAAAOpgAAA6mAAA" +
		"F2+SX8VGAAAA0UlEQVR42mL8//8/A7kAIIBY0AUYG1G4BkB8AVngfz2CDRBATFgMbADiByB1" +
		"QHweSoP4DugKAQIIm2aQInk0MRB/AbpCgADCphnkzEIgVgRiQyB+iGQAiu0AAcSCRXMBGv8B" +
		"FpeAAUAAMREIUJD/7aFskAsOIEsCBBALHo0fgJgfyj4IxAHoCgACCJ/NMI0ToX79gK4AIIDw" +
		"2XwQKQCxAoAAwqVZAd1/2ABAAOHTXI/kggXYFAEEEC7NoOhpRGJjBQABxEhJxgAIICYGCgBA" +
		"gAEAHtwgxMCFBqgAAAAASUVORK5CYII=";

	private static final String TXT_FAQ =
		"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAACXBIWXMAAAsTAAALEwEAmpwY" +
		"AAAABGdBTUEAALGOfPtRkwAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAAdTAAAOpgAAA6mAAA" +
		"F2+SX8VGAAABPklEQVR42mL8//8/A7kAIIBYsAkyNjIYACkBEPt/PcMBXJoBAogR2WagpkIg" +
		"1QbEf6CYA2rBfqAhbuiaAQKICUljAJDqA+KJQMwDxIFADZxA+jAQuwLlE9A1AwQQExJ7ARBv" +
		"BOIdaGqSgPgtEE8AGiCALAEQQExQWx2AFD/UAAY014BsbIHKGyDLAwQQE5otH5DY+4F4PZR9" +
		"AUo7ICsGCCB0zciSjiB/o8k/QOYABBATmiCKs4ABtgGIG4BMBTQXgAFAADFBFYE0HwRifyAO" +
		"gsohBw7IgINAdSiaAQIIOZGAAucYEKcB8Ukg9gAG2C8o/wNUHgUABBB6IhGAhi5MoQLUBQHY" +
		"UhpAADHiS9tQwzYAsT0QLwTiA1AsAPICQAAxEpMxoPG9ABrXIPAQFLgAAcRISq6CGgKyFZyY" +
		"AAIMACj4UJZKxS5mAAAAAElFTkSuQmCC";

	private final static String TXT_ABOUT = 
		"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAACXBIWXMAAAsTAAAL" +
		"EwEAmpwYAAAABGdBTUEAALGOfPtRkwAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAA" +
		"dTAAAOpgAAA6mAAAF2+SX8VGAAAA5UlEQVR42mL8//8/A7kAIICYGCgAAAFEkWaA" +
		"AGJB5jA2gikFIJ4AxP5Q4Y9QPgh/+F+PUA8QQOg2GwDxBSgNMsoRiDcAcQEQH0C3" +
		"GSCA0DWDTYdqeADVAOI3ALE+1BA4AAggdM32UANAts+Hak6A0geBOABZMUAAYQsw" +
		"kMIFUMXIhmE4GyCAsGkOgGqG+bseKhYA9QIcAAQQC5rGhVBnTkCy8QHUIH2oGBwA" +
		"BBC6Zlio9kMNugC10R7KX4CsGCCA0DWDnOUANcQBGucgkIiuEQQAAoiRkrQNEEAU" +
		"JU+AAKJIM0CAAQBcCi/wtU5IcgAAAABJRU5ErkJggg==";

	public static final ImageIcon IMG_CUT = new ImageIcon(Base64.decode(cutImageText));
	public static final ImageIcon IMG_COPY = new ImageIcon(Base64.decode(copyImageText));
	public static final ImageIcon IMG_PASTE = new ImageIcon(Base64.decode(pasteImageText));
	public static final ImageIcon IMG_SELECTALL = new ImageIcon(Base64.decode(selectAllImageText));
	
	public static final ImageIcon IMG_LKF = new ImageIcon(Base64.decode(TXT_LKF));

	public static final ImageIcon IMG_TOPICS = new ImageIcon(Base64.decode(ImageCreator.TXT_TOPICS));

	public static final ImageIcon IMG_PREFERENCES = new ImageIcon(Base64.decode(TXT_PREFS));
	public static final ImageIcon IMG_HELP = new ImageIcon(Base64.decode(TXT_HELP));
	public static final ImageIcon IMG_FAQ = new ImageIcon(Base64.decode(TXT_FAQ));
	public static final ImageIcon IMG_ABOUT = new ImageIcon(Base64.decode(TXT_ABOUT));

}
