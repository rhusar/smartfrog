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

/**
 * Title:        sfJMX
 * Description:  JMX-based Management Framework for SmartFrog Applications
 * Copyright:    Copyright (c) 2001
 * Company:      Hewlett Packard
 *
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.lang.reflect.*;
import javax.management.*;

public class AdminDialog extends JDialog {
  JPanel panel1 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel domainLabel = new JLabel();
  JTextField domainField = new JTextField();
  JLabel keysLabel = new JLabel();
  JTextField keysField = new JTextField();
  JLabel classLabel = new JLabel();
  JTextField classField = new JTextField();
  JLabel loaderLabel = new JLabel();
  JTextField loaderField = new JTextField();
  JPanel panel2 = new JPanel();
  JRadioButton createRadioButton = new JRadioButton();
  JRadioButton unregisterRadioButton = new JRadioButton();
  ButtonGroup buttonGroup = new ButtonGroup();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JRadioButton constructorRadioButton = new JRadioButton();
  JPanel panel3 = new JPanel();
  JButton resetButton = new JButton();
  JButton cancelButton = new JButton();
  JButton submitButton = new JButton();
  GridBagLayout gridBagLayout3 = new GridBagLayout();

  MainFrame m_browser = null;

  public AdminDialog(MainFrame frame, String title, boolean modal) {
    super(frame, title, modal);
    try {
      m_browser = frame;
      jbInit();
      pack();
      setLocationRelativeTo(frame);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public AdminDialog() {
    this(null, "", false);
  }

  void jbInit() throws Exception {
    panel1.setLayout(gridBagLayout1);
    domainLabel.setForeground(Color.yellow);
    domainLabel.setText("Domain");
    domainField.setText("DefaultDomain");
    keysLabel.setForeground(Color.yellow);
    keysLabel.setText("Keys");
    classLabel.setForeground(Color.yellow);
    classLabel.setToolTipText("");
    classLabel.setText("Class Name");
    loaderLabel.setText("Class Loader");
    createRadioButton.setSelected(true);
    createRadioButton.setText("Create");
    createRadioButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        createRadioButton_actionPerformed(e);
      }
    });
    unregisterRadioButton.setText("Unregister");
    unregisterRadioButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        unregisterRadioButton_actionPerformed(e);
      }
    });
    panel2.setLayout(gridBagLayout2);
    constructorRadioButton.setEnabled(false);
    constructorRadioButton.setText("Constructors");
    resetButton.setText("Reset");
    resetButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        resetButton_actionPerformed(e);
      }
    });
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    submitButton.setText("Submit");
    submitButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        submitButton_actionPerformed(e);
      }
    });
    panel3.setLayout(gridBagLayout3);
    this.getContentPane().add(panel1, BorderLayout.NORTH);
    panel1.add(domainLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 0), 0, 0));
    panel1.add(domainField, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 174, 0));
    panel1.add(keysLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 0), 56, 0));
    panel1.add(classLabel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 0), 21, 0));
    panel1.add(loaderLabel, new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 6, 0));
    panel1.add(keysField, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    panel1.add(classField, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    panel1.add(loaderField, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(panel2, BorderLayout.CENTER);
    panel2.add(unregisterRadioButton, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    panel2.add(createRadioButton, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    panel2.add(constructorRadioButton, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(panel3, BorderLayout.SOUTH);
    panel3.add(submitButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 88, 5, 5), 0, 0));
    panel3.add(resetButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    panel3.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 79), 0, 0));
    buttonGroup.add(createRadioButton);
    buttonGroup.add(unregisterRadioButton);
    buttonGroup.add(constructorRadioButton);
  }

  void cancelButton_actionPerformed(ActionEvent e) {
    this.dispose();
  }

  void resetButton_actionPerformed(ActionEvent e) {
    domainField.setText("DefaultDomain");
    keysField.setText("");
    classField.setText("");
    loaderField.setText("");
  }

  void submitButton_actionPerformed(ActionEvent e) {
    try {
      ObjectName mbeanName = new ObjectName(domainField.getText()+":"+keysField.getText());
      String classLoader = loaderField.getText();
      if (buttonGroup.isSelected(createRadioButton.getModel())) {
        // Create instance
        if (classLoader == null || classLoader.length() == 0) {
          m_browser.getMBeanServer().createMBean(classField.getText(), mbeanName);
        }
        else {
          ObjectName loaderName = new ObjectName(classLoader);
          m_browser.getMBeanServer().createMBean(classField.getText(), mbeanName, loaderName);
        }
      }
      else if (buttonGroup.isSelected(unregisterRadioButton.getModel())) {
        // Unregister instance
        m_browser.getMBeanServer().unregisterMBean(mbeanName);
      }
      else { }
      this.dispose();
    }
    catch (Throwable throwable) {
      Throwable rootCause = null;
      if (throwable instanceof MBeanException) rootCause = ((MBeanException)throwable).getTargetException();
      else if (throwable instanceof InvocationTargetException) rootCause = ( (InvocationTargetException)throwable ).getTargetException();
      if (rootCause == null) rootCause = throwable;
      JOptionPane.showMessageDialog(m_browser, rootCause, "Error", JOptionPane.ERROR_MESSAGE );
    }
  }

  void unregisterRadioButton_actionPerformed(ActionEvent e) {
    classField.setEnabled(false);
    classField.setBackground(Color.lightGray);
    loaderField.setEnabled(false);
    loaderField.setBackground(Color.lightGray);
  }

  void createRadioButton_actionPerformed(ActionEvent e) {
    classField.setEnabled(true);
    classField.setBackground(UIManager.getColor("TextField.background"));
    loaderField.setEnabled(true);
    loaderField.setBackground(UIManager.getColor("TextField.background"));
  }

}
