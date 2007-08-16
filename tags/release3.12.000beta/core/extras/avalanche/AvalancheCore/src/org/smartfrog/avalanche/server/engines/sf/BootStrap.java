/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jul 26, 2005
 *
 */
package org.smartfrog.avalanche.server.engines.sf;

import org.smartfrog.avalanche.core.host.AccessModeType;
import org.smartfrog.avalanche.core.host.ArgumentType;
import org.smartfrog.avalanche.core.host.DataTransferModeType;
import org.smartfrog.avalanche.core.host.HostType;
import org.smartfrog.avalanche.server.AvalancheFactory;
import org.smartfrog.avalanche.server.HostManager;
import org.smartfrog.avalanche.server.ServerSetup;
import org.smartfrog.avalanche.server.engines.HostIgnitionException;
import org.smartfrog.services.sfinstaller.Daemon;
import org.smartfrog.services.sfinstaller.TemplateGen;
import org.smartfrog.services.sfinterface.SmartFrogAdapterImpl;
import org.smartfrog.services.sfinterface.SmartfrogAdapter;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;


/**
 * Provides Host Ignition functionality ( Installs smartfrog on other nodes). Avalanche Server must be installed 
 * started before using this class. It uses Avalanche dayabase to get details of Hosts so the hosts must be added 
 * to Avalanche before ignition is attempted. 
 * @author sanjay, Jul 26, 2005
 * 
 */
public class BootStrap {
	protected AvalancheFactory factory ;
	protected ServerSetup setup ;
	protected String bootDir ;
	protected String serverOS;
	private static Log log = LogFactory.getLog(BootStrap.class);
	
	public static final String sfReleaseFileUnix = "smartfrog.tar.gz";
	public static final String sfReleaseFileWindows = "smartfrog.zip";
	
	public static final String sfReleaseName = "smartfrog";
	public static final String sfTemplate = "sfinstaller.vm";
	public static final String sfWorkDir = "work";
	
	public static final String sfInstallLocationUnix = "." ; // create in user home by deault  
	public static final String sfInstallLocationWindows = "c:\\" ;  // create in c:\\ by default
    private static final String DEFAULT_EMAILTO = "none";
    private static final String DEFAULT_EMAILFROM = "none";
    private static final String DEFAULT_EMAILSERVER = "none";

    /**
     * Contains one additonal File.separator if server os is windows.
     */
    private String strOptSeparator = "";

    public BootStrap(AvalancheFactory f, ServerSetup setup) {
		this.factory = f ;
		this.setup = setup;
		//serverOS = factory.getAvalancheServerOS();
		serverOS =System.getProperty("os.name");
		System.out.println("OS===" + serverOS);

        if ( serverOS.startsWith("Windows") || serverOS.startsWith("windows") )
            strOptSeparator = File.separator;     // additional separator needed for velocity

        bootDir = factory.getAvalancheHome() + File.separator + strOptSeparator + "smartfrog" + File.separator + strOptSeparator + "boot";

    }

