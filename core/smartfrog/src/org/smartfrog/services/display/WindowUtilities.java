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
package org.smartfrog.services.display;

import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.border.SoftBevelBorder;

/**
 *  Window Utilities to handle some common window and frame representation
 *  problems
 *
 */
public class WindowUtilities {
   /**
    *  Boolean to indicate graphic mode available
    */
   protected static boolean graphicsAvailable;


   private WindowUtilities(){
   }

   static {
      graphicsAvailable = testGraphics();
   }

   /**
    *  Hastable to associate color and names
    */
   protected static Hashtable colorNames;

   /**
    *  Default color list
    */
   protected static Color[] colorList = {
         Color.yellow, Color.cyan, new Color(255, 255, 200),
         new Color(255, 200, 200), new Color(200, 255, 200)
         };

   /**
    *  Selected color index
    */
   protected static int usedColorListIndexes;


   /**
    *Check if graphics is available
    *
    *@return    true if graphics is available else false
    */
   public static boolean areGraphicsAvailable() {
      return graphicsAvailable;
   }


   /**
    *Tests the graphics and prints message that it is available.
    *
    *@return true if graphics works fine else false
    */
   public static boolean testGraphics() {
      try {
         Frame f = new Frame();
         f.setVisible(false);
         f.dispose();
         if (sfLog().isDebugEnabled()){
           sfLog().debug("Graphics mode available");
         }
         return true;
      } catch (Throwable t) {
          if (sfLog().isWarnEnabled()){
            sfLog().warn("Graphics mode NOT available",t);
          }
      }

      return false;
   }


   /**
    * Centres a component inside a awt container.
    * <p> If the conatiner is null the component is centered in the screen.</p>
    *
    *@param  parent  The awt container
    *@param  comp    The component
    */
   public static void center(Container parent, Component comp) {
      int x;
      int y;
      Rectangle parentBounds;
      Dimension compSize = comp.getSize();

      // If Container is null or smaller than the component
      // then our bounding rectangle is the
      // whole screen
      if ((parent == null) || (parent.getBounds().width < compSize.width) ||
            (parent.getBounds().height < compSize.height)) {
         parentBounds = new Rectangle(comp.getToolkit().getScreenSize());
         parentBounds.setLocation(0, 0);
      }
      // Else our bounding rectangle is the Container
      else {
         parentBounds = parent.getBounds();
      }

      // Place the component so its center is the same
      // as the center of the bounding rectangle
      x = parentBounds.x + (((parentBounds.width) - (compSize.width)) / 2);
      y = parentBounds.y + (((parentBounds.height) - (compSize.height)) / 2);

      comp.setLocation(x, y);
   }


   /**
    * Positions a component inside a container.
    * <p>
    * If the conatiner is null the component is centered in the screen.
    * </p>
    *
    *@param  parent     The container
    *@param  comp       The component
    *@param  positionN  The new position value at y axis
    *@param  positionW  The new position value at x axis
    */
   public static void setPositionWindow(Container parent, Component comp,
         int positionN, int positionW) {
      // Positions: false=0, true=max, null=center
      // Positions: N(1), S(-1), E(-1), W(1), NE(y=0,x=max), NW, SE, SW,C(0);
      int x;

      // Positions: false=0, true=max, null=center
      // Positions: N(1), S(-1), E(-1), W(1), NE(y=0,x=max), NW, SE, SW,C(0);
      int y;
      Rectangle parentBounds;

      //Relative position
      Dimension compSize = comp.getSize();

      // If Container is null or smaller than the component
      // then our bounding rectangle is the
      // whole screen
      if ((parent == null) || (parent.getBounds().width < compSize.width) ||
            (parent.getBounds().height < compSize.height)) {
         parentBounds = new Rectangle(comp.getToolkit().getScreenSize());
         parentBounds.setLocation(0, 0);
      }
      // Else our bounding rectangle is the Container
      else {
         parentBounds = parent.getBounds();
      }

      // Place the component so its center is the same
      // as the center of the bounding rectangle
      // Place the component so its center is the same
      // as the center of the bounding rectangle
      x = parentBounds.x + (((parentBounds.width) - (compSize.width)) / 2);
      y = parentBounds.y + (((parentBounds.height) - (compSize.height)) / 2);

      // N or S
      if ((positionW != 0)) {
         if (positionW > 0) {
            x = parentBounds.x;
         } else {
            x = parentBounds.x + ((parentBounds.width) - (compSize.width));
         }
      }

      // W or E
      if ((positionN != 0)) {
         if (positionN > 0) {
            y = parentBounds.y;
         } else {
            y = parentBounds.y +
                  ((parentBounds.height) - (compSize.height));
         }
      }
      comp.setLocation(x, y);
   }


