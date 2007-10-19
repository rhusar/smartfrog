/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jmx.communication;

import javax.management.JMRuntimeException;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class RuntimeConnectionException extends JMRuntimeException {

    Throwable throwable;


    /**
     *  Constructor for the ConnectionException object
     *
     *@param  message  Description of the Parameter
     */
    public RuntimeConnectionException(String message) {
        super(message);
    }


    /**
     *  Constructor for the ConnectionException object
     *
     *@param  target   Description of the Parameter
     *@param  message  Description of the Parameter
     */
    public RuntimeConnectionException(Throwable target, String message) {
        super(message);
        throwable = target;
    }


    /**
     *  Gets the target attribute of the ConnectionException object
     *
     *@return    The target value
     */
    public Throwable getTarget() {
        return throwable;
    }

}
