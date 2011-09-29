/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jul 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.exec.ant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AntUtils {
	private Project project;
	private BuildLogger logger;
	
	private static Log log = LogFactory.getLog(AntUtils.class);
	
	/**
	 * 
	 */
	public AntUtils() {
		project = new Project();

        logger = new DefaultLogger();
        logger.setMessageOutputLevel(Project.MSG_INFO);
        logger.setOutputPrintStream(System.out);
        logger.setErrorPrintStream(System.err);
        logger.setEmacsMode(false);
        
        project.addBuildListener(logger); 		
	}
	
	private void setUserProperties(Properties properties) {
		Enumeration e = properties.keys();
        while (e.hasMoreElements()) {
                String userProperty = (String) e.nextElement();
                String value = (String) properties.get(userProperty);
                project.setUserProperty(userProperty, value);
        }
	}
	
	public static String getAntVersion() {
		InputStream in = null;
		String antVersion = null;

		try {
			in = Project.class
					.getResourceAsStream("/org/apache/tools/ant/version.txt");
			if (in == null)
				throw new IOException("version.txt missing from ant.jar");

			Properties props = new Properties();
			props.load(in);
			/*
			 * StringBuffer ver = new StringBuffer("Apache Ant version ")
			 * .append(props.getProperty("VERSION")).append(" compiled on ")
			 * .append(props.getProperty("DATE"));
			 */
			StringBuffer ver = new StringBuffer(props.getProperty("VERSION"));
			antVersion = ver.toString();
		} catch (IOException ex) {
			antVersion = "unknown (" + ex.getMessage() + ")";
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException ignore) {
				}
		}

		return antVersion;
	}
	
	public void runAntTarget(File buildFile, String target, 
			Properties userProperties) throws AntException {
		Throwable error = null;
			
		if (!buildFile.exists()) {
			log.error("Build file " + buildFile.getAbsolutePath() + 
					" does not exist.");
			throw new AntException("Build file " + buildFile.getAbsolutePath() + 
					" does not exist.");
		}
		if (!buildFile.isFile()) {
			log.error("Build file " + buildFile.getAbsolutePath() + 
					" is not a file.");
			throw new AntException("Build file " + buildFile.getAbsolutePath() + 
			" is not a file.");
		}
		
		setUserProperties(userProperties);
        try {
        	project.fireBuildStarted();
            project.init();
            ProjectHelper projHelper = ProjectHelper.getProjectHelper();
            project.setUserProperty("ant.file", buildFile.getAbsolutePath());
            project.setKeepGoingMode(false);
            project.addReference("ant.projectHelper", projHelper);
            projHelper.parse(project, buildFile);
            project.executeTarget(target);                        
        } catch (BuildException be) {
        	error = be;        	
        }
        finally {
        	project.fireBuildFinished(error);
        	if (error != null) {
        		log.error("ANT BUILD FAILED : ", error);
        		throw new AntException("ANT BUILD FAILED", error);
        	}
        }

        if (error != null) {
        	log.info("Build Failed");
        	throw new AntException("Build Failed");        	
        }
        else {
        	log.info("Build Successful");        	
        }        	
	}       
}