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

package org.smartfrog.services.logger;


import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.File;
import java.rmi.RemoteException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Default implementation of distributed logger. This logger is deployed as
 * a smartfrog component and uses RMI for distributed logging. It uses logging
 * APIs provided by JDK.
 * @Author Ashish Awasthi
 */
public class SFLoggerImpl extends PrimImpl implements Prim, SFLogger {
    //default values of the attributes
    private String logFile = "smartfrog.log";
    private String logsDir = "logs";
    private Handler fileHandler = null;
    private Logger logger = null;
    private boolean logToConsole = true;
    private boolean logToFile = true;
    private String loggerName = "default";
    private String logFormatter = "SimpleFormatter";


    /**
     * Constructs SmartFrog logger object.
     *
     * @throws RemoteException in case of network/rmi error
     */
    public SFLoggerImpl() throws RemoteException {
    }
    /** SmartFrog Components Life Cycle Methods Start */
    /**
     * Deploys the Logger component.
     *
     * @throws SmartFrogException in case of error in deploying
     * @throws RemoteException in case of network/emi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
                                                            RemoteException {
        super.sfDeploy();
        logger = Logger.getLogger(loggerName);

        //read logging handlers attributes
        logToConsole = sfResolve(CONSOLE_LOGGING, logToConsole, false);
        logToFile = sfResolve(FILE_LOGGING, logToFile, false);

        //add console handler
        if(logToConsole) {
            logger.addHandler(new ConsoleHandler());
        }
        // add file handler
        if (logToFile) {
            try {
                logsDir = sfResolve(LOG_DIR, logsDir, false);
                logFile = sfResolve (LOG_FILE, logFile, false);
                logFormatter = sfResolve (LOG_FORMATTER,
                                                logFormatter, false);
                File dir = new File(logsDir);
                dir.mkdir();
                fileHandler = new FileHandler(logsDir+File.separator+logFile);
                Class classFormatter = org.smartfrog.sfcore.security.SFClassLoader.forName(logFormatter);
                Formatter formatter = (Formatter) classFormatter.newInstance();
                fileHandler.setFormatter(formatter);
                logger.addHandler(fileHandler);
            }catch (Exception ex) {
                SmartFrogException.forward(ex);
            }
        }
    }

    /**
     * Closes all the handlers and terminates the logger component.
     *
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        // close all the handlers
        try {
            Handler[] handlers = logger.getHandlers();
            for (int i=0 ; i< handlers.length; i++) {
                handlers[i].close();
            }
        }catch (Exception ex) {
            //ignore
        }
        super.sfTerminateWith(tr);
    }
    /** SmartFrog Components Life Cycle Methods End */


    /** Logging Methods End */

    /**
     * Logs Info message.
     * @param msg the log message
     * @throws RemoteException if there is any network or RMI error
     */
    public void logInfo(String msg) throws RemoteException {
        logger.log(Level.INFO, msg);
    }
    /**
     * Logs Warning message.
     * @param msg the log message
     * @throws RemoteException if there is any network or RMI error
     */
    public void logWarning(String msg) throws RemoteException {
        logger.log(Level.WARNING, msg);
    }
    /**
     * Logs Error message.
     * @param msg the log message
     * @throws RemoteException if there is any network or RMI error
     */
    public void logError(String msg) throws RemoteException {
        logger.log(Level.SEVERE, msg);
    }
    /**
     * Logs a log reecord
     * @param logRec the log record
     * @throws RemoteException if there is any network or RMI error
     * @see java.util.logging.LogRecord
     */
    public void log(LogRecord logRec) throws RemoteException {
        logger.log(logRec);
    }

    /**
     * log at debug level
     *
     * @param msg
     * @throws java.rmi.RemoteException
     * @see #isDebug()
     */


    public void logDebug(String msg) throws RemoteException {
        logger.log(Level.FINE,msg);
    }

    /**
     * message which determines whether debug logging is enabled
     *
     * @return
     * @throws java.rmi.RemoteException
     */
    public boolean isDebug() throws RemoteException {
        return logger.isLoggable(Level.FINE);
    }
}
