package org.smartfrog.services.deployapi.transport.wsrf;

import nu.xom.Element;

import javax.xml.namespace.QName;
import java.util.List;

/**

 */
public interface Property {
    public QName getName();

    public List<Element> getValue();
}
