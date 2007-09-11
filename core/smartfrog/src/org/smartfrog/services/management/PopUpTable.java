/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.tree.TreePath;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.rmi.*;
import org.smartfrog.services.display.WindowUtilities;

/**
 *  Popup table UI Component.
 */
public class PopUpTable extends JComponent implements ActionListener {
   /** Pop up table. */
   JPopupMenu popupTable = new JPopupMenu();
   /** Tree. */
   JTree tempTree = null;
   /** Table. */
   JTable tempTable = null;
   /** x coordiante. */
   int tempX = 0;
   /** y coordinate. */
   int tempY = 0;
   /** Parent panel. */
   DeployTreePanel parent = null;

   /** Item for Tree popup menu - modify attribute. */
   JMenuItem menuItemModifyAttribute = new JMenuItem();
   /** Item for Tree popup menu - remove attribute. */
   JMenuItem menuItemRemoveAttribute = new JMenuItem();
    /** Item for Tree popup menu - remove attribute. */
   JMenuItem menuItemInstrospectValue = new JMenuItem();

   /**
    *  Constructs PopUpTable object
    */
   public PopUpTable() {
      popupInit();
   }


   /**
    *  Initializes the popup component.
    */
   void popupInit() {

      menuItemRemoveAttribute.setText("Remove Attribute");
      menuItemModifyAttribute.setText("Add/Modify Attribute");
      menuItemInstrospectValue.setText("Introspect Value");

      popupTable.add(menuItemRemoveAttribute);
      popupTable.add(menuItemModifyAttribute);
      popupTable.add(menuItemInstrospectValue);
      menuItemRemoveAttribute.addActionListener(this);
      menuItemModifyAttribute.addActionListener(this);
      menuItemInstrospectValue.addActionListener(this);
   }


   /**
    *  Gets the popupMenu attribute of the PopUpTable object
    *
    *@return    The popupMenu value
    */
   public JPopupMenu getPopupMenu() {
      return popupTable;
   }


   /**
    * Displays the popup table object.
    *
    *@param  compTree   JTree object
    *@param  compTable  JTable object
    *@param  x          X Coordinate
    *@param  y          Y Coordinate
    *@param  parentPanel     Reference to parent component
    */
   public void show(JTree compTree, JTable compTable, int x, int y, DeployTreePanel parentPanel) {
      tempTree = compTree;
      tempTable = compTable;
      tempX = x;
      tempY = y;
      this.parent = parentPanel;
      popupTable.show((Component) compTable, x, y);
   }


   /**
    * Interface method.
    *
    *@param  e  Action Event object
    */
   public void actionPerformed(ActionEvent e) {
      String path;
      Object source = e.getSource();
      int row = -1;
      TreePath tpath = (tempTree).getSelectionPath();
      row = tempTable.getSelectedRow();

      path = treePath2Path(tpath);

      //System.out.println(" path "+path+", parentcopy: "+ isParentNodeACopy() +", node copy"+ isNodeACopy()+", Action: "+ e);

      if (source == menuItemRemoveAttribute) {
         if (row == -1) {
            if (sfLog().isErrorEnabled()) sfLog().error("No selected Cell");
            WindowUtilities.showError(this,"No selected Cell");
            return;
         }
         if (isNodeACopy()){
                WindowUtilities.showError(this,"The node selected is a copy and no 'remove' action can be applied\n Use a console running in the local process of this node");
                return;
         }
         remove(getNode(), (String) (tempTable.getValueAt(row, 0)));

         // Entry pointed in the tree
      } else if (source == menuItemModifyAttribute) {
          if (isNodeACopy()){
                 WindowUtilities.showError(this,"The node selected is a copy and no 'modify' action can be applied\n Use a console running in the local process of this node");
                 return;
          }
         Object name = null;
         Object value = null;
         Object tags = null;

         if (row == -1) {
            name = "";
         } else {
            name = (tempTable.getValueAt(row, 0));
            value = tempTable.getValueAt(row, 1);
            tags = tempTable.getValueAt(row, 2);
         }

         modifyAttribute(name, value, tags);

         // Entry pointed in the tree
      } else if (source==menuItemInstrospectValue){
         String name = null;
         Object value = null;
         if (row == -1) {
            return;
         } else {
            name = (tempTable.getValueAt(row, 0)).toString();
            Object node = getNode();
             try {
                 if (node instanceof Prim) {
                    value = ((Prim)node).sfResolve(name,false);
                 } else {
                   value = ((ComponentDescription)node).sfResolve(name,false);
                 }
             } catch (SmartFrogResolutionException e1) {
                 if (sfLog().isErrorEnabled()) sfLog().error ("Failed to resolve value during instrospect '"+name,e1);
                 WindowUtilities.showError(this,"Failed to resolve value during instrospect '"+name+"'. \n"+e1.toString());
             } catch (RemoteException e1) {
                 if (sfLog().isErrorEnabled()) sfLog().error ("Failed to instrospect '"+name,e1);
                 WindowUtilities.showError(this,"Failed to instrospect '"+name+"'. \n"+e1.toString());
             }
         }
         modalDialog("Introspection "+ name ,  PopUpTree.introspect(value), "", source);
      }
   }


