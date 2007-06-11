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

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.Frame;

import org.smartfrog.sfcore.prim.TerminationRecord;

import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Set;


import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.services.display.WindowUtilities;
import org.smartfrog.services.display.Display;
import org.smartfrog.services.display.SFDisplay;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.parser.SFParser;


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
    JMenuItem menuItemRemoveAttribute = new JMenuItem();
    /** Item for Tree popup menu - normal terminate. */
    JMenuItem menuItemTerminateNormal = new JMenuItem();
    /** Item for Tree popup menu - abnormal terminate . */
    JMenuItem menuItemTerminateAbnormal = new JMenuItem();
    /** Item for Tree popup menu - Dterminate. */
    JMenuItem menuItemDTerminate = new JMenuItem();
    /** Item for Tree popup menu - detach. */
    JMenuItem menuItemDetach = new JMenuItem();

    /** Item for Tree popup menu - detach. */
    JMenuItem menuItemDumpContext = new JMenuItem();

    /** Item for Tree popup menu - detach. */
    JMenuItem menuItemIntrospector = new JMenuItem();

    /** Item for Tree popup menu - sfParentageChanged. */
    JMenuItem menuItemParentageChanged = new JMenuItem();
    /** Item for Tree popup menu - add ScriptingPanel. */
    JMenuItem menuItemAddScriptingPanel = new JMenuItem();

    /** Item for Tree popup menu - edit Tags. */
    JMenuItem menuItemEditTags = new JMenuItem();

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

        menuItemRemoveAttribute.setText("Remove Data");
        //      menuItemModifyAttribute.setText("Modify Attribute");
        menuItemDetach.setText("Detach Component");
        menuItemTerminateNormal.setText("Terminate Component - NORMAL");
        menuItemTerminateAbnormal.setText("Terminate Component - ABNORMAL");
        menuItemDTerminate.setText("Detach and Terminate Comp");
        menuItemDumpContext.setText("Diagnostics Report");
        menuItemParentageChanged.setText("sfParentageChanged()");
        menuItemAddScriptingPanel.setText("Add Scripting Panel");
        menuItemIntrospector.setText("Instrospector");
        menuItemEditTags.setText("Edit Tags");

        // Tree: options
        //      popupTree.add(menuItemAddAttribute);
        popupTree.add(menuItemRemoveAttribute);
        //      popupTree.add(menuItemModifyAttribute);
        popupTree.add(menuItemTerminateNormal);
        popupTree.add(menuItemTerminateAbnormal);
        popupTree.add(menuItemDTerminate);
        popupTree.add(menuItemDetach);

        popupTree.add(menuItemDumpContext);

        popupTree.add(menuItemParentageChanged);
        popupTree.add(menuItemAddScriptingPanel);
        popupTree.add(menuItemIntrospector);
        popupTree.add(menuItemEditTags);

        // Add action listeners for tree popup
        menuItemAddAttribute.addActionListener(this);

        menuItemRemoveAttribute.addActionListener(this);
        //      menuItemModifyAttribute.addActionListener(this);
        menuItemTerminateNormal.addActionListener(this);
        menuItemTerminateAbnormal.addActionListener(this);
        menuItemDTerminate.addActionListener(this);
        menuItemDetach.addActionListener(this);

        menuItemDumpContext.addActionListener(this);

        menuItemParentageChanged.addActionListener(this);
        menuItemAddScriptingPanel.addActionListener(this);
        menuItemIntrospector.addActionListener(this);
        menuItemEditTags.addActionListener(this);
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
        if ( getNode() instanceof Prim){
          menuItemRemoveAttribute.setVisible(false);
            menuItemDetach.setVisible(true);
            menuItemTerminateNormal.setVisible(true);
            menuItemTerminateAbnormal.setVisible(true);
            menuItemDTerminate.setVisible(true);
            menuItemDumpContext.setVisible(true);
            menuItemParentageChanged.setVisible(true);
            menuItemAddScriptingPanel.setVisible(true);
            menuItemIntrospector.setVisible(true);
            menuItemEditTags.setVisible(true);
        }else if  (getNode()instanceof ComponentDescription){
            menuItemRemoveAttribute.setVisible(true);
            menuItemDetach.setVisible(false);
            menuItemTerminateNormal.setVisible(false);
            menuItemTerminateAbnormal.setVisible(false);
            menuItemDTerminate.setVisible(false);
            menuItemDumpContext.setVisible(true);
            menuItemParentageChanged.setVisible(false);
            menuItemAddScriptingPanel.setVisible(true);
            menuItemIntrospector.setVisible(true);
            menuItemEditTags.setVisible(true);
        }
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

        Object node = getNode();

        path = treePath2Path(tpath);

        //System.out.println(" path "+path+", parentcopy: "+ isParentNodeACopy() +", node copy"+ isNodeACopy()+", Action: "+ e);

        //System.out.println("Tree PopUp(source): "+e.getSource()+", Path:
        //"+path);
        // Launch it
        if (source == menuItemRemoveAttribute) {
          remove();
        } else
        if (source == menuItemAddAttribute) {
           addAttrib();
       } else if (source == menuItemTerminateNormal) {
           terminate(node, TerminationRecord.NORMAL , "Console Management Action");
       } else if (source == menuItemTerminateAbnormal) {
           terminate(node, TerminationRecord.ABNORMAL, "Console Management Action");
            // Entry selected in the tree
        } else if (source == menuItemDTerminate) {
            dTerminate(node, TerminationRecord.NORMAL , "Console Management Action");
            // Entry selected in the tree
        } else if (source == menuItemDetach) {
            detach(node);
            // Entry selected in the tree
        } else if (source == menuItemEditTags) {
            editTags(node);
            // Entry selected in the tree
        } else if (source == menuItemParentageChanged) {
            if (node instanceof Prim){
                try {
                    ((Prim)node).sfParentageChanged();
                } catch (RemoteException ex1) {
                    if (sfLog().isErrorEnabled()) sfLog().error (ex1);
                }
            } else if (node instanceof ComponentDescription){
                ((ComponentDescriptionImpl)node).sfParentageChanged();
            }
            // Entry selected in the tree
        } else if (source == menuItemDumpContext) {
            StringBuffer message=new StringBuffer();
            String name = "error";
            if (node instanceof Prim) {
                try {
                    Prim objPrim = ((Prim)node);
                    message.append(objPrim.sfDiagnosticsReport());
                    name = ((Prim)objPrim).sfCompleteName().toString();
                } catch (Exception ex) {
                    message.append("\n Error: "+ex.toString());
                }
            } else {
                try {
                    ComponentDescription objCD = ((ComponentDescription)node);
                    message.append(((ComponentDescriptionImpl)objCD).sfDiagnosticsReport());
                    name = ((ComponentDescription)objCD).sfCompleteName().toString();
                } catch (Exception ex) {
                    message.append("\n Error: "+ex.toString());
                }

            }
            modalDialog("Context info for "+ name ,  message.toString(), "", source);
        } else if (source == menuItemIntrospector) {

            StringBuffer message=new StringBuffer();
            String name = "error";
            if (node instanceof Prim) {
                try {
                    Prim objPrim = ((Prim)node);
                    name = ((Prim)objPrim).sfCompleteName().toString();
                } catch (Exception ex) {
                    message.append("\n Error: "+ex.toString());
                }
            } else {
                try {
                    ComponentDescription objCD = ((ComponentDescription)node);
                    name = ((ComponentDescription)objCD).sfCompleteName().toString();
                } catch (Exception ex) {
                    message.append("\n Error: "+ex.toString());
                }

            }
            modalDialog("Introspection "+ name ,  introspect(node), "", source);
        }  else if (source == menuItemAddScriptingPanel) {
            StringBuffer message=new StringBuffer();
            String name = "error";
            String hostname = "localhost";
            int port = 3800;
            if (node instanceof Prim) {
                try {
                    Prim objPrim = ((Prim)node);
                    name = ((Prim)objPrim).sfCompleteName().toString();
                    name = name.substring(name.lastIndexOf("."));
                    hostname = objPrim.sfResolve("sfHost",hostname,false);
                    ProcessCompound pc = SFProcess.getProcessCompound();
                    if (pc!=null) {
                     port = pc.sfResolve("sfRootLocatorPort",port,false);
                    }
                } catch (Exception ex) {
                    message.append("\n Error: "+ex.toString());
                }
            } else {
                try {
                    ComponentDescription objCD = ((ComponentDescription)node);
                    name = ((ComponentDescription)objCD).sfCompleteName().toString();
                    name = name.substring(name.lastIndexOf("."));
                } catch (Exception ex) {
                    message.append("\n Error: "+ex.toString());
                }

            }

            try {
                Object obj = (parent.getParent());
                SFDeployDisplay.addScriptingPanel(((JTabbedPane)(obj)) ,name ,node, hostname ,port );
            } catch (Exception e1) {
                if (sfLog().isErrorEnabled()) sfLog().error (e1);
                WindowUtilities.showError(this,e1.toString());
            }

        }

    }

    public static String introspect(Object node) {
        StringBuffer message = new StringBuffer();
        message.append("\n***** Class****\n");
        message.append(node.getClass());
        message.append("\n***** Constructors****\n");
        message.append(print(node.getClass().getConstructors()));
        message.append("\n***** Fields****\n");
        message.append(print(node.getClass().getDeclaredFields()));
        message.append("\n***** Methods****\n");
        message.append(print(node.getClass().getMethods()));
        message.append("\n***** Interfaces ****\n");
        message.append(print(node.getClass().getInterfaces()));
        return message.toString();
    }

    private static String print(Object[] objs) {
        StringBuffer strb = new StringBuffer();
        for(int i=0;i<objs.length;i++)  {
            strb.append(objs[i].toString());
            strb.append("\n");
        }
        return strb.toString();
    }
    /**
     * Get Node
     * @return  Object
     */
    public Object getNode() {
        TreePath tpath = ((JTree) tempComp).getPathForLocation(tempX, tempY);
        Object node = (((DeployEntry) (tpath.getLastPathComponent())).getEntry());
        return node;
    }
    /**
     * Get RDN Node
     * @return  Object
     */
    public String getRDNNode() {
        TreePath tpath = ((JTree) tempComp).getPathForLocation(tempX, tempY);
        String nodeRDN = ((DeployEntry) (tpath.getLastPathComponent())).getRDN();
        return nodeRDN;
    }

    public boolean isNodeACopy(){
        TreePath tpath = ((JTree) tempComp).getPathForLocation(tempX, tempY);
        DeployEntry node = (((DeployEntry) (tpath.getLastPathComponent())));
        return node.isCopy();

    }

    public boolean isParentNodeACopy(){
        TreePath tpath = ((JTree) tempComp).getPathForLocation(tempX, tempY);
        DeployEntry parentNode = (((DeployEntry) (tpath.getParentPath().getLastPathComponent())));
        return parentNode.isCopy();
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
        if (sfLog().isWarnEnabled()) sfLog().warn("ADD ATTRIBUTE! @Todo Complete!!!!!!!!!!1");
    }

    void remove() {
      Object obj = getNode();
      //we need to check is the parent is a copy
      if (isParentNodeACopy()  || getRDNNode().endsWith("*copy*") ) {
          String msg = "The node selected is a copy and no action can be applied\n Use a console running in the local process of this node";
          if (sfLog().isErrorEnabled()) sfLog().error (msg);
          WindowUtilities.showError(this,msg);
          return;
      }
      //System.out.println("remove() "+obj);
      if (obj instanceof ComponentDescription) {
        ComponentDescription cd = (ComponentDescription)obj;
        ComponentDescription CDparent = cd.sfParent();
        Prim primParent = cd.sfPrimParent();
        try {
          if (CDparent != null) {
            CDparent.sfRemoveAttribute(CDparent.sfAttributeKeyFor(cd));

          } else if (primParent != null) {
            primParent.sfRemoveAttribute(primParent.sfAttributeKeyFor(cd));
          }
          parent.refresh();
        }
        catch (Exception ex) {
          String msg = "Problem when trying to remove '";
          if (sfLog().isErrorEnabled()) sfLog().error (msg + cd);
          WindowUtilities.showError(this,msg +cd+"'. \n"+ex.toString());
        }
      }
    }
    /**
     * Terminates the deploy management
     *
     *@param  obj  SF Component
     * @param type error type
     * @param reason cause
     */
    void terminate(Object obj, String type, String reason) {
        //System.out.println("Terminating: "+obj.toString());
        if (obj instanceof Prim) {
            String name ="";
            try {
                name = ((Prim)obj).sfCompleteName().toString();
                org.smartfrog.services.management.DeployMgnt.terminate((Prim) obj, type, reason);
            } catch (Exception ex){
               String msg = "Problem when trying to Terminate '" +name;
               if (sfLog().isErrorEnabled()) sfLog().error (msg ,ex);
               WindowUtilities.showError(this,msg +"'. \n"+ex.toString());
            }
        }
    }

    /**
     * Detaches and terminates the deploy management
     *
     * @param  obj  SF Component
     * @param type error type
     * @param reason cause
     */
    void dTerminate(Object obj, String type, String reason) {
        //System.out.println("Detatching and Terminating: "+obj.toString());
        String name = "";
        if (obj instanceof Prim) {
            try {
                name = ((Prim)obj).sfCompleteName().toString();
                org.smartfrog.services.management.DeployMgnt.dTerminate((Prim) obj, type, reason);
                parent.refresh();
            } catch (Exception ex){
                String msg = "Problem when trying to Detach and Terminate '"+name;
                if (sfLog().isErrorEnabled()) sfLog().error (msg,ex);
               WindowUtilities.showError(this, msg + "'. \n"+ex.toString());
            }
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
            String name ="";
            try {
                name = ((Prim)obj).sfCompleteName().toString();
                org.smartfrog.services.management.DeployMgnt.detach((Prim) obj);
                parent.refresh();
            } catch (Exception ex){
              String msg = "Problem when trying to Detach and Terminate "+name;
              if (sfLog().isErrorEnabled()) sfLog().error (msg);
              WindowUtilities.showError(this, msg +"'. \n"+ex.toString());
            }
            // Refresh Console.
            // To do: automatic Refresh ;-)
        }
    }

    void editTags (Object obj) {
         //System.out.println("Detatching: "+obj.toString());
        if (obj instanceof Prim) {
            String name ="";
            try {
                name = ((Prim)obj).sfCompleteName().toString();
                Object tags = ((Prim)obj).sfGetTags();
                tags = JOptionPane.showInputDialog(this,"Edit Tags",tags);
                if (tags!=null) {
                   Set newTags = (Set)parseTags(tags.toString(),"sf");
                   ((Prim)obj).sfSetTags(newTags);
                }
            } catch (Exception ex){
              String msg = "Problem when trying to edit tags on Component "+name;
              if (sfLog().isErrorEnabled()) sfLog().error (msg,ex);
              WindowUtilities.showError(this, msg +"'. \n"+ex.toString());
            }
        } else if (obj instanceof ComponentDescription){
             String name ="";
            try {
                name = ((ComponentDescription)obj).sfCompleteName().toString();
                Object tags = ((ComponentDescription)obj).sfGetTags();
                tags = JOptionPane.showInputDialog(this,"Edit Tags",tags);
                if (tags!=null) {
                    Set newTags = (Set)parseTags(tags.toString(),"sf");
                    ((ComponentDescription)obj).sfSetTags(newTags);
                }
            } catch (Exception ex){
              String msg = "Problem when trying to edit tags on ComponentDescription "+name;
              if (sfLog().isErrorEnabled()) sfLog().error (msg,ex);
              WindowUtilities.showError(this, msg +"'. \n"+ex.toString());
            }
        } else {
           if (sfLog().isErrorEnabled()) sfLog().error ( "Error when editing tags on object: "+obj.toString()+"\n "+obj.getClass().getName());
           WindowUtilities.showError(this, "Error when editing tags on object: "+obj.toString()+"\n "+obj.getClass().getName());
        }
    }



    /**
     * Parse
     * @param textToParse  text to be parsed
     * @param language language
     * @return Object
     */
    public Object parseTags(String textToParse, String language) {
        try {
            SFParser parser = new SFParser(language);
            return parser.sfParseTags( textToParse);
        } catch (Throwable ex) {
            if (sfLog().isErrorEnabled()) sfLog().error (ex);
        }
        return null;
    }

    /**
     * Prepares option dialog box
     *
     *@param  title    title displayed on the dialog box
     *@param  message  message to be displayed
     *@param defaultValue default value
     */
    public static void modalDialog(String title, String message,
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
