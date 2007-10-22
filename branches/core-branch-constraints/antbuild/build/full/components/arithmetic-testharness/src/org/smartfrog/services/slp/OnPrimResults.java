package org.smartfrog.services.slp;

import org.smartfrog.sfcore.workflow.eventbus.*;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.*;

import org.smartfrog.services.slp.*;

import java.rmi.*;
import java.util.Vector;
import java.util.Enumeration;
/**
 * On each discovered compound, either deploy the component description
 * that is pointed by 'link' if it has not been already deployed.
 * Allows to set 'role-based deployment', where each compound advertised
 * under a given name will, on its discovery by the SFSLP facility, receive
 * a given component to deploy.
 *
 * @author Guillaume Mecheneau
 */

public class OnPrimResults extends OnResults implements Prim{
  int count = -1;
  public OnPrimResults() throws RemoteException { }

  public void sfDeploy() throws SmartFrogException, RemoteException {
    super.sfDeploy();
    try {
      count = ((Integer) sfResolve("count")).intValue();
    } catch (SmartFrogResolutionException rex) {}
  }
  /**
   * This default implementation triggers on the service Provider, if it is a Compound, the deployment
   */
  public void triggerActionOn(Object serviceProvider) throws Exception{
    if (serviceProvider instanceof Compound) {
      // get the description of the component to deploy
      Object key = ((ComponentDescription) sfResolve("action")).sfContext().keys().nextElement();
      ComponentDescription action = (ComponentDescription)((ComponentDescription) sfResolve("action")).sfContext().get(key);
      // get the eventual link to be added to the context.
      ContextImpl ctxt = null;
      try {
        ctxt = new ContextImpl();
        ctxt.put("to", this.sfResolve("link"));
      }catch (Exception e){e.printStackTrace();}

   // if the object discovered does not already contain a compound named after the one I want to deploy,
      if ((!((Prim)serviceProvider).sfContext().containsKey(key))&&(count !=0)){
        System.out.println(((Prim)serviceProvider).sfCompleteName()+ " hasn't been affected yet --> DEPLOY ! (count = "+count+")");
        Prim p = ((Compound)serviceProvider).sfDeployComponentDescription(key,(Compound)serviceProvider,action,ctxt);
        p.sfDeploy();
        p.sfStart();
        if (--count ==0 ) {
           System.out.println("Count zero interrupt");
          ((EventBus)resultsCollector).sendEvent(SLPResultsCollector.stopSearch);
        }
      }
    }
  }
}
