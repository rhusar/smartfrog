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

/**
 * <p>Turning the cache on will cause the entire file to stay
 * in memory. Turning the cache off will cause the file to be
 * read line by line each time a setting is retrieved. Also, if
 * caching is turned on, all changes will not be saved until
 * the <code>flush()</code> method is called. When caching is
 * turned off, all writes are immediate.
 *
 * Some code from "IniFile.java" (Jeff L. Williams (http://www.jeffguy.com/java/))
 */

package org.smartfrog.services.filesystem.replacevar;

import java.io.*;

import java.util.Vector;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

public class ReplaceVar {

  private String      filename; //File to parse.
  private String      newfilename=null; //File to parse.
  private HashMap     data;
  private Vector      cache = new Vector();
  private boolean     cached = false;
  private boolean     errorLoading=false;

  /**
   * Passes the filename to the object on creation
   *
   * @param filename  the name of the file to use
   */
  public ReplaceVar (String filename) {
    this.filename = filename;
  }

  /**
   * Passes the filename to the object on creation
   *
   * @param filename  the name of the file to use
   * @param newfilename  the name of the new file
   */
  public ReplaceVar (String filename, String newfilename) {
    this.filename = filename;
    this.newfilename = newfilename;
  }

  /**
   * Passes the filename to the object on creation
   *
   * @param filename  the name of the file to use
   * @param newfilename  the name of the new file
   * @param cached    whether the file is cached in memory or not

   */
  public ReplaceVar (String filename, String newfilename, boolean cached) {
    this.filename = filename;
    this.newfilename = newfilename;
    this.cached = cached;
    this.loadCache();
  }

   /**
   * Passes the filename to the object on creation
   *
   * @param filename  the name of the file to use
   * @param cached    whether the file is cached in memory or not

   */
  public ReplaceVar (String filename, boolean cached) {
    this.filename = filename;
    this.cached = cached;
    this.loadCache();
  }

//----------------------------------------------------------------------------

  /**
   * InString finds an string within an existing string
   * starting from a specified location
   *
   * @param   string          the string to be searched
   * @param   search          what to look for in the string
   * @param   startPosition   where to start searching in the string
   * @return  returns an integer that holds the location
   *          to the first occurance found in the string
   *          from the starting position (-1 if nothing was found)
   */
  private int inString(String string,
                       String search,
                       int startPosition) {

    //Create a variable that will hold the position of the search string
    int pos = -1;

    //If what we are looking for is longer than the original string,
    //then it's not going to be a match and return 0
    if (search.length() > string.length()) {
      pos = -1;

    } else {

      //We can't search past the end of the string so find a stopping point
      int stopPosition = (string.length() - search.length()) + 1;

      //Loop through the positions in the string looking for the criteria
      for (int i = startPosition - 1; i < stopPosition; i++) {
        String sTemp = string.substring(i, i + search.length());
        //If we found a match, break out and return the results
        if (sTemp.compareTo(search) == 0) {
          pos = i + 1;
          break;
        }
      }
    }
    return pos;
  }

//----------------------------------------------------------------------------

  /**
   * Determines if the file is cached in memory or read each
   * time it is called.
   *
   * @param cached    true if the entire file is kept in memory;
   *                  false if the file is not kept in memory

   */
  public void setCachedState(boolean cached) {
    this.cached = cached;
    if (!cached) this.cache.clear();
  }

//----------------------------------------------------------------------------

  /**
   * Returns true if the file is cached in memory or false if it
   * is read each time the object is called.
   *
   * @return true if the entire file is kept in memory;
   *                  false if the file is not kept in memory

   */
  public boolean getCachedState() {
    return this.cached;
  }

//----------------------------------------------------------------------------

  /**
   * Sets the name of the file the object is using.
   *
   * @param filename  the name of the file to use

   */
  public void setFilename(String filename) {
    this.filename = filename;
  }

//----------------------------------------------------------------------------

