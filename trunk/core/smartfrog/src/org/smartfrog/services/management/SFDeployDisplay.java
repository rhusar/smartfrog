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
package org.smartfrog.services.management;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;

import org.smartfrog.services.display.Display;
import org.smartfrog.services.display.SFDisplay;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.services.display.WindowUtilities;
import org.smartfrog.sfcore.common.Logger;


/**
 *  Management Console component. It can be deployed as a normat SmartFrog
 *  component or it can be started as a separate console.
 */
public class SFDeployDisplay extends SFDisplay implements ActionListener {
   protected JButton refresh = new JButton();
   private JPanel panelTree = null;
   private JTree tree = null;
   private JScrollPane scrollPaneTree = null;


   /**
    * Constructs SFDeployDisplay object
    *
    *@throws  RemoteException  If network or RMI error
    */
   public SFDeployDisplay() throws RemoteException {
      super();
   }


   /**
    *  The main program for the SFDeployDisplay class
    *
    *@param  args           The command line arguments
    *@throws  Exception  If any error
    */
   public static void main(String[] args) throws Exception {
      Logger.log("Starting management window...");

      String nameDisplay = "sfManagementConsole";
      int height = 440;
      int width = 600;
      OptionSet opts = new OptionSet(args);

      if (opts.errorString != null) {
         exitWith(opts.errorString);
      }

      final boolean showRootProcess = opts.showRootProcess;
      final String hostname = opts.host;
      final int port = opts.port;
      String positionDisplay = opts.windowPosition;

      try {
         startConsole(nameDisplay, height, width, positionDisplay,
               showRootProcess, hostname, port, true);
      } catch (java.net.UnknownHostException uex) {
         exitWith("Error: Unknown host.");
      } catch (java.rmi.ConnectException cex) {
         exitWith("Error: " + cex.getMessage());
      } catch (Exception e) {
         e.printStackTrace();
         exitWith("Error in SFDeployDisplay.main():" + e);
      }

      Logger.log("Running.");
   }


   /**
    * Starts the console
    *
    *@param  nameDisplay       name of the display
    *@param  height            height of the window
    *@param  width             width of the window
    *@param  positionDisplay   position  of display
    *@param  showRootProcess   boolean to enable display of root process
    *@param  hostname          host name
    *@param  port              port
    *@param  shouldSystemExit  boolean to indicate exit at close of window
    *@return                   Display object
    *@throws  Exception     In case of any error
    *
    */


   public static Display startConsole(String nameDisplay, int height,
         int width, String positionDisplay, final boolean showRootProcess,
         final String hostname, final int port, boolean shouldSystemExit)
          throws Exception {
      final JButton refresh;
      JMenu jMenuMng = new JMenu();
      final JCheckBoxMenuItem jCheckBoxMenuItemShowRootProcessPanel = new JCheckBoxMenuItem();
      String infoConnection = ("sfManagementConsole connecting to " +
            hostname + ":" + port);
      System.out.print(infoConnection);
      nameDisplay = nameDisplay + " [" + "sfManagementConsole connected to " +
            hostname + ":" + port + "]";

      if (showRootProcess) {
         Logger.log(" showing rootProcess");
      } else {
         //System.out.println("");
      }

      final Display display;

      if (org.smartfrog.services.display.WindowUtilities.areGraphicsAvailable()) {
         display = new Display(nameDisplay, null);
         display.setShouldSystemExit(shouldSystemExit);
         display.setVisible(false);
         display.setSize(width, height);
         org.smartfrog.services.display.WindowUtilities.setPositionDisplay(null,
               display, positionDisplay);

         // Button for Refresh view ...
         refresh = new JButton();
         refresh.setText("Reload panels");
         refresh.setActionCommand("refreshButton");
         refresh.addActionListener(
            new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  //System.out.println("ActionEvent SFDEployDisplay: "+ e);
                  if ((e.getActionCommand()).equals("refreshButton")) {
                     display.cleanAddedPanels();
                     try {
                        addProcessesPanels(display, jCheckBoxMenuItemShowRootProcessPanel.isSelected(), //showRootProcess,
                              hostname, port);
                     } catch (Exception ex) {
                        Logger.log(ex);
//                        System.err.println();
//                        ex.printStackTrace();
                        exitWith("Error in SFDeployDisplay.refresh():" +
                              ex);
                     }
                  }
               }
            });
         display.mainToolBar.add(refresh);
         display.showToolbar(true);
         // Add deployTreePanel menu items
         jMenuMng.setText("Mng. Console");
         display.jMenuBarDisplay.add(jMenuMng);
         jCheckBoxMenuItemShowRootProcessPanel.setSelected(showRootProcess);
         jCheckBoxMenuItemShowRootProcessPanel.setText("Show rootProcess");
         jCheckBoxMenuItemShowRootProcessPanel.setAccelerator(javax.swing.KeyStroke.getKeyStroke(77, java.awt.event.KeyEvent.CTRL_MASK | java.awt.event.KeyEvent.ALT_MASK, false));
         jCheckBoxMenuItemShowRootProcessPanel.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                            refresh.doClick();
               }
         });
         jMenuMng.add(jCheckBoxMenuItemShowRootProcessPanel);

         display.setVisible(true);
         addProcessesPanels(display, showRootProcess, hostname, port);

         return display;
      }

      return null;
   }


   /**
    *  Prints given error string and exits system
    *
    *@param  str  string to print on out
    */
   public static void exitWith(String str) {
      if (str != null) {
         System.err.println(str);
      }

      System.exit(1);
   }


   // Used in Main
   /**
    *  Adds a feature to the ProcessesPanels attribute of the SFDeployDisplay
    *  class
    *
    *@param  display              The feature to be added to the ProcessesPanels
    *      attribute
    *@param  addRootProcessPanel  The feature to be added to the ProcessesPanels
    *      attribute
    *@param  hostname             The feature to be added to the ProcessesPanels
    *      attribute
    *@param  port                 The feature to be added to the ProcessesPanels
    *      attribute
    *@throws  Exception        If any error
    */
   public static void addProcessesPanels(Display display,
         boolean addRootProcessPanel, String hostname, int port)
          throws Exception {
      int indexPanel = 0;

      // Adding pannels
      JPanel panelTree = null;

      if (addRootProcessPanel) {
         panelTree = new DeployTreePanel(SFProcess.getRootLocator()
               .getRootProcessCompound(InetAddress.getByName(
               hostname), port), true);
         panelTree.setEnabled(true);
         display.tabPane.add(panelTree, "rootProcess...", indexPanel++);
      }

      Context context = SFProcess.getRootLocator()
            .getRootProcessCompound(InetAddress.getByName(
            hostname), port).sfContext();
      java.util.Enumeration keys = context.keys();
      Object key = "";
      Object value = "";

      while (keys.hasMoreElements()) {
         key = keys.nextElement();

         //out.println("* " + key + ": " + (context.get((String)key)).toString());
         value = context.get((String) key);

         //System.out.println("* " + key + ": " + value.toString());
         if (value instanceof Prim) {
            if (((Prim) value).sfParent() == null) {
               panelTree = new DeployTreePanel(value, false);
               panelTree.setEnabled(true);
               display.tabPane.add(panelTree, key, indexPanel++);
            }
         }
      }
   }


   /**
    * Deploys display component.
    *
    *@throws  SmartFrogException  If unable to deploy the component
    *@throws  RemoteException     If RMI or network error
    */
   public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
