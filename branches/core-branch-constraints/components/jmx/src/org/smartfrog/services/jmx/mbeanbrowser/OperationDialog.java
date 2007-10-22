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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.management.*;
import javax.management.modelmbean.*;

import org.smartfrog.services.jmx.common.Utilities;
import javax.swing.border.*;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class OperationDialog extends JDialog {
    JPanel panel1 = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();

    JTable m_parametersTable = new JTable();
    JScrollPane jScrollPane = new JScrollPane();
    JButton jOKButton = new JButton();
    JButton jCancelButton = new JButton();

    MainFrame m_browser = null;
    SFNode m_sfNode = null;
    MBeanOperationInfo opInfo = null;
    String m_attribute = null;
    TitledBorder titledBorder1;


    /**
     *  Constructor for the OperationDialog object
     *
     *@param  frame          Description of the Parameter
     *@param  node           Description of the Parameter
     *@param  title          Description of the Parameter
     *@param  attribute      Description of the Parameter
     *@param  modal          Description of the Parameter
     *@exception  Exception  Description of the Exception
     */
    public OperationDialog(MainFrame frame, SFNode node, String title, String attribute, boolean modal) throws Exception {
        super(frame, title, modal);
        try {
            m_browser = frame;
            m_sfNode = node;
            m_attribute = attribute;
            setParameterTable(title);
            // Uses the title as the name of the operation to invoke
            jbInit();
            pack();
            setLocationRelativeTo(frame);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     *  Constructor for the OperationDialog object
     *
     *@exception  Exception  Description of the Exception
     */
    public OperationDialog() throws Exception {
        this(null, null, "", null, false);
    }


    /**
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    void jbInit() throws Exception {
        titledBorder1 = new TitledBorder("");
        panel1.setLayout(gridBagLayout1);
        this.addKeyListener(
            new java.awt.event.KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    this_keyPressed(e);
                }
            });
        jOKButton.setMaximumSize(new Dimension(75, 25));
        jOKButton.setMinimumSize(new Dimension(75, 25));
        jOKButton.setPreferredSize(new Dimension(75, 25));
        jOKButton.setText("OK");
        jOKButton.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jOKButton_actionPerformed(e);
                }
            });
        jCancelButton.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jCancelButton_actionPerformed(e);
                }
            });
        jCancelButton.setText("Cancel");
        jCancelButton.setPreferredSize(new Dimension(75, 25));
        jCancelButton.setMinimumSize(new Dimension(75, 25));
        jCancelButton.setMaximumSize(new Dimension(75, 25));
        panel1.setBorder(titledBorder1);
        panel1.setMaximumSize(new Dimension(380, 200));
        panel1.setMinimumSize(new Dimension(380, 200));
        panel1.setPreferredSize(new Dimension(380, 200));
        jScrollPane.setMaximumSize(new Dimension(350, 116));
        jScrollPane.setMinimumSize(new Dimension(350, 116));
        jScrollPane.setPreferredSize(new Dimension(350, 116));
        titledBorder1.setTitle("Parameters");
        getContentPane().add(panel1);
        jScrollPane.getViewport().add(m_parametersTable);
        panel1.add(jScrollPane, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 0, 15), 0, 0));
        panel1.add(jOKButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(15, 50, 15, 53), 0, 0));
        panel1.add(jCancelButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(15, 0, 15, 50), 0, 0));
    }


    /**
     *  Sets the parameterTable attribute of the OperationDialog object
     *
     *@param  opName         The new parameterTable value
     *@exception  Exception  Description of the Exception
     */
    private void setParameterTable(String opName) throws Exception {
        opInfo = ((ModelMBeanInfo) m_browser.getMBeanServer().getMBeanInfo(m_sfNode.m_beanName)).getOperation(opName);
        m_parametersTable.setModel(new ParameterTableModel(opInfo));
        m_browser.statusBar.setText(opInfo.getDescription());

        if (opName.equals("sfChangeAccess")) {
            m_parametersTable.setValueAt(m_attribute, 0, 2);
        }

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
        m_parametersTable.sizeColumnsToFit(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }


    /**
     *  Overridden so we can exit when window is closed
     *
     *@param  e  Description of the Parameter
     */
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            this.dispose();
        }
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jOKButton_actionPerformed(ActionEvent e) {
        try {
            ParameterTableModel model = (ParameterTableModel) m_parametersTable.getModel();
            MBeanParameterInfo parameterInfo[] = opInfo.getSignature();
            Object parameters[] = new Object[parameterInfo.length];
            String signature[] = new String[parameterInfo.length];
            String type = null;
            Object value = null;
            Object result = null;
            for (int i = 0; i < parameterInfo.length; i++) {
                type = parameterInfo[i].getType();
                value = model.getValueAt(i, 2);
                if (i == 0 && !opInfo.getName().equals("sfChangeAccess") && type.equals("java.lang.String")) {
                    // && parameterInfo[i].getName().endsWith("Path")) {
                    String path = m_sfNode.getPath();
                    if ((path != null) && (!path.equals(""))) {
                        path += ":";
                    }
                    value = path + value.toString();
                }
                parameters[i] = Utilities.objectFromString(type, value);
                signature[i] = type;
            }
            result = m_browser.treePanel.invokeAction(opInfo.getName(), parameters, signature);
            if (opInfo.getReturnType().equals("void")) {
                JOptionPane.showMessageDialog(m_browser, "Done", opInfo.getName(), JOptionPane.INFORMATION_MESSAGE);
            } else {
                if ((result != null) && (result.getClass().isArray())) {
                    StringBuffer buf = new StringBuffer();
                    for (int i = 0; i < Array.getLength(result); i++) {
                        buf.append(i);
                        buf.append("=");
                        buf.append(Array.get(result, i));
                        buf.append(System.getProperty("line.separator", "\n"));
                    }
                    result = buf.toString();
                }
                JOptionPane.showMessageDialog(m_browser, result, opInfo.getName(), JOptionPane.INFORMATION_MESSAGE);
            }
            //m_browser.treePanel.refresh();
            m_sfNode.refresh();
            m_browser.setSFComponent(m_sfNode);
            m_browser.statusBar.setText("");
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(m_browser, ex, opInfo.getName(), JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jCancelButton_actionPerformed(ActionEvent e) {
        m_browser.statusBar.setText("");
        this.dispose();
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void this_keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_ENTER) {
            if (jOKButton.hasFocus()) {
                jOKButton_actionPerformed(null);
            } else if (jCancelButton.hasFocus()) {
                jCancelButton_actionPerformed(null);
            }
        }
    }

}
