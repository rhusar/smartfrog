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

package org.smartfrog.tools.gui.browser.util;

import java.awt.*;
import javax.swing.*;
import org.smartfrog.tools.gui.browser.MainFrame;

import java.util.Vector;

import javax.swing.text.PlainDocument;
import org.gjt.sp.jedit.textarea.*;
import org.smartfrog.tools.gui.browser.syntax.*;
// SF Syntax
import java.awt.event.*;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 *
 */

public class BrowseSFFilesPanel extends JPanel {
   BorderLayout borderLayout1 = new BorderLayout();
   JPanel northPanel = new JPanel();
   JPanel southPanel = new JPanel();
   JComboBox sfComboBox = new JComboBox();
   BorderLayout borderLayout2 = new BorderLayout();
   //JTextArea jTextArea1 = new JTextArea();
   JEditTextArea textArea = new JEditTextArea();
   BorderLayout borderLayout3 = new BorderLayout();
   JLabel jLabel1 = new JLabel();

   LoadSFFiles loadSFFiles = new LoadSFFiles();
   JToolBar jToolBar = new JToolBar();
   JButton Copy = new JButton();
   JButton CopyURL = new JButton();
   JButton jButtonRun = new JButton();
   MainFrame mainF = null;
   BrowseEntry rootTreeEntry = null;


