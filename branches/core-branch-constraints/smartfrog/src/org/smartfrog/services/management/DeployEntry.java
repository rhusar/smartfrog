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
import java.util.Set;
import java.util.HashSet;

import org.smartfrog.services.trace.Entry;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.rmi.RemoteException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;


/**
 * DeployEntry class implements the interface Entry.
 * It is a tree entry proxy for SmartFrog (Prim) components.
 *
 * @see Entry
 */
public class DeployEntry implements Entry {

    /** Log for this class, created using class name*/
    LogSF sfLog = LogFactory.getLog(DeployEntry.class);

    boolean showCDasChild = true;

    private Object entry = null;

    private boolean showRootProcessName = false;

    private boolean isCopy = false;

    /**
     * Constructs the DeployEntry object
     *
     * @param  entry  object entry.
     * @param showRootProcessName flag indicating to show rootprocess name
     * @param showCDasChild flag indicating to show CD as child
     */
//    public DeployEntry(Object entry, boolean showRootProcessName, boolean showCDasChild) {
//        this (entry,false,showRootProcessName, showCDasChild);
//    }

    /**
     * Constructs the DeployEntry object
     *
     * @param  entry  object entry.
     * @param  isCopy is entry a copy of the original object (normally when a CD is accessed remotely)
     * @param showRootProcessName flag indicating to show rootprocess name
     * @param showCDasChild flag indicating to show CD as child
     */
    public DeployEntry(Object entry,boolean isCopy, boolean showRootProcessName, boolean showCDasChild) {
        try {
            this.entry = (Object) entry;
            this.isCopy = isCopy;
            this.showRootProcessName = showRootProcessName;
            this.showCDasChild=showCDasChild;
            initLog();
        } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error("sfManagementConsole (DeployEntry1): "+ex.toString(),ex);
        }
    }

    /**
     *  Constructs the DeployEntry object
     */
    public DeployEntry() {
        try {
            this.entry = new PrimImpl();
            initLog();
            //System.out.println("Model created");
        } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error("sfManagementConsole (DeployEntry2): "+ex.toString(),ex);
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
            initLog();
        } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error("sfManagementConsole (DeployEntry3): "+ex.toString(),ex);
        }
    }

