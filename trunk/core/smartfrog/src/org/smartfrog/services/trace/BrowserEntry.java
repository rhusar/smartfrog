/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.trace;


import java.util.HashMap;
import java.util.Iterator;

import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;


/**
 *  BrowserEntry represents a node in a BrowseTreeModel.
 *
 */
public final class BrowserEntry implements Entry {



    //public static String CRLF = "\r\n";
    /** String for name. */
    private String name = null;
    /** String for parent DN. */
    private String parentDN = null;
    /** HashMap for attributes. */
    private HashMap attributes = null;
    /** HashMap for children. */
    private HashMap children = null;

    /**
     *  Constructor for the BrowserEntry object.
     *
     * @param  DN dN attribute of BrowserEntry object
     */
    public BrowserEntry(String DN) {
        this.name = this.getRDN(DN);
        this.parentDN = this.getParentDN(DN);

        //System.out.println("**CREATED: "+ name +", in: "+DN);
    }

    /**
     *  Constructor for the BrowserEntry object.
     */
    public BrowserEntry() {
        new BrowserEntry("ROOT[]>");
    }

    /**
     * Adds a new child.
     *
     * @param  msg message
     */
    public void add(String msg) {
        //msg format: system:demoA, DEPLOYED, 15:51:37.187 22/06/01,
    //guijarro-j-5/15.144.25.153
        //add children
        //parseMsg(msg);
        //String msgDN = getMsgDN(msg);
        // check if DN is this component, if it is not looks for a clidren
    // and pass msg
        // if children doesn't exist, create it.
        // Only create 1 level in the hierarchy!
        //System.out.println("adding in: "+this.getDN()+"/"+msg);
        // ROOT=ROOT
        if (this.getDN().equals(this.getMsgDN(msg))) {
            //this is the node referenced
            //System.out.println("   Node adding attributes to itself: "+
        //this.getDN());
            addAtribute(this.getMsgAction(msg), this.getMsgLocation(msg));
        } else {
            //check if this child exist
            //ROOT=ROOT:(System)
            //System.out.println(this.getDN()+"=?"+this.getParentDN(
        //this.getMsgDN(msg)));
            if (this.getDN().equals(this.getParentDN(this.getMsgDN(msg)))) {
                // The node referenced is a children of this node
                try {
                    if (children == null) {
                        this.children = new HashMap();

                        //Create new Child (as children were null)
                        BrowserEntry newChild = new BrowserEntry(this.
                    getMsgDN(msg));
                        newChild.add(msg);
                        children.put(newChild.getName(), newChild);

                        //System.out.println("  Created Node (and Children): "+
            //this.getDN());
                    } else {
                        String nameChild = this.getRDN(this.getMsgDN(msg));

                        // Check if already exist, if not create it
                        //Only first level!!! So last position in MsgDN should
            //be Name.
                        try {
                            if (children.containsKey(nameChild)) {
                                // Already exist so we add attributes to it.
                                BrowserEntry newChild = (BrowserEntry)children.
                    get(nameChild);

                                if (newChild != null) {
                                    newChild.add(msg);
                                    children.put(newChild.getName(), newChild);

                                    //System.out.println("  Added attribs to
                    //existing node: "+this.getDN());
                                    // To add more than one level... if it
                    // doesn't exist, create...
                                }
                            } else {
                                //Child didn't exist so, it is created and
                //added attribs
                                BrowserEntry newChild = new BrowserEntry(this.
                        getMsgDN(msg));
                                newChild.add(msg);
                                children.put(newChild.getName(), newChild);

                                //System.out.println("  Created Node and
                //attribs: "+this.getDN());
                            }
                        } catch (Exception ex) {
                            if (sfLog().isErrorEnabled()) sfLog().error("Child " + nameChild +" not contained in " + this.getDN(),ex);
                        }
                    }
                } catch (Exception ex) {
                    if (sfLog().isErrorEnabled()) sfLog().error("Failed in adding new child:" + getDN(),ex);
                }
            } else {
                // if it is for one or our children we pass the ball
                //ROOT=ROOT:System:(foo)
                if (this.getMsgDN(msg).startsWith(this.getDN())) {
                    // Look for child and pass the hot potato
                    String nameChild = getMsgChild4Parent(this.getDN(),
                            getMsgDN(msg));

                    //System
                    //System.out.println("hot patato to(namechild): "+
            //nameChild);
                    try {
                        if (children == null) {
                            this.children = new HashMap();
                        }

                        if (children.containsKey((Object) nameChild)) {
                            BrowserEntry newChild = (BrowserEntry) children.
                    get(nameChild);

                            if (newChild != null) {
                                newChild.add(msg);
                                children.put(newChild.getName(), newChild);

                                //System.out.println("  Passed hot potato to
                //grandchild...: "+newChild.getDN());
                            }
                        } else {
                            // And for entries not ordered!
                            // Example you receive ROOT:system:foo before ROOT:
                // system
                            BrowserEntry newChild = new BrowserEntry(this.
                        getDN() +":" + nameChild);

                            if (newChild != null) {
                                newChild.add(msg);
                                children.put(newChild.getName(), newChild);
                            }

                            // ----
                        }
                    } catch (Exception ex) {
                        if (sfLog().isErrorEnabled()) sfLog().error("xChild " + nameChild +" not contained in " + this.getDN(),ex);
                    }
                } else {
                    if (sfLog().isErrorEnabled()) sfLog().error("ERROR: Trying to add: \n" + getMsgDN(msg) + " to \n" + this.getDN());
                }
            }
        }
    }

