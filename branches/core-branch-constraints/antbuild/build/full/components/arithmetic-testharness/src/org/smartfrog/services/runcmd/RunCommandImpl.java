/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.runcmd;

import org.smartfrog.services.display.PrintErrMsgInt;
import org.smartfrog.services.display.PrintMsgInt;
import org.smartfrog.services.utils.generic.OutputStreamIntf;
import org.smartfrog.services.utils.generic.StreamGobbler;
import org.smartfrog.services.utils.generic.StreamIntf;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.logging.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.smartfrog.tools.testharness.NotifyOutputFilterPrimImpl;
import org.smartfrog.services.os.runshell.RunShellImpl;


/**
 * This class implements the Compound interface because it can "contain" Virtual Hosts components. The Apache interface
 * is the Remoteable interface and the Runnable interface is used to monitor the httpd process. The httpd process is
 * started in sfStart by setting the apacheState variable to true and ended in sfTerminate by setting the apacheState
 * variable to false. The Internet Activator scripts are used to edit the httpd.conf file. These rely on certain
 * environment variables being set, these variables are defined in the sf file and are passed to the
 * common.executeScript() method. Adding them to the sf file avoids the need to hard code these paramters. The scripts
 * are downloaded from a webserver and are then saved locally.
 */
public class RunCommandImpl extends RunShellImpl implements Runnable {

    /**
     * Constructor.
     *
     * @throws RemoteException In case of network/rmi error
     */
    public RunCommandImpl() throws RemoteException {
    }

    /**
     * Reads SF description = initial configuration. Override this to read/set properties before we read ours, but
     * remember to call the superclass afterwards
     */
    protected void readSFAttributes() throws SmartFrogException, RemoteException {
        super.readSFAttributes();

        setOutputStreamObj((NotifyOutputFilterPrimImpl) sfResolve(varOutputStreamTo, getOutputStreamObj(), false));
    }
}
