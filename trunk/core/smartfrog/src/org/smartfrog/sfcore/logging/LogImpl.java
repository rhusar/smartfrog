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

import org.smartfrog.sfcore.common.Logger;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLogException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.reference.Reference;

import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;
import org.smartfrog.sfcore.common.*;
import java.util.Vector;

public class LogImpl implements LogSF, LogRegistration, Serializable {
//    //check Local java.util.looging log record
//    static ThreadLocal localThreadLog =
//      new ThreadLocal() {
//        protected Log[] initialValue() {
//          return new Log[1];
//        }
//      };


    /** Default Log object */
    protected Log localLog = null;

    /** Name of this log instance */
    protected String logName = null;
    /** Current log level */
    protected int currentLogLevel= LOG_LEVEL_INFO;



    public static final Method TRACE_O =
          getObjectMethod("trace", new Class[] {Object.class});
    public static final Method TRACE_O_T =
          getObjectMethod("trace", new Class[] {Object.class,Throwable.class});

      public static final Method DEBUG_O =
          getObjectMethod("debug", new Class[] {Object.class});
      public static final Method DEBUG_O_T =
          getObjectMethod("debug", new Class[] {Object.class, Throwable.class});

      public static final Method INFO_O =
          getObjectMethod("info", new Class[] {Object.class});
      public static final Method INFO_O_T =
          getObjectMethod("info", new Class[] {Object.class, Throwable.class});

      public static final Method WARN_O =
          getObjectMethod("warn", new Class[] {Object.class});
      public static final Method WARN_O_T =
          getObjectMethod("warn", new Class[] {Object.class, Throwable.class});

      public static final Method ERROR_O =
          getObjectMethod("error", new Class[] {Object.class});
      public static final Method ERROR_O_T =
          getObjectMethod("error", new Class[] {Object.class, Throwable.class});

      public static final Method FATAL_O =
          getObjectMethod("fatal", new Class[] {Object.class});
      public static final Method FATAL_O_T =
          getObjectMethod("fatal", new Class[] {Object.class, Throwable.class});


    /**
     * Gets the corresponding method of {@link Log}
     *
     * @param method A method name.
     * @param args An array with the arguments of that method.
     * @return A  corresponding method of the Log class
     */
    public static Method getObjectMethod(String method, Class[] args) {
        try {
            return (Log.class).getDeclaredMethod(method, args);
        } catch (Exception e) {
            return null;
        }
    }

    // Used to process one of the registered logs
    private Log log=null;
    /**
     * <p> Invokes method in localLog and registered logs. </p>
     *
     * @param method method to call
     * @param args args to invoke
     */
    public void  invoke (Method method, Object[] args) {
//        // Local Thread log/debug info
//        Log thrLog = null;
//        try {
//            Object[] thrCtx = localThreadLog.get();
//            if (thrCtx[0]==null) {
//                try {
//                    thrCtx[0]= new Object[];
//
//                } finally {
//                    thrCtx[0]=null;
//                }
//
//            } else {
//                thrLog = (Log)localThreadLog[0];
//                method.invoke(thrLog,args);
//            }
//        } catch (Throwable thr){
//            if(thr instanceof InvocationTargetException) {
//                //get a sub throwable here
//                thr=thr.getCause();
//            }
//            if (localThreadLog!=null) {
//                thrLog.error("Error Invoke LogImpl for LocalThread",thr);
//            }
//            else {
//                thr.printStackTrace();
//            }
//        }
//        // end -  Local Thread log/debug info

        try {
            if (localLog!=null) {
                method.invoke(localLog,args);
            }
        } catch (Throwable thr){
            if(thr instanceof InvocationTargetException) {
                //get a sub throwable here
                thr=thr.getCause();
            }
            if (localLog!=null) {
                localLog.error("Error Invoke LogImpl",thr);
            }
            else {
                thr.printStackTrace();
            }
        }
        //Registered logs
        synchronized (registeredLogs){
            log=null;
            Enumeration logs = registeredLogs.elements();
            while (logs.hasMoreElements()){
                try {
                    log = (Log) logs.nextElement();
                    if (isCurrentLevelEnabled(log)) {
                        method.invoke(log, args);
                    }
                } catch (Throwable thr) {
                    if (thr instanceof InvocationTargetException) {
                        //get a sub throwable here
                        thr = thr.getCause();
                    }
                    if (log!=null) {
                        log.trace("",thr);
                    }
                    else if (localLog!=null) {
                        localLog.trace("",thr);
                    }
                    else {
                        thr.printStackTrace();
                    }
                }
            }
            log=null;
        }
    }

