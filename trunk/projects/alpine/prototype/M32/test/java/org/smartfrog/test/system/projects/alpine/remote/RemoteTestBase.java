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

package org.smartfrog.test.system.projects.alpine.remote;

import junit.framework.TestCase;

import java.net.URL;

/**
 
 */
public abstract class RemoteTestBase extends TestCase  {
    
    String endpoint;
    URL endpointURL;

    /**
     * Sets up the fixture, for example, open a network connection. This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        endpoint=System.getProperty(getEndpointPropertyName());
        if(endpoint==null) {
            throw new Exception("No endpoint property "+getEndpointPropertyName());
        }
        endpointURL=new URL(endpoint);
    }

    private String getEndpointPropertyName() {
        return "echoEndpoint";
    }

    public String getEndpoint() {
        return endpoint;
    }

    public URL getEndpointURL() {
        return endpointURL;
    }
}