    /**
     *  Adds a feature to the Atribute attribute of the BrowserEntry object.
     *
     * @param  attrib  The feature to be added to the Atribute attribute
     * @param  value   The feature to be added to the Atribute attribute
     */
    private void addAtribute(String attrib, String value) {
        if (attributes == null) {
            attributes = new HashMap();

            // Add attribute and value contained in the message;
        }

        attributes.put(attrib, value);
    }

    // Parsing msg
    ////msg format: system:demoA, DEPLOYED, 15:51:37.187 22/06/01
    //guijarro-j-5/15.144.25.153

    /**
     *  Parsing message.
     *
     *@param  msg  message to be parsed
     */
    private void parseMsg(String msg) {
        // To analyse the component that you need to create!
        System.out.println("   -- New component --");
        System.out.println("msgDN:" + this.getMsgDN(msg));
        System.out.println("    action:" + this.getMsgAction(msg));
        System.out.println("    location:" + this.getMsgLocation(msg));
        System.out.println("    RDN:" + this.getRDN(this.getMsgDN(msg)));
        System.out.println("    ParentDN:" +
            this.getParentDN(this.getMsgDN(msg)));
        System.out.println("    getMsgChild4Parent(System): " +
            getMsgChild4Parent(SmartFrogCoreKeys.SF_ROOT, getMsgDN(msg)));
        System.out.println("    getMsgChild4Parent(System): " +
            getMsgChild4Parent("ROOT:System", getMsgDN(msg)));
        System.out.println("    getMsgChild4Parent(System:foo): " +
            getMsgChild4Parent("ROOT:System:foo", getMsgDN(msg)));

        // -- end test block
    }

    /**
     *  Gets the msgDN attribute of the BrowserEntry object
     *
     * @param  msg message
     * @return      The msgDN value
     */
    private String getMsgDN(String msg) {
        String msgDN = msg.substring(0, msg.indexOf(','));

        return msgDN;
    }

    /**
     *  Gets the msgAction attribute of the BrowserEntry object
     *
     *@param  msg  message
     *@return      The msgAction value
     */
    private String getMsgAction(String msg) {
        String msgAction = msg.substring(msg.indexOf(',') + 1, msg.length());
        msgAction = msgAction.substring(0, msgAction.indexOf(','));

        return msgAction;
    }

    /**
     *  Gets the msgLocation attribute of the BrowserEntry object
     *
     *@param  msg  message
     *@return      The msgLocation value
     */
    private String getMsgLocation(String msg) {
        String msgLocation = msg.substring(msg.lastIndexOf('[') ,
                msg.length());

        return msgLocation;
    }

    /**
     *  Gets the rDN attribute of the BrowserEntry object
     *
     *@param  DN  dN attribute
     *@return     The rDN value
     */
    private String getRDN(String DN) {
        return DN.substring(DN.lastIndexOf(':') + 1, DN.length());
    }

    /**
     *  Gets the parentDN attribute of the BrowserEntry object
     *
     * @param  DN  dN attribute
     * @return     The parentDN value
     */
    private String getParentDN(String DN) {
        if (DN.lastIndexOf(':') > 0) {
            return DN.substring(0, DN.lastIndexOf(':'));
        } else {
            return null;
        }
    }

    /**
     *  Gets the msgChild4Parent attribute of the BrowserEntry object
     *
     *@param  parent  parentdN attribute
     *@param  fullDN    fulldN attribute
     *@return           The msgChild4Parent value
     */
    private String getMsgChild4Parent(String parent, String fullDN) {
        if (fullDN.equals(parent)) {
            return "";
        }

        if (fullDN.startsWith(parent)) {
            int index = fullDN.lastIndexOf(parent + ":") + parent.length() +
                1;
            int indexEnd = fullDN.indexOf(":", index);

            if (indexEnd > index) {
                return (fullDN.substring(index, indexEnd));
            } else {
                return fullDN.substring(index, fullDN.length());
            }
        }

        return "Node not contained";

        //return null
    }

