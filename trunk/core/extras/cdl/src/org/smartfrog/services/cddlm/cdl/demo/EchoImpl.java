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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.services.cddlm.cdl.cmp.CmpComponentImpl;
import org.smartfrog.services.cddlm.cdl.demo.Echo;
import org.smartfrog.services.xml.utils.XsdUtils;

import javax.xml.namespace.QName;
import javax.swing.*;
import java.rmi.RemoteException;

/**
 * created 23-Jun-2005 17:52:13
 */

public class EchoImpl extends CmpComponentImpl implements Echo {

    public static QName QNAME_MESSAGE = new QName(Echo.DEMO_NAMESPACE, Echo.ATTR_MESSAGE);
    public static QName QNAME_GUI = new QName(Echo.DEMO_NAMESPACE, Echo.ATTR_GUI);

    public EchoImpl() throws RemoteException {
    }

    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        String message = resolveText(QNAME_MESSAGE, true);
        String gui = resolveText(QNAME_GUI, true);
        boolean showGui = XsdUtils.isXsdBooleanTrue(gui);
        if (showGui) {
            JOptionPane.showMessageDialog(null, message);
        } else {
            System.out.println("Message: " + message);
        }
    }
}
