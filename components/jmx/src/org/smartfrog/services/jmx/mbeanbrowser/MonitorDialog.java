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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.management.*;
import javax.management.modelmbean.*;

import org.smartfrog.services.jmx.communication.ConnectorClient;
import org.smartfrog.services.jmx.common.*;


/**
 *  Title: JMX Framework for SmartFrog Description: Creation of a management
 *  framework for SmartFrog applications using JMX technology. Copyright:
 *  Copyright (c) 2001 Company: Hewlett Packard
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */

public class MonitorDialog extends JDialog {
    MainFrame m_browser = null;
    String m_attribute = null;
    Class m_clazz = null;
    ObjectName m_mbeanName = null;

    final static int ENABLE = 0;
    final static int DISABLE = 1;

    // TargetPanel
    JPanel targetPanel = new JPanel();
    TitledBorder targetBorder;
    GridBagLayout gridBagLayout2 = new GridBagLayout();

    JLabel jObjectLabel = new JLabel();
    JLabel jObsObjectLabel = new JLabel();
    JLabel jAttrLabel = new JLabel();
    JLabel jObsAttrLabel = new JLabel();

    // MonitorPanel
    JPanel monitorPanel = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();

    // GeneralPanel
    JPanel generalPanel = new JPanel();
    TitledBorder generalBorder;
    GridBagLayout gridBagLayout1 = new GridBagLayout();

    JLabel jMonitorLabel = new JLabel();
    JLabel jGranularityLabel = new JLabel();

    JComboBox jMonitorComboBox = new JComboBox();
    JTextField jGranularityTextField = new JTextField();

    // Specific parameters panel
    JPanel specificPanel = new JPanel();
    TitledBorder specificBorder;
    GridBagLayout gridBagLayout4 = new GridBagLayout();

    // ButtonPanel
    JPanel buttonPanel = new JPanel();
    GridBagLayout gridBagLayout3 = new GridBagLayout();
    JButton jOKButton = new JButton();
    JButton jCancelButton = new JButton();

    // StringMonitor components (There is no panel)
    JLabel differLabel;
    JLabel matchingLabel;
    JLabel stringLabel;

    JComboBox differingComboBox;
    JComboBox matchingComboBox;
    JTextField stringTextField1;

    // Common components for numerical Monitors
    JLabel modeLabel;
    JComboBox modeComboBox;

    // CounterPanel
    JPanel counterPanel;
    TitledBorder counterBorder;
    GridBagLayout gridBagLayout5;

    JLabel modulusLabel;
    JLabel notifyLabel;
    JLabel thresholdLabel;
    JLabel offsetLabel;

    JTextField modulusTextField;
    JComboBox notifyComboBox;
    JTextField offsetTextField;
    JTextField thresholdTextField;

    // GaugePanel
    JPanel gaugePanel;
    TitledBorder gaugeBorder;
    GridBagLayout gridBagLayout6;

    JLabel lowNotifLabel;
    JLabel lowThresholdLabel;
    JLabel highThresholdLabel;
    JLabel highNotifLabel;

    JComboBox highNotifyComboBox;
    JTextField lowThresholdTextField;
    JTextField highThresholdTextField;
    JComboBox lowNotifyComboBox;


