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

package org.smartfrog.services.jmx.deployment;

import java.lang.reflect.*;
import java.util.*;
import java.rmi.*;
import javax.management.*;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.services.jmx.common.SFAttribute;

/**
 * Interface for the DeploymentAnalyzer.
 *
 * @version 1.0
 */

public interface DeploymentAnalyzer_StubMBean extends Remote {

    /**
     *  Returns the absolute path of the component from which the deployment
     *  tree will be visualized
     *
     *@return  String representing path
     *@throws  RemoteException
     *@throws  Exception
     */
    public String getRootPath() throws RemoteException, Exception;


    /**
     *  Description of the Method
     *
     *@param  attrib_path          Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public Object sfGetAttribute(String attrib_path) throws RemoteException, Exception;


    /**
     *  Description of the Method
     *
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public SFAttribute sfGetRoot() throws RemoteException, Exception;


    /**
     *  Description of the Method
     *
     *@param  comp_path            Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public Context sfGetAttributes(String comp_path) throws RemoteException, Exception;


    /**
     *  Description of the Method
     *
     *@param  attrib_path          Description of the Parameter
     *@param  value                Description of the Parameter
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void sfSetAttribute(String attrib_path, Object value) throws RemoteException, Exception;


    /**
     *  Description of the Method
     *
     *@param  attrib_path          Description of the Parameter
     *@param  value                Description of the Parameter
     *@param  type                 Description of the Parameter
     *@param  description          Description of the Parameter
     *@param  readable             Description of the Parameter
     *@param  writable             Description of the Parameter
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void sfAddAttribute(String attrib_path, String value, String type, String description, boolean readable, boolean writable) throws RemoteException, Exception;


    /**
     *  Description of the Method
     *
     *@param  attrib_path          Description of the Parameter
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void sfRemoveAttribute(String attrib_path) throws RemoteException, Exception;


    /**
     *  Description of the Method
     *
     *@param  comp_path            Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public MBeanOperationInfo[] sfGetMethods(String comp_path) throws RemoteException, Exception;


    /**
     *  Description of the Method
     *
     *@param  comp_path            Description of the Parameter
     *@param  method               Description of the Parameter
     *@param  params               Description of the Parameter
     *@param  signature            Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public Object sfInvokeMethod(String comp_path, String method, String params, String signature) throws RemoteException, Exception;


    /**
     *  Description of the Method
     *
     *@param  attribute            Description of the Parameter
     *@param  readable             Description of the Parameter
     *@param  writable             Description of the Parameter
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void sfChangeAccess(String attribute, boolean readable, boolean writable) throws RemoteException, Exception;


}
