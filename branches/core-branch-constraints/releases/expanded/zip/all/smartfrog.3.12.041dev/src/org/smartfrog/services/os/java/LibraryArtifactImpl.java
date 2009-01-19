/* (C) Copyright 2005-2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.os.java;

import org.smartfrog.services.filesystem.FileIntf;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.filesystem.FileUsingCompoundImpl;
import org.smartfrog.services.os.download.Download;
import org.smartfrog.services.os.download.DownloadImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.rmi.RemoteException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.utils.SmartFrogThread;

/**
 * Implementation of a library artifact.
 * This is where all download logic is implemented.
 * created 04-Apr-2005 13:38:47
 */

public class LibraryArtifactImpl extends FileUsingCompoundImpl
        implements LibraryArtifact, Runnable {

    /**
     * 
     */
    public static final String ERROR_NO_DOWNLOAD = "The network policy prevents the download of ";
    private Library owner;
    private Vector<String> repositories;
    private boolean syncDownload;
    private String sha1;
    private String md5;
    private String extension;
    private String artifact;
    private String project;
    private String classifier;
    private String version;
    private boolean exists;
    private String remoteUrlPath;
    private int blocksize;
    private boolean downloadIfAbsent;
    private boolean downloadAlways;
    private boolean failIfNotPresent;
    private File copyTo;

    private Log log;

    public static final String ERROR_CHECKSUM_FAILURE = "Checksum mismatch on file ";

    /**
     * block size for downloads and digests {@value}
     */
    public static final int BLOCKSIZE = 8192;

    /**
     * Error text when there is no repository entry anywhere
     */
    public static final String ERROR_NO_OWNER = "No owner repository defined";
    /**
     * Error text when repostiories == [] and a download was needed.
     */
    public static final String ERROR_NO_REPOSITORIES = "No repositories to download from";

    /**
     * Error when a file was not found in any repository
     */
    public static final String ERROR_ARTIFACT_NOT_FOUND = "Artifact not found at ";

    private volatile SmartFrogThread thread;
    
    /**
     *  Maximum age in the cache
     */
    private int maxCacheAge;
    public static final String NOT_FOUND_LOCALLY = "\nwhich was not found locally at ";

    public LibraryArtifactImpl() throws RemoteException {
    }

    /**
     * Retrieve our file from our parent libraries.
     * <p/>
     * <ol> <li> locate parent Library implementation <li> get information about
     * repository cache <li> work out names of remote URL, local filename <li>
     * bind our localfilename <li> fetch the jar (or fail) <li> validate the JAR
     * (or fail) <li> </ol>
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        log = sfGetApplicationLog();
        Prim ownerPrim = sfResolve(ATTR_LIBRARY, (Prim)null, true);
        owner = (Library) ownerPrim;
        repositories = ListUtils.resolveStringList(ownerPrim, 
                new Reference(Library.ATTR_REPOSITORIES),
                true);
        syncDownload = sfResolve(ATTR_SYNCHRONOUS, syncDownload, true);
        project = sfResolve(ATTR_PROJECT, project, true);
        version = sfResolve(ATTR_VERSION, version, false);
        artifact = sfResolve(ATTR_ARTIFACT, artifact, true);
        extension = sfResolve(ATTR_EXTENSION, extension, true);
        sha1 = sfResolve(ATTR_SHA1, sha1, false);
        md5 = sfResolve(ATTR_MD5, md5, false);
        blocksize = sfResolve(ATTR_BLOCKSIZE, BLOCKSIZE, false);
        downloadIfAbsent = sfResolve(ATTR_DOWNLOAD_IF_ABSENT,
                downloadIfAbsent,
                true);
        downloadAlways = sfResolve(ATTR_DOWNLOAD_ALWAYS,
                downloadAlways,
                true);
        failIfNotPresent = sfResolve(ATTR_FAIL_IF_NOT_PRESENT,
                failIfNotPresent,
                true);
        classifier = sfResolve(ATTR_CLASSIFIER, classifier, false);
        copyTo = FileSystem.lookupAbsoluteFile(this,ATTR_COPYTO,null,null,false,null);
        maxCacheAge = sfResolve(Download.ATTR_MAX_CACHE_AGE, maxCacheAge, true);

        //all info is fetched. So work out our filename and URL.
        //we do this through methods for override points

        //artifact is project if nothing else is set.
        if (artifact == null) {
            artifact = project;
        }
        remoteUrlPath = makeRemoteUrlPath();
        File localFile = makeLocalFile();
        //get the superclass to do our binding
        bind(localFile);
        checkExistence();

        //if we dont exist, we fetch
        boolean mustDownload;
        mustDownload = (!exists && downloadIfAbsent) || downloadAlways;

        if (mustDownload) {
            // Bail out if we cannot download an artifact that we must
            if (remoteUrlPath == null) {
                throw new SmartFrogDeploymentException(
                                ERROR_NO_DOWNLOAD 
                                + toString() 
                                + NOT_FOUND_LOCALLY
                                +localFile);
            }
            if (syncDownload) {
                download();
                postDownloadActions(mustDownload);
            } else {
                thread = new SmartFrogThread(this);
                thread.start();
            }

        } else {
            //no download, do whatever the post d/l actions are
            postDownloadActions(false);
        }
        
    }

    /**
     * {@inheritDoc}
     * @param status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (thread != null) {
            thread.interrupt();
        }
    }

    /**
     * Runnable entry point to a background download
     */
    public void run() {
        try {
            Throwable caught=null;
            try {
                download();
                postDownloadActions(true);
            } catch (SmartFrogException e) {
                caught=e;
            } catch (RemoteException e) {
                caught=e;
            }
            if(caught!=null) {
                new ComponentHelper(this).sfSelfDetachAndOrTerminate(
                        null,
                        caught.getMessage(),
                        null,
                        caught);
            }
        } finally {
            thread=null;
        }
    }

    /**
     * Actions to do after a download
     * @param downloaded flag set if we did a download
     * @throws RemoteException for network problems
     * @throws SmartFrogException for other problems
     */
    private void postDownloadActions(boolean downloaded)
            throws RemoteException, SmartFrogException {
        checkExistence();
        if (exists) {
            //we check the checksums if needed.
            if (md5 != null) {
                checkMd5Checksum();
            }

            if (sha1 != null) {
                checkSha1Checksum();
            }
        } else {
            if (failIfNotPresent) {
                StringBuffer message = new StringBuffer();
                message.append(ERROR_ARTIFACT_NOT_FOUND);
                message.append(getFile().toString());
                if (downloaded) {
                    message.append(" -or downloadable from ");
                    message.append(makeRepositoryUrlList());
                }
                throw new SmartFrogException(message.toString(), this);
            }
        }

        String message = "LibraryArtifactImpl completed. File : "+ getFile();
        if (sfLog().isDebugEnabled()) sfLog().debug(message);

        if(copyTo!=null) {
            try {
                FileSystem.fCopy(getFile(), copyTo);
            } catch (IOException ex) {
                throw SmartFrogException.forward("Failed when copying "
                    + getFile().getAbsolutePath() +
                    " to " +
                    copyTo.getAbsolutePath()
                    , ex);
            }
        }
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,message,
                null,null);
    }

    /**
     * Probe for our library existing already
     *
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    private void checkExistence() throws SmartFrogRuntimeException,
            RemoteException {
        //set our exists flag
        exists = getFile().exists();
        //and the matching resource
        sfReplaceAttribute(FileIntf.ATTR_EXISTS, Boolean.valueOf(exists));
    }


    /**
     * Download the file, even if it is present. Tries every repository in
     * turn.
     *
     * @throws SmartFrogException if there are no repositories.
     */
    public void download() throws SmartFrogException {
        if (repositories.isEmpty()) {
            throw new SmartFrogException(ERROR_NO_REPOSITORIES);
        }
        Iterator it = repositories.iterator();
        while (it.hasNext()) {
            String repository = (String) it.next();
            IOException ioe = downloadFromOneRepository(repository);
            if (ioe == null) {
                //success
                return;
            } else {
                //failure

            }
        }
    }

    /**
     * make string list of the repositories
     *
     * @return a (possibly empty) list of URLs
     */
    public String makeRepositoryUrlList() {
        StringBuffer repos = new StringBuffer();
        repos.append('[');
        Iterator<String> it = repositories.iterator();
        while (it.hasNext()) {
            String repository = it.next();
            repos.append(repository);
            repos.append('/');
            repos.append(remoteUrlPath);
            if (it.hasNext()) {
                repos.append(' ');
            }
        }
        repos.append(']');
        return repos.toString();
    }

    /**
     * try to fetch the file from a single repository. All IOExceptions caught
     * during fetching are logged at debug level, and turned into a failure of
     * this method.
     *
     * @param repositoryBaseURL base URL of the repository
     *
     * @return the exception that got us here
     * @throws SmartFrogException on any download failure
     */
    public IOException downloadFromOneRepository(String repositoryBaseURL)
            throws SmartFrogException {
        String url;
        url = repositoryBaseURL;
        if (!url.endsWith("/")) {
            url += "/";
        }
        url += remoteUrlPath;
        log.info("Trying to download from " + url);

        try {
            DownloadImpl.download(url, getFile(), blocksize, maxCacheAge);
            return null;
        } catch (MalformedURLException e) {
            throw SmartFrogException.forward(url, e);
        } catch (NoRouteToHostException e) {
            throw SmartFrogException.forward(url, e);
        } catch (ConnectException e) {
            throw SmartFrogException.forward(url, e);
        } catch (IOException e) {
            log.debug("Failed fetch from " + url, e);
            throw SmartFrogException.forward(url, e);
            //return e;
        }
    }

    /**
     * check that md5 checksum
     *
     * @throws SmartFrogException on a checksum failure
     */
    public void checkMd5Checksum() throws SmartFrogException {
        checkChecksum(getFile(), "MD5", md5, BLOCKSIZE);
    }

    /**
     * check our sha1 checksum
     *
     * @throws SmartFrogException on a checksum failure
     */
    public void checkSha1Checksum() throws SmartFrogException {
        checkChecksum(getFile(), "SHA", sha1, BLOCKSIZE);
    }

    /**
     * check the checksum of a file. This is not hidden at download time, as we
     * do it *every* load, even if the file is in cache. That may be overkill,
     * but there you go.
     *
     * @param file file to check
     * @param algorithm java crypto api algorithm
     * @param hexValue expected hex value
     * @param bufferSize buffer size to read the file
     *
     * @throws SmartFrogException on a checksum failure
     */
    public void checkChecksum(File file,
                              String algorithm,
                              String hexValue,
                              int bufferSize)
            throws SmartFrogException {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new SmartFrogException("No algorithm " + algorithm, e, this);
        }
        FileInputStream instream = null;
        DigestInputStream digestStream=null;
        byte[] buffer= new byte[0];
        try {
            instream = new FileInputStream(file);
            buffer = new byte[bufferSize];

            digestStream = new DigestInputStream(instream,
                    messageDigest);
            while (digestStream.read(buffer, 0, bufferSize) != -1) {
                // all the work is in the digest stream; here we just pump
                // the channel.
            }
            digestStream.close();
            digestStream=null;
            instream.close();
            instream = null;
        } catch (IOException ioe) {
            FileSystem.close(digestStream);
            FileSystem.close(instream);
            throw new SmartFrogException(ioe, this);
        }
        //now we have a digest array to extract.
        byte[] fileDigest = messageDigest.digest();

        //next: compare with the expected string
        String actual = LibraryHelper.digestToString(fileDigest);
        //clean up leading, tailing chars in the request
        String expected = hexValue.trim().toLowerCase(Locale.ENGLISH);

        if (expected.length() > 0) {
            //case insensitive comparison.
            //we could go the other way, converting from string into hex. But
            //this approach leaves us set up for error reporting.
            String actuallc=actual.toLowerCase(Locale.ENGLISH);
            if (!actuallc.equals(expected)) {
                throw new SmartFrogException(ERROR_CHECKSUM_FAILURE
                        + file.getAbsolutePath()
                        + " with algorithm " + algorithm
                        + "- expected [" + expected + ']'
                    + " - got " + actual,
                        this);
            }
        } else {
            //if "" is the checksum value, it is printed but not checked.
            log.info("Digest of File " + file.getAbsolutePath()
                    + " with algorithm " + algorithm
                    + " is: " + actual);
        }
    }

    /**
     * Determine our relative path. This forwards up to the owner, which must,
     * of course, not be null
     *
     * @return path or null if there is no supported remote path
     *
     * @throws SmartFrogException on resolution trouble
     * @throws RemoteException on network problems
     */
    public String makeRemoteUrlPath() throws RemoteException,
            SmartFrogException {
        return owner.determineArtifactRelativeURLPath(createSerializedArtifact());
    }



    /**
     * create the file that represents the full path to the local file.
     *
     * @return a file that goes to the local location in the cache
     * @throws SmartFrogResolutionException on resolution trouble
     * @throws RemoteException on network problems
     */
    private File makeLocalFile() throws RemoteException, SmartFrogException {
        String absolutepath = owner.determineArtifactPath(createSerializedArtifact());
        File file = new File(absolutepath);
        return file;
    }


    /**
     * @return Returns the artifact.
     */
    public String getArtifact() {
        return artifact;
    }

    /**
     * @param artifact The artifact to set.
     */
    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    /**
     * @return Returns the classifier.
     */
    public String getClassifier() {
        return classifier;
    }

    /**
     * @param classifier The classifier to set.
     */
    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    /**
     * @return Returns the downloadAlways.
     */
    public boolean isDownloadAlways() {
        return downloadAlways;
    }

    /**
     * @param downloadAlways The downloadAlways to set.
     */
    public void setDownloadAlways(boolean downloadAlways) {
        this.downloadAlways = downloadAlways;
    }

    /**
     * @return Returns the downloadIfAbsent.
     */
    public boolean isDownloadIfAbsent() {
        return downloadIfAbsent;
    }

    /**
     * @param downloadIfAbsent The downloadIfAbsent to set.
     */
    public void setDownloadIfAbsent(boolean downloadIfAbsent) {
        this.downloadIfAbsent = downloadIfAbsent;
    }

    /**
     * @return Returns the extension.
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @param extension The extension to set.
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * @return Returns the failIfNotPresent flag.
     */
    public boolean isFailIfNotPresent() {
        return failIfNotPresent;
    }

    /**
     * @param failIfNotPresent The failIfNotPresent to set.
     */
    public void setFailIfNotPresent(boolean failIfNotPresent) {
        this.failIfNotPresent = failIfNotPresent;
    }

    /**
     * @return Returns the md5.
     */
    public String getMd5() {
        return md5;
    }

    /**
     * @param md5 The md5 to set.
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * @return Returns the project.
     */
    public String getProject() {
        return project;
    }

    /**
     * @param project The project to set.
     */
    public void setProject(String project) {
        this.project = project;
    }

    /**
     * @return Returns the sha1.
     */
    public String getSha1() {
        return sha1;
    }

    /**
     * @param sha1 The sha1 to set.
     */
    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    /**
     * @return Returns the version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version The version to set.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Create a serialized artifact to work with
     *
     * @return a serialized representation of the artifact's state.
     */
    public SerializedArtifact createSerializedArtifact() {
        SerializedArtifact pojo = new SerializedArtifact();
        pojo.project = project;
        pojo.artifact = artifact;
        pojo.version = version;
        pojo.extension = extension;
        pojo.classifier = classifier;
        pojo.md5 = md5;
        pojo.sha1 = sha1;
        return pojo;
    }

    /** 
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder b=new StringBuilder();
        b.append(project);
        b.append("/");
        b.append(LibraryHelper.createIvyArtifactFilename(createSerializedArtifact(),true));
        return b.toString();
    }

    

}
