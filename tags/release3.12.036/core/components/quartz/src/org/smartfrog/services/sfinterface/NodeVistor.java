/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.sfinterface;

import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;


public class NodeVistor implements CDVisitor {

    Map attAndvalue = new HashMap();
    Vector attlist = null;
    Vector complist = null;
    String usertag = null;

    public NodeVistor() throws FileNotFoundException {
        attlist = new Vector();
        complist = new Vector();
    }

    public NodeVistor(String tagname) throws FileNotFoundException {

        usertag = tagname;
        attlist = new Vector();
        complist = new Vector();
    }

    /**
     * This method is called for every node in the tree
     * for each node visited its component name is added into the breakpoint vector
     */
    public void actOn(ComponentDescription node, Stack stk) throws Exception {
        String compName = node.sfCompleteName().toString();
        System.out.println("node.sfCompleteName  --------: " + compName);
        if (compName.length() > 0) {
            complist.addElement(compName.substring(compName.lastIndexOf(" ") + 1));
        }
        Iterator i = node.sfAttributes();
        Iterator j = node.sfValues();
        String attName = null;
        String attValue = null;
        while (i.hasNext()) {
            Object attObj = i.next();
            Object valueObj = j.next();
            if (compName.length() == 0) {
                // attName = attObj.toString();
                attName = "sfConfig".concat(":").concat(attObj.toString());
                attValue = valueObj.toString();
            } else {
                attName = compName.concat(":").concat(attObj.toString());
                attName = attName.substring(attName.lastIndexOf(" ") + 1);
                attName = "sfConfig".concat(":").concat(attName);
                attValue = valueObj.toString();
            }
            if (usertag != null) {
                Set tags = node.sfGetTags(attObj);
                if (tags != null) {
                    Iterator iter = tags.iterator();
                    while (iter.hasNext()) {
                        Object tag = iter.next();
                        if ((tag.toString()).equals(usertag) && !(attValue.startsWith("LAZY"))) {
                            attAndvalue.put(attName, attValue);
                            break;
                        }
                    }
                }
            } else {
                attAndvalue.put(attName, attValue);
            }
            for (int k = 0; k < complist.size(); k++) {

                String key = "sfConfig:" + complist.get(k);
                if (key.equals(attName)) {
                    attAndvalue.remove(attName);
                }
            }
        }
    }
}

