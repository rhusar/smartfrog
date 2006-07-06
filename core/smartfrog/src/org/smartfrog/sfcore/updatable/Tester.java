package org.smartfrog.sfcore.updatable;

import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.rmi.RemoteException;

/**

 */
public class Tester extends CompoundImpl implements Compound, Runnable {
    Thread tester;
    boolean finished;

    int sleepTime = 30000;

    String versionPrefix = "update";

    public Tester() throws RemoteException {
        super();
    }

    public void sfDeploy() throws RemoteException, SmartFrogException {
         sleepTime = sfResolve("sleepTime", sleepTime, false);
    }

    public void sfStart() throws RemoteException, SmartFrogException {
        tester = new Thread(this);
        tester.start();
    }

    public void sfTerminateWith(TerminationRecord tr) {
        finished = true;
        tester.interrupt();
    }


    public void run() {
        int counter = 0;
        ComponentDescription template;
        Update child = null;
        try {
             while (!finished) {
                 try {
                     template = (ComponentDescription)sfResolve(versionPrefix + counter, true);
                     if (counter == 0) {
                         System.out.println("deploying first");
                         child = (Update) sfCreateNewChild("child", template, null);
                     } else {
                         System.out.println("updating " + counter);
                         child.sfUpdateComponent(template);
                     }
                 } catch (SmartFrogResolutionException e) {
                    finished = true;
                 }
                 System.out.println("done");
                 Thread.sleep(sleepTime);
                 counter++;
             }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("tester finished");
        }
    }

}
