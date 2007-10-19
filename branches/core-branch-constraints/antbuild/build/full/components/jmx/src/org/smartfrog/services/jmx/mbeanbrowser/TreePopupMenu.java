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

import java.security.*;
import java.lang.reflect.*;
import javax.swing.*;
import java.awt.event.*;
import javax.management.*;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class TreePopupMenu extends JPopupMenu {

    /**
     *  The Main Frame
     */
    private MainFrame m_browser = null;

    /**
     *  The node of the tree over which to perform the actions
     */
    private SFNode m_sfNode = null;

    JMenuItem jAddMenuItem = null;
    JMenuItem jDeployMenuItem = null;
    JMenuItem jStartMenuItem = null;
    JMenuItem jDetachMenuItem = null;
    JMenuItem jTerminateMenuItem = null;
    JMenuItem jDetachTermMenuItem = null;


    /**
     *  Constructor for the TreePopupMenu object
     *
     *@param  browser  Description of the Parameter
     *@param  node     Description of the Parameter
     */
    public TreePopupMenu(MainFrame browser, SFNode node) {
        m_browser = browser;
        m_sfNode = node;
        try {
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
        jAddMenuItem = new JMenuItem();
        jAddMenuItem.setText("Add Attribute");
        jAddMenuItem.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jAddMenuItem_actionPerformed(e);
                }
            });
        this.add(jAddMenuItem);

        if (m_sfNode.isComponent()) {
            jDeployMenuItem = new JMenuItem();
            jStartMenuItem = new JMenuItem();
            jDetachMenuItem = new JMenuItem();
            jTerminateMenuItem = new JMenuItem();
            jDetachTermMenuItem = new JMenuItem();

            jDeployMenuItem.setText("Deploy Component");
            jDeployMenuItem.addActionListener(
                new java.awt.event.ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        jDeployMenuItem_actionPerformed(e);
                    }
                });
            jStartMenuItem.setText("Start Component");
            jStartMenuItem.addActionListener(
                new java.awt.event.ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        jStartMenuItem_actionPerformed(e);
                    }
                });
            jTerminateMenuItem.setText("Terminate Component");
            jTerminateMenuItem.addActionListener(
                new java.awt.event.ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        jTerminateMenuItem_actionPerformed(e);
                    }
                });
            jDetachMenuItem.setText("Detach Component");
            jDetachMenuItem.addActionListener(
                new java.awt.event.ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        jDetachMenuItem_actionPerformed(e);
                    }
                });
            jDetachTermMenuItem.setText("Detach and Terminate Component");
            jDetachTermMenuItem.addActionListener(
                new java.awt.event.ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        jDetachTermMenuItem_actionPerformed(e);
                    }
                });
            this.add(jDeployMenuItem);
            this.add(jStartMenuItem);
            this.add(jTerminateMenuItem);
            this.add(jDetachMenuItem);
            this.add(jDetachTermMenuItem);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jAddMenuItem_actionPerformed(ActionEvent e) {
        try {
		System.out.println("SFNODE====" + m_sfNode.toString());
            OperationDialog attrDialog = new OperationDialog(m_browser, m_sfNode, "sfAddAttribute", null, true);
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
    void jDeployMenuItem_actionPerformed(ActionEvent e) {
        try {
            Object[] sfParameters = new Object[]{m_sfNode.getPath(), "sfDeploy", null, null};
            String[] sfSignature = new String[]{"java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String"};
            m_browser.treePanel.invokeAction("sfInvokeMethod", sfParameters, sfSignature);
            JOptionPane.showMessageDialog(m_browser, "Done");
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
    void jStartMenuItem_actionPerformed(ActionEvent e) {
        try {
            Object[] sfParameters = new Object[]{m_sfNode.getPath(), "sfStart", null, null};
            String[] sfSignature = new String[]{"java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String"};
            m_browser.treePanel.invokeAction("sfInvokeMethod", sfParameters, sfSignature);
            JOptionPane.showMessageDialog(m_browser, "Done");
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
    void jTerminateMenuItem_actionPerformed(ActionEvent e) {
        try {
            Object[] sfParameters = new Object[]{m_sfNode.getPath(), "sfTerminate", "External Management Action", "org.smartfrog.sfcore.prim.TerminationRecord"};
            String[] sfSignature = new String[]{"java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String"};
            m_browser.treePanel.invokeAction("sfInvokeMethod", sfParameters, sfSignature);
            JOptionPane.showMessageDialog(m_browser, "Done");
            //m_browser.treePanel.refresh();
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
    void jDetachMenuItem_actionPerformed(ActionEvent e) {
        try {
            Object[] sfParameters = new Object[]{m_sfNode.getPath(), "sfDetach", null, null};
            String[] sfSignature = new String[]{"java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String"};
            m_browser.treePanel.invokeAction("sfInvokeMethod", sfParameters, sfSignature);
            JOptionPane.showMessageDialog(m_browser, "Done");
            m_browser.treePanel.refresh();
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
    void jDetachTermMenuItem_actionPerformed(ActionEvent e) {
        try {
            Object[] sfParameters = new Object[]{m_sfNode.getPath(), "sfDetachAndTerminate", "External Management Action", "org.smartfrog.sfcore.prim.TerminationRecord"};
            String[] sfSignature = new String[]{"java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String"};
            m_browser.treePanel.invokeAction("sfInvokeMethod", sfParameters, sfSignature);
            JOptionPane.showMessageDialog(m_browser, "Done");
            m_browser.treePanel.refresh();
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