    /**
     * Constructor
     * @param name for Log
     */
    public LogImpl (String name){
        this (name,null);
    }

    /**
     * Constructor
     * @param name for Log
     * @param componentComponentDescription configuration that overwrites class configuraion
     */
    public LogImpl (String name, ComponentDescription componentComponentDescription) {
        this (name, componentComponentDescription,null);
    }

    /**
     * Constructor
     * @param name for Log
     * @param componentComponentDescription configuration that overwrites class configuraion
     * @param initialLogLevel level to log at. If null it will be ignored.
     */
    public LogImpl (String name, ComponentDescription componentComponentDescription, Integer initialLogLevel) {
        //Configuration for LogImpl class
        ComponentDescription classComponentDescription = null;

        // Level set in configuration
        int configurationLevel = currentLogLevel;

        if (initialLogLevel!=null){
            configurationLevel = initialLogLevel.intValue();
        }
        // logger class set in configuration (Vector or String)
        Object configurationClass = "org.smartfrog.sfcore.logging.LogToFileImpl";
        // codebase used to load  class set in configuration
        String configurationCodeBase = null;

        Vector loggersConfiguration = null;

        logName = name;
        try {
            //Check Class and read configuration...including system.properties
            classComponentDescription = ComponentDescriptionImpl.getClassComponentDescription(this, true,null);
            if (classComponentDescription!=null){
               configurationClass = getConfigurationClass(classComponentDescription,configurationClass);
               loggersConfiguration = getLoggersConfigurationForConfigurationClass(classComponentDescription, configurationClass, loggersConfiguration);
               configurationLevel = getConfigurationLevel(classComponentDescription,configurationLevel);
               configurationCodeBase = getConfigurationCodeBase(classComponentDescription,configurationCodeBase);
            }
            // overwriting class configuration with component configuration if provided.
            if (componentComponentDescription!=null){
                configurationClass = getConfigurationClass(componentComponentDescription,configurationClass);
                loggersConfiguration = getLoggersConfigurationForConfigurationClass(componentComponentDescription, configurationClass, loggersConfiguration);
                configurationLevel = getConfigurationLevel(componentComponentDescription,configurationLevel);
                configurationCodeBase = getConfigurationCodeBase(componentComponentDescription,configurationCodeBase);
            }

            setLevel (configurationLevel);

            loadStartUpLoggers(name, configurationClass, configurationCodeBase, loggersConfiguration);

            //Set lower level of the two, just in case local logger has its own mechanism to set log level
            int i= getLevel(localLog);
            if (currentLogLevel>i){
              setLevel(i);
            }

        } catch (Exception ex ){
            String msg = "Error during initialization of localLog for LogImpl. Next trying to use Default (LogToFile)";
            String msg2 = "Log '"+name+"' , values [class,level,codebase]: "+ configurationClass +", "+  configurationLevel +", "+ configurationCodeBase +
                        "\nusing Class ComponentDescription:\n{"+classComponentDescription+
                        "}\n, and using Component ComponentDescription:\n{"+ componentComponentDescription+"}";

            System.err.println("[WARN] "+msg+", Reason: "+ex.getMessage());
            if (org.smartfrog.sfcore.common.Logger.logStackTrace) {
                System.err.println("[WARN] "+msg2);
            }
            try {
                localLog=new LogToFileImpl(name,new Integer(currentLogLevel));
                if ((localLog.isWarnEnabled())) localLog.warn(msg2);
                if ((localLog.isWarnEnabled())) localLog.warn(msg, ex);
            } catch (java.lang.NullPointerException nex){
                msg = "Error during emergency initialization of localLog using default LogToFile for LogImpl. No logger available.";
                System.err.println("[FATAL] "+msg+", Reason: "+nex.toString());
                if (org.smartfrog.sfcore.common.Logger.logStackTrace) nex.printStackTrace();
                throw nex;
            } catch (Throwable thr) {
                msg = "Error during emergency initialization of localLog using default LogToFile for LogImpl. No logger available.";
                System.err.println("[FATAL] "+msg+", Reason: "+thr.toString());
                final Writer result = new StringWriter();
			    PrintWriter printWriter = new PrintWriter(result);
			    thr.printStackTrace(printWriter);
                printWriter.close();
                System.err.print("[FATAL] Stack trace: "+result.toString());
            }
        }
        if ((localLog!=null)&&(localLog.isTraceEnabled())) {
            String msg2 = "Log '"+name+"' , values [class,level,codebase]: "+ configurationClass +", "+  configurationLevel +", "+ configurationCodeBase +
                        "\nusing Class ComponentDescription:\n {"+classComponentDescription+
                        "}\n, and using Component ComponentDescription:\n{"+ componentComponentDescription+"}";
            localLog.trace(msg2);
        }
    }

