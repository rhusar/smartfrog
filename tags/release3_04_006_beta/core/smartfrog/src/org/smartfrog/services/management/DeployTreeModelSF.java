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

import java.util.Vector;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * DeployTreeModelSF is the deployable tree model for SmartFrog component
 * hierarchy.
 */
public class DeployTreeModelSF implements TreeModel {
    DeployEntry entry = null;

    //Prim or Compound
    // Listeners for the tree model.

    /**
     *  Listeners.
     */
    protected Vector listeners;

    //  public void DefaultTreeModel(){
    //      listeners = new Vector();
    //
    //  }

    /**
     *  Constructs the DeployTreeModelSF object
     */
    public DeployTreeModelSF() {
        try {
            this.entry = new DeployEntry();
            this.listeners = new Vector();

            //System.out.println("Model created");
        } catch (Exception ex) {
            System.out.println(ex.toString());

            //ex.printStackTrace();
        }
    }

    /**
     *  Constructs the DeployTreeModelSF object with the deploy entry
     *
     *@param  entry  deploy entry
     */
    public DeployTreeModelSF(Object entry, boolean showRootProcessName) {
        try {
            this.entry = new DeployEntry(entry, showRootProcessName);
            this.listeners = new Vector();

            //System.out.println("DeployTreeModel created");
        } catch (Exception ex) {
            System.out.println("sfManagementConsole (DeployTreeModel): "+ex.toString());

            //ex.printStackTrace();
        }
    }

    /**
     *  Gets the root attribute of the DeployTreeModelSF object
     *
     *@return    The root value
     */
    public Object getRoot() {
        //System.out.println("getRoot():"+entry);
        // Needs to the the real ROOT of the system
        return entry.getRoot();
    }

    /**
     *  Implements getChild () method defined in java swing TreeModel.
     *
     *@param  parent  Deploy entry object acting as a parent component
     *@param  index   index
     *@return         The child value
     */
    public Object getChild(Object parent, int index) {
        //System.out.println("DeployTreeModel.getChild("+parent+","+index+")");
        if (parent instanceof DeployEntry) {
            return ((DeployEntry) parent).getChild(index);
        } else {
            return null;
        }
    }

    /**
     * Gets the childCount attribute of the DeployTreeModelSF component
     *
     *@param  parent  Deploy entry parent
     *@return         The childCount value
     */
    public int getChildCount(Object parent) {
        //System.out.println("DeployTreeModel.getChildCount("+parent+")");
        if (parent instanceof DeployEntry) {
            return ((DeployEntry) parent).getChildrenCount();
        }

        return 0;
    }

    /**
     *  Checks if the deploy entry is the leaf component in the component's
     *  tree
     *
     *@param  node  Node (deploy entry)
     *@return       true if that node is leaf node else false
     */
    public boolean isLeaf(Object node) {
        if (node instanceof DeployEntry) {
            return ((DeployEntry) node).isLeaf();
        } else {
            return true;
        }
    }

    /**
     * Not implemented.
     *
     *@param  path
     *@param  newValue
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        /**
         *@todo:    Implement this javax.swing.tree.TreeModel method
         */
        throw new java.lang.UnsupportedOperationException(
            "Method valueForPathChanged() not yet implemented.");
    }

    /**
     *  Gets the indexOfChild attribute of the DeployTreeModelSF object
     *
     *@param  parent  Deploy Entry object
     *@param  child   Child of the deploy entry
     *@return         The indexOfChild value
     */
    public int getIndexOfChild(Object parent, Object child) {
        //System.out.println("DeployTreeModel.getIndexOfChild("+parent+","+child+")");
        if (parent instanceof DeployEntry) {
            ((DeployEntry) parent).getIndexOfChild(child);
        }

        return -1;
    }

    /**
     *  Implements addTreeModelListener () method defined in java swing
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
     *  Implements removeTreeModelListener () method defined in java swing
     *  TreeModel.
     *
     *@param  l  tree model listener object
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.removeElement(l);
    }

    /**
     *  Not implemented.
     *
     *@param  node
     */
    public void add(String node) {
        System.out.println("Not implemented. DeployTreeModelSF: " + node);

        //entry.add(node);
    }
}