    // end Parsing msg

    /**
     *  Adds a feature to the Attributes attribute of the BrowserEntry object
     *
     *@param  msg  The feature to be added to the Attributes attribute
     *@return      if added true else false
     */
    private boolean addAttributes(String msg) {
        return false;
    }

    /**
     * Adds a new child.
     *
     * @param  DN     dN attribute
     * @param  value  value
     * @return        if added true else false
     */
    public boolean add(String DN, Object value) {
        // Is this the right node?
        throw new java.lang.UnsupportedOperationException(
            "Method add(DN, value) not completed yet");

        // add in present node
        //      if (value instanceof BrowserEntry) {
        //         //add children
        //         if (children == null) {
        //            this.children = new HashMap();
        //         }
        //         //@todo complete
        //      } else {
        //         //add attribute
        //         if (attributes == null) {
        //            this.attributes = new HashMap();
        //         }
        //        //@todo complete
        //      }
        //      return false;
    }

    /**
     *  Removes a child.
     *
     * @param  DN  dN attribute
     * @return     if removed true else false
     */
    public boolean remove(String DN) {
        throw new java.lang.UnsupportedOperationException(
            "Method remove(DN) not yet implemented.");

        //return false;
    }

    /**
     *  Gets the leaf attribute of the BrowserEntry object.
     *
     * @return    The leaf value
     */
    public boolean isLeaf() {
        //if (children !=null) System.out.println("isLeaf() ["+this.getDN()+"]"
    //+children.size());
        if ((children != null) && (children.size() > 0)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Gets the rDN attribute of the BrowserEntry object.
     *
     * @return    rDN attribute
     */
    public String toString() {
        return this.getRDN();
    }

    /**
     *  Return a textual representation of the File object.
     *
     * @return   textual representation of the File object
     */
    public String toStringAll() {
        //return parentDN+":"+name+"/"+childrenString()+"/"+attributesString();
        StringBuffer txt = new StringBuffer(parentDN + ":" + name);
        String aux = attributesString();

        if (!aux.equals("")) {
            txt.append("\n   " + aux);
        }

        aux = childrenString();

        if (!aux.equals("")) {
            txt.append("\n   " + aux);
        }

        return new String(txt);
    }

    /**
     *  Returns the childern string.
     *
     * @return    children string
     */
    public String childrenString() {
        if (children != null) {
            //return children.keySet().toString();
            return dumpHashMap(children);
        } else {
            return "";
        }
    }

    /**
     *  Returns the attributes string.
     *
     * @return    attributes string
     */
    public String attributesString() {
        if (attributes != null) {
            //return (attributes.keySet().toString()+":"+attributes.values().
        //toString());
            return ("  attributes: " + dumpHashMap(attributes));
        } else {
            return "";
        }
    }

    /**
     *  Dumps the hash map.
     *
     * @param  hashmap  hashmap
     * @return         textual representation of hash map
     */
    public static String dumpHashMap(HashMap hashmap) {
        StringBuffer desc = new StringBuffer("");

        for (Iterator i = (hashmap.keySet()).iterator(); i.hasNext();) {
            Object key = i.next();
            Object obj = hashmap.get(key);

            if (obj instanceof BrowserEntry) {
                desc.append("\n     \"" + key + "\" --> \"" +
                    ((BrowserEntry) obj).toStringAll() + "\"");
            } else {
                desc.append("\n     \"" + key + "\" --> \"" + obj + "\"");
            }
        }

        return new String(desc);
    }

     /**
     *  Dumps the hash map to array.
     *
     * @param  hashmap  hashmap
     * @return         array of hash map
     */
    public static Object[][] dumpHashMap2Array(HashMap hashmap) {
        Object[][] data = new Object[hashmap.size()][2];
        int i = 0;

        for (Iterator iterat = (hashmap.keySet()).iterator(); iterat.hasNext();) {
            Object key = iterat.next();
            data[i][0] = key;

            // Attribute
            data[i][1] = hashmap.get(key);

            // Value
            i++;
        }

        return data;
    }

    /**
     *  Gets the root attribute of the BrowserEntry object.
     *
     * @return    The root value
     */
    public String getRoot() {
        if ((parentDN == null)) {
            return name;
        } else {
            try {
                return parentDN.substring(0, ((String) parentDN).indexOf(':'));
            } catch (Exception ex) {
                return parentDN;
            }
        }
    }

    /**
     *  Gets the name attribute of the BrowserEntry object.
     *
     * @return    The name value
     */
    public String getName() {
        return name;
    }

    /**
     *  Gets the rDN attribute of the BrowserEntry object.
     *
     * @return    The rDN value
     */
    public String getRDN() {
        return name;
    }

    /**
     *  Gets the parentDN attribute of the BrowserEntry object.
     *
     * @return    The parentDN value
     */
    public String getParentDN() {
        return parentDN;
    }

    /**
     *  Gets the dN attribute of the BrowserEntry object.
     *
     * @return    The dN value
     */
    public String getDN() {
        //System.out.println("name: "+ name + ", parentDN "+parentDN);
        String DN = null;

        if ((parentDN == null)) {
            DN = name;
        } else {
            if (parentDN.startsWith("ROOT[") && parentDN.endsWith("]>")) {
        // Special case for the SFTRACE added tag: ROOT[port]>
                DN = (parentDN + "" + name);
            } else {
                DN = parentDN + ":" + name;
            }
        }

        //System.out.println(" getDN() "+DN);
        return DN;
    }

    /**
     *  To Get the children of the entry.
     *
     * @return    The children value
     */
    public HashMap getChildren() {
        return children;
    }

    /**
     *  Gets the child attribute of the BrowserEntry object.
     *
     * @param  index  index for searching
     *@return        The child value
     */
    public BrowserEntry getChild(int index) {
        // index starts with 0!
        index++;

        if (index == 0) {
            return null;
        }

        int count = 1;
        int limit = children.keySet().size();

        if ((children != null) && (limit >= index)) {
            Iterator i = (children.keySet()).iterator();

            while (i.hasNext()) {
                Object key = i.next();

                //System.out.println("Key/entry: "+key+"/"+this.getRDN()+"/"+
        //count+"/"+index);
                if (count == index) {
                    return (BrowserEntry) children.get(key);
                }

                count++;
            }

            return null;
        }

        return null;
    }

    /**
     *  To Get the number of children of the entry.
     *
     * @return    The childrenCount value
     */
    public int getChildrenCount() {
        if (children != null) {
            return children.size();
        } else {
            return 0;
        }
    }

    /**
     *  Gets the attributes attribute of the BrowserEntry object.
     *
     * @return    The attributes value
     */
    public HashMap getAttributes() {
        return this.attributes;
    }

    /**
     *  Gets the attributesArray attribute of the BrowserEntry object.
     *
     * @return    The attributesArray value
     */
    public Object[][] getAttributesArray() {
        if (this.attributes == null) {
            Object[][] data = null; //{{""},{""}};

            return (data);
        }

        return dumpHashMap2Array(this.attributes);
    }

    /**
     *  Search for a particular entry in its children tree.
     *
     *@param  DN        dN attribute
     *@param  allLevel  bollean indicating to search 1 or all levels
     *@return           The entry value
     */
    public BrowserEntry getEntry(String DN, boolean allLevel) {
        // Search 1 or all levels bellow this object
        throw new java.lang.UnsupportedOperationException(
            "Method remove(DN) not yet implemented.");

        //return null;
    }

//    /**
//     *  The main method.
//     *
//     * @param  args  cpmmand line arguments
//     */
//    public static void main(String[] args) {
//        //Test
//        System.out.println("Starting...a new adventure.");
//
//        BrowserEntry entry = new BrowserEntry(SmartFrogCoreKeys.SF_ROOT);
//        System.out.println(
//            "Adding: ROOT:System -------------------------------------------");
//        entry.add(
//            "ROOT:System, DEPLOYED, 15:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        System.out.println(
//            "Adding: ROOT:System:foo:bar2 -----------------------------------");
//        entry.add(
//            "ROOT:System:foo:bar2,DEPLOYED,15:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        System.out.println(
//            "Adding: ROOT:System:foo ----------------------------------------");
//        entry.add(
//            "ROOT:System:foo, DEPLOYED, 15:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        System.out.println(
//            "Adding: ROOT:System:foo:bar ------------------------------------");
//        entry.add(
//            "ROOT:System:foo:bar,DEPLOYED,15:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        System.out.println(
//            "Adding: ROOT:baz and bar---------------------------------");
//        entry.add(
//            "ROOT:baz, STARTED,15:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        entry.add(
//            "ROOT:bar, STARTED,15:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        entry.add(
//            "ROOT:System:foo:bar, STARTED,15:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        entry.add(
//            "ROOT, DEPLOYED,00:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        entry.add(
//            "ROOT, STARTED,00:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        System.out.println("Node root: " + entry.toStringAll());
//        System.out.println("Entry[1]:" + entry.getChild(1));
//        System.out.println("Entry[2]:" + entry.getChild(2));
//        System.out.println("Entry[0]:" + entry.getChild(0));
//        System.out.println("Entry[3]:" + entry.getChild(3));
//        System.out.println("...Finished");
//    }

   /** Log for this class, created using class name*/
    static LogSF sfLog = LogFactory.getLog(BrowserEntry.class);

    /**
     * Log for this class
      * @return
     */
   private LogSF sfLog(){
        return sfLog;
   }
}


//class
