/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;

/**
 * Version class provides version and copyright strings for SmartFrog System.
 */
public class Version {

    /** SmartFrog attribute name. Value = {@value} */
    final static String ATR_NAME = "name";
    /** SmartFrog attribute name. Value = {@value} */
    final static String ATR_MAJOR_RELEASE = "majorRelease";
    /** SmartFrog attribute name. Value = {@value} */
    final static String ATR_MINOR_RELEASE = "minorRelease";
    /** SmartFrog attribute name. Value = {@value} */
    final static String ATR_BUILD = "build";
    /** SmartFrog attribute name. Value = {@value} */
    final static String ATR_STATUS = "status";
    /** SmartFrog attribute name. Value = {@value} */
    final static String COPYRIGHT = "copyright";


    // Dont' change this. MODIFY version.sf in same package!!!!!!!!!!!!!!!!!!!
    private static String name=        "SmartFrog";
    private static String majorRelease="3";
    private static String minorRelease="4";
    private static String build=       "17"; // odd numbers are development versions
    private static String status=      ""; //alpha, beta, stable
    // Dont' change this. MODIFY version.sf in same package!!!!!!!!!!!!!!!!!!!
    /** The copyright String for the SmartFrog system. */
    private static String copyright = "(C) Copyright 1998-2005 HP Development Company, LP";

    private static boolean initialized=false;

    private Version(){
        init();
    }

    private synchronized void  init() {
        if (initialized) return;
        try {
            //Check Class and read configuration...NOT including system.properties
            ComponentDescription classComponentDescription = ComponentDescriptionImpl.
                getClassComponentDescription(this, false, null);

            name = classComponentDescription.sfResolve(ATR_NAME, name , false);
            majorRelease = classComponentDescription.sfResolve(ATR_MAJOR_RELEASE, majorRelease , false);
            minorRelease = classComponentDescription.sfResolve(ATR_MINOR_RELEASE, minorRelease , false);
            build = classComponentDescription.sfResolve(ATR_BUILD, build , false);
            status = classComponentDescription.sfResolve(ATR_STATUS, status , false);
            copyright = classComponentDescription.sfResolve(COPYRIGHT, copyright , false);
            initialized=true;
        } catch (Exception ex) {
            if (sfGetProcessLog().isWarnEnabled())
                sfGetProcessLog().warn(
                "Error during init of "+ this.getClass().toString()+"",
                ex);
        }
    }

    public static String versionString(){
        //init();
        if (!initialized) new Version();
        String newStatus=null;
        if (!status.trim().equals("")){
            newStatus="_"+status;
        } else newStatus="";
        return name+" "+majorRelease+"."+minorRelease+"."+build+"_"+status;
    }

    public static String copyright(){
        //init();
        if (!initialized) new Version();
        return copyright;
    }
    /**
     *  To get the sfCore logger
     * @return Logger implementing LogSF and Log
     */
    private LogSF sfGetProcessLog() {
       LogSF sflog  =  LogFactory.sfGetProcessLog();
       return sflog;
    }

    /**
     * Method invoked to print the version info.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        System.out.print(versionString());
    }
}
