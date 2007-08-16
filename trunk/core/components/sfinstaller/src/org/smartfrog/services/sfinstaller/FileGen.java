package org.smartfrog.services.sfinstaller;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.Enumeration;

/** A class for creating the data file from map 
 * The data file is read for creating the sfinstaller description file
 *
 * @author Ritu Sabharwal
 */
public class FileGen { 

	/** The seperator for fields of data file*/
	static String seperator = " ";

	/** The Daemon object to be read from map*/
	static Daemon host ;
	
	/** An iterator for entries in a map.*/
	static Iterator t;
	
       /** Iterator over the map entries and writes to data file
   	* 
   	* @param mapFile map with daemon objects
   	*/
	static public void createFile(Map mapFile, String inFileName) throws Exception{
	  try {
		  if (!mapFile.isEmpty()) {
		  	Collection values = mapFile.values();
			t = values.iterator();
		  } 

	  PrintStream out = new PrintStream(new FileOutputStream(inFileName, true));
	  BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
	  
	  while (t.hasNext()) {
		host = (Daemon)t.next();
	  writer.write(host.name);
          writer.write(seperator);
	  writer.write(host.os);
          writer.write(seperator);
	  writer.write(host.host);
          writer.write(seperator);
	  writer.write(host.transfertype);
          writer.write(seperator);
	  writer.write(host.logintype);
          writer.write(seperator);
	  writer.write(host.user);
          writer.write(seperator);
	  writer.write(host.password);
          writer.write(seperator);
	  writer.write(host.localfile1);
	  if (host.os.equals("windows")){
          	writer.write(seperator);
	  	writer.write(host.localfile2);
          	writer.write(seperator);
	  	writer.write(host.localfile3);
	  }
	  if (host.keyfile != null)  {
          	writer.write(seperator);
	  	writer.write(host.keyfile);
	  }
	  if (host.secproperties != null)  {
          	writer.write(seperator);
	  	writer.write(host.secproperties);
	  }
	  if (host.smartfrogjar != null)  {
          	writer.write(seperator);
	  	writer.write(host.smartfrogjar);
	  }
	  if (host.servicesjar != null)  {
          	writer.write(seperator);
	  	writer.write(host.servicesjar);
	  }
	  if (host.examplesjar != null)  {
          	writer.write(seperator);
	  	writer.write(host.examplesjar);
	  }
          writer.write(seperator);
	  writer.write(host.releasename);
	//  if (host.os.equals("windows")) {
	    if (host.javahome != null) {
          	writer.write(seperator);
	  	writer.write(host.javahome);
	    }
	//  }
          writer.write(seperator);
	  writer.write(host.installdir);
          writer.write(seperator);
	  writer.write(host.emailto);
          writer.write(seperator);
	  writer.write(host.emailfrom);
          writer.write(seperator);
	  writer.write(host.emailserver);
          writer.write(seperator);
	  writer.write("\n");
	  }
	  writer.flush();
          writer.close();   		  
	  } catch (Exception ex) {
		throw ex;
	  }
	}
}
