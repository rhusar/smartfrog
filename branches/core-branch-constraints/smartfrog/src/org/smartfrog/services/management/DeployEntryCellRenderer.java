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

package org.smartfrog.services.management;

import javax.swing.ImageIcon;
import javax.swing.JTree;

import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;

public class DeployEntryCellRenderer extends DefaultTreeCellRenderer {

  /** Log for this class, created using class name*/
  static LogSF sfLogStatic = LogFactory.getLog("sfManagementConsole");

  // icons for the Tree
  ImageIcon icon = null;
  private String image_path = "org/smartfrog/services/management/icons/";
  private String prim_icon_name = "prim.gif";
  private String icon_name = prim_icon_name;
  private String compound_icon_name = "compound.gif";
  private String processCompound_icon_name = "processCompound.gif";
  private String componentDescription_icon_name = "componentDescription.gif";

  private String image = null;

  String name ="";

  public DeployEntryCellRenderer() {
    //ImageIcon icon;
    image = image_path + prim_icon_name;
    icon = createImageIcon(image);
  }

  public Component getTreeCellRendererComponent(JTree tree,
                                                Object value,
                                                boolean selected,
                                                boolean expanded,
                                                boolean leaf,
                                                int row,
                                                boolean hasFocus) {
    super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

    if (value instanceof DeployEntry) value = ((DeployEntry)value).getEntry();

    if ((value instanceof Prim)||(value instanceof ComponentDescription)) {
        if (value instanceof ProcessCompound) {
          icon_name = processCompound_icon_name;
        } else if (value instanceof Compound) {
          icon_name = compound_icon_name;
        } else if (value instanceof Prim) {
          icon_name = prim_icon_name;
        } else if (value instanceof ComponentDescription) {
          icon_name = componentDescription_icon_name;
        }
      image = image_path + icon_name;
      icon = createImageIcon(image);
    }
    // if(data != null) {
    setIcon(icon);
    return this;
  }

  /**
   * Returns an ImageIcon, or null if the path was invalid.
   * @return ImageIcon or null
   */
  protected static ImageIcon createImageIcon(String path) {
    try {
      return new ImageIcon(org.smartfrog.SFSystem.getByteArrayForResource(path));
    } catch (SmartFrogException ex) {
      if (sfLogStatic.isErrorEnabled()) sfLogStatic.error(ex);
    }
    return null;
  }


}
