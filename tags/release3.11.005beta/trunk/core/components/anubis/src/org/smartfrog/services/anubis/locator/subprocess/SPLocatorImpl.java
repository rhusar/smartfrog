/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.anubis.locator.subprocess;



import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.smartfrog.services.anubis.locator.AnubisListener;
import org.smartfrog.services.anubis.locator.AnubisLocator;
import org.smartfrog.services.anubis.locator.AnubisProvider;
import org.smartfrog.services.anubis.locator.AnubisStability;
import org.smartfrog.services.anubis.locator.util.ActiveTimeQueue;
import org.smartfrog.services.anubis.partition.util.Config;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogImplAsyncWrapper;


public class SPLocatorImpl
        extends PrimImpl
        implements Prim, AnubisLocator {


    class LivenessChecker extends PeriodicTimer {
        LivenessChecker(long period) {
            super("SPLocator liveness checker", period);
        }
        protected void act(long now) {
            checkLiveness(now);
        }
    }

    class Pinger extends PeriodicTimer {
        Pinger(long period) {
            super("SPLocator adapter pinger", period);
            setPriority(MAX_PRIORITY);
        }
        protected void act(long now) {
            pingAdapter();
        }
    }



    private boolean          isDeployed     = false;
    private boolean          isRegistered   = false;
    private Object           adapterMonitor = new Object();
    private SPLocatorAdapter adapter;
    private Set              providers      = new HashSet();
    private Map              listeners      = new HashMap();
    private Map              stabilities    = new HashMap();
    private Reference        reference;
    private Liveness         liveness;
    private LivenessChecker  livenessChecker;
    private Pinger           pinger;
    private ActiveTimeQueue  timers;
    private long             maxTransDelay;
    private LogSF            syncLog;
    private LogSF            asyncLog;


    /****************************************************/
    /****************************************************/
    /*******           Prim interface             *******/
    /****************************************************/
    /****************************************************/
    public SPLocatorImpl() throws RemoteException { super(); }

    /**
     * Prim interface
     */
    public void sfDeploy() throws SmartFrogException, RemoteException  {
        try {
            super.sfDeploy();

            syncLog = this.sfGetApplicationLog();
            asyncLog = new LogImplAsyncWrapper( syncLog );

            reference = sfCompleteName();
            long period = Config.getLong(this, "heartbeatInterval");
            long timeout = Config.getLong(this, "heartbeatTimeout") * period;
            liveness = new Liveness(timeout);
            pinger   = new Pinger(period);
            livenessChecker = new LivenessChecker(period);
            timers          = new ActiveTimeQueue();
            timers.start();
            maxTransDelay = period * timeout;

            synchronized(adapterMonitor) {
                adapter = (SPLocatorAdapter)sfResolve("subProcessAdapter");
                isDeployed = true;
            }
        }
        catch (Exception ex) {
            throw (SmartFrogException)SmartFrogException.forward(ex);
        }
    }
    public void sfStart() throws SmartFrogException, RemoteException  {
        try {
            super.sfStart();
            registered();
        }
        catch (Exception ex) {
            throw (SmartFrogException)SmartFrogException.forward(ex);
        }
    }

    /**
     * Note that on termination the SPLocator will deregister all the
     * listeners and providers it knows about, but it will not tell those
     * listeners and providers
     *
     * @param status
     */
    public void sfTerminateWith(TerminationRecord status) {
        deregister();
        timers.terminate();
        super.sfTerminateWith(status);
    }


    private boolean registered() {

        synchronized(adapterMonitor) {

            if( !isDeployed ) {
                // this case implies an attempt to register before the component
                // has been fully deployed.
                sfTerminate(TerminationRecord.abnormal("Attempt to register with adapter before depoyed", reference));
                return false;
            }

            if( sfIsTerminated ) {
                // this case implies the system is in the process of terminating
                // so just return indicating no registration
                return false;
            }

            if( isRegistered ) {
                // this case implies we've already registered so
                // return inidicating success
                return true;
            }

            try {

                adapter.registerSPLocator(this);
                isRegistered = true;
                liveness.ping();
                pinger.start();
                livenessChecker.start();
                return true;

            } catch (DuplicateSPLocatorException ex) {

                sfTerminate(TerminationRecord.abnormal("Already registered when trying to register with SPLocatorAdapter", reference ) );
                return false;

            } catch (RemoteException ex) {

                sfTerminate(TerminationRecord.abnormal("Remote exception trying to register with SPLocatorAdapter", reference ) );
                return false;

            }
        }
    }


    private void deregister() {

        synchronized(adapterMonitor) {

            if( !isRegistered )
                return;

            try {

                adapter.deregisterSPLocator(this);

            } catch (RemoteException ex) {
                // failure is ok
            } catch (UnknownSPLocatorException ex) {
                // failure is ok
            }

            pinger.terminate();
            livenessChecker.terminate();
            providers.clear();
            listeners.clear();
        }
    }


    /**
     * Lineness methods. The pinger pings the adapter at regular intervals.
     * The adapter will check it is being pinged regularly and the liveness
     * checker will check the pings are happening.
     */
    private void pingAdapter() {

        try {

            adapter.livenessPing(this);
            liveness.ping();

        } catch (UnknownSPLocatorException ex) {

            if( asyncLog.isWarnEnabled() )
                asyncLog.warn(ex);
            pinger.terminate();

        } catch (RemoteException ex) {

            if( asyncLog.isWarnEnabled() )
                asyncLog.warn(ex);
            pinger.terminate();

        } catch (AdapterTerminatedException ex) {

            if( asyncLog.isWarnEnabled() )
                asyncLog.warn(ex);
            pinger.terminate();

        }
    }


    private void checkLiveness(long now) {
        if( liveness.isNotTimely(now) )
            sfTerminate(TerminationRecord.abnormal("Failed timeliness at SPLocator end", reference ));
    }


    /****************************************************/
    /****************************************************/
    /*******       AnubisLocator interface        *******/
    /****************************************************/
    /****************************************************/
    public void registerProvider(AnubisProvider provider) {

        if( sfIsTerminated )
            return;

        if( !registered() )
            return;

        if( providers.contains(provider) )
            return;

        try {
            SPProviderRegRet ret = adapter.registerProvider(this, provider.getName(), provider.getValueData());
            provider.setAnubisData(this, ret.time, ret.instance);
            providers.add(provider);
        } catch (RemoteException ex) {
            sfTerminate( TerminationRecord.abnormal("Failed to call adapter", reference ) );
        } catch (UnknownSPLocatorException ex) {
            sfTerminate( TerminationRecord.abnormal("Adapter did not recognise me", reference ));
        }
    }


    public void deregisterProvider(AnubisProvider provider) {

        if( sfIsTerminated )
            return;

        if( !registered() )
            return;

        if( !providers.contains(provider) )
            return;

        try {
            adapter.deregisterProvider(this, provider.getInstance());
            providers.remove(provider);
        } catch (RemoteException ex) {
            sfTerminate( TerminationRecord.abnormal("Failed to call adapter", reference ) );
        } catch (UnknownSPLocatorException ex) {
            sfTerminate( TerminationRecord.abnormal("Adapter did not recognise me", reference ));
        }
    }


    public void newProviderValue(AnubisProvider provider) {

        if( sfIsTerminated )
            return;

        if( !registered() )
            return;

        if( !providers.contains(provider) )
            return;

        try {
            adapter.newProviderValue(this, provider.getInstance(), provider.getValueData(), provider.getTime());
        } catch (RemoteException ex) {
            sfTerminate( TerminationRecord.abnormal("Failed to call adapter", reference ) );
        } catch (UnknownSPLocatorException ex) {
            sfTerminate( TerminationRecord.abnormal("Adapter did not recognise me", reference ));
        }
    }


    public void registerListener(AnubisListener listener) {

        if( sfIsTerminated )
            return;

        if( !registered() )
            return;

        if( listeners.containsKey(listener) )
            return;

        try {
            listener.setTimerQueue(timers);
            SPListener spListener = new SPListenerImpl(listener);
            adapter.registerListener(this, listener.getName(), spListener);
            listeners.put(listener, spListener);
        } catch (RemoteException ex) {
            sfTerminate( TerminationRecord.abnormal("Failed to call adapter", reference ) );
        } catch (UnknownSPLocatorException ex) {
            sfTerminate( TerminationRecord.abnormal("Adapter did not recognise me", reference ));
        }
    }


    public void deregisterListener(AnubisListener listener) {

        if( sfIsTerminated )
            return;

        if( !registered() )
            return;

        if( !listeners.containsKey(listener) )
            return;

        try {
            SPListener spListener = (SPListener)listeners.remove(listener);
            adapter.deregisterListener(this, spListener);
            // don't set timers to null in listener
        } catch (RemoteException ex) {
            sfTerminate( TerminationRecord.abnormal("Failed to call adapter", reference ) );
        } catch (UnknownSPLocatorException ex) {
            sfTerminate( TerminationRecord.abnormal("Adapter did not recognise me", reference ));
        }
    }


    public void registerStability(AnubisStability stability) {

        if( sfIsTerminated )
            return;

        if( !registered() )
            return;

        if( stabilities.containsKey(stability) )
            return;

        try {
            stability.setTimerQueue(timers);
            SPStability spStability = new SPStabilityImpl(stability);
            adapter.registerStability(this, spStability);
            stabilities.put(stability, spStability);
        } catch (RemoteException ex) {
            sfTerminate( TerminationRecord.abnormal("Failed to call adapter", reference ) );
        } catch (UnknownSPLocatorException ex) {
            sfTerminate( TerminationRecord.abnormal("Adapter did not recognise me", reference ));
        }
    }


    public void deregisterStability(AnubisStability stability) {

        if( sfIsTerminated )
            return;

        if( !registered() )
            return;

        if( !stabilities.containsKey(stability) )
            return;

        try {
            SPStability spStability = (SPStability)listeners.remove(stability);
            adapter.deregisterStability(this, spStability);
            // don't set timers to null in stability
        } catch (RemoteException ex) {
            sfTerminate( TerminationRecord.abnormal("Failed to call adapter", reference ) );
        } catch (UnknownSPLocatorException ex) {
            sfTerminate( TerminationRecord.abnormal("Adapter did not recognise me", reference ));
        }
    }


    public ActiveTimeQueue getTimeQueue() {
        return timers;
    }

    public long getmaxDelay() {
        return maxTransDelay;
    }



}
