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
import org.smartfrog.SFSystem;

import java.rmi.RemoteException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Ping a component
 */
public class ActionDump extends ConfigurationAction implements Serializable {
    /**
     * ping message. {@value}
     */
    public static final String DUMP_MESSAGE = "Dump time :";

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
                targetP = SFProcess.sfSelectTargetProcess(configuration.getHost(),configuration.getSubProcess());
            }
            long time= dump(configuration.getName(), targetP);
            configuration.setSuccessfulResult();
            configuration.setResult(ConfigurationDescriptor.Result.SUCCESSFUL,DUMP_MESSAGE+(time/1000.0)+" seconds",null);
            return new Long(time);
        } catch (SmartFrogException sex) {
            configuration.setResult(ConfigurationDescriptor.Result.FAILED,null,sex);
            throw sex;
        } catch (RemoteException rex) {
            configuration.setResult(ConfigurationDescriptor.Result.FAILED,null,rex);
            throw rex;
        }

    }

    /**
     * Dump  a component description from a live system
     * @param componentName name of component; if null, we assume the processcompound
     *  itself is the target
     * @param targetP process to resolve against
     * @return long time (in milliseconds) the ping took.
     * @throws SmartFrogException  failure in some part of the process
     * @throws RemoteException    In case of network/rmi error
     */
    private long dump (String componentName, ProcessCompound targetP) throws SmartFrogException, RemoteException {
        Prim targetC;

        if (componentName == null) {
            targetC = targetP;
        } else {
            targetC = (Prim) targetP.sfResolveWithParser(componentName);
        }
        long start,finish;
        start=System.currentTimeMillis();
        StringBuffer message=new StringBuffer();
        String name = "error no name";
        try {
            name = targetC.sfCompleteName().toString();
        } catch (RemoteException e) {
            if (SFSystem.sfLog().isWarnEnabled()) SFSystem.sfLog().warn(e);
        }
        //Only works for Prims.
        if (targetC instanceof Prim) {
            try {
                Prim objPrim = ((Prim)targetC);
                message.append ("\n*************** State for "+ name+"  *****************\n");
                Dumper dumper = new DumperCDImpl(objPrim);
                objPrim.sfDumpState(dumper.getDumpVisitor());
                message.append (dumper.toString());
                name = (objPrim).sfCompleteName().toString();
                message.append ("\n*************** End state for "+ name+"  *****************\n");
            } catch (Exception ex) {
                if (SFSystem.sfLog().isErrorEnabled()) SFSystem.sfLog().error (ex);
                StringWriter sw = new StringWriter();
                PrintWriter pr = new PrintWriter(sw,true);
                ex.printStackTrace(pr);
                pr.close();
                message.append("\n Error: "+ex.toString()+"\n"+sw.toString());
            }
        }
        finish = System.currentTimeMillis();
        SFSystem.sfLog().out(message);
        return finish-start;
    }
}
