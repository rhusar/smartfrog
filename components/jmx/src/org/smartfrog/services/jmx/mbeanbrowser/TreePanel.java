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
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.management.*;
import javax.management.modelmbean.*;
import org.smartfrog.services.jmx.common.SFAttribute;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.event.*;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class TreePanel extends JPanel implements TreeSelectionListener {
    /**
     *  The class name.
     */
    public final static String CLASS = TreePanel.class.getName();

    /**
     *  The Main Frame of this application
     */
    private MainFrame m_browser = null;

    /**
     *  TODO JavaDoc field m_beanName
     */
    private ObjectName m_beanName = null;

    private JTree m_tree = new JTree();

    private BorderLayout borderLayout1 = new BorderLayout();

    private JScrollPane jScrollPane1 = new JScrollPane();


    /**
     *  Constructor for the TreePanel object
     *
     *@param  browser  Description of the Parameter
     */
    public TreePanel(MainFrame browser) {
        m_browser = browser;
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        m_tree.addMouseListener(new TreePanel_m_tree_mouseAdapter(this));
        this.add(jScrollPane1, BorderLayout.CENTER);
        m_tree.addTreeSelectionListener(this);
        m_tree.setModel(new DeployTreeModel(new SFNode()));
        jScrollPane1.getViewport().add(m_tree, null);
    }


    /**
     *  Sets the mBean attribute of the TreePanel object
     *
     *@param  name  The new mBean value
     */
    public void setMBean(ObjectName name) {
        if (name == null) {
            this.clear();
        } else {
		System.out.println("Setting Name for Tree Panel=====" + name.toString());
            try {
                m_beanName = name;
                ObjectInstance objectInstance = m_browser.getMBeanServer().getObjectInstance(m_beanName);
                Class sfMBeanClass = Class.forName("org.smartfrog.services.jmx.modelmbean.SFModelMBean");
		System.out.println("SFMBeanClass in Tree Panel=====" + sfMBeanClass.toString());
                Class mbeanClass = Class.forName(objectInstance.getClassName());
		System.out.println("Class for MBean in Tree Panel=====" + mbeanClass.toString());
                if (sfMBeanClass.isAssignableFrom(mbeanClass)) {
		System.out.println("Classes match");
		ModelMBeanInfo info = (ModelMBeanInfo) m_browser.getMBeanServer().getMBeanInfo(m_beanName);
		ModelMBeanOperationInfo opInfo = info.getOperation("sfGetRoot");
	         	
		System.out.println("Info====" + opInfo.toString());
                 //   if (((ModelMBeanInfo) m_browser.getMBeanServer().getMBeanInfo(m_beanName)).getOperation("sfGetRoot") != null) {
                        SFAttribute root = (SFAttribute) m_browser.doAction(new InvokeAction("sfGetRoot", new Object[0], new String[0]));
			System.out.println("TreePanel Name-=====" + m_beanName.toString());
                        m_tree.setModel(new DeployTreeModel(new SFNode(m_browser, m_beanName, null, root)));

                 //   }
                } else {
                   this.clear();
                }
            } catch (InstanceNotFoundException infe) {
                JOptionPane.showMessageDialog(this.m_browser, infe);
                m_browser.queryPanel.requery();
                m_browser.propertyPanel.clear();
                m_browser.operationPanel.clear();
                m_browser.treePanel.clear();
                m_browser.setTitle("JMX Browser");
            } catch (Throwable throwable) {
                // throwable.printStackTrace();
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
                JOptionPane.showMessageDialog(this.m_browser, rootCause, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /**
     *  Rebuild the tree
     */
    public void refresh() {
        this.setMBean(this.m_beanName);
        this.m_browser.setTitle("");
    }


    /**
     *  Creates an empty tree
     */
    public void clear() {
        this.m_tree.setModel(new DeployTreeModel());
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    public void valueChanged(TreeSelectionEvent e) {
        SFNode node = (SFNode) m_tree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        m_browser.setSFComponent(node);
        m_browser.statusBar.setText("");
    }


    /**
     *  Description of the Method
     *
     *@param  operation      Description of the Parameter
     *@param  parameters     Description of the Parameter
     *@param  signature      Description of the Parameter
     *@return                Description of the Return Value
     *@exception  Exception  Description of the Exception
     */
    public Object invokeAction(String operation, Object[] parameters, String[] signature) throws Exception {
        return m_browser.doAction(new InvokeAction(operation, parameters, signature));
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


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void m_tree_mouseClicked(MouseEvent e) {
        if (e.getModifiers() == Event.META_MASK) {
            int row = m_tree.getRowForLocation(e.getX(), e.getY());
            m_tree.setSelectionRow(row);
            //SFNode node = (SFNode) m_tree.getLastSelectedPathComponent();
	    DeployTreeModel model = (DeployTreeModel) m_tree.getModel();
	    System.out.println("Tree Panel model======" + model.toString());
            SFNode node = (SFNode) model.getRoot();
	    System.out.println("Tree Panel node======" + node.toString());
            //if (node == null || !node.isComponent()) return;
            TreePopupMenu treePopup = new TreePopupMenu(m_browser, node);
            treePopup.show(m_tree, e.getX(), e.getY());
        }
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
static class TreePanel_m_tree_mouseAdapter extends java.awt.event.MouseAdapter {


    TreePanel adaptee;


    /**
     *  Constructor for the TreePanel_m_tree_mouseAdapter object
     *
     *@param  adaptee  Description of the Parameter
     */
    TreePanel_m_tree_mouseAdapter(TreePanel adaptee) {
        this.adaptee = adaptee;
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    public void mouseClicked(MouseEvent e) {
        adaptee.m_tree_mouseClicked(e);
    }
}
}
