package org.smartfrog.sfcore.logging;

import org.smartfrog.sfcore.common.SmartFrogLogException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.Reference;

import java.util.Hashtable;
import java.rmi.RemoteException;


public  class LogFactory {

    /**
     * hashtable of loggers
     */
    protected static Hashtable loggers = new Hashtable();

    /**
     * Protected constructor that is not available for public use.
     */
    protected LogFactory() { }

    /**
     * get a log for a component
     * @param prim component
     * @return a new log.
     * @throws SmartFrogLogException when something went wrong with getting a log
     */
    public static LogSF getLog(Prim prim)
            throws SmartFrogLogException
    {
        LogSF log=null;
        try {
            final Reference completeName = prim.sfCompleteName();
            //look for a log
            log = (LogSF)loggers.get(completeName);
            //if found, return it
            if (log!=null) {
                return log;
            }
            //else create a new one
            log = new LogImpl (completeName.toString());
            //and remember it
            loggers.put(prim, log);
        } catch (RemoteException e){
            throw (SmartFrogLogException)SmartFrogLogException.forward(e);
        }
        return log;
    }

    /**
     * get a named log.
     * @param name
     * @return
     */
    public static LogSF getLog(String name)
    {
        LogSF log=null;
        log = (LogSF)loggers.get(name);
        if (log!=null) {
            return log;
        }
        log = (LogSF)(new LogImpl(name));
        loggers.put(name, log);
        return log;
    }


    /**
     * Convenience method to derive a name from the specified class and
     * call <code>getInstance(String)</code> with it.
     *
     * @param prim component to use
     * @param registerWithCoreLog
     * @return a log
     * @throws SmartFrogLogException if the prim name could not be completed, or when registration failed.
     *
     */
    public static LogSF getLog(Prim prim, boolean registerWithCoreLog) throws SmartFrogLogException{
        LogSF log = getLog(prim);
        if (registerWithCoreLog) {
            String name = null;
            try {
                name = prim.sfCompleteName().toString();
            } catch (RemoteException e) {
                throw (SmartFrogLogException) SmartFrogLogException.forward(e);
            }
            registerWithCore(name,log);
        }
        return log;
    }


    /**
     * <p>Construct (if necessary) and return a <code>Log</code> instance.</p>
     *
     * @param name Logical name of the <code>Log</code> instance to be
     *  returned (the meaning of this name is only known to the underlying
     *  loggers registered with Log )
     *
     * @param registerWithCoreLog flag to enable registration with the core
     * @return a log
     * @exception SmartFrogLogException if a suitable <code>Log</code>
     *  instance cannot be returned
     */
    public static LogSF getLog (String name, boolean registerWithCoreLog) throws SmartFrogLogException {
        LogSF log = getLog(name);
        if ( registerWithCoreLog ) {
            registerWithCore(name, log);
        }
        return log;
    }

    /**
     * register a log with the core
     * @param name
     * @param log
     * @throws SmartFrogLogException
     * @todo what happens when it changes its log level - needs to be fixed!
     */
    private static void registerWithCore(String name, LogSF log) throws SmartFrogLogException {
        try {
            LogSF coreLog = getLog(SmartFrogCoreKeys.SF_CORE_LOG);
            if(!(coreLog instanceof LogRegistration )) {
                throw new SmartFrogLogException("Core log does not implement LogRegistration");
            }
            LogRegistration logRegistration = ((LogRegistration) coreLog);
            logRegistration.register(name, log);
        } catch (RemoteException e) {
            throw (SmartFrogLogException) SmartFrogLogException.forward(e);
        }
    }


    /**
     * Release any internal references to previously created {@link Log}
     * logs returned by this factory.
     */
    public void release() {
        loggers.clear();
    }

    /**
     * get the log of an owner, or, if that fails, from our classname.
     * Useful for helper classes that want to log through their owner
     * and dont want lots of error handling in their constructors
     * @param owner component that owns this helper class
     * @param clazz class own classname
     * @return a log
     */
    public static Log getOwnerLog(final Prim owner, final Class clazz) {
        Log log=null;
        try {
            log=owner.sfGetApplicationLog();
        } catch (SmartFrogException e) {
        } catch (RemoteException e) {
        }
        if(log==null) {
            log=getLog(clazz.getName());
        }
        return log;
    }

    /**
     * get the log of an owner, or, if that fails, from our classname.
     * Useful for helper classes that want to log through their owner
     * and dont want lots of error handling in their constructors
     * @param owner component that owns this helper class
     * @param object class to log off
     * @return a log
     */
    public static Log getOwnerLog(final Prim owner, final Object object) {
        return getOwnerLog(owner, object.getClass());
    }
}
