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

package org.smartfrog.test.system.cleanup;

import java.rmi.RemoteException;
import java.io.*;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;


/**
 *  Basic example component
 */
public class Comp2 extends PrimImpl implements Prim {
    
    private String fileName = "Comp2";
    private PrintWriter fileWriter = null;

    /**
     * Default Constructor 
     *
     *@exception  RemoteException  Description of the Exception
     */
    public Comp2() throws RemoteException {
    }

    // LifeCycle methods

    /**
     * sfDeploy: default implementation
     *
     *@exception  Exception  Description of the Exception
     */
    public void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        // get the file name attribute
        fileName = sfResolve("filename", fileName, true);
        // open one file writer for the file
        try {
            fileWriter
               = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        }catch (IOException ioex){
            throw new SmartFrogDeploymentException(ioex);
        }
    }

    /**
     *  sfStart: starts counter thread
     *
     *@exception  Exception  Description of the Exception
     */
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        String msg = "This is a test for SF f/w cleanup at termination of daemon";
        BufferedReader br =  new BufferedReader(new 
                                InputStreamReader(System.in));
        String ln = null;
        //write something to the file
        fileWriter.write(msg, 0, msg.length());
        sfLog().warn("Please shutdown the daemon in 10 sec..");
        try {
            Thread.sleep(10000);
        } catch (Exception ex) {
            //ignore
        }
    }

    /**
     *  sfTerminate
     *
     *@param  t  Description of the Parameter
     */
    public void sfTerminateWith(TerminationRecord t) {
        super.sfTerminateWith(t);
        sfLog().info("sfTerminateWith of Comp2...");
        // close the o/p stream
        //try {
            fileWriter.close();
            // delete the file 
            File fl = new File (fileName);
            fl.delete();
        //}catch (IOException ioex) {
        //    System.out.println(ioex);
        //}
    }

    // End LifeCycle methods
}
