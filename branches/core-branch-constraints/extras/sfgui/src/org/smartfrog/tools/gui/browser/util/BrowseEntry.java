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



package org.smartfrog.tools.gui.browser.util;

//import org.smartfrog.tools;
/*
 * BrowseEntry.java
 *
 *
 */
import java.util.*;

/**
 *  Description of the Class
 */
public final class BrowseEntry implements Entry {

   private String name = null;
   private String parentDN = null;

   private HashMap attributes = null;
   private HashMap children = null;

   char separator = '/';

   /**
    *  Constructor for the BrowseEntry object
    *
    *@param  DN  Description of Parameter
    */
   public BrowseEntry(String DN) {
      this.name = this.getRDN(DN);
      this.parentDN = this.getParentDN(DN);
      //System.out.println("**CREATED: "+ name );
   }

   /**
    *  Constructor for the BrowseEntry object
    */
   public BrowseEntry() {
      new BrowseEntry("*");
   }

   /**
    *  Description of the Method
    *
    *@param  msg  Description of Parameter
    */
   public void add(String msg) {
      //msg format: dir1/dir2/file.sf
      // Old msg format: system:demoA, DEPLOYED, 15:51:37.187 22/06/01, guijarro-j-5/15.144.25.153
      //add children
      //parseMsg(msg);
      //String msgDN = getMsgDN(msg);
      // check if DN is this component, if it is not looks for a clidren and pass msg
      // if children doesn't exist, create it.
      // Only create 1 level in the hierarchy!
      //System.out.println("adding in: "+msg+" to "+this.getDN());
      // ROOT=ROOT
      if (this.getDN().equals(this.getMsgDN(msg))) {
         //this is the node referenced
         //System.out.println("   Node adding attributes to itself: "+this.getDN());
//         addAtribute(this.getMsgAction(msg), this.getMsgLocation(msg));
      } else {
         //check if this child exist
         //ROOT=ROOT:(System)
         //System.out.println(this.getDN()+"=?"+this.getParentDN(this.getMsgDN(msg)));
         if (this.getDN().equals(this.getParentDN(this.getMsgDN(msg)))) {
            // The node referenced is a children of this node
            try {
               if (children == null) {
                  this.children = new HashMap();
                  //Create new Child (as children were null)
                  BrowseEntry newChild = new BrowseEntry(this.getMsgDN(msg));
                  newChild.add(msg);
                  children.put(newChild.getName(), newChild);
                  //System.out.println("  Created Node (and Children): "+this.getDN());
               } else {
                  String nameChild = this.getRDN(this.getMsgDN(msg));
                  // Check if already exist, if not create it
                  //Only first level!!! So last position in MsgDN should be Name.
                  try {
                     if (children.containsKey(nameChild)) {
                        // Already exist so we add attributes to it.
                        BrowseEntry newChild = (BrowseEntry)children.get(nameChild);
                        if (newChild != null) {
                           newChild.add(msg);
                           children.put(newChild.getName(), newChild);
                           //System.out.println("  Added attribs to existing node: "+this.getDN());
                           // To add more than one level... if it doesn't exist, create...
                        }
                     } else {
                        //Child didn't exist so, it is created and added attribs
                        BrowseEntry newChild = new BrowseEntry(this.getMsgDN(msg));
                        newChild.add(msg);
                        children.put(newChild.getName(), newChild);
                        //System.out.println("  Created Node and attribs: "+this.getDN());
                     }
                  } catch (Exception ex) {
                     System.out.println("Child " + nameChild + " not contained in " + this.getDN());
                  }
               }
            } catch (Exception ex) {
               System.out.println("Failed in adding new child:" + getDN());
            }
         } else {
            // if it is for one or our children we pass the ball
            //ROOT=ROOT:System:(foo)
            if (this.getMsgDN(msg).startsWith(this.getDN())) {
               // Look for child and pass the hot potato
               String nameChild = getMsgChild4Parent(this.getDN(), getMsgDN(msg));
               //System
               //System.out.println("hot patato to(namechild): "+nameChild);
               try {
                  if (children == null) {
                     this.children = new HashMap();
                  }
                  if (children.containsKey((Object)nameChild)) {
                     BrowseEntry newChild = (BrowseEntry)children.get(nameChild);
                     if (newChild != null) {
                        newChild.add(msg);
                        children.put(newChild.getName(), newChild);
                        //System.out.println("  Passed hot potato to grandchild...: "+newChild.getDN());
                     }
                  } else {
                     // And for entries not ordered!
                     // Example you receive ROOT:system:foo before ROOT:system
                     BrowseEntry newChild = new BrowseEntry(this.getDN() + separator + nameChild);
                     if (newChild != null) {
                        newChild.add(msg);
                        children.put(newChild.getName(), newChild);
                     }
                     // ----
                  }
               } catch (Exception ex) {
                  ex.printStackTrace();
                  System.out.println("xChild " + nameChild + " not contained in " + this.getDN());
               }
            } else {
               System.out.println("ERROR: Trying to add: \n" + getMsgDN(msg) + " to \n" + this.getDN());
            }
         }
      }
   }

