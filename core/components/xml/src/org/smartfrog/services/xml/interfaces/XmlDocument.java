package org.smartfrog.services.xml.interfaces;

import java.rmi.RemoteException;
import java.io.IOException;


/**
 * XML Document
 */
public interface XmlDocument extends XmlNode {


    /**
     * can be a string or a File instance/reference filename extends Optional
     */
    static final String ATTR_DESTFILE = "destFile";

    /**
     * can be a string or a File instance/reference filename extends Optional
     */
    static final String ATTR_SOURCEFILE = "sourceFile";

    /**
     * document type of type DocType; docType extends Optional
     */
    static final String ATTR_DOCTYPE = "docType";
    /**
     * encoding string when saving encoding extends String;
     */
    static final String ATTR_ENCODING = "encoding";

    /**
     * root node must be a Document Node. root extends Compulsory
     */
    static final String ATTR_ROOT = "root";

    /**
     * save a document to a file
     * @param filename
     * @param encoding
     * @throws RemoteException
     */
    void save(String filename, String encoding) throws RemoteException,IOException ;

    /**
     * save a document to a file, utf-8 encoded
     *
     * @param filename
     * @throws RemoteException
     */
    void save(String filename) throws RemoteException,
            IOException;

}
