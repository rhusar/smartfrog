package org.smartfrog.sfcore.logging;

import org.smartfrog.sfcore.common.SmartFrogLogException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.SFProcess;
import java.util.Hashtable;


public  class LogFactory {

        protected static Hashtable loggers = new Hashtable();

        /**
         * Protected constructor that is not available for public use.
         */
        protected LogFactory() { }

        public static LogSF getLog(Prim prim)
            throws SmartFrogLogException
        {
            LogSF log=null;
            try {
                log = (LogSF)loggers.get(prim.sfCompleteName());
                if (log!=null)
                    return log;

                log = new LogImpl (prim.sfCompleteName().toString());
                loggers.put(prim, log);
            } catch (Throwable thr){
              throw (SmartFrogLogException)SmartFrogLogException.forward(thr);
            }
            return log;
        }

        public static LogSF getLog(String name)
        {
            LogSF log=null;
                log = (LogSF)loggers.get(name);
                if (log!=null)
                    return log;
                log = (LogSF)(new LogImpl(name));
                loggers.put(name, log);
            return log;
        }


        /**
         * Convenience method to derive a name from the specified class and
         * call <code>getInstance(String)</code> with it.
         *
         * @param clazz Class for which a suitable Log name will be derived
         *
         */
        public static LogSF getLog(Prim prim, boolean registeredWithCoreLog) throws SmartFrogLogException{
           LogSF log = getLog(prim);
           try {
               if (registeredWithCoreLog)
                  //@TODO what happens when it changes it log level - needs to be fixed!
                  ((LogRegistration)(getLog(SmartFrogCoreKeys.SF_CORE_LOG))).register(prim.sfCompleteName().toString(), log);
            } catch (Throwable thr){
              throw (SmartFrogLogException)SmartFrogLogException.forward(thr);
            }
            return log;
        }


        /**
         * <p>Construct (if necessary) and return a <code>Log</code> instance.</p>
         *
         * @param name Logical name of the <code>Log</code> instance to be
         *  returned (the meaning of this name is only known to the underlying
         *  loggers registred with Log )
         *
         * @exception SmartFrogLogException if a suitable <code>Log</code>
         *  instance cannot be returned
         */
        public static LogSF getLog (String name, boolean registeredWithCoreLog) throws SmartFrogLogException {
            LogSF log = getLog(name);
            try {
                if (registeredWithCoreLog)
                   //@TODO what happens when it changes it log level - needs to be fixed!
                   ((LogRegistration)getLog(SmartFrogCoreKeys.SF_CORE_LOG)).register(name, log);
            } catch (Throwable thr){
              throw (SmartFrogLogException)SmartFrogLogException.forward(thr);
            }
            return log;
        }




        /**
         * Release any internal references to previously created {@link Log}
         * logs returned by this factory.
         */
        public void release() {
            loggers.clear();

        }

}
