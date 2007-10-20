package org.smartfrog.tools.gui.browser;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Insets;


/**
 *  Title: SmartFrog CVS Description:

 */

public class ProcessPanel extends JPanel {
   /**
    *  Description of the Field
    */
   public MngProcess mngProcess = null;

   // Icons
   ImageIcon imageRun;
   ImageIcon imageStop;
   ImageIcon imageKill;
   ImageIcon imageAdd;
   ImageIcon imageDelete;
   ImageIcon imageRefresh;
   ImageIcon imagePreferences;
   ImageIcon imageRunAll;
   ImageIcon imageSaveAll;
   ImageIcon imageKillAll;
   ImageIcon imageOpen;


   BorderLayout borderLayout1 = new BorderLayout();
   JButton AddjButton = new JButton();
   JButton KilljButton = new JButton();
   JToolBar jToolBar1 = new JToolBar();
   JButton RefreshjButton = new JButton();
   JButton StartPjButton = new JButton();
   JButton DeletejButton = new JButton();
   JButton StopPjButton = new JButton();
   JTable processTable = new JTable();
   JScrollPane jScrollPane = new JScrollPane();
   JTextField cmdText = new JTextField();
   JLabel processNamejLabel = new JLabel();
   JLabel commandjLabel = new JLabel();
   JPanel statusjPanel = new JPanel();
   JTextField processNameText = new JTextField();
   GridBagLayout gridBagLayout1 = new GridBagLayout();
   JButton jButtonStartAll = new JButton();
   JButton jButtonKillAll = new JButton();
   JButton jButtonSaveAll = new JButton();
   JButton jButtonLoadList = new JButton();


