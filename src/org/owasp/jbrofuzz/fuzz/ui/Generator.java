/**
 * JBroFuzz 1.4
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007, 2008, 2009 subere@uncon.org
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

public class Generator {

	private String type;
	private Integer start;
	private Integer end;

	public Generator(final String generator, final int start, final int end) {

		this.start = Integer.valueOf(start);
		this.end = Integer.valueOf(end);
		type = generator;

	}

	public int getEnd() {
		return end.intValue();
	}

	public int getStart() {
		return start.intValue();
	}

	public String getType() {
		return type;
	}

	public void setEnd(final int end) {
		this.end = Integer.valueOf(end);
	}

	public void setStart(final int start) {
		this.start = Integer.valueOf(start);
	}

	public void setType(final String generator) {
		type = generator;
	}
}