//      try {
         super.sfDeploy();

         // We add the new Tree component here
         Object root = this.sfResolveWithParser(SmartFrogCoreKeys.SF_ROOT);

         //root= new CompoundImpl();
         //System.out.println("Root: "+root.toString());
         this.panelTree = new DeployTreePanel(root, false);
         this.panelTree.setEnabled(true);

         display.tabPane.add(panelTree, "Deploy Deployed System ...", 0);

         this.display.screen.append("\n Version: " +
               org.smartfrog.Version.versionString + "\n");

         // Button for Refresh view ...
         refresh.setText("Refresh");
         refresh.setActionCommand("refreshButton");
         refresh.addActionListener(this);
         display.mainToolBar.add(this.refresh);
         display.showToolbar(true);
      //end panelTree example
   }


   /**
    * Starts the display component.
    *
    *@throws  SmartFrogException  If unable to start the component
    *@throws  RemoteException     If RMI or network error
    */
   public synchronized void sfStart() throws SmartFrogException, RemoteException {
      super.sfStart();
   }


   /**
    * Terminates the SFDeployDisplay component
    *
    *@param  t  The reason why it was terminated
    */
   public synchronized void sfTerminateWith(TerminationRecord t) {
      this.display.dispose();
      super.sfTerminateWith(t);
   }


   /**
    *  Main processing method for the SFDeployDisplay object
    */
   public void run() { }


   /**
    * Method of interface PrintMsgInt
    *
    *@param  msg  message to print
    */
   public void printMsg(String msg) {
      super.printMsg(msg);

      // We print in the output
      ((DeployTreePanel) panelTree).add(msg);
   }


   /**
    *  Refreshes the display panel.
    */
   public void refresh() {
      try {
         //System.out.println("Refreshing info");
         Object root = this.sfResolve(SmartFrogCoreKeys.SF_ROOT,true);
         ((DeployTreePanel) panelTree).setModel(root);
         ((DeployTreePanel) panelTree).refresh();
      } catch (Throwable ex) {
         Logger.logQuietly("Failure refresh() SFDeployDisplay!",ex);
      }
   }


   /**
    * Interface Method.
    *
    *@param  e  action event
    */
   public void actionPerformed(ActionEvent e) {
      Logger.log("ActionEvent SFDEployDisplay: "+ e);
      if ((e.getActionCommand()).equals("refreshButton")) {
         refresh(e);
      }

   }


   /**
    * Interface Method.
    *
    *@param  e  acrtion event
    */
   private void refresh(ActionEvent e) {
      this.refresh();
   }

}