    /**
     *  Loads all predefined loggers and their configurations.
     *
     * @param name String
     * @param configurationClass Object
     * @param configurationCodeBase String
     * @param loggersConfiguration Vector
     * @throws RemoteException in case of remote/network failure
     * @throws SmartFrogLogException  if failed to register logger
     */

    private void loadStartUpLoggers(String name, Object configurationClass, String configurationCodeBase, Vector loggersConfiguration) throws RemoteException, SmartFrogLogException {
        if (configurationClass instanceof String) {
            localLog = loadLogger(name, (ComponentDescription)loggersConfiguration.firstElement(), new Integer(currentLogLevel), (String)configurationClass, configurationCodeBase);
            if (localLog.isDebugEnabled()) localLog.debug("Logger registered: "+ localLog.getClass().toString());
        } else if (configurationClass instanceof Vector) {
            String className = null;
            ComponentDescription loggerConfiguration = null;
            Log logger = null;
            for (int i = 0; i<((Vector)configurationClass).size(); i++) {
                try {
                    className = (String)((Vector)configurationClass).get(i);
                    loggerConfiguration = (ComponentDescription)loggersConfiguration.get(i);
                    logger = loadLogger(name, loggerConfiguration,
                                        new Integer(currentLogLevel), className,
                                        configurationCodeBase);
                    if (i==0) {
                        localLog = logger;
                    } else {
                        register("localLog"+i, logger);
                    }
                    if (isDebugEnabled()) {
                        debug("Logger registered: "+logger.getClass().toString());
                    }
                } catch (Exception ex) {
                   if (i ==0) {
                       throw (SmartFrogLogException)SmartFrogLogException.forward("Fail to register mandatory logger",ex);
                   } else {
                       if ((localLog!=null)&&isErrorEnabled()){
                           error("Fail to register logger: "+className,ex);
                       }
                   }
                }
            }
        }
    }

