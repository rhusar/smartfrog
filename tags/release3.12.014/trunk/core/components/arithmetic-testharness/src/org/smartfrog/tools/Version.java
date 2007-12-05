package org.smartfrog.tools;

//import org.smartfrog.tools;

/**
 *  Description of the Class
 *
 *@author     julgui
 *created    23 October 2001
 */
public class Version {
   /**
    *  Description of the Field
    */
   public static String versionString = "1.00.00";

   /**
    *  Gets the version attribute of the Version class
    *
    *@return    The version value
    */
   public static String getVersion() {
      return versionString;
   }

   /**
    *  The main program for the Version class
    *
    *@param  args  The command line arguments
    */
   public static void main(String[] args) {
      System.out.print(versionString);
   }
}
