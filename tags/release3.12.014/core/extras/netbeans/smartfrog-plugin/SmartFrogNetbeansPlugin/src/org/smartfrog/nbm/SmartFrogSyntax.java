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

import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenID;
import org.openide.ErrorManager;

public class SmartFrogSyntax extends Syntax {
    
    // possible states for parsing
    
    private static final int STATE_INIT = 0;
    private static final int STATE_START_OF_LINE = 1;
    private static final int STATE_INCLUDE = 2;
    private static final int STATE_ATTRIBUTE_NAME = 3;
    private static final int STATE_ATTRIBUTE_VALUE = 4;
    private static final int STATE_CHILD_BLOCK = 5;
    private static final int STATE_AFTER_NAME = 6;
    private static final int STATE_AFTER_EXTENDS = 7;
    private static final int STATE_PARTIALWORD = 8;
    private static final int ISA_AFTER_POUND = 9;
    private static final int STATE_AFTER_INCLUDE = 10;
    private static final int STATE_IN_COMMENT_LINE = 11;
    private static final int STATE_IN_COMMENT_BLOCK = 12;
    private static final int STATE_IN_POUND_QUOTE = 13;
    private static final int STATE_OPERATOR = 14;
    private static final int STATE_OPERATOR_SIMPLEVALUE = 15;
    private static final int STATE_OPERATOR_BINARY = 16;
    private static final int STATE_IF = 17;
    private static final int STATE_THEN = 18;
    private static final int STATE_ELSE = 19;
    private static final int STATE_AFTER_THEN = 20;
    private static final int STATE_AFTER_ELSE = 21;
    private static final int STATE_FI = 22;
    private static final int STATE_OPER = 23;
    private static final int STATE_VECTOR = 24;
    private static final int STATE_VECTOR_STRING=25;
    private static final int STATE_IN_QUOTE = 26;
    
    private String partialToken = null;
    boolean firstQuote = false;
    private int operatorStackCount = 0;
    
    public SmartFrogSyntax() {
        tokenContextPath = SmartFrogTokenContext.contextPath;
    }
    
    /**
     * The logger for this class. It can be used for tracing the class activity,
     * logging debug messages, etc.
     */
    private static final ErrorManager LOGGER =
            ErrorManager.getDefault().getInstance("com.hp.ov." +
            "smartfrogsvc.SmartFrogSyntax");
    
    /**
     * Used to avoing calling the log() or notify() method if the message
     * wouldn't be loggable anyway.
     */
    private static final boolean LOG =
            LOGGER.isLoggable(ErrorManager.INFORMATIONAL);
    
    
    protected TokenID parseToken() {
        if (state == INIT) {
            state = this.STATE_INIT;
        }
        TokenID result = doParseToken();
        if (LOG) {
            LOGGER.log(ErrorManager.INFORMATIONAL, "parseToken: " + result);
        }
        return result;
    }
    
