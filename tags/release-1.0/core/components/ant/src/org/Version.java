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

package org;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.common.SFNull;

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
    /** SmartFrog attribute name. Value = {@value}
     * Used to determine the minimun core version compatible with this instance*/
    final static String ATR_MIN_CORE_VERSION = "minCoreVersion";
    /** SmartFrog attribute name. Value = {@value}
     * Used to determine the maximum core version compatible with this instance*/
    final static String ATR_MAX_CORE_VERSION = "maxCoreVersion";


    // Dont' change this. MODIFY version.sf in same package!!!!!!!!!!!!!!!!!!!
    private static String name=        "SmartFrog";
    private static String majorRelease="3";
    private static String minorRelease="4";
    private static String build=       "17"; // odd numbers are development versions
    private static String status=      ""; //alpha, beta, stable

    private static String minCoreVersion = null;

    private static String maxCoreVersion = null;

    // Dont' change this. MODIFY version.sf in same package!!!!!!!!!!!!!!!!!!!
    /** The copyright String for the SmartFrog system. */
    private static String copyright = "(C) Copyright 1998-2006 HP Development Company, LP";

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
            minCoreVersion = classComponentDescription.sfResolve(ATR_MIN_CORE_VERSION, minCoreVersion , false);
            maxCoreVersion = classComponentDescription.sfResolve(ATR_MAX_CORE_VERSION, maxCoreVersion , false);
            initialized=true;

        } catch (Exception ex) {
            if (sfGetProcessLog().isWarnEnabled())
                sfGetProcessLog().warn(
                "Error during init of "+ this.getClass().toString()+"",
                ex);
        }
    }

    /**
     *
     * @return String Complete Version String
     */
    public static String versionString(){
        //init();
        if (!initialized) new Version();
        String newStatus=null;
        if (!status.trim().equals("")){
            newStatus="_"+status;
        } else newStatus="";
        return name+" "+majorRelease+"."+minorRelease+"."+build+newStatus;
    }

    /**
     *
     * @return String Complete Version String
     */
    public static String versionStringforrelease(){
        //init();
        if (!initialized) new Version();
        String newStatus=null;
        if (!status.trim().equals("")){
            newStatus="_"+status;
        } else newStatus="";
        return majorRelease+"."+minorRelease+"."+build+newStatus;
    }

    /**
     * Major release number.
     */
    public static String majorRelease(){
        if (!initialized) new Version();
        return majorRelease;
    }

    /**
     *Minor release number.
     */
    public static String minorRelease(){
        if (!initialized) new Version();
        return minorRelease;
    }
    /**
     * Build number.
     */
    public static String build(){
        if (!initialized) new Version();
        return build;
    }

    /**
     * Status [alpha, beta, (Empty when statable)].
     */
    public static String status(){
        if (!initialized) new Version();
        return status;
    }

    public static String copyright(){
        //init();
        if (!initialized) new Version();
        return copyright;
    }

    /**
     * Min Core compatible version
     * If null, it is considred compatible with all
     */
    public static String minCoreVersion(){
        if (!initialized) new Version();
        return minCoreVersion;
    }

    /**
     * Max Core compatible version
     * If null, it is considered compatible with all version numbers bigger
     * than ours
     */
    public static String maxCoreVersion(){
        if (!initialized) new Version();
        return maxCoreVersion;
    }


    /**
     * Checks is version provides is compatible with this version.
     * @param version String Version has to be of the form: MajorRelease.MinorRelease.Build_status.
     * @return boolean
     */
    public static boolean compatible(String version){
        if (!initialized) new Version();
        String majorRelease = version.substring(0,version.indexOf('.'));
        String cutVersion = version.substring(majorRelease.indexOf('.'));
        String minorRelease = cutVersion.substring(0,cutVersion.indexOf('.'));
        cutVersion = version.substring(majorRelease.indexOf('.'));
        String build = cutVersion.substring(0,cutVersion.indexOf('_'));

        boolean compatible = true;
        compatible = checkMaxVersion(majorRelease, minorRelease, build);
        compatible = checkMinVersion(majorRelease, minorRelease, build);
        return compatible;
    }


    //@todo review this matching method.
    private static boolean checkMaxVersion (String majorReleaseN, String minorReleaseN, String buildN){
        if (majorRelease==null) return true;
        if (!(Integer.parseInt(majorRelease) < (Integer.parseInt(majorReleaseN)))){return false;}
        if (!(Integer.parseInt(majorRelease) == (Integer.parseInt(majorReleaseN)))){
            if (!(Integer.parseInt(minorRelease) < (Integer.parseInt(minorReleaseN)))){return false;}
            if (!(Integer.parseInt(minorRelease) == (Integer.parseInt(minorReleaseN)))){
                if (!(Integer.parseInt(build) < (Integer.parseInt(buildN)))){return false;}
                if (!(Integer.parseInt(build) == (Integer.parseInt(buildN)))){
                    return true; // All numbers are equal :-)
                }
            }
        }

        return true;
    }


    //@todo review this matching method.
    private static boolean checkMinVersion (String majorReleaseN, String minorReleaseN, String buildN){
        if (majorRelease==null) return true;
        if (!(Integer.parseInt(majorRelease) > (Integer.parseInt(majorReleaseN)))){return false;}
        if (!(Integer.parseInt(majorRelease) == (Integer.parseInt(majorReleaseN)))){
            if (!(Integer.parseInt(minorRelease) > (Integer.parseInt(minorReleaseN)))){return false;}
            if (!(Integer.parseInt(minorRelease) == (Integer.parseInt(minorReleaseN)))){
                if (!(Integer.parseInt(build) > (Integer.parseInt(buildN)))){return false;}
                if (!(Integer.parseInt(build) == (Integer.parseInt(buildN)))){
                    return true; // All numbers are equal :-)
                }
            }
        }
        return true;
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
	if ((args.length > 0) && (args[0].equals("-b")))
	    System.out.print(versionStringforrelease());
	else if ((args.length > 0) && (args[0].equals("-min")))
	    System.out.print(minCoreVersion());
	else if ((args.length > 0) && (args[0].equals("-max")))
	    System.out.print(maxCoreVersion());
	else     	
	    System.out.print(versionString());
    }
}
