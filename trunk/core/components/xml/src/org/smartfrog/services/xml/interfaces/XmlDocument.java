package org.smartfrog.services.xml.interfaces;


/**
 * XML Document
 */
public interface XmlDocument extends XmlNode {


    /**
     * can be a string or a File instance/reference filename extends Optional
     */
    static final String ATTR_FILENAME = "filename";

    /**
     * document type of type DocType; docType extends Optional
     */
    static final String ATTR_DOCTYPE = "docType";
    /**
     * encoding string when saving encoding extends String;
     */
    static final String ATTR_ENCODING = "encoding";

    /**
     * flag to trigger generation when we deploy generateOnDeploy extends
     * Boolean;
     */
    static final String ATTR_GENERATE_ON_DEPLOY = "generateOnDeploy";

    /**
     * root node must be a Document Node. root extends Compulsory
     */
    static final String ATTR_ROOT = "root";
}
