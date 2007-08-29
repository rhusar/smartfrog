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

import java.awt.event.MouseEvent;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import java.rmi.RemoteException;
import javax.swing.tree.TreePath;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.Set;

import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.services.display.FontSize;
import org.smartfrog.services.display.WindowUtilities;

/**
 * Tree panel for SmartFrog hierarchy of components.
 *
 */
public class DeployTreePanel extends JPanel implements TreeSelectionListener, FontSize {

    /** Log for this class, created using class name*/
    LogSF sfLog = LogFactory.getLog("sfManagementConsole");

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
    private boolean showCDasChild = true;

    /**
     * Constructs the DeployTreePanel object
     */
    public DeployTreePanel() {
        try {
            treeInit(null,false , false,true);
            jbInit();
        } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error (ex);
        }
    }

    /**
     * Constructs the DeployTreePanel object with root.
     *
     * @param  root  Root of the tree.
     * @param  isCopy is root a copy?
     * @param inRootPanel flag indicating to show in root panel
     * @param showCDasChild flag indicating to show CD as child
     */
    public DeployTreePanel(Object root, boolean isCopy, boolean inRootPanel,boolean showCDasChild) {
        try {
            this.inRootPanel = inRootPanel;
            treeInit(root, isCopy , inRootPanel,showCDasChild);
            jbInit();
            popupinit();
        } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error (ex);
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
        tableScrollPane.getViewport().addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseReleased(MouseEvent e) {
                    table_mouseClicked(e);
                }
                public void mousePressed(MouseEvent e) {
                    table_mouseClicked(e);
                }
            });

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
     * @param  root  Root of the tree
     * @param isCopy is root a copy?.
     * @param inRootPanel flag indicating to show in root panel
     * @param showCDasChild flag indicating to show CD as child
     */
    private void treeInit(Object root, boolean isCopy, boolean inRootPanel, boolean showCDasChild) {
        if (root != null) {
            treeModel = new DeployTreeModelSF(root, isCopy, inRootPanel,showCDasChild);
        } else {
            treeModel = new DeployTreeModelSF();
        }
        systemViewTree = new JTree(treeModel);
        //specialized cell renderer
        systemViewTree.setCellRenderer(new DeployEntryCellRenderer());
        //Listen for when the selection changes.
        systemViewTree.addTreeSelectionListener(this);
        //systemViewJTree.setPreferredSize(new Dimension(200, 100));
    }

    public void showCDasChild(boolean showCDasChild){
       treeModel.showCDasChild(showCDasChild);
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
            String[] title = { "Attribute", "Value","Tag(s)" };
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
     * @param isCopy is root a copy?
     */
    public void setModel(Object root, boolean isCopy) {
        treeModel = new DeployTreeModelSF(root, isCopy, inRootPanel,showCDasChild);
        this.systemViewTree.setModel(treeModel);
    }

    /**
     *  Refreshes the model.
     */
    public void refresh() {
        this.jTextArea1.setText("");
        this.completeName.setText(" ");
        String[] title = { "Attribute", "Value", "Tag(s)" };
        Object[][] data = {
            { " ", " ","" }
        };
        this.table.setModel(new DefaultTableModel(data, title));
        updateTable();
        this.table.repaint();
        refreshSelectedNode();
        //org.smartfrog.services.utils.gui.TableUtilities.setColumnWidths(this.table,new java.awt.Insets(4,4,4,4),true,false);

    }

    public void refreshSelectedNode(){
        try {
            Vector selectedRows = new Vector();
            boolean[] openClosed = new boolean[(systemViewTree.getRowCount())];

            for (int i = 0; i < systemViewTree.getRowCount(); ++i) {
                if (systemViewTree.isExpanded(i)) {
                    openClosed[i] = true;
                } else {
                    openClosed[i] = false;
                }
                if (systemViewTree.isRowSelected(i)) {
                    selectedRows.add(new Integer(i));
                }
            }

            systemViewTree.updateUI();

            int rowIndex = 0;
            while (rowIndex < systemViewTree.getRowCount()) {
                if (openClosed[rowIndex] == true) {
                    systemViewTree.expandRow(rowIndex);
                }
                ++rowIndex;
            }

            //Tree expansion
            int[] rows = new int[selectedRows.size()];
            int index = 0;
            for (int i = 0; i < selectedRows.size();) {
                //System.out.println("Selected: i " + i + " - " + ((Integer) selectedRows.elementAt(i)).intValue());
                rows[index++] = ((Integer) selectedRows.elementAt(i)).intValue();
            }
            systemViewTree.setSelectionRows(rows);
            int[] aux = {2};
            systemViewTree.setSelectionRows(aux);
        } catch (Exception e) {
            if (sfLog().isIgnoreEnabled()) sfLog().ignore(e);
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            //Ignore. It happens when a node is removed.
        }
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
        LogFactory.getLog(DeployTreePanel.class).out("Starting...a new adventure.");
        LogFactory.getLog(DeployTreePanel.class).out("...Finished");
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
            Object attrib = this.table.getValueAt(this.table.getSelectedRow(), 0);
            resolveAttrib(attrib);
        } catch (Exception ex) {
            if (sfLog().isIgnoreEnabled()) sfLog().ignore(ex);
        }

        if (e.isPopupTrigger()) {
            // Make the jPopupMenu visible relative to the current mouse position in the container.
            this.tablePopUp.show(this.systemViewTree, this.table, e.getX(),
                e.getY(), this);
        }
    }

    /**
     *  Resolves an Attrib  even if it is LAZY.
     *  @param attribName  name of the attribute
     */
    void resolveAttrib(Object attribName) {
      Object value;
      String tags="";
      StringBuffer solvedValue = new StringBuffer();
      try {
          Object node = getNode();
          value = sfResolveHere(attribName, node);
          tags = sfGetTags(attribName, node).toString();
          String solvedValueClass = "class not found";
          String stackTrace = null;
          try {
              Object objSolvedValue = sfResolve(attribName.toString(), node);
              String tempString = objSolvedValue.toString();
              try {
                  if (objSolvedValue instanceof ComponentDescription) {
                    tempString = prettyPrint(objSolvedValue);
                  } else {
                    tempString = ContextImpl.getBasicValueFor(objSolvedValue);
                  }

              } catch (Exception ex1){}
              solvedValue.append(prettyPrint(tempString));
              solvedValueClass = objSolvedValue.getClass().toString();
          } catch (Exception ex) {
              solvedValue.append(" Failed to resolve ("+attribName+"): "+ ex.toString());
              try {
                  StringWriter sw = new StringWriter();
                  PrintWriter pw = new PrintWriter(sw);
                  ex.printStackTrace(pw);
                  stackTrace = ("\r\n"+sw.toString()+"\r\n");
              } catch (Exception e2) {
                  if (sfLog().isErrorEnabled()) sfLog().error (e2);
              }
          }
          String tempString = "";
          StringBuffer text = new StringBuffer();
          text.append("* Attribute: "+attribName);
          text.append("\n * Tags: "+tags);
          text.append("\n * Value: ");
          tempString = value.toString();
          try {
            try {
              if (value instanceof ComponentDescription) {
                tempString = prettyPrint(value);
              } else {
                tempString = ContextImpl.getBasicValueFor(value);
              }
            } catch (Exception ex1){}
          } catch (Exception ex1) {
              tempString= tempString + "\n["+"Error when parsing value, defaulting to String\n"+ex1.toString()+"]";
          }//ignore exception }
          text.append("\n"+tempString);
          text.append("\n * Value resolved: \n"+ prettyPrint(solvedValue));
          text.append("\n\n"+"+ Value class:"+value.getClass().toString());
          text.append("\n"+"+ Solved Value class:"+solvedValueClass);
          if (stackTrace !=null) text.append("\n\n"+"+ StackTrace:"+stackTrace);
          jTextArea1.setText(text.toString());
      } catch (Throwable rex) {
          String err =
              "sfManagementConsole.deployEntry.getAttributes: error reading "+
              attribName+" >"+rex.getMessage();
          jTextArea1.setText(err);
          if (sfLog().isErrorEnabled()) sfLog().error (err,rex);
      }
   }

   private String prettyPrint (Object obj){
       StringBuffer sb = new StringBuffer();
       if (obj instanceof String[]){
           Object[] objects = (Object[]) obj;
           int length = objects.length;
           for (int i=0; i<length; i++ ){
              sb.append(objects[i]);
              sb.append("\n");
           }
       } else if (obj instanceof ComponentDescription) {
           StringWriter sw = new StringWriter();
            try {
                ((ComponentDescription)obj).writeOn(sw,0);
            } catch (IOException ioex) {
                // ignore should not happen
                if (sfLog().isIgnoreEnabled()) sfLog().ignore (ioex);
            }
           return sw.toString();
       } else {

           return obj.toString();
       }

       return sb.toString();
   }

    /**
     * Get Node
     * @return Object
     */
   private Object getNode() {
       Object node;
       TreePath tpath = (this.systemViewTree).getSelectionPath();
       node = ((((DeployEntry) (tpath.getLastPathComponent())).getEntry()));
       return node;
   }

    /**
     * Resolve an attribute
     * @param attribName attribute name
     * @param node Node
     * @return Object
     * @throws SmartFrogResolutionException error in resolving
     * @throws RemoteException in case of remote/network error
     */
   private Object sfResolveHere(Object attribName, Object node) throws  SmartFrogResolutionException, RemoteException {
    Object value=null;
    if (node instanceof Prim){
        value = ((Prim)node).sfResolveHere(attribName);
    } else if (node instanceof ComponentDescription){
        value = ((ComponentDescription)node).sfResolveHere(attribName);
    }
    return value;
   }

    /**
     * Resolve an attribute
     * @param attribName attribute name
     * @param node Node
     * @return Object
     * @throws SmartFrogResolutionException error in resolving
     * @throws RemoteException in case of remote/network error
     */
   private Object sfResolve(Object attribName, Object node) throws  SmartFrogResolutionException, RemoteException {
    Object value=null;
    if (node instanceof Prim){
        value = ((Prim)node).sfResolve(attribName.toString());
    } else if (node instanceof ComponentDescription){
        value = ((ComponentDescription)node).sfResolve(attribName.toString());
    }
    return value;
   }

    /**
     * Get tags for an attribute
     * @param attribName attribute name
     * @param node Node
     * @return Set Tags
     * @throws SmartFrogContextException error in resolving
     * @throws RemoteException in case of remote/network error
     */
   private Set sfGetTags(Object attribName, Object node) throws SmartFrogRuntimeException, RemoteException {
    Set tags=null;
    if (node instanceof Prim){
        tags = ((Prim)node).sfGetTags(attribName);
    } else if (node instanceof ComponentDescription){
        tags = ((ComponentDescription)node).sfGetTags(attribName);
    }
    return tags;
   }

    /**
     * Log for this class
      * @return
     */
   private LogSF sfLog(){
        return sfLog;
   }

    public void setFontSize(int fontSize) {
        //JTree
        jTextArea1.setFont(new java.awt.Font("DialogInput", 0, fontSize));
        //Table
        table.setFont(new java.awt.Font("DialogInput", 0, fontSize));
        systemViewTree.setFont(new java.awt.Font("DialogInput", 0, fontSize));
    }

    public void increaseFontSize() {
        table.setFont(new java.awt.Font("DialogInput", 0, table.getFont().getSize()+1));
        jTextArea1.setFont(new java.awt.Font("DialogInput", 0, table.getFont().getSize()+1));
        systemViewTree.setFont(new java.awt.Font("DialogInput", 0, table.getFont().getSize()+1));
    }

     public void reduceFontSize() {
        if (table.getFont().getSize()>1)
          table.setFont(new java.awt.Font("DialogInput", 0, table.getFont().getSize()-1));
        if (jTextArea1.getFont().getSize()>1)
          jTextArea1.setFont(new java.awt.Font("DialogInput", 0, jTextArea1.getFont().getSize()-1));
        if (systemViewTree.getFont().getSize()>1)
          systemViewTree.setFont(new java.awt.Font("DialogInput", 0, systemViewTree.getFont().getSize()-1));
    }
}
