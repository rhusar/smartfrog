/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jmx.mbeanbrowser;

import javax.management.*;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class OperationListElement {

    /**
     *  TODO
     */
    private MBeanOperationInfo m_operationInfo = null;


    /**
     *  TODO
     *
     *@param  operationInfo
     */
    public OperationListElement(MBeanOperationInfo operationInfo) {
        m_operationInfo = operationInfo;
    }


    /**
     *  TODO
     *
     *@return MBeanOperationInfo
     */
    public MBeanOperationInfo getOperationInfo() {
        return (m_operationInfo);
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String turnParametersIntoString() {
        MBeanParameterInfo[] paramArray = m_operationInfo.getSignature();
        String param2String = "( ";
        for (int i = 0; i < paramArray.length; i++) {
            param2String += paramArray[i].getType();
            if (i != paramArray.length) {
                param2String += ", ";
            }
        }
        return param2String += " )";
    }


    /**
     *  TODO
     *
     *@return  String
     */
    public String toString() {
        return (m_operationInfo.getName());
    }

}
