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

package org.smartfrog;

/**
 * Version class provides version and copyright strings for SmartFrog System.
 */
public class Version {

    private final static String name=        "SmartFrog";
    private final static String majorRelease="3";
    private final static String minorRelease="02";
    private final static String build=       "003"; // odd numbers are development versions
    private final static String status=      "beta"; //alpha, beta, stable

    /** The version String for the SmartFrog system. */
    public final static String versionString =
                    name+" "+majorRelease+"."+minorRelease+"."+build+"_"+status;


    /** The copyright String for the SmartFrog system. */
    public final static String copyright = "(C) Copyright 1998-2004 Hewlett-Packard Development Company, LP";

    private Version(){
    }

    /**
     * Method invoked to print the version info.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        System.out.print(versionString);
    }
}
