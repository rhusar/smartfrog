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

import java.awt.event.MouseEvent;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;

/**
 * Tree panel for SmartFrog hierarchy of components.
 *
 */
public class DeployTreePanel extends JPanel implements TreeSelectionListener {
    BorderLayout borderLayout1 = new BorderLayout();
    JSplitPane jSplitPane1 = new JSplitPane();
    BorderLayout borderLayout2 = new BorderLayout();
    JScrollPane treeScrollPane = new JScrollPane();
    JTree systemViewTree = new JTree();

    //Tree Model for SF components!
    DeployTreeModelSF treeModel;

    /**
     *  Tree control popup menu
     */
    PopUpTree treePopUp;

    /**
     *  Table control popup menu
     */
    PopUpTable tablePopUp;
    private JSplitPane jSplitPane2 = new JSplitPane();
    private JScrollPane tableScrollPane = new JScrollPane();
    private JTable table = new JTable();
    private JScrollPane jScrollPane1 = new JScrollPane();
    public JTextArea jTextArea1 = new JTextArea();
    private JPanel statusPanel = new JPanel();
    //private JLabel completeName = new JLabel();
    private JTextField completeName = new JTextField();
    private BorderLayout borderLayout3 = new BorderLayout();

    private boolean inRootPanel = false;

    /**
     * Constructs the DeployTreePanel object
     */
    public DeployTreePanel() {
        try {
            treeInit(null, false);
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Constructs the DeployTreePanel object with root.
     *
     *@param  root  Root of the tree.
     */
    public DeployTreePanel(Object root, boolean inRootPanel) {
        try {
            this.inRootPanel = inRootPanel;
            treeInit(root, inRootPanel);
            jbInit();
            popupinit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *  Initializes the popup
     */
    void popupinit() {
        // add popups menus for Tree and Table
        treePopUp = new PopUpTree();
        tablePopUp = new PopUpTable();
    }

    /**
     * Initializes the JPanel.
     *
     *@throws  Exception  If any error during initialization
     */
    void jbInit() throws Exception {
        this.setLayout(borderLayout2);
        jSplitPane1.setMinimumSize(new Dimension(189, 200));
        jSplitPane1.setPreferredSize(new Dimension(300, 300));
        jSplitPane1.setDividerSize(3);
        jSplitPane1.setLastDividerLocation(150);

        //40% of the space goes to left/top
        jSplitPane1.setResizeWeight(0.3);
        jSplitPane1.setDividerLocation(200);
        systemViewTree.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseReleased(MouseEvent e) {
                    systemViewTree_mouseClicked(e);
                }
                public void mousePressed(MouseEvent e) {
                    systemViewTree_mouseClicked(e);
                }
            });
        jSplitPane2.setDividerSize(2);
        jSplitPane2.setDividerLocation(220);
        statusPanel.setLayout(borderLayout3);
        completeName.setBackground(Color.lightGray);
        completeName.setFont(new java.awt.Font("SansSerif", 0, 12));
        completeName.setBorder(BorderFactory.createEtchedBorder());
        completeName.setToolTipText("Cannonical component name");
        completeName.setEditable(false);
        completeName.setText("  ");
        treeScrollPane.getViewport().add(systemViewTree, null);
        jSplitPane1.add(treeScrollPane, JSplitPane.LEFT);
        jSplitPane1.add(jSplitPane2, JSplitPane.RIGHT);
        jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
        jTextArea1.setText("");
        jScrollPane1.getViewport().add(jTextArea1, null);
        tableScrollPane.getViewport().add(table, null);
        jSplitPane2.add(tableScrollPane, JSplitPane.TOP);
        jSplitPane2.add(jScrollPane1, JSplitPane.BOTTOM);
        this.add(statusPanel,  BorderLayout.SOUTH);
        statusPanel.add(completeName, BorderLayout.SOUTH);
        this.add(jSplitPane1, BorderLayout.CENTER);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseReleased(MouseEvent e) {
                    table_mouseClicked(e);
                }
                public void mousePressed(MouseEvent e) {
                    table_mouseClicked(e);
                }
            });
    }

