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
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import javax.management.*;

import org.smartfrog.services.jmx.common.*;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class SFPropertyPopupMenu extends JPopupMenu {

    /**
     *  The Main Frame
     */
    private MainFrame m_browser = null;

    /**
     *  The Object Name
     */
    private ObjectName m_mbeanName = null;

    /**
     *  The node of the tree over which the actions are performed
     */
    private SFNode m_sfNode = null;

    /**
     *  The table from which the selected row is obtained to perform the
     *  required action.
     */
    private JTable m_table = null;

    private boolean isSFAttribute = false;

    private int selectedRow = -1;

    private Class clazz = null;

    JMenuItem jApplyMenuItem = new JMenuItem();
    JMenuItem jRefreshMenuItem = new JMenuItem();
    JMenuItem jAccessMenuItem = new JMenuItem();
    JMenuItem jRemoveMenuItem = new JMenuItem();


    /**
     *  Constructor for the SFPropertyPopupMenu object
     *
     *@param  browser    Description of the Parameter
     *@param  mbeanName  Description of the Parameter
     *@param  node       Description of the Parameter
     *@param  table      Description of the Parameter
     */
    public SFPropertyPopupMenu(MainFrame browser, ObjectName mbeanName, SFNode node, JTable table) {
        try {
            m_browser = browser;
            m_mbeanName = mbeanName;
            m_sfNode = node;
            m_table = table;
            if (m_table.getModel() instanceof SFPropertyTableModel) {
                isSFAttribute = true;
            }
            selectedRow = m_table.getSelectedRow();
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    private void jbInit() throws Exception {
//    jApplyMenuItem = new JMenuItem();
//    jApplyMenuItem.setText("Apply");
//    jApplyMenuItem.addActionListener(new java.awt.event.ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        jApplyMenuItem_actionPerformed(e);
//      }
//    });
        jRefreshMenuItem = new JMenuItem();
        jRefreshMenuItem.setText("Refresh");
        jRefreshMenuItem.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jRefreshMenuItem_actionPerformed(e);
                }
            });
//    this.add(jApplyMenuItem);
        this.add(jRefreshMenuItem);

        if (isSFAttribute) {
            JMenuItem jAccessMenuItem = new JMenuItem();
            JMenuItem jRemoveMenuItem = new JMenuItem();
            jAccessMenuItem.setText("Access");
            jAccessMenuItem.addActionListener(
                new java.awt.event.ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        jAccessMenuItem_actionPerformed(e);
                    }
                });
            jRemoveMenuItem.setText("Remove");
            jRemoveMenuItem.addActionListener(
                new java.awt.event.ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        jRemoveMenuItem_actionPerformed(e);
                    }
                });
            this.add(jAccessMenuItem);
            this.add(jRemoveMenuItem);
        } else {
            clazz = Utilities.classFromString((String) m_table.getValueAt(selectedRow, 2));
            if ( clazz != null &&
                ((clazz.isPrimitive() && !clazz.getName().equals("boolean")) ||
                 (String.class.isAssignableFrom(clazz) || Number.class.isAssignableFrom(clazz)))) {
                JMenuItem jMonitorMenuItem = new JMenuItem();
                jMonitorMenuItem.setText("Monitor");
                jMonitorMenuItem.addActionListener(
                    new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            jMonitorMenuItem_actionPerformed(e);
                        }
                    });
                this.add(jMonitorMenuItem);
            }
        }
    }


//  void jApplyMenuItem_actionPerformed(ActionEvent e) {
//    m_table.getModel().setValueAt(m_table.getValueAt(selectedRow, 1), selectedRow, 1);
//  }

    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jRefreshMenuItem_actionPerformed(ActionEvent e) {
        try {
            if (isSFAttribute) {
                ((SFPropertyTableModel) m_table.getModel()).refresh(selectedRow);
            } else {
                ((PropertyTableModel) m_table.getModel()).refresh(selectedRow);
            }
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
     *@param  e  Description of the Parameter
     */
    void jAccessMenuItem_actionPerformed(ActionEvent e) {
        try {
            String attributeName = (String) m_table.getValueAt(selectedRow, 0);
            OperationDialog attrDialog = new OperationDialog(m_browser, m_sfNode, "sfChangeAccess", attributeName, true);
            attrDialog.show();
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
     *@param  e  Description of the Parameter
     */
    void jRemoveMenuItem_actionPerformed(ActionEvent e) {
        try {
            ((SFPropertyTableModel) m_table.getModel()).removeRow(selectedRow);
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
     *@param  e  Description of the Parameter
     */
    void jMonitorMenuItem_actionPerformed(ActionEvent e) {
        try {
            String attribute = (String) m_table.getValueAt(selectedRow, 0);
            MonitorDialog monitorDialog = new MonitorDialog(m_browser, m_mbeanName, attribute, clazz, true);
            monitorDialog.show();
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

}
