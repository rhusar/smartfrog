/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.jetty.log;

import org.mortbay.log.Logger;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.common.SmartFrogLogException;

/**
 *
 * Created 17-Oct-2007 11:55:48
 *
 */

public class JettyLogger implements Logger {

    private LogSF log;
    public static final String LOG_NAME = "org.smartfrog.services.jetty";

    public JettyLogger() {
        this(LOG_NAME);
    }

    public JettyLogger(String name) {
        try {
            log = LogFactory.getLog(name, true);
        } catch (SmartFrogLogException e) {
            //registration failed, fall back
            log = LogFactory.getLog(name);
        }
    }
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    /**
     * Mutator used to turn debug on programatically.
     */
    public void setDebugEnabled(boolean enabled) {
        log.setLevel(enabled? LogSF.LOG_LEVEL_DEBUG: LogSF.LOG_LEVEL_INFO);
    }

    public void info(String msg, Object arg0, Object arg1) {
        if(log.isInfoEnabled()) {
            log.info(printf(msg,arg0,arg1));
        }
    }

    public void debug(String msg, Throwable th) {
        log.debug(msg, th);
    }

    public void debug(String msg, Object arg0, Object arg1) {
        if (log.isDebugEnabled()) {
            log.debug(printf(msg, arg0, arg1));
        }
    }

    public void warn(String msg, Object arg0, Object arg1) {
        if (log.isWarnEnabled()) {
            log.warn(printf(msg, arg0, arg1));
        }
    }

    public void warn(String msg, Throwable th) {
        log.warn(msg,th);
    }

    /**
     * Create a new jetty logger bound to the specific name.
     * <p/>
     * The name will be looked up in the core log registry
     * @param name name to look up
     * @return the logger
     */
    public Logger getLogger(String name) {
        return new JettyLogger(name);
    }


    /**
     * This is from Jetty's StdErrLog class
     * @param message text message
     * @param arg0 first (optional) argument
     * @param arg1 second (optional) argument
     * @return a formated string
     */
    private String printf(String message, Object arg0, Object arg1) {
        int i0 = message.indexOf("{}");
        int i1 = i0 < 0 ? -1 : message.indexOf("{}", i0 + 2);
        String result = message;


        if (arg1 != null && i1 >= 0)
            result = result.substring(0, i1) + arg1 + result.substring(i1 + 2);
        if (arg0 != null && i0 >= 0)
            result = result.substring(0, i0) + arg0 + result.substring(i0 + 2);
        return result;
    }
}
