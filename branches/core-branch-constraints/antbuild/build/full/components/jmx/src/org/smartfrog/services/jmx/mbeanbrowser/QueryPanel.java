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

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.management.*;
import org.smartfrog.services.jmx.communication.ConnectorClient;
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
public class QueryPanel extends JPanel implements ListSelectionListener, ActionListener {
    /**
     *  The class name.
     */
    public final static String CLASS = QueryPanel.class.getName();

    /**
     *  TODO
     */
    private MainFrame m_browser = null;

    /**
     *  TODO
     */
    private JList m_list = null;

    /**
     *  TODO
     */
    private DefaultListModel m_model = null;

    /**
     *  TODO
     */
    private JTextField m_query = null;

    /**
     *  TODO
     */
    private JButton m_requery = null;

    /**
     *  TODO
     */
    private ObjectName m_mbeanName = null;

    /**
     *  The Border Layout manager
     */
    private BorderLayout borderLayout = new BorderLayout();


    /**
     *  Constructor for the QueryPanel object
     *
     *@param  browser  Description of the Parameter
     */
    public QueryPanel(MainFrame browser) {
        //super(new BorderLayout());
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
        setPreferredSize(new Dimension(450, 300));
        //setSize( new Dimension( 450, 300 ) );
        m_query = new JTextField("*:*");
        m_query.addActionListener(this);
        add(m_query, BorderLayout.NORTH);
        m_model = new DefaultListModel();
        m_list = new JList();
        m_list.addListSelectionListener(this);
        m_list.addMouseListener(new QueryPanel_m_list_mouseAdapter(this));
        m_list.setModel(m_model);
        add(new JScrollPane(m_list), BorderLayout.CENTER);
        m_requery = new JButton("Requery");
        m_requery.addActionListener(this);
        add(m_requery, BorderLayout.SOUTH);
    }


    /**
     *  Sets the MBean ObjectName for this Panel and creates the list with the
     *  ObjectNames requested in the query.
     *
     *@param  mbeanName  the MBean name
     */
    public void setMBean(ObjectName mbeanName) {
        int index;

        if (mbeanName == null) {
            m_list.setSelectedIndex(-1);
        } else {
            index = m_model.indexOf(mbeanName);
            m_list.setSelectedIndex(index);
        }
        m_mbeanName = mbeanName;
    }


    /**
     *  Creates an empty table
     */
    public void clear() {
        m_model.clear();
    }


    /**
     *  TODO
     *
     *@return  Object Name
     */
    public ObjectName getMBean() {
        ObjectName name = null;
        int index;

        index = m_list.getSelectedIndex();
        if (index >= 0) {
            name = (ObjectName) m_model.getElementAt(index);
        }
        return (name);
    }


    /**
     *  TODO
     */
    public void requery() {
        ConnectorClient server;
        //ObjectName  selectedObjectName;
        String query;
        ObjectName objectName;
        Set beanSet;
        TreeSet sortedBeanSet;
        Iterator iterator;

        try {
            server = m_browser.getMBeanServer();
            if (server != null) {
                //selectedObjectName = getMBean();
                query = m_query.getText();
                objectName = new ObjectName(query);
                beanSet = server.queryNames(objectName, null);
                m_model.clear();
                sortedBeanSet = new TreeSet(Utilities.getComparator(Utilities.STRING_COMPARATOR));
                sortedBeanSet.addAll(beanSet);
                iterator = sortedBeanSet.iterator();
                while (iterator.hasNext()) {
                    m_model.addElement(iterator.next());
                }
                //m_browser.setMBean(selectedObjectName);
            } else {
                m_model.clear();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.m_browser, e
            /*
             *  .getLocalizedMessage()
             */
                    );
            //e.printStackTrace();
        }
    }


    /**
     *  TODO
     *
     *@param  event
     */
    public void actionPerformed(ActionEvent event) {
        requery();
    }


    /**
     *  TODO
     *
     *@param  event
     */
    public void valueChanged(ListSelectionEvent event) {
        if (!event.getValueIsAdjusting()) {
            m_browser.setMBean(getMBean());
        }
        m_browser.statusBar.setText("");
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void m_list_mouseClicked(MouseEvent e) {
        if (e.getModifiers() == Event.META_MASK) {
            int row = m_list.locationToIndex(e.getPoint());
            m_list.setSelectedIndex(row);
            QueryPopupMenu queryPopup = new QueryPopupMenu(m_browser, getMBean());
            queryPopup.show(m_list, e.getX(), e.getY());
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
static class QueryPanel_m_list_mouseAdapter extends java.awt.event.MouseAdapter {


    QueryPanel adaptee;


    /**
     *  Constructor for the QueryPanel_m_list_mouseAdapter object
     *
     *@param  adaptee  Description of the Parameter
     */
    QueryPanel_m_list_mouseAdapter(QueryPanel adaptee) {
        this.adaptee = adaptee;
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    public void mouseClicked(MouseEvent e) {
        adaptee.m_list_mouseClicked(e);
    }
}
}
