package org.smartfrog.sfcore.logging;

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

public interface LogToFile extends LogToErr {

   //Configuration parameters
   /** String name for optional attribute "path". */
    final static String ATR_PATH = "path";
    /** String name for optional attribute "logFileExtension". */
    final static String ATR_LOG_FILE_EXTENSION = "logFileExtension";
    /** String name for optional attribute "useDatedFileName". */
    final static String ATR_USE_DATED_FILE_NAME = "useDatedFileName";
    /** String name for optional attribute "useDatedFileName". */
    final static String ATR_REDIRECT_SYSTEM_OUTPUTS = "redirectSystemOutputs";

}