   /**
    *  Adds a feature to the Atribute attribute of the BrowseEntry object
    *
    *@param  attrib  The feature to be added to the Atribute attribute
    *@param  value   The feature to be added to the Atribute attribute
    */
   private void addAtribute(String attrib, String value) {
      if (attributes == null) {
         attributes = new HashMap();
         // Add attribute and value contained in the message;
      }
      attributes.put(attrib, value);
   }

// Parsing msg
////msg format: system:demoA, DEPLOYED, 15:51:37.187 22/06/01 guijarro-j-5/15.144.25.153
   /**
    *  Description of the Method
    *
    *@param  msg  Description of Parameter
    */
   private void parseMsg(String msg) {
      // To analyse the component that you need to create!
      System.out.println("   -- New component --");
      System.out.println("msgDN:" + this.getMsgDN(msg));
//      System.out.println("    action:" + this.getMsgAction(msg));
//      System.out.println("    location:" + this.getMsgLocation(msg));
      System.out.println("    RDN:" + this.getRDN(this.getMsgDN(msg)));
      System.out.println("    ParentDN:" + this.getParentDN(this.getMsgDN(msg)));
      System.out.println("    getMsgChild4Parent(System): " + getMsgChild4Parent("/", getMsgDN(msg)));
      System.out.println("    getMsgChild4Parent(System): " + getMsgChild4Parent("/System", getMsgDN(msg)));
      System.out.println("    getMsgChild4Parent(System:foo): " + getMsgChild4Parent("/System/foo", getMsgDN(msg)));
      // -- end test block
   }

   /**
    *  Gets the msgDN attribute of the BrowseEntry object
    *
    *@param  msg  Description of Parameter
    *@return      The msgDN value
    */
   private String getMsgDN(String msg) {
      if (msg.indexOf(',')==-1){
        return msg;
      }
      return msg.substring(0, msg.indexOf(','));
   }

//   /**
//    *  Gets the msgAction attribute of the BrowseEntry object
//    *
//    *@param  msg  Description of Parameter
//    *@return      The msgAction value
//    */
//   private String getMsgAction(String msg) {
//      String msgAction = msg.substring(msg.indexOf(',') + 1, msg.length());
//      msgAction = msgAction.substring(0, msgAction.indexOf(','));
//      return msgAction;
//   }

//   /**
//    *  Gets the msgLocation attribute of the BrowseEntry object
//    *
//    *@param  msg  Description of Parameter
//    *@return      The msgLocation value
//    */
//   private String getMsgLocation(String msg) {
//      String msgLocation = msg.substring(msg.lastIndexOf(',') + 1, msg.length());
//      return msgLocation;
//   }

