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

package org.smartfrog;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Properties;
import java.util.StringTokenizer;


import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.OptionSet;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.security.SFSecurity;
import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.security.*;


/**
 * SFSystem offers utility methods to deploy from a stream and a deployer.  It
 * attempts to deploy and start the component found in the sfConfig attribute.
 * Any failure will cause a termination of the component under deployment or
 * starting.  The main function looks for a property and configuration option
 * on the argument line and deploys locally based on these.
 *
 * <P>
 * You can create your own main loop, using the utility methods in this class.
 * The main loop of SFSystem reads an optionset, checks if -c or /? missing to
 * pring usage string (and exit). It then reads the system properties, and
 * does a deployFromURLs given the URLs on the command line. Any exception or
 * error causes the main loop to do an exit. If the /e option is present on
 * the command line, the main loop exits after deployment. This is good for
 * one shot deployment, with deployment occurring into other processes.
 * </p>
 */
public class SFSystem implements MessageKeys {

    /** A flag that ensures only one system initialization. */
    private static boolean alreadySystemInit = false;

    /**
     * Base for all smartfrog properties. All properties looked up by classes
     * in SmartFrog use this as a base, add the package name and then the
     * property id to look up
     */
    public static final String propBase = "org.smartfrog.";

    /** Property name for class name for standard output stream. */
    public static final String propOutStreamClass = propBase +
        "outStreamClass";

    /** Property name for class name for standard error stream. */
    public static final String propErrStreamClass = propBase +
        "errStreamClass";

    /** Property name for ini file to read at start-up. */
    public static final String iniFile = propBase + "iniFile";


    /** Property name for logging stackTrace during exceptions. */
    public static final String propLogStackTrace = propBase +
        "logger.logStackTrace";

    /**
     * value of the errror code returned during a failed exit
     */
    private static final int EXIT_ERROR_CODE = -1;

    /**
     * Parses and deploys "sfConfig" from a stream to the target process
     * compound rethrows an exception if it fails, after trying to clean up.
     *
     * @param is input stream to parse
     * @param target the target process compound to request deployment
     * @param c a context of additional attributes that should be set before
     *        deployment
     * @param language the language whose parser to use
     * @return Reference to deployed component
     *
     * @exception SmartFrogException failure in some part of the process
     * @throws RemoteException In case of network/rmi error
     */
    public static Prim deployFrom(InputStream is, ProcessCompound target,
        Context c, String language) throws SmartFrogException, RemoteException {
        Prim comp = null;
        Phases top;
        //To calculate how long it takes to deploy a description
        long deployTime = 0;
        long parseTime = 0;
        if (Logger.logStackTrace) {
            deployTime = System.currentTimeMillis();
        }
        try {
            top = new SFParser(language).sfParse(is);
        } catch (SmartFrogException sfex){
           throw sfex;
        } catch (Throwable thr) {
            throw new SmartFrogException(MessageUtil.
                    formatMessage(MSG_ERR_PARSE), thr);
        }
        try {
            top = top.sfResolvePhases();
        } catch (SmartFrogException sfex){
           throw sfex;
        } catch (Throwable thr) {
            throw new SmartFrogException(MessageUtil.
                    formatMessage(MSG_ERR_RESOLVE_PHASE), thr);
        }
        try {
            ComponentDescription cd = top.sfAsComponentDescription();
            if (Logger.logStackTrace) {
                parseTime = System.currentTimeMillis() - deployTime;
                deployTime = System.currentTimeMillis();
            }
            comp = target.sfDeployComponentDescription(null, null, cd, c);
            try {
                comp.sfDeploy();
            } catch (Throwable thr){
                if (thr instanceof SmartFrogLifecycleException)
                    throw (SmartFrogLifecycleException) SmartFrogLifecycleException.forward(thr);
                throw SmartFrogLifecycleException.sfDeploy("",thr,null);
            }
            try {
                comp.sfStart();
            } catch (Throwable thr){
                if (thr instanceof SmartFrogLifecycleException)
                    throw (SmartFrogLifecycleException)SmartFrogLifecycleException.forward(thr);
                throw SmartFrogLifecycleException.sfStart("",thr,null);
            }
        } catch (Throwable e) {
               if (comp != null) {
                  Reference compName = null;
                  try {
                      compName = comp.sfCompleteName();
                  }
                  catch (Exception ex) {
                  }
                  try {
                  comp.sfTerminate(TerminationRecord.
                           abnormal("Deployment Failure: " +
                                e, compName));
                  } catch (Exception ex) {}
               }
               throw ((SmartFrogException) SmartFrogException.forward(e));
      }

      if (Logger.logStackTrace) {
          deployTime = System.currentTimeMillis()-deployTime;
          try {
              comp.sfAddAttribute("sfParseTime",new Long(parseTime));
              comp.sfAddAttribute("sfDeployTime",new Long(deployTime));
          } catch (Exception ex){
            //ignored, this is only information
          }
      }
      return comp;
    }

/*
    if (Logger.logStackTrace) {
        deployTime = System.currentTimeMillis()-deployTime;
        Logger.log("     * "+comp.sfCompleteName()
                   +" parsed in "+parseTime+" msecs."
                   +" deployed in "+deployTime+" msecs.");
        try {
            comp.sfAddAttribute("sfParseTime",new Long(parseTime));
            comp.sfAddAttribute("sfDeployTime",new Long(deployTime));
        } catch (Exception ex){
          //ignored, this is only information
        }
    }
*/



