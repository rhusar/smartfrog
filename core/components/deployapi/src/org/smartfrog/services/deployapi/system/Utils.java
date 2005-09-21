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
package org.smartfrog.services.deployapi.system;

import org.ggf.cddlm.utils.QualifiedName;
import org.ggf.xbeans.cddlm.cmp.DeploymentFaultType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.smartfrog.services.deployapi.transport.faults.BaseException;

import javax.xml.namespace.QName;
import java.util.ArrayList;

/**
 * created 20-Sep-2005 17:07:38
 */

public class Utils {

    protected Utils() {
    }

    /**
    * Turn a ggf qualifiedname into a proper java one
    * @param in
    * @return a converted qname
    */
    public static QName convert(QualifiedName in) {
        return new QName(in.getNamespaceURI(),in.getLocalPart());
    }

    /**
     * Turn a java qname into a ggf qualifiedname
     * @param in
     * @return a converted qname
     */
    public static QualifiedName convert(QName in) {
        return new QualifiedName(in.getNamespaceURI(), in.getLocalPart());
    }

    /**
     * We are invalid.
     *
     * @param message
     * @throws org.smartfrog.services.deployapi.transport.faults.DeploymentException
     * @returns true for use in conditional code
     */
    public static boolean validate(XmlObject message) {
        ArrayList validationErrors = new ArrayList();
        XmlOptions validationOptions = new XmlOptions();
        validationOptions.setErrorListener(validationErrors);
        if (!message.validate(validationOptions)) {
            DeploymentFaultType fault = DeploymentFaultType.Factory.newInstance();
            //TODO
            throw new BaseException(Constants.BAD_ARGUMENT_ERROR_MESSAGE);
        }
        return true;
    }

    /**
     * validate documents iff assertions are enabled
     *
     * @param message
     */
    public static void maybeValidate(XmlObject message) {
        assert validate(message);
    }
}
