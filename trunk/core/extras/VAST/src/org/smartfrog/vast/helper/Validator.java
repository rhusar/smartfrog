/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

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

package org.smartfrog.vast.helper;

import java.util.regex.Pattern;

/**
 * Gathering class for validation functions which are equal for every os.
 */
public class Validator {
    /**
     * Validates an ip address.
     * @param inIP The ip.
     * @return True if it's a valid ip address, false otherwise.
     */
    public static boolean isValidIP(String inIP) {
        String  a = "(?:25[0-4]|2[0-4]\\d|[0-1]?\\d\\d?)";
        return Pattern.matches("(?:"+a+"\\.){3}"+a, inIP);
    }

    /**
     * Validates an subnet masks.
     * @param inMask The subnet mask.
     * @return True if it's a valid subnet mask, false otherwise.
     */
    public static boolean isValidSubnetMask(String inMask) {
        String  a = "(?:255|254|252|248|240|224|192|128|0)",
                a_ = "(?:255|254|252|248|240|224|192|128)",
                b = "255\\.";
        return Pattern.matches("(?:(?:"+b+"){3}"+a+")|(?:(?:"+b+"){2}"+a+"\\.0)|(?:"+b+a+"(?:\\.0){2})|(?:"+a_+"(?:\\.0){3})", inMask);
    }
}
