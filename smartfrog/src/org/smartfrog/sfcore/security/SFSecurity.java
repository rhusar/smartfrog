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

package org.smartfrog.sfcore.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.JarURLConnection;
import java.security.cert.Certificate;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;

import org.smartfrog.sfcore.processcompound.SFServerSocketFactory;

import org.smartfrog.sfcore.security.rmispi.SFRMIClassLoaderSpi;
import org.smartfrog.sfcore.security.rmispi.ClassLoaderRegistry;


/**
 * Provides basic security functionality to SF, i.e., triggers the
 * initialization of all the security mechanisms (this should be called right
 * at the beginning of SF startup) or returns information about the security
 * environment. We assume JDK 1.4 so crypto and JSSE is already fully
 * integrated.
 *
 */
public class SFSecurity {
    /** A flag that ensures only one initialization. */
    private static boolean alreadyInit = false;

    /** A security environment shared by all the local SF components */
    private static SFSecurityEnvironment securityEnv;

    /** A RMIServerSocketFactory used when security is off */
    private static SFServerSocketFactory nonSecServerSocketFactory;

     private static Registry realRMIRegistry;
 
    /**
     * Initializes the security using system properties to decide on the level
     * of security required.
     *
     * @throws SFGeneralSecurityException if error initializing security.
     */
    synchronized public static void initSecurity()
        throws SFGeneralSecurityException {
        try {
            if (!alreadyInit) {
                // Add the new RMIClassLoaderSpi
                System.setProperty("java.rmi.server.RMIClassLoaderSpi",
                    "org.smartfrog.sfcore.security.rmispi.SFRMIClassLoaderSpi");
                SFSecurityProperties.readSecurityProperties();

                if (Boolean.getBoolean(SFSecurityProperties.propSecurityOn)) {
                    // Activate the real security manager.
                    System.setSecurityManager(new SecurityManager());

                    securityEnv = new SFSecurityEnvironmentImpl(null);

                    /*Make sure that we restrict downloading of stubs/RMIClientFactory
                       Note that this only works if this is called before RMI
                       classes are loaded... */
                    /* Not needed after using SFRMIClassLoaderSpi
                       System.setProperty(SFSecurityProperties.propUseCodebaseOnly,"true");
                     */
                    /* Set the java.rmi.server.codebase to the value of
                       org.smartfrog.codebase unless it was explicitly set. This
                       allow us to respect propUseCodebaseOnly by just setting the
                       SF codebase. */
                    /* No longer can rely on  java.rmi.server.codebase because
                       it is *static*
                       if (SFClassLoader.getTargetClassBase() != null) {
                         String currentCodebase =
                           System.getProperty("java.rmi.server.codebase");
                         if (currentCodebase == null) {
                           System.setProperty("java.rmi.server.codebase",
                                              SFClassLoader.getTargetClassBase());
                         } else if (!(currentCodebase.equals(SFClassLoader.
                                                             getTargetClassBase()))) {
                           System.out.println("WARNING: java.rmi.server.codebase "+
                                              "already set to " + currentCodebase +
                                               " that is different to  " +
                                              "org.smartfrog.codebase set to " +
                                              SFClassLoader.getTargetClassBase());
                         }
                       }
                       //Make objectIDs difficult to guess.
                       System.setProperty(SFSecurityProperties.propRandomIDs,"true");
                       /* Activate a safe default RMISocketFactory. This could be
                          modified by the one in the object reference, provided that
                          its class can be found in our codebase. For this reason,
                          we have to be *very* careful on what RMIClientSocketFactory
                          classes are available in our codebase...*/
                    RMISocketFactory.setSocketFactory(securityEnv.getRMISocketFactory());
                    SFRMIClassLoaderSpi.setSecureResourcesOff(Boolean.getBoolean(SFSecurityProperties.propSecureResourcesOff));
                    SFRMIClassLoaderSpi.setSecurityOn(true);
                } else {
                    //System.setSecurityManager(new DummySecurityManager());
                    // if a java.security.policy is set then we initialize standard java security
                    // This is necessary for dynamic classloading to work.
                    String secPro = System.getProperty("java.security.policy");
                    if  (secPro != null) {
                        System.setSecurityManager(new SecurityManager());
                    }

                    SFRMIClassLoaderSpi.setSecurityOn(false);
                    //Notification moved to SFSyten after the ini file is read.
                }
            }
        } catch (IOException e) {
            // Problems setting up RMI.
            throw (SFGeneralSecurityException)new SFGeneralSecurityException(e.getMessage()).initCause(e);
        }
    }

    /**
     * Gets the security environment shared by all the local SF components.
     *
     * @return The security environment shared by all the local SF components.
     */
    synchronized static SFSecurityEnvironment getSecurityEnvironment() {
        checkSFCommunity();
        return securityEnv;
    }

    /**
     * Checks that the calling stack has the SFCommunityPermission, i.e., all
     * the code involved is signed by a trusted key.
     */
    public static void checkSFCommunity() {
        SFRMIClassLoaderSpi.checkSFCommunity();
    }

