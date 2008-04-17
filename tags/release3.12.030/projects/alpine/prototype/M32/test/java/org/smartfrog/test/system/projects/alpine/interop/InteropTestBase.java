/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.projects.alpine.interop;

import org.smartfrog.test.system.projects.alpine.remote.RemoteTestBase;

/**
 * created 05-May-2006 11:52:25
 * <p/>
 * http://webservices.sonicsw.com:8080/interop/services/wsaTest - Apache Axis Web Services Addressing
 * http://cwctest.dynalias.org:9080/wsaiop/ - IBM WSAddressing Implementation
 * http://213.162.124.157:8080/interop - JBoss Web Services Addressing
 * http://131.107.72.15/WSAddressingCR_Service_WCF/ - Microsoft Windows Communication Foundation 1.0 (WCF)
 * http://soapinterop.java.sun.com:8080/index.html - Sun Java API for XML Web Services Addressing
 * http://www-lk.wso2.com:5049/axis2/services/wsaTestService - WSO2 Web Services Addressing
 */

public abstract class InteropTestBase extends RemoteTestBase {
    public InteropTestBase(String name) {
        super(name);
    }

    /**
     * ovveride point: get the endpoint and bind to it.
     *
     * @throws Exception
     */
    protected void selectEndpoint() throws Exception {

    }
}
