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
package org.smartfrog.test.system.projects.alpine.remote;

import org.smartfrog.projects.alpine.wsa.AddressingConstants;

/**
 * created 13-Apr-2006 10:27:32
 */

public class Wsa2004EchoTest extends EchoTest {

    public Wsa2004EchoTest(String name) {
        super(name);
    }


    /**
     * Sets up the fixture, for example, open a network connection. This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        address.setNamespace(AddressingConstants.XMLNS_WSA_2004);
        address.setMustUnderstand(true);
    }

    /**
     * @return the path of the actual endpoint.
     */
    protected String getEndpointName() {
        return WSA_PATH;
    }

}
