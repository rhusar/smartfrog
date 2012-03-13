/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartfrog.services.archives;

import org.smartfrog.services.filesystem.FileUsingComponentImpl;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipEntry;

import java.rmi.RemoteException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Date;

/**
 * Unzip component.
 * <p/> 
 * Imports code from {@link org.apache.tools.ant.taskdefs.Expand} in Ant's codebase
 * 
 */

public class UnzipImpl extends FileUsingComponentImpl implements Unzip {

    private static final String ENCODING = "UTF8";
    private boolean failOnEmptyArchive;
    private boolean overwrite;
    private boolean stripAbsolutePathSpec = true;
    private int bufferSize = 1024;

    public UnzipImpl() throws RemoteException {
    }

    /**
     * this is called at runtime
     *
     * @throws SmartFrogException error while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        failOnEmptyArchive = sfResolve(ATTR_FAIL_ON_EMPTY_ARCHIVE, true, true);
        overwrite = sfResolve(ATTR_OVERWRITE, true, true);
        stripAbsolutePathSpec = sfResolve(ATTR_STRIP_ABSOLUTE_PATH_SPEC, true, true);
        bufferSize = sfResolve(ATTR_BUFFERSIZE, 0, true);
        bind(true, null);
        if (!getFile().exists()) {
            throw new SmartFrogLifecycleException("Missing zip file " + getFile());
        }
        
        String destPath = FileSystem.lookupAbsolutePath(this,
                ATTR_DESTDIR,
                null,
                null,
                true,
                null);
        if (destPath == null || destPath.length() == 0) {
            throw new SmartFrogException("The Destination Directory is invalid");
        }
        File destDir = new File(destPath);
        expandFile(getFile(),destDir);
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,
                "Unzipped File " + getFile().getAbsolutePath(),
                sfCompleteNameSafe(),
                null);
    }


    /**
     * This method is to be overridden by extending unarchival tasks.
     *
     * @param srcF      the source file
     * @param destD       the destination directory
     * @throws SmartFrogException if anything failed
     */
    protected void expandFile(File srcF, File destD) throws SmartFrogException {
        FileUtils fileUtils = FileUtils.getFileUtils();
        if (!srcF.exists()) {
            throw new SmartFrogException("Unable to unzip "
                    + srcF
                    + " as the file does not exist");
        }
        sfLog().info("Unzipping: " + srcF + " into " + destD);
        ZipFile zf = null;
        try {
            zf = new ZipFile(srcF, ENCODING);
            boolean empty = true;
            Enumeration e = zf.getEntries();
            while (e.hasMoreElements()) {
                empty = false;
                ZipEntry ze = (ZipEntry) e.nextElement();
                extractFile(fileUtils, srcF, destD, zf.getInputStream(ze),
                        ze.getName(), new Date(ze.getTime()),
                        ze.isDirectory());
            }
            if (empty && failOnEmptyArchive) {
                throw new SmartFrogException("archive '" + srcF + "' is empty");
            }
        } catch (IOException ioe) {
            throw new SmartFrogException(
                    "Error while unzipping " + srcF.getPath() + " to "+ destD.getPath()
                            + "\n" + ioe.toString(),
                    ioe);
        } finally {
            ZipFile.closeQuietly(zf);
        }
    }


    /**
     * extract a file to a directory
     *
     * @param fileUtils             a fileUtils object
     * @param srcF                  the source file
     * @param dir                   the destination directory
     * @param compressedInputStream the input stream
     * @param entryName             the name of the entry
     * @param entryDate             the date of the entry
     * @param isDirectory           if this is true the entry is a directory
     * @throws IOException on error
     * @throws SmartFrogException on other problems
     */
    protected void extractFile(FileUtils fileUtils, File srcF, File dir,
                               InputStream compressedInputStream,
                               String entryName, Date entryDate,
                               boolean isDirectory)
            throws IOException, SmartFrogException {

        String target = entryName;
        if (stripAbsolutePathSpec && entryName.length() > 0
                && (entryName.charAt(0) == File.separatorChar
                || entryName.charAt(0) == '/'
                || entryName.charAt(0) == '\\')) {
            sfLog().info("stripped absolute path spec from " + entryName);
            target = entryName.substring(1);
        }

        File f = fileUtils.resolveFile(dir, target);
        try {
            if (!overwrite && f.exists()) {
                sfLog().debug("Skipping " + f + " as it exists");
                return;
            }

            sfLog().debug("expanding " + entryName + " to " + f);
            // create intermediary directories - sometimes zip don't add them
            File dirF = f.getParentFile();
            if (dirF != null) {
                dirF.mkdirs();
            }

            if (isDirectory) {
                f.mkdirs();
            } else {
                byte[] buffer = new byte[bufferSize];
                int length = 0;
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(f);

                    while ((length =
                            compressedInputStream.read(buffer)) >= 0) {
                        fos.write(buffer, 0, length);
                    }

                    fos.close();
                    fos = null;
                } finally {
                    FileUtils.close(fos);
                }
            }

            fileUtils.setFileLastModified(f, entryDate.getTime());
        } catch (FileNotFoundException ex) {
            throw new SmartFrogException("Unable to expand to file " + f.getPath() 
                    +  ":" + ex,
                    ex);
        }

    }
}