  /**
   * Returns a String that contains the name of the
   * file currently being used.
   *
   * @return          a String that contains the name of the file

   */
  public String getFilename() {
    return this.filename;
  }


//----------------------------------------------------------------------------

  /**
   * Loads the file into an internal vector to cache it in
   * memory.
   *

   */
  private synchronized void loadCache() {
    errorLoading = false;
    String thisLine;
    //Clear the current cache
    cache.clear();
    if ((this.cached)&& (this.filename!=null) && (!(this.filename.equals("")))) {
      try {
        //Do not allow other threads to read from the input
        //or write to the output while this is taking place
        LineNumberReader lineReader = new LineNumberReader(
                                 new FileReader(this.filename));
        //Loop through each line and add non-blank
        //lines to the Vector
        while ((thisLine = lineReader.readLine()) != null) {
          //cache.add(thisLine.trim());
          cache.add(thisLine);
        }
        lineReader.close();
      } catch (IOException e) {
         System.err.println(e.getMessage());
         errorLoading = true;
      }
    }
  }


//----------------------------------------------------------------------------

  /**
   * Sets a value for a specified key. This temporarily loads the entire
   * file into memory if it is not already cached. If
   * the file is cached, the change is only in memory until the
   * <code>flush()</code> method is called. If the file is not cached,
   * then writes happen immediately.
   *
   * @param key       the name of the key to change
   * @param value     the String value to change the key to

   */
  public void setSetting(String key, String value) {

    //If the file is cached in memory
    if (this.cached) {
      setSettingCached(key, value);

    //The file is not cached in memory
    } else {
      this.cached = true;
      this.loadCache();
      this.setSettingCached( key, value);
      this.flush();
      this.setCachedState(false);
    }
  }

//----------------------------------------------------------------------------

  /**
   * Set settings
   * @param data  the hashmap

   */
  public void setSetting(HashMap data) {
    //If the file is cached in memory
    if (this.cached) {
      setSettingCached(data);

    //The file is not cached in memory
    } else {
      this.cached = true;
      this.loadCache();
      this.setSettingCached(data);
      this.flush();
      this.setCachedState(false);
    }
  }

//----------------------------------------------------------------------------
   /**
   * Set settings when cached
   * @param data   hashmap to write

   */
  public void setSettingCached(HashMap data) {
     String key = null;
     Map.Entry dataElement=null;
     Set dataSet = data.entrySet();
     Iterator dataIter = dataSet.iterator();
     while( dataIter.hasNext() ) {
        dataElement =(Map.Entry) dataIter.next();
        setSettingCached((String)dataElement.getKey(),(String)dataElement.getValue());
     }
  }

  //----------------------------------------------------------------------------


    /**
     * Set settings
     * @param data
     */
  public void setSetting(Vector data) {
    if (data==null) return;
    //If the file is cached in memory
    if (this.cached) {
      setSettingCached(data);

    //The file is not cached in memory
    } else {
      this.cached = true;
      this.loadCache();
      this.setSettingCached(data);
      //this.flush();
      //this.setCachedState(false);
    }
  }

  //----------------------------------------------------------------------------
  /**
   * Set settings when cached
   * @param data
   */
  public void setSettingCached(Vector data) {
     if (data==null) return;
     String key = null;
     Vector dataElement=null;
     if (data==null) return;
     data.trimToSize();
     int size=data.size();
     if (size==0) {
        return;
     }
     for (int i = 0; i < size; i++) {
        if (data.elementAt(i) instanceof Vector){
           dataElement =(Vector)data.elementAt(i);
           if (dataElement!=null)
              setSettingCached((String)dataElement.elementAt(0),(String)dataElement.elementAt(1));
        }
     }
  }

//----------------------------------------------------------------------------

 //----------------------------------------------------------------------------

    /**
     * Append data
     * @param appendData
     */
  public void append(Vector appendData) {
    if (appendData==null) return;
    //If the file is cached in memory
    if (this.cached) {
      appendCached(appendData);

    //The file is not cached in memory
    } else {
      this.cached = true;
      this.loadCache();
      this.appendCached(appendData);
      //this.flush();
      //this.setCachedState(false);
    }
  }

