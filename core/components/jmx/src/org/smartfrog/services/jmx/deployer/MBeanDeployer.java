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
import java.util.*;
import java.rmi.*;
import java.rmi.server.RemoteStub;
import java.rmi.server.UnicastRemoteObject;

import javax.management.*;
import javax.management.modelmbean.*;

import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.componentdescription.*;
import org.smartfrog.sfcore.processcompound.*;
import org.smartfrog.services.jmx.agent.*;
import org.smartfrog.services.jmx.communication.ConnectionFactory;
import org.smartfrog.services.jmx.communication.ConnectorClient;
import org.smartfrog.services.jmx.communication.ServerAddress;
import org.smartfrog.services.jmx.communication.ConnectionFactory;
import org.smartfrog.services.jmx.modelmbean.SFModelMBeanInfoBuilder;
import org.smartfrog.services.jmx.common.Utilities;

/**
 *  MBean deployer compound
 *
 */
public class MBeanDeployer extends CompoundImpl implements Compound, MBeanDeployerMBean {

    /**
     *  Reference name of this component
     */
    protected Reference nameRef;

    /**
     *  String name of this component
     */
    protected String name;

    /**
      *  Date deployment time of this component
      *  This time is used to generate unique names for MBeans registered by this MBeanDeployer
     */
    protected long deploymentTime=System.currentTimeMillis();

    /**
     *  The local MBeanServer
     */
    protected MBeanServer server = null;

    /**
     *  The MBeanServer identifier
     */
    protected String mBeanServerId = null;

    /**
     *  Flag indicating if the MBeanServer is remote or is within the same JVM
     */
    protected boolean isServerRemote = false;

    /**
     *  sfMBeans registered by this component in the JMX Agent
     */
    protected Hashtable registeredMBeans = new Hashtable();


    /**
     *  Default constructor for RMI
     *
     *@exception  RemoteException  Description of the Exception
     */
    public MBeanDeployer() throws RemoteException {
        super();
    }


    public InetAddress getSFProcessName() throws RemoteException {
        return sfDeployedHost();
    }

    public String getSFDeployedHost() throws RemoteException {
        return sfDeployedProcessName();
    }


    /**
     *  Indicates if the found MBeanServer is Remote
     *
     *@return  bolean is a remote server?
     *@throws  RemoteException
     */
    public boolean isServerRemote() throws RemoteException {
        return isServerRemote;
    }


    /**
     *  Returns the MBeanServer indentifier
     *
     *@return                   The mBeanServerId value
     *@throws  RemoteException
     *@throws  Exception
     */
    public String getMBeanServerId() throws RemoteException {
        return mBeanServerId;
    }


    /**
     *  Description of the Method
     *
     *@param  managedResource      Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     */
    public ObjectName findObjectName(Object managedResource) throws RemoteException {
        ObjectName on = null;
        on = (ObjectName) registeredMBeans.get(managedResource);
        if (on != null) {
            return on;
        }
        if (!(managedResource instanceof Remote)) {
            try {
                on = (ObjectName) registeredMBeans.get(Utilities.getStub((Remote) managedResource));
            } catch (Exception e) {}
        }
        return on;
    }


    /**
     *  Gets the managedResource attribute pointed by the reference passed as
     *  parameter turning it into a RemoteStub if necessary.
     *
     *@param  mrRef                The reference to managed resource
     *@return                      The managed resource Prim component
     *@exception  Exception        Description of the Exception
     *@exception  RemoteException  Description of the Exception
     */
    public Object findManagedResource(Reference mrRef) throws RemoteException, Exception {
        Object pointedResource = sfResolve(mrRef);
        return checkManagedResource(pointedResource);
    }


    /**
     *  Checks if the managed resource should be a RemoteStub object. This is
     *  necessary if the MBeanServer is remote. If necessary, this method will
     *  return the RemoteStub object if it exists. If it does not exist will
     *  throw a exception. If the server is local no RemoteStub is required.
     *
     *@param  managedResource
     *@return  Object RemoteStub is it exists
     *@exception  RemoteException  Description of the Exception
     *@throws  Exception           the MBeanServer is remote but the RemoteStub
     *      of the managed resource could not be found
     */
    public Object checkManagedResource(Object managedResource) throws RemoteException, Exception {
        if (isServerRemote && !sfIsRemote(managedResource)) {
            if (!(managedResource instanceof Remote)) {
                throw new Exception(managedResource.getClass().toString() + "must implement Remote interface");
            }
            return Utilities.getStub((Remote) managedResource);
        } else {
            return managedResource;
        }
    }