   /**
    *  Constructor for the BroweSFFilesPanel object
    *
    *@param  mainF  Description of Parameter
    */
   public BrowseSFFilesPanel(MainFrame mainF, BrowseEntry rootTreeEntry) {
      try {
         this.rootTreeEntry = rootTreeEntry;
         this.mainF = mainF;
         jbInit();
         init();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   /**
    *  Constructor for the BroweSFFilesPanel object
    *
    *@param  mainF  Description of Parameter
    */
   public BrowseSFFilesPanel(MainFrame mainF) {
      try {
         this.mainF = mainF;
         jbInit();
         init();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   /**
    *  Description of the Method
    */
   void init() {
      Vector list = loadSFFiles.init();
      if (this.rootTreeEntry!=null) {
         for (int i=0;i<list.size();i++) {
            this.rootTreeEntry.add("#include /"+(String)list.get(i));
         }
      }
      this.sfComboBox.setModel(new DefaultComboBoxModel(list));
      // SmartFrog Token Marker ;-)
      textArea.setTokenMarker(sfTokenMarker);
      setTabSize(textArea, 3);
      textArea.getGutter().setHighlightInterval(5);
      textArea.getGutter().setHighlightedForeground(Color.red);
      textArea.setFont(new java.awt.Font("DialogInput", 0, 12));
      textArea.setText(loadSFFiles.getFile((String)this.sfComboBox.getSelectedItem()));
   }

   public void loadFile(String fileURL){
       //System.out.println("Load in TexArea (BrowsePanel:"+fileURL);
       textArea.setText(loadSFFiles.getFile(fileURL));
       this.sfComboBox.setSelectedItem(fileURL);
       this.setTokenEditMarker(fileURL);
   }


   //Token markers
   org.gjt.sp.jedit.syntax.TokenMarker sfTokenMarker =new SfTokenMarker();
   org.gjt.sp.jedit.syntax.TokenMarker sf2TokenMarker =new Sf2TokenMarker();
   org.gjt.sp.jedit.syntax.TokenMarker sfXMLTokenMarker =new SfXMLTokenMarker();

   private void setTokenEditMarker(String fileName){
         if (fileName.endsWith(".sf")){
           textArea.setTokenMarker(sfTokenMarker);
           //System.out.println("TextArea SF");
         } else if (fileName.endsWith(".sf2")){
           textArea.setTokenMarker(sf2TokenMarker);
           //System.out.println("TextArea SF2");
         } else if (fileName.endsWith(".sfxml")){
            textArea.setTokenMarker(sfXMLTokenMarker);
           //System.out.println("TextArea SFXML");
         } else if (fileName.endsWith(".sfcd")){
           textArea.setTokenMarker(sfTokenMarker);
         } else {
            textArea.setTokenMarker(sfTokenMarker);
         }
   }
   /**
    *  Description of the Method
    *
    *@exception  Exception  Description of Exception
    */
   void jbInit() throws Exception {
      this.setLayout(borderLayout1);
      northPanel.setLayout(borderLayout2);
      textArea.setEditable(false);
      southPanel.setLayout(borderLayout3);
      sfComboBox.addActionListener(new BroweSFFilesPanel_sfComboBox_actionAdapter(this));
      sfComboBox.setAutoscrolls(true);
      Copy.setText("Copy Text");
      Copy.setIcon(mainF.imageCopy);
      Copy.addActionListener(new BroweSFFilesPanel_Copy_actionAdapter(this));
      Copy.addActionListener(new BroweSFFilesPanel_Copy_actionAdapter(this));
      CopyURL.setText("Copy URL");
      CopyURL.setIcon(mainF.imageCopy);
      CopyURL.addActionListener(new BroweSFFilesPanel_CopyURL_actionAdapter(this));
      jToolBar.setFloatable(false);
      jButtonRun.setActionCommand("jButtonRun");
      jButtonRun.setText("Run");
      jButtonRun.setIcon(mainF.imageRun);
      jButtonRun.addActionListener(new BroweSFFilesPanel_jButtonRun_actionAdapter(this));
      this.add(northPanel, BorderLayout.NORTH);
      this.add(southPanel, BorderLayout.CENTER);
      northPanel.add(sfComboBox, BorderLayout.CENTER);
      northPanel.add(jToolBar, BorderLayout.EAST);
      southPanel.add(textArea, BorderLayout.CENTER);
      southPanel.add(jLabel1, BorderLayout.NORTH);
      jToolBar.add(jButtonRun, null);
      jToolBar.add(CopyURL, null);
      //jToolBar.add(Copy, null); // No needed anymore
   }

   /**
    *  Sets the tabSize attribute of the MainFrame object
    *
    *@param  textArea  The new tabSize value
    *@param  tabSize   The new tabSize value
    */
   public void setTabSize(JEditTextArea textArea, int tabSize) {
      //int auxTabSize =((Integer) textArea .getDocument().getProperty(PlainDocument.tabSizeAttribute)).intValue();
      //auxTabSize = tabSize;
      textArea.getDocument().putProperty(PlainDocument.tabSizeAttribute, new Integer(tabSize));
   }

   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   void sfComboBox_actionPerformed(ActionEvent e) {
      textArea.setText(loadSFFiles.getFile((String)this.sfComboBox.getSelectedItem()));
   }

   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   void Copy_actionPerformed(ActionEvent e) {
      textArea.copy();
   }

   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   void CopyURL_actionPerformed(ActionEvent e) {

      Clipboard clipboard = textArea.getToolkit().getSystemClipboard();
      String selection = "#include \"" + (String)this.sfComboBox.getSelectedItem() + "\" ";
      StringBuffer buf = new StringBuffer();
      buf.append(selection);
      clipboard.setContents(new StringSelection(buf.toString()), null);

   }

   /**
    *  Gets the selectedFile attribute of the BrowseSFFilesPanel object
    *
    *@return    The selectedFile value
    */
   public String getSelectedFile() {
      return ((String)this.sfComboBox.getSelectedItem());
   }

   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   void jButtonRun_actionPerformed(ActionEvent e) {
      mainF.runSFProcess((String)this.sfComboBox.getSelectedItem());
   }

   /**
    *  Gets the textArea attribute of the BrowseSFFilesPanel object
    *
    *@return    The textArea value
    */
   public JEditTextArea getTextArea() {
      return this.textArea;
   }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    13 December 2001
 */
class BroweSFFilesPanel_sfComboBox_actionAdapter implements java.awt.event.ActionListener {

   BrowseSFFilesPanel adaptee;

   /**
    *  Constructor for the BroweSFFilesPanel_sfComboBox_actionAdapter object
    *
    *@param  adaptee  Description of Parameter
    */
   BroweSFFilesPanel_sfComboBox_actionAdapter(BrowseSFFilesPanel adaptee) {
      this.adaptee = adaptee;
   }

   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   public void actionPerformed(ActionEvent e) {
      adaptee.sfComboBox_actionPerformed(e);
   }

}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    13 December 2001
 */
class BroweSFFilesPanel_Copy_actionAdapter implements java.awt.event.ActionListener {

   BrowseSFFilesPanel adaptee;

   /**
    *  Constructor for the BroweSFFilesPanel_Copy_actionAdapter object
    *
    *@param  adaptee  Description of Parameter
    */
   BroweSFFilesPanel_Copy_actionAdapter(BrowseSFFilesPanel adaptee) {
      this.adaptee = adaptee;
   }

   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   public void actionPerformed(ActionEvent e) {
      adaptee.Copy_actionPerformed(e);
   }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    13 December 2001
 */
class BroweSFFilesPanel_CopyURL_actionAdapter implements java.awt.event.ActionListener {

   BrowseSFFilesPanel adaptee;

   /**
    *  Constructor for the BroweSFFilesPanel_CopyURL_actionAdapter object
    *
    *@param  adaptee  Description of Parameter
    */
   BroweSFFilesPanel_CopyURL_actionAdapter(BrowseSFFilesPanel adaptee) {
      this.adaptee = adaptee;
   }

   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   public void actionPerformed(ActionEvent e) {
      adaptee.CopyURL_actionPerformed(e);
   }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    13 December 2001
 */
class BroweSFFilesPanel_jButtonRun_actionAdapter implements java.awt.event.ActionListener {

   BrowseSFFilesPanel adaptee;

   /**
    *  Constructor for the BroweSFFilesPanel_jButtonRun_actionAdapter object
    *
    *@param  adaptee  Description of Parameter
    */
   BroweSFFilesPanel_jButtonRun_actionAdapter(BrowseSFFilesPanel adaptee) {
      this.adaptee = adaptee;
   }

   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   public void actionPerformed(ActionEvent e) {
      adaptee.jButtonRun_actionPerformed(e);
   }
}
