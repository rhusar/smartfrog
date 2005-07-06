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

package org.smartfrog.projects.alpine.om.soap11;

import org.smartfrog.projects.alpine.om.ExtendedNodeFactory;
import org.smartfrog.projects.alpine.om.base.ElementEx;
import org.smartfrog.projects.alpine.xmlutils.XsdUtils;
import nu.xom.Element;

/**
 * this iteration doesnt have the envisaged chain of handlers, all we do is create soap nodes
 * 
 */
public class SoapFactory extends ExtendedNodeFactory {


    /**
     * Make a new element
     *
     * @param fullname  this comes in with a prefix: on it, which we will need to strip off
     * @param namespace
     * @return
     */
    public Element startMakingElement(String fullname, String namespace) {
        String name = XsdUtils.extractLocalname(fullname);
        if (Soap11Constants.ELEMENT_ENVELOPE.equals(name)) {
            return new Envelope(name, namespace);
        }
        if (Soap11Constants.ELEMENT_HEADER.equals(name)) {
            return new Header(name, namespace);
        }
        if (Soap11Constants.ELEMENT_FAULT.equals(name)) {
            //TODO: more fault parsing?
            return new Fault(name, namespace);
        }
        
        //something else in our namespace. wierd.
        return new ElementEx(name, namespace);
    }
    
    
}