    /**
     *  Returns the ConnectorClient of this component
     *
     *@return   ConnectorClient or null
     *@exception  Exception     Description of the Exception
     *@throws  RemoteException
     */
    public ConnectorClient findConnectorClient() throws RemoteException, Exception {
        // if server is local, then we search for a ConnectorServer
        if (server == null) {
            return null;
        }
        if (isServerRemote) { // It is a ConnectorClient
            return (ConnectorClient)server;
        } else {              // It is the MBeanServer itself, and we need the ConnectorClient
            return ConnectionFactory.findConnectorClient(server);
        }
    }


    /**
     *  This method returns the MBeanServer of a deployed local instance of the
     *  JMXAgent referenced by the "address" parameter. The address must contain
     *  a reference reachable from sfResolve() method.
     *
     *@param  address                     Reference to a local JMX Agent
     *@return                             The MBeanServer
     *@exception  AgentNotFoundException  Thrown if the local referenced Agent
     *                                    does not exist
     */
    public MBeanServer findLocalMBeanServer(Reference address) throws AgentNotFoundException {
        try {
            address.setEager(true);
            SFJMXAgentImpl agent = (SFJMXAgentImpl) sfResolve(address);
            return agent.getMBeanServer(); // Get the MBeanServer itself
        } catch (Exception e) {
            throw new AgentNotFoundException(address, nameRef, "No local JMX Agent found", e.toString());
        }
    }


    /**
     * If the reference points to a JMX Agent then the MBeanServer interface of
     * a ConnectorClient is returned.
     *
     * @param address                       Reference to any JMX Agent
     * @return                              The ConnectorClient
     * @throws AgentNotFoundException       If the referenced JMX Agent does not
     *                                      exist
     * @throws RemoteException
     */
    public MBeanServer findRemoteMBeanServer(Reference address) throws AgentNotFoundException {
        try {
            SFJMXAgent agent = (SFJMXAgent) sfResolve(address);
            return agent.getConnectorClient(); // Get a client to the ConnectorServer
        } catch (Exception e) {
            throw new AgentNotFoundException(address, nameRef, "No remote JMX Agent found", e.toString());
        }
    }

    /**
     * Tries to locate the MBeanServer using different methods. If the address
     * is a reference, it looks for the MBeanServer locally and then remotely.
     * If the address is a string, it is considerend an URL and tries to connect
     * to the remote ConnectorServer.
     *
     * @param address
     * @throws AgentNotFoundException
     */
    public void findMBeanServer(Object address) throws RemoteException, AgentNotFoundException {
        if (address instanceof Reference) {
            try {
                server = findLocalMBeanServer((Reference) address);
                isServerRemote = false;
            }
            catch (Exception e) {
                server = findRemoteMBeanServer((Reference) address);
                isServerRemote = true;
            }
        } else if (address instanceof String) {
            try {
                server = ConnectionFactory.findMBeanServer((String) address);
                isServerRemote = true;
            } catch (AgentNotFoundException ex) {
                ex.source=nameRef;
                throw(ex);
            }
        } else {
            throw new AgentNotFoundException(address,nameRef,"Unknown JMXAgent address: " + address.toString());
        }
    }


