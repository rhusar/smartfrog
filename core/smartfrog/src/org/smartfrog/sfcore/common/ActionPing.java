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
package org.smartfrog.sfcore.common;

import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;
import java.io.Serializable;

/**
 * Ping a component
 */
public class ActionPing extends ConfigurationAction implements Serializable {
    /**
     * ping message. {@value}
     */
    public static final String PING_MESSAGE = "Ping time :";

    /**
     * this has to be implemented by subclasses; execute a configuration command
     * against a specified target
     *
     * @param targetP   target where to execute the configuration command
     * @param configuration   configuration command to be executed
     * @return Object Reference to parsed component
     * @throws SmartFrogException  failure in some part of the process
     * @throws RemoteException    In case of network/rmi error
     */
    public Object execute(ProcessCompound targetP,
                          ConfigurationDescriptor configuration)
            throws SmartFrogException,
            RemoteException {
        try {
            if (targetP == null) {
                targetP = SFProcess.sfSelectTargetProcess(configuration.getHost(),
                        configuration.getSubProcess());
            }
            long time=ping(configuration.getName(), targetP);
            configuration.setSuccessfulResult();
            configuration.setResult(ConfigurationDescriptor.Result.SUCCESSFUL,
                    PING_MESSAGE+(time/1000.0)+" seconds",
                    null);
            return new Long(time);
        } catch (SmartFrogException sex) {
            configuration.setResult(ConfigurationDescriptor.Result.FAILED,
                    null,
                    sex);
            throw sex;
        } catch (RemoteException rex) {
            configuration.setResult(ConfigurationDescriptor.Result.FAILED,
                    null,
                    rex);
            throw rex;
        }

    }

    /**
     * Ping a component
     * @param name name of component; if null, we assume the processcompound
     *  iself is the target
     * @param targetP process to resolve against
     * @return long time (in milliseconds) the ping took.
     * @throws SmartFrogException  failure in some part of the process
     * @throws RemoteException    In case of network/rmi error
     */
    private long ping(String name, ProcessCompound targetP) throws SmartFrogException,
            RemoteException {
        Prim targetC;

        if (name == null) {
            targetC = targetP;
        } else {
            targetC = (Prim) targetP.sfResolveWithParser(name);
        }
        long start,finish;
        start=System.currentTimeMillis();
        targetC.sfPing(this);
        finish = System.currentTimeMillis();
        return finish-start;
    }
}
