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
/*
 * Copyright  2000-2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */



package org.smartfrog.sfcore.common;

import org.smartfrog.Version;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.security.SFSecurity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Vector;


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
     * @param dir directory
     * @return the list of jar files existing in "dir" or
     * <tt>null</tt> if an error occurs.
     *
     * Derived from Ant Diagnostics class
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
     * Derived from Ant Diagnostics class
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

//    /**
//     * main entry point for command line
//     * @param args command line arguments.
//     */
//    public static void main(String[] args) {
//        doReport(System.out);
//    }


    /**
     * Helper method to get the implementation version.
     * @param clazz the class to get the information from.
     * @return null if there is no package or implementation version.
     * '?.?' for JDK 1.0 or 1.1.
     * Derived from Ant Diagnostics class
     */
    public static String getImplementationVersion(Class clazz) {
        Package pkg = clazz.getPackage();
        return pkg.getImplementationVersion();
    }

    /**
     * Print a report to the given stream.
     * @param outPS the stream to print the report to.
     */
    public static void doReport(PrintStream outPS) {
      StringBuffer out = new StringBuffer();
      doReport(out);
      outPS.print(out);
    }


    private static void doReportCommon(StringBuffer out) {
        header(out, "Implementation Version");
        out.append(Version.versionString());
        out.append("\n");
        out.append(Version.copyright());
        out.append("\n");
        out.append("Build date: ");
        out.append(Version.buildDate());
        out.append("\n");

        header(out, "System properties summary");
        doReportSummary(out);

        header(out, "Temp dir");
        doReportTempDir(out);

        header(out, "Network");
        if (Logger.testNetwork) {
            doReportLocalNetwork(out);
            out.append("\n");
            doReportRemoteNetwork(out, Logger.testURI);
        } else {
            out.append("Network report disabled.");
        }
        out.append("\n");


        header(out, "ClassPath");
        doReportClassPath(out);

        //header(out, "ClassPath repeats");
        doReportClassPathRepeats(out);


        header(out, "CodeBase");
        doReportCodeBase(out);
        
        //header(out, "CodeBase repeats");
        doReportCodeBaseRepeats(out);

        header(out, "Locale information");
        doReportLocale(out);
    }



    /**
     * Print a report to the given StringBuffer.
     * @param out the StringBuffer to print the report to.
     * @param object prim/componentdescription the SmartFrog component where to extract info from.
     * Derived from Ant Diagnostics class
     */
    public static void doReport(StringBuffer out, Object object) {

        if (object instanceof Prim) {
            out.append("\n------- SF diagnostics report -------");
        } else if (object instanceof ComponentDescription) {
            out.append("\n------- SF CD diagnostics report -------");
        } else {
           out.append("\n------- SF (unknown) diagnostics report -------");
        }

        doReportCommon(out);

        if (object instanceof Prim) {
            doReportPrim(out, (Prim) object);
        } else if (object instanceof ComponentDescription) {
            doReportCD(out, (ComponentDescription) object);
        } else {
            header (out, " WARNING: no SF object to report");
        }

        header(out, "System Thread Dump");
        doReportThreadDump(out);

        header(out, "System properties");
        doReportSystemProperties(out);

        header(out, Version.versionString() );
        out.append("\n");

    }


   /**
     * Print a short report to the given StringBuffer.
     * @param out the StringBuffer to print the report to.
     * @param object prim/componentdescription the SmartFrog component where to extract info from.
     * Derived from Ant Diagnostics class
     */
    public static void doShortReport(StringBuffer out, Object object) {
        doReportCommon(out);
        header(out, Version.versionString() );
        out.append("\n");
    }


        /**
          * Report specific Prim information.
         * @param out StringBuffer
         * @param cd  Component Description
         *
         */
        private static void doReportCD(StringBuffer out, ComponentDescription cd) {
            if (cd!=null) {
              try {
                Diagnostics.header(out, "sfCompleteName");
                out.append(cd.sfCompleteName()); out.append("\n");
              } catch (Exception ex1) {
                  out.append(" Error:").append(ex1.getMessage()).append("\n");
              }

              header(out, "class");
              out.append(cd.getClass().getName());out.append("\n");

              Diagnostics.header(out, "sfParent");
              try {
                ComponentDescription parent = cd.sfParent();
                out.append("CD Parent: ").append(parent.sfCompleteName()).append("\n");
                out.append("        [");
                out.append(parent.getClass().toString());
                out.append("] ");out.append("\n");
              } catch (Exception ex) {
                out.append("No CD parent: " + ex.getMessage());out.append("\n");
              }
              Diagnostics.header(out, "sfPrimParent");
              try {
                Prim parent = cd.sfPrimParent();
                out.append("Prim Parent: ").append(parent.sfCompleteName()).append("\n");
                out.append("        [");
                out.append(parent.getClass().toString());
                out.append(", ");
                out.append(parent.sfDeployedHost());
                out.append("] ");out.append("\n");
              } catch (Exception ex) {
                out.append("No Prim parent: " + ex.getMessage());out.append("\n");
              }

              try {
                Diagnostics.header(out, "sfContext");
                out.append(cd.sfContext().toString()); //out.append("\n");
              } catch (Exception ex2) {
                out.append(" Error:" + ex2.getMessage());
                out.append("\n");
              }

              if (cd.sfPrimParent()!=null){
                  Diagnostics.header(out, "Diagnostics for sfPrim parent");
                  out.append("###########################################");
                  doReportPrim(out, cd.sfPrimParent());
                  out.append(out.toString().replaceAll("\n", "\n    "));
                  out.append("\n#########################################\n");
              }
          }
        }



    /**
      * Report specific Prim information.
     * @param out StringBuffer
     * @param prim Compound
     */
    private static void doReportPrim(StringBuffer out, Prim prim) {
        if (prim!=null) {
          try {
            Diagnostics.header(out, "sfCompleteName");
            out.append(prim.sfCompleteName()); out.append("\n");
          } catch (RemoteException ex1) {
              out.append(" Error:").append(ex1.getMessage()).append("\n");
          }

          header(out, "class");
          out.append(prim.getClass().getName());out.append("\n");

          Diagnostics.header(out, "sfParent");
          try {
            Prim parent = prim.sfParent();
              out.append("Parent: ").append(parent.sfCompleteName()).append("\n");
            out.append("        [");
            out.append(parent.getClass().toString());
            out.append(", ");
            out.append(parent.sfDeployedHost());
            out.append("] ");out.append("\n");
          } catch (Exception ex) {
            out.append("No parent: " + ex.getMessage());out.append("\n");
          }

          try {
            Diagnostics.header(out, "sfContext");
            out.append(prim.sfContext().toString()); //out.append("\n");
          } catch (RemoteException ex2) {
            out.append(" Error:");
            out.append(ex2.getMessage());
            out.append('\n');
          }

          if (prim instanceof Compound) {
            doReportCompound(out, (Compound)prim);
          }

          if (!(prim instanceof ProcessCompound)){
            doReportProcessCompound(out);
          }
      }
    }

    /**
      * Report specific information to local process compound.
     * @param out StringBuffer
     */
    private static void doReportProcessCompound(StringBuffer out) {
      try {
        ProcessCompound pc = SFProcess.getProcessCompound();
        StringBuffer reportPC = new StringBuffer();
        Diagnostics.header(out, "sfContext host ProcessCompound");
        out.append("+++++++++++++++++++++++++++++++++++++++++++");
        doReportPrim (reportPC,pc);
        out.append(reportPC.toString().replaceAll("\n","\n    "));
        out.append("\n+++++++++++++++++++++++++++++++++++++++++++\n");
      } catch (Exception ex2) {
        out.append(" Error:" + ex2.getMessage() + "\n");
      }
    }

    /**
      * Report specific Compound information.
     * @param out StringBuffer
     * @param compound Compound
     */
    private static void doReportCompound(StringBuffer out, Compound compound) {
      Enumeration enu = null;
      StringBuilder childrenInfo = new StringBuilder();
      Prim child = null;
      try {
        Diagnostics.header(out, "sfChildren");
        for (enu =  compound.sfChildren(); enu.hasMoreElements(); ) {
          try {
            child = (Prim) enu.nextElement();
            childrenInfo.append("- ");
            childrenInfo.append(child.sfCompleteName());
            childrenInfo.append("\n");
            childrenInfo.append("      [");
            childrenInfo.append(child.getClass().toString());
            childrenInfo.append(", ");
            childrenInfo.append(child.sfDeployedHost());
            childrenInfo.append("] \n");
          } catch (Throwable ex) {
            childrenInfo.append("  Error: ");
            childrenInfo.append(ex.getMessage());
            childrenInfo.append(" \n");
          }
        }
        out.append(childrenInfo.toString());
      } catch (RemoteException ex) {
        out.append(childrenInfo.toString());
        out.append("\n Error: ");
        out.append(ex.toString());
      }
    }

    /**
     * Print a report to the given StringBuffer.
     * @param out the StringBuffer to print the report to.
     */
    public static void doReport(StringBuffer out) {
       doReport ( out, null);
    }

    /**
     * Print a short report to the given StringBuffer.
     * @param out the StringBuffer to print the report to.
     */
    public static void doShortReport(StringBuffer out) {
       doShortReport (out, null);
    }

    //  Derived from Ant Diagnostics class
    public static void header(StringBuffer out, String section) {
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

        	Vector<String> keysVector = new Vector<String>();
        	for (Enumeration keys = sysprops.propertyNames(); keys.hasMoreElements();) {
        		keysVector.add((String) keys.nextElement());
        	}
        	// Order keys
        	keysVector= JarUtil.sort(keysVector);


        	for(String key:keysVector) {
        		String value = "";
        		try {
        			if (!(key.trim().equals(""))) value = System.getProperty(key);
        		} catch (SecurityException e) {
        			value = "Access to this property blocked by a security manager";
        		}
        		out.append(key).append(" : ").append(value);
        		out.append('\n');
        	}
        } catch (SecurityException  e) {
        	out.append("Access to System.getProperties() blocked by a security manager\n");
        }
    }

    /**
      * Report specific information to local process compound.
     * @param out StringBuffer
     */
    private static void doReportThreadDump(StringBuffer out) {
      try {
        ThreadDump td = new ThreadDump();
        StringBuffer reportTD = new StringBuffer();
        out.append("+++ Threads - [nane (id), state, priority, isDaemon, thread group]\n");
        for (Thread thread : td.getThreads()){
           out.append( thread.getName()+" ("+thread.getId()+")"+
                      ", "+thread.getState()+", "+thread.getPriority()+", "+thread.isDaemon()+
                      ", "+thread.getThreadGroup()+"\n");
        }

        out.append("\n+++ Traces - [name (id), stack trace \n");
        Map<Thread, StackTraceElement[]> traces = td.getTraces();   
        for (Thread thread : traces.keySet()){
            out.append(thread.getName()+" ("+thread.getId()+")"+"\n");
            for (StackTraceElement threadTraceLine :traces.get(thread)){
               out.append("    "+ threadTraceLine+"\n");
            }
        }        
      } catch (Exception ex2) {
        out.append(" Error:" + ex2.getMessage() + "\n");
      }
    }


    /**
     * Report a summary of system properties.
     * @param out the stream to print the properties to.
     */
    private static void doReportSummary(StringBuffer out) {

      out.append("* Java Version:    ");out.append( System.getProperty("java.version"));out.append("\n");
      out.append("* Java Home:       ");out.append(System.getProperty("java.home"));out.append("\n");
      out.append("* Java Ext Dir:    ");out.append(System.getProperty("java.ext.dirs"));out.append("\n");

      //out.append("* Java ClassPath: " + System.getProperty("java.class.path")out.append("\n");
      //);
      out.append("* OS Name:         ");out.append(System.getProperty("os.name"));out.append("\n");
      out.append("* OS Version:      ");out.append(System.getProperty("os.version"));out.append("\n");
      out.append("* User Name:       ");out.append(System.getProperty("user.name"));out.append("\n");
      out.append("* User Home:       ");out.append(System.getProperty("user.home"));out.append("\n");
      out.append("* User Work Dir:   ");out.append(System.getProperty("user.dir"));out.append("\n");

      try {
        java.net.InetAddress localhost = java.net.InetAddress.getLocalHost();
        out.append("* LocalHost Name:  ");out.append(localhost.getCanonicalHostName());out.append("\n");
        out.append("* LocalHost Addr:  ");out.append(localhost.getHostAddress());out.append("\n");

        //out.append("* isMulticast?    " + localhost.isMulticastAddress());
      } catch (Exception ex) {
        out.append("Exception Info:");out.append(ex.toString());out.append("\n");
      }
      out.append("* \n");
      String nameP =System.getProperty("org.smartfrog.iniFile");
      if ( nameP!=null){
        out.append("* SF ini file:     ");out.append(nameP);out.append("\n");
      }
      //@TODO improve this, there can be many. Prefix:  org.smartfrog.sfcore.processcompound.sfDefault.x
      nameP =System.getProperty("org.smartfrog.sfcore.processcompound.sfDefault.sfDefault");
      if ( nameP!=null){
        out.append("* SF default desc: ");out.append(nameP);out.append("\n");
      }
      nameP =System.getProperty("org.smartfrog.sfcore.processcompound.sfProcessName");
      if ( nameP!=null){
        out.append("* SF process name: ");out.append(nameP);out.append("\n");
      }

      nameP =System.getProperty(SmartFrogCoreProperty.codebase);
      if ((nameP!=null)&& !nameP.equals("")){
        out.append("* SF codebase:     ");out.append(nameP);out.append("\n");
      }

      nameP =System.getProperty("java.awt.headless");
      if ((nameP!=null)&& !nameP.equals("")){
        out.append("* Headless:        true");out.append("\n");
      } else {
        out.append("* Headless:        false");out.append("\n");  
      }

      if ( SFSecurity.isSecurityOn()){
        out.append("* SmartFrog Security: ON\n");
      } else {
         out.append("* SmartFrog Security: OFF\n");
      }
      if ( SFSecurity.isSecureResourcesOff()){
        out.append("* Secure Resources:  OFF\n");
      }
      nameP =System.getProperty("java.security.policy");
      if ( nameP!=null){
        out.append("* Java security policy:     ");out.append(nameP);out.append("\n");
      }

    }

    /**
     * Report simple local network diagnostics by default bound to local hostname
     * @param out the stream to print the report to.
     * @return failed. It reports if the test failed or not.
     */
    public static boolean doReportLocalNetwork(StringBuffer out) {
       boolean failed = true;
       URI uri = null;
       long time =0;
       long time2 = 0;
       InetAddress localhost=null;
       out.append("Network test localhost: ");
       try {
         localhost = SFProcess.sfDeployedHost();
         String localhostName = localhost.getCanonicalHostName();
         out.append("hostname '"+localhostName+"', ");
         out.append("ip '"+localhost.getHostAddress()+"', ");
         time=System.currentTimeMillis();
         InetAddress newLocalhost = InetAddress.getByName(localhostName);
         time2=System.currentTimeMillis()-time;
         if (localhost.equals(newLocalhost)){
             out.append(" [Successful], ").append(time2).append("ms");
             failed = false;
         } else {
             out.append(" [Failed], ").append(time2).append("ms");
         }
       } catch (Exception ex1) {
           time2=System.currentTimeMillis()-time;
           out.append("[Failed], Failed to resolve localhost ip, "+time2+"ms"+", "+ex1.toString());
       }
       return failed;
     }

    /**
     * Report simple remote network diagnostics for list of URIs
     *
     * @param out the stream to print the report to.
     * @param listURI list of URIs for a host to reach
     */
    private static void doReportRemoteNetwork(StringBuffer out, String[] listURI) {
        for (int i = listURI.length; i > 0; i--) {
            doReportRemoteNetwork(out, listURI[i - 1]);
        }
    }

    /**
     * Report simple remote network diagnostics of the system given by the URIhed
     * @param out the stream to print the report to.
     * @param uriString URI for a host to reach
     * @return failed. It reports if the test failed or not.
     */
    public static boolean doReportRemoteNetwork(StringBuffer out, String uriString) {
       boolean failed = true;
       URI uri = null;
       long time =0;
       long time2 = 0;
       InetAddress remotehost=null;
       out.append("Network test remotehost ("+uriString+"): ");
       try {
           uri =new URI(uriString); // Default address, we need to add a list of them during init
           try {
              time=System.currentTimeMillis();
              remotehost = InetAddress.getByName(uri.getHost());
              time2=System.currentTimeMillis()-time;
              String remotehostName = remotehost.getCanonicalHostName();
              out.append("hostname '"+remotehostName+"', ");
              out.append("ip '"+remotehost.getHostAddress()+"', "+time2+"ms, ");
              time=System.currentTimeMillis();
              InetAddress newRemotehost = InetAddress.getByName(remotehostName);
              time2=System.currentTimeMillis()-time;
              if (remotehost.equals(newRemotehost)){
                 out.append(" [Successful], "+time2+"ms");
                 failed = false;
              } else {
                 out.append(" [Failed], "+time2+"ms");
              }
           } catch (UnknownHostException ex) {
               time2=System.currentTimeMillis()-time;
               out.append("[Failed], Failed to resolve remote hostname '"+uri.getHost()+"', "+time2+"ms, "+ex.toString());
           }
       } catch (URISyntaxException e) {
           out.append("[Failed], Broken uri for remote host: '"+uriString+"', "+e.toString());
       }
       out.append('\n');
       return failed;
     }


    /**
     * Report a listing of classpath used in the current vm.
     * @param out the stream to print the properties report to.
     */
    public static void doReportClassPath(StringBuffer out) {
      out.append((System.getProperty("java.class.path")).replace(
                      System.getProperty("path.separator").charAt(0), '\n'));
    }

    /**
     * Report a listing of codebase used in the current vm.
     * @param out the stream to print the properties to.
     */
    private static void doReportCodeBase(StringBuffer out) {

      String codebaseString = System.getProperty(org.smartfrog.sfcore.security.SFClassLoader.SF_CODEBASE_PROPERTY);
      if (codebaseString!=null) {
          out.append(""+codebaseString.replace(System.getProperty("path.separator").charAt(0), '\n'));
      } else {
         out.append("Not defined, using default\n");
      }
    }

    /**
     * Splits a multipath in several lines using "part.separator" property
     * @param multiPath String
     * @param out StringBuffer
     */
    private static void multiPathReport(String multiPath,StringBuffer out) {
      String[] array = System.getProperty(multiPath).split(System.getProperty("path.separator"));
      Arrays.sort(array, new StringComparator());
      for (int i = array.length-1 ; i >= 0; i--) {
         out.append(array[i]);out.append("\n");
      }
    }


