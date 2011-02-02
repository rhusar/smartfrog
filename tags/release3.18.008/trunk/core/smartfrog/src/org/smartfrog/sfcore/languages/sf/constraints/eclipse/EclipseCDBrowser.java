/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.sf.constraints.eclipse;


import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;


/**
 * Browser for displaying sfComfig hierarchy for the purpose of setting user variables
 *
 * @author anfarr
 */
public class EclipseCDBrowser extends JFrame implements CDBrowser {
    private JScrollPane scrollPane;
    private JLabel label;
    private JLabel undoLabel;
    private JTextField entry;
    private JButton setButton;
    private JButton undoButton;
    private JButton doneButton;
    private EclipseStatus eclipseStatus;
    private EclipseCDAttr eclipseCDAttr;
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
     * Sets EclipseStatus object pertaining to status information for browser
     *
     * @param est
     */
    public void setES(EclipseStatus est) {
        eclipseStatus = est;
    }

    /**
     * Add attribute to the hierarchy to be displayed
     *
     * @param d      -- my parent as unspecified object
     * @param av     -- my attribute name / value as unspecified object
     * @param showme -- am I in the immediate component description to be displayed?
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

            if (eclipseStatus.isBack()) {
                eclipseStatus.setBack(false);
                label.setText("Attribute setting undone. " + label.getText());
                setButton.setEnabled(false);
            }
            int undo = eclipseStatus.getUndo();
            if (undo > 0) {
                undoLabel.setText(eclipseStatus.getUndoLabel());
                undoButton.setText("Undo (" + undo + ")");
                undoButton.setEnabled(true);
            } else {
                undoLabel.setText("");
                undoButton.setText("Undo");
                undoButton.setEnabled(false);
            }
            doneButton.setEnabled(eclipseStatus.isDone());

            repaint();
        }
    }

    /**
     * Reset browser to initial look...
     */
    void reset_display() {
        label.setText("Click on an attribute with range to set its value");
        entry.setText("");
        setButton.setEnabled(false);
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
                        if (path == null) {
                            return;
                        }
                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                        Object uobj = selectedNode.getUserObject();
                        if (uobj instanceof EclipseCDAttr) {
                            eclipseCDAttr = (EclipseCDAttr) uobj;
                            if (eclipseCDAttr.isSet()) {
                                reset_display();
                            } else {
                                setButton.setEnabled(false);
                                label.setText(eclipseCDAttr.toString());
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
        scrollPane = new JScrollPane(tree);
        vbox.add(scrollPane);

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
                setButton.setEnabled(true);
                repaint();
            }
        });


        JPanel hbox = new JPanel();
        hbox.setLayout(new GridLayout(2, 3));
        vbox.add(hbox);

        setButton = new JButton("Set");
        Box setbox = Box.createHorizontalBox();
        setbox.add(setButton);
        setbox.add(Box.createHorizontalGlue());
        hbox.add(setbox);

        undoButton = new JButton("Undo");
        undoButton.setEnabled(false);
        Box undobox = Box.createHorizontalBox();
        undobox.add(Box.createHorizontalGlue());
        undobox.add(undoButton);
        undobox.add(Box.createHorizontalGlue());
        hbox.add(undobox);

        doneButton = new JButton("Done");
        doneButton.setEnabled(false);
        Box donebox = Box.createHorizontalBox();
        donebox.add(Box.createHorizontalGlue());
        donebox.add(doneButton);
        hbox.add(donebox);

        undoLabel = new JLabel("");
        hbox.add(new JPanel());
        hbox.add(undoLabel);

        System.out.println("init..4");
    	
        
        reset_display();

        setButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                boolean succ = eclipseCDAttr.process_sel(entry.getText());
                if (succ) {
                    label.setText("Attribute set successfully");
                    setButton.setEnabled(false);
                    entry.setEnabled(false);
                } else {
                    label.setText(
                            "Value selected not in range:" + eclipseCDAttr.getRangeAsString() + ". Please try again");
                }
                entry.setText("");
            }
        });
        
        undoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                eclipseStatus.undo();
            }
        });

        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                eclipseStatus.done();
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

        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setVisible(true);
    }


 }