    /**
     *  Constructor for the MonitorDialog object
     *
     *@param  frame      Description of the Parameter
     *@param  mbeanName  Description of the Parameter
     *@param  attribute  Description of the Parameter
     *@param  clazz      Description of the Parameter
     *@param  modal      Description of the Parameter
     */
    public MonitorDialog(MainFrame frame, ObjectName mbeanName, String attribute, Class clazz, boolean modal) {
        super(frame, "Monitor", modal);
        m_browser = frame;
        m_attribute = attribute;
        m_clazz = clazz;
        m_mbeanName = mbeanName;
        try {
            jbInit();
            initFrame();
            pack();
            setLocationRelativeTo(frame);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     *  Constructor for the MonitorDialog object
     */
    public MonitorDialog() {
        this(null, null, null, null, false);
    }


    /**
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    void jbInit() throws Exception {
        targetBorder = new TitledBorder("");
        generalBorder = new TitledBorder("");
        specificBorder = new TitledBorder("");

        // TargetPanel
        targetPanel.setLayout(gridBagLayout2);
        targetPanel.setBorder(targetBorder);
        targetBorder.setTitle("Target");
        jObjectLabel.setText("Observed Object:");
        jAttrLabel.setText("Observed Attribute:");
        jObsAttrLabel.setFont(new java.awt.Font("Monospaced", 1, 12));
        jObsAttrLabel.setForeground(Color.yellow);
        jObsAttrLabel.setText(" ");
        jObsObjectLabel.setFont(new java.awt.Font("Monospaced", 1, 12));
        jObsObjectLabel.setForeground(Color.yellow);
        jObsObjectLabel.setText(" ");

        // MonitorPanel
        monitorPanel.setLayout(borderLayout1);

        // GeneralPanel
        generalPanel.setLayout(gridBagLayout1);
        generalPanel.setBorder(generalBorder);
        generalBorder.setTitle("General Parameters");
        jMonitorLabel.setText("Monitor Type:");
        jGranularityLabel.setText("Granularity Period:");
        jGranularityTextField.setText("1000");

        // SpecificPanel
        specificPanel.setLayout(gridBagLayout4);
        specificPanel.setBorder(specificBorder);
        specificBorder.setTitle("Specific Parameters");

        // ButtonPanel
        buttonPanel.setLayout(gridBagLayout3);
        jOKButton.setMaximumSize(new Dimension(73, 27));
        jOKButton.setMinimumSize(new Dimension(73, 27));
        jOKButton.setPreferredSize(new Dimension(73, 27));
        jOKButton.setText("OK");
        jOKButton.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jOKButton_actionPerformed(e);
                }
            });
        jCancelButton.setText("Cancel");
        jCancelButton.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jCancelButton_actionPerformed(e);
                }
            });

        // Build panels
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(jOKButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        buttonPanel.add(jCancelButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.getContentPane().add(targetPanel, BorderLayout.NORTH);
        targetPanel.add(jObjectLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 31, 0));
        targetPanel.add(jObsObjectLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        targetPanel.add(jAttrLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
        targetPanel.add(jObsAttrLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.getContentPane().add(monitorPanel, BorderLayout.CENTER);
        monitorPanel.add(generalPanel, BorderLayout.NORTH);
        generalPanel.add(jMonitorLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        generalPanel.add(jMonitorComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        generalPanel.add(jGranularityLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        generalPanel.add(jGranularityTextField, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        monitorPanel.add(specificPanel, BorderLayout.CENTER);
    }


    /**
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    void initFrame() throws Exception {
        jObsObjectLabel.setText(m_mbeanName.toString());
        jObsAttrLabel.setText(m_attribute);
        if (String.class.isAssignableFrom(m_clazz)) {
            jMonitorComboBox.addItem("StringMonitor");
            createStringPanel();
        } else if ((m_clazz.isPrimitive() && !m_clazz.getName().equals("boolean")) || Number.class.isAssignableFrom(m_clazz)) {
            jMonitorComboBox.addItem("CounterMonitor");
            jMonitorComboBox.addItem("GaugeMonitor");
            createCounterAndGaugePanel();
        }
    }


    /**
     *  Description of the Method
     */
    void createStringPanel() {

        // Instantiate components
        differLabel = new JLabel();
        matchingLabel = new JLabel();
        stringLabel = new JLabel();

        differingComboBox = new JComboBox();
        matchingComboBox = new JComboBox();
        stringTextField1 = new JTextField();

        // Configure components
        differLabel.setText("Differing Notification:");
        matchingLabel.setText("Matching Notification:");
        stringLabel.setText("String to compare:");
        differingComboBox.addItem("Enable");
        differingComboBox.addItem("Disable");
        matchingComboBox.addItem("Enable");
        matchingComboBox.addItem("Disable");

        // Build panel
        specificPanel.add(differLabel, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        specificPanel.add(differingComboBox, new GridBagConstraints(2, 1, 1, 2, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        specificPanel.add(matchingLabel, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        specificPanel.add(matchingComboBox, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        specificPanel.add(stringLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        specificPanel.add(stringTextField1, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    }


    /**
     *  Description of the Method
     */
    void createCounterAndGaugePanel() {

        // Instantiate components
        modeComboBox = new JComboBox();
        modeLabel = new JLabel();

        // Configure components
        modeLabel.setText("Difference Mode:");
        modeComboBox.addItem("Enable");
        modeComboBox.addItem("Disable");
        jMonitorComboBox.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMonitorComboBox_actionPerformed(e);
                }
            });

        // Instantiate components
        counterPanel = new JPanel();
        counterBorder = new TitledBorder("");
        gridBagLayout5 = new GridBagLayout();

        modulusLabel = new JLabel();
        notifyLabel = new JLabel();
        thresholdLabel = new JLabel();
        offsetLabel = new JLabel();

        modulusTextField = new JTextField();
        notifyComboBox = new JComboBox();
        offsetTextField = new JTextField();
        thresholdTextField = new JTextField();

        // CounterPanel
        counterPanel.setLayout(gridBagLayout5);
        counterPanel.setBorder(counterBorder);
        counterBorder.setTitle("Counter Monitor");

        modulusLabel.setText("Modulus:");
        notifyLabel.setText("Notification:");
        offsetLabel.setText("Offset:");
        thresholdLabel.setText("Threshold:");
        notifyComboBox.addItem("Enable");
        notifyComboBox.addItem("Disable");

        specificPanel.add(counterPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        counterPanel.add(notifyLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        counterPanel.add(notifyComboBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        counterPanel.add(modulusLabel, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        counterPanel.add(modulusTextField, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        counterPanel.add(offsetTextField, new GridBagConstraints(2, 2, 1, 2, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        counterPanel.add(offsetLabel, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        counterPanel.add(thresholdLabel, new GridBagConstraints(0, 3, 2, 2, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        counterPanel.add(thresholdTextField, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        // Instantiate components
        gaugePanel = new JPanel();
        gaugeBorder = new TitledBorder("");
        gridBagLayout6 = new GridBagLayout();

        lowNotifLabel = new JLabel();
        lowThresholdLabel = new JLabel();
        highThresholdLabel = new JLabel();
        highNotifLabel = new JLabel();

        highNotifyComboBox = new JComboBox();
        lowThresholdTextField = new JTextField();
        highThresholdTextField = new JTextField();
        lowNotifyComboBox = new JComboBox();

        // Configure componets
        gaugePanel.setLayout(gridBagLayout6);
        gaugePanel.setBorder(gaugeBorder);
        gaugeBorder.setTitle("Gauge Monitor");

        lowNotifLabel.setText("Low Notification:");
        lowThresholdLabel.setText("Low Threshold:");
        highThresholdLabel.setText("High Threshold:");
        highNotifLabel.setText("High Notification:");
        highNotifyComboBox.addItem("Enable");
        highNotifyComboBox.addItem("Disable");
        lowNotifyComboBox.addItem("Enable");
        lowNotifyComboBox.addItem("Disable");

        highNotifyComboBox.setEnabled(false);
        lowNotifyComboBox.setEnabled(false);
        highThresholdTextField.setEnabled(false);
        lowThresholdTextField.setEnabled(false);

        // Build panels
        specificPanel.add(gaugePanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        gaugePanel.add(highNotifLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        gaugePanel.add(highNotifyComboBox, new GridBagConstraints(2, 0, 1, 2, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        gaugePanel.add(lowNotifLabel, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        gaugePanel.add(highThresholdTextField, new GridBagConstraints(2, 3, 1, 2, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        gaugePanel.add(highThresholdLabel, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        gaugePanel.add(lowThresholdLabel, new GridBagConstraints(0, 4, 2, 2, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        gaugePanel.add(lowThresholdTextField, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        gaugePanel.add(lowNotifyComboBox, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        specificPanel.add(modeComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        specificPanel.add(modeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jCancelButton_actionPerformed(ActionEvent e) {
        this.dispose();
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jOKButton_actionPerformed(ActionEvent e) {
        try {
            String monitorType = (String) jMonitorComboBox.getSelectedItem();

            Hashtable properties = m_mbeanName.getKeyPropertyList();
            properties.put("attribute", m_attribute);
            properties.put("domain", m_mbeanName.getDomain());
            properties.put("monitor", monitorType);
            ObjectName monitorName = new ObjectName("Monitor", properties);
            ConnectorClient server = m_browser.getMBeanServer();

            try {
                server.createMBean("javax.management.monitor." + monitorType, monitorName);
            } catch (InstanceAlreadyExistsException iaee) {

            } catch (MBeanException mbex) {
                if (mbex.getTargetException() instanceof InstanceAlreadyExistsException) {
                    ;
                } else {
                    throw mbex;
                }
            }
            // Set general arameters
            server.invoke(monitorName, "setObservedObject",
                    new Object[]{m_mbeanName},
                    new String[]{"javax.management.ObjectName"});
            server.invoke(monitorName, "setObservedAttribute",
                    new Object[]{m_attribute},
                    new String[]{"java.lang.String"});
            server.invoke(monitorName, "setGranularityPeriod",
                    new Object[]{new Integer(jGranularityTextField.getText())},
                    new String[]{"long"});
            // Set specific parameters of each monitor
            if (monitorType.equals("StringMonitor")) {
                boolean notifyDiffer = differingComboBox.getSelectedIndex() == ENABLE ? true : false;
                server.invoke(monitorName, "setNotifyDiffer",
                        new Object[]{new Boolean(notifyDiffer)},
                        new String[]{"boolean"});
                boolean notifyMatch = matchingComboBox.getSelectedIndex() == ENABLE ? true : false;
                server.invoke(monitorName, "setNotifyMatch",
                        new Object[]{new Boolean(notifyMatch)},
                        new String[]{"boolean"});
                String stringToCompare = stringTextField1.getText();
                server.invoke(monitorName, "setStringToCompare",
                        new Object[]{stringToCompare},
                        new String[]{"java.lang.String"});
            } else if (monitorType.equals("CounterMonitor")) {
                boolean differMode = modeComboBox.getSelectedIndex() == ENABLE ? true : false;
                server.invoke(monitorName, "setDifferenceMode",
                        new Object[]{new Boolean(differMode)},
                        new String[]{"boolean"});
                boolean notify = notifyComboBox.getSelectedIndex() == ENABLE ? true : false;
                server.invoke(monitorName, "setNotify",
                        new Object[]{new Boolean(notify)},
                        new String[]{"boolean"});
                Number modulus = (Number) Utilities.objectFromString(m_clazz.getName(), modulusTextField.getText());
                server.invoke(monitorName, "setModulus",
                        new Object[]{modulus},
                        new String[]{"java.lang.Number"});
                Number offset = (Number) Utilities.objectFromString(m_clazz.getName(), offsetTextField.getText());
                server.invoke(monitorName, "setOffset",
                        new Object[]{offset},
                        new String[]{"java.lang.Number"});
                Number threshold = (Number) Utilities.objectFromString(m_clazz.getName(), thresholdTextField.getText());
                server.invoke(monitorName, "setThreshold",
                        new Object[]{threshold},
                        new String[]{"java.lang.Number"});
            } else if (monitorType.equals("GaugeMonitor")) {
                boolean differMode = modeComboBox.getSelectedIndex() == ENABLE ? true : false;
                server.invoke(monitorName, "setDifferenceMode",
                        new Object[]{new Boolean(differMode)},
                        new String[]{"boolean"});
                boolean notifyHigh = highNotifyComboBox.getSelectedIndex() == ENABLE ? true : false;
                server.invoke(monitorName, "setNotifyHigh",
                        new Object[]{new Boolean(notifyHigh)},
                        new String[]{"boolean"});
                boolean notifyLow = lowNotifyComboBox.getSelectedIndex() == ENABLE ? true : false;
                server.invoke(monitorName, "setNotifyLow",
                        new Object[]{new Boolean(notifyLow)},
                        new String[]{"boolean"});
                Number highThreshold = (Number) Utilities.objectFromString(m_clazz.getName(), highThresholdTextField.getText());
                Number lowThreshold = (Number) Utilities.objectFromString(m_clazz.getName(), lowThresholdTextField.getText());
                server.invoke(monitorName, "setThresholds",
                        new Object[]{highThreshold, lowThreshold},
                        new String[]{"java.lang.Number", "java.lang.Number"});
            }
            // Start the monitor
            server.invoke(monitorName, "start", null, null);
            this.dispose();
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
        this.dispose();
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jMonitorComboBox_actionPerformed(ActionEvent e) {
        if ("CounterMonitor".equals(jMonitorComboBox.getSelectedItem())) {
            notifyComboBox.setEnabled(true);
            modulusTextField.setEnabled(true);
            offsetTextField.setEnabled(true);
            thresholdTextField.setEnabled(true);
            highNotifyComboBox.setEnabled(false);
            lowNotifyComboBox.setEnabled(false);
            highThresholdTextField.setEnabled(false);
            lowThresholdTextField.setEnabled(false);
        } else if ("GaugeMonitor".equals(jMonitorComboBox.getSelectedItem())) {
            notifyComboBox.setEnabled(false);
            modulusTextField.setEnabled(false);
            offsetTextField.setEnabled(false);
            thresholdTextField.setEnabled(false);
            highNotifyComboBox.setEnabled(true);
            lowNotifyComboBox.setEnabled(true);
            highThresholdTextField.setEnabled(true);
            lowThresholdTextField.setEnabled(true);
        }
    }

}
