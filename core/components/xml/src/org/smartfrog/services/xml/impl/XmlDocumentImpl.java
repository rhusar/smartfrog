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
import org.smartfrog.services.filesystem.FileImpl;
import org.smartfrog.services.xml.interfaces.LocalNode;
import org.smartfrog.services.xml.interfaces.XmlDocument;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;

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
        try {
            XmlElementImpl element = (XmlElementImpl) root;
            Document document = new Document(element.getElement());
            return document;

        } catch (ClassCastException e) {
            throw new SmartFrogRuntimeException(ATTR_ROOT
                    + "is not an XMLElement", e, this);

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
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    protected void addChildren() throws SmartFrogException, RemoteException {

        Prim root = resolveRoot();
        //we still iterate through comments and things, but skip the root
        for (Enumeration e = sfChildren(); e.hasMoreElements();) {
            Object elem = e.nextElement();
            if (!(elem instanceof Prim)) {
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
        String sourceFilename = FileImpl.lookupAbsolutePath(this,
                ATTR_SOURCEFILE,
                (String) null,
                null,
                false,
                null);
        if (sourceFilename != null) {
            throw new SmartFrogDeploymentException(ERROR_UNSUPPORTED_FEATURE,
                    this);
        }
        String destFilename = FileImpl.lookupAbsolutePath(this,
                ATTR_DESTFILE,
                (String) null,
                null,
                false,
                null);
        if (destFilename != null) {
            //save to a file
            getDocument().
        }

    }

    protected void saveToFile(String filename, String encoding)
            throws IOException {
        File file = new File(filename);
        FileOutputStream fileout;
        fileout = new FileOutputStream(file);
        OutputStream out;
        out = new BufferedOutputStream(fileout);
        Serializer serializer = new Serializer(out, encoding);
        serializer.write(getDocument());
    }
}
