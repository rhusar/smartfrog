package org.smartfrog.services.runcmd;

//import com.hp.sfTools;

/**
 *  Title: SerranoGuiUtils Package Description: Copyright: Copyright (c) 2001
 *  Company: HP Labs Bristol
 *
 *@author     Julio Guijarro
 *@version    1.0
 */

//import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.common.*;
//import org.smartfrog.sfcore.reference.*;

//import java.rmi.*;
import java.rmi.RemoteException;
//import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import org.smartfrog.services.display.SFDisplay;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
//import org.smartfrog.services.runcmd.ProcessPanel;
import org.smartfrog.services.runcmd.RunCommandInt;

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    23 October 2001
 */


import java.util.Enumeration;
import org.smartfrog.sfcore.common.Context;

public class SFMngRunCmds extends SFDisplay implements ActionListener {

   /**
    *  Description of the Field
    */
   protected JButton refresh = new JButton();

   private JPanel processPanel = null;
//   private JScrollPane scrollPane = null;


   /**
    *  Constructor for the SFDeployDisplay object
    *
    *@exception  RemoteException  Description of Exception
    */
   public SFMngRunCmds() throws RemoteException {
      super();
   }

   /**
    *  The main program for the SFDeployDisplay class
    *
    *@param  args  The command line arguments
    */
//   public static void main(String[] args) {
//     try {
//      SFMngRunCmds mngRunCmds = new SFMngRunCmds();
//     } catch (Exception e){};
//   }


   /**
    *  Description of the Method
    *
    *@exception  Exception  Description of Exception
    */
   public void sfDeploy() throws SmartFrogException , RemoteException{
      super.sfDeploy();
      // We add the new Tree component here
      try {
         this.processPanel = new ProcessPanel();
         this.processPanel.setEnabled(true);
         display.tabPane.add(processPanel, "Management RunCommand Processes ...", 0);
         //this.display.screen.append("\n Version sfServices: " + org.smartfrog.services.Version.getVersion() + "\n");
         // Button for Refresh view ...
         refresh.setText("Refresh");
         refresh.setActionCommand("refreshButton");
         refresh.addActionListener(this);
         display.mainToolBar.add(this.refresh);
         display.showToolbar(true);


      } catch (Exception ex) {
         System.out.println("Failure sfDeploy SFMngRunCmds!");
         ex.printStackTrace();
      }


   }

   /**
    *  Description of the Method
    *
    *@exception  Exception  Description of Exception
    */
   public void sfStart() throws SmartFrogException, RemoteException {
      super.sfStart();

      //Processes already created at this stage (so we can get info about them)
      try{
         Object value=null;
         // loop to add all the runCmd found in the context!
         for (Enumeration e = sfContext().keys(); e.hasMoreElements(); ) {
            Object key = e.nextElement();
            value = sfContext().get(key);
            value=this.resolveLink(value);
            if (value instanceof RunCommandInt) {
               (((ProcessPanel)this.processPanel).getMngProcess()).addProcess((RunCommandInt)value,true);
            }
         }
         this.refresh();
      } catch (Exception ex) {
         System.out.println("Failure sfStart SFMngRunCmds!");
         ex.printStackTrace();
      }
   }

 private Object resolveLink (Object value){
      try {
         if (value instanceof Prim) {
            return value;
         }
         else if (value instanceof Reference) {
            do {
                value =((Prim)this).sfResolve((Reference)value);
            } while (value instanceof Reference);
         }
      } catch (Exception ex) {
         System.out.println("Error resolving context: " + ex);
         //return new DeployEntry((ex.getMessage()+(value.toString())));
      }
      return value;
 }



   /**
    *  Description of the Method
    *
    *@param  t  Description of Parameter
    */
   public void sfTerminateWith(TerminationRecord t) {
      super.sfTerminateWith(t);
   }

   /**
    *  Main processing method for the SFDeployDisplay object
    */
   public void run() { }

   /**
    *  Description of the Method
    *
    *@param  msg  Description of Parameter
    */
   public void printMsg(String msg) {
      super.printMsg(msg);
      // We print in the output
      //((DeployTreePanel)panelTree).add(msg);

   }

   /**
    *  Description of the Method
    */
   public void refresh() {
      try {
         ((ProcessPanel)processPanel).refresh();
      } catch (Exception ex) {
         System.out.println("Failure refresh() SFMngRunCmds!");
         ex.printStackTrace();
      }
   }

   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   public void actionPerformed(ActionEvent e) {
      //System.out.println("ActionEvent SFDEployDisplay: "+ e);
      if ((e.getActionCommand()).equals("refreshButton")) {
         refresh(e);
      }
   }


   /**
    *  Description of the Method
    *
    *@param  e  Description of Parameter
    */
   private void refresh(ActionEvent e) {
      this.refresh();
   }




}
