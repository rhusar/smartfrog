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
import org.smartfrog.services.os.download.DownloadImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
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

/**
 * Implementation of a library artifact.
 * This is where all download logic is implemented.
 * created 04-Apr-2005 13:38:47
 */

public class LibraryArtifactImpl extends FileUsingComponentImpl implements LibraryArtifact {
    private Library owner;
    private File cacheDir;
    private Vector repositories;
    private boolean syncDownload;
    private String sha1;
    private String md5;
    private String extension;
    private String artifact;
    private String project;
    private String version;
    private boolean existsInCache;
    private String artifactName;
    private boolean exists;
    private String remoteUrlPath;
    private int blocksize;
    private boolean downloadIfAbsent;
    private boolean downloadAlways;
    private boolean failIfNotPresent;

    private Log log;

    /**
     * what artifacts are separated by {@value}
     */
    public static final String ARTIFACT_SEPARATOR = "-";
    public static final String ERROR_CHECKSUM_FAILURE = "Checksum mismatch on file ";

    /**
     * block size for downloads and digests {@value}
     */
    public static int BLOCKSIZE = 8192;

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
     *
     * <ol>
     * <li> locate parent Library implementation
     * <li> get information about repository cache
     * <li> work out names of remote URL, local filename
     * <li> bind our localfilename
     * <li> fetch the jar (or fail)
     * <li> validate the JAR (or fail)
     * <li>
     * </ol>
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        log=sfGetApplicationLog();
        owner = findOwner();
        cacheDir = FileSystem.resolveAbsolutePath(owner);
        repositories = ((Prim) owner).sfResolve(Library.ATTR_REPOSITORIES,
                (Vector) null, true);
        syncDownload = sfResolve(ATTR_SYNCHRONOUS, syncDownload, true);
        project = sfResolve(ATTR_PROJECT, project, true);
        version = sfResolve(ATTR_VERSION, version, false);
        artifact = sfResolve(ATTR_ARTIFACT, artifact, true);
        extension = sfResolve(ATTR_EXTENSION, extension, true);
        sha1 = sfResolve(ATTR_SHA1, sha1, false);
        md5 = sfResolve(ATTR_MD5, md5, false);
        blocksize = sfResolve(ATTR_BLOCKSIZE,BLOCKSIZE,false);
        boolean terminate = sfResolve(ATTR_TERMINATE,false,false);
        downloadIfAbsent = sfResolve(ATTR_DOWNLOAD_IF_ABSENT, downloadIfAbsent, true);
        downloadAlways = sfResolve(ATTR_DOWNLOAD_ALWAYS,
                        downloadAlways,
                        true);
        failIfNotPresent = sfResolve(ATTR_FAIL_IF_NOT_PRESENT,
                        failIfNotPresent,
                        true);


        //all info is fetched. So work out our filename and URL.
        //we do this through methods for override points
        artifactName=makeArtifactName();
        remoteUrlPath = makeRemoteUrlPath();
        File localFile=makeLocalFile(remoteUrlPath);
        //get the superclass to do our binding
        bind(localFile);
        checkExistence();

        //if we dont exist, we fetch
        boolean mustDownload;
        mustDownload = (!exists && downloadIfAbsent) || downloadAlways;
        if(mustDownload) {
            download();
        }

        checkExistence();
        if(exists) {
            //we check the checksums if needed.
            if(md5!=null) {
                checkMd5Checksum();
            }

            if(sha1!=null) {
                checkSha1Checksum();
            }
        } else {
            if(failIfNotPresent) {
                StringBuffer message=new StringBuffer();
                message.append(ERROR_ARTIFACT_NOT_FOUND);
                message.append(getFile().toString());
                if(mustDownload) {
                    message.append(" -or downloadable from ");
                    message.append(makeRepositoryUrlList());
                }
                throw new SmartFrogException(message.toString(),this);
            }
        }

