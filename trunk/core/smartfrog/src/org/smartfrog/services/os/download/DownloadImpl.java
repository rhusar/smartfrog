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

package org.smartfrog.services.os.download;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.services.filesystem.FileImpl;
import org.smartfrog.services.filesystem.FileSystem;

/**
 * Defines the Downloader class. It downloads the data from a given url.
 */ 
public class DownloadImpl extends PrimImpl implements Download {

    /**
     * Constructor.
     *
     * @throws RemoteException in case of network/rmi error
     */
    public DownloadImpl() throws RemoteException {
    }
    
    /**
     * Starts the download component.
     *
     * @throws SmartFrogException in case of error in starting
     * @throws RemoteException in case of network/emi error
     */ 
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        LogSF log = LogFactory.getLog(this);
        String url = "NOT YET SET";
        String localFile = "NOT YET SET";

        try {
            url = (String) sfResolve("url");
            localFile =
                    FileSystem.lookupAbsolutePath(this,
                            ATTR_LOCALFILE,
                            null,
                            null,
                            true,
                            null);

            int blocksize = ((Integer) sfResolve(ATTR_BLOCKSIZE)).intValue();

            download(url, new File(localFile), blocksize);

            //spawn the thread to terminate normally
            ComponentHelper helper = new ComponentHelper(this);
            helper.targetForTermination();
        } catch (IOException e) {
            String errStr = "error in downloading of url " +
                    url +
                    " to " +
                    localFile;
            if (log.isErrorEnabled()) {
                log.error(errStr);
            }
            //TODO : Need to be revisited
            throw new SmartFrogLifecycleException(e, this);
        }
    }

    /**
     * Simple Download
     * @param url
     * @param localFile
     * @param blocksize
     * @throws IOException
     */
    public static void download(String url, File localFile, int blocksize)
            throws IOException {
        /** FileOutputStream object. */
        FileOutputStream fs = null;
        /** InputStream. */
        InputStream is = null;

        byte[] b = new byte[blocksize];
        int bytesRead;
        boolean finished=false;
        //create our output directories.
        localFile.getParentFile().mkdirs();
        try {
            // open the URL,
            is = SFClassLoader.getResourceAsStream(url);

            // open the file,
            fs = new FileOutputStream(localFile);

            // transfer the data
            do {
                bytesRead = is.read(b, 0, blocksize);

                if (bytesRead > 0) {
                    fs.write(b, 0, bytesRead);
                }
            } while (bytesRead > 0);
            //mark as finished
            finished=true;
            fs.close();
        } finally {
            FileSystem.close(fs);
            FileSystem.close(is);
            //delete any half-downloaded local file if
            //something went wrong during download.
            if(!finished && localFile.exists()) {
                localFile.delete();
            }
        }
    }


}
