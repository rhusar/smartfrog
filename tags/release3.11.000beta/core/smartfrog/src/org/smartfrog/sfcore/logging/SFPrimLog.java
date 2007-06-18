/** (C) Copyright Hewlett-Packard Development Company, LP

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
 * Defines the attributes for SFLog component.
 */
public interface SFPrimLog extends LogRemote {
    /** String name for attribute. Value {@value}. */
    final static String ATR_LOG_NAME = "logName";

    /** String name for attribute. Value {@value}. */
    final static String ATR_LOG_LEVEL = "logLevel";

    /** String name for attribute. Value {@value}. */
    final static String ATR_LOG_TO = "logTo";

    /** String name for attribute. Value {@value}. */
    final static String ATR_LOG_ASYNCH = "logAsynch";

    /** String name for attribute. Value {@value}. */
    final static String ATR_LOGGER_CLASS = "loggerClass"; //Object implementing Log interface.

    /** String name for attribute. Used to actively register with a Log. Value {@value}. */
    final static String ATR_LOG_FROM = "logFrom";
}