    /**
     * Gets configurations (if any) for one (String) or several (Vector) for logger classes
     * @param configurationComponentDescription ComponentDescription Where to find the configuration
     * @param configurationClass Object String or Vector which configuration is to be found
     * @param defaultLoggersConfiguration Vector default Configuration/s for configurationClass/es
     * @throws SmartFrogResolutionException if failed to resolve
     */
    private Vector getLoggersConfigurationForConfigurationClass( ComponentDescription configurationComponentDescription,
        Object configurationClass, Vector defaultLoggersConfiguration) throws SmartFrogResolutionException {
        Vector loggersConfiguration = new Vector();
        ComponentDescription defaultConfig = null;
        String className=null;
        if (configurationClass instanceof String){
             if ((defaultLoggersConfiguration!=null)&& (defaultLoggersConfiguration.size()>=1)){
                 defaultConfig = (ComponentDescription)defaultLoggersConfiguration.firstElement();
             }
             loggersConfiguration.add(getConfigurationForClass(configurationComponentDescription,(String)configurationClass,defaultConfig));
        } else if (configurationClass instanceof Vector) {
           for( int i = 0; i < ((Vector)configurationClass).size(); i++){
               defaultConfig = null;
               if ((defaultLoggersConfiguration!=null)){
                   try {
                       defaultConfig = (ComponentDescription) defaultLoggersConfiguration.get(i);
                   } catch (Exception ex) {
                       defaultConfig = null;
                   }
               }
               className = (String)((Vector)configurationClass).get(i);
               loggersConfiguration.add(getConfigurationForClass(configurationComponentDescription,className,defaultConfig));
           }
        } else {
            throw SmartFrogResolutionException.illegalClassType(null,null,configurationClass,configurationClass.getClass().toString(),"String or Vector");
        }
        return loggersConfiguration;
    }

    /**
     * Reads configurationClass attribute for LogImpl from a componentDescription
     *
     * @param componentDescription ComponentDescription
     * @param configurationClass configurationClass Object
     * @return Object (Vector or String)
     * @throws SmartFrogResolutionException if failed to resolve
     */
    private Object getConfigurationClass(ComponentDescription componentDescription, Object configurationClass) throws SmartFrogResolutionException {
        if (componentDescription==null) return configurationClass;
        try {
            return componentDescription.sfResolve(ATR_LOGGER_CLASS, true);
        } catch (SmartFrogResolutionException ex) {
            return configurationClass;
        }
    }

    /**
     * Searches componentDescription for a particular configurationClass searching for an
     * attribute called classname+Config this is used to overwrite the class configuration stored in
     * default classname.sf files
     * @param componentDescription ComponentDescription where to find the configuration
     * @param configurationClass configurationClass Object
     * @param defaultConfig configuration Object returned if nothing is found
     * @return componentdescription configuration found or default value if nothing was found or null if
     * a problem ocurred
     * @throws SmartFrogResolutionException if failed to resolve
     */
    private Object getConfigurationForClass(ComponentDescription componentDescription, String configurationClass, ComponentDescription defaultConfig) throws SmartFrogResolutionException {
        if (componentDescription==null) {
            return defaultConfig;
        }
        String className = configurationClass.substring(configurationClass.lastIndexOf("."));
        return (ComponentDescription)componentDescription.sfResolve(className+"Config", defaultConfig, false);
    }

    /**
     * Reads configurationLevel attribute for LogImpl from a componentDescription
     *
     * @param componentDescription ComponentDescription
     * @param configurationLevel int
     * @return int
     * @throws SmartFrogResolutionException if failed to resolve
     */
    private int getConfigurationLevel(ComponentDescription componentDescription, int configurationLevel) throws SmartFrogResolutionException {
        if (componentDescription==null) return configurationLevel;
        return componentDescription.sfResolve(ATR_LOG_LEVEL, getLevel(), false);
    }

    /**
     * Reads configurationCodeBase attribute for LogImpl from a componentDescription
     *
     * @param componentDescription ComponentDescription
     * @param configurationCodeBase String
     * @return String
     * @throws SmartFrogResolutionException  if failed to resolve
     */
    private String getConfigurationCodeBase(ComponentDescription componentDescription,String configurationCodeBase) throws SmartFrogResolutionException {
        if (componentDescription==null) return configurationCodeBase;
        return getSfCodeBase(componentDescription);
    }


    /**
     *  Registered inputs, distribution list
     */
    protected Hashtable registeredLogs = new Hashtable();

