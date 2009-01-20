/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.components.io;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.smartfrog.services.filesystem.TupleDataSource;
import org.smartfrog.services.filesystem.TupleReaderThread;
import org.smartfrog.services.hadoop.common.DfsUtils;
import org.smartfrog.services.hadoop.components.dfs.DfsClusterBoundImpl;
import org.smartfrog.services.hadoop.core.SFHadoopException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;

/**
 * Created 22-Sep-2008 15:49:03
 */

public class TuplesToHadoopImpl extends DfsClusterBoundImpl implements TuplesToHadoop {

    private TupleUploadThread worker;
    private DistributedFileSystem fileSystem;
    private Path dest;
    private String lineBegin;
    private String lineEnd;
    private String separator;
    private String quoteBegin,quoteEnd;
    private int bufferSize;
    private int replication;
    private long blockSize;
    private boolean overwrite;
    private String encoding;

    public TuplesToHadoopImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        TupleDataSource source = (TupleDataSource) sfResolve(ATTR_SOURCE, (Prim) null,
                true);
        fileSystem = createFileSystem();
        dest = resolveDfsPath(ATTR_DEST);
        lineBegin = sfResolve(ATTR_LINEBEGIN, "", true);
        lineEnd = sfResolve(ATTR_LINEEND, "", true);
        separator = sfResolve(ATTR_SEPARATOR, "", true);
        quoteBegin = sfResolve(ATTR_QUOTEBEGIN, "", true);
        quoteEnd = sfResolve(ATTR_QUOTEEND, "", true);
        bufferSize = sfResolve(ATTR_BUFFERSIZE, 0, true);
        replication = sfResolve(ATTR_REPLICATION, 0, true);
        blockSize = sfResolve(ATTR_BLOCKSIZE, 0L, true);
        overwrite = sfResolve(ATTR_OVERWRITE, false, true);
        encoding = sfResolve(ATTR_ENCODING, "", true);

        DfsUtils.mkParentDirs(fileSystem, dest);
        worker = new TupleUploadThread(source, true);
        worker.start();
    }


    /**
     * This is the worker thread that gets invoked whenever
     * a tuple is to be uploaded. It opens the file for writing
     * and whenever it gets a tuple, it pushes out every line
     * in the chosen format
     */
    private class TupleUploadThread extends TupleReaderThread {
        private PrintWriter output;

        private TupleUploadThread(TupleDataSource source,
                                  boolean workflowTermination) {
            super(TuplesToHadoopImpl.this, source, workflowTermination);
        }


        /**
         * we've started. do any preparation
         *
         * @throws SmartFrogException SmartFrog problems
         * @throws RemoteException    network problems
         */
        @Override
        protected void onStarted() throws SmartFrogException, RemoteException {
            super.onStarted();
            try {
                FSDataOutputStream dataOut = fileSystem
                        .create(dest,
                                overwrite,
                                bufferSize,
                                (short) replication,
                                blockSize, null);
                output = new PrintWriter(new OutputStreamWriter(dataOut, encoding));
            } catch (IOException e) {
                throw SFHadoopException.forward("failed to open " + dest, e,
                        TuplesToHadoopImpl.this, null);
            }
        }

        /**
         * Process one line of the data source
         *
         * @param line line to process
         * @throws SmartFrogException SmartFrog problems
         * @throws RemoteException    network problems
         */
        @Override
        protected void processOneLine(String[] line)
                throws SmartFrogException, RemoteException {
            //upload the line
            output.print(lineBegin);
            int width = line.length;
            for (int i = 0; i < width; i++) {
                if (i > 0) {
                    output.print(separator);
                }
                output.print(quoteBegin);
                output.print(line[i]);
                output.print(quoteEnd);
            }
            output.println(lineEnd);
        }

        /**
         * we've finished (successfully)
         *
         * @throws SmartFrogException SmartFrog problems
         * @throws RemoteException    network problems
         */
        @Override
        protected void onFinished() throws SmartFrogException, RemoteException {
            super.onFinished();
            try {
                output.close();
                fileSystem.close();
            } catch (IOException e) {
                throw SFHadoopException.forward("failed to close " + dest, e,
                        TuplesToHadoopImpl.this, null);
            }
        }


    }
}
