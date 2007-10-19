/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.tools.gui.browser;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Title:        SFGui
 * Description:  Preferences Dialog
 * Copyright:    Copyright (c) 2001
 */

public class PreferencesDialog extends JDialog implements ActionListener{
  JPanel jPanel1 = new JPanel();
  JLabel jLabelPath = new JLabel();
  JTextField jTextField1 = new JTextField();
  String classPath="";
  JButton jButtonOk = new JButton();
  JButton jButtonCancel = new JButton();
  Frame frame;

  public PreferencesDialog(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    this.frame=frame;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.classPath=(String)((MainFrame)frame).getCurrFilePath();
    //System.out.println(this.classPath);
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }


  public PreferencesDialog() {
    this(null, "", false);
  }
  void jbInit() throws Exception {
    jLabelPath.setText("Path");
    jTextField1.setFont(new java.awt.Font("DialogInput", 0, 12));
    jTextField1.setPreferredSize(new Dimension(500, 21));
    jTextField1.setText("                                                                 " +
    "                                                               ");
    jTextField1.setText(this.classPath);
    jButtonOk.setText("OK");
    jButtonOk.addActionListener(this);
    jButtonCancel.setText("Cancel");
    jButtonCancel.addActionListener(this);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jLabelPath, null);
    jPanel1.add(jTextField1, null);
    jPanel1.add(jButtonOk, null);
    jPanel1.add(jButtonCancel, null);
  }

/**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }
  /**Close the dialog*/
  void cancel() {
    dispose();
  }
  void ok(){
    ((MainFrame)this.frame).setCurrFilePath(jTextField1.getText());
    dispose();
  }
  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == jButtonCancel) {
      cancel();
      //System.out.print("Cancel");
    } else if (e.getSource()==jButtonOk){
      ok();
      //System.out.print("Ok");
    }
  }

}