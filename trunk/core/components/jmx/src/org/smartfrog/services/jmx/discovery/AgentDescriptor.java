package org.smartfrog.services.jmx.discovery;

import java.util.Vector;
import java.io.Serializable;

/**
 *  Provides a description of a given discovered JMX Agent
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *@author         Serrano
 *@version        1.0
 */

public class AgentDescriptor  implements Serializable {

    /**
     *  A list containing descriptors about all advertised Communicators of the
     *  JMX Agent.
     */
    Vector commDescriptors = new Vector();

    /**
     *  The host name where the JMX Agent is located
     */
    String Host;

    /**
     *  MBeanServer identifier
     */
    String MBeanServerId;

    /**
     *  JMX implementation name
     */
    String ImplementationName;

    /**
     *  JMX implementation vendor;
     */
    String ImplementationVendor;

    /**
     *  JMX implementation version
     */
    String ImplementationVersion;

    /**
     *  JMX specification name
     */
    String SpecificationName;

    /**
     *  JMX specification vendor
     */
    String SpecificationVendor;

    /**
     *  JMX specification version
     */
    String SpecificationVersion;


// Getter methods

    /**
     *  Returns the vector of CommunicatorDescriptor
     *
     *@return    The communicators value
     */
    public Vector getCommunicators() {
        return commDescriptors;
    }


    /**
     *  Gets the host attribute of the AgentDescriptor object
     *
     *@return    The host value
     */
    public String getHost() {
        return Host;
    }


    /**
     *  Gets the mBeanServerId attribute of the AgentDescriptor object
     *
     *@return    The mBeanServerId value
     */
    public String getMBeanServerId() {
        return MBeanServerId;
    }


    /**
     *  Gets the implementationName attribute of the AgentDescriptor object
     *
     *@return    The implementationName value
     */
    public String getImplementationName() {
        return ImplementationName;
    }


    /**
     *  Gets the implementationVendor attribute of the AgentDescriptor object
     *
     *@return    The implementationVendor value
     */
    public String getImplementationVendor() {
        return ImplementationVendor;
    }


    /**
     *  Gets the implementationVersion attribute of the AgentDescriptor object
     *
     *@return    The implementationVersion value
     */
    public String getImplementationVersion() {
        return ImplementationVersion;
    }


    /**
     *  Gets the specificationName attribute of the AgentDescriptor object
     *
     *@return    The specificationName value
     */
    public String getSpecificationName() {
        return SpecificationName;
    }


    /**
     *  Gets the specificationVendor attribute of the AgentDescriptor object
     *
     *@return    The specificationVendor value
     */
    public String getSpecificationVendor() {
        return SpecificationVendor;
    }


    /**
     *  Gets the specificationVersion attribute of the AgentDescriptor object
     *
     *@return    The specificationVersion value
     */
    public String getSpecificationVersion() {
        return SpecificationVersion;
    }

    public String toString() {
        return MBeanServerId+": "+commDescriptors;
    }

}
