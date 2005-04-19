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
package org.smartfrog.services.xml.impl;

import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Serializer;
import nu.xom.XMLException;
import org.smartfrog.services.filesystem.FileImpl;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.filesystem.FileUsingComponentImpl;
import org.smartfrog.services.xml.interfaces.LocalNode;
import org.smartfrog.services.xml.interfaces.XmlDocument;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 * An XML Document. TODO
 */
public class XmlDocumentImpl extends CompoundXmlNode implements XmlDocument {
    public static final String ERROR_UNSUPPORTED_FEATURE = "Unsupported Feature";
    public static final String ERROR_NO_SAVE = "Failed to save to ";

    /**
     * default encoding {@value}.
     */
    public static final String UTF8 = "UTF-8";
    public static final String ERROR_WRONG_TYPE = "is not an XMLElement";

    public XmlDocumentImpl() throws RemoteException {
    }

    /**
     * create a node of the appropriate type. This is called during deployment;
     *
     * @return a new node
     * @throws nu.xom.XMLException if needed
     */
    public Node createNode() throws RemoteException, SmartFrogException {
        Prim root = resolveRoot();
        XmlElementImpl element;
        try {
            element = (XmlElementImpl) root;
        } catch (ClassCastException e) {
            throw new SmartFrogRuntimeException(ATTR_ROOT
                    + ERROR_WRONG_TYPE, e, this);
        }
        Document document;
        try {
            document = new Document(element.getElement());
            return document;
        } catch (XMLException e) {
            throw XmlNodeHelper.handleXmlException(e);
        }

    }

    private Prim resolveRoot() throws SmartFrogResolutionException,
            RemoteException {
        Prim root = sfResolve(ATTR_ROOT, (Prim) null, true);
        return root;
    }

    /**
     * get the node typecast to a document
     *
     * @return
     */
    public Document getDocument() {
        return (Document) getNode();
    }

    /**
     * root is added when we create the document; this call does the others
     * Called during the {@link CompoundXmlNode#sfDeploy()} operation of
     * our superclass
     * @throws SmartFrogException
     * @throws RemoteException
     */
    protected void addChildren() throws SmartFrogException, RemoteException {

        //load the file.
        bindToSourceFile();


        Prim root = resolveRoot();
        //we still iterate through comments and things, but skip the root
        for (Enumeration e = sfChildren(); e.hasMoreElements();) {
            Object elem = e.nextElement();
            if (!(elem instanceof Prim)) {
                //ignore this, whatever it is
                continue;
            }
            Prim p = (Prim) elem;

            if (p instanceof LocalNode && p != root) {
                LocalNode node = (LocalNode) elem;
                appendChild(node);
            }
        }
    }

    /**
     * After calling the superclass (and so deploying all our children), we
     * generate the XML, Then save the document, if desired
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        String encoding = sfResolve(ATTR_ENCODING, (String) null, true);

        String destFilename = FileSystem.lookupAbsolutePath(this,
                ATTR_DESTFILE,
                (String) null,
                null,
                false,
                null);
        if (destFilename != null) {
            try {
                saveToFile(destFilename, encoding);
            } catch (IOException e) {
                throw new SmartFrogDeploymentException(
                        ERROR_NO_SAVE + destFilename, e);
            }
        }

    }

    /**
     * optional code to bind to a source file.
     * @todo Implement file loading, building up a graph of prims as we go
     * @throws RemoteException
     * @throws SmartFrogRuntimeException
     */
    private void bindToSourceFile() throws RemoteException,
            SmartFrogRuntimeException {
        String sourceFilename = FileSystem.lookupAbsolutePath(this,
                new Reference(ATTR_SOURCEFILE),
                (String) null,
                null,
                false,
                null);
        if (sourceFilename != null) {
            File source = new File(sourceFilename);
            FileUsingComponentImpl.bind(this, source);
            throw new SmartFrogDeploymentException(ERROR_UNSUPPORTED_FEATURE,
                    this);
        }
    }

    /**
     * internal save routine
     *
     * @param filename
     * @param encoding
     * @throws IOException
     */
    protected void saveToFile(String filename, String encoding)
            throws IOException {
        File file = new File(filename);
        FileOutputStream fileout;
        fileout = new FileOutputStream(file);
        OutputStream out;
        out = new BufferedOutputStream(fileout);
        try {
            Serializer serializer = new Serializer(out, encoding);
            serializer.write(getDocument());
        } finally {
            FileSystem.close(out);
            FileSystem.close(fileout);
        }
    }

    /**
     * save a document to a file
     *
     * @param filename
     * @throws RemoteException
     */
    public void save(String filename) throws RemoteException, IOException {
        save(filename, UTF8);
    }

    /**
     * Get the entire Xom document serialised for local manipulation.
     *
     * @return
     * @throws RemoteException
     */
    public SerializedXomDocument getXomDocument() throws RemoteException {
        return new SerializedXomDocument(getDocument());
    }

    /**
     * set a new Xom document. After this point, the doc graph will diverge from
     * that of (any components) used to describe it
     *
     * @param document
     * @throws RemoteException
     */
    public void setXomDocument(SerializedXomDocument document)
            throws RemoteException {
        helper.setNode(document.getDocument());

    }

    /**
     * save a document to a file
     *
     * @param filename
     * @param encoding
     * @throws RemoteException
     */
    public void save(String filename, String encoding) throws RemoteException,
            IOException {
        saveToFile(filename, encoding);
    }
}
