/**
 * JbroFuzz 2.5
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007 - 2010 subere@uncon.org
 *
 * This file is part of JBroFuzz.
 * 
 * JBroFuzz is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JBroFuzz is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JBroFuzz.  If not, see <http://www.gnu.org/licenses/>.
 * Alternatively, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * Verbatim copying and distribution of this entire program file is 
 * permitted in any medium without royalty provided this notice 
 * is preserved. 
 * 
 */
package org.owasp.jbrofuzz.version;

import javax.swing.Icon;
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
 * @author subere@uncon.org
 * @version 1.5
 */
public class ImageCreator {
	
	/**
	 * <p>
	 * The about image icon, displayed in the Help - About menu.
	 * </p>
	 * 
	 * @since 2.0
	 */
	public static final ImageIcon IMG_ABOUT = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/about.png"));

	/**
	 * <p>
	 * The add image seen in the menu bar having the shape of red, transparent
	 * cross. This is an original PNG file.
	 * </p>
	 * 
	 * @since 0.1
	 */
	public static final ImageIcon IMG_ADD = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/add.png"));

	/**
	 * <p>The clear icon, used in clear fuzzers and clear output.</p>
	 * 
	 * @since 2.0 
	 */
	public static final ImageIcon IMG_CLEAR = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/clear.png"));

	/**
	 * <p>
	 * The copy image icon.
	 * </p>
	 * 
	 * @since 2.0 
	 */
	public static final ImageIcon IMG_COPY = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/copy.png"));

	/**
	 * <p>
	 * The cut image icon.
	 * </p>
	 * 
	 * @since 2.0
	 */
	public static final ImageIcon IMG_CUT = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/cut.png"));
	
	/**
	 * <p>
	 * The disclaimer image icon.
	 * </p>
	 * 
	 * @since 2.0 
	 */
	public static final ImageIcon IMG_DISCLAIMER = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/disclaimer.png"));

	/**
	 * <p>
	 * The exit image seen in the menu bar. This is an original GIF file.
	 * </p>
	 * 
	 * @since 0.1
	 */
	public static final ImageIcon IMG_EXIT = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/exit.png"));

	/**
	 * <p>
	 * The Frequently Asked Questions image icon.
	 * </p>
	 * 
	 * @since 2.0 
	 */
	public static final ImageIcon IMG_FAQ = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/faq.png"));

	/**
	 * <p>The image being displayed at the top left of the frame, when the UI
	 * manager is windows based.</p>
	 * <p>The frame icon as a png file of 48 x 48 pixels.</p>
	 * 
	 * @version 2.0
	 * @since 0.3
	 */
	public static final ImageIcon IMG_FRAME = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/jbrofuzz.png"));

	/**
	 * <p>The pause image icon.</p>
	 * 
	 * @version 2.0
	 */
	public static final ImageIcon IMG_PAUSE = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/pause.png"));
	
	/**
	 * <p>
	 * The Look & Feel image icon.
	 * </p>
	 * 
	 * @version 2.0
	 */
	public static final ImageIcon IMG_LKF = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/lkf.png"));

	/**
	 * <p>The New Document image icon.</p>
	 * 
	 * @version 2.0
	 */
	public static final ImageIcon IMG_NEW = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/new.png"));

	/**
	 * <p>The open icon.
	 * 
	 * @version 2.0
	 */
	public static final ImageIcon IMG_OPEN = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/open.png"));

	/**
	 * <p>
	 * The open in browser icon.
	 * </p>
	 * 
	 * @version 2.0
	 */
	public static final Icon IMG_OPENINBROWSER = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/open-brw.png"));

	/**
	 * <p>
	 * The OWASP image being displayed in the about box.</p>
	 * 
	 * @version 2.0
	 * @since 0.4
	 */
	public static final ImageIcon IMG_OWASP = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/owasp-big.png"));

	/**
	 * <p>The medium owasp image, seen in the graphs.</p>
	 * 
	 * @version 2.0
	 */
	public static final ImageIcon IMG_OWASP_MED = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/owasp-med.png"));

	/**
	 * <p>The small owasp image icon, seen in the menu bar and tool bar.</p>
	 * 
	 * @version 2.0
	 */
	public static final ImageIcon IMG_OWASP_SML = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/owasp-sml.png"));

	/**
	 * <p>The paste image icon.</p>
	 * 
	 * @version 2.0
	 */
	public static final ImageIcon IMG_PASTE = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/paste.png"));

	/**
	 * <p>The preferences image icon.</p>
	 * 
	 * @version 2.0
	 */
	public static final ImageIcon IMG_PREFERENCES = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/prefs.png"));

	/**
	 * <p>The remove image seen in the menu bar having the shape of a transparent
	 * minus sign.</p>
	 * 
	 * @version 2.0
	 * @since 0.1
	 */
	public static final ImageIcon IMG_REMOVE = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/remove.png"));

	/**
	 * <p>The save image icon seen in the 'save' on the menu bar.</p>
	 * 
	 * @version 2.0
	 * @since 1.2
	 */
	public static final ImageIcon IMG_SAVE = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/save.png"));

	/**
	 * <p>The save as image icon.</p>
	 * 
	 * @version 2.0
	 * @since 1.8
	 */
	public static final ImageIcon IMG_SAVE_AS = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/save-as.png"));

	/**
	 * <p>The select all image icon.</p>
	 * 
	 * @version 2.0
	 */
	public static final ImageIcon IMG_SELECTALL = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/select-all.png"));

	/**
	 * <p>The start image seen in the menu bar having the shape of a play button.</p>
	 * 
	 * @version 2.0
	 * @since 0.1
	 */
	public static final ImageIcon IMG_START = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/start.png"));

	/**
	 * <p>The stop image seen in the menu bar, being a light blue solid box.</p>
	 * 
	 * @version 2.0
	 */
	public static final ImageIcon IMG_STOP = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/stop.png"));

	/**
	 * <p>The image icon for show all.</p>
	 * 
	 * @version 2.0
	 */
	public static final ImageIcon IMG_SHOW_ALL = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/show-all.png"));

	/**
	 * <p>The Show/Hide tab icon.</p> 
	 * 
	 * @version 2.0
	 */
	public static final ImageIcon IMG_SHOW_HIDE = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/show-hide.png"));

	/**
	 * <p>The help topics image icon.</p>
	 * 
	 * @version 2.0
	 */
	public static final ImageIcon IMG_TOPICS = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/topics.png"));

	/**
	 * <p>The update image icon.</p>
	 * 
	 * @version 2.0
	 */
	public static final ImageIcon IMG_UPDATE = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/update.png"));
	
	/**
	 * <p>The up image icon.</p>
	 * 
	 * @version 1.0
	 */
	public static final ImageIcon IMG_UP = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/up.png"));
	
	/**
	 * <p>The up-all image icon.</p>
	 * 
	 * @version 1.0
	 */
	public static final ImageIcon IMG_UPALL = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/up-all.png"));
	
	/**
	 * <p>The down image icon.</p>
	 * 
	 * @version 1.0
	 */
	public static final ImageIcon IMG_DOWN = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/down.png"));
	
	/**
	 * <p>The down-all image icon.</p>
	 * 
	 * @version 1.0
	 */
	public static final ImageIcon IMG_DOWNALL = new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource("icons/down-all.png"));




}
