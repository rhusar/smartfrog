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

package org.smartfrog.examples.persistence;

import java.rmi.RemoteException;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.smartfrog.sfcore.common.SmartFrogException;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

import org.smartfrog.examples.counter.Counter;
import org.smartfrog.examples.counter.CounterImpl;

/**
 *  Basic example component.
 *  The PersistentCounter component (in components.sf) is a basic primitive component so
 *  its component description class PersistentCounterImpl extends PrimImpl
 *  through extending CounterImpl.
 *
 *  The persistent counter is identical to counter, but provides in addition the
 *  capability to be restarted according to the simple persistence pattern provided with the core
 *
 *  The pattern provides the ability for the component to be aware of the fact that it is being restarted
 *  rather than being started from scratch, and provides some attributes, such as a directory and filename stem,
 *  which allows it to persist and recover its internal state at regular checkpoints. In this way the
 *  comopnent can restart at the point it had previously reached.
 *
 *  In the case of the counter, this state is the last value reached...
 */
public class PersistentCounterImpl extends CounterImpl implements Prim, Counter, Runnable {

    String checkpointDir = "/tmp";
    String checkpointFileRoot = "persistentCounter";
    File checkpointFile = null;

    /**
     * Constructor for the PersistenceCounter object.
     * @throws RemoteException in case of remote/network error
     */
    public PersistentCounterImpl() throws RemoteException {
    }

    /**
     *  sfDeploy: reads Counter attributes and configures counter thread
     *  The superclass implementation of sfDeploy is called before the
     *  component specific initialization code (reading Counter attributes
     *  and configuring counter thread) to maintain correct behaviour of
     *  initial deployment and starting the heartbeat monitoring of this
     *  component.
     *
     * @exception  SmartFrogException In case of error in deploying
     * @exception  RemoteException In case of network/rmi error
     */
    public void sfDeploy() throws SmartFrogException, RemoteException {
	super.sfDeploy();

        // construct filename for state checkpoint
	// if not restarting
        //   checkpoint intial state
        // if restarting
        //   read state and if fail leave as is...

	try {
            // windows does not like ":" in the file name
	    String name = sfCompleteNameSafe().toString().replaceAll(":", "#");

	    checkpointDir = sfResolve("sfCheckpointDirectory", checkpointDir, false);
	    checkpointFileRoot = sfResolve("sfCheckpointFileRoot", checkpointFileRoot, false);

	    checkpointFile = new File(checkpointDir, checkpointFileRoot + "_" + name + ".chkpt" );

	    if (checkpointFile.canRead()) {
		ObjectInputStream reader = new ObjectInputStream(new FileInputStream(checkpointFile));
		counter = reader.readInt();
		reader.close();
	    }

	    checkpointState();
	} catch (Exception e) {
	    SmartFrogException.forward("Error reading checkpointed state", e);
	}
    }


    /**
     *  sfTerminate: The superclass implementation of sfTerminateWith is called
     *  after the component specific termination code to implement useful
     *  termination behaviour of the component.
     *
     * @param  t TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord t) {
        if (sfLog().isInfoEnabled()) sfLog().info("sfTerminateWith is terminating for reason: " + t.toString());
        if (action != null) {
            action.interrupt();
        }

	    if (checkpointFile != null) checkpointFile.delete();
        super.sfTerminateWith(t);
    }

    /**
     * Checkpointing the state
     * @throws FileNotFoundException error in finding the file
     * @throws IOException error in IO
     */
    protected synchronized void checkpointState() throws FileNotFoundException, IOException {
	    ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(checkpointFile));
            writer.writeInt(counter);
	    writer.close();
    }

    /**
     * Run method
     */
    public void run() {
        try {
            while (limit >= counter++) {
                String messageSt = ("COUNTER: " + message + " " + counter);
                sfLog().out(messageSt);
		try {
		    checkpointState();
		} catch (Exception e) {
		    String messageE = ("COUNTER: " + message + " error in checkpointing state: " + e);
		    sfLog().out(messageE);
		}

                if(sleeptime>0) {
                    Thread.sleep(sleeptime);
                }
            }

            if (terminate) sfTerminate(TerminationRecord.normal(this.sfCompleteNameSafe()));

            //end while
        } catch (InterruptedException ie) {
            if (sfLog().isErrorEnabled()) sfLog().error("peristent counter thread failed with exception ", ie);
        }
    }

}
