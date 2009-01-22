package org.smartfrog.examples.sudoku;

import java.util.Vector;

import org.smartfrog.SFParse;
import org.smartfrog.SFParse.RawParseModifier;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

public class SudokuApplication extends BaseSudokuApplication {
	
	public void process(){
		//Collect together puzzle input...
		final Vector puzzle = new Vector();
		for(int i=0;i<bsfe.board.size();i++){
			Location l = convertLocation(i);
			Vector entry = new Vector();
			String text = bsfe.board.get(i).getText();
			if (text.equals("")) continue;
			Integer val = Integer.parseInt(text);
			entry.add(l.loc);
			entry.add(val);
			puzzle.add(entry);
		}
		
		ComponentDescription cd = null;
		
		try {
			cd=SFParse.parseFileToDescription(sudoko9, new RawParseModifier(){
	
			public void modify(ComponentDescription cd){
				ComponentDescription sfConfig = (ComponentDescription) cd.sfContext().get("sfConfig");
				sfConfig.sfContext().put("puzzle", puzzle);
			}
		});
		} catch (Throwable t){/*Do nothing*/}
		
		if (cd!=null){
			Vector result = (Vector) cd.sfContext().get("puzzle");
			
			for(int i=0; i<result.size(); i++){
				Vector square = (Vector) result.get(i);
				Vector loc = (Vector) square.get(0);
				int r = ((Integer)loc.get(0)).intValue();
				int c = ((Integer)loc.get(1)).intValue();
				int sq = convertLocationToIndex(r, c);
				int v = ((Integer)square.get(1)).intValue();
				bsfe.board.get(sq).setText(""+v);
			}
			bsfe.unsolvable.setText("");
		} else {
			//System.out.println("No Solution");
			bsfe.unsolvable.setText("No solution found");
		}
	}
	
	public static void main(String[] args){
		properties();
		SudokuApplication frame = new SudokuApplication();
		frame.app_init();
	}
	
}
