/**
 * JBroFuzz 2.0
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
package org.owasp.jbrofuzz.headers;

import java.util.Enumeration;
import java.util.Map;

import javax.swing.tree.TreePath;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.core.Prototype;
import org.owasp.jbrofuzz.core.Verifier;

class HeaderLoader {

	private static final String HEADER = "JBroFuzz Headers Collection";

	private static final int MAX_RECURSION = 1024;

	private final HeaderTreeNode myNode;

	private int globalCounter;

	private Map<String, Prototype> headers_;

	public HeaderLoader() {

		myNode = new HeaderTreeNode(HEADER);
		globalCounter = 0;

		headers_ = Verifier.loadFile("headers.jbrf");
		
	} // constructor

	/**
	 * <p>
	 * Private, recursive method used at construction to populate the master
	 * <code>HeaderTreeNode</code> that can be accessed via the
	 * <code>getMasterTreeNode</code> method.
	 * </p>
	 * <p>
	 * In accessing this method, do not forget to reset the
	 * <code>globalCounter</code> as if it reaches a value of
	 * <code>MAX_RECURSION
	 * </code> this method will simply return.
	 * </p>
	 * 
	 * @param categoriesArray
	 *            The array of nodes to be added.
	 * @param headerTreeNode
	 *            The tree node on which to be added.
	 * 
	 * @see #getMasterHeader()
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	private void addNodes(final String[] categoriesArray, 
			final HeaderTreeNode headerTreeNode) {

		if (categoriesArray.length == 0) {
			return;
		}
		if (globalCounter > MAX_RECURSION) {
			return;
		}
		globalCounter++;
		// Find the first element
		final String firstElement = StringUtils.stripStart(StringUtils.stripEnd(
				categoriesArray[0], " "), " ");
		// Use the index to know its position
		int index = 0;
		boolean exists = false;
		for (final Enumeration<HeaderTreeNode> e = extracted(headerTreeNode); e.hasMoreElements()
		&& !exists;) {

			final String currentElement = e.nextElement().toString();

			if (currentElement.equalsIgnoreCase(firstElement)) {

				exists = true;

			} else {
				// If firstElement not current, increment
				index++;

			}
		}

		// Does the first element exist?
		if (!exists) {

			headerTreeNode.add(new HeaderTreeNode(firstElement));
		}

		// Remove the first element, start again
		final String[] temp = (String[]) ArrayUtils.subarray(categoriesArray, 1,
				categoriesArray.length);
		final HeaderTreeNode nemp = (HeaderTreeNode) headerTreeNode.getChildAt(index);

		addNodes(temp, nemp);

	}

	@SuppressWarnings("unchecked")
	private Enumeration<HeaderTreeNode> extracted(final HeaderTreeNode headerTreeNode) {
		return headerTreeNode.children();
	}

	protected Header getHeader(final TreePath treePath) {

		if (!((HeaderTreeNode) treePath.getLastPathComponent()).isLeaf()) {
			return Header.ZERO;
		}

		for(String headerName : headers_.keySet()) {
			
			Prototype proto = headers_.get(headerName);
			
			int catLength = proto.getNoOfCategories();
			
			final String[] categories = new String[catLength];
			proto.getCategories().toArray(categories);

			final Object[] path = treePath.getPath();
			int success = path.length - 1;
			
			for (int i = 0; i < path.length; i++) {
				try {
					
					if(path[i + 1].toString().equalsIgnoreCase(categories[i])) {
						success--;
					} else {
						i = 32;
					}
					
				} catch (ArrayIndexOutOfBoundsException exp) {
					i = 32;
				}
			}
			// We have found the header we were looking for
			if(success == 0) {
				int noOfFields = proto.size();
				
				final String[] output = new String[noOfFields];
				proto.getPayloads().toArray(output);
				
				final StringBuffer myBuffer = new StringBuffer();
				for(String payload : output) {
					myBuffer.append(payload);
					myBuffer.append('\n');
				}
				myBuffer.append('\n');
				
				final String commentL = proto.getComment();
				
				return new Header(noOfFields, myBuffer.toString(), commentL);
				
			}
			
		}
		
		return Header.ZERO;
		
	}

	public HeaderTreeNode getMasterTreeNode() {

		return myNode;
	}

	protected void load() {

		
		for (String hd : headers_.keySet()) {
			
			Prototype pt = headers_.get(hd);

			String [] catArray = new String[pt.getNoOfCategories()];
			pt.getCategories().toArray(catArray);
			
			addNodes(catArray, myNode);
			
		}
		

	}


}
