package org.smartfrog.services.sfinterface;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;


public class SubmitterThread extends Thread
{
    String compName=null;
    ComponentDescription compDes=null;
    Prim submittedComp;
    SubmitterThread( Prim p )
    {
        this.submittedComp=p;

    }
    public void run()
    {
        System.out.println("Inside ST");
        try
        {
          //SmartFrogAdapterImpl.sfDaemon.sfCreateNewApp(compName, compDes, null);
            submittedComp.sfDeploy();
            submittedComp.sfStart();

         } catch (SmartFrogDeploymentException e) {
            SmartFrogAdapterImpl.sfLog().error("Deployment Exception" , e);
        } catch (SmartFrogException e) {
            SmartFrogAdapterImpl.sfLog().error("Deployment Exception" , e);
        } catch (RemoteException e) {
            SmartFrogAdapterImpl.sfLog().error("Deployment Exception" , e);
        }
    }
}
