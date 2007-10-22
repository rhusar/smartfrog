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
import javax.management.*;
import javax.management.monitor.*;
import javax.swing.*;
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
public class NotificationFrame extends JFrame implements NotificationViewer {
    Hashtable notifHashtable = null;
    int notifNumber = 0;
    Vector columnVector = new Vector();
    Vector rowVector = new Vector();

    JPanel jPanel = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JButton jCloseButton = new JButton();
    JButton jDetailButton = new JButton();
    JButton jClearButton = new JButton();
    JScrollPane jScrollPane = new JScrollPane();
    JTable notifTable = null;
    String m_title = "Notifications";


    /**
     *  Constructor for the NotificationFrame object
     *
     *@param  title  Description of the Parameter
     */
    public NotificationFrame(String title) {
        m_title = title;
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
        setIconImage(Toolkit.getDefaultToolkit().createImage(NotificationFrame.class.getResource("Frog.gif")));

        columnVector.addElement("Sequence");
        columnVector.addElement("Type");
        columnVector.addElement("Source");
        columnVector.addElement("Time Stamp");
        columnVector.addElement("Message");
        columnVector.addElement("User Data");
        notifTable =
            new JTable(rowVector, columnVector) {
                public boolean isCellEditable(int i, int j) {
                    return false;
                }
            };

        jPanel.setLayout(gridBagLayout1);
        if (m_title == null || m_title.equals("")) {
            this.setTitle("Notifications");
        } else {
            this.setTitle(m_title);
        }
        jCloseButton.setText("Close");
        jCloseButton.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jCloseButton_actionPerformed(e);
                }
            });
        jDetailButton.setEnabled(false);
        jDetailButton.setText("Detail");
        jDetailButton.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jDetailButton_actionPerformed(e);
                }
            });
        jClearButton.setText("Clear");
        jClearButton.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jClearButton_actionPerformed(e);
                }
            });
        jPanel.setMinimumSize(new Dimension(625, 325));
        this.getContentPane().add(jPanel, BorderLayout.CENTER);
        jScrollPane.getViewport().add(notifTable, null);
        jPanel.add(jScrollPane, new GridBagConstraints(8, 0, 15, 1, 1.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 15, 5), 455, 141));
        jPanel.add(jCloseButton, new GridBagConstraints(8, 1, 1, 1, 1.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 30, 0, 30), 0, 0));
        jPanel.add(jClearButton, new GridBagConstraints(22, 1, 1, 1, 1.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 30, 0, 30), 0, 0));
        jPanel.add(jDetailButton, new GridBagConstraints(21, 1, 1, 1, 1.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 30, 0, 30), 0, 0));
    }


    /**
     *  Adds a feature to the Notification attribute of the NotificationFrame
     *  object
     *
     *@param  notification  The feature to be added to the Notification
     *      attribute
     */
    public void addNotification(Notification notification) {
        if (notifHashtable == null) {
            notifHashtable = new Hashtable();
            jDetailButton.setEnabled(true);
        }
        notifHashtable.put(String.valueOf(notifNumber++), notification);
        Vector row = new Vector();
        row.addElement(String.valueOf(notification.getSequenceNumber()));
        row.addElement(notification.getType());
        row.addElement(notification.getSource().toString());
        row.addElement((new Date(notification.getTimeStamp())).toString());
        row.addElement(notification.getMessage());
        String userData = "";
        if (notification.getUserData() != null) {
            userData = notification.getUserData().toString();
        }
        row.addElement(userData.toString());
        rowVector.addElement(row);
        notifTable.validate();
        notifTable.updateUI();
    }


    /**
     *  Description of the Method
     */
    private void showDetailDialog() {
        String message = null;
        String title = null;
        String userdata = null;
        int i = notifTable.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "Select a row", "Alert", 2);
            return;
        }
        Notification notification = (Notification) notifHashtable.get(String.valueOf(i));
        if (notification != null) {
            if (notification instanceof MBeanServerNotification) {
                MBeanServerNotification mbeanservernotification = (MBeanServerNotification) notification;
                String type = "";
                if (mbeanservernotification.getType().equals(MBeanServerNotification.REGISTRATION_NOTIFICATION)) {
                    type = "Registered";
                } else
                        if (mbeanservernotification.getType().equals(MBeanServerNotification.UNREGISTRATION_NOTIFICATION)) {
                    type = "Unregistered";
                }
                message = String.valueOf(mbeanservernotification.getMBeanName()) + "\n" + "has been " + type;
                title = "MBeanServerNotification";
            } else if (notification instanceof AttributeChangeNotification) {
                AttributeChangeNotification attributechangenotification = (AttributeChangeNotification) notification;
                message = attributechangenotification.getAttributeName() + "\n\n" + "Old value: " + attributechangenotification.getOldValue().toString() + "\nNew value: " + attributechangenotification.getNewValue().toString();
                title = "AttributeChangeNotification";
            } else if (notification instanceof MonitorNotification) {
                MonitorNotification monNotif = (MonitorNotification) notification;
                message = "Observed Object: " + monNotif.getObservedObject() + "\n" +
                        "Attribute: " + monNotif.getObservedAttribute() + "\n" +
                        "Derived Gauge: " + monNotif.getDerivedGauge() + "\n" +
                        "Trigger: " + monNotif.getTrigger();
                title = "MonitorNotification";
            } else {
                if (notification.getUserData() != null) {
                    userdata = notification.getUserData().toString();
                }
                message = notification.getMessage() + "\nUser data: " + userdata;
                title = "Notification";
            }
        } else {
            return;
        }

        JOptionPane.showMessageDialog(this, message, title, 1);
        notification = null;
    }


    /**
     *  Description of the Method
     */
    private void clear() {
        rowVector.removeAllElements();
        notifTable.validate();
        notifTable.updateUI();
        if (notifHashtable != null) {
            notifHashtable.clear();
        }
        notifHashtable = null;
        notifNumber = 0;
        jDetailButton.setEnabled(false);
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jDetailButton_actionPerformed(ActionEvent e) {
        showDetailDialog();
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jClearButton_actionPerformed(ActionEvent e) {
        clear();
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jCloseButton_actionPerformed(ActionEvent e) {
        setVisible(false);
    }

}
