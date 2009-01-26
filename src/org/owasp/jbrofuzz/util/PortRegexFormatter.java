/**
 * JBroFuzz 1.2
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
package org.owasp.jbrofuzz.util;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.text.DefaultFormatter;

public class PortRegexFormatter extends DefaultFormatter {
	
	private static final String _65535 = "(?:(6553[0-5]|655[0-2][0-9]|65[0-4][0-9][0-9]|6[0-4][0-9][0-9][0-9]|\\d{2,4}|[1-9])?)";

    private static final Pattern pattern = Pattern.compile( "^" + _65535 + "$");
    
    private Matcher matcher;

    public PortRegexFormatter() {
        super();
    }

    public PortRegexFormatter(String pattern) throws PatternSyntaxException {
        this();
        // setPattern(Pattern.compile(pattern));
    }

    public PortRegexFormatter(Pattern pattern) {
        this();
        setPattern(pattern);
    }

    /**
     * <p>Method has been intentionaly left blank, 
     * so that no other pattern can be set.</p>
     * 
     * @param pattern
     * @author subere@uncon.org
     * @version 1.2
     * @since 1.2
     */
    public void setPattern(Pattern pattern) {
        // this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

    protected void setMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    protected Matcher getMatcher() {
        return matcher;
    }

    public Object stringToValue(String text) throws ParseException {
        Pattern pattern = getPattern();

        if (pattern != null) {
            Matcher matcher = pattern.matcher(text);

            if (matcher.matches()) {
                setMatcher(matcher);
                return super.stringToValue(text);
            }
            throw new ParseException("Pattern did not match", 0);
        }
        return text;
    }
}



