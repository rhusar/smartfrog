/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Aug 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.javawscore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.client.sf.apps.gt4.javawscore.utils.EditXML;
import org.smartfrog.avalanche.client.sf.disk.DiskUtils;
import org.smartfrog.avalanche.client.sf.exec.ant.AntException;
import org.smartfrog.avalanche.client.sf.exec.ant.AntUtils;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;


/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeployToTomcat {
	private String globusLocation = null;
	private String buildFile = null;
	private String tomcatDirectory = null;
	private String webappName = null;
	
	private static final Log log = LogFactory.getLog(DeployToTomcat.class);
	private AntUtils antRunner; 
	
	/**
	 * fileName is build file path
	 * @param globusPath
	 * @param tomcatDir
	 */
	public DeployToTomcat(String globusPath, String tomcatDir) {
		globusLocation = new String(globusPath);
		globusLocation = globusLocation.replace('\\', File.separatorChar);
		globusLocation = globusLocation.replace('/', File.separatorChar);
		
		tomcatDirectory = new String(tomcatDir);
		tomcatDirectory = tomcatDirectory.replace('\\', File.separatorChar);
		tomcatDirectory = tomcatDirectory.replace('/', File.separatorChar);
		
		webappName = new String("wsrf");
		
		antRunner = new AntUtils();
	}
	
	/**
	 * Properties can be null. User can define any additional properties using
	 * the props argument
	 * Additional properties include,
	 * 		-Dwebapp.name=<name> can be specified to set the name of the web 
	 * 				application under which the installation will be deployed. 
	 * 				By default "wsrf" web application name is used.
	 * @param target
	 * @param props
	 * @return
	 */
	public void deployIntoTomcat(String target, Properties props) 
				throws WSCoreException {
		if (!checkDir(tomcatDirectory) && !checkDir(globusLocation)) {
			log.error("Check Tomcat directory/Globus Location exists");
			throw new WSCoreException("Check Tomcat directory/Globus Location exists");
		}		
		
		String fileName = new String(
				globusLocation.concat("/share/globus_wsrf_common/tomcat/tomcat.xml"));
		fileName = fileName.replace('\\', File.separatorChar);
		fileName = fileName.replace('/', File.separatorChar);
		log.info("Build File used in Tomcat: " + fileName);
		File buildFile = new File(fileName);
		
		if (props == null) {
			props = new Properties();
		}
				
		props.setProperty("deploy.dir", globusLocation);
		props.setProperty("tomcat.dir", tomcatDirectory);
		
		if (null != props.getProperty("webapp.name")) {
			webappName = props.getProperty("webapp.name");
			log.info("Web app Name : " + webappName);
		}
		
		try {
			antRunner.runAntTarget(buildFile, target, props);
		} catch (AntException ae) {
			log.error("Failed to deploy WSCore into Tomcat", ae);
			throw new WSCoreException("Failed to deploy WSCore into Tomcat", ae);
		}
			
		log.info("Java WS core successfully deployed into Tomcat");
	}
	
	/**
	 * Checks if the location exists and is a directory
	 * @param location
	 * @return
	 */
	private boolean checkDir(String location) {
		File dir = new File(location);
		
		if (!dir.exists()) {
			log.error("The directory " + location + " does not exist.");
			return false;
		}
		if (!dir.isDirectory()) {
			log.error("The given path " + location + " is not a directory.");
			return false;
		}
		
		return true;
		
	}
	
	/**
	 * This method is valid only for Tomcat version 4.1.x
	 * 
	 * XML file used is $<tomcat.dir>/conf/server.xml
	 * Adds the following node to the parent node <Service name="Standalone-Tomcat">
	 * <Connector
	 * 	className="org.apache.catalina.connector.http.HttpConnector"
	 * 	port="8443" minProcessors="5" maxProcessors="75"
	 * 	authenticate="true" secure="true" scheme="https"
	 * 	enableLookups="true" acceptCount="10" debug="0">
	 * 		<Factory
	 * 			className="org.globus.tomcat.catalina.net.HTTPSServerSocketFactory"
	 * 			proxy="/path/to/proxy/file"
	 * 			cert="/path/to/certificate/file"
	 * 			key="/path/to/private/key/file"
	 * 			cacertdir="/path/to/ca/certificates/directory"/>
	 * </Connector>
	 * All the attribute name/values should be defined by the Hashtables    
	 * @param xmlHelper
	 * @param connAttrs
	 * @return
	 * @throws FileNotFoundException
	 * @throws TransformerException
	 */
	public void addConnectorForTomcat4_1_x(EditXML xmlHelper, Hashtable connAttrs) 
				throws WSCoreException {
		Element parentElement = xmlHelper.getElementByTagNameAttrName("Service", "Tomcat-Standalone");
		if (parentElement == null) {
			log.error("The element 'Service' is not found. " +
					"Please check the server.xml file");
			throw new WSCoreException("The element 'Service' is not found. " +
					"Please check the server.xml file");			
		}
		
		Element connElement = xmlHelper.createElementWithAttrs("Connector", connAttrs);
		
		Hashtable factoryAttrs = new Hashtable();
		factoryAttrs.put("className", "org.globus.tomcat.catalina.net.HTTPSServerSocketFactory");
		Element factoryElement = xmlHelper.createElementWithAttrs("Factory", factoryAttrs);
		xmlHelper.addFirstChild(connElement, factoryElement);
		xmlHelper.addFirstChild(parentElement, connElement);
	}
	
	public void addConnectorForTomcat5_x(EditXML xmlHelper, 
			Hashtable connAttrs) throws WSCoreException {
		Element parentElement = 
			xmlHelper.getElementByTagNameAttrName("Service", "Catalina");
		if (parentElement == null) {
			log.error("The element 'Service' is not found. " +
					"Please check the server.xml file");
			throw new WSCoreException("The element 'Service' is not found. " +
					"Please check the server.xml file");
		}
		
		Element connElement = 
			xmlHelper.createElementWithAttrs("Connector", connAttrs);
		if (null == connElement) {
			log.error("The element 'Connector' cannot be created");
			throw new WSCoreException("The element 'Connector' cannot be created");
		}
		
		xmlHelper.addFirstChild(parentElement, connElement);		
	}	
	
	/**
	 * This method is valid for both Tomcat 4.1.x and 5.x
	 * 
	 * Edits the file $<tomcat.dir>/conf/server.xml to include the appropriate 
	 * connectors. 
	 * If default port (8443) is not used, then edits the file 
	 * $<tomcat.dir>/webapps/wsrf/WEB-INF/web.xml to include the non-default port.
	 * @param connectionAttrs
	 * @param valueAttrs
	 * @return
	 */
	public void editXMLFiles(String version, 
			Hashtable connectionAttrs, Hashtable valveAttrs) 
				throws WSCoreException {
		String fileName = new String(tomcatDirectory.concat("/conf/server.xml"));
		fileName = fileName.replace('\\', File.separatorChar);
		fileName = fileName.replace('/', File.separatorChar);
				
		EditXML xmlHelper;
		EditXML webXMLHelper;
		boolean defaultPort = true;
		Element engine = null;
		log.info("Entered editXMLFiles... with tomcat version : " + version);
		try {
			log.info("server.xml file location : " + fileName);
			xmlHelper = new EditXML(fileName);
			log.info("EditXML object created");
							
			if (version.startsWith("4.1")) {
				addConnectorForTomcat4_1_x(xmlHelper, connectionAttrs);
				engine = xmlHelper.getElementByTagNameAttrName("Engine", "Standalone");
			}
			else if (version.startsWith("5.")) {
				log.info("Before adding connector");
				addConnectorForTomcat5_x(xmlHelper, connectionAttrs);
				log.info("After adding connector");
				engine = xmlHelper.getElementByTagNameAttrName("Engine", "Catalina");
			}
			else {
				log.error("The version " + version + " is not supported");
				throw new WSCoreException("The version " + version + " is not supported");
			}			

			log.info("Before adding Valve");
			Element valve = xmlHelper.createElementWithAttrs("Valve", valveAttrs);
			xmlHelper.addFirstChild(engine, valve);
			log.info("After adding Valve");
			
			if (! ((String)connectionAttrs.get("port")).equals("8443")) {
				defaultPort = false; 
				String webXMLFile =	new String(tomcatDirectory.concat("/webapps/" + 
						webappName + "/WEB-INF/web.xml"));
				webXMLFile = webXMLFile.replace('/', File.separatorChar);
				webXMLFile = webXMLFile.replace('\\', File.separatorChar);
				webXMLHelper = new EditXML(webXMLFile);
				Element servlet = webXMLHelper.getElementByTagName("servlet");
				if (null == servlet) {
					log.error("Cannot find the node 'servlet' in the file " + 
							webXMLFile);
					throw new WSCoreException("Cannot find the node 'servlet' in the file " + 
							webXMLFile);
				}
				
				/*
				 * adding the following xml elements
				 * <init-param>
				 * 		<param-name>defaultProtocol</param-name>
				 * 		<param-value>https</param-value>
				 * </init-param>
				 */
				Element protocol = webXMLHelper.createElement("init-param", null);
				Element protocolName = webXMLHelper.createElement("param-name", "defaultProtocol");
				Element protocolValue = webXMLHelper.createElement("param-value", "https");
				webXMLHelper.addLastChild(protocol, protocolName);
				webXMLHelper.addLastChild(protocol, protocolValue);
				webXMLHelper.addBeforeLastChild(servlet, protocol);
				
				/*
				 * Adds the following elements to web.xml
				 * <init-param>
				 * 		<param-name>defaultPort</param-name>
				 * 		<param-value>443</param-value>
				 * </init-param>   
				 */
				Element portElement = webXMLHelper.createElement("init-param", null);
				Element portName = webXMLHelper.createElement("param-name", "defaultPort");
				Element portValue = webXMLHelper.createElement("param-value", (String)connectionAttrs.get("port"));
				webXMLHelper.addLastChild(portElement, portName);
				webXMLHelper.addLastChild(portElement, portValue);
				webXMLHelper.addBeforeLastChild(servlet, portElement);
				xmlHelper.commitChanges();
				if (!defaultPort) {
					webXMLHelper.commitChanges();
				}
			}
		} catch (ParserConfigurationException pce) {
			throw new WSCoreException(pce.toString());
		} catch (IOException ioe) {
			throw new WSCoreException(ioe.toString());
		} catch (SAXException se) {
			throw new WSCoreException(se.toString());
		} catch (TransformerException te) {
			throw new WSCoreException(te.toString());	
		}
		/*finally {
			try {
				xmlHelper.commitChanges();
				if (!defaultPort) {
					webXMLHelper.commitChanges();
				}				
			}catch (FileNotFoundException fnf) {
				log.error(fnf);
		
			}catch (TransformerException te) {
				log.error(te);
		
			}			
		}*/
		
	}
		
	public void undeployFrmTomcat(String version, String webAppName) 
				throws WSCoreException {
		String dirName = tomcatDirectory + File.separatorChar + 
				"webapps" + File.separatorChar + webAppName;
		
		File dir = new File(dirName);
		if (!dir.exists()) {
			log.error("The directory " + dirName + " does not exist.");
			log.error("The webapp name " + webAppName + " is wrong or " + 
					"it is not deployed");
			throw new WSCoreException("The webapp name " + webAppName +
					" is wrong or it is not deployed");
		}
		
		Runtime rt = Runtime.getRuntime();
		try {
			log.debug("Deleting the web application " +	webAppName + " from Tomcat...");
			//Stop Tomcat
			String envp[] = {"JAVA_HOME="+System.getProperty("java.home")};
			log.info("Shutting down Tomcat...");
			rt.exec(tomcatDirectory+File.separatorChar+"bin"+File.separatorChar+"shutdown.sh", envp);
			DiskUtils.forceDelete(dir);
			
			// clean xml files
			cleanXMLFiles(version);
			rt.exec(tomcatDirectory+File.separatorChar+"bin"+File.separatorChar+"startup.sh", envp);
			log.info("Started Tomcat....");
		} catch(IOException ioe) {
			log.error("Error in deleting directory " + dirName);
			log.error("Failed to undeploy " + webAppName);
			throw new WSCoreException(ioe.toString());
		} 
		log.info("Successfully undeployed " + webAppName);
	}
	
	public void cleanXMLFiles(String version) throws WSCoreException {
		String serverxml = new String(tomcatDirectory.concat("/conf/server.xml"));
		serverxml = serverxml.replace('\\', File.separatorChar);
		serverxml = serverxml.replace('/', File.separatorChar);

		EditXML xmlHelper = null;
		Element connector = null;
		Element valve = null;
		try {
			xmlHelper = new EditXML(serverxml);
			
			if (version.startsWith("4.1")) {
				connector = xmlHelper.getElementByTagNameAttrName("Connector", "className",
						"org.apache.catalina.connector.http.HttpConnector");
				xmlHelper.removeNode(connector);
				
				valve = xmlHelper.getElementByTagNameAttrName("Valve", "className",
				"org.globus.tomcat.catalina.valves.HTTPSValve");
				xmlHelper.removeNode(valve);								
			}
			else if (version.startsWith("5.")) {
				connector = xmlHelper.getElementByTagNameAttrName("Connector", 
						"className", "org.globus.tomcat.coyote.net.HTTPSConnector");
				xmlHelper.removeNode(connector);
							
				valve = xmlHelper.getElementByTagNameAttrName("Valve", "className",
						"org.globus.tomcat.coyote.valves.HTTPSValve");
				xmlHelper.removeNode(valve);
			}
			else {
				log.error("The version " + version + " is not supported");
				throw new WSCoreException("The version " + version + 
						" is not supported");
			}			
		} catch (ParserConfigurationException pce) {
			throw new WSCoreException(pce.toString());
		} catch (IOException ioe) {
			throw new WSCoreException(ioe.toString());
		} catch (SAXException se) {
			throw new WSCoreException(se.toString());
		} finally {
			try {
				xmlHelper.commitChanges();								
			}catch (FileNotFoundException fnf) {
				log.error(fnf);	
				throw new WSCoreException(fnf);
			}catch (TransformerException te) {
				log.error(te);				
				throw new WSCoreException(te);
			}	
		}
	}
	
	public static void main(String args[]) {
		DeployToTomcat ws = new DeployToTomcat("/home/sandya/globus401bin", "/opt/jakarta-tomcat-5.0.28");
		Properties p = new Properties();
		try {
			p.setProperty("webapp.name", "Testing");
			ws.deployIntoTomcat("deployTomcat", p);
						
			Hashtable conAttrs = new Hashtable();
			conAttrs.put("className", "org.globus.tomcat.coyote.net.HTTPSConnector");
			conAttrs.put("port", "8443");
			conAttrs.put("maxThreads", "150");
			conAttrs.put("minSpareThreads", "25");
			conAttrs.put("autoFlush", "true");
			conAttrs.put("disableUploadTimeout", "true");
			conAttrs.put("scheme", "https");
			conAttrs.put("enableLookups", "true");
			conAttrs.put("acceptCount", "10");
			conAttrs.put("debug", "0");
			
			Hashtable valve = new Hashtable();
			valve.put("className", "org.globus.tomcat.coyote.valves.HTTPSValve");
			
			ws.editXMLFiles("5.0.28", conAttrs, valve);
			
			UndeployFrmTomcat undeploy = new UndeployFrmTomcat("/opt/jakarta-tomcat-5.0.28", "5.0.28");
			undeploy.undeployFrmTomcat("Testing");			
		} catch (WSCoreException tce) {
			log.error("Tomcat Error : " + tce);			
		}
	}
}