    /**
     * Append data when cached
     * @param appendData
     */
  public void appendCached(Vector appendData) {
     if (appendData==null) return;
     String key = null;
     Object dataElement=null;
     if (appendData==null) return;
     appendData.trimToSize();
     int size=appendData.size();
     if (size==0) {
        return;
     }
     for (int i = 0; i < size; i++) {
        dataElement =(Object)appendData.elementAt(i);
        //System.out.println(" Element is "+ dataElement.toString());
        if (dataElement!=null)
           this.cache.add(dataElement);
     }

  }

//----------------------------------------------------------------------------




  /**
   * Saves the cached information back out to the
   * file. This method is only available when caching
   * is turned on. Otherwise, it does nothing. If you do not call this
   * method when caching is turned on, your changes will not be saved.
   *

   */
  public synchronized void flush() {
    if (errorLoading) return;
    if (this.cached) {
      try {
        String file=this.filename;
        if ((this.newfilename!=null)&&(!(this.newfilename.equals("")))){
          file=this.newfilename;
        }
        //Do not allow other threads to read from the input
        //or write to the output while this is taking place
        FileWriter fileWriter = new FileWriter(file);
        // Loop through the vector
        for (int i = 0; i < this.cache.size(); i++) {
          //Write out each line
          System.out.println(" Flushing element  "+ cache.elementAt(i).toString());

          fileWriter.write(cache.elementAt(i).toString());

          //If this is not the last line of the file
          if (i < (this.cache.size() - 1)) {
            //Append a carriage return and line feed
            //fileWriter.write(13);
            //fileWriter.write(10);
            fileWriter.write(System.getProperty("line.separator"));
          }
        }

        //Write out and close
        fileWriter.flush();
        fileWriter.close();
      } catch (IOException e) { }
    }
    this.setCachedState(false);
  }

//----------------------------------------------------------------------------

  /**
   * Sets a value for a specified key in the cached Vector object
   *
   * @param key       the name of the key to change
   * @param value     the String value to change the key to

   */
  private void setSettingCached(String key, String value) {
    if (errorLoading) return;

    String currentLine      = new String("");
    String newLine          = new String("");
    int index = 0;

    //Loop through the vector
    for (int i = 0; i < this.cache.size(); i++) {
      //Get the current line of text
      currentLine = this.cache.elementAt(i).toString();
      //If it's an empty line
      //if (currentLine.trim().length() > 0) {
      if (currentLine.length() > 0) {
        index = this.inString(currentLine, key , 1);
        if (index >= 0) {
          newLine = new String("");
          if ((index>0)){
             newLine = currentLine.substring(0,index-1)
                     + value
                     + currentLine.substring(index+key.length()-1);
          } else {
             index=0;
             newLine = value
                     + currentLine.substring(index+key.length()-1);
          }
          //newLine.trim();
          this.cache.set(i,newLine);
          i=i-1; // If we have modified the line we parse it again.
        }
      }
    }//next
  }
    /**Main method*/
//  public static void main(String[] args) {
//    ReplaceVar replaceVar = null;
//    try {
//      String fileName="";
//      boolean runAll = false;
//      int numberParameters=args.length;
//      if((args!=null)&&(numberParameters>2)) {
//            System.out.println("Parsing...");
//            System.out.println("  - File: "+args[0]);
//            System.out.println("  - Key: "+args[1]+", Value:"+args[2]);
//            replaceVar = new ReplaceVar (args[0]);
//            replaceVar.setSetting(args[1],args[2]);
//            //Parse
//      }  else {
//        System.err.println("Help ReplaceVar: java org.smartfrog.services.filesystem.replacevar.ReplaceVar <file> <key> <value>");
//      }
//    }
//    catch(Exception e) {
//      System.out.println("ReplaceVar Error - Exit!!");
//      e.printStackTrace();
//    } finally {
//       //System.out.println("...SFGui finished.");
//    }
//  } //End main
}