    //LogImpl configuration

    /**
     *  Dynamically loads the class that implements the selected logger
     * @param name String
     * @param configuration ComponentDescription
     * @param logLevel Integer
     * @param targetClassName String
     * @param targetCodeBase String
     * @return Log logger implementing Log interface.
     * @throws SmartFrogLogException if failed to load
     */

   public static Log loadLogger(String name, ComponentDescription configuration,Integer logLevel, String targetClassName , String targetCodeBase)
          throws SmartFrogLogException{
          try {
            Class deplClass = SFClassLoader.forName(targetClassName, targetCodeBase, true);

            Class[] deplConstArgsTypes = { String.class, ComponentDescription.class , Integer.class };

            Constructor deplConst = deplClass.getConstructor(deplConstArgsTypes);

            Object[] deplConstArgs = { name, configuration, logLevel};

            return (Log) deplConst.newInstance(deplConstArgs);
        } catch (NoSuchMethodException nsmetexcp) {
            throw new SmartFrogLogException(MessageUtil.formatMessage(
                    MessageKeys.MSG_METHOD_NOT_FOUND, targetClassName, "getConstructor(String,CompDesc,Integer)"),
                nsmetexcp);
        } catch (ClassNotFoundException cnfexcp) {
            throw new SmartFrogLogException(MessageUtil.formatMessage(
                    MessageKeys.MSG_CLASS_NOT_FOUND, targetClassName), cnfexcp);
        } catch (InstantiationException instexcp) {
            throw new SmartFrogLogException(MessageUtil.formatMessage(
                    MessageKeys.MSG_INSTANTIATION_ERROR, targetClassName), instexcp);
        } catch (IllegalAccessException illaexcp) {
            throw new SmartFrogLogException(MessageUtil.formatMessage(
                    MessageKeys.MSG_ILLEGAL_ACCESS, targetClassName, "newInstance()"), illaexcp);
        } catch (InvocationTargetException intarexcp) {
            String msg = "Error during initialization of logger."+
                         " Data: name "+name+", targetClassName "+targetClassName+
                         ", logLevel "+logLevel+", targetCodeBase "+targetCodeBase;
            System.err.println("[ERROR] "+msg+", Reason: "+intarexcp.toString());
            intarexcp.getCause().printStackTrace();
            throw new SmartFrogLogException(MessageUtil.formatMessage(MessageKeys.MSG_INVOCATION_TARGET, targetClassName), intarexcp);

       } catch (Exception ex){
          throw (SmartFrogLogException)SmartFrogLogException.forward(ex);
       }
   }

   /**
    * <p> Get log name. </p>
    *
    * @return log name
    */
    public String getLogName(){
        return this.logName;
    }


   /**
    * Gets the class code base by resolving the sfCodeBase attribute in the
    * given description.
    *
    * @param desc Description in which we resolve the code base.
    *
    * @return class code base for that description.
    */
   protected String getSfCodeBase(ComponentDescription desc) {
       try {
           return (String) desc.sfResolve(new Reference(SmartFrogCoreKeys.SF_CODE_BASE));
       } catch (Exception e) {
           // Not found, return null...
       }

       return null;
   }


    /**
     * <p> Set logging level. </p>
     *
     * @param currentLogLevel new logging level
     */
    public void setLevel(int currentLogLevel) {
        this.currentLogLevel = currentLogLevel;
        if (localLog instanceof LogLevel){
           ((LogLevel)localLog).setLevel(currentLogLevel);
        }
        if (isTraceEnabled()) this.trace("setLogLevel()="+this.currentLogLevel);
    }

    /**
     * <p> Get logging level. </p>
     * @return int log level
     */
    public int getLevel() {
        return currentLogLevel;
    }

