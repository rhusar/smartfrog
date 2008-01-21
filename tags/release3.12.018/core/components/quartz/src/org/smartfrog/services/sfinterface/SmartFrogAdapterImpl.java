package org.smartfrog.services.sfinterface;


import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.Reference;

import java.io.*;
import java.net.InetAddress;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.smartfrog.sfcore.security.*;
import org.smartfrog.sfcore.compound.CompoundImpl;

public class SmartFrogAdapterImpl implements SmartfrogAdapter {

    private java.text.SimpleDateFormat sdf;
    private Calendar cal;
    static ProcessCompound sfDaemon = null;
    private static String iniFile = null;
    private static String sfDefault = null;
    private static String sfDefaultSecurity = null;
    private boolean SFDYNAMICCLASSLOADING_ON = false;
    private String dyClassLoading_codebase = "";
    private static final String fileSeparator = File.separator;
    private static final String pathSeparator = System.getProperty("path.separator");
    private String distPath;
    private static long appCounter = 1;
    private static String logFilePath=null;


    /* Empty constructor - when Smartfrog Daemon is running in the local system */
    public SmartFrogAdapterImpl() throws Exception {
        String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        cal = Calendar.getInstance(TimeZone.getDefault());
        sdf = new SimpleDateFormat(DATE_FORMAT);
        try {  // there is a Daemon  running in local system
            sfDaemon = SFProcess.getRootLocator().getRootProcessCompound(null, 3800);
        } catch (ConnectException cEx) {  // there is no Daemon  running in local system
            throw new SFSubmitException("There is no Daemon  running in Local System");
        }
    }

