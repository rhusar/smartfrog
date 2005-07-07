package org.smartfrog.projects.alpine.interfaces;

import org.smartfrog.projects.alpine.om.soap11.Fault;

/**
 * This interface is primarily for Exceptions. It declares that we can
 * create a SOAPFault
 */
public interface SoapFaultSource {
    
    
    /**
     * Create a soap fault from ourselves.
     * @return
     */ 
    public Fault GenerateSoapFault();
}
