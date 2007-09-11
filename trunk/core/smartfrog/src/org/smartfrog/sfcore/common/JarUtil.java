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


package org.smartfrog.sfcore.common;

import java.util.Vector;
import java.util.zip.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.io.Serializable;

/**
 *  Title: SmartFrog CVS Description: Copyright:
 */

public class JarUtil {

   /**
    *  Description of the Field
    */
   String[] filters = {".sf",".sf2",".sfxml",".sfcd"};
   Vector listJars = new Vector();
   private Hashtable sfFiles = new Hashtable();
   private Hashtable htSizes = new Hashtable();


   /**
    *  Constructor for the LoadSFFiles object
    */
   public JarUtil() { }


   /**
    *  The main program for the LoadSFFiles class
    *
    *@param  args  The command line arguments
    */
   public static void main(String[] args) {
      JarUtil loadSFFiles = new JarUtil();
      Vector list = loadSFFiles.init();

      //loadSFFiles.getSFFiles(list);
      //System.out.println("Print: " + loadSFFiles.getFile("com/hp/SmartFrog/Prim/prim.sf"));
      //System.out.println("Print: " + loadSFFiles.getFile("/com/hp/SmartFrog/components.s"));
      System.out.println("Total Cache Size:" + loadSFFiles.getSizeCache());
   }


   /**
    *  Gets the sizeCache attribute of the LoadSFFiles object
    *
    *@return    The sizeCache value
    */
   public int getSizeCache() {
      return sfFiles.size();
   }


   /**
    *  Gets the file attribute of the LoadSFFiles object
    *
    *@param  fileName  Description of the Parameter
    *@return           The file value
    */
   public String getFile(String fileName) {
      try {
         return ((String)sfFiles.get(fileName));
      } catch (Exception ex) {
         System.out.println("Error LoadSFFiles.getFile()" + ex.getMessage());
         return ("");
      }

   }


   /**
    *  Gets the sFFiles attribute of the LoadSFFiles object
    *
    *@param  list  Description of Parameter
    */
   public void getSFFiles(Vector list) {
      int index = list.size();
      while ((index--) > 0) {
         System.out.println("Jar: " + list.get(index));
         this.listJarContent((String)list.get(index), filters);
      }
   }


   /**
    *  Description of the Method
    *
    *@return    Description of the Returned Value
    */
   public Vector init() {
      Vector listSF = new Vector();
      String classpath = System.getProperty("java.class.path");
      String pathSeparator = System.getProperty("path.separator");
      String fileSeparator = System.getProperty("file.separator");
      String jarFile = "";
      int index = 0;
      int index2 = 0;
      while (true) {
         index = classpath.lastIndexOf(pathSeparator);
         if (index < 0) {
            break;
         }
         jarFile = classpath.substring(index + 1);
         classpath = classpath.substring(0, index);
         listJars.add(jarFile);
//         if (debugOn) {
//            //System.out.println("jarFile: " + jarFile);
//         }
         this.addJarFiles(jarFile, filters);
      }
//      if (debugOn) {
//         System.out.println("List: " + listJars.toString());
//      }
      return getListSFSorted();
      //return getListSF();
   }


   /**
    *  Description of the Method
    *
    *@param  jarFile  Description of Parameter
    *@param  filter   Description of Parameter
    */
   public void listJarContent(String jarFile, String[] filter) {
      try {

         ZipFile zf = new ZipFile(jarFile);
         int size = 0;
         Enumeration e = zf.entries();
         while (e.hasMoreElements()) {
            ZipEntry ze = (ZipEntry)e.nextElement();
            size = (int)ze.getSize();
//            if (debugOn) {
//               if (ze.getName().endsWith(filter)) {
//                  //System.out.println("name: "+ ze.getName());
//                  //System.out.println("Content:" );
//                  //this.getFileJar(jarFile,ze.getName());
//               }
//            }
         }
         zf.close();
      } catch (Exception ex) {
         System.out.println("Error: " + ex.getMessage());
         ex.printStackTrace();
      }

   }

   /**
    *  Gets the listSF attribute of the LoadSFFiles object
    *
    *@return    The listSF value
    */
   Vector getListSF() {
      Vector list = new Vector();
      for (Enumeration e = this.sfFiles.keys(); e.hasMoreElements(); ) {
         list.add(e.nextElement());
      }
      return list;
   }

