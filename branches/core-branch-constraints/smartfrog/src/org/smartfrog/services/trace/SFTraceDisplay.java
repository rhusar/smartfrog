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

package org.smartfrog.services.trace;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.smartfrog.services.display.SFDisplay;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.Logger;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Implements the display for the trace component.
 *
 * @deprecated 12 December 2001
 */
public class SFTraceDisplay extends SFDisplay implements ActionListener {
    /** Tree panel. */
    private JPanel panelTree = null;
    /** Tree. */
    private JTree tree = null;
    /** Tree Scrollpane. */
    private JScrollPane scrollPaneTree = null;
    /** Button for Refresh view. */
    protected JButton refresh = new JButton();

    /**
     * Constructor for the SFTraceDisplay object.
     *
     * @throws RemoteException In case of network/rmi error
     */
    public SFTraceDisplay() throws RemoteException {
        super();
    }

    /**
     * Deploys the display trace component.
     *
     * @throws SmartFrogException In case of error while deployment
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
    RemoteException {
//        try {
            super.sfDeploy();

            // We add the new Tree component here
            //panel Tree Class. Example to extend Display ;-)
            String rootDN = SmartFrogCoreKeys.SF_ROOT;
            String rootLocatorPort = "";

            try {
                rootLocatorPort = SFProcess.getProcessCompound()
                                           .sfResolve(SmartFrogCoreKeys.SF_ROOT_LOCATOR_PORT)
                                           .toString();
            } catch (Exception ex) {
                System.out.println("Exception deployment:" + ex.toString());
            }

            rootDN = rootDN + "[" + rootLocatorPort + "]>";
            this.panelTree = new TraceTreePanel(rootDN);
            this.panelTree.setEnabled(true);

            try {
                display.tabPane.add(panelTree, "Trace Deployed System ...", 0);
            } catch (Exception ex) {
            }

            // Button for Refresh view ...
            refresh.setText("Refresh");
            refresh.setActionCommand("refreshButton");
            refresh.addActionListener(this);
            display.mainToolBar.add(this.refresh);
            display.showToolbar(true);
            boolean stepTraceBoolean = false;
            stepTraceBoolean = sfResolve("stepTrace",stepTraceBoolean, false);
            if (stepTraceBoolean) stepTrace = 0; else stepTrace =1;
        //end panelTree example
    }

    /**
     * Starts the display trace component.
     *
     * @throws SmartFrogException In case of error while starting the component
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized void sfStart() throws SmartFrogException,
    RemoteException {
        super.sfStart();
    }

    /**
     * Terminates the display trace component.
     *
     * @param t TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord t) {
        super.sfTerminateWith(t);
    }

    /**
     * Main processing method for the SFTraceDisplay object.
     */
    public void run() {
    }

    /**
     * Prints the message.
     *
     * @param msg message
     */
    public void printMsg(String msg) {
        super.printMsg(msg);

        // We print in the output
        ((TraceTreePanel) panelTree).add(msg);
        if (stepTrace == 0) getUserConfirmation(this.display, msg);
    }

    // Refresh Section

    /**
     * Refreshes SFTrcaeDisply.
     */
    public void refresh() {
        try {
            ((TraceTreePanel) panelTree).refresh();
        } catch (Exception ex) {
            if (sfLog().isIgnoreEnabled()){
              sfLog().ignore("Failure refresh() SFTraceDisplay!",ex);
            }
            //Logger.logQuietly("Failure refresh() SFTraceDisplay!",ex);
        }
    }

    /**
     * Action.
     *
     * @param e action event
     */
    public void actionPerformed(ActionEvent e) {
        if ((e.getActionCommand()).equals("refreshButton")) {
            refresh(e);
        }
    }

    /**
     * Refreshes the SFTraceDisplay.
     *
     * @param e action event
     */
    private void refresh(ActionEvent e) {
        this.refresh();
    }

    //--end refresh

    /**
     * The main program for the SFTraceDisplay class.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        //SFTraceDisplay SFTraceDisplay1 = new SFTraceDisplay();
    }

    static int stepTrace = 0;
//    static Object[] options = {"NEXT", "Finish"};
     /**
      * Asks the user confirmation
      *
      *@param  cp       Component
      *@param  message  Message
      *@return          The user confirmation value
      */
     public static boolean getUserConfirmation(Component cp, String message) {
        message=message.replace(',','\n');
        stepTrace = JOptionPane.showConfirmDialog(cp, message,
              "Please confirm...", JOptionPane.YES_NO_OPTION);

        if (stepTrace == 0) {
           return true;
        } else {
           return false;
        }
     }


}
