/*
 * SmartFrogSyntaxTest.java
 * JUnit based test
 *
 * Created on December 23, 2006, 7:54 AM
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
        
        TokenID token = null;
        Iterator i = Arrays.asList(expected).iterator();
        do {
            token = s.nextToken();
            if (token != null) {
                if (!i.hasNext()) {
                    fail("More tokens returned than expected.");
                } else {
                    assertSame("Tokens differ", i.next(), token);
                }
            } else {
                assertFalse("More tokens expected than returned.", i.hasNext());
            }
            System.out.println(token);
        } while (token != null);
    }
}
