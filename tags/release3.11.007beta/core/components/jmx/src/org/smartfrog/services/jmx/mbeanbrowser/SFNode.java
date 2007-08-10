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

import java.lang.reflect.*;
import java.security.*;
import javax.swing.JOptionPane;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.TreeSet;
import javax.management.*;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.services.jmx.common.SFAttribute;
import org.smartfrog.services.jmx.communication.RuntimeConnectionException;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class SFNode {

    /**
     *  An enumeration that is always empty. This is used when an enumeration of
     *  a leaf node's children is requested.
     */
    public final static Iterator EMPTY_ENUMERATION =
        new Iterator() {
            public boolean hasNext() {
                return false;
            }


            public Object next() {
                throw new NoSuchElementException("No more elements");
            }


            public void remove() {
                throw new NoSuchElementException("No elements");
            }
        };

    /**
     *  Object representing the SF component
     */
    protected SFAttribute m_attribute = null;

    /**
     *  The Main Frame of this application
     */
    protected MainFrame m_browser = null;

    /**
     *  The Object Name
     */
    protected ObjectName m_beanName = null;

    /**
     *  The Parent SFNode
     */
    protected SFNode m_parent = null;

    /**
     *  The List containing the child nodes
     */
    protected ArrayList m_children = null;

    /**
     *  The List containing the basic attributes
     */
    protected ArrayList m_basicAttributes = null;


    /**
     *  Constructors
     */
    public SFNode() { }


    /**
     *  Constructor for the SFNode object
     *
     *@param  browser    Description of the Parameter
     *@param  beanName   Description of the Parameter
     *@param  parent     Description of the Parameter
     *@param  attribute  Description of the Parameter
     */
    public SFNode(MainFrame browser, ObjectName beanName, SFNode parent, SFAttribute attribute) {
        m_browser = browser;
        m_beanName = beanName;
        m_parent = parent;
        m_attribute = attribute;
        refresh();
    }


    /**
     *  Refresh the data contained in this node
     */
    public void refresh() {
        TreeSet childrenSortedSet;
        TreeSet basicAttSortedSet;
        try {
            Context result = (Context) m_browser.doAction(new InvokeAction("sfGetAttributes", new Object[]{getPath()}, new String[]{"java.lang.String"}));
            childrenSortedSet = new TreeSet(new SFAttributeComparator());
            basicAttSortedSet = new TreeSet(new SFAttributeComparator());
            if (result != null) {
                m_basicAttributes = new ArrayList();
                for (Enumeration e = result.elements(); e.hasMoreElements(); ) {
                    SFAttribute sfAttr = (SFAttribute) e.nextElement();
                    if (!isNode(sfAttr)) {
                        basicAttSortedSet.add(sfAttr);
                    } else {
                        childrenSortedSet.add(sfAttr);
                    }
                }
                m_children = new ArrayList(childrenSortedSet);
                m_basicAttributes = new ArrayList(basicAttSortedSet);
            }
        } catch (RuntimeConnectionException ce) {
            ce.printStackTrace();
            JOptionPane.showMessageDialog(this.m_browser, ce
            /*
             *  .getLocalizedMessage()
             */
                    );
            m_browser.getMBeanServer().disconnect();
            m_browser.clear();
            m_parent.m_children = null;
        } catch (Throwable throwable) {
            Throwable rootCause = null;
            if (throwable instanceof MBeanException) {
                rootCause = ((MBeanException) throwable).getTargetException();
            } else if (throwable instanceof InvocationTargetException) {
                rootCause = ((InvocationTargetException) throwable).getTargetException();
            } else if (throwable instanceof PrivilegedActionException) {
                rootCause = ((PrivilegedActionException) throwable).getException();
                if (rootCause instanceof MBeanException) {
                    rootCause = ((MBeanException) rootCause).getTargetException();
                }
                if (rootCause instanceof InvocationTargetException) {
                    rootCause = ((InvocationTargetException) rootCause).getTargetException();
                }
            }
            if (rootCause == null) {
                rootCause = throwable;
            }
            JOptionPane.showMessageDialog(m_browser, rootCause, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  index          Description of the Parameter
     *@exception  Exception  Description of the Exception
     */
    public void refreshBasicAttribute(int index) throws Exception {
        SFAttribute attribute = (SFAttribute) m_basicAttributes.get(index);
        String attrPath = getPath();
        if ((attrPath != null) && (!attrPath.equals(""))) {
            attrPath += ":";
        }
        attrPath += attribute.getName();
        Object result = m_browser.doAction(new InvokeAction("sfGetAttribute", new Object[]{attrPath}, new String[]{"java.lang.String"}));
        attribute.setValue(result);
    }


    /**
     *  Description of the Method
     *
     *@param  index          Description of the Parameter
     *@exception  Exception  Description of the Exception
     */
    public void removeBasicAttribute(int index) throws Exception {
        SFAttribute attribute = (SFAttribute) m_basicAttributes.get(index);
        String attrPath = getPath();
        if ((attrPath != null) && (!attrPath.equals(""))) {
            attrPath += ":";
        }
        attrPath += attribute.getName();
        m_browser.doAction(new InvokeAction("sfRemoveAttribute", new Object[]{attrPath}, new String[]{"java.lang.String"}));
        m_basicAttributes.remove(index);
    }


    /**
     *  Gets the basicAttributes attribute of the SFNode object
     *
     *@return    The basicAttributes value
     */
    public Iterator getBasicAttributes() {
        if (m_basicAttributes == null) {
            return EMPTY_ENUMERATION;
        } else {
            return m_basicAttributes.iterator();
        }
    }


    /**
     *  Gets the basicAttribute attribute of the SFNode object
     *
     *@param  index  Description of the Parameter
     *@return        The basicAttribute value
     */
    public SFAttribute getBasicAttribute(int index) {
        if (m_basicAttributes == null) {
            return null;
        }
        if (index > -1) {
            return (SFAttribute) m_basicAttributes.get(index);
        } else {
            return null;
        }
    }


    /**
     *  Methods to manage the nodes
     *
     *@param  sfAttr  Description of the Parameter
     *@return         The node value
     */
    public boolean isNode(SFAttribute sfAttr) {
        int sfType = sfAttr.getSFType();
        if ((sfType != SFAttribute.BASIC) && (sfType != SFAttribute.UNKNOWN)) {
            return true;
        }
        return false;
    }


    /**
     *  Gets the component attribute of the SFNode object
     *
     *@return    The component value
     */
    public boolean isComponent() {
        if (m_attribute == null) {
            return false;
        }
        int sfType = m_attribute.getSFType();
        if ((sfType == SFAttribute.PRIM) || (sfType == SFAttribute.COMPOUND)) {
            return true;
        }
        return false;
    }


    /**
     *  Gets the path attribute of the SFNode object
     *
     *@return    The path value
     */
    public String getPath() {
        if (m_attribute != null && m_parent != null) {
            String parentPath = m_parent.getPath();
            if ((parentPath != null) && (!parentPath.equals(""))) {
                parentPath += ":";
            }
            return parentPath + m_attribute.getName();
        } else {
            return "";
        }
    }


    /**
     *  Methods to handle the tree *
     *
     *@return    The attribute value
     */

    public SFAttribute getAttribute() {
        return this.m_attribute;
    }


    /**
     *  Gets the childAt attribute of the SFNode object
     *
     *@param  childIndex  Description of the Parameter
     *@return             The childAt value
     */
    public SFNode getChildAt(int childIndex) {
        if (m_children != null) {
            return new SFNode(m_browser, m_beanName, this, (SFAttribute) m_children.get(childIndex));
        } else {
            return null;
        }
    }


    /**
     *  Gets the childCount attribute of the SFNode object
     *
     *@return    The childCount value
     */
    public int getChildCount() {
        if (m_children != null) {
            return m_children.size();
        } else {
            return 0;
        }
    }


    /**
     *  Gets the parent attribute of the SFNode object
     *
     *@return    The parent value
     */
    public SFNode getParent() {
        return m_parent;
    }


    /**
     *  Gets the index attribute of the SFNode object
     *
     *@param  node  Description of the Parameter
     *@return       The index value
     */
    public int getIndex(SFNode node) {
        if (node == null) {
            throw new IllegalArgumentException("argument is null");
        }
        String name = node.getAttribute().getName();
        for (int i = 0; i < getChildCount(); i++) {
            if (name.equals(m_children.get(i))) {
                return i;
            }
        }
        return -1;
    }


    /**
     *  Gets the allowsChildren attribute of the SFNode object
     *
     *@return    The allowsChildren value
     */
    public boolean getAllowsChildren() {
        return (m_attribute.getSFType() == SFAttribute.COMPOUND);
    }


    /**
     *  Gets the leaf attribute of the SFNode object
     *
     *@return    The leaf value
     */
    public boolean isLeaf() {
        return (getChildCount() == 0);
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public Iterator children() {
        if (m_children == null) {
            return EMPTY_ENUMERATION;
        } else {
            return m_children.iterator();
        }
    }


    /**
     *  To make it mutable
     *
     *@param  child  Description of the Parameter
     *@param  index  Description of the Parameter
     */
    public void insert(SFNode child, int index) {

        throw new java.lang.UnsupportedOperationException("Method insert() not yet implemented.");
    }


    /**
     *  Description of the Method
     *
     *@param  index  Description of the Parameter
     */
    public void remove(int index) {

        throw new java.lang.UnsupportedOperationException("Method remove() not yet implemented.");
    }


    /**
     *  Description of the Method
     *
     *@param  node  Description of the Parameter
     */
    public void remove(SFNode node) {

        throw new java.lang.UnsupportedOperationException("Method remove() not yet implemented.");
    }


    /**
     *  Sets the userObject attribute of the SFNode object
     *
     *@param  object  The new userObject value
     */
    public void setUserObject(Object object) {

        throw new java.lang.UnsupportedOperationException("Method setUserObject() not yet implemented.");
    }


    /**
     *  Description of the Method
     */
    public void removeFromParent() {

        throw new java.lang.UnsupportedOperationException("Method removeFromParent() not yet implemented.");
    }


    /**
     *  Sets the parent attribute of the SFNode object
     *
     *@param  newParent  The new parent value
     */
    public void setParent(SFNode newParent) {
        this.m_parent = newParent;
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String toString() {
        if (this.m_attribute != null) {
            if (this.m_attribute.getValue() != null) {
                return this.m_attribute.getName() + " [" + m_attribute.getValue() + "]";
            } else {
                return this.m_attribute.getName();
            }
        }
        return null;
    }


    /**
     *  Description of the Class
     *
     *          sfJMX
     *   JMX-based Management Framework for SmartFrog Applications
     *       Hewlett Packard
 *
     *@version        1.0
     */
    private class InvokeAction implements PrivilegedExceptionAction {
        private String m_operation;
        private Object[] m_parameters;
        private String[] m_signature;


        /**
         *  Constructor for the InvokeAction object
         *
         *@param  operation   Description of the Parameter
         *@param  parameters  Description of the Parameter
         *@param  signature   Description of the Parameter
         */
        public InvokeAction(String operation, Object[] parameters, String[] signature) {
            m_operation = operation;
            m_parameters = parameters;
            m_signature = signature;
        }


        /**
         *  Main processing method for the InvokeAction object
         *
         *@return                Description of the Return Value
         *@exception  Exception  Description of the Exception
         */
        public Object run() throws Exception {
            return (m_browser.getMBeanServer().invoke(m_beanName, m_operation, m_parameters, m_signature));
        }
    }

}
