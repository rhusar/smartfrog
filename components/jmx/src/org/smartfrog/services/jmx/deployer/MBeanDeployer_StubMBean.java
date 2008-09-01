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

package org.smartfrog.services.jmx.deployer;

import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBeanInfo;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.services.jmx.communication.ConnectorClient;
import org.smartfrog.services.jmx.agent.AgentNotFoundException;

/**
 * Stub remote MBEan
 *
 * @version 1.0
 */

public interface MBeanDeployer_StubMBean extends Remote {

    public InetAddress getSFProcessName() throws RemoteException;

    public String getSFDeployedHost() throws RemoteException;

    /**
     *  Registers the object as an MBean
     *
     *@param  mbeanInstance  Description of the Parameter
     *@return                Description of the Return Value
     *@exception  Exception  Description of the Exception
     */
    public ObjectName registerMBean(Object mbeanInstance) throws RemoteException, Exception;

    /**
     *  Analizes the Context metadata and extract the neccessary configuration
     *  parameter to create and register a ModelMBean.
     *
     *@param  modelMBean     Description of the Parameter
     *@return                Description of the Return Value
     *@exception  Exception  Description of the Exception
     */
    public ObjectName registerModelMBeanWith(Context modelMBean) throws RemoteException, Exception;

    /**
     *  Creates and registers a ModelMBean with the specified configuration
     *  parameters.
     *
     * @param modelMBeanClass
     * @param modelMBeanName
     * @param modelMBeanInfo
     * @param managedResource
     * @throws Exception
     */
    public void registerModelMBean(String modelMBeanClass, ObjectName modelMBeanName, ModelMBeanInfo modelMBeanInfo, Object managedResource) throws Exception;

    /**
     * Creates and registers a PrimDynamicMBean for the Prim component given as
     * parameter.
     *
     * @param prim
     * @return the registration info
     * @throws RemoteException
     * @throws Exception
     */
    public ObjectName registerPrimDynamicMBean(Prim prim) throws RemoteException, Exception;

    /**
     * Tries to locate the MBeanServer using different methods. If the address
     * is a reference, it looks for the MBeanServer locally and then remotely.
     * If the address is a string, it is considerend an URL and tries to connect
     * to the remote ConnectorServer.
     *
     * @param address
     * @throws AgentNotFoundException
     */
    public void findMBeanServer(Object address) throws RemoteException, AgentNotFoundException;

    /**
     *  Indicates if the found MBeanServer is remote to this MBeanDeployer.
     *
     *@return                      The serverRemote value
     *@exception  RemoteException  Description of the Exception
     */
    public boolean isServerRemote() throws RemoteException;


    /**
     *  Gets a ConnectorClient connected to the MBeanServer found by this MBeanDeployer.
     *
     *@return                      The remoteMBeanServer value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public ConnectorClient findConnectorClient() throws RemoteException, Exception;


    /**
     *  Gets the mBeanServerId attribute of MBeanServerDelegate
     *
     *@return                      The mBeanServerId value
     *@exception  RemoteException  Description of the Exception
     */
    public String getMBeanServerId() throws RemoteException;


    /**
     *  Locates the managed resource pointed by the reference from this
     *  MBeanDeployer.
     *
     *@param  mrRef                Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public Object findManagedResource(Reference mrRef) throws RemoteException, Exception;


    /**
     *  Check if the managed resource is apropriate for being registered in a
     *  remote JMX Agent. If necessary, it will try to find the RemoteStub for
     *  this object.
     *
     *@param  managedResource      Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public Object checkManagedResource(Object managedResource) throws RemoteException, Exception;


    /**
     *  Determines what the best ObjectName should identify the MBean representing
     *  the managed resource.
     *
     *@param  managedResource      Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     */
    public ObjectName findObjectName(Object managedResource) throws RemoteException;

}
