/*
* Copyright 2006 Sun Microsystems, Inc.  All Rights Reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
*   - Redistributions of source code must retain the above copyright
*     notice, this list of conditions and the following disclaimer.
*
*   - Redistributions in binary form must reproduce the above copyright
*     notice, this list of conditions and the following disclaimer in the
*     documentation and/or other materials provided with the distribution.
*
*   - Neither the name of Sun Microsystems nor the names of its
*     contributors may be used to endorse or promote products derived
*     from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
* IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
* THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
* EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
* PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
* LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.smartfrog.tools.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * This is Sun's InstallCertificate code from {@link http://blogs.sun.com/andreas/resource/InstallCert.java}
 * turned into an Ant task.
 * <p/>
 *
 * It takes the following attributes: url, the URL of a server, passphrase - a passphrase for the server, and
 * (optionally) timeout, a timeout for operations
 * certificateIndex declares which certificate from the set to install
 */
public class InstallCertificate extends Task {

    private String url;
    private String passphrase;
    private int timeout = 10000;
    private int certificateIndex = -1;

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setPassphrase(final String passphrase) {
        this.passphrase = passphrase;
    }

    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }

    public void setCertificateIndex(final int certificateIndex) {
        this.certificateIndex = certificateIndex;
    }

    @Override
    public void execute() throws BuildException {
        try {
            if (url == null) {
                throw new BuildException("No url attribute");
            }
            URL target;
            try {
                target = new URL(url);
            } catch (MalformedURLException e) {
                throw new BuildException("Bad URL " + url, e);
            }
            String host = target.getHost();
            //set the port, defaulting to 443 if not set
            int port = target.getPort();
            if (port == -1) {
                port = 443;
            }
            char[] passphraseArray;
            passphraseArray = (passphrase != null) ? passphrase.toCharArray() : null;

            File file = new File("jssecacerts");
            if (!file.isFile()) {
                char SEP = File.separatorChar;
                File dir = new File(System.getProperty("java.home") + SEP
                        + "lib" + SEP + "security");
                file = new File(dir, "jssecacerts");
                if (!file.isFile()) {
                    file = new File(dir, "cacerts");
                }
            }
            log("Loading KeyStore " + file + " with "
                    + (passphraseArray == null ? "null"
                    : ("" + passphrase.length() + " char"))
                    + " passphrase ");
            InputStream in = new FileInputStream(file);
            KeyStore ks;
            try {
                ks = KeyStore.getInstance(KeyStore.getDefaultType());
                ks.load(in, passphraseArray);
            } finally {
                in.close();
            }

            SSLContext context = SSLContext.getInstance("TLS");
            TrustManagerFactory tmf =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
            SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
            context.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory factory = context.getSocketFactory();

            log("Opening connection to " + host + ":" + port + "...");
            SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
            socket.setSoTimeout(timeout);
            try {
                log("Starting SSL handshake...");
                socket.startHandshake();
                socket.close();
                log("No errors, certificate is already trusted");
            } catch (SSLException e) {
                log("server is not yet trusted", e, Project.MSG_INFO);
            }

            X509Certificate[] chain = tm.chain;
            if (chain == null) {
                log("Could not obtain server certificate chain");
                return;
            }

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(System.in));

            log("Server sent " + chain.length + " certificate(s):");
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            for (int i = 0; i < chain.length; i++) {
                X509Certificate cert = chain[i];
                log(" " + (i + 1) + " Subject " + cert.getSubjectDN());
                log("   Issuer  " + cert.getIssuerDN());
                sha1.update(cert.getEncoded());
                log("   sha1    " + toHexString(sha1.digest()));
                md5.update(cert.getEncoded());
                log("   md5     " + toHexString(md5.digest()));
            }

            if (certificateIndex >= 0) {
                int k;
                k = certificateIndex;

                X509Certificate cert = chain[k];
                String alias = host + "-" + (k + 1);
                ks.setCertificateEntry(alias, cert);

                OutputStream out = new FileOutputStream("jssecacerts");
                try {
                    ks.store(out, passphraseArray);
                } finally {
                    out.close();
                }

                log("Added certificate " + cert + " to keystore 'jssecacerts' using alias '"
                        + alias + "'");
            }
        } catch (KeyStoreException e) {
            throw new BuildException(e);
        } catch (IOException e) {
            throw new BuildException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new BuildException(e);
        } catch (CertificateException e) {
            throw new BuildException(e);
        } catch (KeyManagementException e) {
            throw new BuildException(e);
        }

    }

    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
            sb.append(' ');
        }
        return sb.toString();
    }

    private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }

}

