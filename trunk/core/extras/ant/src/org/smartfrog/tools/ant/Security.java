/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Assertions;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;

import java.io.File;

/**
 * This is a datatype for the smartfrog tasks, one that
 * takes security settings
 *         Date: 19-Apr-2004
 *         Time: 16:46:05
 */
public class Security extends DataType {

    private File keystore;
    private File passfile;
    private String password;

    public File getKeystore() {
        return keystore;
    }

    public void setKeystore(File keystore) {
        this.keystore = keystore;
    }

    public File getPassfile() {
        return passfile;
    }

    public void setPassfile(File passfile) {
        this.passfile = passfile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * take a reference in a project and resolve it.
     * @param project
     * @param reference
     * @return the security object we were referrring to. 
     * @throws BuildException if the reference is to an unsupported type.
     */
    public static Security resolveReference(Project project,Reference reference) {
        assert project!=null;
        assert reference!=null;
        Object o = reference.getReferencedObject(project);
        if (!(o instanceof Security)) {
            throw new BuildException("reference is of wrong type");
        }
        return (Security) o;
    }
}
