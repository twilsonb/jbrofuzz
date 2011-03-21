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
package org.owasp.jbrofuzz.fuzz.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.owasp.jbrofuzz.system.Logger;


/**
 * <p>
 * FuzzFileUtils - Utility class providing static functionality to read and return the contents of
 * a fuzzing session output.
 * </p>
 * 
 * @author ranulf
 * @version 2.5
 * @since 2.5
 */
public  class FuzzFileUtils {

	public static String getResponse(String input){
		return input.substring(input.indexOf("--jbrofuzz-->")+14,input.length());
	}

	public static String getRequest(String input){
		BufferedReader reader = new BufferedReader(new StringReader(input));
		int numLines = 0;
		String line = new String();
		StringBuffer sBuf = new StringBuffer();

		try{
			while (numLines < 13) {   
				reader.readLine(); 
				numLines ++;
			}

			

			while (!(line = reader.readLine()).equals("--jbrofuzz-->")){
				sBuf.append(line);
				sBuf.append("\n");
			}

		}catch(IOException ioe){
			Logger.log("Fuzzer file is not in the correct format", 3);
		}
		String rawReq = sBuf.toString();
		return rawReq.substring(0,rawReq.lastIndexOf("--")-1);	
	}

}
