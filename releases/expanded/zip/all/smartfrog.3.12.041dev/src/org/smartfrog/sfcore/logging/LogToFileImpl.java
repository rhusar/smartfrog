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


package org.smartfrog.sfcore.logging;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.processcompound.SFProcess;

/**
 *
 *  Logs log info into a file.
 *
 */

public class LogToFileImpl extends LogToStreamsImpl implements LogToFile {

   //Configuration parameters

    /** Log file. */
    File logFile;

    StringBuilder fullLogFileName = new StringBuilder();

    /** String name for path. */
    String path=".";//+File.separator+"log";

    /**
     *  Log File extension
     */
    String logFileExtension = "log";
    /**  */
    String  fileNamePrefix = null;

    /** Use date in file name */
    boolean datedName = true;
    /**  Used to format date used in filename */
    protected DateFormat fileNameDateFormatter = new SimpleDateFormat(
            FILE_DATE_FORMAT);

    /** Use Log Name in file name */
    boolean useLogNameInFileName = true;

    /** Use HostName in file name */
    boolean useHostNameInFileName = true;

    /** Use ProcessName in file name */
    boolean useProcessNameInFileName = true;

    /** Redirect system.out and system.err */
    boolean redirectSystemOutputs = false;

    /** Append data  */
    boolean append = true;
    private static final String FILE_DATE_FORMAT = "yyyy-MM-dd_HH:mm:ss:SSS_zzz";


    /**
     * Construct a simple log with given name and log level
     * and log to output level
     * @param name log name
     * @param initialLogLevel level to log at
     */
    public LogToFileImpl (String name, Integer initialLogLevel) {
        this (name,null,initialLogLevel);                                                     
    }

    /**
     * Construct a simple log with given name and log level
     * and log to output level
     * @param name log name
     * @param componentComponentDescription A component description to overwrite class configuration
     * @param initialLogLevel level to log at
     */
    public LogToFileImpl(String name, ComponentDescription componentComponentDescription, Integer initialLogLevel) {
        super(name, initialLogLevel);
        try {
            readSFFileAttributes(classComponentDescription);
            readSFFileAttributes(componentComponentDescription);
            PrintStream out = null;
            logFile = createFile(logFileExtension);
            FileOutputStream fos=null;
            try {
               fos = new FileOutputStream(logFile, append);

            } catch (FileNotFoundException ex) {
               if (isErrorEnabled()) {
                   error("Failed to create log file '"+logFile+"', Reason: "+ex.getMessage()+", creating ramdom log file: ",ex);
               }
                // Create TempFile already checks for certail error and returns null if it failed to create temp file
                logFile = createTempFile ();
                if  (logFile!=null) {
                    try {
                        fos = new FileOutputStream(logFile, append);
                        String msg = "A tempfile file has been created for logging: "+ logFile.getAbsolutePath() +" to replace the user log file that could not be created";
                        if (isWarnEnabled()) {
                            warn(msg);
                            //Direct output in console so that the new log file can be found.
                            System.err.println("[WARN] " + msg);
                        }
                    } catch (Throwable e) {
                        faultInInitialization("Could not create logging file: "+ex.getMessage()+", or temp "+e.getMessage(),e);
                    }
                } else {
                    faultInInitialization("Could not create logging file.",ex);

                }
            }
            out = new PrintStream(fos);
            if (isDebugEnabled() && this.getClass().toString().endsWith(getShortClassName())) {
                //This will go to the std output.
                debug( getShortClassName() +" using file name: " + logFile.getAbsolutePath());
            }
            setOutstream(out);
            if (redirectSystemOutputs) {
                redirectOutputs();
            }
            if (isTraceEnabled() && this.getClass().toString().endsWith(getShortClassName())) {
                String msg2 = "Log '" + name + "' " +
                        "\nusing Class ComponentDescription:\n{" + classComponentDescription +
                        "}\n, and using Component ComponentDescription:\n{" + componentComponentDescription + "}";
                trace(this.getClass().toString() + " " + msg2);
            }
//        setLevel(initialLogLevel.intValue());
        } catch (FileNotFoundException ex) {
            faultInInitialization("Could not create logging file.",ex);
        }
        //For info
        if (logFile!=null) {
            setLogFileProperty (logFile.getAbsolutePath());
        }    
    }

