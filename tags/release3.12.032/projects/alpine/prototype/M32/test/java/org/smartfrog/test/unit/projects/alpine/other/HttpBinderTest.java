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
package org.smartfrog.test.unit.projects.alpine.other;

import junit.framework.TestCase;
import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.faults.ServerException;
import org.smartfrog.projects.alpine.http.HttpBinder;
import org.smartfrog.projects.alpine.http.HttpConstants;

/**
 * created 03-May-2006 15:56:28
 */

public class HttpBinderTest extends TestCase {


    /**
     * Constructs a test case with the given name.
     */
    public HttpBinderTest(String name) {
        super(name);
    }

    private HttpBinder binder;


    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        EndpointContext ctx = new EndpointContext();
        binder = new HttpBinder(ctx);
    }

    public void testShortContentType() throws Exception {
        HttpBinder.validateContentType("text/xml");
    }

    public void testUTF88Content() throws Exception {
        HttpBinder.validateContentType("text/xml; charset=UTF-8");
    }


    public void testSOAPContent() throws Exception {
        HttpBinder.validateContentType(HttpConstants.CONTENT_TYPE_SOAP_XML+"; charset=UTF-8");
    }

    public void testHtmlContent() throws Exception {
        try {
            HttpBinder.validateContentType("text/html; charset=UTF-8");
            fail("shound not have validated this");
        } catch (ServerException e) {
            //expected
        }
    }

}