    /**
     * <p> Get logger level </p>
     *
     * @param logger
     * @return int log level
     */
    public static int getLevel(Log logger) {
       if (logger.isTraceEnabled()){
         return LOG_LEVEL_TRACE;
       } else if (logger.isDebugEnabled()){
         return LOG_LEVEL_DEBUG;
       } else if (logger.isInfoEnabled()){
         return LOG_LEVEL_INFO;
       } else if (logger.isWarnEnabled()){
         return LOG_LEVEL_WARN;
       } else if (logger.isErrorEnabled()){
         return LOG_LEVEL_ERROR;
       } else if (logger.isFatalEnabled()){
          return LOG_LEVEL_FATAL;
       }
       return LOG_LEVEL_ALL;
    }

    /**
     * Is the given log level currently enabled?
     *
     * @param logLevel is this level enabled?
     * @return boolean true if given log level is currently enabled
     */
    public boolean isLevelEnabled(int logLevel) {
        // log level are numerically ordered so can use simple numeric
        // comparison
        //System.out.println("LogImpl: Current "+currentLogLevel+", compared "+logLevel);
        return (logLevel >= currentLogLevel);
    }




    //Log interface

    /**
     * <p> Are debug messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     * @return boolean true if debug level is currently enabled
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
     * @return boolean true if error level is currently enabled
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
     * @return boolean true if fatal level is currently enabled
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
     * @return boolean true if info level is currently enabled
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
     * @return boolean true if trace level is currently enabled
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
     * @return boolean true if warn  level is currently enabled
     */
    public final boolean isWarnEnabled() {
        return isLevelEnabled(LOG_LEVEL_WARN);
    }


     // -------------------------------------------------------- Logging Methods

     /**
      * Gets a valid message from Throwable if message was not defined.
      * @param message Object
      * @param t Throwable
      * @return Object
      */
     private Object getLogMessageIfNull(Object message, Throwable t) {
         if ((message==null)&& (t!=null)) { message = t.getMessage(); }
         return message;
     }

