/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cddlm.cdl.demo;

import org.smartfrog.services.cddlm.cdl.cmp.CmpCompoundImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;

import javax.swing.*;
import javax.xml.namespace.QName;
import java.awt.*;
import java.rmi.RemoteException;

/**
 * created 23-Jun-2005 17:52:13
 */

public class EchoImpl extends CmpCompoundImpl implements Echo, Runnable {

    private Log log;
    public static QName QNAME_MESSAGE = new QName(Echo.DEMO_NAMESPACE, Echo.ATTR_MESSAGE);
    public static QName QNAME_GUI = new QName(Echo.DEMO_NAMESPACE, Echo.ATTR_GUI);
    private boolean showGui;
    private String message;
    private Thread thread;
    private boolean abnormalTermination=false;
    private ComponentHelper helper;
    private Reference name;

    public EchoImpl() throws RemoteException {
    }


    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
    }

    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        log=sfGetApplicationLog();
        helper = new ComponentHelper(this);
        name = sfCompleteName();
        message = (String) resolve(QNAME_MESSAGE, true);
        showGui =sfResolve(new Reference(QNAME_GUI),false,false);
        if(showGui && GraphicsEnvironment.isHeadless()) {
            //on a headless server, downgrade the event
            log.warn("The echo is meant to be by the GUI, but we are a headless system");
            showGui=false;
        }

        thread = new Thread(this);
        thread.start();
    }


    /**
     * Performs the compound termination behaviour. Based on sfSyncTerminate
     * flag this gets forwarded to sfSyncTerminate or sfASyncTerminateWith
     * method. Terminates children before self.
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if(thread.isAlive()) {
            thread.interrupt();
        }
    }

    /**
     * Implements ping for a compound. A compound extends prim functionality by
     * pinging each of its children, any failure to do so will call
     * sfLivenessFailure with the compound as source and the errored child as
     * target. The exception that ocurred is also passed in. This check is
     * only done if the source is non-null and if the source is the parent (if
     * parent exists). If there is no parent and the source is non-null the
     * check is still done.
     *
     * @param source source of ping
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *          liveness failed
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if(abnormalTermination) {
            throw new SmartFrogLivenessException(getTerminationString());
        }
    }

    private String getTerminationString() {
        return "ABEND:"+ message;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
        log.info(message);
        boolean normal = true;
        if (showGui) {
            Object[] options = {"End Normally", "End Abnormally"};
            int normalButton=0;
            int result = JOptionPane.showConfirmDialog(null,
                    message,
                    "Terminate Echo Component Normally",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
            normal = result == normalButton;
        }
        //create a termination record
        TerminationRecord record;
        if(normal) {
            record=TerminationRecord.normal(name);
        } else {
            record= TerminationRecord.abnormal(getTerminationString(), name);
        }
        try {
            sfReplaceAttribute(ATTR_END_NORMAL,normal);
        } catch (SmartFrogRuntimeException ignored) {
            log.info("ignoring",ignored);
        } catch (RemoteException ignored) {
            log.info("ignoring", ignored);
        }
        sfTerminate(record);
    }
}
