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

// Some methods taken from ANT 1.5 Diagnostics class.

package org.smartfrog.sfcore.common;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.util.Properties;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Arrays;
import java.util.Comparator;
import java.io.Serializable;


/**
 * Diagnostic helper class to extract some information that may help
 * in support.
 *
 */
public final class Diagnostics {

    /** Utility class */
    private Diagnostics() {
      //private constructor
    }

    /**
     * return the list of jar files existing in "dir".
     * @return the list of jar files existing in "dir" or
     * <tt>null</tt> if an error occurs.
     *
     * Stolen from ANT Diagnostics class
     */
    public static File[] listLibraries(String dir) {
        if (dir == null) {
            return null;
        }
        File libDir = new File(dir, "lib");
        return listJarFiles(libDir);

    }

    /**
     * get a list of all JAR files in a directory
     *
     * Stolen from ANT Diagnostics class
     *
     * @param libDir directory
     * @return array of files (or null for no such directory)
     *
     */
    private static File[] listJarFiles(File libDir) {
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        };

        File[] files  = libDir.listFiles(filter);
        return files;
    }

    /**
     * main entry point for command line
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        doReport(System.out);
    }


    /**
     * Helper method to get the implementation version.
     * @param clazz the class to get the information from.
     * @return null if there is no package or implementation version.
     * '?.?' for JDK 1.0 or 1.1.
     * Stolen from ANT Diagnostics class
     */
    private static String getImplementationVersion(Class clazz) {
        Package pkg = clazz.getPackage();
        return pkg.getImplementationVersion();
    }

    /**
     * Print a report to the given stream.
     * @param out the stream to print the report to.
     */
    public static void doReport(PrintStream outPS) {
      StringBuffer out = new StringBuffer();
      doReport(out);
      outPS.print(out);
    }

    /**
     * Print a report to the given stream.
     * @param out the StringBuffer to print the report to.
     */
    public static void doReport(StringBuffer out) {

        out.append("------- SF diagnostics report -------");

        header(out, "Implementation Version");
        out.append(org.smartfrog.Version.versionString());
        out.append(org.smartfrog.Version.copyright());
        out.append("Build date: "+ org.smartfrog.Version.buildDate());

        header(out, "System properties");
        doReportSystemProperties(out);

        header(out, "ClassPath");
        doReportClassPath(out);

        header(out, "CodeBase");
        doReportClassPath(out);

        header(out, "System properties summary");
        doReportSummary(out);

        header(out, "Temp dir");
        doReportTempDir(out);

        header(out, "Locale information");
        doReportLocale(out);
        header(out, org.smartfrog.Version.versionString() + " - "+org.smartfrog.Version.buildDate());
        out.append("\n");
    }

    //  Stolen from ANT Diagnostics class
    private static void header(StringBuffer out, String section) {
        out.append("\n");
        out.append("-------------------------------------------");out.append("\n");
        out.append(" ");
        out.append(section);out.append("\n");
        out.append("-------------------------------------------");out.append("\n");
    }

    /**
     * Report a listing of system properties existing in the current vm.
     * @param out the stream to print the properties to.
     */
    private static void doReportSystemProperties(StringBuffer out) {
        Properties sysprops = null;
        try {
            sysprops = System.getProperties();
        } catch (SecurityException  e) {
            out.append("Access to System.getProperties() blocked " +
                    "by a security manager");out.append("\n");
        }

        Vector keysVector = new Vector();
        for (Enumeration keys = sysprops.propertyNames(); keys.hasMoreElements();) {
          keysVector.add((String) keys.nextElement());
        }
        // Order keys
        keysVector= sort(keysVector);

        //for (Enumeration keys = sysprops.propertyNames();keys.hasMoreElements();) {
        for (Enumeration keys = keysVector.elements(); keys.hasMoreElements();) {
            String key = (String) keys.nextElement();
            String value;
            try {
                value = System.getProperty(key);
            } catch (SecurityException e) {
                value = "Access to this property blocked by a security manager";
            }
            out.append(key + " : " + value);out.append("\n");
        }
    }

    /**
     *  Sort a Vector
     *
     *@param  vect  Description of Parameter
     *@return       Description of the Returned Value
     */
    public static Vector sort(Vector vect) {
       Object[] array = vect.toArray();
//       if (debugOn) {
//        System.out.append("Vector.size:" + vect.size());
//       }
       //System.out.append("Array:" + array.toString());
       Arrays.sort(array, new StringComparator());
       //System.out.append("ArraySorted:" + array.toString());
       //vect = new Vector(array);
       vect = new Vector();
       //for (int i =0 ; i < array.length; i++) {
       for (int i = array.length-1 ; i >= 0; i--) {
          vect.add(array[i]);
       }
//       if (debugOn) {
//          System.out.append("Vector.size(After):" + vect.size());
//       }
       return vect;
   }


    /**
     * Report a listing of some system properties existing in the current vm.
     * @param out the stream to print the properties to.
     */
    private static void doReportSummary(StringBuffer out) {

      out.append("* Java Version:   " + System.getProperty("java.version"));out.append("\n");
      out.append("* Java Home:      " + System.getProperty("java.home"));out.append("\n");
      out.append("* Java Ext Dir:   " + System.getProperty("java.ext.dirs"));out.append("\n");

      //out.append("* Java ClassPath: " + System.getProperty("java.class.path")out.append("\n");
      //);
      out.append("* OS Name:        " + System.getProperty("os.name"));out.append("\n");
      out.append("* OS Version:     " + System.getProperty("os.version"));out.append("\n");
      out.append("* User Name:      " + System.getProperty("user.name"));out.append("\n");
      out.append("* User Home:      " + System.getProperty("user.home"));out.append("\n");
      out.append("* User Work Dir:  " + System.getProperty("user.dir"));out.append("\n");

      try {
        java.net.InetAddress localhost = java.net.InetAddress.getLocalHost();
        out.append("* LocalHost Name: " + localhost.getCanonicalHostName());out.append("\n");
        out.append("* LocalHost Add:  " + localhost.getHostAddress());out.append("\n");

        //out.append("* isMulticast?    " + localhost.isMulticastAddress());
      } catch (Exception ex) {
        out.append("Exception Info:" + ex.toString());out.append("\n");
      }

    }

    /**
     * Report a listing of classpath used in the current vm.
     * @param out the stream to print the properties to.
     */
    private static void doReportClassPath(StringBuffer out) {
      out.append(""+(System.getProperty("java.class.path")).replace(
                      System.getProperty("path.separator").charAt(0), '\n'));
    }

    /**
     * Report a listing of codebase used in the current vm.
     * @param out the stream to print the properties to.
     */
    private static void doReportCodeBase(StringBuffer out) {
      out.append(""+
         (System.getProperty(org.smartfrog.sfcore.security.SFClassLoader.SF_CODEBASE_PROPERTY)).replace(
          System.getProperty("path.separator").charAt(0), '\n'));
    }

    private static void multiPathReport(String multiPath,StringBuffer out) {
      String[] array = System.getProperty(multiPath).split(System.getProperty("path.separator"));
      Arrays.sort(array, new StringComparator());
      for (int i = array.length-1 ; i >= 0; i--) {
         out.append(array[i]);out.append("\n");
      }
    }


