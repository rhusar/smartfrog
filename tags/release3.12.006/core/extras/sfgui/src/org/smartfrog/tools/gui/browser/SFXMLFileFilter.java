package org.smartfrog.tools.gui.browser;


import java.io.*;


class SFXMLFileFilter extends javax.swing.filechooser.FileFilter {
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
            if (extension.equals("sfxml"))
            {
             accepted = true;
            }
         }
         return accepted;
      }
      public String getDescription()
      {
         return "SmartFrog XML file (*.sfxml)";
      }
}
