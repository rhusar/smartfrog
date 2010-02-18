/** (C) Copyright 1998-2006 Hewlett-Packard Development Company, LP

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


/**
 */
public interface LogToStreams extends LogToNothing {

   /** Include the instance name in the log message? */
    final static String ATR_SHOW_LOG_NAME = "showLogName";

  /** Include the short name ( last component ) of the logger in the log
      message. Default to true - otherwise we'll be lost in a flood of
      messages without knowing who sends them.
  */
  /** String name for optional attribute "path". */
   final static String ATR_SHOW_SHORT_NAME = "showShortName";
  /** Include the current time in the log message */
  final static String ATR_SHOW_DATE_TIME = "showDateTime";

  /** Include thread name in the log message */
  final static String ATR_SHOW_THREAD_NAME = "showThreadName";


  /** Include method call in the log message */
  final static String ATR_SHOW_METHOD_CALL = "showMethodCall";

  /** Include stack trace in log message */
  final static String ATR_SHOW_STACK_TRACE = "showStackTrace";

  /** Used to format times */
  final static String ATR_DATE_FORMAT = "dateFormat";

}

