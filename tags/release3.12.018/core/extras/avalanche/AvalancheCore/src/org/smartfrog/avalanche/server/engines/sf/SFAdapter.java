/**
 (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org
 */
/*
 * Created on Jul 29, 2005
 *
 */
package org.smartfrog.avalanche.server.engines.sf;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerUtils;
import org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType;
import org.smartfrog.avalanche.core.activeHostProfile.ModuleStateType;
import org.smartfrog.avalanche.core.host.ArgumentType.Argument;
import org.smartfrog.avalanche.core.host.HostType;
import org.smartfrog.avalanche.server.ActiveProfileManager;
import org.smartfrog.avalanche.server.AvalancheFactory;
import org.smartfrog.avalanche.server.DatabaseAccessException;
import org.smartfrog.avalanche.server.HostManager;
import org.smartfrog.avalanche.server.SettingsManager;
import org.smartfrog.avalanche.server.modules.ModuleCreationException;
import org.smartfrog.avalanche.settings.sfConfig.SfConfigsType;
import org.smartfrog.avalanche.settings.sfConfig.SfDescriptionType;
import org.smartfrog.services.xmpp.MonitoringEvent;
import org.smartfrog.services.display.Display;
import org.smartfrog.services.quartz.collector.DataSource;
import org.smartfrog.services.sfinterface.SFMultiHostSubmitException;
import org.smartfrog.services.sfinterface.SFParseException;
import org.smartfrog.services.sfinterface.SFSubmitException;
import org.smartfrog.services.sfinterface.SmartFrogAdapterImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.avalanche.core.host.DataTransferModeType;
import org.smartfrog.avalanche.core.host.ArgumentType;
import org.smartfrog.services.sfinterface.SmartfrogAdapter;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import javax.swing.*;
import java.io.File;
import java.io.FilenameFilter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * @author sanjay, Jul 29, 2005
 *         This class connects Avalanche Server to Smartfrog for deployments. It adds Avalanche
 *         specific attributes to Smartfrog descriptor and configures the codebase for remote class loading.
 */
public class SFAdapter {

    protected AvalancheFactory avalancheFactory;
    protected Scheduler sched;
    private Vector<String> machines = new Vector<String>();
    public static Hashtable allValues = new Hashtable();
    public static final String AVALANCHE_SERVER = "_Avalanche_server";

    public SFAdapter(AvalancheFactory factory) {
        super();
        this.avalancheFactory = factory;
    }

    public SFAdapter(AvalancheFactory factory, Scheduler scheduler) {
        super();
        this.avalancheFactory = factory;
        this.sched = scheduler;
    }

