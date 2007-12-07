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
 * Browser for displaying sfComfig hierarchy for the purpose of setting user variables
 * @author anfarr
 *
 */
public class EclipseCDBrowser extends JFrame implements CDBrowser {
	private JScrollPane m_scpane;
	private JLabel m_label;
	private JLabel m_undo_label;
	private JTextField m_entry;
	private JButton m_set;
	private JButton m_undo;
	private JButton m_done;
	private EclipseSolver.EclipseStatus m_est;
	private EclipseSolver.EclipseCDAttr m_ecda;
	private DefaultMutableTreeNode m_visnode;
	
	/**
	 * Kill the browser...
	 */
	public void kill(){
		setVisible(false);
	}
	
	/**
	 * Sets an unspecified object as pertaining to the state of the solving engine
	 * @param es
	 */
	public void setES(Object est){
		m_est=(EclipseSolver.EclipseStatus) est;
	}
	
	/**
	 * Add attribute to the hierarchy to be displayed
	 * @param d -- my parent as unspecified object
	 * @param av  -- my attribute name / value as unspecified object
	 * @param showme -- am I in the immediate component description to be displayed? 
	 * @return
	 */
	public Object attr(Object d, Object av, boolean showme){
	   DefaultMutableTreeNode node = new DefaultMutableTreeNode(av);
	
	   if (showme && m_visnode==null) m_visnode=node;
	   
	   if (root==null){
		   root = node;;
	   } else {
		   ((DefaultMutableTreeNode) d).add(node);
	   }
	   return node;
	}
	
	/**
	 * Redraws browser...
	 */
	public void redraw(){
	   if (!isVisible()){
		  init();
	   } else {

		   if (m_est.isBack()){
			   m_est.setBack(false);
			   String label = "Attribute setting undone. "+m_label.getText();
    		   m_label.setText(label);
    		   m_set.setEnabled(false);
		   }
    	   int undo = m_est.getUndo();
	       if (undo>0){
    	      m_undo_label.setText(m_est.getUndoLabel());
	    	  m_undo.setText("Undo ("+undo+")");
    	      m_undo.setEnabled(true);
	       } else {
	    	  m_undo_label.setText(""); 
	    	  m_undo.setText("Undo");
	    	  m_undo.setEnabled(false);
	       }
	       m_done.setEnabled(m_est.isDone());
	       
	       repaint();
	   }   	
	}
		
	/**
	 * Reset browser to initial look...
	 *
	 */
	void reset_display(){
		m_label.setText("Click on an attribute with range to set its value");
		m_entry.setText("");
		m_set.setEnabled(false);
		m_entry.setEnabled(false);		
	}	
	
	/**
	 * Initialise browser...
	 */
	void init(){
	       setTitle("sfConfig Browser");
	       setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

	       // the root of the class tree is Object 
	       model = new DefaultTreeModel(root);
	       tree = new JTree(model);

	       // set up selection mode
	       tree.addTreeSelectionListener(new
	          TreeSelectionListener()
	          {
	             public void valueChanged(TreeSelectionEvent event)
	             {  
	                // the user selected a different node--update description
	                TreePath path = tree.getSelectionPath();
	                if (path == null) return;
	                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
	                Object uobj = selectedNode.getUserObject();
	                if (uobj instanceof EclipseSolver.EclipseCDAttr){
	                	m_ecda = (EclipseSolver.EclipseCDAttr) uobj;
		                if (m_ecda.isSet()) reset_display();
		                else {
		                	m_set.setEnabled(false);
		                    m_label.setText(m_ecda.toString());
		                    m_entry.setEnabled(true);
		                }	   	                	
	                } else reset_display();	                
	             }
	          });
	       	       
	       int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
	       tree.getSelectionModel().setSelectionMode(mode);

	       Box vbox = Box.createVerticalBox();
	       add(vbox, BorderLayout.CENTER);
	       m_scpane = new JScrollPane(tree);
	       vbox.add(m_scpane);	    
	       
	       m_label = new JLabel();
	       Box labelbox = Box.createHorizontalBox();
	       labelbox.add(Box.createHorizontalGlue());
	       labelbox.add(m_label);
	       labelbox.add(Box.createHorizontalGlue());
	       vbox.add(labelbox);
	       
	       vbox.add(Box.createVerticalStrut(10));
	       m_entry = new JTextField();
	       vbox.add(Box.createVerticalGlue());
	       vbox.add(m_entry); 
	       vbox.add(Box.createVerticalStrut(10));
	       
	       //set up selection mode
	       m_entry.addKeyListener(new KeyAdapter(){
	    	   public void keyTyped(KeyEvent event){
	    		   m_set.setEnabled(true);
	    		   repaint();
	    	   } 
	       });
		       
	       JPanel hbox = new JPanel();
	       hbox.setLayout(new GridLayout(2,3));
	       vbox.add(hbox);
	       
	       m_set = new JButton("Set");
	       Box setbox = Box.createHorizontalBox();
	       setbox.add(m_set);
	       setbox.add(Box.createHorizontalGlue());
	       hbox.add(setbox);
	       
	       m_undo = new JButton("Undo");
	       m_undo.setEnabled(false);
	       Box undobox = Box.createHorizontalBox();
	       undobox.add(Box.createHorizontalGlue());
	       undobox.add(m_undo);
	       undobox.add(Box.createHorizontalGlue());
	       hbox.add(undobox);
	       
	       m_done = new JButton("Done");
	       m_done.setEnabled(false);
	       Box donebox = Box.createHorizontalBox();
	       donebox.add(Box.createHorizontalGlue());
	       donebox.add(m_done);
	       hbox.add(donebox);
	       
	       m_undo_label = new JLabel("");
	       hbox.add(new JPanel());
	       hbox.add(m_undo_label);
	       	       
	       reset_display();
	       
	       m_set.addActionListener(new ActionListener(){
	    	   public void actionPerformed(ActionEvent event){
	    		     boolean succ = m_ecda.process_sel(m_entry.getText());
	    		     if (succ) {
	    		    	 m_label.setText("Attribute set successfully");
	    		    	 m_set.setEnabled(false);
	    		    	 m_entry.setEnabled(false);
	    		     } else {
	    		    	 m_label.setText("Value selected not in range:"+m_ecda.getRangeAsString()+". Please try again");
	    		     }
	    		     m_entry.setText("");
	    	   }
	       });
	       
	       m_undo.addActionListener(new ActionListener(){
	    	   public void actionPerformed(ActionEvent event){
	    		   m_est.undo();
	    	   }
	       });
	    	  
	       m_done.addActionListener(new ActionListener(){
	    	   public void actionPerformed(ActionEvent event){
	    		   m_est.done();
	    	   }
	       });
	      
	       tree.makeVisible(new TreePath(model.getPathToRoot(m_visnode)));
	       
	       URL url = getClass().getResource("HP_ICON.PNG");
	       if (url!=null){
	    	   Image image=Toolkit.getDefaultToolkit().getImage(url);
	    	   if (image!=null){
	    		   setIconImage(image);
	    	   }
	       }
	       	
          
		  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  setVisible(true); 		

	}
	
    private DefaultMutableTreeNode root;
    private DefaultTreeModel model;
    private JTree tree;
    private static final int DEFAULT_WIDTH = 600;
    private static final int DEFAULT_HEIGHT = 400;  
 }