//    /**
//     * main Method
//     *
//     *@param  args  command line arguments
//     */
//    public static void main(String[] args) {
//        //Test
//        System.out.println("Starting...a new adventure.");
//
//        DeployEntry newEntry = new DeployEntry(SmartFrogCoreKeys.SF_ROOT);
//        System.out.println("...Finished");
//    }

    /**
     *  Checks if the DeployEntry is the leaf
     *
     *@return boolean  true if it is leaf entry else false
     */
    public boolean isLeaf() {
        if ((entry instanceof Compound)
            ||(showCDasChild && (entry instanceof Prim))
            ||(showCDasChild && (entry instanceof ComponentDescription))) {
            //System.out.println(this.toString()+": NO Leaf");
            return false;
        } else {
            //System.out.println(this.toString()+": Leaf");
            return true;
        }
    }

    /**
     *  An entry will be a copy when access to a ComponentDescription remotely because CD does not have a remote interface
     * @return is copy
     */
    public boolean isCopy(){
        return isCopy;
    }

    /**
     *  Gets the root attribute of the DeployEntry object
     *
     *@return    The root value
     */
    public DeployEntry getRoot() {
        //sfLog().info("getRoot():"+this.toString());
        // Needs to the the real ROOT of the system
        try {
            if ((entry instanceof ProcessCompound)&& this.showRootProcessName){
                return this;
            }
            if (entry instanceof Prim) {
               return (new DeployEntry(((Prim) entry).sfResolveWithParser(SmartFrogCoreKeys.SF_ROOT),false,this.showRootProcessName,this.showCDasChild));
            }
            //return entry;
        } catch (Exception ex) {
            //System.out.println(ex.toString());
            if (sfLog().isErrorEnabled()) sfLog().error(ex);
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
        return getRDN(getDNReference());
    }

    /**
     *  Gets the parentDN attribute of the DeployEntry object
     *
     *@return    The parentDN value
     */
    public String getParentDN() {
        //System.out.println("getParentDN()"+getParentDN(getDN()));
        return getParentDN(getDNReference());
    }

    /**
     *  Gets the dN attribute of the DeployEntry object
     *
     *@return    The dN value
     */
    public Reference getDNReference() {
        Reference ref = new Reference();
        ref.addElement(new HereReferencePart("unknown"));;

        if (entry instanceof String) {
            ref = new Reference();
            ref.addElement(new HereReferencePart(entry));;
            return ref;
        } else if (entry instanceof Prim) {
            //System.out.println("EntryPrim: getting name");
            try {
                ref = ((Prim) entry).sfCompleteName();
                //System.out.println("EntryPrim: getting name - "+name);
            } catch (java.rmi.NoSuchObjectException nex){
                //Ignore. component has terminated and RMI object has been
                //unexported
                if (sfLog().isIgnoreEnabled()) sfLog().ignore(nex);
            } catch (Exception ex) {
                if (sfLog().isErrorEnabled()) sfLog().error(ex);
            }
        } else if (entry instanceof ComponentDescription) {
            //System.out.println("EntryCD: getting name");
            try {
                ref = ((ComponentDescription) entry).sfCompleteName();
            //    System.out.println("EntryCD: getting name - "+name);
            } catch (Exception ex) {
                if (sfLog().isErrorEnabled()) sfLog().error(ex);
            }
        }
        //System.out.println("getDN(): "+name);
        return (ref);
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
            //System.out.println("EntryPrim: getting name");
            try {
                name = ((Prim) entry).sfCompleteName().toString();
                //System.out.println("EntryPrim: getting name - "+name);
            } catch (java.rmi.NoSuchObjectException nex){
                //Ignore. component has terminated and RMI object has been
                //unexported
                if (sfLog().isIgnoreEnabled()) sfLog().ignore(nex);
            } catch (Exception ex) {
                if (sfLog().isErrorEnabled()) sfLog().error(ex);
            }
        } else if (entry instanceof ComponentDescription) {
            //System.out.println("EntryCD: getting name");
            try {
                name = ((ComponentDescription) entry).sfCompleteName().toString();
            //    System.out.println("EntryCD: getting name - "+name);
            } catch (Exception ex) {
                if (sfLog().isErrorEnabled()) sfLog().error(ex);
            }
        }
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
        if ((entry instanceof Compound)|| ((showCDasChild)&&(entry instanceof ComponentDescription)) || ((showCDasChild)&&(entry instanceof Prim))) {
            //System.out.println("getChildCount():["+index+"]"+"");
            try {
                return ((getChildren())[index][1]);
            } catch (Exception ex) {
                //System.out.println(ex.toString());
                if (sfLog().isErrorEnabled()) sfLog().error(ex);
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
        if ((entry instanceof Compound) || ((showCDasChild)&&(entry instanceof ComponentDescription)) || ((showCDasChild)&&(entry instanceof Prim))) {
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
        //Attribute,value,tag
        String[][] empty = { { "", "","" }  };

        try {

            Context context = null;
            if (entry instanceof Prim){
                context = ((Prim) entry).sfContext();
            } else if (entry instanceof ComponentDescription){
                context = (( ComponentDescription) entry).sfContext();
            } else {
                return(empty);
            }
            String name = "";
            Object value = null;
            String tags ="";
            String solvedValue = null;
            Object[][] data = new Object[this.sizeAttributes()][3];
            int index = 0;

            for (Enumeration e = context.keys(); e.hasMoreElements();) {
                try {
                    name = "";
                    value = null;
                    tags ="";
                    name = e.nextElement().toString();
                    value = context.get(name);
                    tags = context.sfGetTags(name).toString();
                    if (!isChild(value)) {
                        try {
                            value = ContextImpl.getBasicValueFor(value);
                        } catch (Exception ex1) { if (sfLog().isIgnoreEnabled()) sfLog().ignore(ex1);/*ignore*/ }

                        try {
                            data[index][0] = name;
                            data[index][1] = value;
                            data[index][2] = tags;
                        } catch (Exception ex) {
                            if (sfLog().isErrorEnabled()) sfLog().error("sfManagementConsole.deployEntry.getAttributes: error reading "+name+" >"+ex.getMessage());
                            data[index][0] = name;
                            data[index][1] = "Error:"+ex.toString();
                            data[index][2] = tags;
                            index++;
                            throw ex;
                        }
                        index++;
                    }
                } catch (Exception ex1) {
                    if (sfLog().isErrorEnabled()) sfLog().error(ex1);
                }
            }

            return data;
        } catch (java.rmi.NoSuchObjectException nso){
            //Ignore: tipically component terminated and unexported from rmi
            if (sfLog().isIgnoreEnabled()) sfLog().ignore(nso);
            return empty;
        } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error("Error DeployEntry.getAttributes()" +  ex.toString(),ex);
            return empty;
        }
    }

    /**
     *  Gets the children attribute of the DeployEntry object
     *
     *@return    The children value
     */
    public Object[][] getChildren() {
        String[][] empty = {
            { "", "" }
        };
        boolean isContextCopy = isCopy;
        try {
            Context context = null;
            Object contextCopy=null;
            if (entry instanceof Prim){
                context = ((Prim) entry).sfContext();
                contextCopy= ((Prim) entry).sfContext();
            } else if (entry instanceof ComponentDescription){
                context = (( ComponentDescription) entry).sfContext();
                contextCopy =  (( ComponentDescription) entry).sfContext();
            } else {
                return(empty);
            }
            //Check for copy
            if (context!=contextCopy){
               isContextCopy = true;
            }

            String name = "";
            Object obj = null;
            Object[][] data = new Object[this.sizeChildren()][2];

            int index = 0;

            for (Enumeration e = context.keys(); e.hasMoreElements();) {
                try {
                  name = e.nextElement().toString();
                  obj = context.get(name);
                  if ((isChild(obj))) {
                    data[index][0] = name;
                    data[index][1] = obj2Entry(obj,isContextCopy);
                    index++;
                  }
                } catch (Exception ex1) {
                  if (sfLog().isErrorEnabled()) sfLog().error(ex1);
                  data[index][0] = name;
                  data[index][1] = "Error:"+ex1.toString();
                  index++;
                }
            }
            return data;
        } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error(ex);
            return null;
        }
    }

    /**
     *
     * @return
     */
    public Object getEntryTags(){
       Object parent = null;
        Set tags = new HashSet();
        tags.add("tags_error");
        try {
            if (entry instanceof Prim){
                     tags = ((Prim) entry).sfGetTags();
            } else if (entry instanceof ComponentDescription){
                try {
                     tags = (( ComponentDescription) entry).sfGetTags();
                } catch (SmartFrogContextException ex){
                     //only show in trace - it will happen when browsing "*copy*" descriptions
                     if (sfLog().isTraceEnabled()) sfLog().trace("Error DeployEntry.getEntryTags()" +  ex.toString(),ex);
                     return tags;
                }
            } else {
                return tags;
            }
            if (tags==null) tags=new HashSet();
            return tags;

        } catch (Exception e) {
            if (sfLog().isErrorEnabled()) sfLog().error("Error DeployEntry.getEntryTags()" +  e.toString(),e);
            tags = new HashSet();
            tags.add("[tags error]");
            return tags;
        }

    }
    /**
     *  Search for a particular entry in its children tree.
     *
     *@return    The entry value
     */
    public Object getEntry() {
        return entry;
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
    }

    /**
     * Gets string representation of the deploy entry
     *
     *@return  string representation of the deploy entry
     */
    public String toString() {
        Object tags = getEntryTags();
        if ((tags!=null)&&(!tags.toString().equals(""))&&(!tags.toString().equals("[]"))){
            return getRDN()+" "+getEntryTags();
        }
        return getRDN();
    }

    /**
     * Returns a textual representation of the deploy entry
     *
     *@return    textual representation of the deploy entry
     */
    public String toStringAll() {
        return entry.toString();
    }

    /**
     * Returns children string
     *
     *@return  children string
     */
    public String childrenString() {
        return "";
    }

    /**
     * Returns attributes string
     *
     *@return    attributes in sting form.
     */
    public String attributesString() {
        return "";
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

            Context context = null;

            if (entry instanceof Prim){
                context = ((Prim) entry).sfContext();
            } else if (entry instanceof ComponentDescription){
                context = (( ComponentDescription) entry).sfContext();
            } else {
                return(counter);
            }

            Object obj = null;

            for (Enumeration e = context.keys(); e.hasMoreElements();) {
                name = e.nextElement().toString();
                obj = context.get(name);

                if (!isChild(obj)) {
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
            Context context = null;

            if (entry instanceof Prim){
                context = ((Prim) entry).sfContext();
            } else if (entry instanceof ComponentDescription){
                context = (( ComponentDescription) entry).sfContext();
            } else {
                return(counter);
            }
            for (Enumeration e = context.keys(); e.hasMoreElements();) {
                try {
                  name = e.nextElement().toString();
                  obj = context.get(name);
                  if ((isChild(obj))) {
                    //&& !name.toString().endsWith("URL"))
                    counter++;
                  }
                } catch (Exception ex1) {
                  if (sfLog().isErrorEnabled()) sfLog().error(ex1);
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
        sfLog().out("Info: " + this.toString());
        sfLog().out("    - #Children&CD:" + this.getChildrenCount());
        sfLog().out("    - #Attributes:" + this.sizeAttributes());
        sfLog().out("    - isLeaf():" + this.isLeaf());
        sfLog().out("    - children:" + getChildren());
        sfLog().out("    - attributes:" + getAttributes());
    }

    // Parse Name of Entry (SFObjects)

    /**
     *  Gets the rDN attribute of the DeployEntry object
     *
     *@param  DN  DeployEntry
     *@return   rdn attribute
     * @deprecated
     */
    private String getRDN(String DN) {
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
     *  Gets the rDN attribute of the DeployEntry object
     *
     *@param  DN  DeployEntry
     *@return   rdn attribute
     */
    private String getRDN(Reference DN) {
          String RDN ="";
          if (this.showRootProcessName) {
             RDN = this.getRDNProcessCompound();
          }
          if ((RDN.equals(""))&&(DN.size()>=1)){
             ReferencePart refP = DN.lastElement();
             if (refP instanceof HereReferencePart){
               return ((HereReferencePart)refP).toString(-1);
             } else {
               return refP.toString();
             }
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
        String entryName ="";
        ProcessCompound pcEntry=null;
        try {
            //Now every component can register with its local processCompound
            // and therefore it should be shown in this way.
            //Find ProcessCompound where entry is deployed
//            StringBuffer refStr = new StringBuffer();
//            refStr.append("HOST \"");
//            refStr.append(((Prim)entry).sfDeployedHost().getCanonicalHostName());
//            refStr.append("\":");
//            refStr.append(((Prim)entry).sfDeployedProcessName());
//            Reference refPC = Reference.fromString(refStr.toString());
            // enty host
            //pcEntry = (ProcessCompound)((Prim)entry).sfResolve(refPC);
            pcEntry = SFProcess.getProcessCompound();
            try {
              Object prim = ((Prim)entry).sfResolve( ((Prim) entry).sfCompleteName());
              if (pcEntry.sfContainsValue(prim)) {
                entryName =  (String)pcEntry.sfAttributeKeyFor(prim);
              }
            } catch (Exception ex2) {
              if ((pcEntry!=null)&&(pcEntry.sfContainsValue(this.getEntry()))) {
                  entryName =  (String)pcEntry.sfAttributeKeyFor(this.getEntry());
              }
            }

        } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error(ex);
        }

        return entryName;
    }




    /**
     *  Gets the parentDN attribute of the DeployEntry object
     *
     *@param  DN  DeployEntry
     *@return     The parentDN value
     * @deprecated
     */
    private String getParentDN(String DN) {
        if (DN.lastIndexOf(':') > 0) {
            return DN.substring(0, DN.lastIndexOf(':'));
        } else {
            return null;
        }
    }

    /**
     *  Gets the parentDN attribute of the DeployEntry object
     *
     *@param  DN  DeployEntry
     *@return     The parentDN value
     */
    private String getParentDN(Reference DN) {
        if (DN.size()>1) {
            Reference newRef = (Reference) DN.copy();
            ReferencePart refP = DN.lastElement();
            newRef.removeElement(refP);
            return (newRef.toString());
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
     * Checks if the input object is an attribute not a child
     *
     *@param  obj  inp object
     *@return      true if it is an attribute else false
     */
    private boolean isChild(Object obj) {
        try {
          // Component child
          if ( (obj instanceof Prim) && (entry instanceof Prim) &&
              ( (Compound) entry).sfContainsChild( (org.smartfrog.sfcore.prim.Liveness) obj)) {
            return true;
          }
          //ComponentDescription child
          if ( (obj instanceof ComponentDescription) && showCDasChild) {
             return true;
          }

        } catch (RemoteException ex) {
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
     *@param  isCopy of the original object?
     *@return        DeployEntry object
     */
    private DeployEntry obj2Entry(Object value, boolean isCopy) {
        try {
            boolean newShowRootProcessName = (this.showRootProcessName&&(entry instanceof ProcessCompound));
            if ((value instanceof Prim)) {
                return (new DeployEntry(value, false, newShowRootProcessName,this.showCDasChild));
            }
            if ((value instanceof ComponentDescription)) {
                return (new DeployEntry(value, isCopy, newShowRootProcessName,this.showCDasChild));
            }
//            else if (value instanceof Reference) {
//                //sfLog().info("resolving reference...");
//                do {
//                   ((Reference) value).setEager(true);
//                    value = ((Prim) entry).sfResolve((Reference) value);
//                    //Check if we are getting a copy
//                    if (value instanceof ComponentDescription) {
//                     Object value2 = ((Prim) entry).sfResolve((Reference) value);
//                     if (value!=value2){
//                         //once a cd is a copy all the children CDs will be copies
//                         isCopy = true;
//                     }
//                }
//
//                } while (value instanceof Reference);
//                return (new DeployEntry(value,isCopy, newShowRootProcessName,this.showCDasChild));
//            }
        } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error("Error building mgt info: " + ex,ex);
            //return new DeployEntry((ex.getMessage()+(value.toString())));
        }
        return new DeployEntry();
    }

    private void initLog (){
        try {
           this.sfLog=LogFactory.getLog("sfManagementConsole");
        } catch (Exception e) {
            sfLog.error(e);
        }
    }
    private LogSF sfLog(){
        return sfLog;
    }
}


//class
