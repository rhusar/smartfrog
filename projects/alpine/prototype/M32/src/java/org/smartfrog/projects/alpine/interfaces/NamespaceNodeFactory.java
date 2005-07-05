package org.smartfrog.projects.alpine.interfaces;

import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.NodeFactory;

/**
 
 */
public interface NamespaceNodeFactory {
    
    
    /**
     * Make a new element
     *
     * @param fullname  this comes in with a prefix: on it, which we will need to strip off
     * @param namespace
     * @return an element or null
     */
    public Element startMakingElement(String fullname, String namespace, NodeFactory baseFactory);
    
    public Nodes finishMakingElement(Element element, NodeFactory baseFactory) ;
}
