/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

import org.smartfrog.services.filesystem.FileUsingComponentImpl;
import org.smartfrog.services.filesystem.FileUsingComponent;
import org.smartfrog.services.filesystem.FileImpl;
import org.smartfrog.services.filesystem.FileIntf;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.filesystem.FileUsingCompoundImpl;
import org.smartfrog.services.os.download.DownloadImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.utils.PlatformHelper;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.logging.Log;

import java.rmi.RemoteException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.DigestInputStream;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.ConnectException;

/**
 * Implementation of a library artifact.
 * This is where all download logic is implemented.
 * created 04-Apr-2005 13:38:47
 */

public class LibraryArtifactImpl extends FileUsingCompoundImpl implements LibraryArtifact {

    private Library owner;
    private Vector repositories;
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
        owner = findOwner();
        repositories = ((Prim) owner).sfResolve(Library.ATTR_REPOSITORIES,
                (Vector) null, true);
        syncDownload = sfResolve(ATTR_SYNCHRONOUS, syncDownload, true);
        project = sfResolve(ATTR_PROJECT, project, true);
        version = sfResolve(ATTR_VERSION, version, false);
        artifact = sfResolve(ATTR_ARTIFACT, artifact, true);
        extension = sfResolve(ATTR_EXTENSION, extension, true);
        sha1 = sfResolve(ATTR_SHA1, sha1, false);
        md5 = sfResolve(ATTR_MD5, md5, false);
        blocksize = sfResolve(ATTR_BLOCKSIZE, BLOCKSIZE, false);
        boolean terminate = sfResolve(ATTR_TERMINATE, false, false);
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
            download();
        }

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
                if (mustDownload) {
                    message.append(" -or downloadable from ");
                    message.append(makeRepositoryUrlList());
                }
                throw new SmartFrogException(message.toString(), this);
            }
        }

        //do we need to terminate ourselves?
        if (terminate) {
            new ComponentHelper(this).targetForTermination();
        }
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
        if (repositories.size() == 0) {
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
        Iterator it = repositories.iterator();
        while (it.hasNext()) {
            String repository = (String) it.next();
            repos.append(repository);
            repos.append("/");
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
     * @param repositoryBaseURL
     *
     * @return the exception that got us here
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
            DownloadImpl.download(url, getFile(), blocksize);
            return null;
        } catch (MalformedURLException e) {
            throw SmartFrogException.forward(url.toString(), e);
        } catch (NoRouteToHostException e) {
            throw SmartFrogException.forward(url.toString(), e);
        } catch (ConnectException e) {
            throw SmartFrogException.forward(url.toString(), e);
        } catch (IOException e) {
            log.debug("Failed fetch from " + url, e);
            throw SmartFrogException.forward(url.toString(), e);
            //return e;
        }
    }

    /**
     * check that md5 checksum
     *
     * @throws SmartFrogException
     */
    public void checkMd5Checksum() throws SmartFrogException {
        checkChecksum(getFile(), "MD5", md5, BLOCKSIZE);
    }

    /**
     * check our sha1 checksum
     *
     * @throws SmartFrogException
     */
    public void checkSha1Checksum() throws SmartFrogException {
        checkChecksum(getFile(), "SHA", sha1, BLOCKSIZE);
    }

    /**
     * check the checksum of a file. This is not hidden at download time, as we
     * do it *every* load, even if the file is in cache. That may be overkill,
     * but there you go.
     *
     * @param file
     * @param algorithm
     * @param hexValue
     * @param blocksize
     *
     * @throws SmartFrogException
     */
    public void checkChecksum(File file,
                              String algorithm,
                              String hexValue,
                              int blocksize)
            throws SmartFrogException {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new SmartFrogException("No algorithm " + algorithm, e, this);
        }
        FileInputStream instream = null;
        byte buffer[] = new byte[0];
        try {
            instream = new FileInputStream(file);
            buffer = new byte[blocksize];
            DigestInputStream digestStream = new DigestInputStream(instream,
                    messageDigest);
            while (digestStream.read(buffer, 0, blocksize) != -1) {
                // all the work is in the digest stream; here we just pump
                // the channel.
            }
            digestStream.close();
            instream.close();
            instream = null;
        } catch (IOException ioe) {
            FileSystem.close(instream);
            throw new SmartFrogException(ioe, this);
        }
        //now we have a digest array to extract.
        byte[] fileDigest = messageDigest.digest();

        //next: compare with the expected string
        String actual = LibraryHelper.digestToString(fileDigest);
        //clean up leading, tailing chars in the request
        String expected = hexValue.trim();

        if (expected.length() > 0) {
            //case insensitive comparison.
            //we could go the other way, converting from string into hex. But
            //this approach leaves us set up for error reporting.
            if (!actual.equalsIgnoreCase(hexValue)) {
                throw new SmartFrogException(ERROR_CHECKSUM_FAILURE
                        + file.getAbsolutePath()
                        + " with algorithm " + algorithm
                        + "- expected [" + expected + "]"
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
     * @return path
     *
     * @throws RemoteException if things go wrong
     */
    public String makeRemoteUrlPath() throws RemoteException,
            SmartFrogException {
        return owner.determineArtifactRelativeURLPath(createSerializedArtifact());
    }

    
    /**
     * get the full name of the artifact. If a version tag is included, it
     * is artifact-version+extension. If not, it is artifact+extension.
     * @return
     */
/*    public String makeArtifactName() {
        return Maven1Policy.createMaven1ArtifactName(artifact,version,extension);
    }
*/    
    /**
     * create the file that represents the full path to the local file.
     *
     * @return a file that goes to the local location in the cache
     */
    private File makeLocalFile() throws RemoteException, SmartFrogException {
        String absolutepath = owner.determineArtifactPath(createSerializedArtifact());
        File file = new File(absolutepath);
        return file;
    }


    /**
     * Find our owning Library <ol> <li> direct attribute <li> Parent </li>
     *
     * @return a libraries instance or an error
     *
     * @throws SmartFrogResolutionException on resolution trouble
     * @throws RemoteException
     */
    protected Library findOwner() throws SmartFrogResolutionException,
            RemoteException {
        final Object libAttr = sfResolve(ATTR_LIBRARY, owner, true);
        assert  (!(libAttr instanceof ComponentDescription)):"Uninstantiated component: " + libAttr;
        return (Library) libAttr;
        /*
        owner = null;
        Object resolved = sfResolve(ATTR_LIBRARY, owner, false);
        if (resolved != null) {
            if (resolved instanceof Library) {
                owner = (Library) resolved;
            } else {
                throw SmartFrogResolutionException.illegalClassType(new Reference(ATTR_LIBRARY),
                        this.sfCompleteNameSafe());
            }
        }
        if(owner==null) {
            owner=findLibrariesParent(this);
        }
        if(owner==null) {
            throw new SmartFrogResolutionException(ERROR_NO_OWNER,this);
        }
        return owner;
        */
    }

    /**
     * recursive search for anything that is a library
     *
     * @param instance
     *
     * @return the parent that implements the interface, or null
     *
     * @throws RemoteException
     */
    private Library findLibrariesParent(Prim instance)
            throws RemoteException {
        if (instance == null) {
            return null;
        }
        if (instance instanceof Library) {
            return (Library) instance;
        }
        return findLibrariesParent(instance.sfParent());
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


}