   /**
    *  Gets the listSF attribute of the LoadSFFiles object
    *
    *@return    The listSF value
    */
   Vector getListSFSorted() {
      return (sort(this.getListSF()));
   }



   /**
    *  Sort a Vector
    *
    *@param  vect  Description of Parameter
    *@return       Description of the Returned Value
    */
   public static Vector sort(Vector vect) {
      Object[] array = vect.toArray();

      //System.out.println("Array:" + array.toString());
      Arrays.sort(array, new StringComparator());
      //System.out.println("ArraySorted:" + array.toString());
      //vect = new Vector(array);
      vect = new Vector();
      //for (int i =0 ; i < array.length; i++) {
      for (int i = array.length-1 ; i >= 0; i--) {
         vect.add(array[i]);
      }
      return vect;
   }

   /**
    *  initializes internal hash tables with Jar file resources.
    *
    *@param  jarFileName  The feature to be added to the JarFiles attribute
    *@param  fileFilters       The feature to be added to the JarFiles attribute
    */
   private void addJarFiles(String jarFileName, String[] fileFilters) {
      try {

         // extracts just sizes only.
         if (jarFileName==null||jarFileName.endsWith(System.getProperty("file.separator"))||jarFileName.endsWith(".")||jarFileName.equals("")||jarFileName.equals(" ")) {
            return;
         }
         ZipFile zf = new ZipFile(jarFileName);
         Enumeration e = zf.entries();
         while (e.hasMoreElements()) {
            ZipEntry ze = (ZipEntry)e.nextElement();
            htSizes.put(ze.getName(), new Integer((int)ze.getSize()));
         }
         zf.close();

         FileInputStream fis = new FileInputStream(jarFileName);
         BufferedInputStream bis = new BufferedInputStream(fis);
         ZipInputStream zis = new ZipInputStream(bis);
         ZipEntry ze = null;
         while ((ze = zis.getNextEntry()) != null) {
            if (ze.isDirectory()) {
               continue;
            }
            for (int i=0;i<fileFilters.length;i++){
              if ((ze.getName()).endsWith(fileFilters[i])) {
//                 if (debugOn) {
//                    //System.out.println( "ze.getName()="+ze.getName()+","+"getSize()="+ze.getSize());
//                 }
                 int size = (int)ze.getSize();
                 // -1 means unknown size.
                 if (size == -1) {
                    size = ((Integer)htSizes.get(ze.getName())).intValue();
                 }
                 byte[] b = new byte[(int)size];
                 int rb = 0;
                 int chunk = 0;
                 while (((int)size - rb) > 0) {
                    chunk = zis.read(b, rb, (int)size - rb);
                    if (chunk == -1) {
                       break;
                    }
                    rb += chunk;
                 }
                 StringBuffer sb = new StringBuffer(size);
                 int auxindex = -1;
                 while ((auxindex++) < rb - 1) {
                    sb.append((char)(b[auxindex]));
                 }
                 // add to internal resource hashtable
                 this.sfFiles.put(ze.getName(), sb.toString());
//                 if (debugOn) {
//                    System.out.println("Name: " + ze.getName() + ",size=" + size + ",csize=" + ze.getCompressedSize());
//                    //System.out.println(sb);
//                 }
                 break;
              }
           }
         }
         //while
      } catch (NullPointerException e) {
         System.out.println("done.");
      } catch (FileNotFoundException e) {
         System.err.println("File : " + jarFileName + ", " + e.getMessage());
         //e.printStackTrace();
      } catch (IOException e) {
         System.err.println("Error(IO): " + jarFileName + ", " + e.getMessage());
         //e.printStackTrace();
      } catch (Exception ex) {
         System.out.println("File: " + jarFileName);
      }
   }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *created    04 October 2001
 */
class StringComparator  implements Comparator, Serializable {

   /**
    *  Description of the Method
    *
    *@param  o1  Description of Parameter
    *@param  o2  Description of Parameter
    *@return     Description of the Returned Value
    */
   public int compare(
         Object o1, Object o2) {
      if (!(o1 instanceof String)) {
         throw new ClassCastException();
      }
      if (!(o2 instanceof String)) {
         throw new ClassCastException();
      }

      int result = ((String)o1).
            compareTo(((String)o2));
      return result * (-1);
   }
   //end compare()
}
//end class TheComparator

