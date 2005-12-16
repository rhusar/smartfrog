/** (C) Copyright Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.ant;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.PrimImpl;

import org.apache.tools.ant.Task;
import java.util.Iterator;

/**
 */
public class AntImpl extends PrimImpl implements Prim, Ant {


    /**
     *  Constructor for the Ant object.
     *
     *@exception  RemoteException In case of network/rmi error
     */
    public AntImpl() throws RemoteException {
    }

    // LifeCycle methods

    /**
     *
     * @exception  SmartFrogException In case of error in deploying
     * @exception  RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
       super.sfDeploy();

       readSFAttributes();

       AntProject ant = new AntProject();

       //Test get enviroment
       System.out.println( "SFHOME = " +  ant.getenv("SFHOME"));

       //set src to test if {$src} works.
       ant.setenv("src",".");

       Object attribute = null;
       Object value = null;
       Iterator a = this.sfAttributes();
       for (Iterator i = this.sfValues(); i.hasNext();){
           attribute = a.next();
           value = i.next();
           if (value instanceof org.smartfrog.sfcore.componentdescription.ComponentDescription){
               try {
                   Object task = ant.getTask((String)attribute, (ComponentDescription)value);
//                   ((AntProject)echo).invoke("message", new String[] {"hola"});
                   ((Task)task).execute();
               } catch (Exception ex) {
                   Throwable thr = ex;
                   if (thr instanceof java.lang.reflect.InvocationTargetException){
                       thr =  ex.getCause();
                   }
                   throw SmartFrogException.forward("Error executing: "+attribute,thr);
               }
           }
       }
       if (sfLog().isInfoEnabled()) sfLog().info("end sfDeploy");

    }

    /**
     *
     * @exception  SmartFrogException In case of error while starting
     * @exception  RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
    }

    /**
     * @param  t TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord t) {
        //if (sfLog().isInfoEnabled()) sfLog().info(" Terminating for reason: " + t.toString());
        super.sfTerminateWith(t);
    }

    // End LifeCycle methods

    // Read Attributes from description
    /**
     *  Reads optional and mandatory attributes.
     *
     * @exception  SmartFrogException error while reading attributes
     * @exception  RemoteException In case of network/rmi error
     */
    protected void readSFAttributes() throws SmartFrogException, RemoteException {
        //
        // Mandatory attributes. //True to Get exception thown!
//        try {
//
//            //limit = sfResolve(ATR_LIMIT, limit, true);
//
//        } catch (SmartFrogResolutionException e) {
//          if (sfLog().isErrorEnabled()) sfLog().error("Failed to read mandatory attribute. "+"Error:"+ e.toString());
//            throw e;
//        }
        //Optional attributes.
        //debug = sfResolve(ATR_DEBUG, debug, false);

    }

    // Main component action methods

}
