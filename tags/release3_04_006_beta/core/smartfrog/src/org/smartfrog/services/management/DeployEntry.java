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

package org.smartfrog.services.management;

import java.util.Enumeration;

import org.smartfrog.services.trace.Entry;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;

//import org.smartfrog.sfcore.parser.*;

/**
 * DeployEntry class implements the interface Entry.
 * It is a tree entry proxy for SmartFrog (Prim) components.
 *
 * @see Entry
 */
public class DeployEntry implements Entry {
    //private String name=null;
    //private String parentDN=null;
    private Object entry = null;

    private boolean showRootProcessName = false;

    //Prim or Compound
    //   private HashMap attributes=null;
    //   private HashMap children=null;

    /**
     * Constructs the DeployEntry object
     *
     *@param  entry  object entry.
     */
    public DeployEntry(Object entry, boolean rootProcessPanel) {
        try {
            //         if (entry instanceof Prim){
            //            this.entry=(Prim)entry;
            //         } else if (entry instanceof Compound) {
            //            this.entry = (Compound) entry;
            //         }
            this.entry = (Object) entry;
            this.showRootProcessName= rootProcessPanel;

            //System.out.println("Entry created with "+this.toString());
        } catch (Exception ex) {
            System.out.println("sfManagementConsole (DeployEntry1): "+ex.toString());
            //ex.printStackTrace();
        }
    }

    /**
     *  Constructs the DeployEntry object
     */
    public DeployEntry() {
        try {
            this.entry = new PrimImpl();

            //System.out.println("Model created");
        } catch (Exception ex) {
            System.out.println("sfManagementConsole (DeployEntry2): "+ex.toString());

            //ex.printStackTrace();
        }
    }

    /**
     * Constructs for the DeployEntry object with the message
     *
     *@param  message  The message
     */
    public DeployEntry(String message) {
        try {
            this.entry = message;

            //System.out.println("Model created");
        } catch (Exception ex) {
            System.out.println("sfManagementConsole (DeployEntry3): "+ex.toString());

            //ex.printStackTrace();
        }
    }

    /**
     * main Method
     *
     *@param  args  command line arguments
     */
    public static void main(String[] args) {
        //Test
        System.out.println("Starting...a new adventure.");

        DeployEntry newEntry = new DeployEntry(SmartFrogCoreKeys.SF_ROOT);
        System.out.println("...Finished");
    }

    /**
     *  Checks if the DeployEntry is the leaf
     *
     *@return boolean  true if it is leaf entry else false
     */
    public boolean isLeaf() {
        if (entry instanceof Compound) {
            //System.out.println(this.toString()+": NO Leaf");
            return false;
        } else {
            //System.out.println(this.toString()+": Leaf");
            return true;
        }
    }

    /**
     *  Gets the root attribute of the DeployEntry object
     *
     *@return    The root value
     */
    public DeployEntry getRoot() {
        //System.out.println("getRoot():"+this.toString());
        //return "ROOT";
        // Needs to the the real ROOT of the system
        try {
            if (entry instanceof Compound) {
                return (new DeployEntry(((Compound) entry).sfResolveWithParser(SmartFrogCoreKeys.SF_ROOT), this.showRootProcessName));
            } else {
                return (new DeployEntry(((Prim) entry).sfResolveWithParser(SmartFrogCoreKeys.SF_ROOT),this.showRootProcessName));
            }

            //return entry;
        } catch (Exception ex) {
            //System.out.println(ex.toString());
            ex.printStackTrace();
        }

        return null;

        //Local root!
    }

    /**
     *  Gets the name attribute of the DeployEntry object
     *
     *@return    The name value
     */
    public String getName() {
        return  getRDNProcessCompound();
        //return getRDN();
    }

    /**
     *  Gets the rDN attribute of the DeployEntry object
     *
     *@return    The rDN value
     */
    public String getRDN() {
        //System.out.println("getRDN()"+getRDN(getDN()));
        return getRDN(getDN());
    }

    /**
     *  Gets the parentDN attribute of the DeployEntry object
     *
     *@return    The parentDN value
     */
    public String getParentDN() {
        //System.out.println("getParentDN()"+getParentDN(getDN()));
        return getParentDN(getDN());
    }

