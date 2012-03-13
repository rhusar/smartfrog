package org.smartfrog.examples.sudoku;

import javax.swing.JApplet;
import javax.swing.JLabel;

public class SudokuApplet extends JApplet {
	public void init(){
		SudokuApplicationClient sud = new SudokuApplicationClient();
		setContentPane(sud.bsfe.init());
	}
}
