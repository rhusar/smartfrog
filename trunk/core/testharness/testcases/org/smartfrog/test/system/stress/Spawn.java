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

package org.smartfrog.test.system.stress;

import java.rmi.RemoteException;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.componentdescription.*;
import org.smartfrog.sfcore.reference.Reference;

public class Spawn extends CompoundImpl implements Prim{
    
  /** Reference used to look up the component description */
  protected static final Reference refOffspringDescription =
                                      new Reference("sfOffspringDescription");
  /** Reference used to look up the generic name used for the siblings */
  protected static final Reference refOffspringName =
                                      new Reference("sfOffspringName");
  /** Reference used to look up the number of siblings to be deployed */
  protected static final Reference refFamilySize =
                                      new Reference("sfFamilySize");
  /** Reference used to look up the eventual deployment destination*/
  protected static final Reference refDestination =
                                      new Reference("sfDestination");

  /** The component description to be deployed. */
  private ComponentDescription offspringDescription;
  
  /** The generic name prefix used to name the siblings */
  private String offspringName = "copy";
  
  /** The number of copies to be deployed.  */
  private int familySize;
  
  /** The destination */
  private Compound destination;

  /**
   * Default constructor
   */
  public Spawn() throws RemoteException {
  }
  
  /**
   * During the deploy phase, picks up the following attributes:
   * sfOffspringDescription - description of the component to be spawned
   * sfFamilySize - the number of siblings to launch.
   * sfDestination - destination component where the description should 
   * be deployed.
   * Then deploy the copies in the destination component.
   */
  public void sfDeploy() throws SmartFrogException, RemoteException {
      try {
          super.sfDeploy();
          offspringDescription = (ComponentDescription)
                  sfResolve(refOffspringDescription);
          familySize = ((Integer) sfResolve(refFamilySize)).intValue();
          offspringName = (String) sfResolve(refOffspringName);
          try {
              destination = (Compound) sfResolve(refDestination);
          } catch (SmartFrogResolutionException rex) {
              destination = this;
          }
      } catch (Throwable th) {
          throw new SmartFrogDeploymentException(th, this);
      }
      for (int i = 0; i < familySize; i++) {
          String copyName = offspringName + (new Integer(i)).toString();
          Prim p = sfDeployComponentDescription(
                  copyName,
                  destination,
                  (ComponentDescription) offspringDescription.copy(),
                  null);
          p.sfDeploy();
      }
  }
}
