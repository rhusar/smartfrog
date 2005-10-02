package org.smartfrog.services.deployapi.transport.wsrf;

import org.apache.axis2.om.OMElement;

import javax.xml.namespace.QName;

/**

 */
public interface Property {
    public QName getName();

    public OMElement getValue();
}