   /**
    *  Constructor for the ProcessPanel object
    */
   public ProcessPanel() {
      try {
         jbInit();
         mngProcess = new MngProcess();
         mngProcess.loadIniFile();
         String[] title = {"Process Name", "Status", "Cmd"};
         processTable.setModel(new DefaultTableModel(this.mngProcess.getListProcesses(), title));
         //processTable.sizeColumnsToFit(processTable.AUTO_RESIZE_ALL_COLUMNS);
         TableUtilities.setColumnWidths (processTable, new Insets(4, 4, 4, 4), true, false);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }



   /**
    *  Description of the Method
    */
   public void refresh() {
      String[] title = {"Process Name", "Status", "Cmd"};
      processTable.setModel(new DefaultTableModel(this.mngProcess.getListProcesses(), title));
      //processTable.sizeColumnsToFit(processTable.AUTO_RESIZE_ALL_COLUMNS);
      TableUtilities.setColumnWidths (processTable, new Insets(4, 4, 4, 4), true, false);
   }


   /**
    *  Description of the Method
    */
   public void saveAll() {
      this.mngProcess.saveIniFile();
   }


   /**
    *  Description of the Method
    *
    *@exception  Exception  Description of Exception
    */
   void jbInit() throws Exception {

      imageRun = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.getResource("ExecuteProject.gif"));
      imageStop = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.getResource("Stop.gif"));
      imageKill = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.getResource("Hide.gif"));
      imageRunAll = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.getResource("RunAll.gif"));
      imageOpen = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.getResource("OpenArrow.gif"));
      imageSaveAll = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.getResource("SaveAll.gif"));
      imageKillAll = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.getResource("KillAll.gif"));
      imageAdd = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.getResource("UpdateRow.gif"));
      imageDelete = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.getResource("DeleteRow.gif"));
      imageRefresh = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.getResource("NewSheet.gif"));
      imagePreferences = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.getResource("Options.gif"));

      this.setLayout(borderLayout1);
      AddjButton.setIcon(imageAdd);
      AddjButton.addActionListener(new ProcessPanel_AddjButton_actionAdapter(this));
      AddjButton.setActionCommand("AddjButton");
      AddjButton.setToolTipText("Add");
      KilljButton.setIcon(imageKill);
      KilljButton.addActionListener(new ProcessPanel_KilljButton_actionAdapter(this));
      KilljButton.setActionCommand("KilljButton");
      KilljButton.setToolTipText("Kill");
      RefreshjButton.setToolTipText("Refresh");
      RefreshjButton.setActionCommand("RefreshjButton");
      RefreshjButton.setIcon(imageRefresh);
      RefreshjButton.addActionListener(new ProcessPanel_RefreshjButton_actionAdapter(this));
      StartPjButton.setToolTipText("Start");
      StartPjButton.setActionCommand("StartPjButton");
      StartPjButton.setIcon(imageRun);
      StartPjButton.addActionListener(new ProcessPanel_StartPjButton_actionAdapter(this));
      DeletejButton.setToolTipText("Delete");
      DeletejButton.setActionCommand("DeletejButton");
      DeletejButton.setIcon(imageDelete);
      DeletejButton.addActionListener(new ProcessPanel_DeletejButton_actionAdapter(this));
      StopPjButton.setToolTipText("Stop");
      StopPjButton.setActionCommand("StopPjButton");
      StopPjButton.setIcon(imageStop);
      StopPjButton.addActionListener(new ProcessPanel_StopPjButton_actionAdapter(this));
      cmdText.setText("java -version");
      cmdText.addActionListener(new ProcessPanel_cmdText_actionAdapter(this));
      processNamejLabel.setText(" Process: ");
      commandjLabel.setText(" Command: ");
      statusjPanel.setLayout(gridBagLayout1);
      jScrollPane.setAutoscrolls(true);
      processNameText.setText("Example(JavaVersion)");
      this.addFocusListener(new ProcessPanel_this_focusAdapter(this));
      jButtonStartAll.setToolTipText("Start All");
      jButtonStartAll.setIcon(imageRunAll);
      jButtonStartAll.addActionListener(new ProcessPanel_jButtonStartAll_actionAdapter(this));
      jButtonKillAll.setToolTipText("Kill All");
      jButtonKillAll.setIcon(imageKillAll);
      jButtonKillAll.addActionListener(new ProcessPanel_jButtonKillAll_actionAdapter(this));
      jButtonSaveAll.setToolTipText("Save list of processes");
      jButtonSaveAll.setIcon(imageSaveAll);
    jButtonSaveAll.addActionListener(new ProcessPanel_jButtonSaveAll_actionAdapter(this));
      jButtonLoadList.setToolTipText("Load list of processes");
      jButtonLoadList.setIcon(imageOpen);
      jButtonLoadList.addActionListener(new ProcessPanel_jButtonLoadList_actionAdapter(this));
      jToolBar1.add(StartPjButton, null);

      jToolBar1.add(StopPjButton, null);
      jToolBar1.add(KilljButton, null);
      jToolBar1.addSeparator();
      jToolBar1.add(jButtonStartAll, null);
      jToolBar1.add(jButtonKillAll, null);
      jToolBar1.addSeparator();
      jToolBar1.add(AddjButton, null);
      jToolBar1.add(DeletejButton, null);
      jToolBar1.add(RefreshjButton, null);
      jToolBar1.addSeparator();
      jToolBar1.add(jButtonLoadList, null);
      jToolBar1.add(jButtonSaveAll, null);

      this.add(jScrollPane, BorderLayout.CENTER);
      this.add(statusjPanel, BorderLayout.SOUTH);
      statusjPanel.add(processNamejLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 4));
      statusjPanel.add(cmdText, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0
            , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 1), 250, 0));
      statusjPanel.add(processNameText, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 49, 0));
      statusjPanel.add(commandjLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 13, 0, 0), 1, 4));

      jScrollPane.getViewport().add(processTable, null);
      this.add(jToolBar1, BorderLayout.NORTH);
   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   void RefreshjButton_actionPerformed(ActionEvent e) {
      this.refresh();
   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   void StartPjButton_actionPerformed(ActionEvent e) {
      try {
         String processName = (String) processTable.getValueAt(processTable.getSelectedRow(), 0);
         this.mngProcess.startProcess(processName);
      } catch (Exception ex) {
         //((MainFrame)this.getParent()).log(ex.getMessage(), "StartProcess", 5);
      }

   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   void StopPjButton_actionPerformed(ActionEvent e) {
      try {
         String processName = (String) processTable.getValueAt(processTable.getSelectedRow(), 0);
         this.mngProcess.stopProcess(processName);
      } catch (Exception ex) {
         //((MainFrame)this.getParent()).log(ex.getMessage(), "StartProcess", 5);
      }
   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   void KilljButton_actionPerformed(ActionEvent e) {
      try {
         String processName = (String) processTable.getValueAt(processTable.getSelectedRow(), 0);
         this.mngProcess.killProcess(processName);
      } catch (Exception ex) {
         //((MainFrame)this.getParent()).log(ex.getMessage(), "StartProcess", 5);
      }

   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   void AddjButton_actionPerformed(ActionEvent e) {
//      InfoProcess infoProc = new InfoProcess(this.processNameText.getText(),this.cmdText.getText(),"");
      this.mngProcess.addProcess(new InfoProcess(this.processNameText.getText(), this.cmdText.getText(), "."), true);
      this.refresh();
   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   void DeletejButton_actionPerformed(ActionEvent e) {
      try {
         String processName = (String) processTable.getValueAt(processTable.getSelectedRow(), 0);
         this.mngProcess.deleteProcess(processName);
         this.refresh();
      } catch (Exception ex) {
         //((MainFrame)this.getParent()).log(ex.getMessage(), "StartProcess", 5);
      }

   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   void this_focusGained(FocusEvent e) {
      this.refresh();
   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of the Parameter
    */
   void cmdText_actionPerformed(ActionEvent e) { }


   /**
    *  Description of the Method
    *
    *@param  e  Description of the Parameter
    */
   void jButtonStartAll_actionPerformed(ActionEvent e) {
      this.runAll();
   }

   public void runAll(){
     this.mngProcess.runAll();
   }

   /**
    *  Description of the Method
    *
    *@param  e  Description of the Parameter
    */
   void jButtonKillAll_actionPerformed(ActionEvent e) {
      this.mngProcess.killAll();
   }

  void jButtonSaveAll_actionPerformed(ActionEvent e) {
      this.mngProcess.saveIniFile();
  }

   void jButtonLoadList_actionPerformed(ActionEvent e) {
      this.mngProcess.killAll();
      this.mngProcess.loadIniFile();
      this.refresh();
   }

}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class ProcessPanel_RefreshjButton_actionAdapter implements java.awt.event.ActionListener {


   ProcessPanel adaptee;


   /**
    *  Constructor for the ProcessPanel_RefreshjButton_actionAdapter object
    *
    *@param  adaptee  Description of Parameter
    */
   ProcessPanel_RefreshjButton_actionAdapter(ProcessPanel adaptee) {
      this.adaptee = adaptee;
   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   public void actionPerformed(ActionEvent e) {
      adaptee.RefreshjButton_actionPerformed(e);
   }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    27 September 2001
 */
class ProcessPanel_StartPjButton_actionAdapter implements java.awt.event.ActionListener {


   ProcessPanel adaptee;


   /**
    *  Constructor for the ProcessPanel_StartPjButton_actionAdapter object
    *
    *@param  adaptee  Description of Parameter
    */
   ProcessPanel_StartPjButton_actionAdapter(ProcessPanel adaptee) {
      this.adaptee = adaptee;
   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   public void actionPerformed(ActionEvent e) {
      adaptee.StartPjButton_actionPerformed(e);
   }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    27 September 2001
 */
class ProcessPanel_StopPjButton_actionAdapter implements java.awt.event.ActionListener {


   ProcessPanel adaptee;


   /**
    *  Constructor for the ProcessPanel_StopPjButton_actionAdapter object
    *
    *@param  adaptee  Description of Parameter
    */
   ProcessPanel_StopPjButton_actionAdapter(ProcessPanel adaptee) {
      this.adaptee = adaptee;
   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   public void actionPerformed(ActionEvent e) {
      adaptee.StopPjButton_actionPerformed(e);
   }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    27 September 2001
 */
class ProcessPanel_KilljButton_actionAdapter implements java.awt.event.ActionListener {


   ProcessPanel adaptee;


   /**
    *  Constructor for the ProcessPanel_KilljButton_actionAdapter object
    *
    *@param  adaptee  Description of Parameter
    */
   ProcessPanel_KilljButton_actionAdapter(ProcessPanel adaptee) {
      this.adaptee = adaptee;
   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   public void actionPerformed(ActionEvent e) {
      adaptee.KilljButton_actionPerformed(e);
   }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    27 September 2001
 */
class ProcessPanel_AddjButton_actionAdapter implements java.awt.event.ActionListener {


   ProcessPanel adaptee;


   /**
    *  Constructor for the ProcessPanel_AddjButton_actionAdapter object
    *
    *@param  adaptee  Description of Parameter
    */
   ProcessPanel_AddjButton_actionAdapter(ProcessPanel adaptee) {
      this.adaptee = adaptee;
   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   public void actionPerformed(ActionEvent e) {
      adaptee.AddjButton_actionPerformed(e);
   }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    27 September 2001
 */
class ProcessPanel_DeletejButton_actionAdapter implements java.awt.event.ActionListener {


   ProcessPanel adaptee;


   /**
    *  Constructor for the ProcessPanel_DeletejButton_actionAdapter object
    *
    *@param  adaptee  Description of Parameter
    */
   ProcessPanel_DeletejButton_actionAdapter(ProcessPanel adaptee) {
      this.adaptee = adaptee;
   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   public void actionPerformed(ActionEvent e) {
      adaptee.DeletejButton_actionPerformed(e);
   }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    27 September 2001
 */
class ProcessPanel_this_focusAdapter extends java.awt.event.FocusAdapter {


   ProcessPanel adaptee;


   /**
    *  Constructor for the ProcessPanel_this_focusAdapter object
    *
    *@param  adaptee  Description of Parameter
    */
   ProcessPanel_this_focusAdapter(ProcessPanel adaptee) {
      this.adaptee = adaptee;
   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   public void focusGained(FocusEvent e) {
      adaptee.this_focusGained(e);
   }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    09 December 2001
 */
class ProcessPanel_cmdText_actionAdapter implements java.awt.event.ActionListener {


   ProcessPanel adaptee;


   /**
    *  Constructor for the ProcessPanel_cmdText_actionAdapter object
    *
    *@param  adaptee  Description of the Parameter
    */
   ProcessPanel_cmdText_actionAdapter(ProcessPanel adaptee) {
      this.adaptee = adaptee;
   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of the Parameter
    */
   public void actionPerformed(ActionEvent e) {
      adaptee.cmdText_actionPerformed(e);
   }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    09 December 2001
 */
class ProcessPanel_jButtonStartAll_actionAdapter implements java.awt.event.ActionListener {


   ProcessPanel adaptee;


   /**
    *  Constructor for the ProcessPanel_jButtonStartAll_actionAdapter object
    *
    *@param  adaptee  Description of the Parameter
    */
   ProcessPanel_jButtonStartAll_actionAdapter(ProcessPanel adaptee) {
      this.adaptee = adaptee;
   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of the Parameter
    */
   public void actionPerformed(ActionEvent e) {
      adaptee.jButtonStartAll_actionPerformed(e);
   }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    09 December 2001
 */
class ProcessPanel_jButtonKillAll_actionAdapter implements java.awt.event.ActionListener {


   ProcessPanel adaptee;


   /**
    *  Constructor for the ProcessPanel_jButtonKillAll_actionAdapter object
    *
    *@param  adaptee  Description of the Parameter
    */
   ProcessPanel_jButtonKillAll_actionAdapter(ProcessPanel adaptee) {
      this.adaptee = adaptee;
   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of the Parameter
    */
   public void actionPerformed(ActionEvent e) {
      adaptee.jButtonKillAll_actionPerformed(e);
   }
}

class ProcessPanel_jButtonSaveAll_actionAdapter implements java.awt.event.ActionListener {
  ProcessPanel adaptee;

  ProcessPanel_jButtonSaveAll_actionAdapter(ProcessPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonSaveAll_actionPerformed(e);
  }
}

class ProcessPanel_jButtonLoadList_actionAdapter implements java.awt.event.ActionListener {
   ProcessPanel adaptee;

   ProcessPanel_jButtonLoadList_actionAdapter(ProcessPanel adaptee) {
      this.adaptee = adaptee;
   }
   public void actionPerformed(ActionEvent e) {
      adaptee.jButtonLoadList_actionPerformed(e);
   }
}
