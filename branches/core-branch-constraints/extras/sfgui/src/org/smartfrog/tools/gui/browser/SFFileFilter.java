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


import java.io.*;


class SFFileFilter extends javax.swing.filechooser.FileFilter {
      public boolean accept(File file)
      {
         if (file.isDirectory())
            return true;

         String filename = file.getName();
         int periodIndex = filename.lastIndexOf('.');
         boolean accepted = false;
         if (periodIndex>0 && periodIndex<filename.length()-1)
         {
            String extension = filename.substring(periodIndex+1).toLowerCase();
            if (extension.equals("sf"))
            {
             accepted = true;
            }
         }
         return accepted;
      }
      public String getDescription()
      {
         return "SmartFrog file (*.sf)";
      }
}
