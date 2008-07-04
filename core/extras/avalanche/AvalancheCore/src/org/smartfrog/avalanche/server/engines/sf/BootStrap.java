/**
 (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP
 This library is free software; you can redistribute it and/or modify it under the terms of the
 GNU Lesser General Public License as published by the Free Software Foundation;
 either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library;
 if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 For more information: www.smartfrog.org
 */

package org.smartfrog.avalanche.server.engines.sf;

import org.smartfrog.avalanche.core.host.AccessModeType;
import org.smartfrog.avalanche.core.host.ArgumentType;
import org.smartfrog.avalanche.core.host.DataTransferModeType;
import org.smartfrog.avalanche.core.host.HostType;
import org.smartfrog.avalanche.server.AvalancheFactory;
import org.smartfrog.avalanche.server.HostManager;
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
import java.util.ArrayList;

/**
 * Provides Host Ignition functionality (Installs smartfrog on other nodes). Avalanche Server must be installed
 * started before using this class. It uses Avalanche dayabase to get details of Hosts so the hosts must be added
 * to Avalanche before ignition is attempted.
 *
 * @author sanjay, Jul 26, 2005
 */
public class BootStrap {
    protected String sfDirectory = null;
    protected String sfBootDirectory = null;
    protected String avalancheHome = null;
    protected AvalancheFactory factory = null;

    private static Log log = LogFactory.getLog(BootStrap.class);

	/**
	 * Path to the package with should be used for the host ignition.
	 */
	private String IgnitionPackage = null;
	private String IgnitionTemplate = null;

	public static final String sfReleaseFileUnix = "smartfrog.tar.gz";
    public static final String sfReleaseFileWindows = "smartfrog.zip";

    public static final String sfReleaseName = "smartfrog";
    public static final String sfTemplate = "sfinstaller.vm";
    public static final String sfWorkDir = "work";

    public static final String sfInstallLocationUnix = "."; // create in user home by deault
    public static final String sfInstallLocationWindows = "c:\\";  // create in c:\\ by default
    private static final String DEFAULT_EMAILTO = "none";
    private static final String DEFAULT_EMAILFROM = "none";
    private static final String DEFAULT_EMAILSERVER = "none";

    // Contains one additonal File.separator if server os is windows.
    private String strOptSeparator = "";

    public BootStrap(AvalancheFactory f) {
        // additional separator needed for velocity
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            log.info("Avalanche Server is running on Windows.");
            this.strOptSeparator = File.separator;

		}

