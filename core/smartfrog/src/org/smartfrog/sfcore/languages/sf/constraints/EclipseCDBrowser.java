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
package org.smartfrog.sfcore.languages.sf.constraints;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/**
 * Browser for displaying sfComfig hierarchy for the purpose of setting user
 * variables
 *
 * @author anfarr
 */
public class EclipseCDBrowser extends JFrame implements CDBrowser {
  private JScrollPane scpane;
  private JLabel label;
  private JLabel undo_label;
  private JTextField entry;
  private JButton set;
  private JButton undo;
  private JButton done;
  private EclipseSolver.EclipseStatus est;
  private EclipseSolver.EclipseCDAttr ecda;
  private DefaultMutableTreeNode visnode;

  private DefaultMutableTreeNode root;
  private DefaultTreeModel model;
  private JTree tree;
  private static final int DEFAULT_WIDTH = 600;
  private static final int DEFAULT_HEIGHT = 400;

  /**
   * Kill the browser...
   */
  public void kill() {
    setVisible(false);
  }

  /**
   * Sets an unspecified object as pertaining to the state of the solving engine
   *
   * @param est
   */
  public void setES(Object est) {
    this.est = (EclipseSolver.EclipseStatus) est;
  }

  /**
   * Add attribute to the hierarchy to be displayed
   *
   * @param d      -- my parent as unspecified object
   * @param av     -- my attribute name / value as unspecified object
   * @param showme -- am I in the immediate component description to be
   *               displayed?
   * @return tree node which will be used later on to add child attributes to
   */
  public Object attr(Object d, Object av, boolean showme) {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(av);

    if (showme && visnode == null) {
      visnode = node;
    }

    if (root == null) {
      root = node;
    } else {
      ((DefaultMutableTreeNode) d).add(node);
    }
    return node;
  }

  /**
   * Redraws browser...
   */
  public void redraw() {
    if (!isVisible()) {
      init();
    } else {

      if (est.isBack()) {
        est.setBack(false);
        String text = "Attribute setting undone. " + label.getText();
        label.setText(text);
        set.setEnabled(false);
      }
      int isUndo = est.getUndo();
      if (isUndo > 0) {
        undo_label.setText(est.getUndoLabel());
        this.undo.setText("Undo (" + isUndo + ")");
        this.undo.setEnabled(true);
      } else {
        undo_label.setText("");
        this.undo.setText("Undo");
        this.undo.setEnabled(false);
      }
      done.setEnabled(est.isDone());

      repaint();
    }
  }

  /**
   * Reset browser to initial look...
   */
  void reset_display() {
    label.setText("Click on an attribute with range to set its value");
    entry.setText("");
    set.setEnabled(false);
    entry.setEnabled(false);
  }

  /**
   * Initialise browser...
   */
  void init() {
    setTitle("sfConfig Browser");
    setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

    // the root of the class tree is Object
    model = new DefaultTreeModel(root);
    tree = new JTree(model);

    // set up selection mode
    tree.addTreeSelectionListener(new
            TreeSelectionListener() {
              public void valueChanged(TreeSelectionEvent event) {
                // the user selected a different node--update description
                TreePath path = tree.getSelectionPath();
                if (path == null) return;
                DefaultMutableTreeNode selectedNode
                        = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object uobj = selectedNode.getUserObject();
                if (uobj instanceof EclipseSolver.EclipseCDAttr) {
                  ecda = (EclipseSolver.EclipseCDAttr) uobj;
                  if (ecda.isSet()) {
                    reset_display();
                  } else {
                    set.setEnabled(false);
                    label.setText(ecda.toString());
                    entry.setEnabled(true);
                  }
                } else {
                  reset_display();
                }
              }
            });

    int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
    tree.getSelectionModel().setSelectionMode(mode);

    Box vbox = Box.createVerticalBox();
    add(vbox, BorderLayout.CENTER);
    scpane = new JScrollPane(tree);
    vbox.add(scpane);

    label = new JLabel();
    Box labelbox = Box.createHorizontalBox();
    labelbox.add(Box.createHorizontalGlue());
    labelbox.add(label);
    labelbox.add(Box.createHorizontalGlue());
    vbox.add(labelbox);

    vbox.add(Box.createVerticalStrut(10));
    entry = new JTextField();
    vbox.add(Box.createVerticalGlue());
    vbox.add(entry);
    vbox.add(Box.createVerticalStrut(10));

    //set up selection mode
    entry.addKeyListener(new KeyAdapter() {
      public void keyTyped(KeyEvent event) {
        set.setEnabled(true);
        repaint();
      }
    });

    JPanel hbox = new JPanel();
    hbox.setLayout(new GridLayout(2, 3));
    vbox.add(hbox);

    set = new JButton("Set");
    Box setbox = Box.createHorizontalBox();
    setbox.add(set);
    setbox.add(Box.createHorizontalGlue());
    hbox.add(setbox);

    undo = new JButton("Undo");
    undo.setEnabled(false);
    Box undobox = Box.createHorizontalBox();
    undobox.add(Box.createHorizontalGlue());
    undobox.add(undo);
    undobox.add(Box.createHorizontalGlue());
    hbox.add(undobox);

    done = new JButton("Done");
    done.setEnabled(false);
    Box donebox = Box.createHorizontalBox();
    donebox.add(Box.createHorizontalGlue());
    donebox.add(done);
    hbox.add(donebox);

    undo_label = new JLabel("");
    hbox.add(new JPanel());
    hbox.add(undo_label);

    reset_display();

    set.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        boolean succ = ecda.process_sel(entry.getText());
        if (succ) {
          label.setText("Attribute set successfully");
          set.setEnabled(false);
          entry.setEnabled(false);
        } else {
          label.setText("Value selected not in range:" + ecda.getRangeAsString()
                  + ". Please try again");
        }
        entry.setText("");
      }
    });

    undo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        est.undo();
      }
    });

    done.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        est.done();
      }
    });

    tree.makeVisible(new TreePath(model.getPathToRoot(visnode)));

    URL url = getClass().getResource("HP_ICON.PNG");
    if (url != null) {
      Image image = Toolkit.getDefaultToolkit().getImage(url);
      if (image != null) {
        setIconImage(image);
      }
    }


    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);

  }
}

