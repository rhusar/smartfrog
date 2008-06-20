/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/

package org.smartfrog.sfcore.languages.sf.functions;

/**
 * Defines the Format function that takes a format string and a set of 
 * parameters and creates a resultant string which has the values of the 
 * parameters embedded. At most 10 format parameters are allowed.
 * The format string is called "format" and the parameter locations are
 * defined using "$i" to indicate the ith parameter. The ith parameter is the
 * attribute "si".
 */ 
public class Format extends BaseFunction {

    /**
     * Does the formatting.
     * @return the formatted string.
     */
    protected Object doFunction() {
        String format;
        String formatted = "";
        String[] s = new String[10];

        format = (String) context.get("format");

        if (format == null) {
            format = "";
        }

        for (int i = 0; i < 10; i++) {
            try {
                s[i] = (String) context.get("s" + i);
            } catch (Exception e) {
                s[i] = "";
            }
        }

        formatted = new String(format);

        for (int i = 0; i < 10; i++) {
            formatted = formatted.replaceAll("\\$" + i, s[i]);
        }

        return formatted;
    }
}
