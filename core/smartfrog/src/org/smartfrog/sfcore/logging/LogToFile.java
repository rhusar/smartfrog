package org.smartfrog.sfcore.logging;

import java.io.PrintStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 *
 */

public class LogToFile extends LogToErr {

    /** Log file. */
    File logFile;
    StringBuffer fullLogFileName = new StringBuffer();

    /** String name for path. */
    String path="";

    String logFileExtension = "log";

    /** Use date in file name */
    boolean datedName = true;

    /** Redirect system.out and system.err */
    boolean redirectSystemOutputs = true;




    /**
     * Construct a simple log with given name and log level
     * and log to output level
     * @param name log name
     * @param intialLogLevel level to log at
     */
    public LogToFile(String name, int initialLogLevel) {
        //super();
        assert name != null;
        assert initialLogLevel >= 0;
        logName = name;
        // Set initial log level
        setLevel(initialLogLevel);

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
        fullLogFileName.append(logName.replace(':','_'));

        if (datedName){
            /** Used to format times in filename */
            DateFormat dateFileNameFormatter = null;
            dateFileNameFormatter = new SimpleDateFormat("-yyyy_MM_dd-HH_mm_ss_SSSzzz");
            // add the extension
            fullLogFileName.append(dateFileNameFormatter.format(new Date()));
        }
        // add the extension

        return new File(fullLogFileName.toString()+"."+fileExtension);
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