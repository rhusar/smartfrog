package org.smartfrog.sfcore.logging;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLogException;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

import java.rmi.RemoteException;
import java.io.Serializable;

import java.lang.reflect.Method;

/**
 * A simple logging interface abstracting logging APIs based in Apache Jakarta
 * logging.
 *
 */
public class LogImpl implements LogSF, LogRegistration, Serializable {

    /** Default Log object */
    protected Log localLog = null;

    /** Name of this log instance */
    protected String logName = null;
    /** Current log level */
    protected int currentLogLevel= LOG_LEVEL_INFO;

    private static final Method TRACE_O =
          getObjectMethod("trace", new Class[] {Object.class});
    private static final Method TRACE_O_T =
          getObjectMethod("trace", new Class[] {Object.class,Throwable.class});

      private static final Method DEBUG_O =
          getObjectMethod("debug", new Class[] {Object.class});
      private static final Method DEBUG_O_T =
          getObjectMethod("debug", new Class[] {Object.class, Throwable.class});

      private static final Method INFO_O =
          getObjectMethod("info", new Class[] {Object.class});
      private static final Method INFO_O_T =
          getObjectMethod("info", new Class[] {Object.class, Throwable.class});

      private static final Method WARN_O =
          getObjectMethod("warn", new Class[] {Object.class});
      private static final Method WARN_O_T =
          getObjectMethod("warn", new Class[] {Object.class, Throwable.class});

      private static final Method ERROR_O =
          getObjectMethod("error", new Class[] {Object.class});
      private static final Method ERROR_O_T =
          getObjectMethod("error", new Class[] {Object.class, Throwable.class});

      private static final Method FATAL_O =
          getObjectMethod("fatal", new Class[] {Object.class});
      private static final Method FATAL_O_T =
          getObjectMethod("fatal", new Class[] {Object.class, Throwable.class});

