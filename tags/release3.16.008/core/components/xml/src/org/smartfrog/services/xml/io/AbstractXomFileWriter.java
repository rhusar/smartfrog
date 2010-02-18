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
package org.smartfrog.services.xml.io;

import nu.xom.Serializer;
import nu.xom.Document;
import org.smartfrog.services.filesystem.FileUsingComponentImpl;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;

/**
 * <p/>
 * Created: 09-Mar-2008
 */
public class AbstractXomFileWriter extends FileUsingComponentImpl
        implements XomSerializerFactory {
    private String encoding;
    private int indent = 0;
    private String lineSeparator;
    private int maxLength;

    public AbstractXomFileWriter() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        bindWithDir(true, null);
        encoding = sfResolve(ATTR_ENCODING, "", true);
        indent = sfResolve(ATTR_INDENT, 0, true);
        maxLength = sfResolve(ATTR_MAX_LENGTH, 0, true);
        lineSeparator = sfResolve(ATTR_LINE_SEPARATOR, "", true);
    }

    /**
     * Create a serializier
     *
     * @param out output stream
     * @return a configured serializer
     * @throws SmartFrogRuntimeException if the encoding is unsupported
     */
    protected Serializer createSerializer(OutputStream out) throws SmartFrogRuntimeException {
        try {
            Serializer s = new Serializer(out, encoding);
            s.setIndent(indent);
            s.setLineSeparator(lineSeparator);
            s.setMaxLength(maxLength);
            return s;
        } catch (UnsupportedEncodingException e) {
            throw new SmartFrogRuntimeException(ERROR_UNSUPPORTED_ENCODING + encoding,
                    this);
        }
    }

    /**
     * Write a document to a named file
     *
     * @param document Xom tree to write
     * @throws SmartFrogRuntimeException any failure to open or write to the file
     */
    protected void writeDocumentToFile(Document document) throws SmartFrogRuntimeException {
        writeDocumentToFile(getFile(), document);
    }


    /**
     * Write a document to a named file
     *
     * @param destFile file to write to
     * @param document Xom tree to write
     * @throws SmartFrogRuntimeException any failure to open or write to the file
     */
    protected void writeDocumentToFile(File destFile, Document document)
            throws SmartFrogRuntimeException {
        OutputStream out;
        try {
            out = new FileOutputStream(destFile);
        } catch (FileNotFoundException e) {
            throw new SmartFrogRuntimeException("File not found:"
                    + destFile,
                    e,
                    this);
        }
        try {
            Serializer serializer;
            serializer = createSerializer(out);
            out = null;
            serializer.write(document);
            serializer.flush();
        } catch (IOException e) {
            throw new SmartFrogRuntimeException("Exception when writing to "
                    + destFile + " : " + e.getMessage(),
                    e,
                    this);
        } finally {
            FileSystem.close(out);
        }
    }

}