    /**
     *  Gets the dN attribute of the DeployEntry object
     *
     *@return    The dN value
     */
    public String getDN() {
        String name = "unknown";

        if (entry instanceof String) {
            return (String) entry;
        } else if (entry instanceof Prim) {
            try {
                name = ((Prim) entry).sfCompleteName().toString();
            } catch (java.rmi.NoSuchObjectException nex){
                //Ignore. component has terminated and RMI object has been
                //unexported
                //@TODO: Log
            } catch (Exception ex) {
                System.out.println("sfManagementConsole (DeployEntry4): "+ex.getMessage());
                //@TODO Log
            }
        }

//        if (!(name.equals(""))) {
//            name = "ROOT:" + name;
//        } else {
//            name = "ROOT";
//        }

        //System.out.println("getDN(): "+name);
        return (name);
    }

    //*****************************

    /**
     *Gets the Child at "index"
     *
     *@param  index  the index value
     *@return        The child value
     */
    public Object getChild(int index) {
        //System.out.println("getChildrenCount()");
        if (entry instanceof Compound) {
            //System.out.println("getChildCount():["+parent+"]"+"");
            try {
                return ((getChildren())[index][1]);
            } catch (Exception ex) {
                //System.out.println(ex.toString());
                ex.printStackTrace();
            }
        }

        return null;
    }

    /**
     *  To Get the number of children of the entry
     *
     *@return    The childrenCount value
     */
    public int getChildrenCount() {
        //System.out.println(this.toString()+".getChildrenCount()");
        if (entry instanceof Compound) {
            return sizeChildren();
        }

        return 0;
    }

    /**
     *  Gets the indexOfChild attribute of the DeployEntry object
     *
     *@param  child  Child object
     *@return        The index value of child
     */
    public int getIndexOfChild(Object child) {
        for (int i = 0; i < getChildrenCount(); i++) {
            if (getChild(i).equals(child)) {
                //System.out.println("#children:["+i+"]"+"--");
                return i;
            }
        }

        return -1;
    }

    /**
     *  Gets the attributes attribute of the DeployEntry object
     *
     *@return    The attributes value
     */
    public Object[][] getAttributes() {
        String[][] empty = {
            { "", "" }
        };

        try {
            if (!(entry instanceof Prim)) {
                return (empty);
            }

            Context context = ((Prim) entry).sfContext();
            String name = "";
            Object value = null;
            String solvedValue = null;
            Object[][] data = new Object[this.sizeAttributes()][2];
            int index = 0;
            Context c = ((Prim) entry).sfContext();

            for (Enumeration e = c.keys(); e.hasMoreElements();) {
                name="";
                value=null;
                solvedValue=null;
                name = e.nextElement().toString();
                value = c.get(name);

                if (isAttribute(value)) {
                    try {
                        //Special case to show special info about he reference
                        if (value instanceof Reference) {
                            String solvedValueClass="class not found";
                            try {
                              Object objSolvedValue = ( ( (Prim)entry).sfResolve( (Reference) value));
                              solvedValue=objSolvedValue.toString();
                              solvedValueClass = objSolvedValue.getClass().toString();
                            }catch (Throwable rex) {
                              solvedValue = " Failed to relsove!: "+rex.toString();
                            }
                            StringBuffer text = new StringBuffer();
                            text.append(value.toString());
                            text.append("\n * Value resolved: \n" + solvedValue);
                            text.append("\n" + "+ Solved Value class:" +solvedValueClass);
                            text.append("\n\n" + "+ Value class:" + value.getClass().toString());
                            //data[index][1] = obj.toString() + " ["+auxValue+"]";
                            data[index][1] = text.toString();
                        } else {
                            data[index][1] = value;
                        }
                    } catch (Exception ex) {
                      System.err.println("sfManagementConsole.deployEntry.getAttributes: error reading "+ name + " >"+ex.getMessage());
                    }
                    //&& !name.toString().endsWith("URL"))
                    data[index][0] = name;
                    index++;
                }
            }

            return data;
        } catch (java.rmi.NoSuchObjectException nso){
            //Ignore: tipically component terminated and unexported from rmi
            //@TODO: log
            return empty;
        } catch (Exception ex) {
            System.out.println("Error DeployEntry.getAttributes()" +
                ex.toString());

            //ex.printStackTrace();
            return empty;
        }
    }