   /**
    *  Positions a component inside a container
    *  if the conatiner is null the component is centered in the screen.
    *
    *@param  parent           The AWT parent
    *@param  comp             The component
    *@param  positionDisplay  The new direction for value
    */
   public static void setPositionDisplay(Container parent, Component comp,
         String positionDisplay) {
      int north = 0;

      //C
      int west = 0;

      //C
      positionDisplay=positionDisplay.toUpperCase();

      if (positionDisplay.equals("C") == true) {
         north = 0;
         west = 0;
      } else if (positionDisplay.equals("N") == true) {
         north = 1;
         west = 0;
      } else if (positionDisplay.equals("S") == true) {
         north = -1;
         west = 0;
      } else if (positionDisplay.equals("E") == true) {
         north = 0;
         west = -1;
      } else if (positionDisplay.equals("W") == true) {
         north = 0;
         west = 1;
      } else if (positionDisplay.equals("NE") == true) {
         north = 1;
         west = -1;
      } else if (positionDisplay.equals("NW") == true) {
         north = 1;
         west = 1;
      } else if (positionDisplay.equals("SE") == true) {
         north = -1;
         west = -1;
      } else if (positionDisplay.equals("SW") == true) {
         north = -1;
         west = 1;
      }

      org.smartfrog.services.display.WindowUtilities.setPositionWindow(parent,
            comp, north, west);
   }


   /**
    *  Returns the usable screen rectangle size for a componen
    *  deleting the lower part of the screen not to hide the start bar.
    *
    *@param  comp  The component
    *@return       The screenRectangle value
    */
   public static Rectangle getScreenRectangle(Component comp) {
      Rectangle screen = new Rectangle(comp.getToolkit().getScreenSize());
      Dimension d = screen.getSize();
      d.height = d.height - 60;
      screen.setSize(d);

      return screen;
   }


   /**
    * Shows the given Image as a logo on a simple window
    *
    *@param  logo  image icon for the logo
    *@return       JWindow after setting the logo
    */
   public static JWindow showLogo(ImageIcon logo) {
      JWindow logoWin = new JWindow();
      JLabel jl = new JLabel(logo);
      JPanel jp = new JPanel();
      jp.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
      jp.add(jl);
      jp.setSize(jl.getPreferredSize());
      logoWin.setContentPane(jp);
      logoWin.setSize(jp.getPreferredSize());
      center(null, logoWin);
      logoWin.validate();
      logoWin.toFront();
      logoWin.setVisible(true);

      return logoWin;
   }


   /**
    *Shows the progress bar.
    *
    *@param  message  Message displayed in progress bar.
    *@param  steps    Steps
    *@return          JProgressBar with message
    */
   public static JProgressBar showProgressBar(String message, int steps) {
      JProgressBar jpb = new JProgressBar();
      jpb.setMinimum(0);
      jpb.setMaximum(steps);

      JWindow pBarWin = new JWindow();
      JLabel jl = new JLabel(message);
      JPanel jp = new JPanel();
      jp.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
      jp.add(jl);
      jp.add(jpb);
      jp.setSize(jl.getPreferredSize());
      pBarWin.setContentPane(jp);
      pBarWin.setSize(jp.getPreferredSize());
      center(null, pBarWin);
      pBarWin.validate();
      pBarWin.toFront();
      pBarWin.setVisible(true);

      return jpb;
   }


