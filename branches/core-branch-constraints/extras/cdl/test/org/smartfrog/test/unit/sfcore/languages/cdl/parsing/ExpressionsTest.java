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
package org.smartfrog.test.unit.sfcore.languages.cdl.parsing;

import org.smartfrog.sfcore.languages.cdl.dom.Expression;
import org.smartfrog.sfcore.languages.cdl.dom.ErrorMessages;
import org.smartfrog.test.unit.sfcore.languages.cdl.XmlTestBase;

/**
 * created 06-Jun-2005 15:02:33
 */

public class ExpressionsTest extends XmlTestBase {

    public ExpressionsTest(String name) {
        super(name);
    }

    /**
     * Sets up the fixture by initialising the parser
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testSimpleExpression() throws Exception {
        loadValidCDL(CDL_DOC_EXPRESSION_1);
    }

    public void testDuplicateExpressions() throws Exception {
        assertInvalidCDL(CDL_DOC_EXPRESSION_DUPLICATE,
                ErrorMessages.ERROR_DUPLICATE_VALUE);
    }


}