	/**
	 * Ignites a list of hosts. These hosts should exist in Avalanche database, this method
	 * piicks up host properties and access details from Avalanche database and uses that information
	 * to ignite the hosts. 
	 * @param hosts
	 * @throws HostIgnitionException
	 */
	public void ignite(String []hosts) throws HostIgnitionException{
		try{
			HostManager hostManager = factory.getHostManager();
			
			String templateFile = bootDir + java.io.File.separator +  sfTemplate; 
			
			String outDir = bootDir + java.io.File.separator + sfWorkDir ;
			
			String outputFile = outDir + File.separator  + "hostIgnition" + getDateTime() + ".sf";  
			
			String avalancheHome = null; 

			HashMap map = new HashMap();
			for( int i=0;i<hosts.length;i++){
				HostType h = hostManager.getHost(hosts[i]);
				HostType.AccessModes am = h.getAccessModes();
				
				// setting default tyoes to ssh and scp
				String accessType = "ssh";
				
				String username = h.getUser();
				String password = h.getPassword(); 

				if( am != null ) {
					AccessModeType []modes = am.getModeArray();
					
					if( null != modes ) {
						for( int j=0;j<modes.length;j++){
							if( modes[j].getIsDefault() ){
								accessType = modes[j].getType();

                                // only one default mode, so we do not need to continue the loop
                                break;
                            }
						}
					}
				}
				String transferType = "scp";
				HostType.TransferModes tm = h.getTransferModes();
				if( null != tm ){
					DataTransferModeType []transferModes = tm.getModeArray();
					
					if( null != transferModes ){
						for( int j=0;j<transferModes.length;j++){
							if( transferModes[j].getIsDefault() ){
								transferType = transferModes[j].getType();
								username = transferModes[j].getUser();
								password = transferModes[j].getPassword();

                                // only one default mode, so we do not need to continue the loop
                                break;
                            }
						}
					}
				}
				
				ArgumentType argType = h.getArguments();
				ArgumentType.Argument[] args = argType.getArgumentArray();
				String java_home = null ;
				for(int j=0;j<args.length;j++){
					if( args[j].getName().equals("JAVA_HOME")){
						java_home = args[j].getValue();
					}
					if( args[j].getName().equals("AVALANCHE_HOME")){
						avalancheHome = args[j].getValue();
					}
				}
				
				String os = h.getPlatformSelector().getOs();

                // if the install location hasn't been set use the default locations
                if (null == avalancheHome)
                    avalancheHome = ( os.equals("windows") ? sfInstallLocationWindows : sfInstallLocationUnix );
				
				Daemon d = null ;
				// supporting only windows and unix now.
				log.info("Host Ignition - " + hosts[i] + ", OS : " + os + ", transferType : " + transferType
						+ ", AccessType : " + accessType + ", UserName : " + username );
				log.info("JAVA_HOME : " + java_home + ", AVALANCHE_HOME : " + avalancheHome );

                // decide about the filename, depending on the target os
                String strLocalfile1 = bootDir + File.separator + strOptSeparator + (os.equals("windows") ? sfReleaseFileWindows : sfReleaseFileUnix);

                d = new Daemon(hosts[i],                // name
                                os,                     // os
                                hosts[i],               // host
                                transferType,           // transfer type
                                accessType,             // access type
                                username,               // username
                                password,               // password
                                strLocalfile1,          // localfile1
                                null,                   // localfile2
                                null,                   // localfile3
                                null,                   // keyfile
                                null,                   // secproperties
                                null,                   // smartfrogjar
                                null,                   // servicesjar
                                null,                   // examplesjar
                                sfReleaseName,          // releasename
                                java_home,              // javahome
                                avalancheHome,          // installdir
                                DEFAULT_EMAILTO,        // emailto
                                DEFAULT_EMAILFROM,      // emailfrom
                                DEFAULT_EMAILSERVER);   // emailserver

				map.put(hosts[i], d);
			}

            String logFileDir = factory.getAvalancheHome() + File.separator + strOptSeparator + "logs" + strOptSeparator;
			
			// to read from map and write to data. all and then create a description
			log.info("TemplateGen Map : "+ map);
			TemplateGen.createTemplate(map, templateFile, outputFile, false, false, null, logFileDir);

			File of = new File(outputFile);
			if( !of.exists() ){
				throw new HostIgnitionException("Template creation failed ! File :" + outputFile);
			}

			SmartfrogAdapter adapter = null ;
			String sfHome = factory.getAvalancheHome() + File.separator + "smartfrog" + 
								File.separator  + "dist";
			adapter = new SmartFrogAdapterImpl(sfHome);
			
			SmartFrogAdapterImpl.setLogFilePath(logFileDir);

			HashMap attrMap = new HashMap();
			// run the description on local host for remote deployments.
			log.info("SF : " + outputFile);

			adapter.submit(outputFile, attrMap, new String[]{"localhost"});
			
			java.io.File f = new File (outputFile) ; 
			//if( !f.delete() ){
			//	log.error("Temporary smartfrog HostIgnition File delete failed");
			//}
		}catch(Exception e){
			log.error(e);
			throw new HostIgnitionException(e);
		}
	}
	private String getDateTime(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        java.util.Date date = new java.util.Date();
        return dateFormat.format(date);
	}
	
}
