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

import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;


/**
 * DEfines the Panel for trace component.
 * @deprecated 12 December 2001
 */
public class TraceTreePanel extends JPanel implements TreeSelectionListener {
    /** BorderLayout. */
    BorderLayout borderLayout1 = new BorderLayout();
    /** Splitpane. */
    JSplitPane jSplitPane1 = new JSplitPane();
    /** BorderLayout. */
    BorderLayout borderLayout2 = new BorderLayout();
    /** Tree scroll pane. */
    JScrollPane treeScrollPane = new JScrollPane();
    /** Table scroll pane. */
    JScrollPane tableScrollPane = new JScrollPane();
    /** Systemview tree. */
    JTree systemViewTree = new JTree();
    /** Table. */
    JTable table = new JTable();

    //Tree
    /** Tree model. */
    BrowseTreeModelSF treeModel;
    /** String for rootDN. */
    String rootDN = "ROOT[3800]>";

    /**
     * Constructor for the TraceTreePanel object.
     */
    public TraceTreePanel() {
        try {
            jbInit();
        } catch (Exception ex) {
            sfLog().error(ex);
        }
    }

    /**
     * Constructor for the TraceTreePanel object.
     *
     * @param rootDN Root
     */
    public TraceTreePanel(String rootDN) {
        this.rootDN = rootDN;

        try {
            jbInit();
        } catch (Exception ex) {
            sfLog().error(ex);
        }
    }

    /**
     * Init method.
     *
     * @exception Exception failure in initalizing
     */
    void jbInit() throws Exception {
        treeInit();
        this.setLayout(borderLayout2);
        jSplitPane1.setMinimumSize(new Dimension(189, 200));
        jSplitPane1.setPreferredSize(new Dimension(300, 300));
        jSplitPane1.setLastDividerLocation(150);

        //40% of the space goes to left/top
        jSplitPane1.setResizeWeight(0.4);
        jSplitPane1.setDividerLocation(200);
        jSplitPane1.add(treeScrollPane, JSplitPane.LEFT);
        this.add(jSplitPane1, BorderLayout.CENTER);
        jSplitPane1.add(tableScrollPane, JSplitPane.RIGHT);
        treeScrollPane.getViewport().add(systemViewTree, null);
        tableScrollPane.getViewport().add(table, null);
    }

    /**
     * Init method.
     */
    private void treeInit() {
        treeModel = new BrowseTreeModelSF(rootDN);
        systemViewTree = new JTree(treeModel);

        //Listen for when the selection changes.
        systemViewTree.addTreeSelectionListener(this);

        //systemViewJTree.setPreferredSize(new Dimension(200, 100));
    }

    /**
     * Adds to tree.
     *
     * @param msg message
     */
    public void add(String msg) {
        treeModel.add(msg);

        //systemViewJTree.updateUI();
    }

    /**
     * This method handles with tree events.
     *
     * @param e TreeSelectionEvent object
     */
    public void valueChanged(TreeSelectionEvent e) {
        Object treeNode = systemViewTree.getLastSelectedPathComponent();

        //System.out.println(treeNode.getClass());
        if (treeNode == null) {
            return;
        }

        if (treeNode instanceof BrowserEntry) {
            //System.out.println("TreeNode: "+treeNode);
            Object[][] data = ((BrowserEntry) treeNode).getAttributesArray();
            String[] title = { "Message", "Time" };

            //         if (data == null){
            //            String[][] emptyData = null; // {{""},{""}};
            //            data = emptyData;
            //         }
            table.setModel(new DefaultTableModel(data, title));
        }
    }

    /**
     * Refreshes the Panel.
     */
    public void refresh() {
        String[] title = { "Message", "Time" };
        Object[][] data = null; //{{""},{""}};
        this.table.setModel(new DefaultTableModel(data, title));
        this.systemViewTree.updateUI();
        this.table.repaint();
    }

    /** Log for this class, created using class name*/
    LogSF sfLog = LogFactory.getLog(this.getClass());

    /**
     * Log for this class
      * @return
     */
   private LogSF sfLog(){
        return sfLog;
   }

//    /**
//     * Main processing method for Trace Tree Panel.
//     *
//     * @param args command line arguments
//     */
//    public static void main(String[] args) {
//        JFrame mainFrame = new JFrame("Text TraceTreePanel", null);
//        TraceTreePanel test = new TraceTreePanel();
//        mainFrame.getContentPane().add(test);
//        mainFrame.setVisible(true);
//        System.out.println("Starting...a new adventure.");
//        System.out.println(
//            "Adding: ROOT:System -------------------------------------------");
//        test.add(
//            "ROOT:baz, STARTED,15:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        test.add(
//            "ROOT:System, DEPLOYED, 15:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        System.out.println(
//            "Adding: ROOT:System:foo:bar2 -----------------------------------");
//        test.add(
//            "ROOT:System:foo:bar2,DEPLOYED,15:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        System.out.println(
//            "Adding: ROOT:System:foo ----------------------------------------");
//        test.add(
//            "ROOT:System:foo, DEPLOYED, 15:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        System.out.println(
//            "Adding: ROOT:System:foo:bar ------------------------------------");
//        test.add(
//            "ROOT:System:foo:bar,DEPLOYED,15:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        System.out.println(
//            "Adding: ROOT:baz ------------------------------------");
//        test.add(
//            "ROOT:baz,STARTED,15:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        test.add(
//            "ROOT:System:foo:bar,STARTED,15:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        test.add(
//            "ROOT,DEPLOYED,00:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        test.add(
//            "ROOT,STARTED,00:51:37.187 22/06/01 guijarro-j-5/15.144.25.153");
//        System.out.println("...Finished");
//    }
}
