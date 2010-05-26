/** (C) Copyright 1996-2009 Hewlett-Packard Development Company, LP
 
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

package org.smartfrog.sfcore.utils;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class provides a single static method - print - which takes a ComponentDescription
 * and iterates over it to generate an output string. It can be used, for example, to generate XML text.
 *
 * It also contains a main method used for testing...
 */
public class CDPrinter {
    
    private static LogSF log = LogFactory.getLog(CDPrinter.class);
    
    /**
     * The method looks for three optional attributes: CDPStart, CDPEnd, CPDSep and returns
     * the following print string
     * <p>
     * CDPStart + print(first child) + CPDSep + ... + CDPSep + print(last child) + CDPEnd
     * <p>
     * If the attributes are not present, they are taken to be empty strings. The attributes
     * may be arbitrarly complex derivatives of the other attributes in the component description
     * assuming that full resolution will be carried out before printing.
     * <p>
     * Normally, the CDPStart, CDPEnd and CDPSep attributes would be defined in the templaets being
     * extended and not visible directly to the end users of the templates. So for example one might have:
     * <p>
     * Network extends {<br>
     *     CDPStart "<network subnet=\"" + subnet + "\"/> "<br>
     *     subnet TBD;<br>
     * }<br>
     * <p>
     * and this would be inherited through normal mechansims.
     * <p>
     * Note that comopnent descriptions that contain no CDP attributes does not stop the printer from
     * printing its children.
     *
     * @param cd  the component description to print
     * @param indent amount to indent every level
     * @param incr indent increment
     * @param indents records indent strings to avoid needing to remake them each time
     * @return  the string that is the result of printing
     */
    public static String print(ComponentDescription cd, int indent, int incr, HashMap<Integer, String> indents) {
        String nested = "";
        String CDPStart = "";
        String CDPEnd = "";
        String CDPSep = "";
        String indentString = "";

        if (indents!=null){
            indentString = indents.get(indent);
            if (indentString==null){
                StringBuilder sb= new StringBuilder();
                for (int i=0; i<indent; i++){
                    sb.append(" ");
                }
                indentString = sb.toString();
                indents.put(indent, indentString);
            }
        }


        try {
            CDPStart = cd.sfResolve(new Reference(ReferencePart.here("CDPStart")), CDPStart, false);
            log.debug("CDPStart"+ CDPStart);
        } catch (SmartFrogResolutionException e) {
            //shouldn't happen
            log.error(e);
        }

        try {
            CDPEnd = cd.sfResolve(new Reference(ReferencePart.here("CDPEnd")), CDPEnd, false);
            log.debug("CDPEnd" + CDPEnd);
        } catch (SmartFrogResolutionException e) {
            log.error(e);
        }
        try {
            CDPSep = cd.sfResolve(new Reference(ReferencePart.here("CDPSep")), CDPSep, false);
            log.debug("CDPSep" + CDPSep);
        } catch (SmartFrogResolutionException e) {
            //shouldn't happen
            log.error(e);
        }

        for (Iterator i = cd.sfAttributes(); i.hasNext(); ) {
            Object next = i.next();
            Object value = cd.sfContext().get(next);
            if (value instanceof ComponentDescription) {
                if (((ComponentDescription)value).sfContext().get("sfCDPrinterIgnore")!=null) {
                    log.debug("print(): " + next + " ignored");
                    continue; //round for...
                }
                String resNext = print((ComponentDescription) value, indent+incr, incr, indents);
                if (!resNext.isEmpty() && !nested.isEmpty()) {
                    nested += CDPSep;
                }
                nested += resNext;
            }
        }
        
        return indentString + CDPStart + nested + (nested.equals("") ? "" : indentString) + CDPEnd;
    }

    /**
     * See comments for print(cd, indent, incr, indents)
     * @param cd  the component description to print
     * @return the string that is the result of printing
     */
    public static String print(ComponentDescription cd) {
        return print(cd, 0, 0, null);
    }

    /**
     * See comments for print(cd, indent, incr, indents)
     * @param cd  the component description to print
     * @param incr indent increment
     * @return the string that is the result of printing
     */
    public static String print(ComponentDescription cd, int incr) {
        return print(cd, 0, incr, new HashMap<Integer, String>());
    }



    /**
     * Method to take a URL, parse it, add the addtional key-value parameters to the top level, resolve and then create the
     * print string on the resultant description.
     * @param url the file to parse
     * @param params a context containing the parameter key-value pairs
     * @return the resultant print string
     * @throws SmartFrogException
     * @throws FileNotFoundException
     */
    public static String printURL(String url, Context params) throws SmartFrogException, FileNotFoundException {
        ComponentDescription cd = SFComponentDescriptionImpl.getDescriptionURL(url, params);
        return print(cd);
    }
    
    /**
     * The main method takes a URL, parses it, resolves the structure and then displays the
     * print string on the resultant description of sfConfig. Used for debugging descriptions.
     * @param args any arguments
     */
    public static void main(String [] args) {
        String url = args[0];
        try {
            Context testC = new ContextImpl();            
            System.out.println("printing " + url);
            System.out.println(printURL(url, testC));
        } catch (SmartFrogException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /* example use
    #include "org/smartfrog/functions.sf"

    System extends {
      CDPStart ("<sys data=\"" ++ sysval ++ "\">");
      CDPEnd "</sys>";
      sysval TBD;
    }

    comp extends {
       CDPStart  ("<nic data=\"" ++ compData ++ "\"/>");
       compData TBD;
    }


    sfConfig extends System {
        c1 extends comp { compData 5; }
        c2 extends comp { compData 10; };
        sysval 100;
    }


    Save in a file, say test.sf, then use using the following command line
    java org.smartfrog.sfcore.utils.CDPrinter test.sf
    */
}
