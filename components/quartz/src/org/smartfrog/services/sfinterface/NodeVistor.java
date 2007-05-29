package org.smartfrog.services.sfinterface;

import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.io.FileNotFoundException;
import java.util.*;


public class NodeVistor implements CDVisitor {

    Map attAndvalue = new HashMap();
    Vector attlist = null;
    Vector complist = null;

    public NodeVistor() throws FileNotFoundException {


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
        if (!compName.equals(""))
            complist.addElement(compName.substring(compName.lastIndexOf(" ") + 1));
        Iterator i = node.sfAttributes();
        Iterator j = node.sfValues();
        String attName = null;
        String attValue = null;
        while (i.hasNext()) {
            Object attObj = i.next();
            Object valueObj = j.next();
            if (compName.equals("")) {
               // attName = attObj.toString();
                attName = "sfConfig".concat(":").concat(attObj.toString());
                attValue = valueObj.toString();
            } else {
                attName = compName.concat(":").concat(attObj.toString());
                attName = attName.substring(attName.lastIndexOf(" ") + 1);
                attName= "sfConfig".concat(":").concat(attName);
                attValue = valueObj.toString();
            }
            attAndvalue.put(attName, attValue);
            for (int k = 0; k < complist.size(); k++) {

              String key="sfConfig:"+complist.get(k);
                if (key.equals(attName))
                {
                    attAndvalue.remove(attName);
                }


                /*attlist.addElement(attName);
                for (int k = 0; k < complist.size(); k++) {
                    if ((complist.get(k)).equals(attName))
                        attlist.removeElement(attName); */
            }
        }
    }
}

