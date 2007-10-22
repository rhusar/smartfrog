/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

 */
package org.smartfrog.services.xml.interfaces;

import org.smartfrog.services.filesystem.FileUsingComponent;
import org.smartfrog.services.xml.impl.SerializedXomDocument;

import java.io.IOException;
import java.rmi.RemoteException;


/**
 * XML Document
 */
public interface XmlDocument extends XmlNode, FileUsingComponent {


    /**
     * can be a string or a File instance/reference filename extends Optional
     */
    static final String ATTR_DESTFILE = "destFile";

    /**
     * can be a string or a File instance/reference filename extends Optional
     */
    static final String ATTR_SOURCEFILE = ATTR_FILENAME;

    /**
     * document type of type DocType; docType extends Optional
     */
    static final String ATTR_DOCTYPE = "docType";
    /**
     * encoding string when saving encoding extends String;
     */
    static final String ATTR_ENCODING = "encoding";

    /**
     * root node must be a Document Node.
     * Root extends Compulsory
     */
    static final String ATTR_ROOT = "root";

    /**
     * save a document to a file
     * @param filename file name
     * @param encoding encoding type
     * @throws IOException for IO or network problems
     */
    void save(String filename, String encoding) throws IOException ;

    /**
     * save a document to a file, utf-8 encoded
     *
     * @param filename file name
     * @throws IOException for IO or network problems
     */
    void save(String filename) throws
            IOException;

    /**
     * Get the entire Xom document serialised for local manipulation.
     * @return the document
     * @throws RemoteException In case of network/rmi error
     */
    SerializedXomDocument getXomDocument() throws RemoteException;

    /**
     * set a new Xom document.
     * After this point, the doc graph will diverge
     * from that of (any components) used to describe it
     * @param document document to pass in
     * @throws RemoteException In case of network/rmi error
     */
    void setXomDocument(SerializedXomDocument document) throws RemoteException;
}
