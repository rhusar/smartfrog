package org.smartfrog.services.deployapi.transport.wsrf;

import org.apache.ws.commons.om.OMElement;

import javax.xml.namespace.QName;

/**

 */
public interface Property {
    public QName getName();

    public OMElement getValue();
}