    private String getShortClassName() {
        try {
            String fullName = getClass().getName();
            fullName = fullName.substring(0, fullName.lastIndexOf("."));
            return fullName.substring(fullName.lastIndexOf("."));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return "failedToGetShortName";
    }

    /**
     * Called to handle faults in initialization
     * @param thrown
     */
    private void faultInInitialization(String message, Throwable thrown) {
        try {
           if (isErrorEnabled()) {
              error(message,thrown);
           }
        } catch (Throwable thr){
           thrown.printStackTrace();
        }
    }

    /**
     *  Reads optional and mandatory attributes.
     * @param cd ComponentDescription A component description to read attributes from
     * @throws SmartFrogException error while reading attributes
     */
    protected void readSFFileAttributes(ComponentDescription cd) {
        if (cd==null) return;
        //Optional attributes.
        try {
          path = cd.sfResolve(ATR_PATH,path, false);
          logFileExtension =cd.sfResolve(ATR_LOG_FILE_EXTENSION,logFileExtension, false);
          datedName =cd.sfResolve(ATR_USE_DATED_FILE_NAME,datedName, false);
          redirectSystemOutputs = cd.sfResolve(ATR_REDIRECT_SYSTEM_OUTPUTS,redirectSystemOutputs, false);
          fileNamePrefix = cd.sfResolve(ATR_FILE_NAME_PREFIX,fileNamePrefix, false);
          useLogNameInFileName = cd.sfResolve(ATR_USE_LOG_NAME_IN_FILE_NAME,useLogNameInFileName, false);
          useHostNameInFileName = cd.sfResolve(ATR_USE_HOST_NAME_IN_FILE_NAME,useHostNameInFileName, false);
          useProcessNameInFileName = cd.sfResolve(ATR_USE_PROCESS_NAME_IN_FILE_NAME,useProcessNameInFileName, false);
          try {
             fileNameDateFormatter = new SimpleDateFormat(cd.sfResolve(ATR_FILE_NAME_DATE_FORMAT,
                     FILE_DATE_FORMAT, false));
          } catch (Exception ex) {
             if (this.isErrorEnabled())this.error("fileNameDateFormatter", ex);
          }
          append = cd.sfResolve(ATR_APPEND,append, false);
        } catch (Exception ex){
           this.warn("",ex);
        }
    }


    /**
     * Creates the file using attributes.
     * @param fileExtension  file extension
     * @return filename
     * @throws Exception error while creating file
     */
    public File createFile(String fileExtension)  {

        // check the path ends correctly
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        //Create new dir if it does not exist
        File parentDir = new File(path);
        parentDir.mkdirs();

        fullLogFileName.append(path);
        StringBuffer newfileName=new StringBuffer();

        if ((fileNamePrefix!=null)){
            if ((newfileName.toString().length()>0)&&!(newfileName.toString().endsWith("_"))) {
                            newfileName.append("_");
            }
          newfileName.append(fileNamePrefix);
        }

        if (useHostNameInFileName) {
            try {
                String hostname = SFProcess.sfDeployedHost().getCanonicalHostName();
                if ((newfileName.toString().length()>0)&&!(newfileName.toString().endsWith("_"))) {
                                newfileName.append("_");
                }
                newfileName.append(hostname);
            } catch (SmartFrogException ex) {
                if (isErrorEnabled()) error("",ex);
            }
        }

        if (useProcessNameInFileName) {
            String processName = System.getProperty(SmartFrogCoreProperty.propBaseSFProcess +SmartFrogCoreKeys.SF_PROCESS_NAME);
            if (processName!=null) {
                if ((newfileName.toString().length()>0) && !(newfileName.toString().endsWith("_"))) {
                    newfileName.append("_");
                }
                newfileName.append(processName);
            }
        }

        if (useLogNameInFileName) {
            if ((newfileName.toString().length()>0)&&!(newfileName.toString().endsWith("_"))) {
                            newfileName.append("_");
            }
           newfileName.append(instanceName);
        }

        if (datedName){
            if ((newfileName.toString().length()>0)&&!(newfileName.toString().endsWith("_"))) {
                            newfileName.append("_");
            }
            // add the extension
            newfileName.append(fileNameDateFormatter.format(new Date()));
        }

        // add the extension
        newfileName.append("."+fileExtension);

        fullLogFileName.append (correctFilename(newfileName.toString()));

        //Return file
        return new File( fullLogFileName.toString());
    }

    /**
     * Creates a ramdom temp file
     * @return filename
     * @throws Exception error while creating file
     */
    public File createTempFile()  {
        String tempdir=System.getProperty("java.io.tmpdir");
        if( tempdir == null ) {
            if (this.isWarnEnabled()) {
                System.err.println("[ERROR] java.io.tmpdir is undefined. No temp file is created");
            }
            return null;
        }
        File tempDirectory=new File(tempdir);
        if(!tempDirectory.exists()) {
            if (this.isWarnEnabled()) {
                System.err.println("[ERROR] java.io.tmpdir directory does not exist: "+ tempdir);
            }
            return null;
        }
        //create the file
        long now=System.currentTimeMillis();
        File tempFile=null;

        try {
            tempFile = File.createTempFile ("smartfrogTemLog","log",tempDirectory);
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to create a temporary file in the temp dir " + tempdir);
            System.err.println("[ERROR] File  "+ tempFile + " could not be created");
            if(tempFile!=null && tempFile.exists()) {
              tempFile.delete();
            }
            testDir(tempDirectory.toString());
            return null;
        }
        //Return file
        return tempFile;
    }

    /**
     * Correct the file name
     * @param filename file name
     * @return  String
     */
    private String correctFilename(String filename) {
        final int length = filename.length();
        StringBuffer buffer=new StringBuffer(length);
        for(int i=0;i<length;i++) {
            char c= filename.charAt(i);
            switch(c) {
                case ':':
                    buffer.append('_');
                    break;
                case ' ':
                    buffer.append('_');
                    break;
                case '"':
                    //remove these
                    break;
                default:
                    buffer.append(c);
            }
        }
        return new String(buffer);
    }

    /**
     * Redirects system outputs to a file.
     * @throws Exception if any io error
     */
    public void redirectOutputs() throws FileNotFoundException {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        /** Direct brand new outs to a file. */
        PrintStream newOut = null;
        /** Direct brand new errs to a file. */
        PrintStream newErr = null;

        /** Log OUT file. */
        File logOutFile= new File(fullLogFileName.toString()+".out");
        /** Log ERR file. */
        File logErrFile = null;
        if (!errToOut) {
             logErrFile = new File(fullLogFileName.toString()+".err");
        }

        if (logFile != null) {
            FileOutputStream fos = new FileOutputStream(logOutFile);
            newOut = new PrintStream(fos);

            FileOutputStream fes = null;
            if (!errToOut) {
                fes = new FileOutputStream(logErrFile);
                newErr = new PrintStream(fes);
            }

            try {
                // Redirecting  output
                System.setOut(newOut);
                // Redirecting  err
                System.setErr(errToOut?newOut:newErr);
            } catch (Exception e) {
                System.setOut(originalOut);
                System.setErr(originalErr);
            }
        }
    }


    /**
     * Tries and creates a temp file in our  dir; this
     * checks that it has space and access.
     * It also does some clock reporting using std.error for messages
     *
     * Derived from Ant Diagnostics class
     * @param dir directory path
     */
    private void testDir(String dir) {
        try {
            if (( dir == null )||(dir.trim().equals(""))) {
                error ("'dir' is undefined");
                return;
            }
            //System.err.println("[ERROR] Temp dir is "+ dir);
            File tempDirectory=new File(dir);
            if(!tempDirectory.exists()) {
                error(""+dir+" directory does not exist: "+dir);
                return;
            }
            //create a temp file for testing
            long now=System.currentTimeMillis();
            File tempFile=null;
            FileOutputStream fileout = null;
            try {
                tempFile = File.createTempFile("sfDiagLog","txt",tempDirectory);
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
                //System.err.println("Temp dir is writeable");
                long drift=filetime-now;
                //System.err.println("temp dir alignment with system clock is "+drift+" ms");
                if(Math.abs(drift)>10000) {
                    warn("big clock drift -maybe a network filesystem");
                }
            } catch (IOException e) {
                error("[ERROR] File  "+ tempFile + " could not be created/written to" + dir,e);
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
        } catch (Throwable thr) {
            thr.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    void setLogFileProperty (String fullFileName ){
         // This is just for info, it security does not allow it, nothing will break.
         try {
            String className = this.getClass().toString();
            if (className.startsWith("class ")) {
              className = className.substring(6);
            }
            String propContent = System.getProperty(className);
            if  (propContent!=null){
               if (!(propContent.contains(fullFileName))){
                  System.setProperty(className+".info.fileName", propContent+", "+fullFileName);
               }
            } else {
               System.setProperty(className+".info.fileName", fullFileName);
            }

         } catch (Throwable thr){
             //ignore
         }
    }

}
