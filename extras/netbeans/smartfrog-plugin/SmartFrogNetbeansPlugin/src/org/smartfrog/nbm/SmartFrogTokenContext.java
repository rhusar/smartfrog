/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

import org.netbeans.editor.BaseTokenID;
import org.netbeans.editor.TokenContext;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.Utilities;

public class SmartFrogTokenContext extends TokenContext {
    
    // Numeric-ids for token categories
    public static final int INCLUDE_ID = 1;
    public static final int IVALUE_ID=2;
    public static final int ATTRIBUTE_NAME_ID=3;
    public static final int ATTRIBUTE_VALUE_ID=4;
    public static final int EXTENDS_ID = 5;
    public static final int BASE_PROTOTYPE_ID = 6;
    public static final int OPEN_BRACKET_ID = 7;
    public static final int CLOSE_BRACKET_ID = 8;
    public static final int SEMI_COLON_ID = 9;
    public static final int END_OF_LINE_ID = 10;
    public static final int COMMENT_ID = 11;
    public static final int LAZY_ID = 12;
    public static final int KEYWORD_ID = 13;
    public static final int STRING_ID = 14;
    public static final int OPERATOR_ID=15;
    public static final int TBD_ID=16;
    
    // Token-ids
    public static final BaseTokenID INCLUDE = new BaseTokenID("include", INCLUDE_ID);
    public static final BaseTokenID IVALUE = new BaseTokenID("ivalue", IVALUE_ID);
    public static final BaseTokenID ATTRIBUTE_NAME = new BaseTokenID("attributename", ATTRIBUTE_NAME_ID);
    public static final BaseTokenID ATTRIBUTE_VALUE = new BaseTokenID("attributevalue", ATTRIBUTE_VALUE_ID);
    public static final BaseTokenID EXTENDS = new BaseTokenID("extends", EXTENDS_ID);
    public static final BaseTokenID BASE = new BaseTokenID("base", BASE_PROTOTYPE_ID);
    public static final BaseTokenID OPEN_BRACKET = new BaseTokenID("openbracket", OPEN_BRACKET_ID);
    public static final BaseTokenID CLOSE_BRACKET = new BaseTokenID("closebracket", CLOSE_BRACKET_ID);
    public static final BaseTokenID SEMI_COLON = new BaseTokenID("semicolon", SEMI_COLON_ID);
    public static final BaseTokenID END_OF_LINE = new BaseTokenID("endofline", END_OF_LINE_ID);
    public static final BaseTokenID COMMENT = new BaseTokenID("comment", COMMENT_ID);
    public static final BaseTokenID LAZY = new BaseTokenID("lazy", LAZY_ID);
    public static final BaseTokenID KEYWORD = new BaseTokenID("keyword", KEYWORD_ID);
    public static final BaseTokenID STRING = new BaseTokenID("string", STRING_ID);
    public static final BaseTokenID OPERATOR = new BaseTokenID("operator", OPERATOR_ID);
    public static final BaseTokenID TBD = new BaseTokenID("tbd", TBD_ID);
    
    // Context instance declaration
    public static final SmartFrogTokenContext context = new SmartFrogTokenContext();
    public static final TokenContextPath contextPath = context.getContextPath();
    
    /**
     * Construct a new ManifestTokenContext
     */
    private SmartFrogTokenContext() {
        super("sf-");
        
        try {
            addDeclaredTokenIDs();
        } catch (Exception e) {
            System.out.println("Exception: " + e.getLocalizedMessage());
            Utilities.annotateLoggable(e);
        }
    }

    
}
