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

    /** Property name for initial sf file to read at start-up. */
    public static final String iniSFFile = propBase + "iniSFFile";

    /** flag indicating error while deployment */
    private static boolean errorDeploy = false;

    /** flag indicating error while termination */
    private static boolean errorTermination = false;


    /** Property name for logging stackTrace during exceptions. */
    public static final String propLogStackTrace = propBase +
        "logger.logStackTrace";

    /**
     * value of the errror code returned during a failed exit
     */
    private static final int EXIT_ERROR_CODE = -1;


    /**
     * Gets language grom the URL
     *
     * @param url URL passed to application
     *
     * @return Language string
     *
     * @throws SmartFrogException In case any error while getting the
     *         language string
     */
    private static String getLanguageFromUrl(String url)
        throws SmartFrogException {
        if (url == null) {
            throw new SmartFrogInitException(MessageUtil.formatMessage(
                    MSG_NULL_URL));
        }

        int i = url.lastIndexOf('.');

        if (i <= 0) {
            // i.e. it cannot contain no "." or start with the only "."
            throw new SmartFrogInitException(MessageUtil.formatMessage(
                    MSG_LANG_NOT_FOUND, url));
        } else {
            return url.substring(i + 1);
        }
    }

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
     * Reads the property "org.smartfrog.iniSFFile and deploys the
     * configuration in the current process.
     *
     * @exception SmartFrogException failure while reading or deploying
     * @throws RemoteException In case of network/rmi error
     */
    public static void deployFromIniSFFile()
        throws SmartFrogException, RemoteException {
        String source = System.getProperty(iniSFFile);

        if (source != null) {
            ProcessCompound target = SFProcess.getProcessCompound();
            InputStream iniFileStream =  getInputStreamForResource(source);
            String lang = getLanguageFromUrl(source);
            try {
                deployFrom(iniFileStream, target, null, lang);
            } catch (SmartFrogParseException sfpex){
                sfpex.add(SmartFrogParseException.DATA, " URL '"+source+"'");
                throw sfpex;
            }
        }
    }

    /**
     * Deploys from a sequence of URLS if the option -c was given. Each
     * deployment is treated independantly and errors in one do not affect the
     * others.
     *
     * @param opts option set to use
     * @param target the target process compound to request deployment
     * @return the number of components successfully deployed.
     */
    public static int deployFromURLsGiven(OptionSet opts,
                                           ProcessCompound target) {
        Context nameContext = null;
        Enumeration names = opts.names.elements();
        String url = "";
        String name = "";
        //number of anonymous deploys
        int counter = 0;
        int successfulDeploys=0;
        //so far so good
        errorDeploy = false;
        for (Enumeration e = opts.configs.elements(); e.hasMoreElements();) {
            url = (String) e.nextElement();
            name = (String) names.nextElement();
            if (name != null) {
                if (nameContext==null) {
                    nameContext = new ContextImpl();
                }
                nameContext.put("sfProcessComponentName", name);
            }

            try {
                deployFromURL(url, name, target, nameContext);
                successfulDeploys++;
            } catch (SmartFrogException sfex) {
                errorDeploy = true;
                sfex.put("URL:", url);
                sfex.put("Component Name:", name);
                Logger.log(MessageUtil.formatMessage(MSG_ERR_DEPLOY_FROM_URL,
                        url, name), sfex);
                if (e.hasMoreElements()) {
                    Logger.log(MessageUtil.
                            formatMessage(MSG_CONT_OTHER_DEPLOY));
                }
            } catch (Exception ex) {
                errorDeploy = true;
                Logger.log(MessageUtil.formatMessage(MSG_ERR_DEPLOY_FROM_URL,
                        url, name), ex);
                if (e.hasMoreElements()) {
                    Logger.log(MessageUtil.
                            formatMessage(MSG_CONT_OTHER_DEPLOY));
                }
            }
        }
        return successfulDeploys;
    }

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
            deployedApp = deployFrom(is, target, nameContext, getLanguageFromUrl(url));
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
     * Terminates the named applications given as -t options on the command
     * line.
     *
     * @param opts the option set to use
     * @param target the target process compound to request the terminations
     */
    public static int terminateNamedApplications(OptionSet opts,
        ProcessCompound target) {
        int terminateCount=0;
        for (Enumeration terms = opts.terminations.elements();
                terms.hasMoreElements();) {
            String term = (String) terms.nextElement();
            Reference targetName = null;
            try {
                targetName = (((Prim) target).sfCompleteName());
            } catch (Exception ex) {
                //ignore  //TODO: Check
            }

            try {
                ((Prim) target.sfResolveHere(term)).
                    sfTerminate(new TerminationRecord("normal",
                    "External Management Action", targetName));
            } catch (ClassCastException cce) {
                errorTermination = true;
                try {
                    if (term.equals("rootProcess")) {
                        ((Prim) target.sfResolve((Reference) target.
                                sfResolveHere(term))).
                                    sfTerminate(new TerminationRecord("normal",
                                    "External Management Action", targetName));
                    }
                } catch (Exception ex) {
                    //TODO: Check exception handling
                    if ((ex.getCause() instanceof java.net.SocketException) ||
                             (ex.getCause() instanceof java.io.EOFException)){
                       Logger.log(MessageUtil.formatMessage(MSG_SF_TERMINATED));
                    } else {
                        Logger.log(ex);
                    }
                }
                terminateCount++;
            } catch (Exception e) {
                errorTermination  = true;
                Logger.log(MessageUtil.formatMessage(MSG_ERR_TERM, term));
                // log stack trace
                Logger.log(e);
            }
        }
        return terminateCount;
    }


    /**
     * Terminates the named components given as -T options on the command
     * line.
     *
    *  @param opts option set up configured with SmartFrog Options
     * @param target the target process compound to request the terminations
     *
     */
    public static void terminateNamedComponents(OptionSet opts,
                    ProcessCompound target) {
            Prim obj = null;
            try {
                    obj = (Prim)target;
            } catch (Exception e) {
                errorTermination  = true;
                Logger.log(MessageUtil.formatMessage(MSG_ERR_TERM, target));
                // log stack trace
                Logger.log(e);
            }
            StringTokenizer st = null;
            String token = null;
            TerminationRecord tr = new TerminationRecord(TerminationRecord.NORMAL,
                         "force to terminate", null);
            for (Enumeration terms = opts.terminating.elements();
                terms.hasMoreElements();) {
                     String term = (String) terms.nextElement();
                     st = new StringTokenizer(term, ":");
                     try {
                             while (st.hasMoreTokens()) {
                                     token = st.nextToken();
                                     obj = ((Prim)obj.sfResolveHere(token));
                             }
                             obj.sfTerminate(tr);
                     } catch (Exception e) {
                             errorTermination  = true;
                             Logger.log(MessageUtil.formatMessage(
                                                       MSG_ERR_TERM,token));
                             // log stack trace
                             Logger.log(e);
                     }
            }
    }



    /**
     * Detaches and terminates the named components given as -d options on the
     * command line.
     *
    * @param opts option set up configured with SmartFrog Options
     * @param target the target process compound to request the terminations
     * @return number of detachments.
     */
    public static int detachAndTerminateNamedComponents(OptionSet opts,
                                                         ProcessCompound target) {
        //return early if there is nothing to detach
        if(opts.detaching.size()==0) {
            return 0;
        }
        int detachCount=0;
        Prim obj;
        try {
            obj = (Prim)target;
        } catch (Exception e) {
            errorTermination  = true;
            Logger.log(MessageUtil.formatMessage(MSG_ERR_TERM, target));
            // log stack trace
            Logger.log(e);
            //exit immediately at this point
            return detachCount;
        }

        StringTokenizer st = null;
        String token = null;

        TerminationRecord tr = new TerminationRecord(TerminationRecord.NORMAL,
                "External Management Action", null);
        for (Enumeration detachs = opts.detaching.elements();
             detachs.hasMoreElements();) {
            String detach = (String) detachs.nextElement();
            st = new StringTokenizer(detach, ":");
            try {
                while (st.hasMoreTokens()) {
                    token = st.nextToken();
                    obj = ((Prim)obj.sfResolveHere(token));
                }
                obj.sfDetachAndTerminate(tr);
                detachCount++;
            } catch (Exception e) {
                errorTermination  = true;
                Logger.log(MessageUtil.formatMessage(
                        MSG_ERR_TERM,token));
                // log stack trace
                Logger.log(e);
            }
        }
        return detachCount;
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
     * Use the -h and -p options, or lack of, to select target process compound
     * for the command line configuration requests.
     *
     * @param opts the set of parsed command line options
     *
     * @return the target process compound
     *
     * @throws Exception In case of SmartFrog system error
     */
    public static ProcessCompound selectTargetProcess(OptionSet opts)
        throws Exception {
        ProcessCompound target = SFProcess.getProcessCompound();

        if (opts.isRemoteDaemon) {
            target = SFProcess.getRootLocator().
                getRootProcessCompound(InetAddress.getByName(opts.host));

            if (opts.isRemoteSubprocess) {
                target = (ProcessCompound) target.sfResolveHere(opts.subprocess);
            }
        }

        return target;
    }

    /**
     * Select target process compound using host and subprocess names
     *
     * @param String host host name.
     * @param String subProcess subProcess name.
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
                SmartFrogException.forward(ex);
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
     * @param status
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
    /**
     * Bring up smartfrog and deploy a component on the specified host.
     * A useful little entry point for external programs and tests.
     * @param hostName host where to deploy
     * @param Url url to the component
     * @param appName application name
     * @param remoteHost host is a remote host or not
     * @return Reference to deployed application
     * @throws SmartFrogException if any application error
     * @throws RemoteException if any rmi or network error
     */
    public static Prim deployAComponent(String hostName, String Url,
            String appName, boolean remoteHost)
                    throws SmartFrogException, RemoteException {
        Prim deployedApp=null;
        try {
            ProcessCompound rootProcess=null;
            // Initialize security
            SFSecurity.initSecurity();
            // Read init properties
            readPropertiesFromIniFile();
            // Deploy process Compound
            rootProcess = SFProcess.deployProcessCompound();
           // Get the target process compound
            ProcessCompound target = getTargetProcessCompound(hostName,
                                                                remoteHost);
            deployedApp = deployFromURL(Url, appName, target);
        }catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }
        return deployedApp;
    }


    public static void runConfigurationDescriptors (Vector cfgDescs){
        if (cfgDescs==null) return;
        for (Enumeration items = cfgDescs.elements(); items.hasMoreElements();) {
           runConfigurationDescriptor((ConfigurationDescriptor)items.nextElement());
        }
    }

    public static Object runConfigurationDescriptor (ConfigurationDescriptor cfgDesc){
        ProcessCompound targetP=null;
        Prim targetC = null;
        boolean isRootProcess = false;
        try {
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
                    Context nameContext = null;
                    InputStream is = getInputStreamForResource(cfgDesc.url);
                    // if the application is named!
                    if (cfgDesc.name!=null) {
                        nameContext = new ContextImpl();
                        nameContext.put("sfProcessComponentName", cfgDesc.name);
                    }
                    Prim prim = deployFrom(is, targetP, nameContext, getLanguageFromUrl(cfgDesc.url));
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
                    targetC.sfDetachAndTerminate(new TerminationRecord("normal",
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
                           targetC.sfTerminate(new TerminationRecord("normal",
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
            } else Logger.logQuietly(thr);
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
        StatusInfo info = new StatusInfo();

        showVersionInfo();

        OptionSet opts = new OptionSet(args);

        if (opts.errorString != null) {
            Logger.log(opts.errorString);
            exitWithError();
        }
        try {
            rootProcess = runSmartFrog(opts, null, info);

        } catch (SmartFrogException sfex) {
            Logger.log(sfex);
            exitWithError();
        } catch (UnknownHostException uhex) {
            Logger.log(MessageUtil.formatMessage(MSG_UNKNOWN_HOST, opts.host),
                    uhex);
            exitWithError();
        } catch (ConnectException cex) {
            Logger.log(MessageUtil.formatMessage(MSG_CONNECT_ERR, opts.host),
                    cex);
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

        // Check for exit flag
        if (opts.exit) {
            //Report Actions successes of failures.
           boolean somethingFailed = false;
           ConfigurationDescriptor cfgDesc = null;
           for (Enumeration items = opts.cfgDescriptors.elements(); items.hasMoreElements();) {
                    cfgDesc = (ConfigurationDescriptor)items.nextElement();
                    if (cfgDesc.getResultType()== ConfigurationDescriptor.Result.FAILED) {
                      somethingFailed = true;
                    }
                    Logger.log(" - "+(cfgDesc).statusString()+"\n");
            }
// Messages not valid if using deploy ConfigurationDescriptors
//            if (opts.names.size() != 0 && !errorDeploy) {
//                Logger.log(MessageUtil.
//                        formatMessage(MSG_DEPLOY_SUCCESS, opts.names));
//            }
//            if (opts.terminations.size() != 0 && !errorTermination) {
//                Logger.log(MessageUtil.
//                        formatMessage(MSG_TERMINATE_SUCCESS, opts.terminations));
//            }
            exitWithStatus(somethingFailed);
        } else {
            //Logger.log(MessageUtil.formatMessage(MSG_SF_READY));
            if (Logger.logStackTrace) {
                String name = "";
                try {
                    if (rootProcess != null) {
                        name = rootProcess.sfResolve("sfProcessName", name, false);
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
     * @param options option set up configured with SmartFrog Options
     * @param iniFile optional initialisation file.
     * @param info status object for complex result reporting
     * @return the root process
     * @throws SmartFrogException for a specific SmartFrog problem
     * @throws UnknownHostException if the target host is unknown
     * @throws ConnectException if the remote system's SmartFrog daemon is unreachable
     * @throws RemoteException if something goes wrong during the communication
     * @throws Exception if anything else went wrong
     */
    public static ProcessCompound runSmartFrog(OptionSet options,
                                               String iniFile,
                                               StatusInfo info)
            throws SmartFrogException, UnknownHostException, ConnectException, RemoteException, Exception {
        ProcessCompound process = null;

        //copy the reference and make a new instance if the caller did not provide
        //one. This avoids lots of conditional checks in the code logging #of calls.
        StatusInfo status=info;
        if(status==null) {
            status = new StatusInfo();
        }

        // Initialize Smart Frog Security
        SFSecurity.initSecurity();

        // Read init properties
        readPropertiesFromIniFile();

        // Set stackTracing
        readPropertyLogStackTrace();

        // Redirect output streams
        setOutputStreams();
        // Deploy process Compound
        process = SFProcess.deployProcessCompound();

        // Add boot time
        try {
            process.sfAddAttribute("sfBootDate", new Date(System.currentTimeMillis()));
        } catch (RemoteException swallowed) {
        }

        deployFromIniSFFile();

        // get the target process compound
        ProcessCompound targetPC = selectTargetProcess(options);

        runConfigurationDescriptors(options.cfgDescriptors);

// First step in cleaning SFSystem. Check runConfigurationDescriptor method to
// replace all this ones.
//        status.detachRequests= options.detaching.size();
//        status.detachCount=detachAndTerminateNamedComponents(options, targetPC);
//
//        status.terminateRequests=options.terminations.size();
//        status.terminatedCount=terminateNamedApplications(options, targetPC);
//
//        status.deployRequests=options.configs.size();
//        status.deployedCount = deployFromURLsGiven(options, targetPC);

        return process;
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


    /**
     * a class used for status feedback
     */
    private static class StatusInfo {
        public int deployRequests;
        public int deployedCount;
        public int terminateRequests;
        public int terminatedCount;
        public int detachRequests;
        public int detachCount;
    }
}
