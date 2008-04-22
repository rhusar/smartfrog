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

    /**
     * @throws RemoteException In case of network/rmi error
     */
    public XmlDocumentImpl() throws RemoteException {
    }

    /**
     * create a node of the appropriate type. This is called during deployment;
     *
     * @return a new node
     * @throws nu.xom.XMLException if needed
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException For smartfrog problems, and for caught
     *                            XMLExceptions
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

    /**
     * Resolve the root attribute, which must be present
     * @return the root atribute
     * @throws SmartFrogResolutionException if there was none
     * @throws RemoteException In case of network/rmi error
     *  */
    private Prim resolveRoot() throws SmartFrogResolutionException,
            RemoteException {
        Prim root = sfResolve(ATTR_ROOT, (Prim) null, true);
        return root;
    }


    /**
     * generate XML from the doc. This always triggers a recalculate of
     * everything, then the attribute is saved. We do it this way because we
     * don't know what has changed underneath.
     *
     * @return XML of the tree
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException For smartfrog problems, and for caught
     *                            XMLExceptions
     */
    public String toXML() throws RemoteException, SmartFrogException {
        //special handling of the situation where there is no root.
        // We have to be loading a document from a file in this situation
        Prim root = sfResolve(ATTR_ROOT, (Prim) null, false);
        if(root!=null) {
            return super.toXML();
        }
        if(getSourcefile()==null) {
            throw new SmartFrogException("The XML document has no "+ATTR_ROOT
                    +" attribute and no "+ATTR_SOURCEFILE+" attribute, so cannot be constructed");
        }
        //this is only transient until we build it
        return "";
    }

    /**
     * get the node typecast to a document
     *
     * @return the document
     */
    public Document getDocument() {
        return (Document) getNode();
    }

    /**
     * root is added when we create the document; this call does the others
     * Called during the {@link CompoundXmlNode#sfDeploy()} operation of
     * our superclass
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException For smartfrog problems, and for caught
     *                            XMLExceptions
     */
    protected void addChildren() throws SmartFrogException, RemoteException {

        //load the file.
        bindToSourceFile();


        Prim root = resolveRoot();
        //we still iterate through comments and things, but skip the root
        for (Prim p : sfChildList()) {
            if (p instanceof LocalNode && p != root) {
                LocalNode node = (LocalNode) p;
                appendChild(node);
            }
        }
    }

    /**
     * After calling the superclass (and so deploying all our children), we
     * generate the XML, Then save the document, if desired
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException For smartfrog problems, and for caught
     *                            XMLExceptions
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        String encoding = sfResolve(ATTR_ENCODING, (String) null, true);

        String destFilename = FileSystem.lookupAbsolutePath(this,
                ATTR_DESTFILE,
                null,
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
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogRuntimeException For smartfrog problems, and for caught
     *                            XMLExceptions
     */
    private void bindToSourceFile() throws RemoteException,
            SmartFrogRuntimeException {
        String sourceFilename = getSourcefile();
        if (sourceFilename != null) {
            File source = new File(sourceFilename);
            FileUsingComponentImpl.bind(this, source);
            throw new SmartFrogDeploymentException(ERROR_UNSUPPORTED_FEATURE+": binding to source files",
                    this);
        }
    }

    /**
     * Get the source file attribute
     * @return the source file
     * @throws SmartFrogResolutionException failure to resolve
     * @throws RemoteException network problems
     */
    private String getSourcefile() throws SmartFrogResolutionException, RemoteException {
        return FileSystem.lookupAbsolutePath(this,
                new Reference(ATTR_SOURCEFILE),
                null,
                null,
                false,
                null);
    }

    /**
     * internal save routine
     *
     * @param filename file to save to
     * @param encoding encoding
     * @throws IOException if saving fails
     */
    protected void saveToFile(String filename, String encoding)
            throws IOException {
        File file = new File(filename);
        FileOutputStream fileout;
        fileout = new FileOutputStream(file);
        OutputStream out=null;
        try {
            out = new BufferedOutputStream(fileout);
            Serializer serializer = new Serializer(out, encoding);
            serializer.write(getDocument());
        } finally {
            FileSystem.close(out);
            FileSystem.close(fileout);
        }
    }

    /**
     * save a document to a file as UTF8
     *
     * @param filename file to save to
     * @throws IOException if saving fails
     */
    public void save(String filename) throws IOException {
        save(filename, UTF8);
    }

    /**
     * Get the entire Xom document serialised for local manipulation.
     *
     * @return the serialized document
     * @throws RemoteException
     */
    public SerializedXomDocument getXomDocument() throws RemoteException {
        return new SerializedXomDocument(getDocument());
    }

    /**
     * set a new Xom document. After this point, the doc graph will diverge from
     * that of (any components) used to describe it
     *
     * @param document the serialized document
     * @throws RemoteException
     */
    public void setXomDocument(SerializedXomDocument document)
            throws RemoteException {
        helper.setNode(document.getDocument());

    }

    /**
     * save a document to a file
     *
     * @param filename file to save to
     * @param encoding encoding
     * @throws IOException if saving fails
     */
    public void save(String filename, String encoding) throws 
            IOException {
        saveToFile(filename, encoding);
    }
}
