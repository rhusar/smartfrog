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

package org.smartfrog.services.jmx.example;

import java.rmi.*;

/**
 *  Description of the Interface
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public interface ArithmeticManager_StubMBean extends Remote {

    /**
     *  Gets the leftValue attribute of the OperationManager_StubMBean object
     *
     *@return                      The leftValue value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public Integer getLeftValue() throws Exception;


    /**
     *  Gets the rightValue attribute of the OperationManager_StubMBean object
     *
     *@return                      The rightValue value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public Integer getRightValue() throws Exception;


    /**
     *  Gets the result attribute of the OperationManager_StubMBean object
     *
     *@return                      The result value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public Integer getResult() throws Exception;


    /**
     *  Sets the leftValue attribute of the OperationManager_StubMBean object
     *
     *@param  value                The new leftValue value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void setLeftValue(Integer value) throws Exception;


    /**
     *  Sets the rightValue attribute of the OperationManager_StubMBean object
     *
     *@param  value                The new rightValue value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void setRightValue(Integer value) throws Exception;


    /**
     *  Description of the Method
     *
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void sendLeftValue() throws Exception;


    /**
     *  Description of the Method
     *
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void sendRightValue() throws Exception;


    /**
     *  Description of the Method
     *
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void sendBothValues() throws Exception;

}
