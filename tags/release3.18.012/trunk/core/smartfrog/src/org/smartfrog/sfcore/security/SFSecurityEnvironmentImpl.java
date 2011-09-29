/** (C) Copyright 1998-2009 Hewlett-Packard Development Company, LP

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
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.net.InetAddress;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;


/**
 * A security environment composed of SF private keys, certificates and trust
 * assumptions in a way that JSSE can use them to configure a secure
 * connection.
 *
 */
public class SFSecurityEnvironmentImpl implements SFSecurityEnvironment {
    /** Cipher suites enabled in SSL */
    private final static String[] ENABLED_CIPHERS = {
        "SSL_RSA_WITH_3DES_EDE_CBC_SHA"
    };

    /**
     * The key store where we keep our private key, certificates and trust
     * assumptions
     */
    private KeyStore ks;

    /** The name of the key store resource. */
    private String keyStoreName = "sfkeys.st";

   /** the type of the keystore */
    private static final String keyStoreType = "JCEKS";

    /** The password needed to unlock the key store. */
    private String keyStorePasswd = "pleasechange";

    /** A set of key managers associated with our key store used by JSSE. */
    private KeyManager[] keyManagers;

    /**
     * A set of trust managers associated with our key store and used by JSSE.
     */
    private TrustManager[] trustManagers;

    /** A context for the client's and server's secure socket factory. */
    private SSLContext context;

    /** A factory to create client's and server's SSL sockets. */
    private SSLSocketFactory sslsf;

    /** A RMI wrapper factory to the client's SSL factory. */
    private RMIClientSocketFactory rmicsf;

    /** A RMI wrapper factory to the server's SSL factory. */
    private RMIServerSocketFactory rmissf;
    /** Binding Address for RMIServerSocketFactory */
    private InetAddress rmissfBindAddr=null;


    /** A RMI wrapper factory to the client's and server's SSL factories. */
    private RMISocketFactory rmisf;

    /** A debugging utility to print messages. */
    private SFDebug debug;



   /**
     * Constructs SFSecurityEnvironmentImpl. Initializes key store, key
     * managers,trust managers, contexts, SSL socket factories and RMI socket
     * factories
     * <p>
     * If the bind address is <code>null</code>, then the system will pick up
     * an ephemeral port and a valid local address to bind the socket.
     * <P>
     * @param bindAddr bind address
     * @throws SFGeneralSecurityException if error during initalization
     */
    public SFSecurityEnvironmentImpl(InetAddress bindAddr) throws SFGeneralSecurityException {
        // Init debugging.
        debug = SFDebug.getInstance("SFSecurityEnvironmentImpl");
        rmissfBindAddr = bindAddr;
        // Check that requests comes from trusted code.
        SFSecurity.checkSFCommunity();

        // Open my keys/certificates store.
        initKeyStore();

        // Activate my private keys from KeyStore.
        initKeyManagers();

        // Activate my trust assumptions.
        initTrustManagers();

        //Initializes SSL contexts for client and server.
        initContexts();

        // Activate SSL socket factories associated with the above.
        initSSLSocketFactories();

        //Acctivate RMI socket factory that wrap ssl factories.
        initRMISocketFactories();
    }

    /**
     * Initializes the key store that contains our private key, certificates
     * and trust assumptions. This includes using a password to check its
     * integrity.
     *
     * @throws SFGeneralSecurityException Cannot open the key store.
     */
    private void initKeyStore() throws SFGeneralSecurityException {
        try {
            // Initialize defaults from the environment.
            keyStorePasswd = System.getProperty(SFSecurityProperties.propKeyStorePasswd,
                    keyStorePasswd);
            keyStoreName = System.getProperty(SFSecurityProperties.propKeyStoreName,
                    keyStoreName);

            // Open and check integrity of the key store.
            ks = KeyStore.getInstance(keyStoreType);
            ks.load(SFClassLoader.getResourceAsStream(keyStoreName),
                keyStorePasswd.toCharArray());
        } catch (Exception e) {
            throw new SFGeneralSecurityException(e);
        }
    }

