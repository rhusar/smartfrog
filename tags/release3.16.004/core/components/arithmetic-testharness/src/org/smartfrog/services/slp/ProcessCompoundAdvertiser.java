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
package org.smartfrog.services.slp;


import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.processcompound.*;

import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 * A SmartFrog component to advertise components using the SLP utility.
 *
 * @author Guillaume Mecheneau
 */
public class ProcessCompoundAdvertiser extends SFSLPAdvertiser implements Prim{

  /** Reference used to lookup the deployer type */
  protected static final Reference refDeployerType =
    new Reference(ReferencePart.here("sfDeployerType"));

  private String deployerType;
  private String serviceURL;
  /** Standard constructor */
  public ProcessCompoundAdvertiser() throws RemoteException {
  }

  public void sfDeploy() throws SmartFrogException, RemoteException {
    super.sfDeploy();
    serviceURL = (String)sfResolve(refServiceURL,"",false);
    // if this serviceURL is empty, build a new one with the deployer type
    if (serviceURL.length()==0) {
      // get the deployer type
      try {
        deployerType = (String) sfResolve(refDeployerType);
      } catch (Exception e){
        deployerType = PrimSLPDeployerImpl.deployerServiceType;
      }
      // build the service URL to advertise, and replace it into the context.
      serviceURL = ServiceType.servicePrefix+deployerType+"://localhost/";
      sfContext().put("sfAdvertisementServiceURL",serviceURL);
    }
  }


/**
 * The service URL advertised by this component should only Advertise the root Process Compound.  Then flag the process compound to mark
 * it as advertised . For further SLP-based deployment, this allows the PC
 * to check if itself is a deployer of the type required before querying
 * for a list of ProcessCompound advertised as deployers with the right type.
 * (In this case, the 'sfServiceType' in the process compound has to match
 * the 'sfDeployerType' in the 'sfDeployerDescription' required for SLP-based
 * deployment)
 */
  public void sfStart() throws SmartFrogException , RemoteException{
    // get the process compound
    ProcessCompound pc = SFProcess.getProcessCompound();
    //ServiceURL sURL = new ServiceURL(serviceURL);
    pc.sfContext().put("advertisedAs",deployerType);
    super.sfStart();
  }
}
