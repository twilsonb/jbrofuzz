/**
 * JBroFuzz 1.9
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
package org.owasp.jbrofuzz.fuzz.ui;

/**
 * 
 * 
 * @see
 * @author subere@uncon.org
 * @version 1.3
 * @since 1.2
 */
public class ResponseOutput {

	protected String one;
	protected String two;
	protected String three;
	protected String four;
	protected String five;
	protected String six;

	public ResponseOutput(final String one, final String two,
			final String three, final String four, final String five,
			final String six) {

		this.one = one;
		this.two = two;
		this.three = three;
		this.four = four;
		this.five = five;
		this.six = six;

	}

	public String getFifth() {
		return five;
	}

	public String getFirst() {
		return one;
	}

	public String getFourth() {
		return four;
	}

	public String getSecond() {
		return two;
	}

	public String getSixth() {
		return six;
	}

	public String getThird() {
		return three;
	}

}
