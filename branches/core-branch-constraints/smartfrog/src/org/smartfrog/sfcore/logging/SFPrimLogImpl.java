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
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.*;

/**
 *  Implementation for SFLog component.
 */
public class SFPrimLogImpl extends PrimImpl implements Prim, SFPrimLog, Log {

    Log logTo = null;
    ComponentDescription logToCD =null;
    LogRegistration logFrom = null;

    String logName = null;
    int logLevel = 3; //Info level
    boolean logAsynch = true;// Default value

    /**
     *  Constructor for the SFLogImpl object.
     *
     *@throws  RemoteException In case of network/rmi error
     */
    public SFPrimLogImpl() throws RemoteException {
    }

    // LifeCycle methods

    /**
     *
     * @throws  SmartFrogException In case of error in deploying
     * @throws RemoteException In case of network/rmi error
      */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
       super.sfDeploy();
       readConfiguration();
       if (logFrom!=null) logFrom.register(logName,this);
      //if (sfLog().isInfoEnabled()) sfLog().info("end sfDeploy");
    }

    /**
     * @param  t TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord t) {
        if (sfLog().isDebugEnabled()) sfLog().debug(" Terminating for reason: " + t.toString());
        if (logFrom != null) {
            try {
                logFrom.deregister(logName);
            } catch (Exception ex) {
                if (sfLog().isErrorEnabled()) sfLog().error("Deregistration failed for: " + logName,ex);
            }
        }
        super.sfTerminateWith(t);
    }

    // End LifeCycle methods

    // Read Attributes from description
    /**
     *  Reads optional and mandatory attributes.
     *
     * @exception  SmartFrogException error while reading attributes
     * @exception  RemoteException In case of network/rmi error
     */
    protected void readConfiguration() throws SmartFrogException, RemoteException {
        logTo = sfLog(); //default logger
        Prim prim = null;
        logToCD = sfResolve(ATR_LOG_TO, logToCD , false);// Get a logger from a component. if it does not exists use sfLog()
        logName = sfResolve(ATR_LOG_NAME, sfCompleteName().toString() , false);
        logLevel = sfResolve (ATR_LOG_LEVEL, logLevel , false);
        logAsynch = sfResolve(ATR_LOG_ASYNCH, logAsynch , false);
        logTo = getLog(logToCD);
        if (logAsynch) logTo = new LogImplAsyncWrapper(logTo);
        if (logTo.isTraceEnabled()){
            logTo.trace("logTo ready.");
        }

        //Try to get a Prim implementing LogRegistration and if not, then get sfLog() from targeted PrimImpl.
        Prim logFromPrim = (Prim)sfResolve(ATR_LOG_FROM, logFrom, false);// Register with Log if exists, if not listen only.
        if (logFromPrim !=null) {
            if (logFromPrim instanceof LogRegistration) {
                logFrom = (LogRegistration) logFromPrim;
            } else{
                LogSF logsf = ((PrimImpl) logFromPrim).sfLog();
                if (logsf instanceof LogRegistration) {
                    logFrom = (LogRegistration)logsf;
                }
            }
            if  (logFrom == null){
                throw new SmartFrogResolutionException("'logFrom' not found for "+logFromPrim+" ["+logFromPrim.getClass().getName()+"]");
             }
        }
    }

    /**
     * Loads the right logger implementing Log interface with the rigth configuration
     * If logToCD is null, the component log is used (this.sfLog())
     * @param logToCD ComponentDescription
     * @return Log
     */
    private Log getLog(ComponentDescription logToCD) throws SmartFrogException, RemoteException{
        if (logToCD == null) return sfLog();
        String logClass = null;
        String codeBase=null;
        logClass = logToCD.sfResolve(ATR_LOGGER_CLASS, logClass, true);
        codeBase = sfResolve(SmartFrogCoreKeys.SF_CODE_BASE, codeBase, false);
        return LogImpl.loadLogger(logName,logToCD, new Integer(logLevel),logClass,codeBase);

    }


    // Main component action methods

    /************************************
     *  Log Interface
     ************************************/
      /**
       * <p> Is debug logging currently enabled? </p>
       *
       * <p> Call this method to prevent having to perform expensive operations
       * (for example, <code>String</code> concatenation)
       * when the log level is more than debug. </p>
       * @return boolean true if debug level is currently enabled
       */
      public boolean isDebugEnabled() {
          return logTo.isDebugEnabled();
      }


      /**
       * <p> Is error logging currently enabled? </p>
       *
       * <p> Call this method to prevent having to perform expensive operations
       * (for example, <code>String</code> concatenation)
       * when the log level is more than error. </p>
       * @return boolean true if error level is currently enabled
       */
      public boolean isErrorEnabled(){
          return logTo.isErrorEnabled();
      }


      /**
       * <p> Is fatal logging currently enabled? </p>
       *
       * <p> Call this method to prevent having to perform expensive operations
       * (for example, <code>String</code> concatenation)
       * when the log level is more than fatal. </p>
       * @return boolean true if fatal level is currently enabled
       */
      public boolean isFatalEnabled(){
          return logTo.isFatalEnabled();
      }


      /**
       * <p> Is info logging currently enabled? </p>
       *
       * <p> Call this method to prevent having to perform expensive operations
       * (for example, <code>String</code> concatenation)
       * when the log level is more than info. </p>
       * @return boolean true if info level is currently enabled
       */
      public boolean isInfoEnabled(){
          return logTo.isInfoEnabled();
      }


      /**
       * <p> Is trace logging currently enabled? </p>
       *
       * <p> Call this method to prevent having to perform expensive operations
       * (for example, <code>String</code> concatenation)
       * when the log level is more than trace. </p>
       * @return boolean true if trace level is currently enabled
       */
      public boolean isTraceEnabled(){
          return logTo.isTraceEnabled();
      }


      /**
       * <p> Is warning logging currently enabled? </p>
       *
       * <p> Call this method to prevent having to perform expensive operations
       * (for example, <code>String</code> concatenation)
       * when the log level is more than warn. </p>
       * @return boolean true if warn level is currently enabled
       */
      public boolean isWarnEnabled(){
          return logTo.isWarnEnabled();
      }



      /**
       * <p> Log a message with trace log level. </p>
       *
       * @param message log this message
       */
      public void trace(Object message) {
          logTo.trace(message);
      }


      /**
       * <p> Log an error with trace log level. </p>
       *
       * @param message log this message
       * @param t log this cause
       */
      public void trace(Object message, Throwable t) {
          logTo.trace(message,t);
      }


      /**
       * <p> Log a message with debug log level. </p>
       *
       * @param message log this message
       */
      public void debug(Object message) {
          logTo.debug(message);
      }

      /**
       * <p> Log an error with debug log level. </p>
       *
       * @param message log this message
       * @param t log this cause
       */
      public void debug(Object message, Throwable t) {
          logTo.debug(message,t);
      }

      /**
       * <p> Log a message with info log level. </p>
       *
       * @param message log this message
       */
      public void info(Object message) {
          logTo.info(message);
      }


      /**
       * <p> Log an error with info log level. </p>
       *
       * @param message log this message
       * @param t log this cause
       */
      public void info(Object message, Throwable t) {
          logTo.info(message,t);
      }

      /**
       * <p> Log a message with warn log level. </p>
       *
       * @param message log this message
       */
      public void warn(Object message) {
          logTo.warn(message);
      }


      /**
       * <p> Log an error with warn log level. </p>
       *
       * @param message log this message
       * @param t log this cause
       */
      public void warn(Object message, Throwable t) {
          logTo.warn(message,t);
      }

      /**
       * <p> Log a message with error log level. </p>
       *
       * @param message log this message
       */
      public void error(Object message) {
          logTo.error(message);
      }


      /**
       * <p> Log an error with error log level. </p>
       *
       * @param message log this message
       * @param t log this cause
       */
      public void error(Object message, Throwable t) {
          logTo.error(message,t);
      }

      /**
       * <p> Log a message with fatal log level. </p>
       *
       * @param message log this message
       */
      public void fatal(Object message) {
          logTo.fatal(message);
      }

      /**
       * <p> Log an error with fatal log level. </p>
       *
       * @param message log this message
       * @param t log this cause
       */
      public void fatal(Object message, Throwable t) {
          logTo.fatal(message,t);
      }

    //************************************

    // Help methods to sfResolve Log type attributes

    /**
     * Resolves given reference and gets a Log. Utility method to resolve
     * an attribute with a Log value.
     *
     * @param reference reference
     * @param defaultValue Log default value that is returned when
     *        reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return Log for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException in cae of Remote/Network error
     *
     */
    public Log sfResolve(Reference reference, Log defaultValue, boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;
        try {
            Object referenceObj = sfResolve(reference);

            if (referenceObj instanceof Log) {
                return (((Log) referenceObj));
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe()
                    , referenceObj , referenceObj.getClass().toString()
                    , Log.class.toString());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }
    /**
     * Resolves a referencePart given a string and gets a Log. Utility
     * method to resolve an attribute with a Log value.
     *
     * @param referencePart string field reference
     * @param defaultValue Log default value that is returned when
     *        reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Log for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException in cae of Remote/Network error
     *
     */
    public Log sfResolve(String referencePart, Log defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

}
