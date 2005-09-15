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


import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import java.util.Vector;
//import com.hp.sfServices.trace.BrowseEntry;

/**
 *  Title: SerranoGuiUtils Package Description:
 *
 *@Todo: - Show "links" visualy (together with DeployTreePanel).
 */

public class BrowseTreeModel implements TreeModel {

   BrowseEntry entry = null;
   /**
    *  Description of the Field
    */
   protected Vector listeners;


   /**
    *  Constructor for the BrowseTreeModel object
    */
   public BrowseTreeModel() {
      entry = new BrowseEntry("#include ");
      listeners = new Vector();
   }

   /**
    *  Constructor for the BrowseTreeModel object
    *
    *@param  DN  Description of Parameter
    */
   public BrowseTreeModel(String DN) {
      entry = new BrowseEntry("/");
      listeners = new Vector();
   }

   /**
    *  Gets the root attribute of the BrowseTreeModel object
    *
    *@return    The root value
    */
   public Object getRoot() {
      //System.out.println("getRoot():"+entry);
      return entry;
   }

   /**
    *  implements getChild () method defined in java swing TreeModel.
    *
    *@param  parent  Description of Parameter
    *@param  index   Description of Parameter
    *@return         The child value
    */
   public Object getChild(Object parent, int index) {
      if (parent instanceof BrowseEntry) {
         BrowseEntry auxEntry = (BrowseEntry)parent;
         //System.out.println("getChild():["+index+"/"+parent+"]"+auxEntry.getChild(index));
         return auxEntry.getChild(index);
      } else {
         return ("error:" + parent);
      }
   }

   /**
    *  Gets the childCount attribute of the BrowseTreeModel object
    *
    *@param  parent  Description of Parameter
    *@return         The childCount value
    */
   public int getChildCount(Object parent) {
      if (parent instanceof BrowseEntry) {
         BrowseEntry auxEntry = (BrowseEntry)parent;
         //System.out.println("getChildCount():"+auxEntry.getChildrenCount());
         return auxEntry.getChildrenCount();
      } else {
         return 0;
      }
   }

   /**
    *  Gets the leaf attribute of the BrowseTreeModel object
    *
    *@param  node  Description of Parameter
    *@return       The leaf value
    */
   public boolean isLeaf(Object node) {
      if (node instanceof BrowseEntry) {
         //System.out.println("isLeaf():("+node+")"+"/"+((BrowseEntry)node).isLeaf()+"/"+((BrowseEntry)node).toStringAll());
         return ((BrowseEntry)node).isLeaf();
      } else {
         return false;
      }
   }

   /**
    *  Description of the Method
    *
    *@param  path      Description of Parameter
    *@param  newValue  Description of Parameter
    */
   public void valueForPathChanged(TreePath path, Object newValue) {
      /**
       *@todo:    Implement this javax.swing.tree.TreeModel method
       */
      throw new java.lang.UnsupportedOperationException("Method valueForPathChanged() not yet implemented.");
   }

   /**
    *  Gets the indexOfChild attribute of the BrowseTreeModel object
    *
    *@param  parent  Description of Parameter
    *@param  child   Description of Parameter
    *@return         The indexOfChild value
    */
   public int getIndexOfChild(Object parent, Object child) {
      for (int i = 0; i < getChildCount(parent); i++) {
         if (getChild(parent, i).equals(child)) {
            return i;
         }
      }
      return -1;
   }

   /**
    *  implements addTreeModelListener () method defined in java swing
    *  TreeModel.
    *
    *@param  l  The feature to be added to the TreeModelListener attribute
    */
   public void addTreeModelListener(TreeModelListener l) {
      if ((l != null) && !listeners.contains(l)) {
         listeners.addElement(l);
      }
   }

   /**
    *  implements removeTreeModelListener () method defined in java swing
    *  TreeModel.
    *
    *@param  l  Description of Parameter
    */
   public void removeTreeModelListener(TreeModelListener l) {
      listeners.removeElement(l);
   }

   /**
    *  Description of the Method
    *
    *@param  node  Description of Parameter
    */
   public void add(String node) {
      entry.add(node);
   }

   /**
    *  Description of the Method
    *
    *@return    Description of the Returned Value
    */
   public String toString() {
      return entry.toStringAll();
   }

   public BrowseEntry getEntry(){
      return entry;
   }
}
