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

import java.io.PrintStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 *  Logs log info into a file.
 *
 */

public class LogToFileImpl extends LogToErrImpl implements LogToFile {

   //Configuration parameters

    /** Log file. */
    File logFile;

    StringBuffer fullLogFileName = new StringBuffer();

    /** String name for path. */
    String path=".";//+File.separator+"log";

    String logFileExtension = "log";

    /** Use date in file name */
    boolean datedName = true;

    /** Redirect system.out and system.err */
    boolean redirectSystemOutputs = false;




    /**
     * Construct a simple log with given name and log level
     * and log to output level
     * @param name log name
     * @param intialLogLevel level to log at
     */
    public LogToFileImpl (String name, Integer initialLogLevel) {
        super(name,initialLogLevel);
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
//        try {
//          readSFAttributes();
//        } catch (SmartFrogException ex1) {
//           this.error("",ex1);
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
        setOutstream(out);
        if (redirectSystemOutputs){
            try {
                redirectOutputs();
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
        }
//        setLevel(initialLogLevel.intValue());
    }


    /**
     *  Reads optional and mandatory attributes.
     *
     * @exception  SmartFrogException error while reading attributes
     * @exception  RemoteException In case of network/rmi error
     */
    protected void readSFAttributes() throws SmartFrogException {
        super.readSFAttributes();
        if (classComponentDescription==null) return;
        //Optional attributes.
        try {
          path = classComponentDescription.sfResolve(ATR_PATH,path, false);
          logFileExtension =classComponentDescription.sfResolve(ATR_LOG_FILE_EXTENSION,logFileExtension, false);
          datedName =classComponentDescription.sfResolve(ATR_USE_DATED_FILE_NAME,datedName, false);
          redirectSystemOutputs = classComponentDescription.sfResolve(ATR_REDIRECT_SYSTEM_OUTPUTS,redirectSystemOutputs, false);
        } catch (Exception sex){
           this.warn("",sex);;
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

        String fixedName = correctFilename(logName);

        fullLogFileName.append(fixedName);

        if (datedName){
            /** Used to format times in filename */
            DateFormat dateFileNameFormatter = null;
            dateFileNameFormatter = new SimpleDateFormat("_yyyyMMdd-HHmmss_SSSzzz");
            // add the extension
            fullLogFileName.append(dateFileNameFormatter.format(new Date()));
        }
        // add the extension
        return new File(fullLogFileName.toString()+"."+fileExtension);
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
        //   InputStream din = System.in;
        /** Output stream. */
        PrintStream originalSysOut = System.out;
        /** Error stream. */
        PrintStream originalSysErr = System.err;

        /** Direct brand new outputs to a file. */
        PrintStream newOut = null;
        /** Direct brand new outputs to a file. */
        PrintStream newErr = null;

        /** Log OUT file. */
        File logOutFile= new File(fullLogFileName.toString()+".out");
        /** Log ERR file. */
        File logErrFile= new File(fullLogFileName.toString()+".err");

        if (logFile != null) {
            FileOutputStream fos = new FileOutputStream(logOutFile);
            newOut = new PrintStream(fos);

            FileOutputStream fes = new FileOutputStream(logErrFile);
            newErr = new PrintStream(fes);

            try {
                // Redirecting standard output
                System.setOut(newOut);
                // Redirecting standard err
                System.setErr(newErr);

            } catch (Exception e) {
                System.setOut(originalSysOut);
                System.setErr(originalSysErr);
                e.printStackTrace();
            }
        }
    }



}