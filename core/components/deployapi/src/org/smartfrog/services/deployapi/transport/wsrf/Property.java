package org.smartfrog.services.deployapi.transport.wsrf;

import nu.xom.Element;

import javax.xml.namespace.QName;

/**

 */
public interface Property {
    public QName getName();

    public Element getValue();
}
