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
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.smartfrog.sfcore.prim.Prim;

/**
 * Popup Tree UI component.
 */
public class PopUpTree extends JComponent implements ActionListener {
    /** Popup Tree. */
    JPopupMenu popupTree = new JPopupMenu();
    /** Tree component. */
    Component tempComp = null;
    /** x coordinate. */
    int tempX = 0;
    /** y coordinate. */
    int tempY = 0;
    /** Parent panel. */
    DeployTreePanel parent = null;

    /** Item for Tree popup menu - add attribute. */
    JMenuItem menuItemAddAttribute = new JMenuItem();

    //   JMenuItem menuItemModifyAttribute = new JMenuItem();
    //   JMenuItem menuItemRemoveAttribute = new JMenuItem();
    /** Item for Tree popup menu - terminate. */
    JMenuItem menuItemTerminate = new JMenuItem();
    /** Item for Tree popup menu - Dterminate. */
    JMenuItem menuItemDTerminate = new JMenuItem();
    /** Item for Tree popup menu - detach. */
    JMenuItem menuItemDetach = new JMenuItem();

    /**
     *  Constructs PopUpTree object
     */
    public PopUpTree() {
        popupInit();
    }

    /**
     *  Initializes popup tree
     */
    void popupInit() {
        // Tree: options
        menuItemAddAttribute.setText("Add Attribute");

        //      menuItemRemoveAttribute.setText("Remove Attribute");
        //      menuItemModifyAttribute.setText("Modify Attribute");
        menuItemDetach.setText("Detach Component");
        menuItemTerminate.setText("Terminate Component");
        menuItemDTerminate.setText("Detach and Terminate Comp");

        // Tree: options
        //      popupTree.add(menuItemAddAttribute);
        //      popupTree.add(menuItemRemoveAttribute);
        //      popupTree.add(menuItemModifyAttribute);
        popupTree.add(menuItemTerminate);
        popupTree.add(menuItemDTerminate);
        popupTree.add(menuItemDetach);

        // Add action listeners for tree popup
        menuItemAddAttribute.addActionListener(this);

        //      menuItemRemoveAttribute.addActionListener(this);
        //      menuItemModifyAttribute.addActionListener(this);
        menuItemTerminate.addActionListener(this);
        menuItemDTerminate.addActionListener(this);
        menuItemDetach.addActionListener(this);
    }

    /**
     *  Gets the popupMenu attribute of the PopUpTree object
     *
     *@return    The popupMenu value
     */
    public JPopupMenu getPopupMenu() {
        return popupTree;
    }

    /**
     * Displays the popup tree.
     *
     *@param  comp    Component to be displayed
     *@param  x       x coordinate
     *@param  y       y coordinate
     *@param  parent  parent of this component
     */
    public void show(Component comp, int x, int y, DeployTreePanel parent) {
        tempComp = comp;
        tempX = x;
        tempY = y;
        popupTree.show(comp, x, y);
        this.parent = parent;
    }

    /**
     * Interface Method
     *
     *@param  e  Action event
     */
    public void actionPerformed(ActionEvent e) {
        String path;
        Object source = e.getSource();
        TreePath tpath = ((JTree) tempComp).getPathForLocation(tempX, tempY);

        //System.out.println(" Object:"+(((JTree)tempComp).
    //getLastSelectedPathComponent()).toString());
        //System.out.println(" Object2:"+(tpath.getLastPathComponent()).
    //toString());
        //System.out.println(" Object2.getEntry():"+(((DeployEntry)(
    //tpath.getLastPathComponent())).getEntry()).toString());
        path = treePath2Path(tpath);

        //System.out.println("Tree PopUp(source): "+e.getSource()+", Path: 
    //"+path);
        // Launch it
        if (source == menuItemAddAttribute) {
            addAttrib();
        } else if (source == menuItemTerminate) {
            terminate((((DeployEntry) (tpath.getLastPathComponent())).
                    getEntry()));

            // Entry pointed in the tree
        } else if (source == menuItemDTerminate) {
            dTerminate((((DeployEntry) (tpath.getLastPathComponent())).
                    getEntry()));

            // Entry pointed in the tree
        } else if (source == menuItemDetach) {
            detach((((DeployEntry) (tpath.getLastPathComponent())).getEntry()));

            // Entry pointed in the tree
        }
    }

    /**
     * Converts tree path to path
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
     *  Adds a feature to the Attrib attribute of the PopUpTree object
     */
    void addAttrib() {
        System.out.println("ADD ATTRIBUTE! To Complete!!!!!!!!!!1");
    }

    /**
     * Terminates the deploy management
     *
     *@param  obj  SF Component
     */
    void terminate(Object obj) {
        //System.out.println("Terminating: "+obj.toString());
        if (obj instanceof Prim) {
            org.smartfrog.services.management.DeployMgnt.terminate((Prim) obj);
        }
    }

    /**
     * Detaches and terminates the deploy management
     *
     *@param  obj  SF Component
     */
    void dTerminate(Object obj) {
        //System.out.println("Detatching and Terminating: "+obj.toString());
        if (obj instanceof Prim) {
            org.smartfrog.services.management.DeployMgnt.dTerminate((Prim) obj);
            parent.refresh();
        }
    }

    /**
     *  Detaches the component from deploy management
     *
     *@param  obj  SF component
     */
    void detach(Object obj) {
        //System.out.println("Detatching: "+obj.toString());
        if (obj instanceof Prim) {
            org.smartfrog.services.management.DeployMgnt.detach((Prim) obj);
            parent.refresh();

            // Refresh Console.
            // To do: automatic Refresh ;-)
        }
    }
}
