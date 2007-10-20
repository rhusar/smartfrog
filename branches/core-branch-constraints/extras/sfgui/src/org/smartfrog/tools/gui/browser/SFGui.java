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


package org.smartfrog.tools.gui.browser;



import javax.swing.UIManager;
import java.awt.*;
import javax.swing.ImageIcon;

public class SFGui {
  boolean packFrame = false;
  /**Construct the application*/
  // fileName = Name of the file to open automatically in the editor
  // runAll = runs every process in the process manager once the SFGui is instanciated
  public SFGui(String fileName, boolean runAll, boolean eclipseMode) {
    MainFrame frame = null;
//    if (fileName=="") {
//      frame = new MainFrame();
//    } else {
      frame = new MainFrame(fileName, runAll, eclipseMode);
//    }
    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, e.g. from their layout
    if (packFrame) {
      frame.pack();
    }
    else {
      frame.validate();
    }
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);
  }
  /**Main method*/
  public static void main(String[] args) {
    try {
      //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());            //Automatic
//      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");            //Metal
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");     // Motif
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); //Windows
      try {
         UIManager.setLookAndFeel(new com.incors.plaf.kunststoff.KunststoffLookAndFeel());
         //SwingUtilities.updateComponentTreeUI(this);
      } catch (Exception ex) {
         ex.printStackTrace();
      }

      System.out.println("Starting SFGui..."+org.smartfrog.tools.gui.browser.MainFrame.version);
      String fileName="";
      boolean runAll = false;
      boolean eclipseMode = false;
      int numberParameters=args.length;
      if((args!=null)&&(numberParameters>0)) {
         if (numberParameters>1) {numberParameters=2;} // Max number parameters 2 (file to load and -runAll)
         numberParameters--;
         for(;numberParameters>=0;numberParameters--){
           if (args[numberParameters].equals("-runAll")){
            runAll=true;
           } else if (args[numberParameters].equals("-eclipse")) {
             eclipseMode=true;
           } else {
            fileName =args[numberParameters];
           }
         }
      } else {
        fileName="";
      }
      new SFGui(fileName,runAll,eclipseMode);
    }
    catch(Exception e) {
      System.out.println("SFGui Error - Exit!! "+org.smartfrog.tools.gui.browser.MainFrame.version);
      e.printStackTrace();
    } finally {
       //System.out.println("...SFGui finished.");
    }
  }
}
