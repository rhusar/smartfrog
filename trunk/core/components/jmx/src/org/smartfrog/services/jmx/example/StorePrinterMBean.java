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
public interface StorePrinterMBean extends Remote {

    /**
     *  Gets the result attribute of the StorePrinterMBean object
     *
     *@return                      The result value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public Integer getResult() throws Exception;


    /**
     *  Gets the evaluationEnabled attribute of the StorePrinterMBean object
     *
     *@return                      The evaluationEnabled value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public Boolean isEvaluationEnabled() throws Exception;


    /**
     *  Prevent the printer from evaluating the next arriving value
     *
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void disableNextEvaluation() throws Exception;

}