    /**
     * Gets the corresponding method of java.lang.Object.
     *
     * @param method A method name.
     * @param args An array with the arguments of that method.
     * @return A  corresponding method of java.lang.Object.
     */
    private static Method getObjectMethod(String method, Class[] args) {
        try {
            return (Log.class).getDeclaredMethod(method, args);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * <p> Invokes method in localLog and registered logs. </p>
     *
     * @param message log this message
     */
    public void  invoke (Method method, Object[] args) {
        try {
            if (localLog!=null)
                method.invoke(localLog,args);
        }catch (Throwable thr){
            if (localLog!=null)
                     localLog.trace("",thr);
                 else thr.printStackTrace();
        }
        //Registered logs
        synchronized (registeredLogs){
            Log log=null;
            Enumeration logs = registeredLogs.elements();
            while (logs.hasMoreElements()){
                try {
                    log = (Log) logs.nextElement();
                    if (isCurrentLevelEnabled(log)) {
                        method.invoke(logs.nextElement(), args);
                    }
                } catch (Throwable thr) {
                    if (log!=null) log.trace("",thr);
                    else if (localLog!=null)
                        localLog.trace("",thr);
                    else thr.printStackTrace();

                }
            }
        }
    }

    public LogImpl (String name){
        //@TODO: load the default class from a SF definition
        // Similar for default deployer.
        localLog=new LogToErr(name,currentLogLevel);
        logName = name;
    }


    /**
     *  Registered inputs, distribution list
     */
    protected Hashtable registeredLogs = new Hashtable();

    //LogImpl configuration

    /**
     * <p> Set logging level. </p>
     *
     * @param currentLogLevel new logging level
     */
    public void setLevel(int currentLogLevel) {
        this.currentLogLevel = currentLogLevel;
    }

    /**
     * <p> Get logging level. </p>
     */
    public int getLevel() {
        return currentLogLevel;
    }

    /**
     * Is the given log level currently enabled?
     *
     * @param logLevel is this level enabled?
     */
    protected boolean isLevelEnabled(int logLevel) {
        // log level are numerically ordered so can use simple numeric
        // comparison
        return (logLevel >= currentLogLevel);
    }




    //Log interface

    /**
     * <p> Are debug messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isDebugEnabled() {
        return isLevelEnabled(LOG_LEVEL_DEBUG);
    }


    /**
     * <p> Are error messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isErrorEnabled() {
        return isLevelEnabled(LOG_LEVEL_ERROR);
    }


    /**
     * <p> Are fatal messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isFatalEnabled() {
        return isLevelEnabled(LOG_LEVEL_FATAL);
    }


    /**
     * <p> Are info messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isInfoEnabled() {
        return isLevelEnabled(LOG_LEVEL_INFO);
    }


    /**
     * <p> Are trace messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isTraceEnabled() {
        return isLevelEnabled(LOG_LEVEL_TRACE);
    }


    /**
     * <p> Are warn messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isWarnEnabled() {
        return isLevelEnabled(LOG_LEVEL_WARN);
    }


     // -------------------------------------------------------- Logging Methods



     private boolean isCurrentLevelEnabled(Log log) {
         if (currentLogLevel >=LOG_LEVEL_OFF) {
             return false;
         } else if (currentLogLevel==LOG_LEVEL_FATAL) {
             return log.isFatalEnabled();
         } else if (currentLogLevel==LOG_LEVEL_ERROR) {
             return log.isErrorEnabled();
         } else if (currentLogLevel==LOG_LEVEL_WARN) {
             return log.isWarnEnabled();
         } else if (currentLogLevel==LOG_LEVEL_INFO) {
             return log.isInfoEnabled();
         } else if (currentLogLevel==LOG_LEVEL_DEBUG) {
             return log.isDebugEnabled();
         } else if (currentLogLevel==LOG_LEVEL_TRACE) {
             return log.isTraceEnabled();
         } else if (currentLogLevel<=LOG_LEVEL_ALL) {
             return true;
         }
         return false;
     }

     /**
      * <p> Log a message with trace log level. </p>
      *
      * @param message log this message
      */
     public void trace(Object message) {
         invoke(TRACE_O,new Object[]{message});
     }



     /**
      * <p> Log an error with trace log level. </p>
      *
      * @param message log this message
      * @param t log this cause
      */
     public void trace(Object message, Throwable t){
         invoke(TRACE_O_T,new Object[]{message,t});
     }


     /**
      * <p> Log a message with debug log level. </p>
      *
      * @param message log this message
      */
     public void debug(Object message){
         invoke(DEBUG_O,new Object[]{message});
     }


     /**
      * <p> Log an error with debug log level. </p>
      *
      * @param message log this message
      * @param t log this cause
      */
     public void debug(Object message, Throwable t){
         invoke(DEBUG_O_T,new Object[]{message,t});
     }


     /**
      * <p> Log a message with info log level. </p>
      *
      * @param message log this message
      */
     public void info(Object message){
         invoke(INFO_O,new Object[]{message});
     }


     /**
      * <p> Log an error with info log level. </p>
      *
      * @param message log this message
      * @param t log this cause
      */
     public void info(Object message, Throwable t){
         invoke(INFO_O_T,new Object[]{message,t});
     }


     /**
      * <p> Log a message with warn log level. </p>
      *
      * @param message log this message
      */
     public void warn(Object message){
         invoke(WARN_O,new Object[]{message});
     }


     /**
      * <p> Log an error with warn log level. </p>
      *
      * @param message log this message
      * @param t log this cause
      */
     public void warn(Object message, Throwable t){
         invoke(WARN_O_T,new Object[]{message,t});
     }


     /**
      * <p> Log a message with error log level. </p>
      *
      * @param message log this message
      */
     public void error(Object message){
         invoke(ERROR_O,new Object[]{message});
     }


     /**
      * <p> Log an error with error log level. </p>
      *
      * @param message log this message
      * @param t log this cause
      */
     public void error(Object message, Throwable t){
         invoke(ERROR_O_T,new Object[]{message,t});
     }


     /**
      * <p> Log a message with fatal log level. </p>
      *
      * @param message log this message
      */
     public void fatal(Object message){
         invoke(FATAL_O,new Object[]{message});
     }


     /**
      * <p> Log an error with fatal log level. </p>
      *
      * @param message log this message
      * @param t log this cause
      */
     public void fatal(Object message, Throwable t){
         invoke(FATAL_O_T,new Object[]{message,t});
     }


    //SFLog interface


    /**
     * <p> Is ignore logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than ignore. </p>
     */
    public boolean isIgnoreEnabled(){
        return isLevelEnabled(LOG_LEVEL_IGNORE);
    }


    /**
     * <p> Log a message with ignore log level. </p>
     *
     * @param message log this message
     */
    public void ignore(Object message){
        invoke(TRACE_O,new Object[]{"IGNORE - "+message.toString()});
    }


    /**
     * <p> Log an error with ignore log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void ignore(Object message, Throwable t){
        invoke(TRACE_O_T,new Object[]{"IGNORE - "+message.toString(),t});
    }


    /**
     * <p> Log a message with ignore log level. </p>
     *
     * @param message log this message
     */
    public void ignore(Object message, SmartFrogException t, TerminationRecord tr) {
        ignore(message,t);
    }


    /**
     * <p> Log an error with ignore log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void ignore(Object message, SmartFrogException t){
        ignore(message,t);
    }


    // Special methods for user output
    /**
     * <p> Log an error with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     */
    public void out(Object message){
        try {
            if (isCurrentLevelEnabled(localLog)) {
                if ((localLog!=null)&&(localLog instanceof LogMessage)) {
                    ((LogMessage)localLog).out(message);
                } else {
                    System.out.println(message.toString());
                }
            }
        }catch (Throwable thr){
            if (localLog!=null)
                     localLog.trace(message,thr);
                 else thr.printStackTrace();
        }

        //Registered logs
        synchronized (registeredLogs){
            Log log=null;
            Enumeration logs = registeredLogs.elements();
            while (logs.hasMoreElements()){
                try {
                    log = (Log) logs.nextElement();
                    if (isCurrentLevelEnabled(log)) {
                        if (log instanceof LogMessage)
                            ((LogMessage)log).out(message);
                        else log.info(message);
                    }
                } catch (Throwable thr) {
                    if (log!=null) log.trace("",thr);
                    else if (localLog!=null)
                        localLog.trace("",thr);
                    else thr.printStackTrace();

                }
            }
        }
    }


    /**
     * <p> Log an error with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void err(Object message, Throwable t){
        try {
            if (isCurrentLevelEnabled(localLog)) {
                if ((localLog!=null)&&(localLog instanceof LogMessage)) {
                    ((LogMessage)localLog).err(message, t);
                } else {
                    System.err.println(message.toString());
                    t.printStackTrace();
                }
            }
        }catch (Throwable thr){
            if (localLog!=null)
                     localLog.trace("",thr);
                 else thr.printStackTrace();
        }
        //Registered logs
        synchronized (registeredLogs){
            Log log=null;
            Enumeration logs = registeredLogs.elements();
            while (logs.hasMoreElements()){
                try {
                    log = (Log) logs.nextElement();
                    if (isCurrentLevelEnabled(log)) {
                        if (log instanceof LogMessage)
                            ((LogMessage)log).err(message,t);
                        else log.info(message,t);
                    }
                } catch (Throwable thr) {
                    if (log!=null) log.trace("",thr);
                    else if (localLog!=null)
                        localLog.trace("",thr);
                    else thr.printStackTrace();

                }
            }
        }
    }


    /**
     * <p> Log a message with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     */
    public void err(Object message, SmartFrogException t, TerminationRecord tr){
       err(message,t);
       System.err.println(tr.toString());
    }


    /**
     * <p> Log an error with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void err(Object message, SmartFrogException t){
      err(message,t);
    }


    /**
     * <p> Log a message with trace log level. </p>
     *
     * @param message log this message
     */
    public void trace(Object message, SmartFrogException t, TerminationRecord tr){
        trace(message,t);
    }


    /**
     * <p> Log an error with trace log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void trace(Object message, SmartFrogException t){
        trace(message,t);
    }


    /**
     * <p> Log a message with debug log level. </p>
     *
     * @param message log this message
     */
    public void debug(Object message, SmartFrogException t, TerminationRecord tr){
        debug(message,t);
    }


    /**
     * <p> Log an error with debug log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void debug(Object message, SmartFrogException t){
        debug(message,t);
    }


    /**
     * <p> Log a message with info log level. </p>
     *
     * @param message log this message
     */
    public void info(Object message, SmartFrogException t, TerminationRecord tr){
        info(message,t);
    }


    /**
     * <p> Log an error with info log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void info(Object message, SmartFrogException t){
        info(message,t);
    }


    /**
     * <p> Log a message with warn log level. </p>
     *
     * @param message log this message
     */
    public void warn(Object message, SmartFrogException t, TerminationRecord tr){
        warn(message,t);
    }


    /**
     * <p> Log an error with warn log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void warn(Object message, SmartFrogException t){
        warn(message,t);
    }


    /**
     * <p> Log a message with error log level. </p>
     *
     * @param message log this message
     */
    public void error(Object message, SmartFrogException t, TerminationRecord tr){
        error(message,t);
    }


    /**
     * <p> Log an error with error log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void error(Object message, SmartFrogException t){
        error(message,t);
    }


    /**
     * <p> Log a message with fatal log level. </p>
     *
     * @param message log this message
     */
    public void fatal(Object message, SmartFrogException t, TerminationRecord tr){
        fatal(message,t);
    }


    /**
     * <p> Log an error with fatal log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void fatal(Object message, SmartFrogException t){
        fatal(message,t);
    }


    //Log Registration interface
    public void register(String name,Log log)  throws SmartFrogLogException , RemoteException{
        try {
            registeredLogs.put(logName+"."+name, log);
        } catch (Exception ex) {
            throw (SmartFrogLogException)SmartFrogLogException.forward(ex);
        }
    }

   public void register(String name,Log log, int logLevel)  throws RemoteException, SmartFrogLogException{

       register(name,log);
       if (currentLogLevel>=logLevel){
         currentLogLevel=logLevel;
       }
   }

    public boolean deregister(String name)  throws SmartFrogLogException, RemoteException {
       try {
           if (registeredLogs.remove(logName+"."+name) == null) {
              return false;
           } else {
              return true;
           }

       } catch (Exception ex) {
            throw (SmartFrogLogException)SmartFrogLogException.forward(ex);
       }
    }


//    /**
//        * Main method used in unit testing.
//        *
//        *@param  args  Command line arguments
//        */
//       public static void main(String[] args) {
//          // Example of use
//          LogSF log = new LogImpl("test");
//          log.setLevel(LogSF.LOG_LEVEL_ALL);
//          String message = "message - ";
//          int i = 1;
//          log.out(message+ i++);
//          log.info(message+ i++);
//          try {
//              log.trace(message+ i++);
//          } catch (Exception ex){
//              log.fatal("Oppss!"+message+ i++,ex);
//          }
//          try {
//              log.trace("Hola -2-"+message+ i++);
//          } catch (Exception ex){
//              log.fatal("Oppss! -2-"+message+ i++,ex);
//          }
//
//       }
//


/*

@TODO add method for user message output! -> Use default Logger!


---
System.err.println((sfex).toString("\n   "));
---
TR and ComponentName
          if ((logStackTrace)&&!tr.errorType.equals(TerminationRecord.NORMAL)) {
            StringBuffer strb = new StringBuffer();
            strb.append("LOG TR: Component: " +componentName +", "+ tr.toString());
            log(strb.toString());


          }

// SET the level of current log according to the level used to register!

*/


}


