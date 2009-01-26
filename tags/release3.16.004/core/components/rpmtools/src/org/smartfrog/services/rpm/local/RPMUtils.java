/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.rpm.local;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.logging.LogSF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * @author sandya <p/> Contains RPM utilities to Install, Un-install and Upgrade RPM packages
 */
public class RPMUtils {
    private LogSF log;
    public static final String ERROR_NOT_FOUND = "Not found: ";
    public static final String ERROR_NOT_A_SIMPLE_FILE = "Not a simple file: ";

    /**
     * Constructor which initialises a Runtime object
     *
     * @param log the log to use for messages
     */
    public RPMUtils(LogSF log) {
        this.log = log;
    }

    /**
     * Installs the RPM package without any install-options
     *
     * @param rpmPackage - full path of RPM Package to be installed
     * @return boolean - returns true if Package is installed successfully else return false
     * @throws IOException if an i/o error occurs
     */
    public boolean InstallPackage(String rpmPackage) throws IOException {
        String installOptions = "";
        return (InstallPackage(rpmPackage, installOptions));
    }

    /**
     * Installs the RPM package in the install path provided. Note: Only relocatable RPM packages can use this method.
     *
     * @param rpmPackage  - full path of RPM Package to be installed
     * @param installPath - location where the package need to be installed
     * @return boolean - returns true if Package is installed successfully else return false
     * @throws IOException if an i/o error occurs
     */
    public boolean InstallPackage(String rpmPackage, File installPath)
            throws IOException {
        String installOptions = null;

        if (installPath.exists() && installPath.isDirectory()
                && installPath.canWrite()) {
            String path = installPath.getCanonicalPath();
            installOptions = "--prefix " + path;
        }

        return InstallPackage(rpmPackage, installOptions);
    }

    /**
     * Installs RPM Package with the given install-options.
     *
     * @param rpmPackage     - RPM package to be installed
     * @param installOptions - RPM install options
     * @return boolean - returns true if Package is installed successfully else return false
     * @throws IOException if i/o error occurs
     */
    public boolean InstallPackage(String rpmPackage, String installOptions)
            throws IOException {
        File rpmFile = new File(rpmPackage);
        validateFile(rpmFile);

        String command = "rpm -i ";
        if (installOptions != null) {
            command = "rpm -i " + installOptions;
        }

        String cmd = command + ' ' + rpmPackage;
        boolean error = executeRpmCommand(cmd);
        if (!error) {
            log.info("The RPM package " + rpmPackage + " is successfully installed.");
        }
        return !error;

    }

    private boolean executeRpmCommand(String cmd) throws IOException {
        String line;
        boolean error = false;
        Process p = null;
        p = Runtime.getRuntime().exec(cmd);
        BufferedReader cmdError = null;
        try {
            cmdError = new BufferedReader(new InputStreamReader(p
                    .getErrorStream()));
            while ((line = cmdError.readLine()) != null) {
                error = true;
                log.error(line);
            }
        } finally {
            FileSystem.close(cmdError);
        }
        BufferedReader cmdOutput = null;
        if (!error) {
            try {
                cmdOutput = new BufferedReader(new InputStreamReader(p
                        .getInputStream()));
                while ((line = cmdOutput.readLine()) != null) {
                    error = true;
                    log.error(line);
                }
            } finally {
                FileSystem.close(cmdOutput);
            }
        }
        return error;
    }

    /*
      * Un-installs the RPM package with out any erase-options
      * @param packageName - RPM package name to be un-installed
      * @return boolean - returns true if package is un-installed
      * 						successfully else returns false
      * @throws IOException if i/o error occurs
      */
    public boolean UninstallPackage(String packageName) throws IOException {
        String eraseOptions = "";

        return UninstallPackage(packageName, eraseOptions);
    }

