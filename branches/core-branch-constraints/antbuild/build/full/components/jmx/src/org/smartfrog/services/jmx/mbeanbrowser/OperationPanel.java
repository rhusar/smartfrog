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
import java.util.*;
import java.security.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.management.*;

import org.smartfrog.services.jmx.common.Utilities;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class OperationPanel extends JPanel implements ListSelectionListener, ActionListener, FocusListener {
    private BorderLayout borderLayout = new BorderLayout();

    /**
     *  TODO JavaDoc field m_browser
     */
    private MainFrame m_browser = null;

    /**
     *  TODO JavaDoc field CLASS
     */
    public final static String CLASS = OperationPanel.class.getName();

    /**
     *  TODO JavaDoc field m_beanName
     */
    private ObjectName m_beanName = null;

    /**
     *  TODO
     */
    private SFNode m_sfNode = null;

    /**
     *  TODO JavaDoc field m_operationsList
     */
    private JList m_operationsList = null;

    /**
     *  TODO JavaDoc field m_operationsModel
     */
    private DefaultListModel m_operationsModel = null;

    /**
     *  TODO JavaDoc field m_parametersTable
     */
    private JTable m_parametersTable = null;

    /**
     *  TODO JavaDoc field m_invokeButton
     */
    private JButton m_invokeButton = null;

    /**
     *  TODO JavaDoc field m_operationInfo[]
     */
    private MBeanOperationInfo m_operationInfo[] = null;

    /**
     *  TODO JavaDoc field m_sfOperations
     */
    private MBeanOperationInfo m_sfOperationInfo[] = null;

    /**
     *  Flag indicating if the events from the JList matches a SF Operation or a
     *  JMX Operation. It is updated each time the methods setMBean() and
     *  setSFComponent() are invoked.
     */
    private boolean isJMXOperation = true;


    /**
     *  Constructor for the OperationPanel object
     *
     *@param  browser  Description of the Parameter
     */
    public OperationPanel(MainFrame browser) {
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
        this.setLayout(borderLayout);
        setMinimumSize(new Dimension(200, 300));
        m_operationsModel = new DefaultListModel();
        m_operationsList = new JList();
        m_operationsList.setVisibleRowCount(10);
        m_operationsList.addListSelectionListener(this);
        m_operationsList.setModel(m_operationsModel);
        add(new JScrollPane(m_operationsList), BorderLayout.NORTH);
        m_parametersTable = new JTable();
        add(new JScrollPane(m_parametersTable), BorderLayout.CENTER);
        m_invokeButton = new JButton("Invoke");
        m_invokeButton.addActionListener(this);
        add(m_invokeButton, BorderLayout.SOUTH);
    }


    /**
     *  Sets the MBean ObjectName for this Panel and creates the list with the
     *  allowed operations in this MBean
     *
     *@param  name       The new mBean value
     */
    public void setMBean(ObjectName name) {
        isJMXOperation = true;
        TreeSet sortedSet;
        Iterator iterator;

        if (name == null) {
            this.clear();
        } else {
            try {
                m_beanName = name;
                m_operationInfo = m_browser.getMBeanServer().getMBeanInfo(name).getOperations();
                m_operationsModel.clear();
                sortedSet = new TreeSet(Utilities.getComparator(Utilities.OPERATION_COMPARATOR));
                for (int i = 0; i < m_operationInfo.length; i++) {
                    sortedSet.add(new OperationListElement(m_operationInfo[i]));
                }
                iterator = sortedSet.iterator();
                while (iterator.hasNext()) {
                    m_operationsModel.addElement(iterator.next());
                }
                m_parametersTable.setModel(new DefaultTableModel());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this.m_browser, e
                /*
                 *  .getLocalizedMessage()
                 */
                        );
            }
        }
    }


    /**
     *  Sets the MBean ObjectName for this Panel and creates the list with the
     *  allowed operations in this MBean
     *
     *@param  node       The new sFComponent value
     */
    public void setSFComponent(SFNode node) {
        isJMXOperation = false;
        TreeSet sortedSet;
        Iterator iterator;

        if (node == null || !node.isComponent()) {
            m_operationsModel.clear();
            return;
        }
        try {
            m_sfNode = node;
            m_sfOperationInfo = (MBeanOperationInfo[]) m_browser.doAction(
                    new InvokeAction("sfGetMethods", new Object[]{m_sfNode.getPath()}, new String[]{"java.lang.String"}));
            if (m_sfOperationInfo == null) {
                return;
            }
            m_operationsModel.clear();
            sortedSet = new TreeSet(Utilities.getComparator(Utilities.STRING_COMPARATOR));
            for (int i = 0; i < m_sfOperationInfo.length; i++) {
                sortedSet.add(new OperationListElement(m_sfOperationInfo[i]));
            }
            iterator = sortedSet.iterator();
            while (iterator.hasNext()) {
                m_operationsModel.addElement(iterator.next());
            }
            m_parametersTable.setModel(new DefaultTableModel());
        } catch (InstanceNotFoundException infe) {
            JOptionPane.showMessageDialog(this.m_browser, infe);
            m_browser.queryPanel.setMBean(null);
            m_browser.queryPanel.requery();
            m_browser.propertyPanel.setMBean(null);
            m_browser.operationPanel.setMBean(null);
            m_browser.treePanel.setMBean(null);
            m_browser.treePanel.clear();
            m_browser.setTitle("JMX Browser");
        } catch (Exception e) {
            //e.printStackTrace();
            JOptionPane.showMessageDialog(this.m_browser, e);
        }
    }


    /**
     *  Creates an empty table
     */
    public void clear() {
        m_operationsModel.clear();
        m_parametersTable.setModel(new DefaultTableModel());
    }



    /**
     *  Method of the interface ListSelectionListener.
     *
     *@param  e
     */
    public void valueChanged(ListSelectionEvent e) {
        OperationListElement selection = (OperationListElement) m_operationsList.getSelectedValue();
        if (selection != null) {
            MBeanOperationInfo opInfo = selection.getOperationInfo();
            m_parametersTable.setModel(new ParameterTableModel(opInfo));
            m_parametersTable.sizeColumnsToFit(0);
            m_browser.statusBar.setText(opInfo.getDescription());

            MBeanParameterInfo[] params = opInfo.getSignature();
            boolean[] flags = new boolean[params.length];
            for (int i = 0; i < params.length; i++) {
                if (params[i].getType().equals("java.lang.Boolean") || params[i].getType().equals("boolean")) {
                    flags[i] = true;
                } else {
                    flags[i] = false;
                }
            }
            m_parametersTable.getColumnModel().getColumn(2).setCellEditor(new BooleanCellEditor(new JTextField(), flags));
        } else {
            m_browser.statusBar.setText("");
        }
    }


    /**
     *  TODO
     *
     *@param  evnt
     *     MSG_DONE Done.
     *     MSG_ABORTED Aborted.
     *     MSG_RESULT Result = {0}.
     *     MSG_EXCEPTION Exception = {0}.
     */
    public void actionPerformed(ActionEvent evnt) {
        TableCellEditor tce;
        StringBuffer buf;

        try {
            if (m_parametersTable.isEditing()) {
                tce = m_parametersTable.getCellEditor();
                if (tce != null) {
                    tce.stopCellEditing();
                }
            }
            OperationListElement selection = (OperationListElement) m_operationsList.getSelectedValue();
            MBeanOperationInfo operationInfo = selection.getOperationInfo();
            ParameterTableModel model = (ParameterTableModel) m_parametersTable.getModel();
            MBeanParameterInfo parameterInfo[] = operationInfo.getSignature();
            Object parameters[] = new Object[parameterInfo.length];
            String signature[] = new String[parameterInfo.length];
            String type = null;
            Object value = null;
            Object result = null;
            for (int i = 0; i < parameterInfo.length; i++) {
                type = parameterInfo[i].getType();
                value = model.getValueAt(i, 2);
                parameters[i] = Utilities.objectFromString(type, value);
                signature[i] = type;
            }
            InvokeAction ia = null;
            if (isJMXOperation) {
                // Operation destined to the SFModelMBean, it is invoked over the JMXActivator
                ia = new InvokeAction(operationInfo.getName(), parameters, signature);
            } else {
                // Operation destined to the JMXActivator, it is invoked over the SF component
                String paramString = "";
                String signaString = "";
                for (int i = 0; i < parameters.length; i++) {
                    paramString = paramString + parameters[i];
                    signaString = signaString + signature[i];
                    if (i < (parameters.length)) {
                        paramString = paramString + ",";
                        signaString = signaString + ",";
                    }
                }
                Object[] sfParameters = new Object[]{m_sfNode.getPath(), operationInfo.getName(), paramString, signaString};
                String[] sfSignature = new String[]{"java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String"};
                ia = new InvokeAction("sfInvokeMethod", sfParameters, sfSignature);
            }
            //result = m_browser.getMBeanServer().invoke( m_beanName, operationInfo.getName(), parameters, signature );
            result = m_browser.doAction(ia);
            if (operationInfo.getReturnType().equals("void")) {
                JOptionPane.showMessageDialog(getParent(), "Done", operationInfo.getName(), JOptionPane.INFORMATION_MESSAGE);
            } else {
                if ((result != null) && (result.getClass().isArray())) {
                    buf = new StringBuffer();
                    for (int i = 0; i < Array.getLength(result); i++) {
                        buf.append(i);
                        buf.append("=");
                        buf.append(Array.get(result, i));
                        buf.append(System.getProperty("line.separator", "\n"));
                    }
                    result = buf.toString();
                }
                JOptionPane.showMessageDialog(getParent(), result, operationInfo.getName(), JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Throwable throwable) {
            //throwable.printStackTrace();
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
            JOptionPane.showMessageDialog(this.m_browser, rootCause
            /*
             *  +": "+rootCause.getLocalizedMessage()
             */
                    );
        }
    }


    /**
     *  Invoked when a component gains the keyboard focus.
     *
     *@param  e
     */
    public void focusGained(FocusEvent e) { }


    /**
     *  Invoked when a component loses the keyboard focus.
     *
     *@param  e
     */
    public void focusLost(FocusEvent e) {
        boolean validate = true;
        TableCellEditor tce = null;

        if (m_parametersTable.isEditing()) {
            tce = m_parametersTable.getCellEditor();
        }
        if (tce != null) {
            if (validate) {
                tce.stopCellEditing();
            } else {
                tce.cancelCellEditing();
            }
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
