/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.security.SFGeneralSecurityException;
import org.smartfrog.sfcore.security.SecureRemoteObject;

import java.rmi.RemoteException;

/**
 * This bridges from a local log to a remote one, allowing logging over RMI
 */

public class LogRemoteImpl implements LogRemote {

    private Log log;

    /**
     * Create a log that is exported for RMI use
     * @param localLog the local log to relay to
     * @return a remote instance
     * @throws RemoteException problems exporting the object
     * @throws SFGeneralSecurityException security problems
     */
    public static LogRemote createExportedLog(Log localLog)
            throws RemoteException, SFGeneralSecurityException {
        LogRemoteImpl instance = new LogRemoteImpl(localLog);
        return (LogRemote) SecureRemoteObject.exportObject(instance, 0);
    }

    /**
     * Create a non-exported instance
     * @param log the local log to relay to
     */
    public LogRemoteImpl(Log log) {
        this.log = log;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDebugEnabled() throws RemoteException {
        return log.isDebugEnabled();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isErrorEnabled() throws RemoteException {
        return log.isErrorEnabled();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFatalEnabled() throws RemoteException {
        return log.isFatalEnabled();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInfoEnabled() throws RemoteException {
        return log.isInfoEnabled();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTraceEnabled() throws RemoteException {
        return log.isTraceEnabled();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isWarnEnabled() throws RemoteException {
        return log.isWarnEnabled();
    }

    /** {@inheritDoc} */
    @Override
    public void trace(Object message) throws RemoteException {
        log.trace(message);
    }

    /** {@inheritDoc} */
    @Override
    public void trace(Object message, Throwable t) throws RemoteException {
        log.trace(message, t);
    }

    /** {@inheritDoc} */
    @Override
    public void debug(Object message) throws RemoteException {
        log.debug(message);
    }

    /** {@inheritDoc} */
    @Override
    public void debug(Object message, Throwable t) throws RemoteException {
        log.debug(message, t);
    }

    /** {@inheritDoc} */
    @Override
    public void info(Object message) throws RemoteException {
        log.info(message);
    }

    /** {@inheritDoc} */
    @Override
    public void info(Object message, Throwable t) throws RemoteException {
        log.info(message, t);
    }

    /** {@inheritDoc} */
    @Override
    public void warn(Object message) throws RemoteException {
        log.warn(message);
    }

    /** {@inheritDoc} */
    @Override
    public void warn(Object message, Throwable t) throws RemoteException {
        log.warn(message, t);
    }

    /** {@inheritDoc} */
    @Override
    public void error(Object message) throws RemoteException {
        log.error(message);
    }

    /** {@inheritDoc} */
    @Override
    public void error(Object message, Throwable t) throws RemoteException {
        log.error(message, t);
    }

    /** {@inheritDoc} */
    @Override
    public void fatal(Object message) throws RemoteException {
        log.fatal(message);
    }

    /** {@inheritDoc} */
    @Override
    public void fatal(Object message, Throwable t) throws RemoteException {
        log.fatal(message, t);
    }
}
