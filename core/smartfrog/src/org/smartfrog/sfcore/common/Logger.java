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
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

/**
 * Class used to store some flags used for log reporting.
 */
public class Logger implements MessageKeys {

    /** Property name for Logger class
     *  Value {@value}
     *  */
    public static final String loggerPropertyBase = "org.smartfrog.sfcore.common.Logger";

    //Configuration parameters
    /** String name for optional attribute "{@value}". */
    public final static String ATR_LOG_STACK_TRACE = "logStackTrace";
    /** String name for optional attribute "{@value}". */
    public final static String ATR_LOG_LIVENESS = "logLiveness";
    /** String name for optional attribute "{@value}". */
    public final static String ATR_LOG_PC_DIAG_REPORT = "processCompoundDiagReport";

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
        ComponentDescription configuration = null;
        //Check Class and read configuration...including system.properties
        try {
            configuration = ComponentDescriptionImpl.getClassComponentDescription(loggerPropertyBase, true, null);
            if (configuration!=null){
               logStackTrace = configuration.sfResolve(ATR_LOG_STACK_TRACE,logStackTrace,false);
               logLiveness = configuration.sfResolve(ATR_LOG_LIVENESS,logLiveness,false);
               processCompoundDiagReport = configuration.sfResolve(ATR_LOG_PC_DIAG_REPORT,processCompoundDiagReport,false);
            }
        } catch (Exception ex){
            if (SFSystem.sfLog().isErrorEnabled()) { SFSystem.sfLog().error(ex); }
        }

        if (logStackTrace&&(SFSystem.sfLog().isWarnEnabled())) {
              SFSystem.sfLog().warn(MessageUtil.formatMessage(MSG_WARNING_STACKTRACE_ENABLED));
        }

        if (logLiveness && (SFSystem.sfLog().isWarnEnabled())) {
          SFSystem.sfLog().warn(MessageUtil.formatMessage(MSG_WARNING_LIVENESS_ENABLED));
        }

        initialized = true;
    }

}
