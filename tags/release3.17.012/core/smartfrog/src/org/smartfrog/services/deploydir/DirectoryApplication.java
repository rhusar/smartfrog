/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.deploydir;

import org.smartfrog.services.filesystem.FileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * Created 11-Mar-2008 17:19:15
 */

public class DirectoryApplication implements Serializable {

    private File propertyFile;
    private File directory;
    private String name;
    private boolean enabled;
    private String resource;
    private boolean resourceExists;
    private Properties propertySet;
    private static final String TRUE = "true";
    public static final String APPLICATION_XML = "application.xml";
    public static final String APPLICATION_PROPERTIES = "application.properties";

    public DirectoryApplication(File directory, String name) throws IOException {
        this.directory = directory;
        this.name = name;
        File file = new File(directory, APPLICATION_XML);
        if (!file.exists()) {
            file = new File(directory, APPLICATION_PROPERTIES);
        }
        load(file);
    }

    public DirectoryApplication() {
    }

    public File getDirectory() {
        return directory;
    }

    public File getPropertyFile() {
        return propertyFile;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Properties getPropertySet() {
        return propertySet;
    }

    public String getName() {
        return name;
    }

    public void load(File file) throws IOException {
        InputStream in = null;
        propertyFile = file;
        Properties props = new Properties();
        try {
            in = new FileInputStream(file);
            if (file.getName().endsWith(".xml")) {
                props.loadFromXML(in);
            } else {
                props.load(in);
            }
            build(props);
        } finally {
            FileSystem.close(in);
        }
    }

    public String getProperty(String key, String defVal) {
        return propertySet.getProperty(key, defVal);
    }

    public boolean build(Properties props) {
        propertySet = props;
        enabled = TRUE.equals(getProperty("application.enabled", TRUE));
        resourceExists = false;
        resource = getProperty("application.resource", null);
        if (resource != null) {
            InputStream resStream = getClass().getClassLoader().getResourceAsStream(resource);
            if (resStream != null) {
                resourceExists = true;
                FileSystem.close(resStream);
            } else {
                resourceExists = false;
            }
        }
        return resourceExists && enabled;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Directory Application ").append(name);
        buffer.append("\n From ").append(directory);
        buffer.append("\n URL ").append(getResource());
        buffer.append("\n");
        return buffer.toString();
    }
}
