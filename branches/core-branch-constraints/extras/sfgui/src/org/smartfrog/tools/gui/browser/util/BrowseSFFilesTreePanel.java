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

//import org.smartfrog.tools;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.util.HashMap;
//import javax.swing.table.DefaultTableModel;
import org.smartfrog.tools.gui.browser.MainFrame;
import org.gjt.sp.jedit.textarea.*;

/**
 *  Title: SerranoGuiUtils Package Description: Copyright: Copyright (c) 2001
 *  Company: HP Labs Bristol
 *
 *@author     Julio Guijarro
 *@created    12 December 2001
 *@version    1.0
 */

public class BrowseSFFilesTreePanel extends JPanel implements TreeSelectionListener {

   String[] filters = {".sf",".sf2",".sfcd",".sfxml"};

   BorderLayout borderLayout1 = new BorderLayout();
   JSplitPane jSplitPane1 = new JSplitPane();
   BorderLayout borderLayout2 = new BorderLayout();
   JScrollPane treeScrollPane = new JScrollPane();
   BrowseSFFilesPanel browsePanel = null;//new BrowseSFFilesPanel(this);
   JTree systemViewTree = new JTree();
   //JTable table = new JTable();
   //Tree
   BrowseTreeModel treeModel;

   /**
    *  Constructor for the BrowseSFFilesTreePanel object
    */
   public BrowseSFFilesTreePanel() {
      try {
         //browsePanel = new BrowseSFFilesPanel();
         jbInit();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }


   /**
    *  Constructor for the BrowseSFFilesTreePanel object
    */
   public BrowseSFFilesTreePanel(MainFrame mainF) {
      try {
         treeModel = new BrowseTreeModel();
         browsePanel = new BrowseSFFilesPanel(mainF,this.treeModel.getEntry());
         jbInit();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   /**
    *  Description of the Method
    *
    *@exception  Exception  Description of Exception
    */
   void jbInit() throws Exception {
      treeInit();
      this.setLayout(borderLayout2);
      //jSplitPane1.setMinimumSize(new Dimension(189, 200));
      //jSplitPane1.setPreferredSize(new Dimension(300, 300));
      jSplitPane1.setLastDividerLocation(200);
      //40% of the space goes to left/top
  //jSplitPane1.setResizeWeight(0.4);
      //jSplitPane1.setDividerLocation(200);
      jSplitPane1.add(treeScrollPane, JSplitPane.LEFT);
      this.add(jSplitPane1, BorderLayout.CENTER);
      jSplitPane1.add(browsePanel, JSplitPane.RIGHT);
      treeScrollPane.getViewport().add(systemViewTree, null);
   }

   /**
    *  Description of the Method
    */
   private void treeInit() {
      systemViewTree = new JTree(treeModel);
      //Listen for when the selection changes.
      systemViewTree.addTreeSelectionListener(this);
      //systemViewJTree.setPreferredSize(new Dimension(200, 100));
   }

   /**
    *  Description of the Method
    *
    *@param  msg  Description of Parameter
    */
   public void add(String msg) {
      treeModel.add(msg);
      //systemViewJTree.updateUI();
   }


   /**
    *  This method handles with tree events
    *
    *@param  e  Description of Parameter
    */
   public void valueChanged(TreeSelectionEvent e) {
      Object treeNode = systemViewTree.getLastSelectedPathComponent();
      //System.out.println(treeNode.getClass());
      if (treeNode == null) {
         return;
      }
      if (treeNode instanceof BrowseEntry) {
        String filter = "";
        for (int i=0;i<filters.length;i++){
           filter = filters[i];
           if (((BrowseEntry)treeNode).getRDN().endsWith(filter)){
              String fileURL=((BrowseEntry)treeNode).getDN();
              fileURL=fileURL.substring("#include /".length(),fileURL.length());
              this.browsePanel.loadFile(fileURL);
              break;
           }
        }
      }
   }

   /**
    *  Description of the Method
    */
   public void refresh() {
//      String[] title = {"Attribute", "Value"};
//      Object[][] data = {{" ", " "}};
//      this.table.setModel(new DefaultTableModel(data, title));
      this.systemViewTree.updateUI();
//      this.table.repaint();
   }

  /**
    *  Gets the selectedFile attribute of the BrowseSFFilesPanel object
    *
    *@return    The selectedFile value
    */
   public String getSelectedFile() {
      return this.browsePanel.getSelectedFile();
   }

  /**
    *  Gets the textArea attribute of the BrowseSFFilesPanel object
    *
    *@return    The textArea value
    */
   public JEditTextArea getTextArea() {
      return this.browsePanel.getTextArea();
   }

   /**
    *  Description of the Method
    *
    *@param  args  Description of Parameter
    */
   public static void main(String args[]) {
      JFrame mainFrame = new JFrame("Text BrowseSFFilesTreePanel", null);
      BrowseSFFilesTreePanel test = new BrowseSFFilesTreePanel();
      mainFrame.getContentPane().add(test);
      mainFrame.setVisible(true);
      System.out.println("Starting...a new adventure.");
      System.out.println("Adding: ------------------------------------------");
      test.add("dirA/dir1/file1.sf");
      test.add("dirA/dir2/file1.sf");
      test.add("dirB/dir1/file2.sf");
      System.out.println("...Finished");
   }

}
