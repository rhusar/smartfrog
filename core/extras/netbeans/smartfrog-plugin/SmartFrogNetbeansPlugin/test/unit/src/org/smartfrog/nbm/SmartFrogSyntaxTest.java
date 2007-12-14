/* (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.nbm;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenID;
//import org.netbeans.modules.smartfrogsupport.*;
import junit.framework.TestCase;

public class SmartFrogSyntaxTest extends TestCase {
    
    public SmartFrogSyntaxTest(String testName) {
        super(testName);
    }
    
    public void testNextToken() {
        doParse("#include test.sf\n", new TokenID[] {
            SmartFrogTokenContext.INCLUDE,
            SmartFrogTokenContext.IVALUE,
            SmartFrogTokenContext.END_OF_LINE
        });
    }
    public void testExtends() {
        
        doParse("#include \"org/smartfrog/components.sf\"\n"+ "bar extends food;\n" +
                "bob extends food;\n",
                new TokenID[] {
            SmartFrogTokenContext.INCLUDE,
            SmartFrogTokenContext.IVALUE,
            SmartFrogTokenContext.END_OF_LINE,
            SmartFrogTokenContext.ATTRIBUTE_NAME,
            SmartFrogTokenContext.EXTENDS,
            SmartFrogTokenContext.BASE,
            SmartFrogTokenContext.SEMI_COLON,
            SmartFrogTokenContext.END_OF_LINE,
            SmartFrogTokenContext.ATTRIBUTE_NAME,
            SmartFrogTokenContext.EXTENDS,
            SmartFrogTokenContext.BASE,
            SmartFrogTokenContext.SEMI_COLON,
            SmartFrogTokenContext.END_OF_LINE,
        });
    }
    public void testNextIncludes() {
        
        doParse("//test\n /*test2*/\n#include \"org/smartfrog/components.sf\"\n"+ "bar extends food;\n" +
                "bob extends food;\n",
                new TokenID[] {
            SmartFrogTokenContext.COMMENT,
            SmartFrogTokenContext.END_OF_LINE,
            SmartFrogTokenContext.COMMENT,
            SmartFrogTokenContext.END_OF_LINE,
            SmartFrogTokenContext.INCLUDE,
            SmartFrogTokenContext.IVALUE,
            SmartFrogTokenContext.END_OF_LINE,
            SmartFrogTokenContext.ATTRIBUTE_NAME,
            SmartFrogTokenContext.EXTENDS,
            SmartFrogTokenContext.BASE,
            SmartFrogTokenContext.SEMI_COLON,
            SmartFrogTokenContext.END_OF_LINE,
            SmartFrogTokenContext.ATTRIBUTE_NAME,
            SmartFrogTokenContext.EXTENDS,
            SmartFrogTokenContext.BASE,
            SmartFrogTokenContext.SEMI_COLON,
            SmartFrogTokenContext.END_OF_LINE,
        });
        
    }
    public void testExtendedAttribute() {
        doParse("#include \"org/smartfrog/components.sf\"\n"+ "bar extends food;\n" +
                "bob extends food { base 56; }\n",
                new TokenID[] {
            SmartFrogTokenContext.INCLUDE,
            SmartFrogTokenContext.IVALUE,
            SmartFrogTokenContext.END_OF_LINE,
            SmartFrogTokenContext.ATTRIBUTE_NAME,
            SmartFrogTokenContext.EXTENDS,
            SmartFrogTokenContext.BASE,
            SmartFrogTokenContext.SEMI_COLON,
            SmartFrogTokenContext.END_OF_LINE,
            SmartFrogTokenContext.ATTRIBUTE_NAME,
            SmartFrogTokenContext.EXTENDS,
            SmartFrogTokenContext.BASE,
            SmartFrogTokenContext.OPEN_BRACKET,
            SmartFrogTokenContext.ATTRIBUTE_NAME,
            SmartFrogTokenContext.ATTRIBUTE_VALUE,
            SmartFrogTokenContext.SEMI_COLON,
            SmartFrogTokenContext.CLOSE_BRACKET,
            SmartFrogTokenContext.END_OF_LINE,
        });
    }
    
    public void doParse(String m, TokenID[] expected) {
        Syntax s = new SmartFrogSyntax();
        s.load(null, m.toCharArray(), 0, m.length(), true, m.length());
        int position=0;
        TokenID token = null;
        Iterator<TokenID> i = Arrays.asList(expected).iterator();
        do {
            token = s.nextToken();
            if (token != null) {
                if (!i.hasNext()) {
                    fail("More tokens returned than expected.");
                } else {
                    expectToken("Tokens differ at position "+position, i.next(), token);
                }
            } else {
                assertFalse("More tokens expected than returned.", i.hasNext());
            }
            System.out.println("@"+position+"\t"+token);
            position++;
        } while (token != null);
    }
    
    
    protected void expectToken(String message,TokenID expected, TokenID actual) {
        if(!expected.equals(actual)) {
            String details;
            details=actual==null?"(null)"
                    :(actual.toString()+" #"+actual.getNumericID());
            fail(message+"\nexpected "+expected.toString()+" #"+expected.getNumericID()
                    +"\nfound:"+details);
        }
    }
}
