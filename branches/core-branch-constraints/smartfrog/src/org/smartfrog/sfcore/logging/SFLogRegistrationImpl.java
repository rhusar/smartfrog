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

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogLogException;

/**
 * This class has to be run through RMIC compiler (
 */
public class SFLogRegistrationImpl extends PrimImpl implements Prim, SFLogRegistration, LogRegistration {

    LogRegistration logreg = null;

    /**
     */
    public SFLogRegistrationImpl() throws RemoteException {
    }

    // LifeCycle methods

    /**
     *
     * @exception  SmartFrogException In case of error in deploying
     * @exception  RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
       super.sfDeploy();
       readConfiguration();
    }

    /**
     *
     * @exception  SmartFrogException In case of error while starting
     * @exception  RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
    }

    /**
     * @param  t TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord t) {
        if (sfLog().isDebugEnabled()) sfLog().debug(" Terminating for reason: " + t.toString());
        super.sfTerminateWith(t);
    }

    // End LifeCycle methods

    /**
     *  Reads configuration
     *
     *  Read attribute log to bind to a particular log, if no configuration is
     * provided, then it binds to the component log.
     *
     * @exception  SmartFrogException error while reading attributes
     * @exception  RemoteException In case of network/rmi error
     */
    protected void readConfiguration() throws SmartFrogException, RemoteException {
        try {
            String logname = sfResolve(ATR_LOG, "", false);
            if (!logname.equals("")){
                LogSF logsf = LogFactory.getLog(logname);
                if (logsf instanceof LogRegistration) {
                    logreg = (LogRegistration) logsf;
                }
                if (logreg == null) {
                    throw new SmartFrogResolutionException("'log' not valid for " + logname + " got " + logsf + "[" + logsf.getClass().getName() + "]");
                }
            } else {
                logreg = (LogRegistration) sfLog();
            }
        } catch (SmartFrogResolutionException e) {
          if (sfLog().isErrorEnabled()) sfLog().error(e);
            throw e;
        }

    }

    // Main component action methods

    /**
     * Log Registration interface
     *
     * @param name log name
     * @param log logger to register
     * @throws SmartFrogLogException  if failed to register
     * @throws RemoteException in case of remote/network error
     */
    public void register(String name,Log log)  throws SmartFrogLogException , RemoteException{
        if (logreg!=null) {
            logreg.register(name,log);
        }
    }

   /**
     *  Log Registration interface
     * @param name log name
     * @param log logger to register
     * @param logLevel  log level
     * @throws RemoteException in case of remote/network error
     * @throws SmartFrogLogException if failed to register
     */
   public void register(String name,Log log, int logLevel)  throws RemoteException, SmartFrogLogException{
       if (logreg!=null) {
           logreg.register(name,log,logLevel);
       }
   }

    /**
     *  Log Deregistration interface
     * @param name log name
     * @return  boolean success/failure
     * @throws SmartFrogLogException if failed to deregister
     * @throws RemoteException in case of remote/network error
     */
    public boolean deregister(String name)  throws SmartFrogLogException, RemoteException {
        if (logreg!=null) {
           return logreg.deregister(name);
        }
        return false;
    }

    /**
     * Get a list of all registered logs
     *
     * @return a list (may be of size 0 for no logs)
     * @throws java.rmi.RemoteException
     * @throws org.smartfrog.sfcore.common.SmartFrogLogException
     *
     */
    public Log[] listRegisteredLogs() throws RemoteException, SmartFrogLogException {
        if (logreg != null) {
            return logreg.listRegisteredLogs();
        }
        return new Log[0];
    }
}