    /**
     *  Register those components deployed or referenced in the sfMBean context
     *  of this MBeanDeployer.
     *
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void createSfMBeans() throws RemoteException, Exception {
        Compound mbeans = (Compound) sfResolveHere("sfMBeans");
        for (Enumeration b = mbeans.sfContext().keys(); b.hasMoreElements(); ) {
            String beanKey = null;
            try {
                beanKey = (String) b.nextElement();
                Object mbeanInstance = null;
                Object mrRef = mbeans.sfResolveHere(beanKey); //sfResolveID
                if (mrRef instanceof Reference) {
                    mbeanInstance = findManagedResource((Reference) mrRef);
                } else if (mbeanInstance instanceof Prim) {
                    mbeanInstance = checkManagedResource(mbeanInstance);
                } else {
                    continue;
                }
                ObjectName mbeanName = registerMBean(mbeanInstance);
                if (sfLog().isDebugEnabled()){ sfLog().debug(mbeanName + " registered");}
            } catch (MalformedObjectNameException e) {
                if (sfLog().isErrorEnabled()){ sfLog().error("\t Could not create the MBean ObjectName for component: " + beanKey,e);}
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *  Description of the Method
     *
     *@param  mbeanInstance  Description of the Parameter
     *@return                Description of the Return Value
     *@exception  Exception  Description of the Exception
     */
    public ObjectName registerMBean(Object mbeanInstance) throws RemoteException, Exception {
        ObjectName mbeanObjectName = null;
        if (mbeanInstance instanceof Prim) {
            mbeanObjectName = getObjectNameFrom(((Prim)mbeanInstance).sfContext(), mbeanInstance);
        }
        else {
            mbeanObjectName = getObjectNameFrom(new ContextImpl(), mbeanInstance);
        }

        // If our JMXAgent is remote but our MBean is local to this Introspector, we need to
        // get the Stub
        if (isServerRemote && !sfIsRemote(mbeanInstance)) {
            Object stub = Utilities.getStub((Remote)mbeanInstance);
            if (stub != null) {
                mbeanInstance = stub;
            }
            else {
                throw new Exception("RemoteStub of ModelMBean managed resource not found");
            }
        }
	Object[] params = {new Boolean(true)};
	String[] signature = {"boolean"};
	String mbeanClass = "org.smartfrog.sfcore.compound.CompoundImpl";
//	server.createMBean(mbeanClass, mbeanObjectName, null, params, signature);
       server.registerMBean(mbeanInstance, mbeanObjectName);

        // Put the MBean in the hashtable along with its ObjectName
        registeredMBeans.put(mbeanInstance, mbeanObjectName);
        return mbeanObjectName;
    }


