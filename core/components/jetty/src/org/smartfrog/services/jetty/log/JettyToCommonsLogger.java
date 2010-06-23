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

// ========================================================================
// Copyright 2004-2005 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ========================================================================


package org.smartfrog.services.jetty.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.log.Logger;

/**
 * this is a jetty to commons logging logger, the {@link #printf(String, Object, Object)} method comes from {@link
 * org.mortbay.log.StdErrLog}; the rest handwritten.
 */

public class JettyToCommonsLogger implements Logger {

    /**
     * The inner log
     */
    private final Log log;

    /**
     * Default log name if none is passed in: {@value}
     */
    public static final String LOG_NAME = "org.mortbay.log.jetty";

    /**
     * Error string printed if someone tries to change the debug settings: {@value}
     */
    public static final String WARN_NOT_IMPLEMENTED = "Not implemented: setDebugEnabled";

    /**
     *
     */
    public JettyToCommonsLogger() {
        this(LOG_NAME);
    }

    /**
     * Create a log instance from a given name
     *
     * @param name
     */
    public JettyToCommonsLogger(String name) {
        log = LogFactory.getLog(name);
    }

    /**
     * Delegate query to the inner log
     *
     * @return the inner log's state
     */
    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    /**
     * Unimplemented method to turn debugging on programmatically. If the setting does not match the current value you
     * get warned that it doesn't work.
     */
    @Override
    public void setDebugEnabled(boolean newValue) {
        if (log.isDebugEnabled() != newValue) {
            warn(WARN_NOT_IMPLEMENTED, null, null);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param msg {@inheritDoc}
     * @param th  {@inheritDoc}
     */
    @Override
    public void debug(String msg, Throwable th) {
        log.debug(msg, th);
    }

    /**
     * {@inheritDoc}
     *
     * @param msg  {@inheritDoc}
     * @param arg0 {@inheritDoc}
     * @param arg1 {@inheritDoc}
     */
    @Override
    public void debug(String msg, Object arg0, Object arg1) {
        if (log.isDebugEnabled()) {
            log.debug(printf(msg, arg0, arg1));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param msg  {@inheritDoc}
     * @param arg0 {@inheritDoc}
     * @param arg1 {@inheritDoc}
     */
    @Override
    public void info(String msg, Object arg0, Object arg1) {
        if (log.isInfoEnabled()) {
            log.info(printf(msg, arg0, arg1));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param msg  {@inheritDoc}
     * @param arg0 {@inheritDoc}
     * @param arg1 {@inheritDoc}
     */
    @Override
    public void warn(String msg, Object arg0, Object arg1) {
        if (log.isWarnEnabled()) {
            log.warn(printf(msg, arg0, arg1));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param msg {@inheritDoc}
     * @param th  {@inheritDoc}
     */
    @Override
    public void warn(String msg, Throwable th) {
        log.warn(msg, th);
    }

    /**
     * Create a new jetty logger bound to the specific name.
     *
     * <p/>
     *
     * The name will be looked up in the core log registry; even if the name matches an
     * existing instance, the logger witll be different. 
     *
     * @param name name to look up
     * @return the logger
     */
    @Override
    public Logger getLogger(String name) {
        return new JettyToCommonsLogger(name);
    }

    /**
     * Print something with up to two arguments inserted. The code is from Jetty's {@link org.mortbay.log.StdErrLog}
     * class
     *
     * @param message text message
     * @param arg0    first (optional) argument
     * @param arg1    second (optional) argument
     * @return a formatted string
     */
    protected static String printf(String message, Object arg0, Object arg1) {
        int i0 = message.indexOf("{}");
        int i1 = i0 < 0 ? -1 : message.indexOf("{}", i0 + 2);
        String result = message;


        if (arg1 != null && i1 >= 0) {
            result = result.substring(0, i1) + arg1 + result.substring(i1 + 2);
        }
        if (arg0 != null && i0 >= 0) {
            result = result.substring(0, i0) + arg0 + result.substring(i0 + 2);
        }
        return result;
    }
}