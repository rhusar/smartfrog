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
import org.smartfrog.services.jmx.communication.ServerAddress;
import org.smartfrog.services.jmx.communication.rmi.RmiServerAddress;
import org.smartfrog.services.jmx.communication.ConnectionFactory;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class SettingDialog extends JDialog {
    JPanel panel1 = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JLabel jHostLabel = new JLabel();
    JTextField jHostTextField = new JTextField();
    JLabel jPortLabel = new JLabel();
    JTextField jPortTextField = new JTextField();
    JLabel jServiceLabel = new JLabel();
    JTextField jServiceTextField = new JTextField();
    JButton jOKButton = new JButton();
    JButton jCancelButton = new JButton();

    MainFrame mainFrame = null;
    JLabel jProtocolLabel = new JLabel();
    JComboBox jProtocolComboBox = new JComboBox();


    // Constructors
    /**
     *  Constructor for the SettingDialog object
     *
     *@param  frame  Description of the Parameter
     *@param  title  Description of the Parameter
     *@param  modal  Description of the Parameter
     */
    public SettingDialog(MainFrame frame, String title, boolean modal) {
        super(frame, title, modal);
        try {
            mainFrame = frame;
            ServerAddress serverAddress = null;
            if (mainFrame != null) {
                setSettingsIntoFields(mainFrame.getServerAddress());
            }
            jbInit();
            pack();
            setLocationRelativeTo(frame);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     *  Constructor for the SettingDialog object
     */
    public SettingDialog() {
        this(null, "", false);
    }


    /**
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    void jbInit() throws Exception {
        panel1.setLayout(gridBagLayout1);
        jProtocolLabel.setText("Protocol");
        jHostLabel.setText("Host");
        jPortLabel.setText("Port");
        jServiceLabel.setText("Service");
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
        jCancelButton.setMaximumSize(new Dimension(75, 25));
        jCancelButton.setMinimumSize(new Dimension(75, 25));
        jCancelButton.setPreferredSize(new Dimension(75, 25));
        jCancelButton.setRequestFocusEnabled(false);
        jCancelButton.setText("Cancel");
        jCancelButton.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jCancelButton_actionPerformed(e);
                }
            });
        this.addKeyListener(
            new java.awt.event.KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    this_keyPressed(e);
                }
            });
        jProtocolComboBox.setEditable(true);
        jProtocolComboBox.addItem("RMI");
        jProtocolComboBox.addItem("HTTP");
        jProtocolComboBox.addItem("HTTPS");
        jProtocolComboBox.addItem("IIOP");
        jProtocolComboBox.setSelectedItem("RMI");
        getContentPane().add(panel1);
        panel1.add(jHostTextField, new GridBagConstraints(0, 3, 4, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 15), 100, 0));
        panel1.add(jPortTextField, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 15), 100, 0));
        panel1.add(jServiceTextField, new GridBagConstraints(0, 7, 2, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 0, 15), 100, 0));
        panel1.add(jHostLabel, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 0, 15), 0, 0));
        panel1.add(jPortLabel, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 0, 15), 0, 0));
        panel1.add(jOKButton, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(15, 30, 15, 10), 0, 0));
        panel1.add(jCancelButton, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(15, 10, 15, 30), 0, 0));
        panel1.add(jServiceLabel, new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 0, 15), 150, 0));
        panel1.add(jProtocolLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 0, 15), 0, 0));
        panel1.add(jProtocolComboBox, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 15), 0, 0));

    }


    // Lay connection settings in the Text Fields
    /**
     *  Sets the settingsIntoFields attribute of the SettingDialog object
     *
     *@param  serverAddress  The new settingsIntoFields value
     */
    protected void setSettingsIntoFields(ServerAddress serverAddress) {
        String host = serverAddress.getHost();
        Object resource = serverAddress.getResource();
        if (host != null) jHostTextField.setText(serverAddress.getHost());
        jPortTextField.setText(String.valueOf(serverAddress.getPort()));
        if (resource != null) jServiceTextField.setText(serverAddress.getResource().toString());
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
            ServerAddress address = ConnectionFactory.createServerAddress(
                    jProtocolComboBox.getSelectedItem().toString(),
                    jHostTextField.getText(),
                    Integer.parseInt(jPortTextField.getText()),
                    jServiceTextField.getText());
            mainFrame.setConnectorAddress(address);
            mainFrame.jSettingButton.setToolTipText("Settings ["+address.toString()+"]");
            mainFrame.setTitle("JMX Browser" + " ["+address.toString()+"]");
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
        }
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
