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
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.Reference;

/**
 * This is a condition that tests for things being deployed.
 *
 * TODO:implement this
 * created 14-May-2004 11:28:52
 */

public class IsDeployed extends ProjectComponent implements Condition {


    /**
     * our security holder
     */
    private SecurityHolder securityHolder = new SecurityHolder();

    /**
     * name of host
     */
    private String host;

    /**
     * the name of the application
     */
    private String application;

    /**
     * SmartFrog daemon connection port.
     * org.smartfrog.ProcessCompound.sfRootLocatorPort=3800;
     */
    protected Integer port;


    private void validate() throws BuildException {
        if (application == null) {
            throw new BuildException("application is undefined");
        }
    }

    /**
     * Is the application deployed
     *
     * @return true if the condition is true
     * @throws org.apache.tools.ant.BuildException
     *          if an error occurs
     */
    public boolean eval() throws BuildException {
        //TODO: implement
        //plan is to use sfResolve, once we have a reference to a remote daemon.
        //all network exceptions should be caught and turned into simple failures.
        throw new BuildException("Not Implemented");
        //return false;
    }


    /**
     * set a reference to the security types
     *
     * @param securityRef security data
     */
    public void setSecurityRef(Reference securityRef) {
        securityHolder.setSecurityRef(securityRef);
    }

    /**
     * set a security definition
     *
     * @param security security data
     */
    public void addSecurity(Security security) {
        securityHolder.addSecurity(security);
    }

    /**
     * set the hostname to deploy to (optional, defaults to localhost)
     * Some tasks do not allow this to be set at all.
     *
     * @param host hostname
     */
    public void setHost(String host) {
        log("setting host to " + host, Project.MSG_DEBUG);
        this.host = host;
    }

    /**
     * port of daemon; optional -default is 3800
     * Some tasks do not allow this to be set at all.
     *
     * @param port port to use
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * set the name of an application to look for
     *
     * @param application application to look for
     */
    public void setApplication(String application) {
        this.application = application;
    }


}