    public static InputStream getSecureInputStream(URL url) throws IOException {
        return getSecureInputStream(url.openConnection());
    }

    /**
     * Checks that the resource pointed by a URLConnection comes from a trusted
     * source, this is, it has been granted the SFCommunityPermission. If this
     * is not the case it throws a security exception. Then, it uses that URL
     * to obtain an input stream to a locally cached object.
     *
     * @param con URLConnection to the resource to be checked.
     * @return Stream that point to the resource. If the resource is in a
     *         signed jar we return a ByteArrayInputStream to a local copy for
     *         security reasons.
     * @throws java.io.IOException in case of any error
     */
    public static InputStream getSecureInputStream(URLConnection con) throws IOException {
        InputStream in = con.getInputStream();
        Certificate[] certs = null;

        if (con instanceof JarURLConnection) {
            // Loaded from a jar file, let's add the certicates.
            JarURLConnection conJar = (JarURLConnection) con;

            // Need to read the full entry so that I can get the certificates.
            int numBytes = in.available();
            byte[] resourceBytes = new byte[numBytes];
            int readBytes = 0;

            while (readBytes != numBytes) {
                // Sometimes the read returns early...
                readBytes += in.read(resourceBytes, readBytes,
                        numBytes - readBytes);
            }

            certs = conJar.getCertificates();

            // Need to return an InputStream to a local copy to avoid that
            // the entry changes after being checked.
            in = new ByteArrayInputStream(resourceBytes);
        }

        CodeSource cs = new CodeSource(con.getURL(), certs);
        Policy pc = Policy.getPolicy();
        PermissionCollection perms = pc == null ? null : pc.getPermissions(cs);
        SFRMIClassLoaderSpi.quickReject(new ProtectionDomain(cs, perms));

        // No security exception, continues...
        return in;
    }
    
    public static void checkSecurity(Class clazz) {
        SFRMIClassLoaderSpi.quickReject(clazz.getProtectionDomain());
    }

	/**
     * Returns whether the SF security checks are active or not. This can only
     * be changed once at initialization time for security reasons.
     *
     * @return whether the SF security is active.
     */
    public static synchronized boolean isSecurityOn() {
        return SFRMIClassLoaderSpi.isSecurityOn();
    }


    /**
     * Returns whether the SF security checks for resources are active or not. This can only
     * be changed once at initialization time for security reasons and does not apply to classes.
     *
     * @return whether the SF security is active.
     */
    public static boolean isSecureResourcesOff (){
        return    SFRMIClassLoaderSpi.isSecureResourcesOff();
    }

    /**
     * Creates and exports a <code>Registry</code> on the local host that
     * accepts requests on the specified <code>port</code>.
     *
     * @param port the port on which the registry accepts requests
     * @param bindAddr The address to bind on
     * @return the registry
     *
     * @throws RemoteException if the registry could not be exported
     *
     * @since JDK1.1
     */
    public static Registry createRegistry(int port, InetAddress bindAddr) throws RemoteException {
        if (isSecurityOn()) {
            realRMIRegistry = LocateRegistry.createRegistry(port,
            securityEnv.getEmptyRMIClientSocketFactory(),
            securityEnv.getRMIServerSocketFactory());
        } else {
            nonSecServerSocketFactory = new SFServerSocketFactory(bindAddr);
            realRMIRegistry = LocateRegistry.createRegistry(port, null, nonSecServerSocketFactory);
        }
        return realRMIRegistry;
    }

    /**
     * Returns a reference to the remote object <code>Registry</code> on the
     * specified <code>host</code> and <code>port</code>. If <code>host</code>
     * is <code>null</code>, the local host is used.
     *
     * @param host host for the remote registry
     * @param port port on which the registry accepts requests
     *
     * @return reference (a stub) to the remote object registry
     *
     * @throws RemoteException if the reference could not be created
     *
     * @since JDK1.1
     */
    public static Registry getRegistry(String host, int port)
        throws RemoteException {
        if (isSecurityOn()) {
            return LocateRegistry.getRegistry(host, port,
                securityEnv.getRMIClientSocketFactory());
        } else {
            return LocateRegistry.getRegistry(host, port);
        }
    }

    public static Registry getNonStubRegistry() {
        return realRMIRegistry;
    }

    /**
     * Used inside a method call invoked by the RMI Server to find out
     * authenticated information of our peer that called this function
     * remotely.
     *
     * @return Authenticated information about our peer.
     */
    public static String getPeerAuthenticatedSubjects() {
        SFSocket inSocket = SFInputStream.getCurrentSocket();

        return inSocket != null ? inSocket.getPeerAuthenticatedSubjects() : null;
    }

    public static synchronized void cleanShutdown() {
        alreadyInit = false;
        securityEnv = null;
        realRMIRegistry = null;
        SecureRemoteObject.cleanShutdown();
        ClassLoaderRegistry.cleanShutdown();
    }
}
