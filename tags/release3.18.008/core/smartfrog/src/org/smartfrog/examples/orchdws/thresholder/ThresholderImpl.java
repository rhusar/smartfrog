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

package org.smartfrog.examples.orchdws.thresholder;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.examples.dynamicwebserver.gui.graphpanel.DataSource;
import org.smartfrog.services.dependencies.statemodel.state.InvokeAsynchronousStateChange;
import org.smartfrog.services.dependencies.statemodel.state.StateComponent;
import org.smartfrog.services.dependencies.statemodel.state.StateComponentTransitionException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;

/**
 * <p>
 * Description: Thresholder component.
 * </p>
 *
 */

public class ThresholderImpl extends CompoundImpl implements Thresholder, Compound, SmartFrogCoreKeys  {
    int pollFrequency;
    int repeatMeasures;
    int stabilizationMeasures;
    DataSource dataSource = null;
    int stabilizationCounter = 0;
    Vector measures = new Vector();
    StateComponent balancer;

    /* start polling for the values */
    Poller poller = null;
    
    public ThresholderImpl() throws RemoteException {
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        balancer = (StateComponent) sfResolve(BALANCER, true);
        pollFrequency = sfResolve(POLLFREQUENCY, 5, false) * 1000;
        repeatMeasures = sfResolve(REPEATMEASURES, 3, false);
        stabilizationMeasures = sfResolve(STABILIZATIONMEASURES, 5, false);
        dataSource = (DataSource) sfResolve(DATASOURCE, true);
        if (sfLog().isDebugEnabled()) sfLog().debug ("thresholder deployed");
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        if (sfLog().isDebugEnabled()) sfLog().debug ("thresholder starting");

        poller = new Poller();
        poller.start();
        if (sfLog().isDebugEnabled()) sfLog().debug ("thresholder started");
    }
    
    public int upperThreshold() throws RemoteException {
    	int thresh=0;
    	try {
    		thresh=sfResolve(UPPERTHRESHOLD, 0, true);
    	}catch(Exception e){/*Shouldn't happen*/}
    	return thresh;
    }

    public int lowerThreshold() throws RemoteException {
    	int thresh=0;
    	try {
    		thresh=sfResolve(LOWERTHRESHOLD, 0, true);
    	}catch(Exception e){/*Shouldn't happen*/}
    	return thresh;
    }

    public void setUpperThreshold(final int threshold){
    	try { balancer.sfReplaceAttribute("upper", threshold);} catch (Exception e){/*Shouldn't happen*/}
    }
    
    public void setLowerThreshold(final int threshold){
    	try { balancer.sfReplaceAttribute("lower", threshold);} catch (Exception e){/*Shouldn't happen*/}
    }
  
    protected class Poller extends Thread {
    	int delay;
        public void run() {
            if (sfLog().isDebugEnabled()) sfLog().debug ( "poller running"); 
            	while (!(ThresholderImpl.this.sfIsTerminated())) {      
            		try {
		            	if (sfLog().isDebugEnabled()) sfLog().debug ("poller sleeping");
		                sleep(pollFrequency);
		                if (sfLog().isDebugEnabled()) sfLog().debug ("poller awake");
		            	
		            	boolean pollingEnabled= ((Boolean)sfResolve(POLLINGENABLED)).booleanValue();
		            	
		            	if (!pollingEnabled) continue;  //round while...
		            		 
		                
	                    int value = dataSource.getData();
	                    if (sfLog().isDebugEnabled()) sfLog().debug ( "poller measure obtained " +  value);
	
	                    measures.add(new Integer(value));
	
	                    if (measures.size() > repeatMeasures) {
	                        if (sfLog().isDebugEnabled()) sfLog().debug ( "poller has sufficient measures to proceed");
	
	                        measures.remove(0);
	
	                        // we now have enough measures to start testing...
	                        if (stabilizationCounter > 0) {
	                            // don't do anything if not stabilized...
	                            if (sfLog().isDebugEnabled()) sfLog().debug ( "poller not stabilized yet");
	                            stabilizationCounter--;
	                        } else {
	                            if (sfLog().isDebugEnabled()) sfLog().debug ("poller stabilized");
	
	                            // have enough measures and are stabilised...
	                            // note it would be more efficient to keep a running total (adding and subtracting
	                            // values as they are added and removed from the vector
	                            // also would be more efficient to multiply the thresholds by the repeats once, not do
	                            // the averaging division each time.
	                            // keeping the logic here, however, makes it easier to maintain and modify during testing...!
	                            delay = 0;
	
	                            for (Enumeration e = measures.elements();
	                                    e.hasMoreElements();) {
	                                delay += ((Integer) e.nextElement()).intValue();
	                            }
	
	                            delay = delay / repeatMeasures;
	                            if (sfLog().isDebugEnabled()) sfLog().debug ( "poller average of repeat measures is " + delay);
	                            try { balancer.sfReplaceAttribute("delay", delay);} catch (Exception e){/*Shouldn't happen*/}
	                        }
	                    }
		
	            	} catch (Exception e) {
	                    if (sfLog().isWarnEnabled()) sfLog().warn("exception caught in the poller. "+e.getMessage(),e);
	                }    
                
            	}
            if (sfLog().isDebugEnabled()) sfLog().debug ("poller stopped");
        }
    }
}