    private TokenID doParseToken() {
        char actChar;
        String tbuf = new String(buffer);
        while (offset < stopOffset) {
            actChar = buffer[offset];
            switch (state) {
                case STATE_INIT:
                    partialToken = new String();
                    switch (actChar) {
                        case ' ':
                            break;
                        case '\n':
                            offset++;
                            return SmartFrogTokenContext.END_OF_LINE;
                        case ';':
                            offset++;
                            return SmartFrogTokenContext.SEMI_COLON;
                        case '{':
                            offset++;
                            return SmartFrogTokenContext.OPEN_BRACKET;
                        case '}':
                            offset++;
                            return SmartFrogTokenContext.CLOSE_BRACKET;
                        case '#':
                            state = ISA_AFTER_POUND;
                            offset++;
                            return SmartFrogTokenContext.INCLUDE;
                        case '/' :
                            offset++;
                            actChar = buffer[offset];
                            if (actChar == '/') {
                                state = this.STATE_IN_COMMENT_LINE;
                                offset++;
                                return SmartFrogTokenContext.COMMENT;
                            } else if (actChar == '*') {
                                state = this.STATE_IN_COMMENT_BLOCK;
                                offset++;
                                return SmartFrogTokenContext.COMMENT;
                            } else {
                                offset++;
                                return SmartFrogTokenContext.ATTRIBUTE_NAME;
                            }
                        case '-':
                            offset++;
                            actChar = buffer[offset];
                            if (actChar == '-') {
                                offset++;
                                state = this.STATE_AFTER_NAME;
                                return SmartFrogTokenContext.KEYWORD;
                            } else {
                                state = this.STATE_ATTRIBUTE_NAME;
                            }
                        default:
                            state = this.STATE_ATTRIBUTE_NAME;
                            partialToken += actChar;
                            return SmartFrogTokenContext.ATTRIBUTE_NAME;
                    }
                    break;
                case STATE_IN_COMMENT_LINE:
                    switch (actChar) {
                        case '\n' :
                            state = this.STATE_INIT;
                            return SmartFrogTokenContext.COMMENT;
                        default:
                            if ( (offset+1 == stopOffset) && (lastBuffer) ) {
                                offset++;
                                return SmartFrogTokenContext.COMMENT;
                            }
                            break;
                    }
                    break;
                case STATE_IN_COMMENT_BLOCK:
                    switch (actChar) {
                        case '*':
                            offset++;
                            if (offset < stopOffset) {
                                actChar = buffer[offset];
                                if (actChar == '/') {
                                    offset++;
                                    state = this.STATE_INIT;
                                    return SmartFrogTokenContext.COMMENT;
                                } else {
                                    offset++;
                                    return SmartFrogTokenContext.COMMENT;
                                }
                            } else {
                                offset++;
                                return SmartFrogTokenContext.COMMENT;
                            }
                        default:
                            if ( (offset+1 == stopOffset) && (lastBuffer) ) {
                                offset++;
                                return SmartFrogTokenContext.COMMENT;
                            } else {
                                offset++;
                                return SmartFrogTokenContext.COMMENT;
                            }
                    }
                case STATE_AFTER_INCLUDE:
                    switch (actChar) {
                        case '\n':
                            state = this.STATE_INIT;
                            return SmartFrogTokenContext.IVALUE;
                        case '\"':
                            if (!firstQuote) {
                                firstQuote = true;
                                offset++;
                                return SmartFrogTokenContext.IVALUE;
                            } else {
                                firstQuote = false;
                                state = this.STATE_INIT;
                                offset++;
                                return SmartFrogTokenContext.IVALUE;
                            }
                        default:
                                offset++;
                                return SmartFrogTokenContext.IVALUE;
                    }
                case ISA_AFTER_POUND:
                    switch (actChar) {
                        case '\n' :
                        case ' ' :
                            if (partialToken.equalsIgnoreCase("include")) {
                                firstQuote = false;
                                state = this.STATE_AFTER_INCLUDE;
                                offset++;
                                return SmartFrogTokenContext.INCLUDE;
                            } else {
                                state = this.STATE_INIT;
                            }
                            break;
                        default :
                            partialToken += actChar;
                            if ( (offset+1 == stopOffset) && (lastBuffer) ) {
                                offset++;
                                return SmartFrogTokenContext.INCLUDE;
                            }
                            
                    }
                    break;
                case STATE_ATTRIBUTE_NAME:
                    switch (actChar) {
                        case ':' :
                            offset++;
                            partialToken = new String();
                            return SmartFrogTokenContext.ATTRIBUTE_NAME;
                        case ';' :
                            state = this.STATE_INIT;
                            return SmartFrogTokenContext.ATTRIBUTE_NAME;
                        case '\n' :
                            state = this.STATE_INIT;
                            return SmartFrogTokenContext.ATTRIBUTE_NAME;
                        case ' ' :
                            state = this.STATE_AFTER_NAME;
                            offset++;
                            partialToken = new String();
                            return SmartFrogTokenContext.ATTRIBUTE_NAME;
                        default:
                            partialToken += actChar;
                            if ( (partialToken.equals("ROOT")) ||
                                    (partialToken.equals("PARENT")) ||
                                    (partialToken.equals("ATTRIB")) ||
                                    (partialToken.equals("HERE")) ||
                                    (partialToken.equals("THIS")) ||
                                    (partialToken.equals("PROPERTY")) ||
                                    (partialToken.equals("IPROPERTY")) ||
                                    (partialToken.equals("HOST")) ||
                                    (partialToken.equals("PROCESS")) ) {
                                partialToken = new String();
                                offset++;
                                return SmartFrogTokenContext.KEYWORD;
                            } else if ( (offset+1 == stopOffset) && (lastBuffer) ) {
                                offset++;
                                return SmartFrogTokenContext.ATTRIBUTE_NAME;
                            }
                    }
                    break;
                case STATE_OPER:
                    switch (actChar) {
                        case '\n':
                            state = STATE_INIT;
                            offset++;
                            return SmartFrogTokenContext.END_OF_LINE;
                        case ';':
                            offset++;
                            return SmartFrogTokenContext.SEMI_COLON;
                        case '(':
                        case ')':
                        case '-':
                        case '/':
                        case '=':
                        case '>':
                        case '<':
                        case '+':
                        case '*':
                        case '&':
                        case '|':
                            offset++;
                            return SmartFrogTokenContext.OPERATOR;
                        default:
                            offset++;
                            return SmartFrogTokenContext.ATTRIBUTE_VALUE;
                    }
                    
                case STATE_OPERATOR:
                    switch (actChar) {
                        case ';':
                            state = STATE_INIT;
                            return SmartFrogTokenContext.OPERATOR;
                        case '!' :
                            offset++;
                            state = STATE_OPERATOR_SIMPLEVALUE;
                            return SmartFrogTokenContext.OPERATOR;
                        case ' ' :
                            break;
                        case '(' :
                            offset++;
                            operatorStackCount++;
                            state = STATE_OPERATOR_SIMPLEVALUE;
                            return SmartFrogTokenContext.OPERATOR;
                        case ')':
                            operatorStackCount--;
                            if (operatorStackCount ==0) {
                                state = STATE_AFTER_NAME;
                            } else {
                                state = STATE_OPERATOR_SIMPLEVALUE;
                            }
                            offset++;
                            return SmartFrogTokenContext.OPERATOR;
                        default:
                            state = STATE_OPERATOR_SIMPLEVALUE;
                    }
                    break;
                    
                case STATE_OPERATOR_BINARY:
                    switch (actChar) {
                        case '-':
                        case '/':
                        case '=':
                        case '>':
                        case '<':
                        case '+':
                        case '*':
                        case '&':
                        case '|':
                            offset++;
                            state = STATE_OPERATOR_SIMPLEVALUE;
                            return SmartFrogTokenContext.OPERATOR;
                    }
                    
                case STATE_OPERATOR_SIMPLEVALUE:
                    switch (actChar) {
                        case ' ':
                            break;
                        case '(':
                        case ')':
                            state = STATE_OPERATOR;
                            return SmartFrogTokenContext.ATTRIBUTE_VALUE;
                        case '-':
                        case '/':
                        case '=':
                        case '>':
                        case '<':
                        case '+':
                        case '*':
                        case '&':
                        case '|':
                            state = STATE_OPERATOR_BINARY;
                            return SmartFrogTokenContext.ATTRIBUTE_VALUE;
                        default:
                            break;
                    }
                    break;
                    
                case STATE_AFTER_NAME:
                    switch (actChar) {
                        case '[':
                            state = STATE_VECTOR;
                            offset++;
                            return SmartFrogTokenContext.KEYWORD;
                        case ':' :
                            offset++;
                            return SmartFrogTokenContext.ATTRIBUTE_VALUE;
                        case '(':
                            state = STATE_OPER;
                            return SmartFrogTokenContext.OPERATOR;
                        case '\"':
                            offset++;
                            state = this.STATE_IN_QUOTE;
                            return SmartFrogTokenContext.STRING;
                        case '#':
                            offset++;
                            actChar = buffer[offset];
                            if (offset < stopOffset) {
                                if (actChar == '#') {
                                    offset++;
                                    state = this.STATE_IN_POUND_QUOTE;
                                    return SmartFrogTokenContext.STRING;
                                }
                            } else {
                                return SmartFrogTokenContext.ATTRIBUTE_VALUE;
                            }
                        case '\n':
                            offset++;
                            state = this.STATE_INIT;
                            return SmartFrogTokenContext.END_OF_LINE;
                        case ';' :
                            state = this.STATE_INIT;
                            // don't increment offset so that it stays pointing at ';'
                            return SmartFrogTokenContext.ATTRIBUTE_VALUE;
                        case ' ' :
                            if (!firstQuote) {
                                if (partialToken.equals("IF")) {
                                    offset++;
                                    state = this.STATE_IF;
                                    partialToken = new String();
                                    return SmartFrogTokenContext.KEYWORD;
                                } else if (partialToken.equalsIgnoreCase("extends")) {
                                    state = this.STATE_AFTER_EXTENDS;
                                    partialToken = new String();
                                    return SmartFrogTokenContext.EXTENDS;
                                } else if (partialToken.equals("TBD")) {
                                    partialToken = new String();
                                    offset++;
                                    return SmartFrogTokenContext.TBD;
                                } else if (partialToken.equals("LAZY")) {
                                    partialToken = new String();
                                    offset++;
                                    return SmartFrogTokenContext.KEYWORD;
                                } else if (partialToken.equals("ATTRIB")) {
                                    partialToken = new String();
                                    offset++;
                                    return SmartFrogTokenContext.KEYWORD;
                                } else if (partialToken.equals("HERE")) {
                                    partialToken = new String();
                                    offset++;
                                    return SmartFrogTokenContext.KEYWORD;
                                } else if (partialToken.equals("THIS")) {
                                    partialToken = new String();
                                    offset++;
                                    return SmartFrogTokenContext.KEYWORD;
                                } else if (partialToken.equals("HOST")) {
                                    partialToken = new String();
                                    offset++;
                                    return SmartFrogTokenContext.KEYWORD;
                                } else if (partialToken.equals("PROCESS")) {
                                    partialToken = new String();
                                    offset++;
                                    return SmartFrogTokenContext.KEYWORD;
                                } else if (partialToken.equals("PROPERTY")) {
                                    partialToken = new String();
                                    offset++;
                                    return SmartFrogTokenContext.KEYWORD;
                                } else if (partialToken.equals("IPROPERTY")) {
                                    partialToken = new String();
                                    offset++;
                                    return SmartFrogTokenContext.KEYWORD;
                                } else {
                                    partialToken = new String();
                                    offset++;
                                    return SmartFrogTokenContext.ATTRIBUTE_VALUE;                                }
                            }
                        default:
                            partialToken += actChar;
                            if (partialToken.equals("TBD")) {
                                offset++;
                                partialToken = new String();
                                return SmartFrogTokenContext.TBD;
                            } else if ( (partialToken.equals("ROOT")) ||
                                    (partialToken.equals("PARENT")) ||
                                    (partialToken.equals("ATTRIB")) ||
                                    (partialToken.equals("HERE")) ||
                                    (partialToken.equals("THIS")) ||
                                    (partialToken.equals("PROPERTY")) ||
                                    (partialToken.equals("IPROPERTY")) ||
                                    (partialToken.equals("HOST")) ||
                                    (partialToken.equals("PROCESS")) ) {
                                partialToken = new String();
                                offset++;
                                return SmartFrogTokenContext.KEYWORD;
                            } else if ( partialToken.equals("extends")) {
                                partialToken = new String();
                                offset++;
                                state = this.STATE_AFTER_EXTENDS;
                                return SmartFrogTokenContext.EXTENDS;
                            } else if ( (offset+1 == stopOffset) && (lastBuffer) ) {
                                offset++;
                                return SmartFrogTokenContext.ATTRIBUTE_VALUE;
                            }
                    }
                    break;
                case STATE_IN_QUOTE:
                    switch (actChar) {
                        case '\n':
                        case '\"' :
                            offset++;
                            state = this.STATE_INIT;
                            return SmartFrogTokenContext.STRING;
                        default:
                            offset++;
                            return SmartFrogTokenContext.STRING;
                    }
                case STATE_IN_POUND_QUOTE:
                    switch (actChar) {
                        case '#' :
                            offset++;
                            state = this.STATE_INIT;
                            return SmartFrogTokenContext.STRING;
                        default:
                            offset++;
                            return SmartFrogTokenContext.STRING;
                    }
                case STATE_AFTER_EXTENDS:
                    switch (actChar) {
                        case ' ' :
                            offset++;
                            return SmartFrogTokenContext.BASE;
                        case '{' :
                        case ';' :
                            state = this.STATE_INIT;
                            // don't increment offset so that it stays pointing at ';'
                            if (partialToken.indexOf("NULL")>=0) {
                                return SmartFrogTokenContext.KEYWORD;
                            } else {
                                return SmartFrogTokenContext.BASE;
                            }
                        case ':' :
                            offset++;
                            return SmartFrogTokenContext.ATTRIBUTE_VALUE;
                        case '\n':
                            state = this.STATE_INIT;
                            break;
                        default:
                            partialToken += actChar;
                            if (partialToken.equals("LAZY")) {
                                partialToken = new String();
                                offset++;
                                return SmartFrogTokenContext.LAZY;
                            } else if ( (partialToken.equals("ROOT")) ||
                                    (partialToken.equals("PARENT")) ||
                                    (partialToken.equals("ATTRIB")) ||
                                    (partialToken.equals("HERE")) ||
                                    (partialToken.equals("THIS")) ||
                                    (partialToken.equals("PROPERTY")) ||
                                    (partialToken.equals("IPROPERTY")) ||
                                    (partialToken.equals("HOST")) ||
                                    (partialToken.equals("PROCESS")) ) {
                                partialToken = new String();
                                offset++;
                                return SmartFrogTokenContext.KEYWORD;
                            } else if ( (offset+1 == stopOffset) && (lastBuffer) ) {
                                offset++;
                                return SmartFrogTokenContext.BASE;
                            }
                    }
                    break;
                case STATE_IF:
                    switch (actChar) {
                        case ')':
                            offset++;
                            state = STATE_THEN;
                            partialToken = new String();
                            return SmartFrogTokenContext.ATTRIBUTE_VALUE;
                    }
                    break;
                case STATE_THEN:
                    partialToken += actChar;
                    if (partialToken.indexOf("THEN")>=0) {
                        offset++;
                        state = STATE_AFTER_THEN;
                        partialToken = new String();
                        return SmartFrogTokenContext.KEYWORD;
                    }
                    break;
                case STATE_AFTER_THEN:
                    switch (actChar) {
                        case ')':
                            offset++;
                            state = STATE_ELSE;
                            partialToken = new String();
                            return SmartFrogTokenContext.ATTRIBUTE_VALUE;
                    }
                    break;
                case STATE_ELSE:
                    partialToken += actChar;
                    if (partialToken.indexOf("ELSE")>=0) {
                        state = STATE_AFTER_ELSE;
                        offset++;
                        partialToken = new String();
                        return SmartFrogTokenContext.KEYWORD;
                    } else if (partialToken.indexOf("FI")>=0) {
                        state = STATE_INIT;
                        offset++;
                        partialToken = new String();
                        return SmartFrogTokenContext.KEYWORD;
                    }
                    break;
                case STATE_AFTER_ELSE:
                    switch (actChar) {
                        case ')':
                            offset++;
                            state = STATE_FI;
                            partialToken = new String();
                            return SmartFrogTokenContext.ATTRIBUTE_VALUE;
                    }
                    break;
                case STATE_FI:
                    switch (actChar) {
                        case ';':
                            if (partialToken.indexOf("FI")>=0) {
                                state = STATE_INIT;
                                return SmartFrogTokenContext.KEYWORD;
                            } else {
                                state=STATE_INIT;
                                partialToken = new String();
                                return SmartFrogTokenContext.ATTRIBUTE_VALUE;
                            }
                        default:
                            partialToken += actChar;
                            break;
                    }
                    break;
                case STATE_VECTOR:
                    switch (actChar) {
                        case '\"':
                            state = STATE_VECTOR_STRING;
                            break;
                        case ';':
                            state = STATE_INIT;
                            return SmartFrogTokenContext.ATTRIBUTE_VALUE;
                        case ']':
                            state = STATE_INIT;
                            offset++;
                            return SmartFrogTokenContext.KEYWORD;
                        case ',':
                            offset++;
                            return SmartFrogTokenContext.ATTRIBUTE_VALUE;
                    }
                    break;
                case STATE_VECTOR_STRING:
                    switch (actChar) {
                        case '\n':
                        case '\"':
                            offset++;
                            state = STATE_VECTOR;
                            return SmartFrogTokenContext.STRING;
                        default:
                            break;
                    }
            }
            offset++;
        }
        
        return null;
    }
    
}