    /**
     * Initializes a set of key managers that JSSE needs, using as base the
     * keys in our key store.
     *
     * @throws SFGeneralSecurityException Cannot create the key managers.
     */
    private void initKeyManagers() throws SFGeneralSecurityException {
        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, keyStorePasswd.toCharArray());
            keyManagers = kmf.getKeyManagers();
        } catch (Exception e) {
            throw new SFGeneralSecurityException(e);
        }
    }

    /**
     * Initializes a set of trust managers that JSSE needs, using as base the
     * keys in our key store.
     *
     * @throws SFGeneralSecurityException Cannot create the trust managers.
     */
    private void initTrustManagers() throws SFGeneralSecurityException {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);
            trustManagers = tmf.getTrustManagers();
        } catch (Exception e) {
            throw new SFGeneralSecurityException(e);
        }
    }

    /**
     * Initializes SSL contexts for client and server operation modes.
     *
     * @throws SFGeneralSecurityException Cannot create the SSL contexts for
     *            client/server.
     */
    private void initContexts() throws SFGeneralSecurityException {
        try {
            context = SSLContext.getInstance("TLS");
            context.init(keyManagers, trustManagers, null); //default RNG for now.
        } catch (Exception e) {
            throw new SFGeneralSecurityException(e);
        }
    }

    /**
     * Initializes the factory that create SSL sockets for the clients and
     * server.
     *
     * @throws SFGeneralSecurityException Cannot create factory.
     */
    private void initSSLSocketFactories() throws SFGeneralSecurityException {
        /* We are currently using the same context for sockets created in
         * client or server mode. Also, we do not need SSLServerSockets at
         * all. */
        sslsf = context.getSocketFactory();

        if (sslsf == null) {
            throw new SFGeneralSecurityException("initSSLSocketFactories::" +
                "Cannot create the " + "SocketFactory");
        }
    }

    /**
     * Initializes RMI wrappers for the SSL factories.
     *
     * @throws SFGeneralSecurityException Cannot create factories.
     */
    private void initRMISocketFactories() throws SFGeneralSecurityException {
        rmicsf = new SFClientSocketFactory(this);
        rmissf = new SFServerSocketFactory(rmissfBindAddr,this);
        rmisf = new SFRMISocketFactory(rmicsf, rmissf);
    }

   /**
    * Get the keystore Entry associated with a specific alias
    */
    KeyStore.Entry getEntry (String alias) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException {
      return ks.getEntry(alias, new KeyStore.PasswordProtection(keyStorePasswd.toCharArray()));
    }

    /**
     * Gets a set of key managers associated with SF private keys, in the key
     * store, that JSSE needs.
     *
     * @return A set of key managers associated with SF private keys.
     */
    KeyManager[] getKeyManagers() {
        return keyManagers;
    }

    /**
     * Gets a set of trust managers, associated with SF trust assumptions in
     * the key store, that JSSE needs.
     *
     * @return A set of trust managers associated with SF trust assumptions in
     *         the key store.
     */
    TrustManager[] getTrustManagers() {
        return trustManagers;
    }

    /**
     * Returns a socket factory that can be used by RMI to establish a JSSE
     * secure channnel.
     *
     * @param useClientMode If the factory generates SSL sockets that initiate
     *        the handshake.
     *
     * @return A socket factory that can be used by RMI to establish a JSSE
     *         secure channnel.
     */
    public SSLSocketFactory getSSLSocketFactory(boolean useClientMode) {
        /* We are currently using the same context for sockets created in
         * client or server mode. Also, we do not need SSLServerSockets at
         * all. */
        return sslsf;
    }

    /**
     * Gets a  RMI wrapper factory to the client's SSL factory.
     *
     * @return A  RMI wrapper factory to the client's SSL factory.
     */
    public RMIClientSocketFactory getRMIClientSocketFactory() {
        return rmicsf;
    }

    /**
     * Gets a  RMI wrapper factory to the client's SSL factory (without
     * initialized).
     *
     * @return A  RMI wrapper factory to the client's SSL factory (without
     *         initialized).
     */
    public RMIClientSocketFactory getEmptyRMIClientSocketFactory() {
        return new SFClientSocketFactory();
    }

    /**
     * Gets a  RMI wrapper factory to the server's SSL factory.
     *
     * @return A  RMI wrapper factory to the server's SSL factory.
     */
    public RMIServerSocketFactory getRMIServerSocketFactory() {
        return rmissf;
    }

    /**
     * Gets an RMISocketFactory that is "safe" enough for the default case.
     * This will be installed as part of the security initialization. However,
     * this default factory will be overwritten by the one specified in the
     * object reference, so we should not expect that this "minimum security"
     * will always be enforced.
     *
     * @return An RMISocketFactory that is "safe" enough for the default case,
     *         i.e., when the object reference does not specify otherwise.
     */
    public RMISocketFactory getRMISocketFactory() {
        return rmisf;
    }

    /**
     * Handles a SFSocket with a SSL socket attached to it. This process
     * includes configuring it depending on use mode and possibly waiting
     * until the secure session gets established, and take an action according
     * to the resulting peer characteristics.
     *
     * @param sfs A SFSocket with a SSL socket attached.
     *
     * @throws IOException Failure while configuring or establishing secure
     *            session.
     */
    public void handleSocket(SFSocket sfs) throws IOException {
        SSLSocket ssls = sfs.getSSLSocket();

        if (ssls == null) {
            throw new IOException("SFSecurityEnvironment:" +
                "handleSocket:: NULL ssl" + sfs);
        }

        ssls.setEnabledCipherSuites(ENABLED_CIPHERS);
        ssls.setUseClientMode(sfs.getUseClientMode());

        if (!sfs.getUseClientMode()) {
            // Server mode.
            ssls.setNeedClientAuth(true);
        }

        /*DO NOT REMOVE THIS LINE
         * Although it looks it is not doing anything, there is a
         * bug in SUN jsse 1.0.2 and the configuration settings only get
         * "flushed" when we call setUseClientMode. Also, because of
         * dependencies between setNeedClientAuth and setUseClientMode we
         * have to call setUseClientMode twice...*/
        // Flushing all the changes...
        ssls.setUseClientMode(sfs.getUseClientMode());

        authenticatePeer(sfs);
    }

    /**
     * Maps the security credentials of our peer into "subjects" associated
     * with him. We later can map these subjects into permissions, i.e. like
     * in JAAS. These subjects are attached to the SFSocket, and this socket
     * will get attached to the thread that is doing the method invocation.
     *
     * @param sfs A socket to which we will attach the authenticated subjects.
     *
     * @throws IOException Error while establishing the secure session or
     *            authenticating our peer.
     */
    private void authenticatePeer(SFSocket sfs) throws IOException {
        SSLSocket ssls = sfs.getSSLSocket();

        // This blocks until the handshake is done.
        SSLSession session = ssls.getSession();

        // Only "valid" certificates should be returned here.
        Certificate[] certs = session.getPeerCertificates();

        /* Here is where we can put a hook for "extra" checks on the
         * certificates, although the basic X509 checks have already been
         * done by TrustManager while establishing the session. */
        if (debug != null) {
            debug.println("authenticatePeer session " + session +
                "client certificates " + certs);
        }

        /* We could run here some authentication, i.e. JAAS, if we are
         * the server and the certificates were not enough. Since we don't
         * support multiple users now, we just give them the default
         * subject. */
        sfs.setPeerAuthenticatedSubjects("SFCommunity Member" +
            certs[0].toString());
    }
}
