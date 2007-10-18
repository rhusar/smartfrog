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
import javax.swing.event.TreeModelEvent;
import javax.swing.event.EventListenerList;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Prim;

/**
 * DeployTreeModelSF is the deployable tree model for SmartFrog component
 * hierarchy.
 */
public class DeployTreeModelSF  implements TreeModel {
    /** Log for this class, created using class name*/
    LogSF sfLog = LogFactory.getLog("sfManagementConsole");

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
            initLog();
            //System.out.println("Model created");
        } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error(ex);
        }
    }

    /**
     *  Constructs the DeployTreeModelSF object with the deploy entry
     *
     * @param  entry  deploy entry
     * @param isCopy is entry a copy?
     * @param showRootProcessName flag indicating to show rootprocess name
     * @param showCDasChild flag indicating to show CD as child
     */
    public DeployTreeModelSF(Object entry, boolean isCopy, boolean showRootProcessName,boolean showCDasChild) {
        try {
           boolean newShowRootProcessName = (showRootProcessName&&(entry instanceof ProcessCompound));
           this.entry = new DeployEntry(entry, isCopy, newShowRootProcessName,showCDasChild);
           this.listeners = new Vector();
           initLog();
            //System.out.println("DeployTreeModel created");
        } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error("sfManagementConsole (DeployTreeModel): "+ex.toString(),ex);
        }
    }

    public void showCDasChild(boolean showCDasChild){
       entry.showCDasChild = showCDasChild;
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
        sfLog().out("Not implemented. DeployTreeModelSF: " + node);
        //entry.add(node);
    }

    private void initLog (){
        try {
            if (entry.getEntry() instanceof Prim) {
               this.sfLog=LogFactory.getLog((Prim)(entry.getEntry()));
            } else {
               this.sfLog=LogFactory.getLog((String)entry.getEntry());
            }
        } catch (Exception e) {
            sfLog.error(e);
        }
    }
    private LogSF sfLog(){
        return sfLog;
    }
}
