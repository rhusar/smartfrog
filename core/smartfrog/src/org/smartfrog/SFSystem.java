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
import java.util.Properties;

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

    private SFSystem(){
    }

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
     *
     * @exception SmartFrogException failure in some part of the process
     * @throws RemoteException In case of network/rmi error
     */
    public static void deployFrom(InputStream is, ProcessCompound target,
        Context c, String language) throws SmartFrogException, RemoteException {
        Prim comp = null;
        Context nameContext = null;
        Phases top;
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
            comp = target.sfDeployComponentDescription(null, null, cd, c);
            try {
                comp.sfDeploy();
            } catch (Throwable thr){
                if (thr instanceof SmartFrogLifecycleException) throw (SmartFrogLifecycleException)SmartFrogLifecycleException.forward(thr);
                throw SmartFrogLifecycleException.sfDeploy("",thr,null);
            }
            try {
                comp.sfStart();
            } catch (Throwable thr){
                if (thr instanceof SmartFrogLifecycleException) throw (SmartFrogLifecycleException)SmartFrogLifecycleException.forward(thr);
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

    }

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
            try {
                deployFrom(SFClassLoader.getResourceAsStream(source), target, null,
                           getLanguageFromUrl(source));
            } catch (SmartFrogParseException sfpex){
                sfpex.add(sfpex.DATA, " URL '"+source+"'");
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
     */
    public static void deployFromURLsGiven(OptionSet opts,
        ProcessCompound target) {
        ComponentDescription comp = null;
        Context nameContext = null;
        Enumeration names = opts.names.elements();
        String url = "";
        String name = "";
        //To calculate how long it takes to deploy a description
        long deployTime=0;

        for (Enumeration e = opts.configs.elements(); e.hasMoreElements();) {
            if (org.smartfrog.sfcore.common.Logger.logStackTrace) {
              deployTime = System.currentTimeMillis();
            }
            url = (String) e.nextElement();
            name = (String) names.nextElement();

            if (name != null) {
                nameContext = new ContextImpl();
                nameContext.put("sfProcessComponentName", name);
            }

            try {
                InputStream is = SFClassLoader.getResourceAsStream(url);

                if (is == null) {
                    errorDeploy = true;
                    // Log the message and contnue with next deployment
                    Logger.log(MessageUtil.
                            formatMessage(MSG_URL_NOT_FOUND, url, name));
                    if(e.hasMoreElements()) {
                        Logger.log(MessageUtil.
                                    formatMessage(MSG_CONT_OTHER_DEPLOY));
                    }
                    continue;
                }
                deployFrom(is, target, nameContext, getLanguageFromUrl(url));
               if (org.smartfrog.sfcore.common.Logger.logStackTrace) {
                  deployTime=System.currentTimeMillis()-deployTime;
                  org.smartfrog.sfcore.common.Logger.log("  - "+name +" ("+url +")"+ " deployed in "+ deployTime + " millisecs.");
               }
            } catch (SmartFrogException sfex) {
                errorDeploy = true;
                sfex.put("URL:", url);
                sfex.put("Component Name:", name);
                Logger.log(MessageUtil.formatMessage(MSG_ERR_DEPLOY_FROM_URL,
                        url, name), sfex);
                if(e.hasMoreElements()) {
                        Logger.log(MessageUtil.
                                    formatMessage(MSG_CONT_OTHER_DEPLOY));
                }
            } catch (Exception ex) {
                errorDeploy = true;
                Logger.log(MessageUtil.formatMessage(MSG_ERR_DEPLOY_FROM_URL,
                        url, name), ex);
                if(e.hasMoreElements()) {
                        Logger.log(MessageUtil.
                                    formatMessage(MSG_CONT_OTHER_DEPLOY));
                }
            }
        }
    }

    /**
     * Terminates the named applications given as -t options on the command
     * line.
     *
     * @param opts the option set to use
     * @param target the target process compound to request the terminations
     */
    public static void terminateNamedApplications(OptionSet opts,
        ProcessCompound target) {
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
                        sfTerminate(new TerminationRecord(
                        "management action", "forced to terminate", targetName));
            } catch (ClassCastException cce) {
                errorTermination  = true;
                try {
                    if (term.equals("rootProcess")) {
                        ((Prim) target.sfResolve((Reference) target.
                               sfResolveHere(term))).
                                sfTerminate(new TerminationRecord(
                                "management action",
                                "sfDaemon forced to terminate ", targetName));
                    }
                } catch (Exception ex) {
                    //TODO: Check exception handling
                    if ((ex.getCause() instanceof java.net.SocketException)){
                       Logger.log(MessageUtil.formatMessage(MSG_SF_TERMINATED));
                    } else {
                        Logger.log(ex);
                    }
                }
            } catch (Exception e) {
                errorTermination  = true;
                Logger.log(MessageUtil.formatMessage(MSG_ERR_TERM, term));
                // log stack trace
                Logger.log(e);
            }
        }
    }

    /**
     * Entry point to get system properties. Works around a bug in some JVM's
     * (ie. Solaris to return the default correctly.
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
     * @exception Exception failed to read properties
     */
    public static void readPropertiesFromIniFile() throws Exception {
        String source = System.getProperty(iniFile);
        if (source != null) {
            readPropertiesFrom(SFClassLoader.getResourceAsStream(source));
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
     * Prints given error string and exits system.
     *
     * @param str string to print on out
     */
    public static void exitWith(String str) {
        if (str != null) {
            System.err.println(str);
        }
        exit();
    }

    /**
     * Exits from the system.
     */
    private static void exit() {
        System.exit(1);
    }

    /**
     * Shows the version info of the SmartFrog system.
     */
    private static void showVersionInfo(){
        System.out.println(Version.versionString);
        System.out.println(Version.copyright);
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
            exit();
        }
        try {
            rootProcess = runSmartFrog(opts, null);

        } catch (SmartFrogException sfex) {
            Logger.log(sfex);
            exit();
        } catch (UnknownHostException uhex) {
            Logger.log(MessageUtil.formatMessage(MSG_UNKNOWN_HOST, opts.host),
                    uhex);
            exit();
        } catch (ConnectException cex) {
            Logger.log(MessageUtil.formatMessage(MSG_CONNECT_ERR, opts.host),
                    cex);
            exit();
        } catch (RemoteException rmiEx) {
            // log stack trace
            Logger.log(MessageUtil.formatMessage(MSG_REMOTE_CONNECT_ERR,
                    opts.host), rmiEx);
            exit();
        } catch (Exception ex) {
            //log stack trace
            Logger.log(MessageUtil.
                    formatMessage(MSG_UNHANDLED_EXCEPTION), ex);
            exit();
        }
        // Check for exit flag
        if (opts.exit) {
            if (opts.names.size() != 0 && !errorDeploy) {
                Logger.log(MessageUtil.
                        formatMessage(MSG_DEPLOY_SUCCESS, opts.names));
            }
            if (opts.terminations.size() != 0 && !errorTermination) {
                Logger.log(MessageUtil.
                        formatMessage(MSG_TERMINATE_SUCCESS, opts.terminations));
            }
            System.exit(0);
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
     * @return the root process
     * @throws SmartFrogException for a specific SmartFrog problem
     * @throws UnknownHostException if the target host is unknown
     * @throws ConnectException if the remote system's SmartFrog daemon is unreachable
     * @throws RemoteException if something goes wrong during the communication
     * @throws Exception if anything else went wrong
     */
    public static ProcessCompound runSmartFrog(OptionSet options, String iniFile)
            throws SmartFrogException, UnknownHostException, ConnectException, RemoteException, Exception {
        ProcessCompound process = null;
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

        terminateNamedApplications(options, targetPC);

        deployFromURLsGiven(options, targetPC);
        return process;
    }
}
