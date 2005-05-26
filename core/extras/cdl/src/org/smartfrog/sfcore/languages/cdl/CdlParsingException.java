/** (C) Copyright 2004-2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.cdl;


/**
 * we add our own exception under the CDL parsing exception
 * created Jul 15, 2004 4:57:59 PM
 */

public class CdlParsingException extends Exception {

    public CdlParsingException(String message, Throwable ex) {
        super(message, ex);
    }

    public CdlParsingException(String message) {
        super(message);
    }


    /**
     * Assert that a test holds, if not, throw an exception.
     * @param test test to verify
     * @param errorText text in exception
     * @throws CdlParsingException iff test==false
     */
    public static void assertValid(boolean test,String errorText) throws CdlParsingException {
        if(!test) {
            throw new CdlParsingException(errorText);
        }
    }
}