   /**
    *  Gets the rDN attribute of the BrowseEntry object
    *
    *@param  DN  Description of Parameter
    *@return     The rDN value
    */
   private String getRDN(String DN) {
      return DN.substring(DN.lastIndexOf(separator) + 1, DN.length());
   }

   /**
    *  Gets the parentDN attribute of the BrowseEntry object
    *
    *@param  DN  Description of Parameter
    *@return     The parentDN value
    */
   private String getParentDN(String DN) {
      if (DN.lastIndexOf(separator) > 0) {
         return DN.substring(0, DN.lastIndexOf(separator));
      } else {
         return null;
      }
   }

   /**
    *  Gets the msgChild4Parent attribute of the BrowseEntry object
    *
    *@param  parentDN  Description of Parameter
    *@param  fullDN    Description of Parameter
    *@return           The msgChild4Parent value
    */
   private String getMsgChild4Parent(String parentDN, String fullDN) {
      if (fullDN.equals(parentDN)) {
         return "";
      }
      if (fullDN.startsWith(parentDN)) {

         int index = fullDN.lastIndexOf(parentDN + separator) + parentDN.length() + 1;
         int indexEnd = fullDN.indexOf(separator, index);
         //System.out.println("    ****index 1 and 2:"+index+separator+indexEnd);
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
    *  Adds a feature to the Attributes attribute of the BrowseEntry object
    *
    *@param  msg  The feature to be added to the Attributes attribute
    *@return      Description of the Returned Value
    */
   private boolean addAttributes(String msg) {
      return false;
   }

   /**
    *  Description of the Method
    *
    *@param  DN     Description of Parameter
    *@param  value  Description of Parameter
    *@return        Description of the Returned Value
    */
   public boolean add(String DN, Object value) {
      // Is this the right node?
      throw new java.lang.UnsupportedOperationException("Method add(DN, value) not completed yet");
      // add in present node
//      if (value instanceof BrowseEntry) {
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
    *  Description of the Method
    *
    *@param  DN  Description of Parameter
    *@return     Description of the Returned Value
    */
   public boolean remove(String DN) {
      throw new java.lang.UnsupportedOperationException("Method remove(DN) not yet implemented.");
      //return false;
   }

   /**
    *  Gets the leaf attribute of the BrowseEntry object
    *
    *@return    The leaf value
    */
   public boolean isLeaf() {
      //if (children !=null) System.out.println("isLeaf() ["+this.getDN()+"]"+children.size());
      if ((children != null) && (children.size() > 0)) {
         return false;
      } else {
         return true;
      }
   }

   /**
    *  Description of the Method
    *
    *@return    Description of the Returned Value
    */
   public String toString() {
      return this.getRDN();
   }

   /**
    *  return a textual representation of the File object
    *
    *@return    Description of the Returned Value
    */
   public String toStringAll() {
      //return parentDN+":"+name+separator+childrenString()+separator+attributesString();
      StringBuffer txt = new StringBuffer(parentDN + separator + name);
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
    *  Description of the Method
    *
    *@return    Description of the Returned Value
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
    *  Description of the Method
    *
    *@return    Description of the Returned Value
    */
   public String attributesString() {
//      if (attributes != null) {
//         //return (attributes.keySet().toString()+":"+attributes.values().toString());
//         return ("  attributes: " + dumpHashMap(attributes));
//      } else {
         return "";
//      }
   }

   /**
    *  Description of the Method
    *
    *@param  hashmap  Description of Parameter
    *@return          Description of the Returned Value
    */
   public static String dumpHashMap(HashMap hashmap) {
      StringBuffer desc = new StringBuffer("");
      for (Iterator i = (hashmap.keySet()).iterator(); i.hasNext(); ) {
         Object key = i.next();
         Object obj = hashmap.get(key);
         if (obj instanceof BrowseEntry) {
            desc.append("\n     \"" + key + "\" --> \"" + ((BrowseEntry)obj).toStringAll() + "\"");
         } else {
            desc.append("\n     \"" + key + "\" --> \"" + obj + "\"");
         }
      }
      return new String(desc);
   }


   /**
    *  Description of the Method
    *
    *@param  hashmap  Description of Parameter
    *@return          Description of the Returned Value
    */
   public static Object[][] dumpHashMap2Array(HashMap hashmap) {
      Object[][] data = new Object[hashmap.size()][2];
      int i = 0;
      for (Iterator iterat = (hashmap.keySet()).iterator(); iterat.hasNext(); ) {
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
    *  Gets the root attribute of the BrowseEntry object
    *
    *@return    The root value
    */
   public String getRoot() {
      if ((parentDN == null)) {
         return name;
      } else {
         try {
            return parentDN.substring(0, ((String)parentDN).indexOf(separator));
         } catch (Exception ex) {
            return parentDN;
         }
      }
   }


   /**
    *  Gets the name attribute of the BrowseEntry object
    *
    *@return    The name value
    */
   public String getName() {
      return name;
   }

   /**
    *  Gets the rDN attribute of the BrowseEntry object
    *
    *@return    The rDN value
    */
   public String getRDN() {
      return name;
   }


   /**
    *  Gets the parentDN attribute of the BrowseEntry object
    *
    *@return    The parentDN value
    */
   public String getParentDN() {
      return parentDN;
   }

   /**
    *  Gets the dN attribute of the BrowseEntry object
    *
    *@return    The dN value
    */
   public String getDN() {
      if ((parentDN == null)) {
         return name;
      } else {
         return parentDN + separator + name;
      }
   }


   /**
    *  To Get the children of the entry
    *
    *@return    The children value
    */

   public HashMap getChildren() {
      return children;
   }

   /**
    *  Gets the child attribute of the BrowseEntry object
    *
    *@param  index  Description of Parameter
    *@return        The child value
    */
   public BrowseEntry getChild(int index) {
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
            //System.out.println("Key/entry: "+key+separator+this.getRDN()+separator+count+separator+index);
            if (count == index) {
               return (BrowseEntry)children.get(key);
            }
            count++;
         }
         return null;
      }
      return null;
   }

   /**
    *  To Get the number of children of the entry
    *
    *@return    The childrenCount value
    */

   public int getChildrenCount() {
      if (children != null) {
         return children.size();
      } else {
         return 0;
      }
   }


   /**
    *  Gets the attributes attribute of the BrowseEntry object
    *
    *@return    The attributes value
    */
   public HashMap getAttributes() {
      return this.attributes;
   }

   /**
    *  Gets the attributesArray attribute of the BrowseEntry object
    *
    *@return    The attributesArray value
    */
   public Object[][] getAttributesArray() {
      if (this.attributes == null) {
        return null;

      }
      return this.dumpHashMap2Array(this.attributes);
   }

   /**
    *  Search for a particular entry in its children tree.
    *
    *@param  DN        Description of Parameter
    *@param  allLevel  Description of Parameter
    *@return           The entry value
    */
   public BrowseEntry getEntry(String DN, boolean allLevel) {
      // Search 1 or all levels bellow this object
      throw new java.lang.UnsupportedOperationException("Method remove(DN) not yet implemented.");
      //return null;
   }


   /**
    *  Description of the Method
    *
    *@param  args  Description of Parameter
    */
   public static void main(String args[]) {
      //Test
      System.out.println("Starting...a new adventure.");
      BrowseEntry entry = new BrowseEntry("#include ");
      System.out.println("Starting...a new adventure.");
      System.out.println("Adding: ------------------------------------------");
      entry.add("#include /dirA/dir1/file1.sf");
      entry.add("#include /dirA/dir2/file1.sf");
      entry.add("#include /dirB/dir1/file2.sf");
      System.out.println("...Finished");
   }

}
//class

