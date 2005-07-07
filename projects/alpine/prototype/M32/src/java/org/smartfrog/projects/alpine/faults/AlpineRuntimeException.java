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

package org.smartfrog.projects.alpine.faults;

import org.smartfrog.projects.alpine.interfaces.SoapFaultSource;
import org.smartfrog.projects.alpine.om.soap11.Fault;
import org.smartfrog.projects.alpine.om.soap11.Soap11Constants;

/**
 * this is a runtime exception
 */
public class AlpineRuntimeException extends RuntimeException implements SoapFaultSource {
    public AlpineRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlpineRuntimeException(Throwable cause) {
        super(cause);
    }

    public AlpineRuntimeException(String message) {
        super(message);
    }

    /**
     * Create a soap fault from ourselves.
     * subclass this to add more detail than just the message, stack trace, 
     *
     * @return a fault
     */
    public Fault GenerateSoapFault() {
        Fault fault=new Fault();
        fault.setFaultCode(Soap11Constants.FAULTCODE_SERVER);
        
        return null;
    }
    
    /**
     * This is an override point, subclasses can add stuff to a fault that already
     * has been preconfigured by the base class
     * @param fault
     */ 
    public void addExtraDetails(Fault fault) {
        
    }
    
}
