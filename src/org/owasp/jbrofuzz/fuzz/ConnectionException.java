/**
 * JBroFuzz 2.1
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
package org.owasp.jbrofuzz.fuzz;

import java.io.IOException;

/**
 * <p>Signals that while connecting on the wire, either via HTTP or
 * HTTPS, an I/O exception of some sort has occurred. This class 
 * is the general class of exceptions produced by failed or 
 * interrupted I/O operations within JBroFuzz.<p>
 * 
 * <p>It is an extension of IOException</p>
 * 
 * @author subere@uncon.org
 * @version 2.2
 * @since 1.5
 */
public class ConnectionException extends IOException {

	private static final long serialVersionUID = 73278167420801202L;

	public ConnectionException(final String message) {

		super(message);

	}

}
