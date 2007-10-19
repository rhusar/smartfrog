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

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class HeartBeatDialog extends JDialog {
    /**
     *  Description of the Field
     */
    public final static int EMPTY = 0;
    /**
     *  Description of the Field
     */
    public final static int PERIOD = 1;
    /**
     *  Description of the Field
     */
    public final static int RETRIES = 2;

    /**
     *  Description of the Field
     */
    public final static String[] titles = new String[]{"",
            "HeartBeat period",
            "HeartBeat retries"};

    JPanel panel1 = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JLabel jLabel = new JLabel();
    JTextField jTextField = new JTextField();
    JButton jOKButton = new JButton();
    JButton jCancelButton = new JButton();

    MainFrame mainFrame = null;
    int parameter = -1;


    /**
     *  Constructor for the HeartBeatDialog object
     *
     *@param  frame  Description of the Parameter
     *@param  param  Description of the Parameter
     *@param  modal  Description of the Parameter
     */
    public HeartBeatDialog(MainFrame frame, int param, boolean modal) {
        super(frame, titles[param], modal);
        try {
            mainFrame = frame;
            parameter = param;
            jbInit();
            pack();
            setLocationRelativeTo(frame);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     *  Constructor for the HeartBeatDialog object
     */
    public HeartBeatDialog() {
        this(null, EMPTY, false);
    }


    /**
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    void jbInit() throws Exception {
        panel1.setLayout(gridBagLayout1);
        switch (parameter) {
            case (HeartBeatDialog.PERIOD):
                jLabel.setText("Period:");
                jTextField.setText(Integer.toString(mainFrame.getHeartBeatPeriod()));
                break;
            case (HeartBeatDialog.RETRIES):
                jLabel.setText("Number of retries:");
                jTextField.setText(Integer.toString(mainFrame.getHeartBeatRetries()));
                break;
            default:
                jLabel.setText("");
        }
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
        getContentPane().add(panel1);
        panel1.add(jLabel, new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 0, 15), 100, 15));
        panel1.add(jTextField, new GridBagConstraints(0, 1, 4, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 15), 100, 0));
        panel1.add(jOKButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(15, 30, 15, 10), 0, 0));
        panel1.add(jCancelButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(15, 10, 15, 30), 0, 0));

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
            switch (parameter) {
                case (HeartBeatDialog.PERIOD):
                    mainFrame.setHeartBeatPeriod(Integer.parseInt(jTextField.getText()));
                    break;
                case (HeartBeatDialog.RETRIES):
                    mainFrame.setHeartBeatRetries(Integer.parseInt(jTextField.getText()));
                    break;
            }
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