    /**
     * Initializes the tree.
     *
     *@param  root  Root of the tree.
     */
    private void treeInit(Object root, boolean inRootPanel) {
        if (root != null) {
            treeModel = new DeployTreeModelSF(root, inRootPanel);
        } else {
            treeModel = new DeployTreeModelSF();
        }

        systemViewTree = new JTree(treeModel);

        //Listen for when the selection changes.
        systemViewTree.addTreeSelectionListener(this);

        //systemViewJTree.setPreferredSize(new Dimension(200, 100));
    }

    /**
     * Adds message to the tree model.
     *
     *@param  msg  Message to add.
     */
    public void add(String msg) {
        treeModel.add(msg);

        //systemViewJTree.updateUI();
    }

    /**
     *  This method handles with tree events
     *
     *@param  e  Tree selection event
     */
    public void valueChanged(TreeSelectionEvent e) {
        updateTable();
    }


    /**
     *  Updates the table.
     */
    private void updateTable() {
        Object treeNode = systemViewTree.getLastSelectedPathComponent();

        //System.out.println(treeNode.getClass());
        if (treeNode == null) {
            return;
        }

        //System.out.println("Query to Node: "+treeNode.toString());
        if (treeNode instanceof DeployEntry) {
            //System.out.println("TreeNode: "+treeNode);
            Object[][] data = ((DeployEntry) treeNode).getAttributes();
            String[] title = { "Attribute", "Value" };
            table.setModel(new DefaultTableModel(data, title));
            this.completeName.setText (((DeployEntry) treeNode).getDN());

            //org.smartfrog.services.utils.gui.TableUtilities.setColumnWidths(table,new java.awt.Insets(4,4,4,4),true,false);
            //table.sizeColumnsToFit(table.AUTO_RESIZE_ALL_COLUMNS);
        }

        this.jTextArea1.setText("");
    }

    /**
     *  Sets the model attribute of the DeployTreePanel object
     *
     *@param  root  root of the tree
     */
    public void setModel(Object root) {
        treeModel = new DeployTreeModelSF(root, inRootPanel);
        this.systemViewTree.setModel(treeModel);
    }

    /**
     *  Refreshes the model.
     */
    public void refresh() {
        this.jTextArea1.setText("");
        this.completeName.setText(" ");
        String[] title = { "Attribute", "Value" };
        Object[][] data = {
            { " ", " " }
        };
        this.table.setModel(new DefaultTableModel(data, title));
        this.systemViewTree.updateUI();

        //org.smartfrog.services.utils.gui.TableUtilities.setColumnWidths(this.table,new java.awt.Insets(4,4,4,4),true,false);
        this.table.repaint();
    }

    /**
     *  Refreshes the table.
     */
    public void refreshTable() {
        updateTable();

        //table.updateUI();
    }

    /**
     *Entry point to DeployTreePanel.
     *
     *@param  args  Arguments
     */
    public static void main(String[] args) {
        JFrame mainFrame = new JFrame("Text DeployTreePanel", null);
        DeployTreePanel test = new DeployTreePanel();
        mainFrame.getContentPane().add(test);
        mainFrame.setVisible(true);
        System.out.println("Starting...a new adventure.");
        System.out.println("...Finished");
    }

    /**
     *Interface Method
     *
     *@param  e  Mouse event
     */
    void systemViewTree_mouseClicked(MouseEvent e) {
        if (e.isPopupTrigger()) {
            // Make the jPopupMenu visible relative to the current mouse
            // position in the container.
            this.treePopUp.show(this.systemViewTree, e.getX(), e.getY(), this);
        }
    }

    /**
     *Interface Method
     *
     *@param  e  Mouse event
     */
    void table_mouseClicked(MouseEvent e) {
        try {
            Object value = this.table.getValueAt(this.table.getSelectedRow(), 0);
            this.jTextArea1.setText("* Attribute: " + value.toString());
            value = this.table.getValueAt(this.table.getSelectedRow(), 1);
            this.jTextArea1.append("\n* Value: \n" + value.toString());
//            //Only gets class when the value is not a reference converted to string.
            if (!this.jTextArea1.getText().endsWith(".Reference")) {
                this.jTextArea1.append("\n\n" + "+ Value class:" +
                    value.getClass().toString());
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        if (e.isPopupTrigger()) {
            // Make the jPopupMenu visible relative to the current mouse position in the container.
            this.tablePopUp.show(this.systemViewTree, this.table, e.getX(),
                e.getY(), this);
        }
    }
}
