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
import org.smartfrog.sfcore.common.SmartFrogLogException;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;

/**
 * Created 17-Oct-2007 11:55:48
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

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    /**
     * Mutator used to turn debug on programatically.
     */
    @Override
    public void setDebugEnabled(boolean enabled) {
        log.setLevel(enabled ? LogSF.LOG_LEVEL_DEBUG : LogSF.LOG_LEVEL_INFO);
    }

    @Override
    public void info(String msg, Object arg0, Object arg1) {
        if (log.isInfoEnabled()) {
            log.info(Printf.printf(msg, arg0, arg1));
        }
    }

    @Override
    public void debug(String msg, Throwable th) {
        log.debug(msg, th);
    }

    @Override
    public void debug(String msg, Object arg0, Object arg1) {
        if (log.isDebugEnabled()) {
            log.debug(Printf.printf(msg, arg0, arg1));
        }
    }

    @Override
    public void warn(String msg, Object arg0, Object arg1) {
        if (log.isWarnEnabled()) {
            log.warn(Printf.printf(msg, arg0, arg1));
        }
    }

    @Override
    public void warn(String msg, Throwable th) {
        log.warn(msg, th);
    }

    /**
     * Create a new jetty logger bound to the specific name.
     * <p/>
     * The name will be looked up in the core log registry
     *
     * @param name name to look up
     * @return the logger
     */
    @Override
    public Logger getLogger(String name) {
        return new JettyLogger(name);
    }


}
