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

package org.smartfrog.examples.arithnet;

import java.rmi.Remote;



/**
 *  Constructs Dup Dup is basically a null operation, acting as a fan out.
 */
public class Dup extends NetElemImpl implements Remote {
   /**
    * Constructs Dup
    * @throws java.rmi.RemoteException if unable to construct Dup remotely
    */
    public Dup() throws java.rmi.RemoteException {
    }
}
