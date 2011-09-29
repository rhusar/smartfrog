/** (C) Copyright 2010 Hewlett-Packard Development Company, LP

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

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.logging.LogSF;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 * Ping a component
 */
public class ActionList extends ConfigurationAction implements Serializable {
    /**
     * ping message. {@value}
     */
    private static final String LIST_MESSAGE = "List time :";

    /**
     * this has to be implemented by subclasses; execute a configuration command against a specified target
     *
     * @param targetP       target where to execute the configuration command
     * @param configuration configuration command to be executed
     * @return the execution time as a long
     * @throws SmartFrogException failure in some part of the process
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public Object execute(ProcessCompound targetP,
                          ConfigurationDescriptor configuration)
            throws SmartFrogException,
            RemoteException {
        try {
            if (targetP == null) {
                targetP = SFProcess.sfSelectTargetProcess(configuration.getHost(),
                        configuration.getSubProcess());
            }
            long time = list(configuration.getName(), targetP);
            configuration.setSuccessfulResult();
            configuration
                    .setResult(ConfigurationDescriptor.Result.SUCCESSFUL,
                            LIST_MESSAGE + (time / 1000.0) + " seconds",
                            null);
            return time;
        } catch (SmartFrogException sex) {
            configuration.setResult(ConfigurationDescriptor.Result.FAILED, null, sex);
            throw sex;
        } catch (RemoteException rex) {
            configuration.setResult(ConfigurationDescriptor.Result.FAILED, null, rex);
            throw rex;
        }

    }

    /**
     * List a component's top level attributes
     *
     * @param componentName name of component; if null, we assume the processcompound itself is the target
     * @param targetP       process to resolve against
     * @return long time (in milliseconds) the operation took.
     * @throws SmartFrogException failure in some part of the process
     * @throws RemoteException    In case of network/rmi error
     */
    private long list(String componentName, ProcessCompound targetP) throws SmartFrogException, RemoteException {
        Prim targetC;

        if (componentName == null) {
            targetC = targetP;
        } else {
            targetC = (Prim) targetP.sfResolveWithParser(componentName);
        }
        long start, finish;
        start = System.currentTimeMillis();
        LogSF log = SFSystem.sfLog();
        String name;
        try {
            name = targetC.sfCompleteName().toString();
        } catch (RemoteException e) {
            log.warn(e);
            name = "error no name";
        }
        StringBuilder message = new StringBuilder();
        try {
            message.append(name).append(":\n");
            Context ctx = targetC.sfContext();
            Enumeration keys = ctx.keys();
            while (keys.hasMoreElements()) {
                try {
                    Object key = keys.nextElement();
                    message.append(key.toString()).append("\t");
                    Object value = ctx.get(key);
                    message.append(value.toString()).append("\t");
                    message.append(value.getClass()).append("\n");
                } catch (Exception e) {
                    message.append(e.toString()).append("\n");
                    log.warn(e);
                }
            }
            log.out(message);
        } catch (Exception ex) {
            log.error(ex, ex);
        }
        finish = System.currentTimeMillis();
        return finish - start;
    }
}