		this.factory = f;
        this.avalancheHome = f.getAvalancheHome();
        this.sfDirectory = this.avalancheHome + File.separator + this.strOptSeparator + "smartfrog";
        this.sfBootDirectory = this.sfDirectory + File.separator + this.strOptSeparator + "boot";

	}

	public String getIgnitionPackage() {
		return IgnitionPackage;
	}

	public void setIgnitionPackage(String ignitionPackage) {
		IgnitionPackage = ignitionPackage;
	}

	public String getIgnitionTemplate() {
		return IgnitionTemplate;
	}

	public void setIgnitionTemplate(String ignitionTemplate) {
		IgnitionTemplate = ignitionTemplate;
	}

	/**
     * Ignites a list of hosts. These hosts should exist in Avalanche database, this method
     * picks up host properties and access details from Avalanche database and uses that information
     * to ignite the hosts.
     *
     * @param hosts is the list of hosts to be ignited
	 * @param inPackage The package which should be used for the ignition. Will only be used for this ignition. If you want to set a default package use <code>setIgnitionPackage()</code>.
	 * @param inTemplate The template which should be used for the ignition. Will only be used for this ignition. If you want to set a default package use <code>setIgnitionTemplate()</code>.
     * @throws HostIgnitionException if ignition failed
     */
	public void ignite(String[] hosts, String inPackage, String inTemplate) throws HostIgnitionException {
		String oldPack = IgnitionPackage;
		String oldTempl = IgnitionTemplate;
		IgnitionPackage = inPackage;
		IgnitionTemplate = inTemplate;
		ignite(hosts);
		IgnitionPackage = oldPack;
		IgnitionTemplate = oldTempl;
	}

	/**
     * Ignites a list of hosts. These hosts should exist in Avalanche database, this method
     * picks up host properties and access details from Avalanche database and uses that information
     * to ignite the hosts.
     *
     * @param hosts is the list of hosts to be ignited
     * @throws HostIgnitionException if ignition failed
     */
    public void ignite(String[] hosts) throws HostIgnitionException {
        try {
            HostManager hostManager = factory.getHostManager();
            String xmppServer = factory.getAttribute(AvalancheFactory.XMPP_SERVER_NAME);
            String securityOn = factory.getAttribute(AvalancheFactory.SECURITY_ON);
			
			String templateFile;
			if (IgnitionTemplate == null)
				templateFile = sfBootDirectory + File.separator + this.strOptSeparator + sfTemplate;
			else
				templateFile = IgnitionTemplate;

			String outDir = sfBootDirectory + File.separator + this.strOptSeparator + sfWorkDir;
            String outputFile = outDir + File.separator + this.strOptSeparator + "hostIgnition" + getDateTime() + ".sf";

            ArrayList<Daemon> listDaemons = new ArrayList<Daemon>();
            for (String host : hosts) {
                // Retrieving host information
                HostType h = hostManager.getHost(host);

                // Username and password
                String username = h.getUser();
                String password = h.getPassword();

				// bind ip
				String bindIP = h.getBindIP();

				// Setting the AccessMode
                HostType.AccessModes am = h.getAccessModes();
                String accessType = "ssh";
                if (am != null) {
                    AccessModeType[] modes = am.getModeArray();
                    if (null != modes) {
                        for (AccessModeType mode : modes) {
                            // Set AccessType which is selected as default
                            if (mode.getIsDefault()) {
                                accessType = mode.getType();
                                break;
                            }
                        }
                    }
                }

                // Setting the TransferMode
                String transferType = "scp";
                HostType.TransferModes tm = h.getTransferModes();
                if (null != tm) {
                    DataTransferModeType[] transferModes = tm.getModeArray();
                    if (null != transferModes) {
                        for (DataTransferModeType transferMode : transferModes) {
                            // Set TransferMode which is selected as default
                            if (transferMode.getIsDefault()) {
                                transferType = transferMode.getType();
                                username = transferMode.getUser();
                                password = transferMode.getPassword();
                                break;
                            }
                        }
                    }
                }

                ArgumentType argType = h.getArguments();
                String avalancheInstallationDirectory = null;
                String javaHomeDirectory = null;
                if (null != argType) {
                    ArgumentType.Argument[] args = argType.getArgumentArray();

                    for (ArgumentType.Argument arg : args) {
                        if (arg.getName().equals("JAVA_HOME")) {
                            javaHomeDirectory = arg.getValue();
                        }
                        if (arg.getName().equals("AVALANCHE_HOME")) {
                            avalancheInstallationDirectory = arg.getValue();
                        }
                    }
                }

                String os = h.getPlatformSelector().getOs();

				// decide about the filename, depending on the target os
				String strLocalfile1;
				if (IgnitionPackage == null)
					strLocalfile1 = sfBootDirectory + File.separator + strOptSeparator + (os.equals("windows") ? sfReleaseFileWindows : sfReleaseFileUnix);
				else
					strLocalfile1 = IgnitionPackage;

				// if the install location hasn't been set use the default locations
                if (avalancheInstallationDirectory == null)
                    avalancheInstallationDirectory = (os.equals("windows") ? sfInstallLocationWindows : sfInstallLocationUnix);

                // supporting only windows and unix now.
                log.info("\nStarting Host Ignition..." +
                        "\nHostname: " + host +
                        "\nOperating System: " + os +
                        "\nTransfermode: " + transferType +
                        "\nAccess Mode: " + accessType +
                        "\nUsername: " + username +
                        "\n\nJAVA_HOME: " + javaHomeDirectory +
                        "\nAVALANCHE_HOME:" + avalancheInstallationDirectory);

                if (securityOn.equals("true")) {
                    Daemon d = new Daemon("n" + host,                // name
                            os,                     // os
                            host,               // host
                            transferType,           // transfer type
                            accessType,             // access type
                            username,               // username
                            password,               // password
                            strLocalfile1,          // localfile1
                            null,                   // localfile2
                            null,                   // localfile3
                            sfDirectory + File.separator + strOptSeparator + "dist" + File.separator + strOptSeparator + "private" + File.separator + strOptSeparator + "host1" + File.separator + strOptSeparator + "mykeys.st",                   // keyfile
                            sfDirectory + File.separator + strOptSeparator + "dist" + File.separator + strOptSeparator + "private" + File.separator + strOptSeparator + "host1" + File.separator + strOptSeparator + "SFSecurity.properties",                   // secproperties
                            sfDirectory + File.separator + strOptSeparator + "dist" + File.separator + strOptSeparator + "signedLib" + File.separator + strOptSeparator + "smartfrog.jar",                   // smartfrogjar
                            sfDirectory + File.separator + strOptSeparator + "dist" + File.separator + strOptSeparator + "signedLib" + File.separator + strOptSeparator + "sfServices.jar",                   // servicesjar
                            sfDirectory + File.separator + strOptSeparator + "dist" + File.separator + strOptSeparator + "signedLib" + File.separator + strOptSeparator + "sfExamples.jar",                   // examplesjar
                            sfReleaseName,          // releasename
                            javaHomeDirectory,              // javahome
                            avalancheInstallationDirectory,          // installdir
                            DEFAULT_EMAILTO,        // emailto
                            DEFAULT_EMAILFROM,      // emailfrom
                            DEFAULT_EMAILSERVER,
							bindIP);
                    listDaemons.add(d);

                } else {
                    Daemon d = new Daemon("n" + host,                // name
                            os,                     // os
                            host,               // host
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
                            javaHomeDirectory,              // javahome
                            avalancheInstallationDirectory,          // installdir
                            DEFAULT_EMAILTO,        // emailto
                            DEFAULT_EMAILFROM,      // emailfrom
                            DEFAULT_EMAILSERVER,
							bindIP);
                    listDaemons.add(d);
                }

            }

            String logFileDir = avalancheHome + File.separator + strOptSeparator + "logs" + strOptSeparator;

            // to read from listDaemons and write to data. all and then create a description
            log.info("TemplateGen Map : " + listDaemons);
            TemplateGen.createHostIgnitionTemplate(listDaemons, templateFile, outputFile, securityOn.equals("true"), false, null, logFileDir, xmppServer);

            File of = new File(outputFile);
            if (!of.exists()) {
                throw new HostIgnitionException("Template creation failed! File does not exist: " + outputFile);
            }
            log.info("SF : " + outputFile);

            SmartfrogAdapter adapter = null;

            adapter = new SmartFrogAdapterImpl(sfDirectory + File.separator + this.strOptSeparator + "dist", securityOn.equals("true"));

            SmartFrogAdapterImpl.setLogFilePath(logFileDir);

            HashMap attrMap = new HashMap();

            // run the description on local host for remote deployments.
            adapter.submit(outputFile, attrMap, new String[]{"localhost"});
        } catch (Exception e) {
            log.error(e);
            throw new HostIgnitionException(e);
        }
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        java.util.Date date = new java.util.Date();
        return dateFormat.format(date);
    }

}
