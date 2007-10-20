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

package org.smartfrog.sfcore.languages.cdl.dom;

/**
 * all the error messages
 */
public class ErrorMessages {
    /**
     * error message for tests {@value}
     */
    public static final String ERROR_WRONG_NAMESPACE = "The element is not in CDL namespace";
    /**
     * error message for tests {@value}
     */
    public static final String ERROR_WRONG_ELEMENT = "Expected an element named ";
    public static final String ERROR_UNEXPECTED_ELEMENT_IN_EXPRESSION = "Unexpected element ";
    public static final String ERROR_DUPLICATE_VALUE = "Duplicate variable in expression: ";
    public static final String ERROR_UNKNOWN_NAMESPACE = "Unknown namespace ";
}