    /**
     *  Builds ModelMBeanInfos and creates ModelMBeans in the MBeanServer from
     *  the descriptions provided under the attribute "modelMBeans" of this
     *  component.
     */
    protected void createModelMBeans() {
        Context modelMBeansContext = null;
        try {
            modelMBeansContext = ((ComponentDescription) sfResolveHere("modelMBeans")).sfContext();
        } catch (Exception e) {
            return;
        }
        if (modelMBeansContext == null) {
            return;
        }
        for (Enumeration mmbeans = modelMBeansContext.elements(); mmbeans.hasMoreElements(); ) {
            try {
                ObjectName mmb = registerModelMBeanWith(((ComponentDescription) mmbeans.nextElement()).sfContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *  Description of the Method
     *
     *@param  modelMBean     Description of the Parameter
     *@return                Description of the Return Value
     *@exception  Exception  Description of the Exception
     */
    public ObjectName registerModelMBeanWith(Context modelMBean) throws RemoteException, Exception {
        // Search managedResource
        Object mrRef = modelMBean.get("managedResource");
        if (!(mrRef instanceof Reference)) {
            throw new Exception("Unexpected reference to managed resource: " + mrRef.toString());
        }
        Object managedResource = findManagedResource((Reference) mrRef);

        // Build ObjectName
        ObjectName modelMBeanName = getObjectNameFrom(modelMBean, managedResource);

        // Find the ModelMBeanClass
        String modelMBeanClass = (String) modelMBean.get("class");

        // Build ModelMBeanInfo
        SFModelMBeanInfoBuilder infoBuilder = new SFModelMBeanInfoBuilder(managedResource, modelMBean);
        infoBuilder.buildAttributeInfo(managedResource);
        infoBuilder.buildOperationInfo(this, managedResource);
        infoBuilder.buildNotificationInfo();
        ModelMBeanInfo modelMBeanInfo = infoBuilder.getModelMBeanInfo();

        // Create and register
        registerModelMBean(modelMBeanClass, modelMBeanName, modelMBeanInfo, managedResource);

        return modelMBeanName;
    }


    /**
     *  Creates and register a modelMBean with the given parameters.
     *
     *@param  modelMBeanClass
     *@param  modelMBeanName
     *@param  modelMBeanInfo
     *@param  managedResource
     *@throws  Exception
     */
    public void registerModelMBean(String modelMBeanClass, ObjectName modelMBeanName, ModelMBeanInfo modelMBeanInfo, Object managedResource) throws Exception {
        // Check resource type whether it is a remote object
        String resourceType = "ObjectReference";
        if (sfIsRemote(managedResource)) {
            resourceType = "RMIReference";
        }
        // Create and register an instance of the ModelMBean
        server.createMBean(modelMBeanClass, modelMBeanName);
       server.invoke(modelMBeanName, "setModelMBeanInfo", new Object[]{modelMBeanInfo},
                new String[]{"javax.management.modelmbean.ModelMBeanInfo"});
        server.invoke(modelMBeanName, "setManagedResource", new Object[]{managedResource, resourceType},
                new String[]{"java.lang.Object", "java.lang.String"});

        // Put the managed resource in the hashtable along with the ObjectName
        registeredMBeans.put(managedResource, modelMBeanName);
    }

    /**
     *  Create and register PrimDynamicMBeans for those components deployed or
     *  referenced in the dynamicMBean context of this MBeanDeployer.
     *
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void createPrimDynamicMBeans() throws RemoteException, Exception {
        Compound mbeans = (Compound) sfResolveHere("dynamicMBeans");
        for (Enumeration b = mbeans.sfContext().keys(); b.hasMoreElements(); ) {
            String beanKey = null;
            try {
                beanKey = (String) b.nextElement();
                Prim prim = null;
                Object mrRef = mbeans.sfResolveHere(beanKey); //sfResolveID
                if (mrRef instanceof Reference) {
                    prim = (Prim) findManagedResource((Reference) mrRef);
                } else if (mrRef instanceof Prim) {
                    prim = (Prim) mrRef;
                } else {
                    continue;
                }
                ObjectName mbeanName = registerPrimDynamicMBean(prim);
                if (sfLog().isDebugEnabled()){ sfLog().debug(mbeanName + " registered");}
            } catch (MalformedObjectNameException e) {
                if (sfLog().isErrorEnabled()){ sfLog().error("\t Could not create the MBean ObjectName for component: " + beanKey,e);}
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *  Description of the Method
     *
     *@param  prim  prim to register
     *@return                Description of the Return Value
     *@exception  Exception  Description of the Exception
     */
    public ObjectName registerPrimDynamicMBean(Prim prim) throws RemoteException, Exception {
        if (prim == null) throw new IllegalArgumentException("prim cannot be null");
        try {
            prim.sfResolveHere("properties");
        }
        catch (Exception e) {
            Context properties = new ContextImpl();
            properties.put("name", Utilities.getDefaultNamePropertyFor(prim));
            properties.put("type", "sf.jmx.dynamicmbean.prim");
            ComponentDescription propertiesCompDesc = new ComponentDescriptionImpl(null, properties, true);
            prim.sfAddAttribute("properties", propertiesCompDesc);
        }

        ObjectName mbeanName = getObjectNameFrom(prim.sfContext(), prim);

        // If our JMXAgent is remote but our MBean is local to this MBeanDeployer, we need to get the Stub
        if (isServerRemote && !sfIsRemote(prim)) {
            Object stub = Utilities.getStub((Remote)prim);
            if (stub != null) {
                prim = (Prim)stub;
            }
            else {
                throw new Exception("RemoteStub of ModelMBean managed resource not found");
            }
        }

	System.out.println("Dynamic MBean registred with Name-=======" + mbeanName);
        server.createMBean( "org.smartfrog.services.jmx.mbean.PrimDynamicMBean",
                            mbeanName,
                            new Object[]{prim},
                            new String[]{"org.smartfrog.sfcore.prim.Prim"});

        // Put the MBean in the hashtable along with its ObjectName
        registeredMBeans.put(prim, mbeanName);
        return mbeanName;
    }

    /**
     *  Get a valid ObjectName from the a context
     *
     *@param  mbeanContext
     *@param  managedResource  Description of the Parameter
     *@return  ObjectName
     *@throws  Exception
     */
    public ObjectName getObjectNameFrom(Context mbeanContext, Object managedResource) throws Exception {
        String domain = (String) mbeanContext.get("domain");
        if (domain == null || domain.equals("default")) {
            domain = server.getDefaultDomain();
        }
        ComponentDescription cd = (ComponentDescription) mbeanContext.get("properties");
        Hashtable properties = null;
        if (cd == null) {
            properties = new Hashtable();
        } else {
            properties = (Hashtable) cd.sfContext();
        }
        if (!properties.containsKey("name")) {
            properties.put("name", Utilities.getDefaultNamePropertyFor(managedResource)+"_"+ Long.toString(deploymentTime));
        } else {
            String tempName = (properties.get("name")).toString();
            if (!tempName.endsWith("_"+ Long.toString(deploymentTime))){
               tempName = tempName + "_"+ Long.toString(deploymentTime);
               properties.put("name",tempName);
            }
        }
        if (!properties.containsKey("type")) {
            properties.put("type", "sf.jmx.mbean.generic");
        }
        if (mBeanServerId != null) {
            properties.put("server", mBeanServerId);
        }
        return new ObjectName(domain, (Hashtable) properties);
    }


    /**
     *  LIFECYCLE METHODS
     */
   TerminationRecord termR;

    /**
     *  sfDeploy method
     *
     *@exception  Exception  Description of the Exception
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        // Do deployment and obtain its own name
        super.sfDeploy();
        try {
          //To differentiate MBean Deployers with same name running in same host during agent registration process
          this.sfContext().put("deploymentTime", new Date(System.currentTimeMillis()));
          nameRef = sfCompleteNameSafe();
          name = nameRef.toString();

          // Locate the MBeanServer
          Object address = this.sfResolveHere("sfAgentAddress"); //sfResolveID
          try {
            findMBeanServer(address);
          } catch (AgentNotFoundException ex) {
            try {
              this.sfDetach();
            } catch (Exception dex) {
            }
            termR = (TerminationRecord.abnormal("MBeanDeployer terminated: Unable to bind to " +
                                           ex.remoteAgentAddress.toString() +
                                           ", reason: " + ex.getMessage(),
                                           nameRef));
            // Proper termination of a component!
            Runnable terminator = new Runnable() {
              public void run() {
                sfTerminate(termR);
              }
            };
            if (sfLog().isErrorEnabled()){ sfLog().error(termR.toString());}
            new Thread(terminator).start();
            return;
          }

          System.err.println("***************ToDo FIX *********** SeverID"+ server.toString());
          //mBeanServerId = (String) server.invoke(new ObjectName("JMImplementation:type=MBeanServerDelegate"), "getMBeanServerId", new Object[] {}, new String[] {});

    //     registerMBean(this);

          createPrimDynamicMBeans();

          createModelMBeans();

          createSfMBeans();
//        } catch (MalformedObjectNameException ex1) {
//        } catch (ReflectionException ex1) {
//        } catch (MBeanException ex1) {
//        } catch (InstanceNotFoundException ex1) {
//        } catch (SmartFrogResolutionException ex1) {
//        } catch (RemoteException ex1) {
        } catch (Exception ex1) {
          throw SmartFrogException.forward(ex1);
        }
    }


    /**
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        // We add the listeners in sfStart() just to let them get ready to receive notifications
    }


    /**
     *  Description of the Method
     *
     *@param  tr  Description of the Parameter
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        // Sent sfTerminateWith hook before unregistering, just in case an MBean is waiting for it
        super.sfTerminateWith(tr);

        // Unregister MBeans
        if (registeredMBeans != null) {
            //if (sfLog().isDebugEnabled()){ sfLog().debug(name + ": Unregistering MBeans from MBeanServer");
            if (sfLog().isDebugEnabled()){ sfLog().debug("Unregistering MBeans from MBeanServer");}
            for (Enumeration mbeans = registeredMBeans.elements(); mbeans.hasMoreElements(); ) {
                ObjectName on = null;
                try {
                    on = (ObjectName) mbeans.nextElement();
                    server.unregisterMBean(on);
                } catch (InstanceNotFoundException infe) {
                    if (sfLog().isErrorEnabled()){ sfLog().error ("MBean '" + on + "' had already been unregistered",infe);}
                } catch (Exception e) {
                    if (sfLog().isErrorEnabled()){ sfLog().error("Could not unregister MBean '" + on + "': " + e, e);}
                }
            }
        }
    }
    // end sfTerminateWith

}
