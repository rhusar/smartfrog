/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.smartfrog.services.jetty.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.log.Logger;

/**
 * this is a jetty to commons logging logger,
 */

public class JettyToCommonsLogger implements Logger {

    private final Log log;
    public static final String LOG_NAME = "org.mortbay.log.jetty";
    public static final String WARN_NOT_IMPLEMENTED = "Not implemented: setDebugEnabled";

    public JettyToCommonsLogger() {
        this(LOG_NAME);
    }

    public JettyToCommonsLogger(String name) {
        log = LogFactory.getLog(name);
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    /**
     * Unimplemented method to turn debugging on programmatically. If the setting does not
     */
    @Override
    public void setDebugEnabled(boolean enabled) {
        if (log.isDebugEnabled() != enabled) {
            warn(WARN_NOT_IMPLEMENTED, null, null);
        }
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
     * Create a new jetty logger bound to the specific name. <p/> The name will be looked up in the core log registry
     *
     * @param name name to look up
     * @return the logger
     */
    @Override
    public Logger getLogger(String name) {
        return new JettyToCommonsLogger(name);
    }


}