        //do we need to terminate ourselves?
        if(terminate) {
            new ComponentHelper(this).targetForTermination();
        }
    }

    private void checkExistence() throws SmartFrogRuntimeException,
            RemoteException {
        //set our exists flag
        exists = getFile().exists();
        //and the matching resource
        sfReplaceAttribute(FileIntf.ATTR_EXISTS,Boolean.valueOf(exists));
    }


    public void download() throws SmartFrogException {
        if(repositories.size()==0) {
            throw new SmartFrogException(ERROR_NO_REPOSITORIES);
        }
        Iterator it=repositories.iterator();
        while (it.hasNext()) {
            String repository = (String) it.next();
            if(downloadFromOneRepository(repository)) {
                //success
                return;
            }
        }
    }

    /**
     * make string list of the repositories
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
            repos.append(' ');
        }
        //we only get here on failure. Alert that the file could not be found
        // in any repository
        repos.append(']');
        return repos.toString();
    }

    /**
     * try to fetch the file from a single repository.
     * All IOExceptions caught during fetching are logged at debug
     * level, and turned into a failure of this method.
     * @param repositoryBaseURL
     * @return true if the fetch was successful, false if not
     */
    public boolean downloadFromOneRepository(String repositoryBaseURL) {
        String url;
        url=repositoryBaseURL;
        if(!url.endsWith("/")) {
            url+="/";
        }
        url+=remoteUrlPath;
        log.info("Trying to download from "+url);

        try {
            DownloadImpl.download(url, getFile(), blocksize);
            return true;
        } catch (IOException e) {
            log.debug("Failed fetch from "+url,e);
            return false;
        }
    }

    /**
     * check that md5 checksum
     * @throws SmartFrogException
     */
    public void checkMd5Checksum() throws SmartFrogException {
        checkChecksum(getFile(), "MD5",md5, BLOCKSIZE);

    }

    /**
     * check our sha1 checksum
     * @throws SmartFrogException
     */
    public void checkSha1Checksum() throws SmartFrogException {
        checkChecksum(getFile(), "SHA", sha1, BLOCKSIZE);
    }

    /**
     * check the checksum of a file.
     * This is not hidden at download time, as we do it *every* load,
     * even if the file is in cache. That may be overkill, but there you go.
     * @param file
     * @param algorithm
     * @param hexValue
     * @param blocksize
     * @throws SmartFrogException
     */
    public void checkChecksum(File file,
            String algorithm,
            String hexValue,
            int blocksize)
            throws SmartFrogException {
        MessageDigest messageDigest;
        try {
            messageDigest= MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new SmartFrogException("No algorithm "+algorithm,e,this);
        }
        FileInputStream instream=null;
        byte buffer[]=new byte[0];
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
            try {
                if(instream!=null) {
                    instream.close();
                }
            } catch (IOException ignored) {

            }
            throw new SmartFrogException(ioe,this);
        }
        //now we have a digest array to extract.
        byte[] fileDigest = messageDigest.digest();

        //next: compare with the expected string
        String actual=digestToString(fileDigest);
        //clean up leading, tailing chars in the request
        String expected=hexValue.trim();

        if(expected.length()>0) {
            //case insensitive comparison.
            //we could go the other way, converting from string into hex. But
            //this approach leaves us set up for error reporting.
            if(!actual.equalsIgnoreCase(hexValue)) {
                throw new SmartFrogException(ERROR_CHECKSUM_FAILURE
                        +file.getAbsolutePath()
                        +" with algorithm "+algorithm
                        +"- expected ["+expected+"]"
                        +" - got "+actual,
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
     * get a string value of a digest as a hex list, two characters per byte.
     * There would seem to be a more efficient implementation of this involving
     * a 256 byte memory buffer.
     * @param digest
     * @return
     */
    public static String digestToString(byte[] digest) {
        int length = digest.length;
        StringBuffer buffer = new StringBuffer(length*2);
        for (int i = 0; i < length; i++) {
            String ff = Integer.toHexString(digest[i] & 0xff);
            if (ff.length() < 2) {
                buffer.append('0');
            }
            buffer.append(ff);
        }
        return buffer.toString();
    }


    /**
     * combine project and artifact to produce a path.
     * This must not include host info or other repository binding
     * information, as this is done for every repository later.
     * @return path such as /project/artifact-version.jar
     */
    public String makeRemoteUrlPath() {
        String patched=patchProject(project);
        String urlPath="/"+patched+"/jars/"+artifactName;
        return urlPath;
    }

    /**
     * get the full name of the artifact. If a version tag is included, it
     * is artifact-version+extension. If not, it is artifact+extension.
     * @return
     */
    public String makeArtifactName() {
        StringBuffer buffer=new StringBuffer();
        buffer.append(artifact);
        if(version!=null) {
            buffer.append(ARTIFACT_SEPARATOR);
            buffer.append(version);
        }
        buffer.append(extension);
        return buffer.toString();
    }

    /**
     * create the file that represents the full path
     * to the local file.
     * @param remoteUrlPath the remote path (as a hint)
     * @return a file that goes to the local location in the cache
     */
    public File makeLocalFile(String remoteUrlPath) {
        PlatformHelper helper=PlatformHelper.getLocalPlatform();
        String localpath=helper.convertFilename(remoteUrlPath);
        File file=new File(cacheDir,localpath);
        return file;
    }


    /**
     * Convert a dotted project name into a forward slashed project name.
     * This is done in preparation for Maven2 repositories, which will have more
     * depth to their classes.
     * NB: only public for testing. This is not a public API.
     * @param projectName
     * @return a string whch may or may not match the old string.
     */
    public static String patchProject(String projectName) {
        //break out early if no match; create no new object
        if(projectName.indexOf('.')<0) {
            return projectName;
        }
        //create a new buffer, patch it
        int len = projectName.length();
        StringBuffer patched=new StringBuffer(len);
        for(int i=0;i<len;i++) {
            char c=projectName.charAt(i);
            if(c=='.') {
                c='/';
            }
            patched.append(c);
        }
        return patched.toString();
    }


    /**
     * Find our owning Library
     * <ol>
     * <li> direct attribute
     * <li> Parent
     * </li>
     * @return a libraries instance or an error
     * @throws SmartFrogResolutionException on resolution trouble
     * @throws RemoteException
     */
    protected Library findOwner() throws SmartFrogResolutionException,
            RemoteException {
        return (Library)sfResolve(ATTR_LIBRARY, owner, true);
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
     * @param instance
     * @return the parent that implements the interface, or null
     * @throws RemoteException
     */
    private Library findLibrariesParent(Prim instance)
            throws RemoteException {
        if(instance==null) {
            return null;
        }
        if(instance instanceof Library) {
            return (Library) instance;
        }
        return findLibrariesParent(instance.sfParent());
    }

}
