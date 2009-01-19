/* (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.os.java;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.filesystem.FileUsingComponentImpl;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ListUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * created 28-Feb-2006 13:50:52
 */

public class LoadPropertyFileImpl extends FileUsingComponentImpl implements LoadPropertyFile {
    public static final String ERROR_NO_PROPFILE = "No property file or resource specified";
    public static final String ERROR_TOO_MANY_ATTRIBUTES = "Both " + ATTR_FILENAME + " and " + ATTR_RESOURCE
            + "are set; only one is allowed";

    public LoadPropertyFileImpl() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException  failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        bindWithDir(false, null);
        ComponentHelper helper = new ComponentHelper(this);
        String resource = sfResolve(ATTR_RESOURCE, (String) null, false);
        boolean required = sfResolve(ATTR_REQUIRED, true, false);
        Properties properties = new Properties();
        //validation
        if (resource != null) {
            if (getFile() != null) {
                throw new SmartFrogDeploymentException(ERROR_TOO_MANY_ATTRIBUTES);
            }
        } else {
            if (getFile() == null) {
                throw new SmartFrogDeploymentException(ERROR_NO_PROPFILE);
            }
        }
        InputStream in = null;
        if (resource != null) {
            try {
                in = helper.loadResource(resource);
            } catch (SmartFrogException sfe) {
                if (required) {
                    throw sfe;
                }
            }
        } else {
            try {
                in = new BufferedInputStream(new FileInputStream(getFile()));
            } catch (FileNotFoundException ioe) {
                if (required) {
                    throw SmartFrogDeploymentException.forward(ioe);
                }
            }
        }
        //at this point, in is null or set to an input stream
        if (in != null) {
            try {
                properties.load(in);
            } catch (IOException ioe) {
                if (required) {
                    throw SmartFrogDeploymentException.forward(ioe);
                }
            } finally {
                FileSystem.close(in);
            }
        }
        //at this point, properties is either an empty list or one containing loaded properties.
        //now its time to write it out
        Vector<Vector<String>> tuples = ListUtils.propertiesToTuples(properties);
        for(Vector<String> tuple:tuples) {
            sfReplaceAttribute(ListUtils.left(tuple), ListUtils.right(tuple));
        }
        sfReplaceAttribute(ATTR_PROPERTIES, tuples);
        helper.sfSelfDetachAndOrTerminate(TerminationRecord.NORMAL,
                "PropertyFile",
                null,
                null);

    }
}
