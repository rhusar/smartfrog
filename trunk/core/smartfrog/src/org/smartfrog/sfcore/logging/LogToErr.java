/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
  Cut version of SimpleLog: Using this one for intial tests!
*/

package org.smartfrog.sfcore.logging;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.PrintStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 */
public interface LogToErr {

//  //Configuration parameters
//  /** String name for optional attribute "path". */
//   final static String ATR_PATH = "path";
//   /** String name for optional attribute "logFileExtension". */
//   final static String ATR_LOG_FILE_EXTENSION = "logFileExtension";
//   /** String name for optional attribute "useDatedFileName". */
//   final static String ATR_USE_DATED_FILE_NAME = "useDatedFileName";
//   /** String name for optional attribute "useDatedFileName". */
//   final static String ATR_REDIRECT_SYSTEM_OUTPUTS = "redirectSystemOutputs";

//  /** Include the instance name in the log message? */
//  protected static boolean showLogName = true;
//
//  /** Include the short name ( last component ) of the logger in the log
//      message. Default to true - otherwise we'll be lost in a flood of
//      messages without knowing who sends them.
//  */
//  protected boolean showShortName = false;
//
//  /** Include the current time in the log message */
//  protected static boolean showDateTime = true;
//
//  /** Include thread name in the log message */
//  protected boolean showThreadName = true;
//
//  /** Include package name in the log message */
//  protected boolean showMethodCall = true;
//
//  /** Include package name in the log message */
//  protected boolean showStackTrace =true;
//
//  /** Used to format times */
//  protected static DateFormat dateFormatter = null;

  /** "Trace" level logging. */
  public static final int LOG_LEVEL_TRACE  = 1;
  /** "Debug" level logging. */
  public static final int LOG_LEVEL_DEBUG  = 2;
  /** "Info" level logging. */
  public static final int LOG_LEVEL_INFO   = 3;
  /** "Warn" level logging. */
  public static final int LOG_LEVEL_WARN   = 4;
  /** "Error" level logging. */
  public static final int LOG_LEVEL_ERROR  = 5;
  /** "Fatal" level logging. */
  public static final int LOG_LEVEL_FATAL  = 6;

  /** Enable all logging levels */
  public static final int LOG_LEVEL_ALL    = (LOG_LEVEL_TRACE - 1);

  /** Enable no logging levels */
  public static final int LOG_LEVEL_OFF    = (LOG_LEVEL_FATAL + 1);

}