   /**
    * Modifies the attribute with the value.
    *
    *@param  name   Name of the object
    *@param  value  Value of the object
    */
   void modifyAttribute(Object name, Object value, Object tags) {
      Object[] attribute = new Object[3];
      attribute[0] = name;
      attribute[1] = value;
      attribute[2] = tags;
      // try to get the object value
      try {
        TreePath tpath = (tempTree).getSelectionPath();
        Object node = getNode();
        if (node instanceof Prim) {
            attribute[1] = ((Prim)node).sfResolve(name.toString());
            attribute[2] = ((Prim)node).sfGetTags(name.toString());
        } else if (node instanceof ComponentDescription) {
            attribute[1] = ((ComponentDescription)node).sfResolve(name.toString());
            attribute[2] = ((ComponentDescription)node).sfGetTags(name.toString());
        }
      } catch (Exception ex) {
          if (sfLog().isIgnoreEnabled()) sfLog().ignore ("Failed to read real value during modify attribute '"+name,ex);
          //WindowUtilities.showError(this,"Failed to modify '"+name+"'. \n"+ex.toString());
      }

      NewAttributeDialog attrDialog = new NewAttributeDialog(null, "Add/Modify attribute", true, attribute);
      WindowUtilities.setPositionDisplay( this.parent.treeScrollPane, attrDialog, "C");
      attrDialog.show();

      if (attribute != null) {
         if (attribute[0] == null) {
            if (sfLog().isTraceEnabled()) sfLog().trace ("No attribute was modified");
            WindowUtilities.showError(this,"No attribute was modified");
            return;
         }
         if (attribute[1] == null) {
             if (sfLog().isErrorEnabled()) sfLog().error (" Wrong format for: " + attribute[0].toString());
             WindowUtilities.showError(this," Wrong format for: " + attribute[0].toString());
            return;
         }

         try {
            TreePath tpath = (tempTree).getSelectionPath();
            Object node = getNode();
            modify(node,attribute[0],attribute[1],attribute[2]);
            //((Prim) (((DeployEntry) (tpath.getLastPathComponent())).getEntry())).sfReplaceAttribute(attribute[0],attribute[1]);
         } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error ("Failed to modify '"+name,ex);
            WindowUtilities.showError(this,"Failed to modify '"+name+"'. \n"+ex.toString());

         }

         parent.refreshTable();
      }
   }

    /**
     * Get Node
     * @return  Object
     */
   private Object getNode() {
       Object node;
       TreePath tpath = (tempTree).getSelectionPath();
       node = ((((DeployEntry) (tpath.getLastPathComponent())).getEntry()));
       return node;
   }

    public boolean isNodeACopy(){
        TreePath tpath = (tempTree).getSelectionPath();
        DeployEntry node = (((DeployEntry) (tpath.getLastPathComponent())));
        return node.isCopy();

    }

    public boolean isParentNodeACopy(){
        TreePath tpath = (tempTree).getSelectionPath();
        DeployEntry parentNode = (((DeployEntry) (tpath.getParentPath().getLastPathComponent())));
        return parentNode.isCopy();
    }

   /**
    * Converts tree path to path
    *
    *@param  tpath  Tree path object
    *@return        path
    */
   private String treePath2Path(TreePath tpath) {
      String path = "";
      path = tpath.toString();
      path = path.substring(1, path.length() - 1);
      path = path.replace(',', '.');
      path = removeSpaces(path);

      //System.out.println("TreePath: "+path);
      return path;
   }


   /**
    * Removes spaces from input string.
    *
    *@param  string  Input string
    *@return         string without spaces
    */
   public static String removeSpaces(String string) {
      //Save the search string as a StringBuffer object so
      //we can take advantage of the replace capabilities
      StringBuffer s = new StringBuffer();

      //loop through the original string
      int thisCharacter;

      for (int i = 0; i < string.length(); i++) {
         thisCharacter = (char) string.charAt(i);

         if ((char) string.charAt(i) != ' ') {
            s.append((char) thisCharacter);
         }
      }

      return s.toString();
   }

   /**
    * Removes the attribute from the SF component.
    *
    *@param  obj SF Component
    *@param  attribName  Attribute Name
    */
   void remove(Object obj, String attribName) {
      if ((obj instanceof Prim)||(obj instanceof ComponentDescription)) {
         try {
            org.smartfrog.services.management.DeployMgnt.removeAttribute(obj,(String) attribName);
            parent.refreshTable();
         } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error ("Failed to remove '"+attribName,ex);
            WindowUtilities.showError(this,"Failed to remove '"+attribName+"'. \n"+ex.toString());
         }
      }
   }


   /**
    * Modifies the attribute from the SF component.
    *
    *@param  obj SF Component
    *@param  attribName  Attribute Name
    *@param  value       Attribute value
    */
   void modify(Object obj, Object attribName, Object value, Object tags) {
      if ((obj instanceof Prim)||(obj instanceof ComponentDescription)) {
         try {
            org.smartfrog.services.management.DeployMgnt.modifyAttribute(obj, attribName, value, tags);
            parent.refreshTable();
         } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error ("Failed to modify '"+attribName,ex);
            WindowUtilities.showError(this,"Failed to modify '"+attribName+"'. \n"+ex.toString());
         }
      } else {
         WindowUtilities.showError(this,"Only Components or ComponentDescriptions can be modified");
      }
   }

    /**
     * Prepares option dialog box
     *
     *@param  title    title displayed on the dialog box
     *@param  message  message to be displayed
     *@param defaultValue default value
     */
    public void modalDialog(String title, String message,
            String defaultValue, Object source) {
        /**
         *  Scrollpane to hold the display's screen.
         */
        JScrollPane scrollPane = new JScrollPane();
        /**
         *  Display's screen object.
         */
        JTextArea screen = new JTextArea(message);
        Frame parentFrame = new Frame();
        JDialog pane = new JDialog(parentFrame,title,true);
        pane.setSize(600,400);
        pane.setResizable(true);
        pane.getContentPane().add(scrollPane);
        scrollPane.getViewport().add(screen, null);
        WindowUtilities.center(parent,parentFrame);
        pane.show(true);
    }

   /** Log for this class, created using class name*/
    LogSF sfLog = LogFactory.getLog("sfManagementConsole");

    /**
     * Log for this class
      * @return
     */
   private LogSF sfLog(){
        return sfLog;
   }
}
