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

package org.smartfrog.examples.exampleTemplate;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.PrimImpl;

/**
 * This class has to be run through RMIC compiler (add it to RMITARGETS)
 */
public class ExampleTemplateImpl extends PrimImpl implements Prim, ExampleTemplate {

    String example = "configToRead";

    /**
     *  Constructor for the Ant object.
     *
     *@exception  RemoteException In case of network/rmi error
     */
    public ExampleTemplateImpl() throws RemoteException {
    }

    // LifeCycle methods

    /**
     *
     * @exception  SmartFrogException In case of error in deploying
     * @exception  RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
       super.sfDeploy();

       readConfiguration();
      //if (sfLog().isInfoEnabled()) sfLog().info("end sfDeploy");
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
        if (sfLog().isInfoEnabled()) sfLog().info(" Terminating for reason: " + t.toString());
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
    protected void readConfiguration() throws SmartFrogException, RemoteException {
        //
        // Mandatory attributes. //True to Get exception thown!
        try {

            example = sfResolve(ATR_EXAMPLE, example, true);

        } catch (SmartFrogResolutionException e) {
          if (sfLog().isErrorEnabled()) sfLog().error("Failed to read mandatory attribute "+example+".Error:"+ e.toString());
            throw e;
        }
        //Optional attributes.
        //example = sfResolve(ATR_EXAMPLE, debug, false);

    }

    // Main component action methods

}
