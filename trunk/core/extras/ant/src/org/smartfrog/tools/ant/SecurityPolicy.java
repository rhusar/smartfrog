/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.JavaResource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;

import java.io.File;
import java.io.IOException;

/**
 * This is an element to contain security policy.
 *
 * <p/>
 * It supports one and only one resource, which can also be set by a
 * file or refid attribute.
 * <p/>
 * Created 19-Oct-2007 16:02:28
 */
public class SecurityPolicy {

    /**
     * Our resource
     */
    protected Resource resource;


    /**
     * the security file
     */
    protected File tempFile;

    /**
     * Error string.
     * <p>
     * {@value}
     */
    public static final String ERROR_DUPLICATE_RESOURCES = "Cannot define duplicate resources in a security policy";

    /**
     * resource containing the security policy to use if none is otherwise given
     * <p>
     * {@value}
     */
    public static final String DEFAULT_SECURITY_POLICY = "/org/smartfrog/tools/ant/sf.no.security.policy";


    public SecurityPolicy() {
    }

    /**
     * Set a resource by refid.
     * This creates a new resource
     * @param refid the id of the existing reference
     */
    public void setResourceRef(Reference refid) {
        resource=new Resource();
        resource.setRefid(refid);
    }

    /**
     * Add a resource
     * @param newResource the resource
     * @throws BuildException if a resource is already defined
     */
    public void addResource(Resource newResource) {
        if(resource!=null) {
            throw new BuildException(ERROR_DUPLICATE_RESOURCES);
        }
        resource = newResource;
    }

    public void setFile(File file) {
        FileResource fr=new FileResource(file);
        addResource(fr);
    }

    /**
     * After a run, clean up
     */
    public synchronized void cleanup() {
        if (tempFile != null) {
            tempFile.delete();
            tempFile = null;
        }
    }

    public void applySecurityPolicy(ProjectComponent owner,Java process) {
        try {
            if(resource==null) {
                //always apply a default policy
                resource=new JavaResource(DEFAULT_SECURITY_POLICY,null);
            }
            //set the owner for any resource created in this class
            if(resource.getProject()==null) {
                resource.setProject(owner.getProject());
            }
            //existing files get used
            File securityFile;
            if(resource instanceof FileResource) {
                //its already a file
                FileResource fr=(FileResource) resource;
                securityFile=fr.getFile();
            } else {
                //create a temp resource
                ResourceHelper rh=new ResourceHelper(owner);
                tempFile= File.createTempFile("sfsecuritypolicy","txt");
                rh.saveResourceToFile(resource,tempFile);
                securityFile=tempFile;
            }
            Environment.Variable settings = new Environment.Variable();
            settings.setFile(securityFile);
            settings.setKey("java.security.policy=");
            process.addSysproperty(settings);
            owner.log("Security policy file: "+securityFile, Project.MSG_VERBOSE);
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }


    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    public String toString() {
        if(resource!=null) {
            return "Security Policy in "+resource.toString();
        } else {
            return "Undefined Security Policy";
        }
    }
}
