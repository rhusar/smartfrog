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

import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.interfaces.SoapFaultSource;
import org.smartfrog.projects.alpine.om.soap11.Fault;
import org.smartfrog.projects.alpine.om.soap11.SoapConstants;

/**
 * Bridge faults from Java to SOAPFault
 *
 * the namespace for faults is extracted from the message contexnt
 */
public class FaultBridge {

    private String namespace;


    /**
     * Create a bridge
     * @param namespace namespace to sue
     */
    public FaultBridge(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Get a faultbridge for a message. This is
     * based on the message context for dynamicity (maybe)
     * @param context source of xmlns and other values
     * @return a new bridge
     */ 
    public static FaultBridge getFaultBridge(MessageContext context) {
        return new FaultBridge(context.getSoapNamespace());
    }

    /**
     * turn a caught fault into a runtime exception, for throwing or for extracting data from
     * <ol>
     * <li>AlpineRuntimeException instances are passed through</li>
     * <li>Anything that is a SoapFaultSource has its fault extracted into a SoapException</li>
     * <li>anything else is turned into a SoapException</li>
     * @param thrown what went wrong
     * @return a translated fault
     */ 
    public AlpineRuntimeException translateFault(Throwable thrown) {
        if(thrown instanceof AlpineRuntimeException) {
            return (AlpineRuntimeException) thrown;
        }
        if(thrown instanceof SoapFaultSource) {
            return convertFaultSourceToAlpineException(thrown);
        }
        //anything else. 
        return convertThrowableToAlpineException(thrown);
    }

    public AlpineRuntimeException convertFaultSourceToAlpineException(Throwable thrown) {
        Fault fault;
        SoapFaultSource source = (SoapFaultSource) thrown;
        fault = source.GenerateSoapFault(namespace);
        return new SoapException(fault);
    }

    /**
     * Turn a throwable into an exception
     * @param thrown what got thrown
     * @return a {@link SoapException}
     */
    public AlpineRuntimeException convertThrowableToAlpineException(Throwable thrown) {
        //create a fault
        Fault fault = createFault(thrown);
        //now we create a soap exception from it
        SoapException soapException=new SoapException(thrown,fault);
        return soapException;
    }

    /**
     * Create a Fault element from a thrown element
     * @param thrown what was thrown
     * @return a matching {Fault}
     */
    private Fault createFault(Throwable thrown) {
        Fault fault=new Fault(SoapConstants.ELEMENT_FAULT,namespace);
        fault.addThrowable(thrown);
        return fault;
    }

    /**
     * If throwable implements {@link SoapFaultSource} we extract the
     * fault from the source, otherwise we create a new fault
     * @param thrown what was thrown
     * @return a fault
     */ 
    public Fault extractFaultFromThrowable(Throwable thrown) {
        if (thrown instanceof SoapFaultSource) {
            return ((SoapFaultSource)(thrown)).GenerateSoapFault(namespace);
        }
        //anything else.
        return createFault(thrown);
    }

}
