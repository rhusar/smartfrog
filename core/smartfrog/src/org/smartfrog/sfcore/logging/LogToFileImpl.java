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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;

/**
 *
 *  Logs log info into a file.
 *
 */

public class LogToFileImpl extends LogToStreamsImpl implements LogToFile {

   //Configuration parameters

    /** Log file. */
    File logFile;

    StringBuffer fullLogFileName = new StringBuffer();

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

    /** Use Log Name in file name */
    boolean useLogNameInFileName = true;

    /** Use HostName in file name */
    boolean useHostNameInFileName = true;

    /** Use ProcessName in file name */
    boolean useProcessNameInFileName = true;

    /** Redirect system.out and system.err */
    boolean redirectSystemOutputs = false;




    /**
     * Construct a simple log with given name and log level
     * and log to output level
     * @param name log name
     * @param initialLogLevel level to log at
     */
    public LogToFileImpl (String name, Integer initialLogLevel) {
        super(name,initialLogLevel);
        try {
          readSFFileAttributes();
        } catch (SmartFrogException ex1) {
           this.error("",ex1);
        }
//        assert name != null;
//        logName = name;
//        // Set initial log level
//        setLevel(initialLogLevel.intValue());

//        //Check Class and read configuration...including system.properties
//        try {
//          classComponentDescription = LogImpl.getClassComponentDescription(this, true);
//        } catch (SmartFrogException ex) {
//           this.warn(ex.toString());
//        }
        PrintStream out=null;
        try {
           logFile = createFile(logFileExtension);
           FileOutputStream fos = new FileOutputStream(logFile);
           out = new PrintStream(fos);
        } catch (Exception ex){
          //@todo
          ex.printStackTrace();
        }
        if (isTraceEnabled() && this.getClass().toString().endsWith("LogToFileImpl")) {
            //This will go to the std output.
            trace("LogToFileImpl using file name: "+logFile.getAbsolutePath());
        }
        setOutstream(out);
        if (redirectSystemOutputs){
            try {
                redirectOutputs();
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
        }
        if (isTraceEnabled() && this.getClass().toString().endsWith("LogToFileImpl")) {
            trace(this.getClass().toString()+" '"+name+"' using ComponentDescription:\n"+classComponentDescription.toString());
        }
//        setLevel(initialLogLevel.intValue());
    }


    /**
     *  Reads optional and mandatory attributes.
     *
     * @exception  SmartFrogException error while reading attributes
     */
    protected void readSFFileAttributes() throws SmartFrogException {
        if (classComponentDescription==null) return;
        //Optional attributes.
        try {
          path = classComponentDescription.sfResolve(ATR_PATH,path, false);
          logFileExtension =classComponentDescription.sfResolve(ATR_LOG_FILE_EXTENSION,logFileExtension, false);
          datedName =classComponentDescription.sfResolve(ATR_USE_DATED_FILE_NAME,datedName, false);
          redirectSystemOutputs = classComponentDescription.sfResolve(ATR_REDIRECT_SYSTEM_OUTPUTS,redirectSystemOutputs, false);
          fileNamePrefix = classComponentDescription.sfResolve(ATR_FILE_NAME_PREFIX,fileNamePrefix, false);
          useLogNameInFileName = classComponentDescription.sfResolve(ATR_USE_LOG_NAME_IN_FILE_NAME,useLogNameInFileName, false);
          useHostNameInFileName = classComponentDescription.sfResolve(ATR_USE_HOST_NAME_IN_FILE_NAME,useHostNameInFileName, false);
          useProcessNameInFileName = classComponentDescription.sfResolve(ATR_USE_PROCESS_NAME_IN_FILE_NAME,useProcessNameInFileName, false);
        } catch (Exception ex){
           this.warn("",ex);;
        }
    }


    /**
     * Creates the file using attributes.
     *
     * @return filename
     * @throws Exception error while creating file
     */
    public File createFile(String fileExtension) throws Exception {

        // check the path ends correctly
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }

        fullLogFileName.append(path);
        StringBuffer newfileName=new StringBuffer();

        if ((fileNamePrefix!=null)){
            if ((newfileName.toString().length()>0)&&!(newfileName.toString().endsWith("_"))) {
                            newfileName.append("_");
            }
          newfileName.append(correctFilename(fileNamePrefix));
        }

        if (useHostNameInFileName) {
            try {
                String hostname = java.net.InetAddress.getLocalHost().getCanonicalHostName();
                if ((newfileName.toString().length()>0)&&!(newfileName.toString().endsWith("_"))) {
                                newfileName.append("_");
                }
                newfileName.append(correctFilename(hostname));
            } catch (UnknownHostException ex) {
                if (isErrorEnabled()) error("",ex);
            }
        }

        if (useProcessNameInFileName) {
            String processName = System.getProperty(SmartFrogCoreProperty.propBaseSFProcess +SmartFrogCoreKeys.SF_PROCESS_NAME);
            if (processName!=null) {
                if ((newfileName.toString().length()>0) && !(newfileName.toString().endsWith("_"))) {
                    newfileName.append("_");
                }
                newfileName.append(correctFilename(processName));
            }
        }

        if (useLogNameInFileName) {
            if ((newfileName.toString().length()>0)&&!(newfileName.toString().endsWith("_"))) {
                            newfileName.append("_");
            }
           newfileName.append(correctFilename(logName));
        }

        if (datedName){
            /** Used to format times in filename */
            DateFormat dateFileNameFormatter = null;
            dateFileNameFormatter = new SimpleDateFormat("yyyyMMdd-HHmmss_SSSzzz");
            if ((newfileName.toString().length()>0)&&!(newfileName.toString().endsWith("_"))) {
                            newfileName.append("_");
            }
            // add the extension
            newfileName.append(dateFileNameFormatter.format(new Date()));
        }

        fullLogFileName.append (newfileName);
        // add the extension
        //System.out.println("**********LogToFileImpl::createFile : "+fullLogFileName.toString()+"."+fileExtension);
        return new File( fullLogFileName.toString()+"."+fileExtension);
    }

    private String correctFilename(String filename) {
        final int length = filename.length();
        StringBuffer buffer=new StringBuffer(length);
        for(int i=0;i<length;i++) {
            char c= filename.charAt(i);
            switch(c) {
                case ':':
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
    public void redirectOutputs() throws Exception {
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
                if (errToOut)
                   System.setErr(newOut);
                else
                   System.setErr(newErr);
            } catch (Exception e) {
                System.setOut(originalOut);
                System.setErr(originalErr);
            }
        }
    }



}
