package org.smartfrog.services.xml.interfaces;


/**
 * Nothing implements this, but other interfaces extend it; it represents nodes
 * with a qname value
 */
public interface XmlQNameNode extends XmlNode {

    static final String ATTR_NAMESPACE = "namespace";
    //localname itself must be supplied
    static final String ATTR_LOCALNAME = "localname";
}
