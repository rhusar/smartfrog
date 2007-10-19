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

package org.smartfrog.test.unit.sfcore.languages.cdl;

import junit.framework.TestCase;

import java.net.URL;

import org.smartfrog.sfcore.languages.cdl.CdlCatalog;

/**
 */
public class ClasspathTest extends TestCase {
    private ClassLoader classLoader;


    public void testXmlCdl() throws Exception {
        assertResourceExists(CdlCatalog.RESOURCE_XML_CDL_XSD);
    }

    public void testWSA() throws Exception {
        assertResourceExists(CdlCatalog.RESOURCE_WS_ADDR_XSD);
    }

    public void testDeployapi() throws Exception {
        assertResourceExists(CdlCatalog.RESOURCE_DEPLOYAPI_XSD);
    }


    private void assertResourceExists(String name) {
        URL resource = classLoader.getResource(name);
        assertNotNull("Missing Resource "+name,resource);
    }

    protected void setUp() throws Exception {
        super.setUp();
        classLoader = getClass().getClassLoader();
    }
}