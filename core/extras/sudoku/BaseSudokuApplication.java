package org.smartfrog.examples.sudoku;

import java.awt.Image;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.smartfrog.examples.sudoku.BaseSudokuFrontEnd;
import org.smartfrog.examples.sudoku.BaseSudokuFrontEnd.ProcessSquares;

abstract public class BaseSudokuApplication extends JFrame implements ProcessSquares {
	static String HPICON_PATH = "/org/smartfrog/examples/sudoku/HP_ICON.PNG";
	static String sudoko9 = "/org/smartfrog/examples/sudoku/sudoko9.sf";
	static String SOLVERCLASS_KEY = "org.smartfrog.sfcore.languages.sf.constraints.SolverClassName";
	static String SOLVERCLASS_VAL = "org.smartfrog.sfcore.languages.sf.constraints.eclipse.EclipseSolver";
	static String CDBROWSER_KEY = "org.smartfrog.sfcore.languages.sf.constraints.CDBrowser";
	static String CDBROWSER_VAL = "org.smartfrog.sfcore.languages.sf.constraints.eclipse.EclipseCDBrowser";
	static String THEORYFILE_KEY = "org.smartfrog.sfcore.languages.sf.constraints.theoryFile0";
	static String THEORYFILE_VAL = "core.ecl";
	static String ECLIPSEDIR_KEY = "org.smartfrog.sfcore.languages.sf.constraints.eclipseDir";
	static String ECLIPSEDIR_VAL = "/usr/share/eclipse-clp";
	
	BaseSudokuFrontEnd bsfe; { bsfe= new BaseSudokuFrontEnd(this); }
	
	protected static void properties(){
		Properties sysProps = System.getProperties();
		sysProps.put(ECLIPSEDIR_KEY, ECLIPSEDIR_VAL);
		sysProps.put(THEORYFILE_KEY, THEORYFILE_VAL);
		sysProps.put(SOLVERCLASS_KEY, SOLVERCLASS_VAL);
		sysProps.put(CDBROWSER_KEY, CDBROWSER_VAL);
	}
        
	public static class Location{
		Vector loc = new Vector();
		void put(int r, int c){
			loc.add(new Integer(r));
			loc.add(new Integer(c));
		}
	}
	
	public abstract void process();
	
	public static Location convertLocation(int idx){
		Location loc = new Location();
		
		int r = idx/27;  
		int rem = idx - 27*r;
		int sq = rem/9;
		int remsq = rem - 9*sq;
		int y = remsq/3;
		int x = remsq - 3*y;
		
		loc.put(sq*3 + x, r*3 + y);
		return loc;
	}
	
	public static int convertLocationToIndex(int row, int col){
		int x = row/3;
		int y = col/3;
		
		int xoff = row- (3*x);
		int yoff = col- (3*y);
		
		int sq = ((y*3 + x)*9) + 3*yoff + xoff;  
		
		return sq;
	}
	
	void app_init(){
		setContentPane(bsfe.init());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Su Doko Solver, powered by SmartFrog");
		URL url = getClass().getResource(HPICON_PATH);
        Image image = new ImageIcon(url).getImage();
        setIconImage(image);
        pack();
		setVisible(true);
	}
	
}
