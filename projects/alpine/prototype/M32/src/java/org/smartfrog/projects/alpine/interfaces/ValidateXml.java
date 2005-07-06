package org.smartfrog.projects.alpine.interfaces;

import org.smartfrog.projects.alpine.faults.InvalidXmlException;

/**
 * An xml validation interface. Any node that implements this is declaring that 
 * when its parent wants to be validated, so should it.
 */
public interface ValidateXml {
    
    /**
     * Validate the Xml. Throw {@link InvalidXmlException} if invalid. 
     */ 
    public void validateXml();
}
