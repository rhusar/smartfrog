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
    /** String name for optional attribute "useLogNameInFileName". */
    final static String ATR_USE_LOG_NAME_IN_FILE_NAME = "useLogNameInFileName";
    /** String name for optional attribute "useHostNameInFileName". */
    final static String ATR_USE_HOST_NAME_IN_FILE_NAME = "useHostNameInFileName";
    /** String name for optional attribute "fileNamePrefix". */
    final static String ATR_FILE_NAME_PREFIX = "fileNamePrefix";
    /** String name for optional attribute "useDatedFileName". */
    final static String ATR_REDIRECT_SYSTEM_OUTPUTS = "redirectSystemOutputs";


}