//        File[] libs = listLibraries();
//        printLibraries(libs, out);

    /**
     * List the libraries
     * Derived from Ant Diagnostics class     *
     * @param libs array of libraries (can be null)
     * @param out String Buffer
     */
    private static void printLibraries(File[] libs, StringBuffer out) {
        if (libs == null) {
            out.append("No such directory.\n");
            return;
        }
        for (File lib : libs) {
            out.append(lib.getName() + " (" + lib.length() + " bytes)");
            out.append("\n");
        }
    }


    /**
     * Tries and creates a temp file in our temp dir; this
     * checks that it has space and access.
     * It also does some clock reporting.
     *
     * Derived from Ant Diagnostics class
     * @param out String Buffer
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
            byte[] buffer=new byte[1024];
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
     * Derived from Ant Diagnostics class
     * @param out String Buffer
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

   /**
     * Report a listing of classpath used in the current vm.
     * @param out the stream to print the properties report to.
     */
   private static void doReportClassPathRepeats(StringBuffer out) {
      String[] words = Logger.testJarRepeat;
      String classpath[] = (System.getProperty("java.class.path")).split(System.getProperty("path.separator"));
      StringBuffer message = Logger.getRepeatsMessage(words, classpath);
      if (message !=null) out.append(message.toString());
      out.append("\n");
   }

   /**
     * Report a listing of classpath used in the current vm.
     * @param out the stream to print the properties report to.
     */
   private static void doReportCodeBaseRepeats(StringBuffer out) {
      String[] words = Logger.testJarRepeat;
      StringBuffer message = null;
      String codebaseproperty = System.getProperty(SFClassLoader.SF_CODEBASE_PROPERTY);
      if (codebaseproperty != null) {
          String codebase[] = codebaseproperty.split(System.getProperty("path.separator"));
          message =  Logger.getRepeatsMessage(words, codebase);
          if (message !=null) out.append(message.toString());
      }      
      out.append("\n");
   }


}
