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

package org.smartfrog.projects.alpine.om;

import nu.xom.Element;
import nu.xom.Nodes;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.projects.alpine.om.soap12.Soap12NodeFactory;
import org.smartfrog.projects.alpine.om.soap11.Soap11NodeFactory;
import org.smartfrog.projects.alpine.om.soap11.SoapElementFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * the chain of factories
 */
public class FactoryChain extends ExtendedNodeFactory {
    
    private List<ExtendedNodeFactory> chain=new ArrayList<ExtendedNodeFactory>();


    /**
     * Add a new factory
     * @param factory factory to add
     */
    public void add(ExtendedNodeFactory factory) {
        chain.add(factory);
    }


    /**
     * Implement inscope test
     * @param element element name
     * @param namespace namespace or ""
     * @return true if a factory was found
     */
    public boolean inScope(String element, String namespace) {
        return null!=findFactory(element, namespace);
    }

    /**
     * Find the handler for the element/namespace
     * @param element element name
     * @param namespace namespace or ""
     * @return a factory if found, or null
     */
    public ExtendedNodeFactory findFactory(String element, String namespace) {
        for(ExtendedNodeFactory factory:chain) {
            if(factory.inScope(element, namespace)) {
                return factory;
            }
        }
        return null;
    }

    /**
     * Find a factory or throw an exception
     * @param name name
     * @param namespace namespace or ""
     * @return the factory
     * @throws AlpineRuntimeException if no factory was found
     */
    private ExtendedNodeFactory lookupFactory(String name, String namespace) {
        ExtendedNodeFactory factory = findFactory(name, namespace);
        if (factory == null) {
            throw new AlpineRuntimeException("No factory for (" + namespace +
                    ',' + name + ')');
        }
        return factory;
    }

    public Element makeRootElement(String name, String namespace) {
        ExtendedNodeFactory factory = lookupFactory(name, namespace);
        return factory.makeRootElement(name, namespace);
    }


    public Element startMakingElement(String name, String namespace) {
        return lookupFactory(name, namespace).startMakingElement(name,namespace);
    }

    public Nodes finishMakingElement(Element element) {
        ExtendedNodeFactory factory = lookupFactory(element.getLocalName(),
                element.getNamespaceURI());
        return factory.finishMakingElement(element);
    }

    /**
     * Create a factory chain for handling SOAP1.1 and 1.2
     * @param soap11 flag to request a soap11 handler
     * @param soap12 flag to request a soap1.2 handler
     * @return the factory chain
     */
    public static FactoryChain createSoapFactoryChain(boolean soap11,boolean soap12) {
        FactoryChain fc=new FactoryChain();
        if (soap12) {
            fc.add(new Soap12NodeFactory());
        }
        if (soap11) {
            fc.add(new Soap11NodeFactory());
        }
        fc.add(new SoapElementFactory());
        return fc;
    }
}