    /**
     *
     * Is current log level enabled?
     *
     * @param logger  logger
     * @return boolean
     */
     private boolean isCurrentLevelEnabled(Log logger) {
         if (currentLogLevel >=LOG_LEVEL_OFF) {
             return false;
         } else if (currentLogLevel==LOG_LEVEL_FATAL) {
             return logger.isFatalEnabled();
         } else if (currentLogLevel==LOG_LEVEL_ERROR) {
             return logger.isErrorEnabled();
         } else if (currentLogLevel==LOG_LEVEL_WARN) {
             return logger.isWarnEnabled();
         } else if (currentLogLevel==LOG_LEVEL_INFO) {
             return logger.isInfoEnabled();
         } else if (currentLogLevel==LOG_LEVEL_DEBUG) {
             return logger.isDebugEnabled();
         } else if (currentLogLevel==LOG_LEVEL_TRACE) {
             return logger.isTraceEnabled();
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
         if (message instanceof Throwable){
             trace (message,(Throwable)message);
         } else {
            invoke(TRACE_O,new Object[]{message});
         }
     }



     /**
      * <p> Log an error with trace log level. </p>
      *
      * @param message log this message
      * @param t log this cause
      */
     public void trace(Object message, Throwable t){
         message = getLogMessageIfNull(message, t);
         invoke(TRACE_O_T,new Object[]{message,t});
     }


     /**
      * <p> Log a message with debug log level. </p>
      *
      * @param message log this message
      */
     public void debug(Object message){
         if (message instanceof Throwable){
             debug (message,(Throwable)message);
         } else {
            invoke(DEBUG_O,new Object[]{message});
         }
     }


     /**
      * <p> Log an error with debug log level. </p>
      *
      * @param message log this message
      * @param t log this cause
      */
     public void debug(Object message, Throwable t){
        message = getLogMessageIfNull(message, t);
        invoke(DEBUG_O_T,new Object[]{message,t});
     }


     /**
      * <p> Log a message with info log level. </p>
      *
      * @param message log this message
      */
     public void info(Object message){
         if (message instanceof Throwable){
             info (message,(Throwable)message);
         } else {
             invoke(INFO_O,new Object[]{message});
         }
     }


     /**
      * <p> Log an error with info log level. </p>
      *
      * @param message log this message
      * @param t log this cause
      */
     public void info(Object message, Throwable t){
         message = getLogMessageIfNull(message, t);
         invoke(INFO_O_T,new Object[]{message,t});
     }


     /**
      * <p> Log a message with warn log level. </p>
      *
      * @param message log this message
      */
     public void warn(Object message){
         if (message instanceof Throwable){
             warn (message,(Throwable)message);
         } else {
             invoke(WARN_O,new Object[]{message});
         }
     }


     /**
      * <p> Log an error with warn log level. </p>
      *
      * @param message log this message
      * @param t log this cause
      */
     public void warn(Object message, Throwable t){
         message = getLogMessageIfNull(message, t);
         invoke(WARN_O_T,new Object[]{message,t});
     }


     /**
      * <p> Log a message with error log level. </p>
      *
      * @param message log this message
      */
     public void error(Object message){
         if (message instanceof Throwable){
             error (message,(Throwable)message);
         } else {
            invoke(ERROR_O,new Object[]{message});
         }
     }


     /**
      * <p> Log an error with error log level. </p>
      *
      * @param message log this message
      * @param t log this cause
      */
     public void error(Object message, Throwable t){
         message = getLogMessageIfNull(message, t);
         invoke(ERROR_O_T,new Object[]{message,t});
     }


     /**
      * <p> Log a message with fatal log level. </p>
      *
      * @param message log this message
      */
     public void fatal(Object message){
         if (message instanceof Throwable){
             fatal (message,(Throwable)message);
         } else {
         invoke(FATAL_O,new Object[]{message});
         }
     }


     /**
      * <p> Log an error with fatal log level. </p>
      *
      * @param message log this message
      * @param t log this cause
      */
     public void fatal(Object message, Throwable t){
         message = getLogMessageIfNull(message, t);
         invoke(FATAL_O_T,new Object[]{message,t});
     }


    //SFLog interface


    /**
     * <p> Is ignore logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than ignore. </p>
     * @return boolean
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
        if (message instanceof Throwable){
            ignore (message,(Throwable)message);
        } else {
            invoke(TRACE_O,new Object[]{"IGNORE - "+message.toString()});
         }
    }


    /**
     * <p> Log an error with ignore log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void ignore(Object message, Throwable t){
        message = getLogMessageIfNull(message, t);
        invoke(TRACE_O_T,new Object[]{"IGNORE - "+message.toString(),t});
    }


    /**
     * <p> Log a message with ignore log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @param tr log this TerminationRecord
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
        ignore(message,(Throwable) t);
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
            log=null;
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
            log=null;
        }
    }

    /**
     * <p> Log an error with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     */
    public void err(Object message){
        Throwable thrNull = null;
        err (message, (Throwable)thrNull);
    }

    /**
     * <p> Log an error with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void err(Object message, Throwable t){
        message = getLogMessageIfNull(message, t);
        try {
            if (isCurrentLevelEnabled(localLog)) {
                if ((localLog!=null)&&(localLog instanceof LogMessage)) {
                    ((LogMessage)localLog).err(message, t);
                    if (Logger.logStackTrace){
                      if (t != null){t.printStackTrace();}
                    }
                } else {
                    System.err.println(message.toString());
                    if (t != null){t.printStackTrace();}
                }
            }
        }catch (Throwable thr){
            if (localLog!=null)
                     localLog.trace("",thr);
             else thr.printStackTrace();
        }
        //Registered logs
        synchronized (registeredLogs){
            log=null;
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
            log=null;
        }
    }


    /**
     * <p> Log a message with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     * @param t log this cause
     * @param tr log this TerminationRecord
     */
    public void err(Object message, SmartFrogException t, TerminationRecord tr){

       err(message.toString()+"\n "+ LogUtils.stringify(tr), LogUtils.extractCause(t, tr));
    }


    /**
     * <p> Log an error with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void err(Object message, SmartFrogException t){
      err(message,(Throwable)t);
    }


    /**
     * <p> Log a message with trace log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @param tr log this TerminationRecord
     */
    public void trace(Object message, SmartFrogException t, TerminationRecord tr){
        trace(message + "\n " + LogUtils.stringify(tr), LogUtils.extractCause(t, tr));
    }


    /**
     * <p> Log an error with trace log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void trace(Object message, SmartFrogException t){
        trace(message,(Throwable)t);
    }


    /**
     * <p> Log a message with debug log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @param tr log this TerminationRecord
     */
    public void debug(Object message, SmartFrogException t, TerminationRecord tr){
        debug(message + "\n " + LogUtils.stringify(tr), LogUtils.extractCause(t, tr));
    }


    /**
     * <p> Log an error with debug log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void debug(Object message, SmartFrogException t){
        debug(message,(Throwable)t);
    }


    /**
     * <p> Log a message with info log level. </p>
     *
     * @param message log this message
     * @param cause log this cause. Can be null, in which case any cause in the termination record is used
     * @param tr log the TerminationRecord
     */
    public void info(Object message, SmartFrogException cause, TerminationRecord tr){
        info(message + LogUtils.stringify(tr), LogUtils.extractCause(cause, tr));
    }


    /**
     * <p> Log an error with info log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void info(Object message, SmartFrogException t){
        info(message,(Throwable)t);
    }


    /**
     * <p> Log a message with warn log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @param tr log this TerminationRecord
     */
    public void warn(Object message, SmartFrogException t, TerminationRecord tr){
        warn(message + "\n " + LogUtils.stringify(tr), LogUtils.extractCause(t, tr));
    }


    /**
     * <p> Log an error with warn log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void warn(Object message, SmartFrogException t){
        warn(message,(Throwable)t);
    }


    /**
     * <p> Log a message with error log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @param tr log this TerminationRecord
     */
    public void error(Object message, SmartFrogException t, TerminationRecord tr){
        error(message + "\n " + LogUtils.stringify(tr), LogUtils.extractCause(t, tr));
    }


    /**
     * <p> Log an error with error log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void error(Object message, SmartFrogException t){
        error(message,(Throwable)t);
    }


    /**
     * <p> Log a message with fatal log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @param tr log this TerminationRecord
     */
    public void fatal(Object message, SmartFrogException t, TerminationRecord tr){
        fatal(message,(Throwable)t);
    }


    /**
     * <p> Log an error with fatal log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void fatal(Object message, SmartFrogException t){
        fatal(message,(Throwable)t);
    }


    /**
     * Log Registration interface
     *
     * @param name log name
     * @param logger logger to register
     * @throws SmartFrogLogException  if failed to register
     * @throws RemoteException in case of remote/network error
     */
    public void register(String name,Log logger)  throws SmartFrogLogException , RemoteException{
        try {
            registeredLogs.put(logName+"."+name, logger);
        } catch (Exception ex) {
            throw (SmartFrogLogException)SmartFrogLogException.forward(ex);
        }
    }

    /**
     *  Log Registration interface
     * @param name log name
     * @param logger logger to register
     * @param logLevel  log level
     * @throws RemoteException in case of remote/network error
     * @throws SmartFrogLogException if failed to register
     */
   public void register(String name,Log logger, int logLevel)  throws RemoteException, SmartFrogLogException{
       register(name,logger);
       if (currentLogLevel>=logLevel){
         currentLogLevel=logLevel;
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

    /**
     * Get a list of all registered logs
     *
     * @return a list (may be of size 0 for no logs)
     * @throws java.rmi.RemoteException
     * @throws org.smartfrog.sfcore.common.SmartFrogLogException
     *
     */
    public Log[] listRegisteredLogs() throws RemoteException, SmartFrogLogException {
        return  (Log[]) registeredLogs.values().toArray(new Log[0]);
    }
}


