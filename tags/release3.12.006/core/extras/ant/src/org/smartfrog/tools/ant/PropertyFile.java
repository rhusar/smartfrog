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
package org.smartfrog.tools.ant;

import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.taskdefs.Java;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Enumeration;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * A type that holds property files
 * created Jul 26, 2004 1:01:50 PM
 */

public class PropertyFile  extends DataType  implements Cloneable {

    /**
     * filename
     */
    private File file;

    /**
     * optional flag
     */
    private boolean optional=false;
    public static final String ERROR_NO_FILE_ATTRIBUTE = "No file specified";
    public static final String ERROR_FILE_LOAD_FAILED = "Failed to load ";
    public static final String ERROR_FILE_NOT_FOUND = "File not found: ";
    public static final String MESSAGE_ABSENT_FILE = "Skipped absent (optional) file ";
    public static final String MESSAGE_LOADING_FILE = "Loading property file";

    public void setFile(File file) {
        this.file = file;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    /**
     * load the file of properties and return it as a properties object
     * @return a loaded properties object (which will be empty for no file load on an optional file)
     * @throws BuildException if there was no file specified, or something went wrong with the load
     */
    public Properties getProperties() {
        Properties props = new Properties();
        if(file==null) {
            throw new BuildException(ERROR_NO_FILE_ATTRIBUTE);
        }
        if(!file.exists()) {
            if(optional) {
                getProject().log(MESSAGE_ABSENT_FILE+file,Project.MSG_VERBOSE);
                return props;
            } else {
                throw new BuildException(ERROR_FILE_NOT_FOUND+file);
            }
        }
        getProject().log(MESSAGE_LOADING_FILE + file, Project.MSG_VERBOSE);
        BufferedInputStream inStream = null;
        try {
            inStream=new BufferedInputStream(new FileInputStream(file));
            props.load(inStream);
        } catch (IOException e) {
            throw new BuildException(ERROR_FILE_LOAD_FAILED+file,e);
        } finally {
            FileUtils.close(inStream);
        }
        return props;
    }

    /**
     * Creates and returns a copy of this object.
     * Since Ant1.7 this has been required to be public
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * add the declared properties to the JVM
     * @param dest recipient of the system properties
     */
    public void addPropertiesToJvm(SysPropertyAdder dest) {
        Properties props=getProperties();
        Enumeration<?> en=props.propertyNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            String value=props.getProperty(name);
            Environment.Variable sysProp=new Environment.Variable();
            getProject().log("Setting property "+name+"="+value, Project.MSG_DEBUG);
            sysProp.setKey(name);
            sysProp.setValue(value);
            dest.addSysproperty(sysProp);
        }
    }

    /**
     * a list class for this class
     */
    public static class PropertyFileList {
        /**
         * internal cache
         */
        private List<PropertyFile> list=new LinkedList<PropertyFile>();

        /**
         * add to the list
         * @param propFile the property file
         */
        public void add(PropertyFile propFile) {
            list.add(propFile);
        }

        /**
         * add all properties to the list
         * @param jvm JVM to add to
         */
        public void addPropertiesToJvm(SysPropertyAdder jvm) {
            for (PropertyFile propertyFile : list) {
                propertyFile.addPropertiesToJvm(jvm);
            }
        }

        /**
         * get an iterator.
         * @return the iterator
         */
        public ListIterator<PropertyFile> iterator() {
            return list.listIterator();
        }

    }
}
