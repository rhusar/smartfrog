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
import java.util.Vector;
import java.io.*;

import javax.swing.text.PlainDocument;
import org.gjt.sp.jedit.textarea.*;
import org.smartfrog.tools.gui.browser.syntax.*;
import org.smartfrog.tools.gui.browser.MainFrame;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.componentdescription.*;

// SF Syntax
import java.awt.event.*;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;



/**
 *  Title: SmartFrog CVS Description:
 */

public class GenParsePanel extends JPanel {
   BorderLayout borderLayout1 = new BorderLayout();
   JPanel northPanel = new JPanel();
   JPanel southPanel = new JPanel();
   JComboBox sfComboBox = new JComboBox();
   BorderLayout borderLayout2 = new BorderLayout();
   //JTextArea jTextArea1 = new JTextArea();
   JEditTextArea textArea = new JEditTextArea();
   MainFrame    mainFrame   = null;
   BorderLayout borderLayout3 = new BorderLayout();
   JLabel jLabel1 = new JLabel();

   JToolBar jToolBar = new JToolBar();
   JButton Copy = new JButton();
   JButton Parse = new JButton();
   JButton jButtonLoadPhases = new JButton();



   /**
    *  Constructor for the GenParsePanel object
    */
   public GenParsePanel(MainFrame frame) {

      try {
         if (frame!=null) {
            mainFrame =  frame;
         } else {
            System.err.println("GenParsePanel:Reference to MainFrame not found.");
            return;
         }
         jbInit();
         init();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
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
    */
   void init() {
      this.sfComboBox.setModel(new DefaultComboBoxModel());
      // SmartFrog Token Marker ;-)
      textArea.setTokenMarker(new SfTokenMarker());
      setTabSize(textArea, 3);
      textArea.getGutter().setHighlightInterval(5);
      textArea.getGutter().setHighlightedForeground(Color.red);
      textArea.setFont(new java.awt.Font("DialogInput", 0, 12));
      textArea.setText("");
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
      sfComboBox.addActionListener(new GenParsePanel_sfComboBox_actionAdapter(this));
      sfComboBox.setAutoscrolls(true);
      Copy.setText("Copy Text");
      Copy.setIcon(mainFrame.imageCopy);
      Copy.addActionListener(new GenParsePanel_Copy_actionAdapter(this));
      Copy.addActionListener(new GenParsePanel_Copy_actionAdapter(this));
      Parse.setText("Parse");
      Parse.setIcon(mainFrame.imageParse);
      Parse.addActionListener(new GenParsePanel_Parse_actionAdapter(this));
      jToolBar.setFloatable(false);
      jButtonLoadPhases.setText("Load Phases");
      jButtonLoadPhases.setIcon(mainFrame.imageOpen);
      jButtonLoadPhases.addActionListener(new GenParsePanel_jButtonLoadPhases_actionAdapter(this));
      this.add(northPanel, BorderLayout.NORTH);
      this.add(southPanel, BorderLayout.CENTER);
      northPanel.add(sfComboBox, BorderLayout.CENTER);
      northPanel.add(jToolBar, BorderLayout.EAST);
      southPanel.add(textArea, BorderLayout.CENTER);
      southPanel.add(jLabel1, BorderLayout.NORTH);
      jToolBar.add(jButtonLoadPhases, null);
      jToolBar.add(Parse, null);
      jToolBar.add(Copy, null);
   }

   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   void sfComboBox_actionPerformed(ActionEvent e) {
      // nothing by now
      this.parseFile(this.mainFrame.getActiveLanguage());
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
   void Parse_actionPerformed(ActionEvent e) {
      //TODO: It should parse up to the phase shown in the list!
      this.parseFile(this.mainFrame.getActiveLanguage());

//      Clipboard clipboard = textArea.getToolkit().getSystemClipboard();
//      String selection = "#include \""+(String) this.sfComboBox.getSelectedItem()+"\" ";
//      StringBuffer buf = new StringBuffer();
//      buf.append(selection);
//      clipboard.setContents(new StringSelection(buf.toString()),null);
   }

  void parseFile(String language){

     Phases top = null;
     Vector phases = null;

     if ((mainFrame.okToAbandon(mainFrame.jTextAreaSFFile.getText(), mainFrame.currFileName))
      &&((this.sfComboBox.getSelectedItem())!=null)) {
         this.textArea.setText("Parsing..."+mainFrame.currFileName);
         // Instead of passing the text content in the JTextArea, the text is saved and the real file is passed!
         if (mainFrame.currFileName != null) {
            // Security?
            try {
               //InputStream is = SFClassLoader.getResourceAsStream(mainFrame.currFileName);
               InputStream is = new FileInputStream(mainFrame.currFileName);
               top = new SFParser(language).sfParse(is);
            } catch (Throwable ex) {
               mainFrame.log(ex.getMessage(), "GeneralParse", 5);
               // 5 Error
               //System.out.print( "["+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format( new Date( System.currentTimeMillis() ) )+"] ");
               //System.err.println(ex.printStackTrace());
               ex.printStackTrace();
            }
            try {
               if (top != null) {
                  phases = top.sfGetPhases();
                  // Parse up to the phase selected in sfComboBox
                  int i = this.sfComboBox.getSelectedIndex();
                  Vector auxphases=new Vector();
                  boolean containSfConfig = false;
                  for (int i2=0;i2<=i;i2++){
                     if (phases.elementAt(i2).equals("sfConfig")) {
                        containSfConfig=true;
                     }
                     auxphases.add(phases.elementAt(i2));
                  }
                  //Vector auxphases = phases.copy
                  top = top.sfResolvePhases(auxphases);
                  this.textArea.setText(top.toString());

                  mainFrame.log("SFParse Done("+(auxphases.lastElement()).toString() +").", "GenParse", 3);
                  //3 Info
               } else {
                  mainFrame.log("SFParse Failed.", "GenParse", 5);
                  //3 Info
               }

            } catch (Throwable ex) {
               mainFrame.log(ex.getMessage(), "GenParse", 5);
               // 5 Erro
               //System.out.print( "["+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format( new Date( System.currentTimeMillis() ) )+"] ");
               //System.err.println(ex.printStackTrace());
               ex.printStackTrace();
            }
         } else {
            mainFrame.log("No SFFile loaded", "GenParse", 4);
         }
      }
  }


   Vector loadPhasesList(String language) {
     Phases top = null;
     Vector phases = null;

     if (mainFrame.okToAbandon(mainFrame.jTextAreaSFFile.getText(), mainFrame.currFileName)) {
         this.textArea.setText("");
         // Instead of passing the text content in the JTextArea, the text is saved and the real file is passed!
         if (mainFrame.currFileName != null) {
            // Security?
            try {
               //InputStream is = SFClassLoader.getResourceAsStream(mainFrame.currFileName);
               InputStream is = new FileInputStream(mainFrame.currFileName);
               top = new SFParser(language).sfParse(is);
            } catch (Throwable ex) {
               mainFrame.log(ex.getMessage(), "GeneralParse", 5);
               // 5 Error
               //System.out.print( "["+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format( new Date( System.currentTimeMillis() ) )+"] ");
               //System.err.println(ex.printStackTrace());
               ex.printStackTrace();
            }
            try {
               if (top != null) {
                  phases = top.sfGetPhases();
                  return phases;
                  //3 Info
               } else {
                  mainFrame.log("LoadPhases Failed.", "GenParse", 5);
                  //3 Info
               }

            } catch (Throwable ex) {
               mainFrame.log(ex.getMessage(), "GenParse", 5);
               // 5 Erro
               //System.out.print( "["+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format( new Date( System.currentTimeMillis() ) )+"] ");
               //System.err.println(ex.printStackTrace());
               ex.printStackTrace();
            }
         } else {
            mainFrame.log("No SFFile loaded", "GenParse", 4);
         }
      }
      return null;
  }

   void jButtonLoadPhases_actionPerformed(ActionEvent e) {
      Vector list = loadPhasesList(this.mainFrame.getActiveLanguage());
      if (list !=null){
         this.sfComboBox.setModel(new DefaultComboBoxModel(list));
      } else {
        //mainFrame.log("No phases loaded", "GenParse", 4);
      }
   }/// end parseFile


   public void clean (){
      this.sfComboBox.setModel(new DefaultComboBoxModel());
      this.textArea.setText("");
   }


   public  JEditTextArea getTextArea () {
      return this.textArea;
   }

//End class
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    04 December 2001
 */
class GenParsePanel_sfComboBox_actionAdapter implements java.awt.event.ActionListener {

   GenParsePanel adaptee;

   /**
    *  Constructor for the GenParsePanel_sfComboBox_actionAdapter object
    *
    *@param  adaptee  Description of Parameter
    */
   GenParsePanel_sfComboBox_actionAdapter(GenParsePanel adaptee) {
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
 *@created    04 December 2001
 */
class GenParsePanel_Copy_actionAdapter implements java.awt.event.ActionListener {

   GenParsePanel adaptee;

   /**
    *  Constructor for the GenParsePanel_Copy_actionAdapter object
    *
    *@param  adaptee  Description of Parameter
    */
   GenParsePanel_Copy_actionAdapter(GenParsePanel adaptee) {
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
 *@created    04 December 2001
 */
class GenParsePanel_Parse_actionAdapter implements java.awt.event.ActionListener {

   GenParsePanel adaptee;

   /**
    *  Constructor for the GenParsePanel_Parse_actionAdapter object
    *
    *@param  adaptee  Description of Parameter
    */
   GenParsePanel_Parse_actionAdapter(GenParsePanel adaptee) {
      this.adaptee = adaptee;
   }

   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   public void actionPerformed(ActionEvent e) {
      adaptee.Parse_actionPerformed(e);
   }
}

class GenParsePanel_jButtonLoadPhases_actionAdapter implements java.awt.event.ActionListener {
   GenParsePanel adaptee;

   GenParsePanel_jButtonLoadPhases_actionAdapter(GenParsePanel adaptee) {
      this.adaptee = adaptee;
   }
   public void actionPerformed(ActionEvent e) {
      adaptee.jButtonLoadPhases_actionPerformed(e);
   }
}
