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

package org.smartfrog.sfcore.common;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;

/**
 * Class used to store some flags used for log reporting.
 */
public class Logger implements MessageKeys {

    private static Date  bootDate = null;

    /** Used as default value in diagnostics for remote host {@value} */
    public static final String SMARTFROG_URL = "http://www.smartfrog.org/";
    /** Used as default value in diagnostics for repeat jars {@value} */
    public static final String SMARTFROG_JAR = "smartfrog-";
    public static final String SFSERVICES_JAR = "sfServices";


    /** Property name for Logger class
     *  Value {@value}
     *  */
    public static final String loggerPropertyBase = "org.smartfrog.sfcore.common.Logger";

    //Configuration parameters
    /** String name for optional attribute "{@value}". */
    public static final String ATR_LOG_STACK_TRACE = "logStackTrace";
    /** String name for optional attribute "{@value}". */
    public static final String ATR_LOG_LIVENESS = "logLiveness";
    /** String name for optional attribute "{@value}". */
    public static final String ATR_LOG_CLASSPATH = "logClasspath";
    /** String name for optional attribute "{@value}". */
    public static final String ATR_LOG_PC_DIAG_REPORT = "processCompoundDiagReport";
    /** String name for optional attribute "{@value}". */
    public static final String ATR_TEST_NETWORK = "testNetwork";


    /** String name for optional attribute "{@value}". */
    public static final String ATR_TEST_URI = "testURI";

    /** String name for optional attribute "{@value}". */
    public static final String ATR_TEST_JAR_REPEAT = "testJarRepeat";

    /** Property to enable stack trace. The default value is overridden by the
     * value specified in default.ini file.
     */
    public static boolean logStackTrace = true;

    /** Property to enable sfPing log. The default value is overridden by the
     * value specified in default.ini file.
     */
    public static boolean logLiveness = false;

    /** Property to create a sfDiagnosticsReport in every ProcessCompound
     */
    public static boolean processCompoundDiagReport = false;

    /** Property to enable initial network test. The default value can be overridden by the
     * value specified in default.ini file.
     */
    public static boolean testNetwork = true;

    /** Property to define a list of remote hosts for remote network test . The default value can be overridden by the
      * value specified in default.ini file.
      */
    public static String[] testJarRepeat = {SMARTFROG_JAR, SFSERVICES_JAR};

    /** Property to define a list of remote hosts for remote network test . The default value can be overridden by the
      * value specified in default.ini file.
      */
    public static String[] testURI = {SMARTFROG_URL};

    private static boolean initialized = false;

    /** Property to define if to log classpaht during startup. The default value can be overridden by the
      * value specified in default.ini file.
      */
    public static boolean logClasspath = false;

    /**
     * empty constructor
     */
    private Logger(){        
    }

    /**
     * Init method
     */
    public static synchronized void  init() {
        if (initialized) return;

        getBootDate();

        ComponentDescription configuration = null;
        //Check Class and read configuration...including system.properties
        try {
            configuration = ComponentDescriptionImpl.getClassComponentDescription(loggerPropertyBase, true, null);
            if (configuration!=null){
               logStackTrace = configuration.sfResolve(ATR_LOG_STACK_TRACE,logStackTrace,false);
               logLiveness = configuration.sfResolve(ATR_LOG_LIVENESS,logLiveness,false);
               logClasspath = configuration.sfResolve(ATR_LOG_CLASSPATH,logClasspath,false);
               processCompoundDiagReport = configuration.sfResolve(ATR_LOG_PC_DIAG_REPORT,processCompoundDiagReport,false);
               testNetwork = configuration.sfResolve(ATR_TEST_NETWORK,testNetwork,false);
               testURI = configuration.sfResolve(ATR_TEST_URI,testURI,false);
               testJarRepeat = configuration.sfResolve(ATR_TEST_JAR_REPEAT,testJarRepeat,false);
            }
        } catch (Exception ex){
            if (SFSystem.sfLog().isErrorEnabled()) { SFSystem.sfLog().error(ex); }
        }
        
        initialized = true;
    }

    public static void logStatus() {
        if (logStackTrace&&(SFSystem.sfLog().isWarnEnabled())) {
              SFSystem.sfLog().warn(MessageUtil.formatMessage(MSG_WARNING_STACKTRACE_ENABLED));
        }

        if (logLiveness && (SFSystem.sfLog().isWarnEnabled())) {
          SFSystem.sfLog().warn(MessageUtil.formatMessage(MSG_WARNING_LIVENESS_ENABLED));
        }
        reportRepeatedJars();

    }

    public static Date getBootDate (){
        if (bootDate == null) bootDate = new Date(System.currentTimeMillis());;
        return bootDate;
    }    
    

    /**
     * Warn of repeated .jars
     */
    private static void reportRepeatedJars() {
        try {
            // Check for repeated Jar files in classpath
            String[] words = Logger.testJarRepeat;
            String[] classpath = (System.getProperty("java.class.path")).split(System.getProperty("path.separator"));
            StringBuffer message = Logger.getRepeatsMessage(words, classpath);
            if (message !=null) {
                SFSystem.sfLog().warn("Possible problem with classpath: \n"+message.toString());
            }
// No more codebase since OSGi integration. Replaced by classloading environments            
//            // Check for repeated Jar files in code base
//            String codebaseproperty = System.getProperty(SFClassLoader.SF_CODEBASE_PROPERTY);
//            if (codebaseproperty != null) {
//                String[] codebase = codebaseproperty.split(System.getProperty(
//                        "path.separator"));
//                message = Logger.getRepeatsMessage(words, codebase);
//                if (message != null) {
//                    SFSystem.sfLog()
//                            .warn("Possible problem with codebase: " + message.toString());
//                }
//            }
        } catch (Throwable thr) { /*ignore*/ }
    }

    /**
     * Return the intializaed status of Logger
     * @return boolean
     */
    public static boolean initialized(){
        return initialized;
    }

    public static StringBuffer getRepeatsMessage(String[] words,
                                                 String[] codebase) {
        String repeats;
        StringBuffer message = null;
        for (String word : words) {
            repeats = checkRepeatedWords(word, codebase);
            if (repeats != null) {
                if (message == null) {
                    message = new StringBuffer();
                }
                message.append("  " + repeats);
            }
        }
        return message;
    }


    /**
     *
     *  Method to search for repeted number of words in a String
     *
     * @param word Word to match
    *  @param content array of strings to search
     * @return resultMessage  list of lines where the word was found  or NULL 1 or less found.
     */
    public static String checkRepeatedWords (String word, String[] content) {
        String regex = word;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher("");         // Create Matcher
        StringBuffer strb = new StringBuffer();
        int count = 0;

        for (String line : content) {
            m.reset(line);
            while (m.find()) {
                strb.append("\n    "+line);
                count++;
            }
        }
        if (count >1) {
           return (count + " occurrences for "+word + strb.toString()+"\n");
        } else {
            return null;
        }
    }

}
