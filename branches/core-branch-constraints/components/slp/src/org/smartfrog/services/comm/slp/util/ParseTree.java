/*
 Service Location Protocol - SmartFrog components.
 Copyright (C) 2004 Glenn Hisdal <ghisdal(a)c2i.net>
 
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
 
 This library was originally developed by Glenn Hisdal at the 
 European Organisation for Nuclear Research (CERN) in Spring 2004. 
 The work was part of a master thesis project for the Norwegian 
 University of Science and Technology (NTNU).
 
 For more information: http://home.c2i.net/ghisdal/slp.html 
 */

package org.smartfrog.services.comm.slp.util;

import org.smartfrog.services.comm.slp.ServiceLocationAttribute;

import java.util.Iterator;
import java.util.Vector;

/**
 * Implements a simple parse tree used for matching a predicate to a set of attributes. This is used by the SLPDatabase
 * to find entries matching a predicate. The class supports the following operators: and, or, equals, less, greater.
 */
public class ParseTree {
    private static final String operators = "&|=<>";
    private char op;
    private Vector expr;
    boolean hasStarted;

    /** Creates a new parse tree (node) */
    public ParseTree() {
        hasStarted = false;
        op = ' ';
        expr = new Vector();
    }

    /** Builds a parse tree from the given predicate string. */
    public int buildTree(String predicate) throws IllegalArgumentException {
        int currPos = 0;
        char currChar;
        hasStarted = false;
        boolean amParsing = true;
        String value = "";
        while (amParsing && currPos < predicate.length()) {
            currChar = predicate.charAt(currPos);
            switch (currChar) {
                case'(':
                    if (hasStarted) {
                        ParseTree newTree = new ParseTree();
                        currPos += newTree.buildTree(predicate.substring(currPos));
                        expr.add(newTree);
                    } else {
                        hasStarted = true;
                    }
                    break;
                case')':
                    if (hasStarted) {
                        if (!value.equals("")) expr.add(value);
                        amParsing = false;
                    } else {
                        throw new IllegalArgumentException("Parse Error");
                    }
                    break;
                case' ':
                case'\n':
                case'\t':
                    break; // skip whitespace
                default:
                    if (operators.indexOf(currChar) != -1) {
                        op = currChar;
                        if (op == '<' || op == '>') {
                            if (predicate.charAt(currPos + 1) == '=') currPos++;
                        }
                        if (!value.equals("")) {
                            expr.add(value);
                            value = "";
                        }
                    } else {
                        value = value + currChar;
                    }
                    break;
            }
            currPos++;
        }
        return currPos - 1;
    }

    /** Tries to find attributes that satisfies the predicate. */
    public boolean evaluate(Vector attributes) {
        switch (op) {
            case'=': {
                String id = expr.elementAt(0).toString();
                String v = expr.elementAt(1).toString();
                Iterator iter = attributes.iterator();
                while (iter.hasNext()) {
                    ServiceLocationAttribute a = (ServiceLocationAttribute) iter.next();
                    if (a.getId().equalsIgnoreCase(id)) {
                        for (Iterator it = a.getValues().iterator(); it.hasNext();) {
                            if (v.equalsIgnoreCase(it.next().toString())) {
                                return true;
                            }
                        }
                    }
                }
            }
            break;
            case'<': {
                String id = expr.elementAt(0).toString();
                String v = expr.elementAt(1).toString();
                Iterator iter = attributes.iterator();
                while (iter.hasNext()) {
                    ServiceLocationAttribute a = (ServiceLocationAttribute) iter.next();
                    if (a.getId().equalsIgnoreCase(id)) {
                        Iterator vIter = a.getValues().iterator();
                        while (vIter.hasNext()) {
                            if (vIter.next().toString().compareTo(v) >= 0) return true;
                        }
                    }
                }
            }
            break;
            case'>': {
                String id = expr.elementAt(0).toString();
                String v = expr.elementAt(1).toString();
                Iterator iter = attributes.iterator();
                while (iter.hasNext()) {
                    ServiceLocationAttribute a = (ServiceLocationAttribute) iter.next();
                    if (a.getId().equalsIgnoreCase(id)) {
                        Iterator vIter = a.getValues().iterator();
                        while (vIter.hasNext()) {
                            if (vIter.next().toString().compareTo(v) <= 0) return true;
                        }
                    }
                }
            }
            break;
            case'&': {
                Iterator iter = expr.iterator();
                while (iter.hasNext()) {
                    ParseTree t = (ParseTree) iter.next();
                    if (!t.evaluate(attributes)) return false;
                }
                return true;
            }
            //break;
            case'|': {
                Iterator iter = expr.iterator();
                while (iter.hasNext()) {
                    ParseTree t = (ParseTree) iter.next();
                    if (t.evaluate(attributes)) return true;
                }
            }
            break;
            default:
                break;
        }

        return false;
    }

    /** Prints the contents of the parse tree to stdout */
    public void print(String prefix) {
        System.out.println(prefix + op);
        Iterator iter = expr.iterator();
        Object o;
        while (iter.hasNext()) {
            o = iter.next();
            try {
                ParseTree t = (ParseTree) o;
                t.print(prefix + "\t");
            } catch (Exception e) {
                System.out.println(prefix + o.toString());
            }
        }
    }
}