//    /**
//     * Report the content of ANT_HOME/lib directory
//     * @param out the stream to print the content to
//     */
//    private static void doReportLibraries(PrintStream out) {
//        out.append("ant.home: " + System.getProperty("ant.home"));
//        File[] libs = listLibraries();
//        printLibraries(libs, out);
//    }


    /**
     * list the libraries
     * Stolen from ANT Diagnostics class     *
     * @param libs array of libraries (can be null)
     * @param out output stream
     */
    private static void printLibraries(File[] libs, StringBuffer out) {
        if (libs == null) {
            out.append("No such directory.");out.append("\n");
            return;
        }
        for (int i = 0; i < libs.length; i++) {
            out.append(libs[i].getName()+ " (" + libs[i].length() + " bytes)");out.append("\n");
        }
    }


    /**
     * try and create a temp file in our temp dir; this
     * checks that it has space and access.
     * We also do some clock reporting.
     *
     * Stolen from ANT Diagnostics class
     *
     * @param out
     */
    private static void doReportTempDir(StringBuffer out) {
        String tempdir=System.getProperty("java.io.tmpdir");
        if( tempdir == null ) {
            out.append("Warning: java.io.tmpdir is undefined");out.append("\n");
            return;
        }
        out.append("Temp dir is "+ tempdir);out.append("\n");
        File tempDirectory=new File(tempdir);
        if(!tempDirectory.exists()) {
            out.append("Warning, java.io.tmpdir directory does not exist: "+
                    tempdir);out.append("\n");
            return;
        }
        //create the file
        long now=System.currentTimeMillis();
        File tempFile=null;
        FileOutputStream fileout = null;
        try {
            tempFile = File.createTempFile("sfDiag","txt",tempDirectory);
            //do some writing to it
            fileout = new FileOutputStream(tempFile);
            byte buffer[]=new byte[1024];
            for(int i=0;i<32;i++) {
                fileout.write(buffer);
            }
            fileout.close();
            fileout=null;
            long filetime=tempFile.lastModified();
            tempFile.delete();
            out.append("Temp dir is writeable");out.append("\n");
            long drift=filetime-now;
            out.append("temp dir alignment with system clock is "+drift+" ms");out.append("\n");
            if(Math.abs(drift)>10000) {
                out.append("Warning: big clock drift -maybe a network filesystem");out.append("\n");
            }
        } catch (IOException e) {
            out.append("Failed to create a temporary file in the temp dir "
                + tempdir);out.append("\n");
            out.append("File  "+ tempFile + " could not be created/written to");out.append("\n");
        } finally {
          if (fileout != null) {
            try {
              fileout.close();
            } catch (IOException ioex) {
              //ignore
            }
          }
          if(tempFile!=null && tempFile.exists()) {
              tempFile.delete();
          }
        }
    }

    /**
     * Report locale information
     * Stolen from ANT Diagnostics class
     * @param out
     */
    private static void doReportLocale(StringBuffer out) {
        //calendar stuff.
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        out.append("Timezone " + tz.getDisplayName()
                + " offset=" + tz.getOffset(cal.get(Calendar.ERA),
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.DAY_OF_WEEK),
                        ((cal.get(Calendar.HOUR_OF_DAY) * 60
                         + cal.get(Calendar.MINUTE)) * 60
                         + cal.get(Calendar.SECOND)) * 1000
                         + cal.get(Calendar.MILLISECOND)));out.append("\n");
    }
}


  /**
   * Comparator for String objects
   */
  class StringComparator implements Comparator, Serializable {

     /**
      *  Compares two String objects
      *
      *@param  o1  Description of Parameter
      *@param  o2  Description of Parameter
      *@return     Description of the Returned Value
      */
     public int compare(Object o1, Object o2) {
        if (!(o1 instanceof String)) {
           throw new ClassCastException();
        }
        if (!(o2 instanceof String)) {
           throw new ClassCastException();
        }

        int result = ((String)o1).compareTo(((String)o2));
        return result * (-1);
   }
     //end compare()
  }
//end class TheComparator
