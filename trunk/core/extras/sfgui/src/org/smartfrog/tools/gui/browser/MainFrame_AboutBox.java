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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class MainFrame_AboutBox extends JDialog implements ActionListener {

  JPanel panel1 = new JPanel();
  JPanel panel2 = new JPanel();
  JPanel insetsPanel1 = new JPanel();
  JPanel insetsPanel2 = new JPanel();
  JPanel insetsPanel3 = new JPanel();
  JButton button1 = new JButton();
  JLabel imageLabel = new JLabel();
  JLabel label1 = new JLabel();
  JLabel label2 = new JLabel();
  JLabel label3 = new JLabel();
  JLabel label4 = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  FlowLayout flowLayout1 = new FlowLayout();
  GridLayout gridLayout1 = new GridLayout();
  String product = "SFGui";
  String copyright = "(C) Copyright 1998-2004 Hewlett-Packard Development Company, LP";
  String author = "@HPLabs Bristol";
  //String author = "Julio Guijarro";
  String comments = "";
  JLabel graphicLabel = new JLabel();


  public MainFrame_AboutBox(Frame parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    pack();
  }



  /**Component initialization*/
  private void jbInit() throws Exception  {
    //imageLabel.setIcon(new ImageIcon(MainFrame_AboutBox.class.getResource("[Your Image]")));
    this.setTitle("About");
    this.setSize(300, 210);
    setResizable(false);
    panel1.setLayout(borderLayout1);
    panel2.setLayout(borderLayout2);
    insetsPanel1.setLayout(flowLayout1);
    insetsPanel2.setLayout(flowLayout1);
    insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    gridLayout1.setRows(4);
    gridLayout1.setColumns(1);
    label1.setText(product);
    label2.setText(org.smartfrog.tools.gui.browser.MainFrame.version);
    label3.setText(copyright);
    label4.setText(comments);
    insetsPanel3.setLayout(gridLayout1);
    insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
    button1.setText("Ok");
    button1.addActionListener(this);
    graphicLabel.setHorizontalAlignment(SwingConstants.CENTER);
    graphicLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    graphicLabel.setIcon(new ImageIcon(MainFrame_AboutBox.class.getResource("hp.gif")));
    panel1.setMaximumSize(new Dimension(259, 210));
    insetsPanel2.add(imageLabel, null);
    panel2.add(insetsPanel3, BorderLayout.CENTER);
    insetsPanel3.add(label1, null);
    insetsPanel3.add(label2, null);
    insetsPanel3.add(label3, null);
    insetsPanel3.add(label4, null);
    panel2.add(insetsPanel2, BorderLayout.NORTH);
    this.getContentPane().add(panel1, null);
    panel1.add(graphicLabel, BorderLayout.CENTER);
    insetsPanel1.add(button1, null);
    panel1.add(insetsPanel1, BorderLayout.SOUTH);
    panel1.add(panel2, BorderLayout.NORTH);
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
  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == button1) {
      cancel();
    }
  }
//  public static void main(String[] args) {
//    try {
//      MainFrame_AboutBox dlg = new MainFrame_AboutBox();
//      dlg.setModal(true);
//      dlg.show();
//    } catch(Exception e) {
//      e.printStackTrace();
//    }
//
//  }

}
