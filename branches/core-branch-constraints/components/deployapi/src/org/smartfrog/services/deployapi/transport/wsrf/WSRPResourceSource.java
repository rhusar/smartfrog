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
package org.smartfrog.services.deployapi.transport.wsrf;

import nu.xom.Element;
import org.smartfrog.services.deployapi.transport.faults.BaseException;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * Interface for anything that provides WSRP resource information.
 * created 22-Sep-2005 15:57:46
 */


public interface WSRPResourceSource {

    /**
     * Get a property value
     *
     * @param property
     * @return null for no match;
     * @throws BaseException if they feel like it
     */
    List<Element> getProperty(QName property);
}
