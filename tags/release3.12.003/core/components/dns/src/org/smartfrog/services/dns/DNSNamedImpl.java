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

package org.smartfrog.services.dns;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Type;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;
import java.rmi.RemoteException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.ReferencePart;
import java.net.InetAddress;
import java.net.UnknownHostException;





/**
 * A "generic" named daemon wrapper that has little control over
 * the life-cycle of the daemon, but can check status using a 
 * "standard" mechanism.
 *
 * 
 * 
 */
public class DNSNamedImpl extends PrimImpl implements DNSNamed {


    /** Whether  we should terminate the named server
        when this component terminates. */
    boolean linkNamedLifeCycle = false;

    /** Configuration data pushed from the manager. */
    DNSData data = null;
    
    /** A static reference for whether we should terminate the named server
        when this component terminates. */
    static final Reference REF_LINKLIFECYCLE =
        new Reference(ReferencePart.here("linkNamedLifeCycle"));
   
    /** Number of checks that the daemon is up before giving up. */
    public static final int NUM_TRIES = 5;

    /** Msec between re-try to check up the daemon is up. */
    public static final long RETRY_MSEC = 200;

    /** A "standard" port for the DNS server.*/
    public static final int DEFAULT_PORT = 53;

    /**
     * Creates a new <code>DNSNamedImpl</code> instance.
     *
     * @exception RemoteException if an error occurs
     */
    public DNSNamedImpl() 
        throws RemoteException {
        
    }


    /**
     * sfDeploy lifecycle method as required by SmartFrog
     * @exception SmartFrogException if an error occurs
     * @exception RemoteException if an error occurs
     */
    public synchronized void sfDeploy()
        throws SmartFrogException, RemoteException {
        
        super.sfDeploy();
        
        linkNamedLifeCycle = sfResolve(REF_LINKLIFECYCLE, false, true);
    }

    /**
     * sfStart lifecycle method as required by SmartFrog.
     * @exception SmartFrogException if an error occurs
     * @exception RemoteException if an error occurs
     */
    public synchronized void sfStart()
        throws SmartFrogException, RemoteException {

        super.sfStart();

        if (data == null) {
            throw new DNSException("Manager did not initialized named");
        }
    }


    /**
     * Updates the internal configuration data reflecting a change in
     * zones/views/options...
     *
     * @param newData A hierarchy of data reflecting the current
     * configuration state.
     * @return The old configuration that is being replaced.
     * @exception RemoteException Cannot change the configuration.
     */
    public synchronized DNSData updateConfigData(DNSData newData) {

        DNSData old = data;
        data = newData;
        return old;
    }


    /**
     * sfTerminateWith lifecycle method as required by SmartFrog
     * @param tr A reason for termination.
     */
    public void sfTerminateWith(TerminationRecord tr) {

        if (linkNamedLifeCycle) {
            try {
                stop();
            } catch (DNSException e) {
                System.out.println("Can't stop named at termination"
                                   + "got exception" + e);
            }
        }
        super.sfTerminateWith(tr);        
    }

    
    /**
     * Starts the named daemon unless it is already started.
     *
     * @exception DNSException Error while starting the named
     * daemon.
     */
    public void start() 
        throws DNSException {

        throw new UnsupportedOperationException("Start of named not"
                                                + " supported");
    }

    /**
     * Stops the daemon and cleans up all the configuration
     * changes using dynamic updates, so that it can be
     * re-started in a known state.
     *
     * @exception DNSException Error while stopping/cleaning
     * the named daemon.
     */
    public void cleanUp() throws DNSException {

      throw new UnsupportedOperationException("Clean up of named not"
                                              + " supported");        
    }

    /**
     * Stops the named daemon unless it is already stopped.
     *
     * @exception DNSException Error while stopping the named
     * daemon.
     */
    public void stop() 
        throws DNSException{

        throw new UnsupportedOperationException("Stop of named not"
                                                + " supported");
    }


    /**
     * Flushes all the caches. This allows forward views or zones
     * to ensure they will get the most up to date information.
     *
     * @exception DNSException Error while flushing caches in the named
     * daemon.
     */
    public void flush()
        throws DNSException {

        throw new UnsupportedOperationException("Flush of named not"
                                                + " supported");
    }

    /**
     * Returns the status of the named daemon.
     *
     * @return True if the named daemon is up, false otherwise.
     * @exception DNSException Error while trying to find the
     * status of the named daemon.
     */
    public synchronized boolean status()
        throws DNSException {

        boolean result = false;
        try {
            Lookup lu = new Lookup("version.bind.", Type.TXT, DClass.CHAOS);
            lu.setResolver(data.getMainView().getResolver());
            lu.setCache(null);
            lu.run();
            if (lu.getResult() == Lookup.SUCCESSFUL) {
                System.out.println(lu.getAnswers()[0].rdataToString());
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            throw new DNSException("got exception while checking"
                                   + " named ", e);
        }
        return result;
    }

    /**
     * Asserts that the daemon is up/down, retrying several times in case of
     * failure.
     *
     * @param isUp Whether we want to make sure that the daemon is up.
     * @exception DNSException if state of daemom after re-tries is not the one
     * we want...
     */
    void assertStatus(boolean isUp) 
        throws DNSException {
        
        for (int i=0; i< NUM_TRIES; i++) {
            try {
                if ((isUp && status()) 
                    || (!isUp && !status())) {
                    // Up and running or down as expected...
                    return;
                }
            } catch (Exception e) {
                if (!isUp) {
                    /* in a leap of faith assume the exception is
                       because the named was stopped. */
                    return;
                }
                // otherwise ignore and retry
            }
            try {
                Thread.sleep(RETRY_MSEC);
            } catch (Exception e) {
                //ignore and retry
            }
            if (i > 0) {
                // avoid the first one...
                System.out.println("Cannot contact or kill named,"
                                   + " trying again...");
            }
        }
        throw new DNSException("Cannot contact or kill named");
    }




}


    
