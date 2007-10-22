/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

*/
package org.smartfrog.regtest.arithmetic;

import org.smartfrog.sfcore.workflow.eventbus.*;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.processcompound.*;
import org.smartfrog.sfcore.componentdescription.*;
import org.smartfrog.services.slp.*;

import java.rmi.*;
import java.util.Vector;
import java.util.Enumeration;

import java.net.*;

public class OnBlackBoxResults extends OnResults implements Prim {
    static int outputNumber = 0;
    int time;
    Thread timer;
    ComponentDescription locateProviderDesc;

    static public String generateUniqueName() {
        String result = "output";
        try {
            result += InetAddress.getLocalHost().toString();
            result += SFProcess.getProcessCompound().sfCompleteName().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        result += (new Integer(outputNumber++)).toString();
        return result;
    }

    public OnBlackBoxResults() throws RemoteException {
    }

    public void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        time = ((Integer) sfResolve("timeout")).intValue();
        locateProviderDesc = (ComponentDescription) sfResolve("onTimeout");
    }

    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        // let any errors be thrown and caught by SmartFrog for abnormal termination  - including empty actions
        timer = new Thread(new Runnable() {
            public boolean running = true;

            public void run() {
                if (time > 0) {
                    try {
                        Thread.sleep(time);
                        if (running) {
                            triggerResourceDeployment();
                        }
                    } catch (Exception e) {
                    }
                }
            }

            public void stop() {
                running = false;
            }
        });
        timer.start();
    }

    public void triggerResourceDeployment() throws Exception {
        // get the locator for the description provider

//    System.out.println( " Resource depl");
        Prim loc = ((Compound) sfParent()).sfDeployComponentDescription("descriptionLocator", (Compound) sfParent(), locateProviderDesc, null);
        loc.sfDeploy();
        loc.sfStart();
        try {
//    System.out.println( " Started "+loc.sfCompleteName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // get the operation for which we want a description
        ComponentDescription resDesc = (ComponentDescription) loc.sfResolve("serviceNeeded");
        String type = (String) resDesc.sfContext().get("sfServiceType");
        String operation = "";
        if (type.indexOf("generator") == -1) {
//      System.out.println( " Generator description needed ");
        } else {
            operation = (String) ((ComponentDescription) resDesc.sfContext().get("sfServiceAttributes")).sfContext().get("op");
//      System.out.println( " Description needed is " +operation);
        }
        // get the description and deploy it.

        DescriptionProvider dp = (DescriptionProvider) loc.sfResolve("descriptionProvider");
//    System.out.println(" I have a description Provider "+ ((Prim)dp).sfCompleteName());
        ComponentDescription soughtAfter = dp.giveDesc(operation);
//    System.out.println(" Received " + soughtAfter);
        Prim opOrGen = ((Compound) sfParent()).sfDeployComponentDescription(null, null, soughtAfter, null);
        opOrGen.sfDeploy();
        opOrGen.sfStart();
//try{     System.out.println( " Generator deployed "+opOrGen.sfCompleteName());
//    } catch (Exception e){e.printStackTrace();}
    }

    /**
     * This default implementation triggers on the service Provider, if it is a Compound, the deployment of a new output
     * pointing to the provided input
     */
    public void triggerActionOn(Object serviceProvider) throws Exception {
        ContextImpl ctxt = null;
        Compound blackBox = (Compound) serviceProvider;
        Prim link = null;
        try {
            ctxt = new ContextImpl();
            ctxt.put("to", sfResolve("to"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ComponentDescription outputDescription = (ComponentDescription) sfResolve("outputDescription");
        Prim p = blackBox.sfDeployComponentDescription(OnBlackBoxResults.generateUniqueName(), blackBox, outputDescription, ctxt);
        p.sfDeploy();
        p.sfStart();
        try {
            timer.stop();
            timer.interrupt();
//      System.out.println(" Found blackBox: "+ blackBox.sfCompleteName()+ "\n and deployed new Output: "+p.sfCompleteName());
        } catch (Exception e) {
        }
    }
}
