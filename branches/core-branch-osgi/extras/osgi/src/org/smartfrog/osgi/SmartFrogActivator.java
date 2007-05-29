package org.smartfrog.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.SFSystem;

public class SmartFrogActivator implements BundleActivator {
    private ProcessCompound rootProcess = null;

    public void start(BundleContext bundleContext) throws Exception {
        System.out.println("Starting smartfrog...");
        System.setProperty("org.smartfrog.sfcore.processcompound.sfProcessName","rootProcess");
        System.setProperty("org.smartfrog.sfcore.processcompound.sfDefault.sfDefault",
                "org/smartfrog/default.sf");
        System.setProperty("org.smartfrog.iniFile",
                "org/smartfrog/default.ini");
        //System.setProperty("org.smartfrog.sfcore.security.debug","true");
        //System.setProperty("java.security.debug","scl");
        //System.setProperty("java.rmi.server.logCalls","true");
        //System.setProperty("sun.rmi.loader.logLevel","VERBOSE");


        System.out.println("Current thread context CL: " + Thread.currentThread().getContextClassLoader());
        System.out.println("System CL " + ClassLoader.getSystemClassLoader());
        ClassLoader bundleCL = this.getClass().getClassLoader();
        System.out.println("This bundle's CL: " + bundleCL);
        System.out.println("Parent: " + bundleCL.getParent());
        System.out.println("Parent of parent: " + bundleCL.getParent().getParent());
        System.out.println("Parent of parent of parent: " + bundleCL.getParent().getParent().getParent());
        System.out.println("Setting the thread context CL to bundle's CL.");
        Thread.currentThread().setContextClassLoader(bundleCL);
        rootProcess = SFSystem.runSmartFrog();
        System.out.println("SmartFrog daemon running...");
    }

    public void stop(BundleContext bundleContext) throws Exception {
        System.out.println("Stopping smartfrog...");
        rootProcess.sfTerminate(new TerminationRecord("normal", "Stopping daemon", null));
        System.out.println("SmartFrog daemon stopped.");
    }
}
