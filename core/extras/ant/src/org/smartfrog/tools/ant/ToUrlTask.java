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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.io.File;
import java.net.MalformedURLException;

/**
 * This task takes a file and turns it into a URL, which it then assigns
 * to a property. This is a way of getting file: URLs into an inline
 * SmartFrog deployment descriptor.
 */

public class ToUrlTask extends Task {

    private String property;

    private File file;

    public void setProperty(String property) {
        this.property = property;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Create the url
     *
     * @throws org.apache.tools.ant.BuildException
     *          if something goes wrong with the build
     */
    public void execute() throws BuildException {
        //validation
        if (property == null) {
            throw new BuildException("No property defined");
        }
        if (file == null) {
            throw new BuildException("No file defined");
        }
        //now exit here if the property is already set
        if (getProject().getProperty(property) != null) {
            return;
        }
        try {
            //create the URL
            String url = file.toURI().toURL().toExternalForm();
            //set the property
            log("Setting "+property+" to URL "+url,Project.MSG_VERBOSE);
            getProject().setNewProperty(property, url);
        } catch (MalformedURLException e) {
            throw new BuildException("Could not convert " + file, e);
        }
    }
}
