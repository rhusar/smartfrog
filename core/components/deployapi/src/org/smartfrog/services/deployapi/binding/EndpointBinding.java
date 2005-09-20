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
package org.smartfrog.services.deployapi.binding;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.axis2.om.OMElement;

/**
 * One stop class to create both the in and out bindings for an endpoint.
 * created 20-Sep-2005 17:09:45
 */

public class EndpointBinding<Tin extends XmlObject,Tout extends XmlObject> {

    private Axis2Beans<Tin> in;
    private Axis2Beans<Tout> out;

    public EndpointBinding(XmlOptions inOptions, XmlOptions outOptions) {
        in=new Axis2Beans<Tin>(inOptions);
        out=new Axis2Beans<Tout>(outOptions);
    }

    public EndpointBinding() {
        this(null,null);
    }

    public Axis2Beans<Tin> getIn() {
        return in;
    }

    public Axis2Beans<Tout> getOut() {
        return out;
    }

    public Tin convertIn(OMElement element) {
        return in.convert(element);
    }

    public OMElement convertIn(Tin data) {
        return in.convert(data);
    }

    public Tout convertOut(OMElement element) {
        return out.convert(element);
    }

    public OMElement convertOut(Tout data) {
        return out.convert(data);
    }

    /**
     * create a request object
     * @return
     */
    public Tin createRequest() {
        return in.createInstance();
    }

    /**
     * create a request object
     * @return
     */
    public Tout createResponse() {
        return out.createInstance();
    }
}
