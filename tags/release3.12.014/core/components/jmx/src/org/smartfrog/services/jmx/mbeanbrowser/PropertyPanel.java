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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.management.*;

/**
 *  Swing property Panel
 *
 *
 *@version        1.0
 */
public class PropertyPanel extends JPanel implements ActionListener, FocusListener, ListSelectionListener {

    /**
     *  The class name.
     */
    public final static String CLASS = PropertyPanel.class.getName();

    /**
     *  TODO
     */
    private MainFrame m_browser = null;

    /**
     *  TODO
     */
    private JTable m_table = null;

    /**
     *  TODO
     */
    private JButton m_apply = null;

    /**
     *  TODO
     */
    private JButton m_refresh = null;

    /**
     *  TODO
     */
    private ObjectName m_mbeanName = null;

    /**
     *  TODO
     */
    private SFNode m_sfNode = null;

    /**
     *  The GridBag Layout manager
     */
    private GridBagLayout gridBagLayout = new GridBagLayout();


    /**
     *  Constructor for the PropertyPanel object
     *
     *@param  browser  Description of the Parameter
     */
    public PropertyPanel(MainFrame browser) {
        //super( new GridBagLayout() );
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
        this.setLayout(gridBagLayout);
        setMinimumSize(new Dimension(200, 300));
        GridBagConstraints constraints = new GridBagConstraints();
        m_table = new JTable();
        constraints.fill = constraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.weightx = 1.0d;
        constraints.weighty = 1.0d;
        m_table.addMouseListener(new PropertyPanel_m_table_mouseAdapter(this));
        add(new JScrollPane(m_table), constraints);
        m_apply = new JButton("Apply");
        m_apply.addActionListener(this);
        constraints.fill = constraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.5d;
        constraints.weighty = 0.0d;
        //kam[  Put back in when it works.
        add(m_apply, constraints);
        //kam]
        m_refresh = new JButton("Refresh");
        m_refresh.addActionListener(this);
        constraints.fill = constraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.5d;
        constraints.weighty = 0.0d;
        add(m_refresh, constraints);

        m_table.getSelectionModel().addListSelectionListener(this);
    }


    /**
     *  Sets the MBean ObjectName for this Panel and creates the table with the
     *  properties of this MBean
     *
     *@param  mbeanName  the MBean name
     */
    public void setMBean(ObjectName mbeanName) {
        m_mbeanName = mbeanName;
        if (mbeanName == null) {
            this.clear();
        } else {
            m_table.setModel(new PropertyTableModel(m_browser, mbeanName));
            //m_table.sizeColumnsToFit( 0 );

            TableModel tm = m_table.getModel();
            int rowNumber = tm.getRowCount();

            boolean[] flags = new boolean[rowNumber];
            for (int i = 0; i < flags.length; i++) {
                if ("java.lang.Boolean".equals(tm.getValueAt(i, 2)) || "boolean".equals(tm.getValueAt(i, 2))) {
                    flags[i] = true;
                } else {
                    flags[i] = false;
                }
            }
            m_table.getColumnModel().getColumn(1).setCellEditor(new BooleanCellEditor(new JTextField(), flags));
            m_table.sizeColumnsToFit(JTable.AUTO_RESIZE_ALL_COLUMNS);
        }
    }


    /**
     *  Sets the SF Component Path for this Panel and creates the table with the
     *  properties of this Component
     *
     *@param  node       The new sFComponent value
     */
    public void setSFComponent(SFNode node) {
        try {
            m_sfNode = node;
            if (node == null) {
                this.clear();
            } else {
                m_table.setModel(new SFPropertyTableModel(m_browser, m_mbeanName, m_sfNode));

                boolean[] flags = new boolean[m_table.getModel().getRowCount()];
                for (int i = 0; i < flags.length; i++) {
                    if (((String) m_table.getValueAt(i, 2)).equals("java.lang.Boolean") || ((String) m_table.getValueAt(i, 2)).equals("boolean")) {
                        flags[i] = true;
                    } else {
                        flags[i] = false;
                    }
                }
                m_table.getColumnModel().getColumn(1).setCellEditor(new BooleanCellEditor(new JTextField(), flags));
                m_table.sizeColumnsToFit(JTable.AUTO_RESIZE_ALL_COLUMNS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.m_browser, e);
        }
    }


    /**
     *  Creates an empty table
     */
    public void clear() {
        this.m_table.setModel(new DefaultTableModel());
    }


    /**
     *  TODO
     *
     *@param  event
     */
    public void actionPerformed(ActionEvent event) {
        TableCellEditor tce;

        if (event.getSource() == m_refresh) {
            if (m_table.getModel() instanceof PropertyTableModel) {
                setMBean(m_mbeanName);
            } else {
                m_sfNode.refresh();
                setSFComponent(m_sfNode);
            }
        } else {
            if (m_table.isEditing()) {
                // System.out.println(event);
                tce = m_table.getCellEditor();
                if (tce != null) {
                    tce.stopCellEditing();
                }
            }
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

        if (m_table.isEditing()) {
            tce = m_table.getCellEditor();
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
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    public void valueChanged(ListSelectionEvent e) {
        int row = m_table.getSelectedRow();
        TableModel tm = m_table.getModel();
        String desc = "";
        if (tm instanceof PropertyTableModel) {
            desc = ((PropertyTableModel) tm).getDescription(row);
        } else if (tm instanceof SFPropertyTableModel) {
            desc = ((SFPropertyTableModel) tm).getDescription(row);
        }
        m_browser.statusBar.setText(desc);
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void m_table_mouseClicked(MouseEvent e) {
        if (e.getModifiers() == Event.META_MASK) {
            if (m_table.isEditing()) {
                // System.out.println(event);
                TableCellEditor tce = m_table.getCellEditor();
                if (tce != null) {
                    tce.stopCellEditing();
                }
            }
            int row = m_table.rowAtPoint(new Point(e.getX(), e.getY()));
            m_table.setRowSelectionInterval(row, row);
            //if (node == null || !node.isComponent()) return;
            SFPropertyPopupMenu tablePopup = new SFPropertyPopupMenu(m_browser, this.m_mbeanName, m_sfNode, m_table);
            tablePopup.show(m_table, e.getX(), e.getY());
        }
    }


    /**
     * handle mouseclick events
     *
     * @version 1.0
     */
    static class PropertyPanel_m_table_mouseAdapter extends java.awt.event.MouseAdapter {


        PropertyPanel adaptee;


        /**
         * Constructor for the PropertyPanel_m_table_mouseAdapter object
         *
         * @param adaptee Description of the Parameter
         */
        PropertyPanel_m_table_mouseAdapter(PropertyPanel adaptee) {
            this.adaptee = adaptee;
        }


        /**
         * Description of the Method
         *
         * @param e Description of the Parameter
         */
        public void mouseClicked(MouseEvent e) {
            adaptee.m_table_mouseClicked(e);
        }
    }
}
