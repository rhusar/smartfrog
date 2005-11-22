/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.xml.utils;

/**
 * XML Schema helper stuff
 * created 26-May-2005 15:35:48
 */

public final class XsdUtils {

    private XsdUtils() {
    }

    /**
     * Test for a string being true against the XSD types
     * @link http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#boolean
     * @param value string to parse
     * @return true iff the string value matches the XSD boolean types
     */
    public static boolean isXsdBooleanTrue(String value) {
        return "true".equals(value) || "1".equals(value);
    }

    /**
     * Test for a string being true against the XSD types
     *
     * @param value string to parse
     * @return true iff the string value matches the XSD boolean types
     * @link http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#boolean
     */
    public static boolean isXsdBooleanFalse(String value) {
        return "false".equals(value) || "0".equals(value);
    }

}
