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

import org.apache.tools.ant.BuildException;

import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * This class is used as an extended base for those tasks that do deployment, as it supports
 * declaring of applications as nested elements.
 * @author steve loughran
 *         created 27-Feb-2004 15:27:47
 */

public abstract class DeployingTaskBase extends SmartFrogTask {


    /**
     * list of applications
     */
    protected List applications=new LinkedList();

    /**
     * add a new application to the list.
     * @param application
     */
    public void addApplication(Application application) {
        applications.add(application);
    }

    protected int getApplicationCount() {
        return applications.size();
    }

    /**
     * test for apps existing
     * @throws BuildException if the count is zero
     */
    protected void checkApplicationsDeclared() {
        if(getApplicationCount()==0) {
            throw new BuildException("No applications declared");
        }
    }

    /**
     * deploy the applications listed by creating a -n app descriptor list
     * on the command line
     */
    public void deployApplications() {
        Iterator it=applications.iterator();
        while (it.hasNext()) {
            Application application = (Application) it.next();
            application.validate();
            addArg("-n");
            addArg(application.getName());
            addArg(application.getDescriptor());
        }
    }

    /**
     * the name and url of an application.
     * Interpretation of descriptor is by smartfrog; it includes resources as well
     * as codebase urls
     */
    public static class Application {

        /**
         * name of app
         */
        private String name;

        /**
         * descriptor: File, url, resource
         */
        private String descriptor;

        /**
         * name of the app
         * @param name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * location of the app's descriptor
         * @param descriptor
         */
        public void setDescriptor(String descriptor) {
            this.descriptor = descriptor;
        }

        /**
         * set the file of the app. This is the same as the descriptor, except
         * that it must exist
         * @param file
         */
        public void setFile(File file) {
            if(!file.exists()) {
                throw new BuildException("File "+file+" does not exist");
            }
            descriptor=file.toString();
        }

        public String getName() {
            return name;
        }

        public String getDescriptor() {
            return descriptor;
        }

        public void validate() {
            if(name==null) {
                throw new BuildException("no application name");
            }

            if(descriptor==null) {
                throw new BuildException("no descriptor provided for "+name);
            }
        }
    }

}
