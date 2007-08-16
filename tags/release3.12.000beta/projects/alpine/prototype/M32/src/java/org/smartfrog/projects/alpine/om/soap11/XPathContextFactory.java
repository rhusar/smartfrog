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

import nu.xom.XPathContext;

/**
 
 */
public class XPathContextFactory implements SoapConstants {


    public static XPathContext create() {
        XPathContext context=new XPathContext();
        context.addNamespace("xsd",URI_XSD_2001);
        context.addNamespace("soap11", URI_SOAP11);
        context.addNamespace("env11", URI_SOAP11);
        context.addNamespace("soap12", URI_SOAP12);
        context.addNamespace("env12", URI_SOAP12);
        context.addNamespace("soap", URI_SOAPAPI);
        context.addNamespace("env", URI_SOAPAPI);
        context.addNamespace("wsdl", URI_WSDL);
        context.addNamespace("xml", URI_XML_1998);
        return context;
}

}