    /**
     *  Gets the children attribute of the DeployEntry object
     *
     *@return    The children value
     */
    public Object[][] getChildren() {
        try {
            Context context = ((Prim) entry).sfContext();
            String name = "";
            Object obj = null;
            Object[][] data = new Object[this.sizeChildren()][2];
            Context c = ((Prim) entry).sfContext();
            int index = 0;

            for (Enumeration e = c.keys(); e.hasMoreElements();) {
                name = e.nextElement().toString();
                obj = c.get(name);

                if (!(isAttribute(obj))) {
                    //&& !name.toString().endsWith("URL"))
                    data[index][0] = name;

                    //data[index][1]=obj;
                    data[index][1] = obj2Entry(obj);

                    //Deploy entries: What about References?, ComponentDescriptions?
                    index++;
                }
            }

            return data;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     *  Search for a particular entry in its children tree.
     *
     *@return    The entry value
     */
    public Object getEntry() {
        return entry;

        // Prim or Compound
    }

    /**
     *Search for a particular entry in its children tree.
     *
     *@param  DN        Deploy Entry
     *@param  allLevel  boolean indicator for all level
     *@return           The entry value
     */
    public DeployEntry getEntry(String DN, boolean allLevel) {
        // Search 1 or all levels bellow this object
        throw new java.lang.UnsupportedOperationException(
            "Method getEntry() not yet implemented.");

        //return null;
    }

    // end Parsing msg

    /**
     * Not implemented.
     *@param  msg
     *@return boolean - Throws exception.
     */
    public boolean add(String msg) {
        throw new java.lang.UnsupportedOperationException(
            "Not implemented.  DeployEntry.add: " + "");

        //return false;
        //entry.add(node);
    }

    /**
     * Not implemented.
     *@param  DN
     *@param  value
     *@return boolean - Throws exception.
     */
    public boolean add(String DN, Object value) {
        throw new java.lang.UnsupportedOperationException(
            "Method add(DN, value) not completed yet");

        //return false;
    }

    /**
     * Not implemented.
     *
     *@param  DN
     *@return boolean - Throws exception!
     */
    public boolean remove(String DN) {
        throw new java.lang.UnsupportedOperationException(
            "Method remove(DN) not yet implemented.");

        //return false;
    }

    /**
     * Gets string representation of the deploy entry
     *
     *@return  string representation of the deploy entry
     */
    public String toString() {
        return this.getRDN();
    }

    /**
     * Returns a textual representation of the deploy entry
     *
     *@return    textual representation of the deploy entry
     */
    public String toStringAll() {
        //return parentDN+":"+name+"/"+childrenString()+"/"+attributesString();
        return entry.toString();

        //      StringBuffer txt = new StringBuffer(parentDN+":"+name);
        //      String aux = attributesString();
        //      if (!aux.equals("")){
        //         txt.append("\n   " +aux);
        //      }
        //      aux = childrenString();
        //      if (!aux.equals("")){
        //         txt.append("\n   " +aux);
        //      }
        //      return new String(txt);
    }

    /**
     * Returns children string
     *
     *@return  children string
     */
    public String childrenString() {
        //      if (children !=null){
        //         //return children.keySet().toString();
        //         return dumpHashMap(children);
        //      } else{
        return "";

        //      }
    }

    /**
     * Returns attributes string
     *
     *@return    attributes in sting form.
     */
    public String attributesString() {
        //      if (attributes!=null){
        //         //return (attributes.keySet().toString()+":"+attributes.values().toString());
        //         return ("  attributes: "+dumpHashMap(attributes));
        //
        //      } else{
        return "";

        //      }
    }

    /**
     * Returns the size of the attributes
     *
     *@return  Size of the attributes
     */
    public int sizeAttributes() {
        try {
            int counter = 0;
            String name = "";

            Context c = ((Prim) entry).sfContext();
            Object obj = null;

            for (Enumeration e = c.keys(); e.hasMoreElements();) {
                name = e.nextElement().toString();
                obj = c.get(name);

                if (isAttribute(obj)) {
                    //&& !name.toString().endsWith("URL"))
                    counter++;
                }
            }

            return counter;
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * Returns the size of children
     *
     *@return    size of children
     */
    public int sizeChildren() {
        try {
            int counter = 0;
            String name = "";
            Object obj = null;
            Context c = ((Prim) entry).sfContext();

            for (Enumeration e = c.keys(); e.hasMoreElements();) {
                name = e.nextElement().toString();
                obj = c.get(name);

                if (!(isAttribute(obj))) {
                    //&& !name.toString().endsWith("URL"))
                    counter++;
                }
            }

            return counter;
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * Displays the description of the DeployEntry
     *
     */
    public void info() {
        System.out.println("Info: " + this.toString());
        System.out.println("    - #Children:" + this.getChildrenCount());
        System.out.println("    - #Attributes:" + this.sizeAttributes());
        System.out.println("    - isLeaf():" + this.isLeaf());
        System.out.println("    - children:" + getChildren());
        System.out.println("    - attributes:" + getAttributes());
    }

    // Parse Name of Entry (SFObjects)

    /**
     *  Gets the rDN attribute of the DeployEntry object
     *
     *@param  DN  DeployEntry
     *@return   rdn attribute
     */
    private String getRDN(String DN) {
        //Special case when Entry is Registered in ProcessCompound.
        // Ex. Context context = SFProcess.getRootLocator()
        //    .getRootProcessCompound(InetAddress.getByName(
        //    hostname), port).sfContext();
//        String RDN =  DN.substring(DN.lastIndexOf(':') + 1, DN.length());
//        String nameInRPC = this.getRDNProcessCompound();
//        if (nameInRPC.equals("")){
//            return RDN;
//        } else {
//            return RDN+ "["+nameInRPC+"]";
//        }
          String RDN ="";
          if (this.showRootProcessName) {
             RDN = this.getRDNProcessCompound();
          }
          if (RDN.equals("")){
             return DN.substring(DN.lastIndexOf(':') + 1, DN.length());
          }
          return RDN;
    }

    /**
     *  Gets the registered Name in ProcessCompound for Entry object
     *
     *@return   rdn attribute
     */
    private String getRDNProcessCompound() {
        //Special case when Entry is Registered in ProcessCompound.
        try {
            Context context = SFProcess.getRootLocator()
            .getRootProcessCompound(((Prim)this.entry).sfDeployedHost()
                                    ).sfContext();
            if (context.contains(this.getEntry())) {
                return  (String)context.keyFor(this.getEntry());
            }
        } catch (Exception ex) {
            //@Todo log this.
            //ex.printStackTrace();
        }
        return "";
    }



    /**
     *  Gets the parentDN attribute of the DeployEntry object
     *
     *@param  DN  DeployEntry
     *@return     The parentDN value
     */
    private String getParentDN(String DN) {
        if (DN.lastIndexOf(':') > 0) {
            return DN.substring(0, DN.lastIndexOf(':'));
        } else {
            return null;
        }
    }

    /**
     *  Gets the msgChild4Parent attribute of the DeployEntry object
     *
     *@param  parentDN  parent DN
     *@param  fullDN    full DeployEntry
     *@return           The msgChild4Parent value
     */
    private String getMsgChild4Parent(String parentDN, String fullDN) {
        if (fullDN.equals(parentDN)) {
            return "";
        }

        if (fullDN.startsWith(parentDN)) {
            int index = fullDN.lastIndexOf(parentDN + ":") + parentDN.length() +
                1;
            int indexEnd = fullDN.indexOf(":", index);

            //System.out.println("    ****index 1 and 2:"+index+"/"+indexEnd);
            if (indexEnd > index) {
                return (fullDN.substring(index, indexEnd));
            } else {
                return fullDN.substring(index, fullDN.length());
            }
        }

        return "Node not contained";

        //return null
    }

    /**
     * Checks if the input object is an attribute
     *
     *@param  obj  inp object
     *@return      true if it is an attribute else false
     */
    private boolean isAttribute(Object obj) {
        if (!(obj instanceof Prim)) {
            //&& !name.toString().endsWith("URL"))
            return true;
        }

        return false;
    }

    /**
     *  Adds a feature to the Atribute attribute of the DeployEntry object
     *
     *@param  attrib  The feature to be added to the Atribute attribute
     *@param  value   The feature to be added to the Atribute attribute
     *@return         status of the operation
     */
    private boolean addAtribute(String attrib, String value) {
        throw new java.lang.UnsupportedOperationException(
            "Not implemented. DeployEntry.addAtributes: " + "");

        //return false;
    }

    /**
     *  Adds a feature to the Attributes attribute of the DeployEntry object
     *
     *@param  msg  The feature to be added to the Attributes attribute
     *@return       status of the operation
     */
    private boolean addAttributes(String msg) {
        throw new java.lang.UnsupportedOperationException(
            "Not implemented. DeployEntry.addAtribute: " + "");
        //return false;
    }


    /**
     *  Utility method to converts an object to DeployEntry
     *  Replacement method introduced: 12-2-02
     *
     *@param  value  inp object
     *@return        DeployEntry object
     */
    private DeployEntry obj2Entry(Object value) {
        try {
            if (value instanceof Prim) {
                return (new DeployEntry(value,this.showRootProcessName));
            } else if (value instanceof Reference) {
                do {
                    ((Reference) value).setEager(true);
                    value = ((Prim) entry).sfResolve((Reference) value);
                } while (value instanceof Reference);

                return (new DeployEntry(value,this.showRootProcessName));
            }
        } catch (Exception ex) {
            System.out.println("Error building mgt info: " + ex);

            //return new DeployEntry((ex.getMessage()+(value.toString())));
        }

        return new DeployEntry();
    }
}


//class
