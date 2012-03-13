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

package org.smartfrog.test.unit.projects.alpine;

import org.smartfrog.projects.alpine.om.soap11.MessageDocument;

/**
 * this test base is for testing valid files. Every test case covers one file 
 * The
 * {@link #setUp()} operation loads in the chosen test file. 
 */
public abstract class ValidTestBase extends ParserTestBase {
    public static final String URI_EXAMPLE_ORG_1 = "http://example.org/uri/1";

    protected ValidTestBase(String name) {
        super(name);
    }
    
    protected MessageDocument document;

    /**
     * Get the loaded document
     * @return
     */ 
    public MessageDocument getDocument() {
        return document;
    }

    /**
     * Sets up the fixture by initialising the parser
     * and then loading in the resource specified by {@link #getTestResource()} 
     */
    protected void setUp() throws Exception {
        super.setUp();
        document=load(getTestResource());
    }
    
    /**
     * Implement this
     * @return the resource to test
     */ 
    protected abstract String getTestResource();
    
    /**
     * Test that our doc believes that it is valid
     * @throws Exception
     */ 
    public void testValid() throws Exception {
        document.validateXml();
    }
}