    /**
     * Submit a smartfrog description to a remote client node.
     *
     * @param moduleId     Module Id the of the module associated with descriptor.
     * @param version      version of the module.
     * @param instanceName a unique name for this instance of module, can be used for tracking.
     * @param title        title of smartfrog description as in Avalanche database.
     * @param attrMap      Attribute Map to be replaced in description.
     * @param hosts        list of hosts to be deployed.
     * @return
     * @throws SFSubmitException
     */
    public Map submit(String moduleId, String version, String instanceName,
                      String title, Map<String, String> attrMap, String[] hosts) throws SFSubmitException {

        ActiveProfileManager apm = null;
        try {
            apm = avalancheFactory.getActiveProfileManager();
            SettingsManager sett = avalancheFactory.getSettingsManager();
            SfConfigsType configs = sett.getSFConfigs();

            SfDescriptionType sfDesc = null;
            if (null != title) {

                SfDescriptionType[] descs = configs.getSfDescriptionArray();
                for (SfDescriptionType desc : descs) {
                    if (desc.getTitle().equals(title)) {
                        sfDesc = desc;
                    }
                }
            } else {
                throw new SFSubmitException("ConfigURL does not exist in database : " + title);
            }

            String actionId = sfDesc.getAction();
            for (String host : hosts) {
                ActiveProfileType profile = apm.getProfile(host);
                if (null == profile) {
                    profile = apm.newProfile(host);
                }

                ModuleStateType[] states = profile.getModuleStateArray();
                ModuleStateType currentState = null;
                for (ModuleStateType state : states) {
                    String mId = state.getId();
                    String ver = state.getVersion();
                    String ins = state.getInstanceName();
                    if (moduleId.equals(mId) && version.equals(ver) && instanceName.equals(ins)) {
                        currentState = state;
                        break;
                    }
                }

                if (null == currentState) {
                    currentState = profile.addNewModuleState();
                    currentState.setId(moduleId);
                    currentState.setVersion(version);
                    currentState.setInstanceName(instanceName);
                }
                currentState.setState("Initializing");
                currentState.setLastAction(actionId);
                currentState.setMsg("Submitting deployment command to remote node");
                String updTime = getDateTime();
                currentState.setLastUpdated(updTime);

                apm.setProfile(profile);
            }

            // insert standard attributes first, these must be present otherwise
            // the submission will be rejected on the remote node.
            // NOTE: these attributes are not visible to end users.
            attrMap.put(MonitoringEvent.MODULEID, moduleId);
            attrMap.put(MonitoringEvent.INSTANCE_NAME, instanceName);
            attrMap.put(MonitoringEvent.ACTION_NAME, actionId);

            // Just submit here, the profile will be updated by remote events.
            Map retCodes = submitToSF(sfDesc, attrMap, hosts);

            Set runningHosts = retCodes.keySet();
            Iterator itor = runningHosts.iterator();
            while (itor.hasNext()) {
                String h = (String) itor.next();
                ActiveProfileType ap = apm.getProfile(h);
                if (ap != null) {

                    // first get hold of the module configuration on this host.
                    ModuleStateType[] states = ap.getModuleStateArray();
                    ModuleStateType currentState = null;
                    for (ModuleStateType state : states) {
                        String mId = state.getId();
                        String ver = state.getVersion();
                        String ins = state.getInstanceName();
                        if (moduleId.equals(mId) && version.equals(ver) && instanceName.equals(ins)) {
                            currentState = state;
                            break;
                        }
                    }
	//	String homeDir = this.avalancheFactory.getAvalancheHome();
       	//	String logsDir = homeDir + File.separator + "logs";
                    if (null != currentState) {
                        currentState.setState("Running");

                        Map m = (Map) retCodes.get(h);
                        String status = (String) m.get("STATUS");
                        String appName = (String) m.get("APP_NAME");
                        ComponentDescription cd = (ComponentDescription) m.get("CD");
			String reportPath = null;
			if ( cd != null)
				reportPath= cd.sfResolve("reportPath", reportPath, false);
                        //currentState.setLogFile(logsDir+File.separator +appName+".out");
                        currentState.setLogFile(appName);
                        currentState.setLastUpdated(getDateTime());
                        currentState.setState(status);
                        currentState.setReportPath(reportPath);
                    }
                    apm.setProfile(ap);
                }
            }
            return retCodes;
        } catch (Exception e) {
            // set profile for this module to failed
            try {
                if (null != apm) {
                    for (String host : hosts) {
                        ActiveProfileType ap = apm.getProfile(host);
                        if (ap != null) {
                            // first get hold of the module configuration on this host.
                            ModuleStateType[] states = ap.getModuleStateArray();
                            ModuleStateType currentState = null;
                            for (ModuleStateType state : states) {
                                String mId = state.getId();
                                String ver = state.getVersion();
                                if (moduleId.equals(mId) && version.equals(ver)) {
                                    currentState = state;
                                    break;
                                }
                            }

                            if (null != currentState) {
                                currentState.setState("Failed");

                                currentState.setLastUpdated(getDateTime());
                                currentState.setMsg("Failed on Server : " + e.getMessage());
                            }
                            apm.setProfile(ap);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            throw new SFSubmitException(e);
        }
    }

    /**
     * Not used.
     *
     * @param hostId
     * @param map
     * @throws Exception
     */
    private void addHostProperties(String hostId, Map map) throws Exception {
        HostManager hm = avalancheFactory.getHostManager();
        HostType hmt = hm.getHost(hostId);
        if (null != hmt) {
            if (null != hmt.getArguments()) {
                Argument[] args = hmt.getArguments().getArgumentArray();
                for (int i = 0; i < args.length; i++) {
                    map.put(args[i].getName(), args[i].getValue());
                }
            }
        }

    }

    /**
     * Checks the state of host as known to Avalanche server through
     * Async events. If the host's active profile doesnt exist in Avalanche server
     * this will return false.
     *
     * @param hostId
     * @return
     * @throws Exception
     */
    public boolean isActive(String hostId) throws Exception {
        ActiveProfileManager apm = avalancheFactory.getActiveProfileManager();
        ActiveProfileType ap = apm.getProfile(hostId);
        boolean state = false;
        if (null != ap && ap.getHostState().equals("Available")) {
            state = true;
        }
        return state;
    }

    /**
     * Checks the state of host by directly contacting the smartfrog port on the
     * host. This method may take a long time to return in case of proxy misconfigurations.
     *
     * @param hostId
     * @return
     * @throws Exception
     */
    public static boolean isActiveSync(String hostId) throws Exception {
        return SmartFrogAdapterImpl.isActive(hostId);
    }

    private Map submitToSF(SfDescriptionType sfDesc, Map attrMap, String[] hosts) throws SFSubmitException {
        String homeDir = this.avalancheFactory.getAvalancheHome();
        String sfLibDir = homeDir + File.separator + "smartfrog" + File.separator + "lib";
        String sfDistDir = homeDir + File.separator + "smartfrog" + File.separator + "dist";
        String logsDir = homeDir + File.separator + "logs";
        String remoteLoadServer =
                this.avalancheFactory.getAttribute(AvalancheFactory.AVALANCHE_SERVER_NAME);


        Map ret = null;
        try {

            SfDescriptionType.Argument[] args = sfDesc.getArgumentArray();
            HashMap<String, String> finalMap = new HashMap<String, String>();

            for (SfDescriptionType.Argument arg : args) {
                String name = arg.getName();
                String value = arg.getValue();
                finalMap.put(name, value);
            }

            finalMap.putAll(attrMap);

            String avalancheServer = (String) attrMap.get(AVALANCHE_SERVER);
            // code base for remote daemons
            String[] jarFiles = sfDesc.getClassPathArray();
            String sfCodeBase = null;
            if (null != jarFiles && null != avalancheServer) {
                sfCodeBase = "";
                for (String jarFile : jarFiles) {
                    sfCodeBase += "http://" + avalancheServer + "/" + remoteLoadServer + "/Downloader.jsp?filePath=" + jarFile + " ";
                }
            }
            String url = sfDesc.getUrl();
            Map sfcMap = SmartFrogAdapterImpl.getAllAttribute(url,null);


            Set cbAttrs = sfcMap.keySet();
            Iterator it = cbAttrs.iterator();

            if (null != sfCodeBase) {
                while (it.hasNext()) {
                    String key = (String) it.next();
                    System.out.println("Attr Key : " + key);
                    System.out.println("Attr Value : " + sfcMap.get(key));
                    if (key.endsWith("sfCodeBase")) {
                        finalMap.put(key, sfCodeBase);
                    }
                }
            }

            // add classpath for all dependent jars for forked VM
            String[] classpath = sfDesc.getClassPathArray();

            // convert to absolute url
            for (int i = 0; i < classpath.length; i++) {
                classpath[i] = sfLibDir + java.io.File.separator + classpath[i];
            }

            // base libs in smartfrog/dist/lib
            String sfBaseLibDir = sfDistDir + File.separator + "lib";
            File[] sfBaseJars = (new File(sfBaseLibDir)).listFiles(new FilenameFilter() {
                public boolean accept(File f, String s) {
                    return s.endsWith(".jar");
                }
            });


            String[] basePath = new String[sfBaseJars.length];
            for (int i = 0; i < sfBaseJars.length; i++) {
                basePath[i] = sfBaseJars[i].getAbsolutePath();
            }

            SmartFrogAdapterImpl adapter = new SmartFrogAdapterImpl(sfDistDir);

            //	adapter.addClasspath(basePath);	// sf core jars
            //	adapter.addClasspath(classpath);	// add classpath for components

            String configURL = sfDesc.getUrl();
            SmartFrogAdapterImpl.setLogFilePath(logsDir);

            // pass finalMap which contains attrMap now. validate if it exists.
            ret = adapter.submit(configURL, finalMap, hosts);

        } catch (ModuleCreationException e) {
            throw new SFSubmitException(e);
        } catch (DatabaseAccessException e) {
            throw new SFSubmitException(e);
        } catch (SFParseException e) {
            throw new SFSubmitException(e);
        } catch (SFMultiHostSubmitException e) {
            throw new SFSubmitException(e);
        } catch (Exception e) {
            throw new SFSubmitException(e);
        }
        return ret;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        java.util.Date date = new java.util.Date();
        return dateFormat.format(date);
    }

    /**
     * Extracts all attributes from the given smartfrog descriptions.
     *
     * @param sfURL
     * @return
     * @throws Exception
     */
    public static Map getSFAttributes(String sfURL) throws Exception {
        return SmartFrogAdapterImpl.getAllAttribute(sfURL);
    }

     /**
     * Extracts all attributes from the given smartfrog descriptions with a  given tag.
     *
     * @param sfURL
     * @param tag
     * @return
     * @throws Exception
     */
    public static Map getSFAttributes(String sfURL, String tag) throws Exception {
        return SmartFrogAdapterImpl.getAllAttribute(sfURL, tag);
    }

    /**
     * Stops Smartfrog daemon running on default port on a remote node.
     *
     * @param host
     * @throws SmartFrogException
     * @throws UnknownHostException
     */
    public static void stopDaemon(String host) throws SmartFrogException, UnknownHostException {
        SmartFrogAdapterImpl.stopBaseSFDaemon(InetAddress.getByName(host));
    }

    public void submitTOScheduler(String moduleId, String version, String instanceName,
                                  String title, Map attrMap, String[] hosts) throws Exception {

        // computer a time that is on the next round minute
        Date runTime = TriggerUtils.getEvenMinuteDate(new Date());

        // define the job and tie it to our HelloJob class
        JobDetail job = new JobDetail("job1", "group1", ScheduleJob.class);

        job.getJobDataMap().put("adapter", this);
        job.getJobDataMap().put("moduleId", moduleId);
        job.getJobDataMap().put("version", version);
        job.getJobDataMap().put("instanceName", instanceName);
        job.getJobDataMap().put("title", title);
        job.getJobDataMap().put("attrMap", attrMap);

        // find hostname from Collector

        System.out.println("Number of machines to collect data from are=======" + hosts.length);
        for (String host : hosts) machines.add(host);
        setTargetInstances(machines.size());
        //Sort the array based on the values in allValues and then schedule on the first element.
        allValues.put("test", new Integer(200));
        Collection collect = allValues.values();
        Object[] array = collect.toArray();
        List list = Arrays.asList(array);
        Collections.sort(list);
        for (int i = 0; i < list.size(); i++) {
            System.out.println("Element in sorted list===========" + (list.get(i)).toString());
        }
        Enumeration keys = allValues.keys();
        Object key = null;
        Object value = null;
        while (keys.hasMoreElements()) {
            key = keys.nextElement();
            value = allValues.get(key);
            if (value == list.get(0)) {
                break;
            }
        }

        System.out.println("Final machine for scheduling is===========" + key.toString());

        //  job.getJobDataMap().put("hostname", "localhost");

        job.getJobDataMap().put("hostname", key.toString());

        // Tell quartz to schedule the job using our trigger
        sched.scheduleJob(job, new SimpleTrigger("trigger1", "group1"));
        System.out.println(job.getFullName() + " will run now");
    }

    // these are internal methods that should not be used directly
    private void setTargetInstances(int target) {

        for (int i = 0; i < target; i++) {
            try {
                startInstance(i);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public static void putValue(String name, Object value) {
        synchronized (allValues) {
            allValues.put(name, value);
        }
    }

    private void startInstance(int instance) throws Exception {
        String server = (String) machines.elementAt(instance);

        Compound cp = SFProcess.getRootLocator().getRootProcessCompound(InetAddress.getByName(server));
        if (cp != null) {
            DataSource monitor = (DataSource) cp.sfResolve("cpumonitor", true);
            putValue(server, new Integer(monitor.getData()));
            System.out.println("Value   " + monitor.getData());
        }
    }

    public void getHostReport(String host, String outputFile, String reportPath) throws SFSubmitException{
	    try {
	    HostManager hm = avalancheFactory.getHostManager();
	    // Retrieving host information
                HostType h = hm.getHost(host);

                // Username and password
                String username = h.getUser();
                String password = h.getPassword();
                
                HostType.TransferModes tm = h.getTransferModes();
                if (null != tm) {
                    DataTransferModeType[] transferModes = tm.getModeArray();
                    if (null != transferModes) {
                        for (DataTransferModeType transferMode : transferModes) {
                            // Set TransferMode which is selected as default
                            if (transferMode.getIsDefault()) {
                                username = transferMode.getUser();
                                password = transferMode.getPassword();
                                break;
                            }
                        }
                    }
                }
		
		ArgumentType argType = h.getArguments();
                String avalancheInstallationDirectory = null;
                
                if (null != argType) {
                    ArgumentType.Argument[] args = argType.getArgumentArray();

                    for (ArgumentType.Argument arg : args) {
                        if (arg.getName().equals("AVALANCHE_HOME")) {
                            avalancheInstallationDirectory = arg.getValue();
                        }
                    }
                }
		String homeDir = this.avalancheFactory.getAvalancheHome();
		String sfDistDir  = homeDir + File.separator + "smartfrog" + File.separator + "dist";
        	String scpFile = homeDir + File.separator + "smartfrog" + File.separator + "boot" + File.separator + "scp.sf";
        	String logsDir = homeDir + File.separator + "logs";
		HashMap attrMap = new HashMap();
		
		if (reportPath != null){
			attrMap.put("sfConfig:SCP:file", username+":"+ password + "@"+ host+ ":" + reportPath + "/*");
		 	File outputDir = new File(logsDir + File.separator + outputFile);
		 	if (!outputDir.exists() && !outputDir.isDirectory())
		 		outputDir.mkdir();
			attrMap.put("sfConfig:SCP:localTodir" , logsDir + File.separator + outputFile);
		}else {
			attrMap.put("sfConfig:SCP:file",username+":"+ password + "@"+ host+ ":" + avalancheInstallationDirectory + "/smartfrog/nohup.out");
			
			attrMap.put("sfConfig:SCP:localTofile" , logsDir + File.separator + outputFile +".out");
		}
		
		SmartfrogAdapter adapter = new SmartFrogAdapterImpl(sfDistDir);
                SmartFrogAdapterImpl.setLogFilePath(logsDir);

            	// run the description on local host for remote deployments.
          
            	adapter.submit(scpFile, attrMap, new String[]{"localhost"});
	} catch (SFParseException e) {
            throw new SFSubmitException(e);
        } catch (SFMultiHostSubmitException e) {
            throw new SFSubmitException(e);
        } catch (Exception e) {
            throw new SFSubmitException(e);
        }

    }

    public void startMngConsole(String hostname) {
        Display mngConsole = null;
        int height = 480;
        int width = 640;
        boolean showRootProcess = false;
        boolean showCDasChild = true;
        boolean showScripting = false;
        String positionDisplay = "NE";
        String nameDisplay = "sfManagementConsole";
        int port = 3800;
        Display display = null;
        try {
            display = new Display("Interface");
            Compound cp = SFProcess.getRootLocator().getRootProcessCompound(InetAddress.getByName(hostname));
            if (cp != null) {
                mngConsole = org.smartfrog.services.management.SFDeployDisplay.startConsole(nameDisplay, height, width, positionDisplay,
                        showRootProcess, showCDasChild, showScripting, hostname, port, false);
            }
        } catch (java.net.UnknownHostException uex) {
            if (mngConsole != null) {
                mngConsole.dispose();
                mngConsole = null;
            }
            dialogBox(display, true, "startMngConsole", JOptionPane.ERROR_MESSAGE, "Couldn't start SFMngConsole for resource " + hostname + ". Unknown host.");
        } catch (java.rmi.ConnectException cex) {
            if (mngConsole != null) {
                mngConsole.dispose();
                mngConsole = null;
            }
            dialogBox(display, true, "startMngConsole", JOptionPane.ERROR_MESSAGE, "Couldn't start SFMngConsole for resource " + hostname + ". " + cex.getMessage());
        } catch (Exception e) {
            if (mngConsole != null) {
                mngConsole.dispose();
                mngConsole = null;
            }
            dialogBox(display, true, "startMngConsole", JOptionPane.ERROR_MESSAGE, "Couldn't start SFMngConsole for resource " + hostname + "." + e.getMessage());
        }
    }

    /**
     * Prepares a dialog box.
     *
     * @param frame      JFrame
     * @param modal      boolean indicator for modal
     * @param title      window title
     * @param windowType window type
     * @param message    Message to be displayed
     */
    private void dialogBox(final JFrame frame, final boolean modal,
                           final String title, final int windowType, final String message) {
        JOptionPane errorPane = new JOptionPane(message, windowType);
        JDialog dialog = errorPane.createDialog(frame, title);
        dialog.setModal(modal);
        dialog.show();
    }


    public static void main(String[] args) throws Exception {
        String url = "D:\\programming\\java\\SmartFrog\\components\\quartz\\src\\org\\smartfrog\\services\\sfinterface\\test\\AttribTest1.sf";

        Map<String, String> map = SmartFrogAdapterImpl.getAllAttribute(url);
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            System.out.println("Next -- " + key);
        }

        System.out.println("Map--- " + map);

        HashMap<String, String> newMap = new HashMap<String, String>();
        newMap.put("srcFile", "newSrcFile");
        newMap.put("actions:gt4setup:tarFilename", "newTarFileName");
        //	map.put("actions:gt4setup:tomcatLoc","/opt/jakarta-tomcat");

        String sfHome = "/mnt/c/data/avalanche/smartfrog/dist";
        String sfBaseLibDir = sfHome + java.io.File.separator + "lib";
        File[] sfBaseJars = (new File(sfBaseLibDir)).listFiles(new FilenameFilter() {
            public boolean accept(File f, String s) {
                return s.endsWith(".jar");
            }
        });

/*		
        String dir = "/mnt/c/data/avalanche/smartfrog" ;
        String sfExtraLibDir = dir + java.io.File.separator + "lib" ;
        File []sfExtraJars = (new File(sfExtraLibDir)).listFiles(new FilenameFilter(){
                public boolean accept(File f, String s){
                    if ( s.endsWith(".jar")){
                        return true;
                    }
                    return false;
                }
        });
        String []extraJars = new String [sfExtraJars.length];
        for( int i=0;i<extraJars.length;i++){
            extraJars[i] = sfExtraJars[i].getAbsolutePath();
        }
        adapter.addClasspath(extraJars);

    */

        String[] baseJarPaths = new String[sfBaseJars.length];
        for (int i = 0; i < baseJarPaths.length; i++) {
            baseJarPaths[i] = sfBaseJars[i].getAbsolutePath();
        }


        SmartFrogAdapterImpl adapter = new SmartFrogAdapterImpl();
        //	adapter.addClasspath(baseJarPaths);
        //	adapter.setLogFilePath("/mnt/c/data/avalanche/logs");
		adapter.submit(url, newMap, "lx97120");
		
		System.out.println("After submit");
	}
	
}
