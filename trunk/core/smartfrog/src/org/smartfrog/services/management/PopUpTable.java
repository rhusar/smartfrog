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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

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

   /** Item for Tree popup menu - Add attribute. */
   JMenuItem menuItemAddAttribute = new JMenuItem();
   /** Item for Tree popup menu - modify attribute. */
   JMenuItem menuItemModifyAttribute = new JMenuItem();
   /** Item for Tree popup menu - remove attribute. */
   JMenuItem menuItemRemoveAttribute = new JMenuItem();
   /** Item for Tree popup menu - resolve attribute. */
   JMenuItem menuItemResolveAttribute = new JMenuItem();


   //   JMenuItem menuItemTerminate = new JMenuItem();
   //   JMenuItem menuItemDTerminate = new JMenuItem();
   //   JMenuItem menuItemDetach = new JMenuItem();

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
      menuItemResolveAttribute.setText("Resolve LAZY Ref.");

      popupTable.add(menuItemRemoveAttribute);
      popupTable.add(menuItemModifyAttribute);
      popupTable.add(menuItemResolveAttribute);

      menuItemRemoveAttribute.addActionListener(this);
      menuItemModifyAttribute.addActionListener(this);
      menuItemResolveAttribute.addActionListener(this);

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
    *@param  parent     Reference to parent component
    */
   public void show(JTree compTree, JTable compTable, int x, int y,
         DeployTreePanel parent) {
      tempTree = compTree;
      tempTable = compTable;
      tempX = x;
      tempY = y;
      this.parent = parent;
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

      //System.out.println("Tree PopUp(source): "+e.getSource()+",
      //Path: "+path);
      // Launch it
      if (source == menuItemResolveAttribute) {
        Object name = null;
        Object value = null;

        if (row == -1) {
           name = "";
        } else {
           name = (tempTable.getValueAt(row, 0));
           //value = tempTable.getValueAt(row, 1);
        }
        resolveAttrib(name);

      } else if (source == menuItemRemoveAttribute) {
         if (row == -1) {
            System.out.println("No selected Cell");

            return;
         }

         //System.out.println("Removing: "+
     //((String)(tempTable.getValueAt(row,0))) +"from"+path);
         remove((((DeployEntry) (tpath.getLastPathComponent())).getEntry()),
               (String) (tempTable.getValueAt(row, 0)));

         // Entry pointed in the tree
      } else if (source == menuItemModifyAttribute) {
         Object name = null;
         Object value = null;

         if (row == -1) {
            name = "";
         } else {
            name = (tempTable.getValueAt(row, 0));
            value = tempTable.getValueAt(row, 1);
         }

         modifyAttribute(name, value);

         // Entry pointed in the tree
      }
   }


   /**
    * Modifies the attribute with the value.
    *
    *@param  name   Name of the object
    *@param  value  Value of the object
    */
   void modifyAttribute(Object name, Object value) {
      Object[] attribute = new Object[2];
      attribute[0] = name;
      attribute[1] = value;

      NewAttributeDialog attrDialog = new NewAttributeDialog(null,
            "Add/Modify attribute", true, attribute);
      org.smartfrog.services.display.WindowUtilities.setPositionDisplay(
              this.parent.treeScrollPane, attrDialog, "C");
      attrDialog.show();

      if (attribute != null) {
         if (attribute[0] == null) {
            return;
         }

         if (attribute[1] == null) {
            System.out.println(" Wrong format for: " +
                  attribute[0].toString());

            return;
         }

         //System.out.println("REPLACING: Attribute: name->"+ attribute[0].
     //toString()+", value->"+attribute[1].toString()+", class"+
     //attribute[1].getClass().toString());
         try {
            TreePath tpath = (tempTree).getSelectionPath();
            ((Prim) (((DeployEntry) (tpath.getLastPathComponent())).
             getEntry())).sfReplaceAttribute(attribute[0],attribute[1]);
         } catch (Exception ex) {
            ex.printStackTrace();
         }

         parent.refreshTable();
      }
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
    *  Resolves an Attrib  even if it is LAZY.
    *  @param attribName  name of the attribute
    */
   void resolveAttrib(Object attribName) {
     Object value;
     StringBuffer solvedValue = new StringBuffer();
     try {
       //Special case to show special info about he reference
         Prim prim;
         TreePath tpath = (tempTree).getSelectionPath();
         prim = ( (Prim) ( ( (DeployEntry) (tpath.getLastPathComponent())).
                          getEntry()));
         value = prim.sfResolveHere(attribName);
         if (value instanceof Reference) {
           try {
             ( (Reference) value).setEager(true);
             Object objSolvedValue = prim.sfResolve( (Reference) value);
             solvedValue.append(objSolvedValue.toString());
           } catch (Exception ex){
            solvedValue.append(" Failed to relsove!: " + ex.toString());
           }
           StringBuffer text = new StringBuffer();
           text.append("* Attribute resolved (LAZY ref): "+attribName);
           text.append("\n * Value: ");
           text.append("\n"+value.toString());
           text.append("\n * Value resolved: \n" + solvedValue.toString());
           text.append("\n\n" + "+ Value class:" +
                       value.getClass().toString());
           text.append("\n" + "+ Solved Value class:" +
                       solvedValue.getClass().toString());

           parent.jTextArea1.setText(text.toString());
         }
         else {
           return;
         }
       }
       catch (Throwable rex) {
         String err = "sfManagementConsole.deployEntry.getAttributes: error reading " +
             attribName + " >" + rex.getMessage();
         parent.jTextArea1.setText(err);
         //ex.printStackTrace();
       }

   }


   /**
    * Removes the attribute from the SF component.
    *
    *@param  obj SF Component
    *@param  attribName  Attribute Name
    */
   void remove(Object obj, String attribName) {
      if (obj instanceof Prim) {
         try {
            org.smartfrog.services.management.DeployMgnt.
            removeAttribute((Prim) obj,(String) attribName);
            parent.refreshTable();
         } catch (Exception ex) {
            ex.printStackTrace();
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
   void modify(Object obj, String attribName, Object value) {
      if (obj instanceof Prim) {
         try {
            org.smartfrog.services.management.DeployMgnt.
            modifyAttribute((Prim) obj, attribName, value);
            parent.refreshTable();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
   }
}
