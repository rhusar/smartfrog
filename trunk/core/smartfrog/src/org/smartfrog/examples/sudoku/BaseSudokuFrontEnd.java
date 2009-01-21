package org.smartfrog.examples.sudoku;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.smartfrog.SFParse;
import org.smartfrog.SFParse.RawParseModifier;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;

public class BaseSudokuFrontEnd {
	
	static String AILLOGO_PATH = "/org/smartfrog/examples/sudoku/sflabsng.PNG";
	static String HPBACK = "/org/smartfrog/examples/sudoku/hpback.PNG";
	
	Vector<JTextField> board;
	JLabel unsolvable = new JLabel();
	Container pane;
	ProcessSquares ps;
	
	public BaseSudokuFrontEnd(ProcessSquares ps){
		this.ps=ps;
	}
	
	public Container init(){
		pane = new JImagePanel();
        
        JPanel centre = new JPanel();
        centre.setOpaque(false);
        centre.setSize(300,200);
        pane.add(centre);
        
        Box vbox = Box.createVerticalBox();
        centre.add(vbox);
       
        Box sbox = Box.createHorizontalBox();
        vbox.add(sbox);
        sbox.add(Box.createHorizontalGlue());
        
        JLabel sudoko = new JLabel("Su Doku");
        sbox.add(sudoko);
        sudoko.setFont(new Font("Helvetica", Font.BOLD, 24));
        sudoko.setForeground(Color.WHITE);
        sbox.add(Box.createHorizontalGlue());
        
        vbox.add(Box.createVerticalStrut(10));
        
        Box ebox = Box.createHorizontalBox();
        vbox.add(ebox);
        ebox.add(Box.createHorizontalGlue());
        
        JLabel enter = new JLabel("Enter values where pre-defined");
		enter.setFont(new Font("Helvetica", Font.PLAIN, 12));
        enter.setForeground(Color.WHITE);
        ebox.add(enter);      
        
        vbox.add(Box.createVerticalStrut(5));
         
        board = new Vector<JTextField>();
        
        for (int i=0; i<3; i++){
        	Box row = Box.createHorizontalBox(); 
         	for (int j=0; j<3; j++){
        	
	        	JPanel entergrid = new JPanel();
	            entergrid.setOpaque(false);
	            entergrid.setLayout(new GridLayout(3,3));
	            
	            for (int k=0;k<9;k++){
	            	JTextField field = new JTextField(1);
	            	board.add(field);
	            	entergrid.add(field);
	            } 
	            row.add(entergrid);
	            row.add(Box.createHorizontalStrut(5));
        	}
       	        	
        	vbox.add(row);
            vbox.add(Box.createVerticalStrut(5));
        }
        vbox.add(Box.createVerticalStrut(10));
		
        //Prespecifieds...
        board.get(3).setText("1");
        board.get(5).setText("6");
        
        board.get(10).setText("8");
        board.get(17).setText("6");
        
        board.get(18).setText("5");
        board.get(24).setText("2");
        board.get(25).setText("7");
        board.get(26).setText("9");
        
        board.get(31).setText("2");
        board.get(33).setText("8");
        board.get(34).setText("5");
        
        board.get(39).setText("4");
        board.get(41).setText("9");
        
        board.get(46).setText("6");
        board.get(47).setText("1");
        board.get(49).setText("8");
        
        board.get(54).setText("9");
        board.get(55).setText("1");
        board.get(56).setText("2");
        board.get(62).setText("8");
        
        board.get(63).setText("5");
        board.get(70).setText("4");
        
        board.get(75).setText("7");
        board.get(77).setText("2");
        
        Box bbox = Box.createHorizontalBox();
        vbox.add(bbox);
        bbox.add(Box.createHorizontalGlue());
        JButton solve = new JButton("Solve");
        solve.setFont(new Font("Helvetica", Font.PLAIN, 12));
        solve.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		ps.process();
        	}
        });
        
        bbox.add(solve);
        bbox.add(Box.createHorizontalGlue());
        JButton clear = new JButton("Clear");
        clear.setFont(new Font("Helvetica", Font.PLAIN, 12));
        clear.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		clear();
        	}
        });
        
        bbox.add(clear);
        bbox.add(Box.createHorizontalGlue());
        
        vbox.add(Box.createVerticalStrut(20));
        
        Box pmsgbox = Box.createHorizontalBox();
        vbox.add(pmsgbox);
        pmsgbox.add(Box.createHorizontalGlue());
        JLabel pmsglabel = new JLabel("Powered by:");
        pmsglabel.setForeground(Color.WHITE);
        pmsglabel.setFont(new Font("Helvetica", Font.PLAIN, 10));
		pmsgbox.add(pmsglabel);
        pmsgbox.add(Box.createHorizontalGlue());
        
        vbox.add(Box.createVerticalStrut(5));
        
        URL url = getClass().getResource(AILLOGO_PATH);
        ImageIcon powimage = new ImageIcon(url);
        
        Box powbox = Box.createHorizontalBox();
        vbox.add(powbox);
        powbox.add(Box.createHorizontalGlue());
        JLabel powlabel = new JLabel(powimage);
		powbox.add(powlabel);
        powbox.add(Box.createHorizontalGlue());
        
        vbox.add(Box.createVerticalStrut(10));
        
        Box failbox = Box.createHorizontalBox();
        vbox.add(failbox);
        failbox.add(Box.createHorizontalGlue());
		failbox.add(this.unsolvable);
		unsolvable.setFont(new Font("Helvetica", Font.PLAIN, 10));
		unsolvable.setForeground(Color.WHITE);
        failbox.add(Box.createHorizontalGlue());
        
        //pack();
        
        return pane;
   }
	
	
	private void clear(){
		for (JTextField square: board){
			square.setText("");
		}
	}
	
	private class JImagePanel extends JPanel {
	    Image hpback;
	 
	    public JImagePanel() {
	      URL url = getClass().getResource(HPBACK);
	      hpback = new ImageIcon(url).getImage();
	      setPreferredSize(new Dimension(hpback.getWidth(null), hpback.getHeight(null))); 
	    }
	 
	    public void paintComponent (Graphics g) {
	    	super.paintComponent(g);
	       g.drawImage(hpback,0,0,getWidth(),getHeight(),null);
	    }
	  }
	
	  public interface ProcessSquares {
		  void process();
	  }
	
}
