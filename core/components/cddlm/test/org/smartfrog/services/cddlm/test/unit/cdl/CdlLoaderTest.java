/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.cddlm.test.unit.cdl;

import junit.framework.TestCase;
import org.smartfrog.services.cddlm.cdl.CdlParser;
import org.smartfrog.services.cddlm.cdl.ResourceLoader;
import nu.xom.Document;

/**
 * Junit test cause
 * 
 * @author root
 */
public class CdlLoaderTest extends TestCase {

    CdlParser laxParser;
    CdlParser strictParser;

    private final static String RESOURCES="org/smartfrog/services/cddlm/test/unit/cdl/";

    private final static String VALID_CDL[]= {
        "webserver.cdl"
    };

    private final static String INVALID_CDL[] = {
        "webserver.cdl"
    };

    public CdlLoaderTest(String test) {
        super(test);
    }

    /**
     * The fixture set up called before every test method.
     */
    protected void setUp() throws Exception {
        ResourceLoader loader=new ResourceLoader(this.getClass());
        laxParser=new CdlParser(loader,false);
        strictParser = new CdlParser(loader, true);
    }

    /**
     * The fixture clean up called after every test method.
     */

    protected void tearDown() throws Exception {
    }

    public void testValidOnLax() throws Exception {
        Document doc;
        for(int i=0;i<VALID_CDL.length;i++) {
            laxParser.parseResource(RESOURCES + VALID_CDL[i]);
        }
    }

    public void testInvalidOnLax() throws Exception {
        Document doc;
        for ( int i = 0; i < INVALID_CDL.length; i++ ) {
            laxParser.parseResource(RESOURCES + INVALID_CDL[i]);
        }
    }

}