    /*
      * Un-installs the RPM Package with erase-options
      * @param packageName - RPM package name to be un-installed
      * @param eraseOptions - erase options to be used while un-installing
      * @return boolean - returns true if package is un-installed
      * 						successfully else returns false
      * @throws IOException if i/o error occurs
      */
    public boolean UninstallPackage(String packageName, String eraseOptions)
            throws IOException {
        String command = null;
        if (eraseOptions.length() != 0) {
            command = "rpm -e " + eraseOptions + packageName;
        } else {
            command = "rpm -e " + packageName;
        }

        boolean result = executeRpmCommand(command);
        if (result) {
            log.info("The RPM package " + packageName + " is successfully un-installed");
        }
        return result;
    }

    /*
      * Upgrades the RPM package with out any upgrade-options. If a
      * previous version of the package is not found, it installs the
      * given package otherwise it removes all the older versions of
      * the package before upgrading.
      * @param rpmPackage - Upgrades using the RPM package
      * @return boolean - returns true if package is successfully upgraded
      * 						else returns false
      * @throws IOException if i/o exception occurs
      */
    public boolean UpgradePackage(String rpmPackage) throws IOException {
        String upgradeOptions = "";

        return UpgradePackage(rpmPackage, upgradeOptions);
    }

    /* Upgrades the RPM package with the given upgrade-options. If a
      * previous version of the package is not found, it installs the
      * given package otherwise it removes all the older versions of
      * the package before upgrading.
      * @param rpmPackage - Upgrades using the RPM package
      * @return boolean - returns true if package is successfully upgraded
      * 						else returns false
      * @throws IOException if i/o exception occurs
      */
    public boolean UpgradePackage(String rpmPackage, String upgradeOptions)
            throws IOException {
        boolean error;
        File rpmFile = new File(rpmPackage);
        validateFile(rpmFile);



        String command = "rpm -U ";
        if (upgradeOptions.length() != 0) {
            command = "rpm -U " + upgradeOptions;
        }

        String cmd = command + rpmPackage;
        error = executeRpmCommand(cmd);

        if (!error) {
            log.info("The RPM package " + rpmPackage + " is successfully upgraded");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check that an rpm file exists
     *
     * @param rpmFile the file to check
     * @throws FileNotFoundException if anything went wrong
     */
    protected void validateFile(File rpmFile) throws FileNotFoundException {
        if (!rpmFile.exists()) {
            throw new FileNotFoundException("No such file " + rpmFile);
        }

        if (!rpmFile.isFile()) {
            throw new FileNotFoundException("Not an RPM file " + rpmFile);
        }
        checkHasRpmExtension(rpmFile);
    }

    protected void checkHasRpmExtension(File rpmFile) throws FileNotFoundException {
        // Get the file extension to check if it is .rpm file
        String fileName = rpmFile.getName();
        if(!fileName.endsWith(".rpm")) {
            throw new FileNotFoundException("Not an RPM file: "+rpmFile);
        }
    }

    /**
     * This takes a list of packages and builds an upgrade command from them
     *
     * @param executable    executable to run
     * @param basedir        base directory
     * @param packages       packages to upgrade
     * @param actions commands to use to upgrade
     * @param upgradeOptions options
     * @param validateFiles  should we check the files exist? This should be true except when generating a remote
     *                       command, as they only need to exist on the remote machine.
     * @return the commands to run
     * @throws FileNotFoundException if the file cannot be validated and validateFiles==true
     */
    public List<String> buildUpgradeCommand(String executable, String actions, String basedir, List<String> packages,
                                            String upgradeOptions,
                                            boolean validateFiles)
            throws IOException {

        ArrayList<String> commands = new ArrayList<String>(packages.size() + 4);
        commands.add(executable);
        commands.add(actions);
        if (upgradeOptions != null) {
            commands.add(upgradeOptions);
        }
        for (String rpm : packages) {
            File rpmFile = new File(basedir, rpm);
            if (validateFiles) {
                validateFile(rpmFile);
            }
            commands.add(rpm);
        }
        return commands;
    }

}