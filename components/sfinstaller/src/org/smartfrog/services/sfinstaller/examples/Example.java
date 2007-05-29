package org.smartfrog.services.sfinstaller.examples;

import java.util.HashMap;
import org.smartfrog.services.sfinstaller.Daemon;
import org.smartfrog.services.sfinstaller.TemplateGen;

public class Example { 
 
  static public void main(String[] args) {
	 String jars[] = {"http://eb97201:8080/velocity.jar", "http://eb97201:8080/abc.jar", "http://eb97201:8080/xyz.jar"};
	 
	 String templateFile = "D:\\cvs\\forge\\2006\\jan02\\core\\components\\sfinstaller\\bin\\sfInstaller.vm";
	 
	 String outputFile = "D:\\installer.sf";
	 
	 boolean securityOn = true;
	 
	 boolean dynamicLoadingOn = true;

	// String httpserver = "eb97201.india.hp.com";
	 
	 HashMap map = new HashMap();
	 Daemon host1 = new Daemon("daemon1", "linux", "ebnt171.india.hp.com", "scp", "ssh", "root", "D:\\\\cvs\\\\forge\\\\node1.txt", "D:\\\\cvs\\\\releases\\\\SmartFrog.3.08.004.20051216_ALL.tar.gz", null, null, "D:\\\\cvs\\\\releases\\\\SmartFrog.3.08.004\\\\dist\\\\private\\\\host0240\\\\mykeys.st", "D:\\\\cvs\\\\releases\\\\SmartFrog.3.08.004\\\\dist\\\\private\\\\host0240\\\\SFSecurity.properties", "D:\\\\cvs\\\\releases\\\\SmartFrog.3.08.004\\\\dist\\\\signedLib\\\\smartfrog-3.08.004.jar", "D:\\\\cvs\\\\releases\\\\SmartFrog.3.08.004\\\\dist\\\\signedLib\\\\sfServices-3.08.004.jar", "D:\\\\cvs\\\\releases\\\\SmartFrog.3.08.004\\\\dist\\\\signedLib\\\\sfExamples-3.08.004.jar", "SmartFrog.3.08.004", "/usr/java/j2sdk1.4.2_08", "/tmp", "ritu@hp.com", "smartfrog.stsd@hp.com", "redsea.india.hp.com");
	 Daemon host2 = new Daemon("daemon2", "windows", "eb97201.india.hp.com", "ftp", "telnet", "ritu", "D:\\\\cvs\\\\forge\\\\node2.txt", "D:\\\\cvs\\\\releases\\\\SmartFrog.3.08.004.20051216_ALL.zip", "D:\\\\cvs\\\\forge\\\\2006\\\\jan02\\\\core\\\\components\\\\sfinstaller\\\\bin\\\\JavaService.exe" , "D:\\\\cvs\\\\forge\\\\2006\\\\jan02\\\\core\\\\components\\\\sfinstaller\\\\bin\\\\start_sfdaemon.bat", "D:\\\\cvs\\\\releases\\\\SmartFrog.3.08.004\\\\dist\\\\private\\\\host0240\\\\mykeys.st", "D:\\\\cvs\\\\releases\\\\SmartFrog.3.08.004\\\\dist\\\\private\\\\host0240\\\\SFSecurity.properties", "D:\\\\cvs\\\\releases\\\\SmartFrog.3.08.004\\\\dist\\\\signedLib\\\\smartfrog-3.08.004.jar", "D:\\\\cvs\\\\releases\\\\SmartFrog.3.08.004\\\\dist\\\\signedLib\\\\sfServices-3.08.004.jar", "D:\\\\cvs\\\\releases\\\\SmartFrog.3.08.004\\\\dist\\\\signedLib\\\\sfExamples-3.08.004.jar", "SmartFrog.3.08.004", "D:\\\\installables\\\\j2sdk1.4.2_08", "C:\\\\temp", "ritu@hp.com", "smartfrog.stsd@hp.com", "redsea.india.hp.com");
	 map.put ("host1", host1);
	 map.put ("host2", host2);
	 
	 
	 try {
		  // to read from map and write to data.all and then create a description
		  TemplateGen.createTemplate(map, templateFile, outputFile, securityOn, dynamicLoadingOn, jars, "D:\\");
	  } catch (Exception ex) {
	   //   throw ex;	  	
	  }
  }
}
