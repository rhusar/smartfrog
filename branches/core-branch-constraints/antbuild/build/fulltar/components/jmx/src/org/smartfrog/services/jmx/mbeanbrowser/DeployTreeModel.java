/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jmx.mbeanbrowser;

import java.util.EventListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import javax.swing.event.EventListenerList;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class DeployTreeModel implements TreeModel {

    /**
     *  Description of the Field
     */
    public final static String CLASS = PropertyTableModel.class.getName();

    /**
     *  Root of the tree
     */
    protected SFNode m_node = new SFNode();

    /**
     *  Listeners
     */
    protected EventListenerList listenerList = new EventListenerList();


    /**
     *  Constructors
     */
    public DeployTreeModel() { }


    /**
     *  Constructor for the DeployTreeModel object
     *
     *@param  node  Description of the Parameter
     */
    public DeployTreeModel(SFNode node) {
        m_node = node;
    }


    /**
     *  Implementation of TreeModel Interface
     *
     *@return    The root value
     */
    public Object getRoot() {
        return this.m_node;
    }


    /**
     *  Gets the child attribute of the DeployTreeModel object
     *
     *@param  parent  Description of the Parameter
     *@param  index   Description of the Parameter
     *@return         The child value
     */
    public Object getChild(Object parent, int index) {
        if (parent instanceof SFNode) {
            return ((SFNode) parent).getChildAt(index);
        } else {
            return null;
        }
    }


    /**
     *  Gets the childCount attribute of the DeployTreeModel object
     *
     *@param  parent  Description of the Parameter
     *@return         The childCount value
     */
    public int getChildCount(Object parent) {
        if (parent instanceof SFNode) {
            return ((SFNode) parent).getChildCount();
        }
        return 0;
    }


    /**
     *  Gets the leaf attribute of the DeployTreeModel object
     *
     *@param  node  Description of the Parameter
     *@return       The leaf value
     */
    public boolean isLeaf(Object node) {
        if (node instanceof SFNode) {
            return ((SFNode) node).isLeaf();
        } else {
            return true;
        }
    }


    /**
     *  Gets the indexOfChild attribute of the DeployTreeModel object
     *
     *@param  parent  Description of the Parameter
     *@param  child   Description of the Parameter
     *@return         The indexOfChild value
     */
    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null) {
            return -1;
        }
        if (parent instanceof SFNode && child instanceof SFNode) {
            return ((SFNode) parent).getIndex((SFNode) child);
        }
        return -1;
    }


    /**
     *  Adds a feature to the TreeModelListener attribute of the DeployTreeModel
     *  object
     *
     *@param  l  The feature to be added to the TreeModelListener attribute
     */
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }


    /**
     *  Description of the Method
     *
     *@param  l  Description of the Parameter
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }


    /**
     *  Description of the Method
     *
     *@param  path      Description of the Parameter
     *@param  newValue  Description of the Parameter
     */
    public void valueForPathChanged(TreePath path, Object newValue) {

        throw new java.lang.UnsupportedOperationException("Method valueForPathChanged() not yet implemented.");
    }

}
