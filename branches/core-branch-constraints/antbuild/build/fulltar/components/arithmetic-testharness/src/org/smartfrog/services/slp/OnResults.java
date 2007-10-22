package org.smartfrog.services.slp;

import org.smartfrog.sfcore.workflow.eventbus.*;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.services.slp.*;

import java.rmi.*;
import java.util.Vector;
import java.util.Enumeration;

/**
 * An abstract utility class to apply systematically an action on results received
 * from a query to the location utility.
 *
 * @author Guillaume Mecheneau
 */

public abstract class OnResults extends EventPrimImpl implements Prim{
    //protected Compound destination;
//    String myName, opponentName;
    // Standard constructor for SmartFrog components.
    Object resultsCollector;
    String serviceType;
    public OnResults() throws RemoteException { }

    /**
    * Collect data from the description
    */
    public void sfDeploy() throws SmartFrogException, RemoteException{
      super.sfDeploy();
      serviceType = (String) sfResolve("serviceType");
      resultsCollector = sfResolve("resultsCollector");
    }

    public void sfTerminateWith(TerminationRecord tr) {
        super.sfTerminateWith(tr);
    }
  /**
   * Do something on the result discovered.
   */
  public abstract void triggerActionOn(Object serviceProvider) throws Exception;

  /**
   * Handle an incoming message.
   * If the message received indicates that a new component of the service type has been located,
   * trigger the above action on it.
   */
  public synchronized void handleEvent(String event){
    if (event.equals(serviceType)){
      try {
        // the 'opponent' attribute is a lazy link to the resultsCollector's results vector
        Vector allResults = (Vector)((SLPResultsCollector) resultsCollector).sfResolve(serviceType.substring(ServiceType.servicePrefix.length()));
        for  (Enumeration e = allResults.elements(); e.hasMoreElements();){
          triggerActionOn(e.nextElement());
        }
      } catch (Exception ex){
        ex.printStackTrace();
      }
    } // do not treat any other event
  }
}
