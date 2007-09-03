package org.smartfrog.sfcore.languages.csf.plugins;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import org.smartfrog.sfcore.languages.csf.constraints.CDBrowserModel;

public class EclipseCDBrowser extends JFrame implements CDBrowserModel {
	private JScrollPane m_scpane;
	private JLabel m_label;
	private JLabel m_undo_label;
	private JTextField m_entry;
	private JButton m_set;
	private JButton m_undo;
	private JButton m_done;
	private EclipseSolver.EclipseStatus m_est;
	private EclipseSolver.EclipseCDAttr m_ecda;
	
	public void kill(){
		setVisible(false);
	}
	
	public void setES(Object est){
		m_est=(EclipseSolver.EclipseStatus) est;
	}
	
	public Object attr(Object d, Object av){
	   DefaultMutableTreeNode node = new DefaultMutableTreeNode(av);
	   
	   if (root==null){
		   root = node;;
	   } else {
		   ((DefaultMutableTreeNode) d).add(node);
	   }
	   return node;
	}
	
	public void redraw(){
	   if (!isVisible()){
		  init();
	   } else {

		   if (m_est.isBack()){
			   m_est.setBack(false);
			   String label = "Attribute setting undone. "+m_label.getText();
    		   m_label.setText(label);
    		   m_set.setEnabled(true);
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
		
	void reset_display(){
		m_label.setText("Click on an attribute with range to set its value");
		m_entry.setText("");
		m_set.setEnabled(false);
		m_entry.setEnabled(false);		
	}	
	
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
		                	m_set.setEnabled(true);
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

