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

package org.smartfrog.sfcore.common;

//import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.SFSystem;

/**
 * Class used to store some flags used for log reporting.
 */
public class Logger implements MessageKeys {

//   /** String name for caller. */
//    public static final String CALLER = "caller";

    /** Property to enable stack trace. The default value is overridden by the
     * value specified in default.ini file.
     */
    public static boolean logStackTrace = true;

    /** Property to enable sfPing log. The default value is overridden by the
     * value specified in default.ini file.
     */
    public static boolean logLiveness = false;

    /** Property to create a sfDiagnosticsReport in every ProcessCompound
     */
    public static boolean processCompoundDiagReport = false;

    private static boolean initialized = false;

    private Logger(){
    }

    public static synchronized void  init() {
        if (initialized) return;
        /**
         * Reads System property "org.smartfrog.logger.logStrackTrace" and
         * updates Logger with the value to enable stack tracing.
         */
        String source = System.getProperty(SmartFrogCoreProperty.propLogStackTrace);
        if ("true".equals(source)) {
            Logger.logStackTrace = true;
            if (SFSystem.sfLog().isWarnEnabled()) {
              SFSystem.sfLog().warn(MessageUtil.
                    formatMessage(MSG_WARNING_STACKTRACE_ENABLED));
            }
        }
        /**
         * Reads System property "org.smartfrog.logger.logLiveness" and
         * updates Logger with the value to enable sfPing tracing.
         */
        source="false";
        source = System.getProperty(SmartFrogCoreProperty.propLogLiveness);
        if ("true".equals(source)) {
            Logger.logLiveness = true;
            if (SFSystem.sfLog().isWarnEnabled()) {
              SFSystem.sfLog().warn(MessageUtil.
                    formatMessage(MSG_WARNING_LIVENESS_ENABLED));
            }
        }

        /**
         * Reads System property "org.smartfrog.logger.processCompoundDiagnosticsReport" and
         * updates Logger with the value to create diagnostics report in
         * every ProcessCompound.
         */
        source="false";
        source = System.getProperty(SmartFrogCoreProperty.processCompoundDiagnosticsReport);
        if ("true".equals(source)) {
            Logger.processCompoundDiagReport = true;
        }

        initialized = true;
    }

}
