/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.axis;

import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * This is the axis service, which registers for anything.
 * The service supports different modes of deployment: deploy local, and deploy remote
 * But that detail is hidden because the work is handed back up to the parent.
 *
 * Date: 14-Jun-2004
 * Time: 14:27:26
 */

public class AxisServiceImpl extends PrimImpl implements AxisService {

    public AxisServiceImpl() throws RemoteException {
    }

    

}