    /* Constructor which takes SFHOME as argument */
    public SmartFrogAdapterImpl(String sfHomePath) throws Exception {
        setSFHOME(sfHomePath);
        try {
            String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
            cal = Calendar.getInstance(TimeZone.getDefault());
            sdf = new SimpleDateFormat(DATE_FORMAT);
            getSFDaemon();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        if (sfDaemon != null) {
            System.out.println("sfDaemon Ready");
        } else {
            System.out.println("sfDaemon NOT Ready. Something went wrong");
        }
    }

    // contacts a running daemon or starts a new one
    private ProcessCompound getSFDaemon() throws RemoteException, SFGeneralSecurityException, SmartFrogException, Exception {
        try {  // there is a Daemon  running in local system
            setSFDaemonEnv();
            sfDaemon = SFProcess.getRootLocator().getRootProcessCompound(null, 3800);
        } catch (ConnectException cEx) {  // there is no Daemon  running in local system
            setSFDaemonEnv();
            sfDaemon = SFSystem.runSmartFrog();
        }
        return sfDaemon;
    }

    private void setSFDaemonEnv() {
        //system properties
        System.setProperty("org.smartfrog.sfcore.processcompound.sfProcessName", "rootProcess");
        System.setProperty("org.smartfrog.iniFile", iniFile);
        System.setProperty("org.smartfrog.sfcore.processcompound.sfDefault.sfDefault", sfDefault);
        System.setProperty("java.security.policy", sfDefaultSecurity);
        System.setSecurityManager(new SecurityManager());
        // smartfrog dist jar files.
        File[] sfBaseJars = (new File(distPath)).listFiles(new FilenameFilter() {
            public boolean accept(File f, String s) {
                if (s.endsWith(".jar")) {
                    return true;
                }
                return false;
            }
        });

        // class path
        String baseClasssPath = "";
        for (int i = 0; i < sfBaseJars.length; i++) {
            baseClasssPath += sfBaseJars[i].getAbsolutePath() + pathSeparator;
            // System.out.println("baseClasssPath :"+baseClasssPath);
        }

        if (sfLog().isDebugEnabled()) {
            sfLog().debug("sfBaseJars length :" + sfBaseJars.length);
        }
        System.setProperty("java.class.path", System.getProperty("java.class.path") + pathSeparator + baseClasssPath);

        if (logFilePath !=null)
        {
           System.setProperty("org.smartfrog.sfcore.logging.LogToFileImpl.path",logFilePath);
            System.setProperty("org.smartfrog.sfcore.logging.LogImpl.localLoggerClass","org.smartfrog.sfcore.logging.LogToFileImpl");
        }


    }

    private ComponentDescription submitTemplate(String descriptionFile, Map attributes, String host) throws SFSubmitException {

        String AppName = null;
        AppName = "App_" + sdf.format(cal.getTime()).replace(' ', '_').replace(':', '_') + appCounter++;
	ComponentDescription cd = null;
        try {
            if (sfDaemon != null) {
                //deploy(AppName, descriptionFile, attributes, host);
                cd = asynchDeploy(AppName, descriptionFile, attributes, host);
            } else {
                if (sfLog().isErrorEnabled()) {
                    sfLog().error("SmartFrog Daemon Not found");
                }
            }

        } catch (SmartFrogDeploymentException ex1) {
            throw new SFSubmitException(AppName, ex1);
        } catch (SmartFrogException ex1) {
            throw new SFSubmitException(AppName, ex1);
        } catch (RemoteException ex1) {
            throw new SFSubmitException(AppName, ex1);
        }
	return cd;
    }


    /**
     * Returns true if smartfrog daemon is running on the given host.
     *
     * @param host String
     * @return boolean
     *         org.smartfrog.services.avalanche.repository.smartfrog.SmartfrogAdapter method
     */
    public static boolean isActive(String host) {
        try {  // there is a Daemon  running in the given system
            sfDaemon = SFProcess.getRootLocator().getRootProcessCompound(InetAddress.getByName(host), 3800);
            if (sfDaemon != null)
                return true;
        } catch (Exception cEx) {
            // there is no Daemon  running in the given node
            return false;
        }
        return false;
    }

    /**
     * Submits a deployment request on multiple hosts, It validates the description file,
     * performs the necessary attribute replacements and submits the description directly to the
     * smartfrog daemon running on these hosts.
     *
     * @param descriptionFile .sf file to submit on the repository.
     * @param attributes      Attributes to replace in the description file dynamically.
     * @param hosts           String[]
     * @throws SFParseException          if the SF in the inputStream is invalid, or if map doesnt
     *                                   contain some mendatory attribute.
     * @throws SFMultiHostSubmitException if the file and attributes are proper but runtime
     *                                   submission fails on one or more nodes due to any error.
     *                                   <p/>
     *                                   org.smartfrog.services.avalanche.repository.smartfrog.SmartfrogAdapter method
     */
    public Map submit(String descriptionFile, Map attributes, String[] hosts) throws SFParseException,
        SFMultiHostSubmitException {

        SFMultiHostSubmitException multiHostException = null;
        Map resultSet = new HashMap();
        String hostN = "";


        for (int i = 0; i < hosts.length; i++) {
            hostN = hosts[i];

            try {
                if (sfLog().isInfoEnabled()) {
                    sfLog().info("host : " + hostN);
                }
             resultSet.put(hostN, submit(descriptionFile, attributes, hostN));
            } catch (Throwable thr) {
                if (multiHostException == null) {
                    multiHostException = new SFMultiHostSubmitException(resultSet.toString());
                }
            }
        }
        if (sfLog().isInfoEnabled()) {
            sfLog().info("Multihost Result: " + resultSet.toString());
        }
        if (multiHostException != null) {
            throw multiHostException;
        }

        return resultSet;
    }



    public Map submit(String descriptionFile, Map attributes, Map hosts) throws SFParseException,
        SFMultiHostSubmitException {

        SFMultiHostSubmitException multiHostException = null;
        Map resultSet = new HashMap();
        String hostN = "";


         for (Iterator e = hosts.keySet().iterator(); e.hasNext();) {
                String key_AppID = (String)e.next();
                String value_Host = (String)attributes.get(key_AppID);
             try {
                 resultSet.put(key_AppID, submit(descriptionFile, attributes, value_Host));
             } catch (SFSubmitException e1) {
                 e1.printStackTrace();
             } catch (SmartFrogRuntimeException e1) {
                 e1.printStackTrace();
             }
         }

     if (sfLog().isInfoEnabled()) {
            sfLog().info("Multihost Result: " + resultSet.toString());
        }
        if (multiHostException != null) {
            throw multiHostException;
        }

        return resultSet;

    }
    /**
     * Submit a complete description to the local smartfrog daemon running in a different JVM.
     *
     * @param descriptionFile file to submit
     * @throws SFParseException  if the description file is invalid
     * @throws SFSubmitException if the submission or execution of the component fails.
     *                           <p/>
     *                           org.smartfrog.services.avalanche.repository.smartfrog.SmartfrogAdapter method
     */
    public Map submit(String descriptionFile) throws SFParseException, SFSubmitException, SmartFrogRuntimeException {
        return this.submit(descriptionFile, new HashMap(), "localhost");
    }

    /**
     * submits a deployment request on a localhost,
     *
     * @param descriptionFile description file to submit.
     * @param attributes      map of dynamically resolved attributes.
     * @throws SFParseException  if the file is invalid.
     * @throws SFSubmitException if the execution of the description fails.
     *                           org.smartfrog.services.avalanche.repository.smartfrog.SmartfrogAdapter method
     */
    public Map submit(String descriptionFile, Map attributes) throws SFParseException, SFSubmitException, SmartFrogRuntimeException {
        //TODO: Check who deploys the daemon onto localhost
        return this.submit(descriptionFile, attributes, "localhost");
    }

    /**
     * Submits a deployment request on a single host.
     *
     * @param descriptionFile .sf file content to submit.
     * @param attributes      map of all dynamically resolved attributes.
     * @param host            host to submit this description.
     * @throws SFParseException  if the description is invalid.
     * @throws SFSubmitException if the submission itself fails, this is not when the
     *                           description is successfully sent to host and while execution some error comes. This
     *                           exception is throws only if the description could not be sent successfully to host.
     *                           org.smartfrog.services.avalanche.repository.smartfrog.SmartfrogAdapter method
     */
    public Map submit(String descriptionFile, Map attributes, String host) throws SFParseException, SFSubmitException, SmartFrogRuntimeException {

        Map resultSet = new HashMap();
        String status = "Success";
	String appName = null;
	ComponentDescription cd = null;
	
        try {
             //appName = submitTemplate(descriptionFile, attributes, host);
             cd = submitTemplate(descriptionFile, attributes, host);
	     appName = cd.sfResolve("compName", appName, true);
        } catch (Exception exp) {
            status = "Deployment Failed with Exception " + exp.getCause();
	    appName = exp.getMessage();
        }
        resultSet.put("STATUS", status);
	if( appName != null)
        	resultSet.put("APP_NAME", appName);
	if( cd != null)
        	resultSet.put("CD", cd);
        return resultSet;
    }


    private void setSFHOME(String homePath) {
        distPath = homePath + fileSeparator + "lib" + fileSeparator;
        iniFile = homePath + fileSeparator + "bin" + fileSeparator + "default.ini";
        sfDefault = homePath + fileSeparator + "bin" + fileSeparator + "default.sf";
        sfDefaultSecurity = homePath + fileSeparator + "private" + fileSeparator + "sf.no.security.policy";
    }


    public static Map getAllAttribute(String descriptionFileName) throws Exception {
        InputStream descriptionStream = org.smartfrog.SFSystem.getInputStreamForResource(descriptionFileName);
        SFParser parser = new SFParser(SFParser.getLanguageFromUrl(descriptionFileName));
        Phases phases = parser.sfParse(descriptionStream);
        phases = phases.sfResolvePhases();
        ComponentDescription cd = phases.sfAsComponentDescription();
        NodeVistor CD = new NodeVistor();
        cd.visit(CD, false);
        return CD.attAndvalue;
    }

    public static Map getAllAttribute(String descriptionFileName, String tag) throws Exception {
        InputStream descriptionStream = org.smartfrog.SFSystem.getInputStreamForResource(descriptionFileName);
        SFParser parser = new SFParser(SFParser.getLanguageFromUrl(descriptionFileName));
        Phases phases = parser.sfParse(descriptionStream);
        phases = phases.sfResolvePhases();
        ComponentDescription cd = phases.sfAsComponentDescription();
        NodeVistor CD = new NodeVistor(tag);
        cd.visit(CD, false);
        return CD.attAndvalue;
    }


    public static void setLogFilePath(String filePath) {
        logFilePath = filePath;

    }

    public void enableDyanamicClassLoading(String codebase) {
        this.SFDYNAMICCLASSLOADING_ON = true;
        this.dyClassLoading_codebase = codebase;
    }

    public void disableDyanamicClassLoading() {
        this.SFDYNAMICCLASSLOADING_ON = false;
    }

    public static void stopBaseSFDaemon(InetAddress host) throws SmartFrogException {
        try {
            TerminationRecord tr = new TerminationRecord("Process Terminated", null, null);
            sfDaemon = SFProcess.getRootLocator().getRootProcessCompound(host, 3800);
            TerminatorThread terminator = new TerminatorThread(sfDaemon, tr).detach().quietly();
            terminator.start();
        } catch (Exception Ex) {
            throw new SmartFrogException("Not able to STOP Base SmartFrog Daemon");
        }
    }

    /**
     * Core Log
     */
    private static LogSF sflog = null;

    /**
     * @return LogSF
     */
    public static LogSF sfLog() {
        if (sflog == null) {
            sflog = LogFactory.getLog("SF-Adapter");
        }
        return sflog;
    }


    private void deploy(String name, String descriptionFile, Map attributes, String host) throws SmartFrogException, RemoteException {
        Phases phases = null;
        try {
            InputStream descriptionStream = org.smartfrog.SFSystem.getInputStreamForResource(descriptionFile);
            SFParser parser = new SFParser(SFParser.getLanguageFromUrl(descriptionFile));
            phases = parser.sfParse(descriptionStream);
            addAttributesToCD(attributes, phases);
        } catch (Exception e) {
            sfLog().err("", e);
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(e);
        }
        ComponentDescription cd = null;
        phases = phases.sfResolvePhases();
        cd = phases.sfAsComponentDescription();
        if (sfLog().isInfoEnabled()) {
            sfLog().info("\n*************************************************\n*** Deploying:\n" +
                    cd.toString() +
                    "\n*************************************************\n");
        }
        try {

            cd.sfAddAttribute("sfProcessHost", host);
            if (sfLog().isDebugEnabled()) sfLog().debug("New CD : " + cd.toString());
            // Object prim = sfDaemon.sfCreateNewApp(name, cd, null);

            CompoundImpl cmp = new CompoundImpl();

            // Parallel Implementation

            SubmitterThread st = new SubmitterThread(cmp.sfDeployComponentDescription(name, null, cd, null));
            st.start();



        } catch (Exception ex) {
            sfLog().error("", ex);
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(ex);
        }
    }

    //Placement done by the compiler
    private void addAttributesToCD(Map attributes, Phases phases) throws SmartFrogResolutionException,
            SmartFrogRuntimeException {
        if (attributes != null) {
            sfLog().info("Attribute Replacement Started\n");
            for (Iterator e = attributes.keySet().iterator(); e.hasNext();) {
                Object key = e.next();
                sfLog().info("key = " + key);
                Object value = attributes.get(key);
                sfLog().info("value = " + value);
                //NOTE: I don't like to do the Reference conversion here. Ideally the conversion should be done in the Map objects.
                Reference keyRef = Reference.fromString(key.toString());
                sfLog().info("Attribute : KeyRef:" + keyRef.toString() + "  ; value:" + value.toString());
                phases.sfReplaceAttribute(keyRef, value);

            }
        }
    }
    private void addExtraArrtibutes(Vector attr , ComponentDescription cd)
    {

    }


    public ComponentDescription asynchDeploy(String compName, String descriptionFile,Map attributes,String host) throws SmartFrogException, RemoteException {
          Phases phases = null;
        try {
            InputStream descriptionStream = org.smartfrog.SFSystem.getInputStreamForResource(descriptionFile);
            SFParser parser = new SFParser(SFParser.getLanguageFromUrl(descriptionFile));
            phases = parser.sfParse(descriptionStream);
            addAttributesToCD(attributes, phases); 
        } catch (Exception e) {
            sfLog().err("", e);
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(e);
        }
        ComponentDescription cd = null;
        phases = phases.sfResolvePhases();
        cd = phases.sfAsComponentDescription();

        try {
         cd.sfAddAttribute("sfProcessHost", host);
         cd.sfAddAttribute("compName", compName);
            if (sfLog().isInfoEnabled()) {
            sfLog().info("\n*************************************************\n*** Ashync. Deploying:\n" +
                    cd.toString() + compName +
                    "\n*************************************************\n");
        }
      sfDaemon.sfCreateNewApp(compName, cd, null);
        }catch(Exception ex) {
            sfLog().error("", ex);
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(ex);
        }
    return cd;
    }
 

}