    /**
     * Deploy a single application URL.
     * @param appName name of the application
     * @param target the target process compound to request deployment
     * @throws SmartFrogException something went wrong with the deploy -this may contain a nested exception
     * @throws RemoteException if anything went wrong over the net
     */
    public static Prim deployFromURL(String url, String appName, ProcessCompound target)
        throws SmartFrogException, RemoteException {

        /* @Todo there is almost no difference between this method and
        * #deployFromURLsGiven; the latter could
        * have its core replaced by this with some work.*/

        Prim deployedApp = null;
        Context nameContext = null;
        nameContext = new ContextImpl();
        nameContext.put("sfProcessComponentName", appName);

        deployedApp = deployFromURL(url, appName, target, nameContext);
        return deployedApp;

    }

    private static Prim deployFromURL(String url, String appName, ProcessCompound target, Context nameContext) throws SmartFrogException, RemoteException {

        Prim deployedApp =  null;

        InputStream is=null;
        try {
            //assumes that the URL refers to stuff on the classpath
            is = SFClassLoader.getResourceAsStream(url);

            if (is == null) {
                throw new SmartFrogDeploymentException(MessageUtil.
                        formatMessage(MSG_URL_NOT_FOUND, url, appName));
            }
            deployedApp = deployFrom(is, target, nameContext, SFParser.getLanguageFromUrl(url));
        } catch (SmartFrogException sfex) {
            sfex.put("URL:", url);
            sfex.put("Component Name:", appName);
            throw sfex;
        } catch (RemoteException ex) {
            //rethrow
            throw ex;
        } catch (Exception ex) {
            //anything that was not dealt with gets wrapped
            throw new SmartFrogException(ex);
        } finally {
            if(is!=null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                    //TODO
                }
            }
        }
        return deployedApp;
    }





    /**
     * Entry point to get system properties. Works around a bug in some JVM's
     * (ie. Solaris) to return the default correctly.
     *
     * @param key property key to look up
     * @param def default to return if key not present
     *
     * @return property value under key or default if not present
     */
    public static String getProperty(String key, String def) {
        String res = System.getProperty(key, def);

        if (res == null) {
            return res = def;
        }

        return res;
    }

    /**
     * Common entry point to get system properties.
     *
     * @param key key to look up
     *
     * @return property value under key
     */
    public static String getProperty(String key) {
        return System.getProperty(key);
    }

    /**
     * Reads properties given a system property "org.smartfrog.iniFile".
     *
     * @throws SmartFrogException if failed to read properties from the
     * ini file
     */
    public static void readPropertiesFromIniFile() throws SmartFrogException {
        String source = System.getProperty(iniFile);
        if (source != null) {
            InputStream iniFileStream = getInputStreamForResource(source);
            try {
                readPropertiesFrom(iniFileStream);
            }
            catch (IOException ioEx) {
                throw new SmartFrogException(ioEx);
            }
        }
    }

    /**
     * Reads and sets system properties given in input stream.
     *
     * @param is input stream
     *
     * @exception IOException failed to read properties
     */
    public static void readPropertiesFrom(InputStream is)
        throws IOException {
        Properties props = new Properties();
        props.load(is);

        Properties sysProps = System.getProperties();

        for (Enumeration e = props.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            sysProps.put(key, props.get(key));
        }

        System.setProperties(sysProps);
    }


    /**
     * Reads System property "org.smartfrog.logger.logStrackTrace" and
     * updates Logger with the value to enable stack tracing.
     */
    public static void readPropertyLogStackTrace() {
        String source = System.getProperty(propLogStackTrace);
        if ((source != null)&&(source.equals("true"))) {
            Logger.logStackTrace = true;
            Logger.log(MessageUtil.
                    formatMessage(MSG_WARNING_STACKTRACE_ENABLED));
        }
    }


    /**
     * Sets stdout and stderr streams to different streams if class names
     * specified in system properties. Uses System.setErr and setOut to set
     * the <b>PrintStream</b>s which form stderr and stdout
     *
     * @exception Exception failed to create or set output/error streams
     */
    public static void setOutputStreams() throws Exception {
        String outClass = SFSystem.getProperty(propOutStreamClass);
        String errClass = SFSystem.getProperty(propErrStreamClass);

        if (errClass != null) {
            System.setErr((PrintStream) SFClassLoader.forName(errClass)
                                                     .newInstance());
        }

        if (outClass != null) {
            System.setOut((PrintStream) SFClassLoader.forName(outClass)
                                                     .newInstance());
        }
    }


    /**
     * Select target process compound using host and subprocess names
     *
     * @param  host host name. If null, assumes localhost.
     * @param  subProcess subProcess name (optional; can be null)
     *
     * @return ProcessCompound the target process compound
     *
     * @throws SmartFrogException In case of SmartFrog system error
     */
    public static ProcessCompound selectTargetProcess(String host, String subProcess)
        throws SmartFrogException, RemoteException {
        ProcessCompound target = null;
        try {
            target = SFProcess.getProcessCompound();
            if (host!=null) {
                    target = SFProcess.getRootLocator().
                        getRootProcessCompound(InetAddress.getByName(host));
            }
            if (subProcess!=null) {
                target = (ProcessCompound)target.sfResolveHere(subProcess);
            }
        } catch (Exception ex) {
                throw SmartFrogException.forward(ex);
        }
        return target;
    }




    /**
     * Gets the ProcessCompound running on the host.
     * @param hostName Name of the host
     * @param remoteHost boolean indicating if the host is remote host
     * @return ProcessCompound
     */
    public static ProcessCompound getTargetProcessCompound(String hostName,
            boolean remoteHost) throws SmartFrogException, RemoteException {
        ProcessCompound target = null;
        try {
            if (!remoteHost) {
                target = SFProcess.getProcessCompound();
            }else {
               target = SFProcess.getRootLocator().
                   getRootProcessCompound(InetAddress.getByName(hostName));
            }
        }catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }
        return target;
    }

    /**
     * Prints given error string and exits system.
     *
     * @param str string to print on out
     */
    public static void exitWith(String str) {
        if (str != null) {
            System.err.println(str);
        }
        exitWithError();
    }

    /**
     * Exits from the system.
     */
    private static void exitWithError() {
        System.exit(EXIT_ERROR_CODE);
    }

    /**
     * exit with an error code that depends on the status of the execution
     *
     * @param somethingFailed flag to indicate trouble
     */
    private static void exitWithStatus(boolean somethingFailed) {
        if(somethingFailed) {
            exitWithError();
        } else {
            System.exit(0);
        }
    }


    /**
     * Shows the version info of the SmartFrog system.
     */
    private static void showVersionInfo(){
        System.out.println(Version.versionString);
        System.out.println(Version.copyright);
    }


    public static void runConfigurationDescriptors (Vector cfgDescs) {
        if (cfgDescs==null) return;
        for (Enumeration items = cfgDescs.elements(); items.hasMoreElements();) {
           runConfigurationDescriptor((ConfigurationDescriptor)items.nextElement());
        }
    }

    public static Object runConfigurationDescriptor (ConfigurationDescriptor cfgDesc) {
        try {
            //return runConfigurationDescriptor(cfgDesc, false);
            return runConfigurationDescriptor(cfgDesc, false);
        } catch (SmartFrogException ex) {
            Logger.logQuietly(ex);
        }
        return null;
    }


    public static Object runConfigurationDescriptor(ConfigurationDescriptor configuration,
                                                    boolean throwException) throws SmartFrogException {
        //return runConfigurationDescriptorOld(configuration,throwException);
        return runConfigurationDescriptorNew(configuration, throwException);

    }
    /**
     * run whatever action is configured
     * @param configuration
     * @param throwException
     * @return
     * @throws SmartFrogException
     */
    public static Object runConfigurationDescriptorNew(ConfigurationDescriptor configuration,
                                                    boolean throwException) throws SmartFrogException {

        try {
            initSystem();
            Object targetC=configuration.execute(null);
            return targetC;

        } catch (Throwable thrown) {
            if (configuration.resultException == null) {
                configuration.setResult(ConfigurationDescriptor.Result.FAILED, null,
                        thrown);
            } else {
                Logger.logQuietly(thrown);
            }
            if (throwException) {
                throw SmartFrogException.forward(thrown);
            }
        }
        return configuration;
    }

    /**
     * this is the old configuration descriptor runner
     * @param cfgDesc
     * @param throwException
     * @return
     * @throws SmartFrogException
     */
    public static Object runConfigurationDescriptorOld(
        ConfigurationDescriptor cfgDesc,
        boolean throwException) throws SmartFrogException {

        ProcessCompound targetP=null;
        Prim targetC = null;
        boolean isRootProcess = false;
        try {
            initSystem();
            targetP = selectTargetProcess(cfgDesc.host,cfgDesc.subProcess);
            // name has different meaning for DEPLOY
            if (cfgDesc.getActionType()!=ConfigurationDescriptor.Action.DEPLOY) {

                targetC = (Prim)targetP.sfResolveWithParser(cfgDesc.name);

                if (targetC instanceof ProcessCompound) {
                    if (((ProcessCompound)targetC).sfIsRoot()) {
                        isRootProcess = true;
                    }
                }
            }
            switch (cfgDesc.getActionType()) {
                case (ConfigurationDescriptor.Action.DEPLOY):
                    Prim prim = deployFromURL(cfgDesc.url, cfgDesc.name, targetP);
                    //Logger.log(MessageUtil.
                    //           formatMessage(MSG_DEPLOY_SUCCESS, prim.sfCompleteName()+", "+
                    //          "\n     using: " +cfgDesc.url));
                    cfgDesc.setSuccessfulResult();
                    return prim;

                case ConfigurationDescriptor.Action.DETACH:
                    {
                        String name = targetC.sfCompleteName().toString();
                        targetC.sfDetach();
                        cfgDesc.setSuccessfulResult();
                        //Logger.log("- Detached: "+name
                        //           +",\n     now:"+targetC.sfCompleteName());
                    }
                    return targetC;

                case ConfigurationDescriptor.Action.DETaTERM:
                    {
                    String name = targetC.sfCompleteName().toString();
                    targetC.sfDetachAndTerminate(new TerminationRecord(
                            TerminationRecord.NORMAL,
                            "External Management Action",
                            targetP.sfCompleteName()));
                    cfgDesc.setSuccessfulResult();
                    //Logger.log("- DetachAndTerminated: "+name);
                    }
                    break;
                case ConfigurationDescriptor.Action.TERMINATE:
                   {
                       try {
                           String name = targetC.sfCompleteName().toString();
                           targetC.sfTerminate(new TerminationRecord(
                               TerminationRecord.NORMAL,
                               "External Management Action",
                               targetP.sfCompleteName()));
                        //Logger.log("- Terminated: "+name);
                            cfgDesc.setSuccessfulResult();
                       } catch (Exception ex) {
                           if (!isRootProcess)
                               throw ex;
                           //TODO: Check exception handling
                           if ((ex.getCause()instanceof java.net.SocketException)||
                               (ex.getCause()instanceof java.io.EOFException)) {
                               Logger.log(MessageUtil.formatMessage(
                                   MSG_SF_TERMINATED));
                               cfgDesc.setSuccessfulResult();
                           } else {
                               Logger.log(ex);
                           }
                       }
                   }
                    break;
                default:
                    throw new SmartFrogInitException("Unknown Action in: "+ cfgDesc.toString());
            }

        } catch (Throwable thr){
            if (cfgDesc.resultException ==null) {
                cfgDesc.setResult(ConfigurationDescriptor.Result.FAILED,null,thr);
                //@Todo Improve error message!
                //Logger.log( thr +"\n - ConfigurationDescriptor:" +cfgDesc.toString()+"\n");
            } else {
                Logger.logQuietly(thr);
            }
            if (throwException) {
                throw SmartFrogException.forward(thr);
            }
        }
        return cfgDesc;
    }


    /**
     * Method invoked to start the SmartFrog system.
     *
     * @param args command line arguments. Please see the usage to get more
     * details
     */
    public static void main(String[] args) {

        ProcessCompound rootProcess = null;

        showVersionInfo();

        OptionSet opts = new OptionSet(args);

        if (opts.errorString != null) {
            Logger.log(opts.errorString);
            exitWithError();
        }
        try {

            rootProcess = runSmartFrog(opts.cfgDescriptors);

        } catch (SmartFrogException sfex) {
            Logger.log(sfex);
            exitWithError();
        } catch (UnknownHostException uhex) {
            Logger.log(MessageUtil.formatMessage(MSG_UNKNOWN_HOST, opts.host), uhex);
            exitWithError();
        } catch (ConnectException cex) {
            Logger.log(MessageUtil.formatMessage(MSG_CONNECT_ERR, opts.host), cex);
            exitWithError();
        } catch (RemoteException rmiEx) {
            // log stack trace
            Logger.log(MessageUtil.formatMessage(MSG_REMOTE_CONNECT_ERR,
                    opts.host), rmiEx);
            exitWithError();
        } catch (Exception ex) {
            //log stack trace
            Logger.log(MessageUtil.
                    formatMessage(MSG_UNHANDLED_EXCEPTION), ex);
            exitWithError();
        }

        //Report Actions successes of failures.
         boolean somethingFailed = false;
         ConfigurationDescriptor cfgDesc = null;
         for (Enumeration items = opts.cfgDescriptors.elements();
              items.hasMoreElements(); ) {
             cfgDesc = (ConfigurationDescriptor)items.nextElement();
             if (cfgDesc.getResultType()==ConfigurationDescriptor.Result.FAILED) {
                 somethingFailed = true;
             }
             Logger.log(" - "+(cfgDesc).statusString()+"\n");
         }
        // Check for exit flag
        if (opts.exit) {
            exitWithStatus(somethingFailed);
        } else {
            //Logger.log(MessageUtil.formatMessage(MSG_SF_READY));
            if (Logger.logStackTrace) {
                String name = "";
                try {
                    if (rootProcess != null) {
                        name = rootProcess.sfResolve(SmartFrogCoreKeys.SF_PROCESS_NAME, name, false);
                    }
                } catch (Exception ex) {
                    //ignore.
                }
                Logger.log(MessageUtil.formatMessage(MSG_SF_READY, "[" + name + "]") + " " + new Date(System.currentTimeMillis()));
            } else {
                Logger.log(MessageUtil.formatMessage(MSG_SF_READY, ""));
            }
        }
    }

    /**
     * Run SmartFrog as configured. This call does not exit smartfrog, even if the OptionSet requests it.
     * This entry point exists so that alternate entry points (e.g. Ant Tasks) can start the system.
     * Important: things like the output streams can be redirected if the
     * @param  cfgDescriptors Vector of Configuration  opts with list of ConfigurationDescriptors
     *         @see ConfigurationDescriptor
     * @return the root process
     * @throws SmartFrogException for a specific SmartFrog problem
     * @throws UnknownHostException if the target host is unknown
     * @throws ConnectException if the remote system's SmartFrog daemon is unreachable
     * @throws RemoteException if something goes wrong during the communication
     * @throws Exception if anything else went wrong
     */

    public static ProcessCompound runSmartFrog(Vector cfgDescriptors) throws
        Exception {
        ProcessCompound rootProcess;
        rootProcess = runSmartFrog();
        if (cfgDescriptors!=null){
            runConfigurationDescriptors(cfgDescriptors);
        }
        return rootProcess;
    }


    /**
     * Run SmartFrog as configured. This call does not exit smartfrog, even if the OptionSet requests it.
     * This entry point exists so that alternate entry points (e.g. Ant Tasks) can start the system.
     * Important: things like the output streams can be redirected if the
     * @return the root process
     * @throws SmartFrogException for a specific SmartFrog problem
     * @throws UnknownHostException if the target host is unknown
     * @throws ConnectException if the remote system's SmartFrog daemon is unreachable
     * @throws RemoteException if something goes wrong during the communication
     * @throws Exception if anything else went wrong
     */
    public static ProcessCompound runSmartFrog()
            throws SmartFrogException, UnknownHostException, ConnectException, RemoteException, Exception {

        ProcessCompound process = null;

        initSystem();

        // Redirect output streams
        setOutputStreams();

        // Deploy process Compound
        process = SFProcess.deployProcessCompound();

        // Add boot time
        try {
            process.sfAddAttribute(SmartFrogCoreKeys.SF_BOOT_DATE, new Date(System.currentTimeMillis()));
        } catch (RemoteException swallowed) {
        }

        return process;
    }

    synchronized public static void initSystem() throws SmartFrogException,
        SFGeneralSecurityException {

        if (!alreadySystemInit) {
            // Initialize SmartFrog Security
            SFSecurity.initSecurity();

            // Read init properties
            readPropertiesFromIniFile();

            // Set stackTracing
            readPropertyLogStackTrace();

            alreadySystemInit = true;
        }
    }

    /**
     * Gets input stream for the given resource. Throws exception if stream is
     * null.
     * @param resource Name of the resource
     * @return Input stream for the resource
     * @throws SmartFrogException if input stream could not be created for the
     * resource
     * @see SFClassLoader
     */
    public static InputStream getInputStreamForResource(String resource)
                                                throws SmartFrogException{
        InputStream  is = null;
        is = SFClassLoader.getResourceAsStream(resource);
        if(is == null) {
            throw new SmartFrogException(MessageUtil.
                    formatMessage(MSG_FILE_NOT_FOUND, resource));
        }
        return is;
    }


}
