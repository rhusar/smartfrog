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

package org.smartfrog.services.net;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.passwords.PasswordProvider;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Vector;

/**
 * This SmartFrog FTP Component uses Apache commons net library. It can be used
 * in SmartFrog workflows to upload/download text or binary files.
 *
 * @author Ashish Awasthi
 */
public class FTPClientImpl extends PrimImpl implements SFFTPClient {
    private static final String BINARY = "binary";
    private static final String GET = "get";
    private static final String PUT = "put";
    private boolean shouldTerminate = true;
    private String ftpServer;
    private String port = ""; //default
    private String user = "smartfrog"; //default
    private String password = "password";
    private String transferMode = BINARY; //default
    private String transferType = GET; //default
    private Vector remoteFileList = null; //list of files to transfer
    private Vector localFileList = null; //list of files to transfer
    private FTPClient ftpClient = null; // Apache commons net's FTPClient

    private Reference pwdProviderRef = new Reference("passwordProvider");
    private PasswordProvider pwdProvider = null;

    /**
     * Constructs FTPClientImpl object.
     *
     * @throws RemoteException in case of network/rmi error
     */
    public FTPClientImpl() throws RemoteException {
    }

    /**
     * Reads SmartFrog attributes and deploys FTPClientImpl component.
     *
     * @throws SmartFrogException in case of error in deploying or reading the
     *                            attributes
     * @throws RemoteException    in case of network/emi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
        RemoteException {
        //read SmartFrog Attributes
        readSFAttributes();
        super.sfDeploy();
    }

    /**
     * Sends or retrieve files over FTP using attributes specified in the
     * SmartFrog description of the component.
     *
     * @throws SmartFrogException in case of error in sending email
     * @throws RemoteException    in case of network/emi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        try {
            ftpClient = new FTPClient();
            int reply;

            // get password from password provider
            password = pwdProvider.getPassword();

            ftpClient.connect(ftpServer);

            reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new SmartFrogLifecycleException("FTP Server:["
                    + ftpServer + "] refused connection");
            }

            //login
            if (!ftpClient.login(user, password)) {
                ftpClient.logout();
            }

            //check type of file transfer
            if (BINARY.equals(transferMode)) {
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            }

            // Use passive mode as default because most of us are
            // behind firewalls these days.
            ftpClient.enterLocalPassiveMode();

            if (GET.equals(transferType)) {
                getFiles(remoteFileList, localFileList);
            } else if (PUT.equals(transferType)) {
                putFiles(remoteFileList, localFileList);
            } else {
                throw new SmartFrogLifecycleException(
                    "Unsupported transfer type:" + transferType);
            }
            //logout
            ftpClient.logout();

            // check if it should terminate by itself
            if (shouldTerminate) {
                TerminationRecord termR = TerminationRecord.normal(
                    "FTP finished: ", sfCompleteName());
                TerminatorThread terminator = new TerminatorThread(this, termR);
                terminator.start();
            }
        } catch (FTPConnectionClosedException e) {
            throw new SmartFrogLifecycleException("Server Closed Connection" +
                e,e);
        } catch (IOException ioe) {
            throw new SmartFrogLifecycleException(ioe);
        } finally {
            disconnectQuietly();
        }
    }


    /**
     * Life cycle method for terminating the SmartFrog component.
     *
     * @param tr Termination record
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        disconnectQuietly();
        super.sfTerminateWith(tr);
    }

    /**
     * Disconnect quietly if needed. Errors are logged at the ignore level
     * */
    private void disconnectQuietly() {
        if ((ftpClient != null) && (ftpClient.isConnected())) {
            try {
                ftpClient.disconnect();
            } catch (IOException failure) {
                sfLog().ignore("when disconnecting the FTP link ", failure);
            } finally {
                ftpClient = null;
            }
        }
    }

    /**
     * Reads SmartFrog attributes.
     *
     * @throws SmartFrogResolutionException if failed to read any attribute or a
     *                                      mandatory attribute is not defined.
     * @throws RemoteException              in case of network/rmi error
     */
    private void readSFAttributes() throws SmartFrogResolutionException,
        RemoteException {
        // Mandatory attributes 
        ftpServer = sfResolve(FTP_HOST, ftpServer, true);
        user = sfResolve(USER, user, true);
        remoteFileList = sfResolve(REMOTE_FILES, remoteFileList, true);
        localFileList = sfResolve(LOCAL_FILES, localFileList, true);
        pwdProvider = (PasswordProvider) sfResolve(pwdProviderRef);
        // optional attributes
        transferType =
            sfResolve(TRANSFER_TYPE, transferType, false).toLowerCase(
                Locale.ENGLISH);
        transferMode =
            sfResolve(TRANSFER_MODE, transferMode, false).toLowerCase(
                Locale.ENGLISH);
        shouldTerminate = sfResolve(TERMINATE, shouldTerminate, false);
    }

    /**
     * Gets files from the FTP server.
     *
     * @param remoteFiles vector of remote file names
     * @param localFiles  vector of corresponding local file names
     * @throws IOException in case not able to transfer files
     */
    private void getFiles(Vector remoteFiles, Vector localFiles)
        throws IOException {
        OutputStream output;
        for (int index = 0; index < localFiles.size(); index++) {
            output = null;
            String localFile = (String) localFiles.elementAt(index);
            String remoteFile = (String) remoteFiles.elementAt(index);
            try {
                output = new FileOutputStream(localFile);
                ftpClient.retrieveFile(remoteFile, output);
            } finally {
                // close output stream
                FileSystem.close(output);
            }
        }
    }

    /**
     * Sends files to FTP server.
     *
     * @param remoteFiles vector of remote file names
     * @param localFiles  vector of corresponding local file names
     * @throws IOException in case not able to transfer files
     */
    private void putFiles(Vector remoteFiles, Vector localFiles)
        throws IOException {
        for (int index = 0; index < localFiles.size(); index++) {
            InputStream input = null;
            try {
                String localFile = (String) localFiles.elementAt(index);
                String remoteFile = (String) remoteFiles.elementAt(index);
                input = new FileInputStream(localFile);
                ftpClient.storeFile(remoteFile, input);
            } finally {
                FileSystem.close(input);
            }
        }
    }

}
