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


package org.smartfrog.sfcore.logging;


import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * A simple logging interface abstracting logging APIs based in Apache Jakarta
 * logging.
 *
 */
public interface LogRemote extends Remote {


    // ----------------------------------------------------- Logging Properties

    /**
     * <p> Is debug logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than debug. </p>
     * @return boolean true if debug level is currently enabled
     * @throws RemoteException in case of remote/network error
     */
    public boolean isDebugEnabled() throws RemoteException;


    /**
     * <p> Is error logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than error. </p>
     * @return boolean true if error level is currently enabled
     * @throws RemoteException in case of remote/network error
     */
    public boolean isErrorEnabled() throws RemoteException;


    /**
     * <p> Is fatal logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than fatal. </p>
     * @return boolean true if fatal level is currently enabled
     * @throws RemoteException in case of remote/network error
     */
    public boolean isFatalEnabled() throws RemoteException;


    /**
     * <p> Is info logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than info. </p>
     * @return boolean true if info level is currently enabled
     * @throws RemoteException in case of remote/network error
     */
    public boolean isInfoEnabled() throws RemoteException;


    /**
     * <p> Is trace logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than trace. </p>
     * @return boolean true if trace level is currently enabled
     * @throws RemoteException in case of remote/network error
     */
    public boolean isTraceEnabled() throws RemoteException;


    /**
     * <p> Is warning logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than warn. </p>
     * @return boolean true if warn level is currently enabled
     * @throws RemoteException in case of remote/network error
     */
    public boolean isWarnEnabled() throws RemoteException;


    // -------------------------------------------------------- Logging Methods

    /**
     * <p> Log a message with trace log level. </p>
     *
     * @param message log this message
     * @throws RemoteException in case of remote/network error
     */
    public void trace(Object message) throws RemoteException;


    /**
     * <p> Log an error with trace log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @throws RemoteException in case of remote/network error
     */
    public void trace(Object message, Throwable t) throws RemoteException;


    /**
     * <p> Log a message with debug log level. </p>
     *
     * @param message log this message
     * @throws RemoteException in case of remote/network error
     */
    public void debug(Object message) throws RemoteException;


    /**
     * <p> Log an error with debug log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @throws RemoteException in case of remote/network error
     */
    public void debug(Object message, Throwable t) throws RemoteException;


    /**
     * <p> Log a message with info log level. </p>
     *
     * @param message log this message
     * @throws RemoteException in case of remote/network error
     */
    public void info(Object message) throws RemoteException;


    /**
     * <p> Log an error with info log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @throws RemoteException in case of remote/network error
     */
    public void info(Object message, Throwable t) throws RemoteException;


    /**
     * <p> Log a message with warn log level. </p>
     *
     * @param message log this message
     * @throws RemoteException in case of remote/network error
     */
    public void warn(Object message) throws RemoteException;


    /**
     * <p> Log an error with warn log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @throws RemoteException in case of remote/network error
     */
    public void warn(Object message, Throwable t) throws RemoteException;


    /**
     * <p> Log a message with error log level. </p>
     *
     * @param message log this message
     * @throws RemoteException in case of remote/network error
     */
    public void error(Object message) throws RemoteException;


    /**
     * <p> Log an error with error log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @throws RemoteException in case of remote/network error
     */
    public void error(Object message, Throwable t) throws RemoteException;


    /**
     * <p> Log a message with fatal log level. </p>
     *
     * @param message log this message
     * @throws RemoteException in case of remote/network error
     */
    public void fatal(Object message) throws RemoteException;


    /**
     * <p> Log an error with fatal log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @throws RemoteException in case of remote/network error
     */
    public void fatal(Object message, Throwable t) throws RemoteException;


}
