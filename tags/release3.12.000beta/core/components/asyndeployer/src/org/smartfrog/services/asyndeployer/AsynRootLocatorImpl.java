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

package org.smartfrog.services.asyndeployer;

import java.net.InetAddress;

import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SmartFrogException;

import org.smartfrog.sfcore.processcompound.DefaultRootLocatorImpl;
import org.smartfrog.sfcore.processcompound.RootLocator;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;

/**
 * Defines a default root locator for SmartFrog Processes. The root locator
 * knows how to set a process compound to be the root of a host, as well as
 * the method on how to get the root process compound on a given host and
 * port. This implementation uses the rmi registry to set the root process
 * compound in. Root Locators should not allow multiple process compounds to
 * set themselves as root.
 *
 */
public class AsynRootLocatorImpl extends DefaultRootLocatorImpl implements RootLocator, MessageKeys {


    /**
     * Constructs the DefaultRootLocatorImpl object.
     */
    public AsynRootLocatorImpl() {
    }

/**
     * Method that bind/unbinds to the "directory service" used by the locator. This method is called asynchronously by the locator.
     * Overwrite point for other locators.
     * @param pc ProcessCompound that will be registered/unregistered in the directory service
     * @param bind boolean to determine if it should register or unregister?
     * @param ex If any exception is thown during bind/unbind then it shold be stored here.
     */
protected void bindAction(ProcessCompound pc, boolean bind, SmartFrogException ex) {

    try {

        System.out.println("pc.sfContext : " + pc.sfContext());


        System.out.println("Binding   " + pc.getClass().getName() + "  is done : ------>" + pc.sfCompleteName() + pc.sfContext());
        if (bind) {
            //registry.bind(defaultName, pc);

            String defaultName1 = "//" + InetAddress.getLocalHost().toString() + "/" + defaultName;

            //  ProActive.register(pc_temp, defaultName);

            //  ProActive.register(pc, defaultName1);
        } else {
            //Unbind
            //registry.unbind(defaultName);
            ProActive.unregister(defaultName);
        }
    } catch (Exception e) {
        // to be thrown in getProcessCompound
        //String msg = "unbinding";
        // if (bind) {msg = "binding";}
        // ex = SmartFrogRuntimeException.forward("Exception while "+msg    + "root ProcessCompound", e);
        e.printStackTrace();
    }

}

    /**
     * Gets the root process compound for a given host on a specified port. If
     * the passed host is null the root process compound for the local host is
     * looked up. If the passed port number is negative (ex.-1) the default port number (3800)
     *  is  used. . Checks if the local process compound is equal to the
     * requested one, and returns the local object instead of the stub to
     * avoid all calls going through RMI
     *
     * @param hostAddress host to look up root process compound
     * @param portNum port to locate registry for root process conmpound if not
     *        default
     *
     * @return the root process compound on given host
     *
     * @throws Exception error locating root process compound on host
     *
     * @see #setRootProcessCompound
     */
    public ProcessCompound getRootProcessCompound(InetAddress hostAddress, int portNum) throws Exception {

		ProcessCompound localCompound = SFProcess.getProcessCompound();

	   /* if (hostAddress == null) {
			hostAddress = InetAddress.getLocalHost();
		}*/

		if ((localCompound != null)&& hostAddress.equals(InetAddress.getLocalHost()) && localCompound.sfIsRoot()) {
			return localCompound;
		}

		if (portNum <= -1){
			portNum = getRegistryPort(localCompound);
		}

		//Registry reg = SFSecurity.getRegistry(hostAddress.getHostAddress(), portNum);

		//ProcessCompound pc = (ProcessCompound) reg.lookup(defaultName);
		String lookupName = "//"+ hostAddress.getHostName()+"/"+defaultName;

		VirtualNode rVirNode= ProActive.lookupVirtualNode("//" + hostAddress.getHostName() + "/RootNode");
		Node rNode = rVirNode.getNode();
		Object[] rObj=rNode.getActiveObjects();
		ProcessCompound pc = (ProcessCompound)rObj[0];

	    //ProcessCompoundImpl.class.getName()
	    // ProcessCompound pc = (ProcessCompound)ProActive.lookupActive(ProcessCompoundImpl.class.getName() ,  lookupName);
		System.out.println("Lookup Done  for "+lookupName + "  is : ------>" + pc.sfCompleteName());
        

        return pc;

	}


}
