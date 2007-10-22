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
import java.util.Iterator;
import java.util.Map;

import org.smartfrog.services.anubis.locator.AnubisLocator;
import org.smartfrog.services.anubis.locator.AnubisProvider;
import org.smartfrog.services.anubis.locator.ValueData;
import org.smartfrog.services.anubis.partition.util.Config;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;


public class SPLocatorAdapterImpl
        extends PrimImpl
        implements Prim, SPLocatorAdapter {


    private class Provider extends AnubisProvider {
        public Provider(String name) { super(name); }
        public synchronized void setValueData(ValueData value) {
            setValueData(value, System.currentTimeMillis());
        }
        public synchronized void setValueData(ValueData value, long time) {
            setValueObj((ValueData)value);
            setTime(time);
            update();
        }
    }


    class LivenessChecker extends PeriodicTimer {
        LivenessChecker(long period) {
            super("Adapter liveness checker", period);
        }
        protected void act(long now) {
            checkLiveness(now);
        }
    }


    private AnubisLocator locator;
    private Map           subProcessLocators = new HashMap();
    private long          timeout;
    private long          period;
    private LivenessChecker livenessChecker;

    /****************************************************/
    /****************************************************/
    /*******           Prim interface             *******/
    /****************************************************/
    /****************************************************/
    public SPLocatorAdapterImpl() throws RemoteException {
        super();
        timeout = 4000;
        livenessChecker = new LivenessChecker(2000);
    }

    /**
     * Prim interface
     */
    public void sfDeploy() throws SmartFrogException, RemoteException  {
        try {
            super.sfDeploy();
            locator = (AnubisLocator)sfResolve("locator");
            period = Config.getLong(this, "heartbeatInterval");
            timeout = Config.getLong(this, "heartbeatTimeout") * period;
            livenessChecker = new LivenessChecker(period);
            livenessChecker.start();
        }
        catch (Exception ex) {
            throw (SmartFrogException)SmartFrogException.forward(ex);
        }
    }
    public void sfStart() throws SmartFrogException, RemoteException  {
        try {
            super.sfStart();
        }
        catch (Exception ex) {
            throw (SmartFrogException)SmartFrogException.forward(ex);
        }
    }
    public void sfTerminateWith(TerminationRecord status) {
        locator = null;
        livenessChecker.terminate();
        super.sfTerminateWith(status);
    }


    private SPLocatorData getSPLocatorData(Prim spLocator) throws UnknownSPLocatorException {
        SPLocatorData spLocatorData = (SPLocatorData)subProcessLocators.get(spLocator);
        if( spLocatorData == null )
            throw new UnknownSPLocatorException();
        else
            return spLocatorData;
    }


    private void checkLiveness(long now) {
        Map.Entry entry;
        SPLocatorData spLocatorData;
        Iterator iter = subProcessLocators.entrySet().iterator();
        while( iter.hasNext() ) {
            entry = (Map.Entry)iter.next();
            spLocatorData = (SPLocatorData)entry.getValue();
            if( spLocatorData.getLiveness().isNotTimely(now) ) {
                clearRegistrations(spLocatorData);
                iter.remove();
            }
        }
    }


    private void clearRegistrations(SPLocatorData spLocatorData) {
        Iterator iter;
        iter = spLocatorData.getProviders().values().iterator();
        while( iter.hasNext() )
            locator.deregisterProvider((Provider)iter.next());
        iter = spLocatorData.getListeners().values().iterator();
        while( iter.hasNext() )
            locator.deregisterListener((SPListenerAdapterImpl)iter.next());
        spLocatorData.clear();
    }

    /****************************************************/
    /****************************************************/
    /*******         SPLocator interface          *******/
    /****************************************************/
    /****************************************************/
    public synchronized void registerSPLocator(Prim spLocator) throws RemoteException, DuplicateSPLocatorException {

        if( this.sfIsTerminated )
            throw new RemoteException();

        if( subProcessLocators.containsKey(spLocator) )
            throw new DuplicateSPLocatorException();

        subProcessLocators.put(spLocator, new SPLocatorData(spLocator, timeout));
    }


    public synchronized void deregisterSPLocator(Prim spLocator) throws RemoteException, UnknownSPLocatorException {

        if( this.sfIsTerminated )
            throw new RemoteException();

        SPLocatorData spLocatorData = getSPLocatorData(spLocator);
        clearRegistrations(spLocatorData);
        subProcessLocators.remove(spLocatorData);
    }


    public synchronized SPProviderRegRet registerProvider(Prim spLocator, String name, ValueData value) throws RemoteException, UnknownSPLocatorException {

        if( this.sfIsTerminated )
            throw new RemoteException();

        Map providers = getSPLocatorData(spLocator).getProviders();
        Provider provider = new Provider(name);
        provider.setValueData(value);
        locator.registerProvider(provider);
        providers.put(provider.getInstance(), provider);
        return new SPProviderRegRet(provider.getInstance(), provider.getTime());
    }


    public synchronized void deregisterProvider(Prim spLocator, String instance) throws RemoteException, UnknownSPLocatorException {

        if( this.sfIsTerminated )
            throw new RemoteException();

        Map providers = getSPLocatorData(spLocator).getProviders();
        if( providers == null )
            return;

        Provider provider = (Provider)providers.remove(instance);
        if( provider != null )
            locator.deregisterProvider(provider);
    }


    public synchronized void newProviderValue(Prim spLocator, String instance, ValueData value, long time) throws RemoteException, UnknownSPLocatorException {

        if( this.sfIsTerminated )
            throw new RemoteException();

        Map providers = getSPLocatorData(spLocator).getProviders();
        Provider provider = (Provider)providers.get(instance);
        if( provider != null ) {
            provider.setValueData(value, time);
        }
    }


    public synchronized void registerListener(Prim spLocator, String name, SPListener spListener) throws RemoteException, UnknownSPLocatorException {

        if( this.sfIsTerminated )
            throw new RemoteException();

        Map listeners = getSPLocatorData(spLocator).getListeners();
        SPListenerAdapterImpl listener = new SPListenerAdapterImpl(name, spListener);
        locator.registerListener(listener);
        listeners.put(spListener, listener);
    }


    public synchronized void deregisterListener(Prim spLocator, SPListener spListener) throws RemoteException, UnknownSPLocatorException {

        if( this.sfIsTerminated )
            throw new RemoteException();

        Map listeners = getSPLocatorData(spLocator).getListeners();
        if( listeners == null )
            return;

        SPListenerAdapterImpl listener = (SPListenerAdapterImpl)listeners.remove(spListener);
        if( listener != null )
            locator.deregisterListener(listener);
    }


    public void registerStability(Prim spLocator, SPStability spStability) throws UnknownSPLocatorException, RemoteException {

        if( this.sfIsTerminated )
            throw new RemoteException();

        Map stabilities = getSPLocatorData(spLocator).getStabilities();
        SPStabilityAdapterImpl stability = new SPStabilityAdapterImpl(spStability);
        locator.registerStability(stability);
        stabilities.put(spStability, stability);
    }


    public void deregisterStability(Prim spLocator, SPStability spStability) throws UnknownSPLocatorException, RemoteException {

        if( this.sfIsTerminated )
            throw new RemoteException();

        Map stabilities = getSPLocatorData(spLocator).getStabilities();
        if( stabilities == null )
            return;

        SPStabilityAdapterImpl stability = (SPStabilityAdapterImpl)stabilities.remove(spStability);
        if( stability != null )
            locator.deregisterStability(stability);
    }



    public synchronized void livenessPing(Prim spLocator) throws RemoteException, UnknownSPLocatorException, AdapterTerminatedException {

        if( this.sfIsTerminated )
            throw new AdapterTerminatedException();

        getSPLocatorData(spLocator).getLiveness().ping();
    }


}
