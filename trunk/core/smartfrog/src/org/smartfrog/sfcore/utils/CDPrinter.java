/** (C) Copyright Hewlett-Packard Development Company, LP
 
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
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * This class provides a single static method - print - which takes a ComponentDescription
 * and iterates over it to generate an output string. It can be used, for example, to generate XML text.
 *
 * It also contains a main method used for testing...
 */
public class CDPrinter {
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
     * @return  the string that is the result of printing
     */
    public static String print(ComponentDescription cd) {
        String nested = "";
        String CDPStart = "";
        String CDPEnd = "";
        String CDPSep = "";

        try {
            CDPStart = cd.sfResolve(new Reference(ReferencePart.here("CDPStart")), CDPStart, false);
        } catch (SmartFrogResolutionException e) {
            //shouldn't happen
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            CDPEnd = cd.sfResolve(new Reference(ReferencePart.here("CDPEnd")), CDPEnd, false);
        } catch (SmartFrogResolutionException e) {
            //shouldn't happen
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            CDPSep = cd.sfResolve(new Reference(ReferencePart.here("CDPSep")), CDPSep, false);
        } catch (SmartFrogResolutionException e) {
            //shouldn't happen
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        for (Iterator i = cd.sfAttributes(); i.hasNext(); ) {
            Object next = i.next();
            Object value = cd.sfContext().get(next);
            if (value instanceof ComponentDescription) {
                String resNext = print((ComponentDescription) value);
                if (!resNext.equals("") && !nested .equals("")) nested += CDPSep;
                nested += resNext;
            }
        }
        return CDPStart + nested + CDPEnd;
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
            Phases p = new SFParser().sfParse(new FileInputStream(url));
            // add params
            if (params != null) {
                for (Enumeration keys = params.keys(); keys.hasMoreElements(); ) {
                    Object k = keys.nextElement();
                    p.sfReplaceAttribute(k, params.get(k));
                }
            }
            p = p.sfResolvePhases();
            return print(p.sfAsComponentDescription());
    }

    /*
    public static void main(String [] args) {
        String url = args[0];
        try {
            Context testC = new ContextImpl();
            testC.put("test1", new Integer(100));
            testC.put("test2", new Integer(200));
            
            System.out.println("printing " + url);
            System.out.println(printURL(url, testC));
        } catch (SmartFrogException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    */
    
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
    */
}
