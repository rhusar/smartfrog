package org.smartfrog.services.xml.interfaces;

/**
 * DTD declaration
 */
public interface XmlDocType extends XmlNode {
    /**
     * extends String;
     */
    static final String ATTR_ROOT_ELEMENT_NAME = "rootElementName";
    /**
     * extends OptionalString;
     */
    static final String ATTR_SYSTEMID = "systemID";

    /**
     * publicID extends OptionalString;
     */
    static final String ATTR_PUBLICID = "publicID";
}
