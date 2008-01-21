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
public class QueryPopupMenu extends JPopupMenu {
    JMenuItem jMenuUnregister = new JMenuItem();

    /**
     *  The Main Frame
     */
    private MainFrame m_browser = null;

    /**
     *  The node of the tree over which to perform the actions
     */
    private ObjectName m_objectname = null;


    /**
     *  Constructor for the QueryPopupMenu object
     *
     *@param  frame  Description of the Parameter
     *@param  mbean  Description of the Parameter
     */
    public QueryPopupMenu(MainFrame frame, ObjectName mbean) {
        m_browser = frame;
        m_objectname = mbean;
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
        jMenuUnregister.setText("Unregister");
        jMenuUnregister.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuUnregister_actionPerformed(e);
                }
            });
        this.add(jMenuUnregister);
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jMenuUnregister_actionPerformed(ActionEvent e) {
        try {
            m_browser.getMBeanServer().unregisterMBean(m_objectname);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(m_browser, ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