   /**
    *Shows progress bar with logo
    *
    *@param  logo   logo
    *@param  steps  steps
    *@return        JProgressBar object with logo
    */
   public static JProgressBar showLogoProgressBar(ImageIcon logo, int steps) {
      JProgressBar jpb = new JProgressBar();
      jpb.setMinimum(0);
      jpb.setMaximum(steps);

      JWindow pBarWin = new JWindow();
      JLabel jl = new JLabel(logo);
      JPanel jp = new JPanel();
      jp.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));

      GridBagLayout layout = new GridBagLayout();
      jp.setLayout(layout);

      GridBagConstraints constraints = new GridBagConstraints();
      constraints.fill = GridBagConstraints.BOTH;
      constraints.weightx = 2.0;
      constraints.gridwidth = GridBagConstraints.REMAINDER;
      layout.setConstraints(jl, constraints);
      jp.add(jl);
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.weightx = 1.0;
      layout.setConstraints(jpb, constraints);
      jp.add(jpb);
      jp.setSize(jl.getPreferredSize());
      pBarWin.setContentPane(jp);
      pBarWin.setSize(jp.getPreferredSize());
      center(null, pBarWin);
      pBarWin.validate();
      pBarWin.toFront();
      pBarWin.setVisible(true);

      return jpb;
   }


   /**
    *Increments the progress bar.
    *
    *@param  jpb  JProgressBar object
    */
   public static void incProgressBar(JProgressBar jpb) {
      jpb.setValue(jpb.getValue() + 1);
      jpb.repaint();
   }


   /**
    *Disposes progress bar's container
    *
    *@param  jpb  JProgressBar object
    */
   public static void disposeProgressBar(JProgressBar jpb) {
      Container c = jpb.getRootPane().getContentPane();

      while (!(c instanceof Window)) {
         c = c.getParent();
      }

      if (c != null) {
         ((Window) c).dispose();
      } else {
         if (sfLog().isErrorEnabled()){ sfLog().error("Progress bar not disposed");  }
      }
   }


   /**
    * Hides the progress bar's container
    *
    *@param  jpb  JProgressBar object
    */
   public static void hideProgressBar(JProgressBar jpb) {
      Container c = jpb.getRootPane().getContentPane();

      while (!(c instanceof Window)) {
         c = c.getParent();
      }

      if (c != null) {
         ((Window) c).setVisible(false);
      } else {
         if (sfLog().isErrorEnabled()){ sfLog().error("Progress bar not hidden");}
      }
   }


   /**
    * Returns a color associated to the given name
    *
    *@param  name  Color Name
    *@return       The color value associated with the color name
    */
   public static Color getColorAssociated(String name) {
      if (colorNames == null) {
         colorNames = new Hashtable();
         usedColorListIndexes = 0;
      }

      if (!(colorNames.containsKey(name))) {
         Color c;

         if (usedColorListIndexes < colorList.length) {
            c = colorList[usedColorListIndexes];
            usedColorListIndexes++;
         } else {
            int r = 150 + (int) (Math.random() * 105);
            int g = 150 + (int) (Math.random() * 105);
            int b = 150 + (int) (Math.random() % 105);

            c = new Color(r, g, b);
         }

         colorNames.put(name, c);
      }

      return (Color) colorNames.get(name);
   }


   /**
    *Inputs Dialog to retrieve a parameter
    *
    *@param  message  message
    *@return          The userInputString value
    */
   public static String getUserInputString(String message) {
      return JOptionPane.showInputDialog(message);
   }


   /**
    * Asks the user confirmation
    *
    *@param  cp       Component
    *@param  message  Message
    *@return          The user confirmation value
    */
   public static boolean getUserConfirmation(Component cp, String message) {
      Object[] options = {"OK", "CANCEL"};
      int option = JOptionPane.showConfirmDialog(cp, message,  " Please Confirm...", JOptionPane.YES_NO_OPTION);

      if (option == JOptionPane.OK_OPTION) {
         return true;
      } else {
         return false;
      }
   }


   /**
    *Outputs a typed message to the user
    *
    *@param  cp       Component
    *@param  message  message
    */
   public static void showMessage(Component cp, String message) {
      if (sfLog().isDebugEnabled()){ sfLog().debug("Shown message INFO:" + message);}
      JOptionPane.showMessageDialog(cp, message, "Information", JOptionPane.INFORMATION_MESSAGE);
   }


   /**
    *Outputs a warning message to the user
    *
    *@param  cp       Component
    *@param  message  warning message
    */
   public static void showWarning(Component cp, String message) {
      if (sfLog().isDebugEnabled()){ sfLog().debug("Shown message WARNING:" + message);}
      JOptionPane.showMessageDialog(cp, message, "Warning",JOptionPane.WARNING_MESSAGE);
   }


   /**
    *Outputs an error message to the user
    *
    *@param  cp       Component
    *@param  message  error message
    */
   public static void showError(Component cp, String message) {
      if (sfLog().isDebugEnabled()){ sfLog().debug("Shown message ERROR:" + message);}
      JOptionPane.showMessageDialog(cp, message, "Error!", JOptionPane.ERROR_MESSAGE);
   }


   private static LogSF sflog =null;
    /**
     *
     * @return LogSF
     */
    public static LogSF sfLog(){
         if (sflog==null) {
             sflog= LogFactory.getLog("WindowUtilities");
         }
         return sflog;
    }

}
