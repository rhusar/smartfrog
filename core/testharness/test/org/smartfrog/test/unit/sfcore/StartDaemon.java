package org.smartfrog.test.unit.sfcore;

import java.io.File;
import java.io.FilenameFilter;
import java.rmi.ConnectException;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.security.*;

public class StartDaemon {

    private static final String fileSeparator = File.separator;

    private static final String pathSeparator = System.getProperty("path.separator");

    private static String sfHome = null;
    private static String distPath = null;
    private static String iniFile = null;
    private static String sfDefault = null;
    static ProcessCompound sfDaemon = null;

    public StartDaemon(String sfHomePath) throws Exception {
        sfHome = sfHomePath;
        distPath = sfHome + fileSeparator + "lib" + fileSeparator;
        iniFile = sfHome + fileSeparator + "bin" + fileSeparator + "default.ini";
        sfDefault = sfHome + fileSeparator + "bin" + fileSeparator + "default.sf";
        /* try {
            getSFDaemon();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        if (sfDaemon != null) {
            System.out.println("sfDaemon Ready");
        } else {
            System.out.println("sfDaemon NOT Ready. Something went wrong");
        }*/
    }

    static public void main(String[] args) throws Exception {
        sfHome = args[0];
        distPath = sfHome + fileSeparator + "lib" + fileSeparator;
        iniFile = sfHome + fileSeparator + "bin" + fileSeparator + "default.ini";
        sfDefault = sfHome + fileSeparator + "bin" + fileSeparator + "default.sf";
        try {
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

    public static ProcessCompound getSFDaemon() throws Exception {
        try {  // there is a Daemon  running in local system
            sfDaemon = SFProcess.getRootLocator().getRootProcessCompound(null, 3800);
        } catch (ConnectException cEx) {  // there is no Daemon  running in local system
            setSFDaemonEnv();
            sfDaemon = SFSystem.runSmartFrog();
        }
        return sfDaemon;
    }

    private static void setSFDaemonEnv() {
        //set system properties for starting the Daemon
        System.setProperty("org.smartfrog.sfcore.processcompound.sfProcessName", "rootProcess");
        System.setProperty("org.smartfrog.iniFile", iniFile);
        System.setProperty("org.smartfrog.sfcore.processcompound.sfDefault.sfDefault", sfDefault);

        // SmartFrog dist jar files
        File[] sfBaseJars = (new File(distPath)).listFiles(new FilenameFilter() {
            public boolean accept(File f, String s) {
                if (s.endsWith(".jar")) {
                    return true;
                }
                return false;
            }
        });
        // Set the classpath to includes all jar files in dist lib directory
        String baseClasssPath = "";
        for (File sfBaseJar : sfBaseJars) {
            baseClasssPath += sfBaseJar.getAbsolutePath() + pathSeparator;
        }
        System.setProperty("java.class.path", System.getProperty("java.class.path") + pathSeparator + baseClasssPath);
    }

}
