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

package org.smartfrog.services.jmx.communication.html;

import javax.management.ObjectName;
import mx4j.tools.adaptor.http.XSLTProcessor;
import mx4j.tools.adaptor.http.ProcessorMBean;
import mx4j.tools.adaptor.AdaptorServerSocketFactory;  //AdaptorSocketFactory;
import mx4j.tools.adaptor.ssl.SSLAdaptorServerSocketFactory; //import mx4j.adaptor.http.ssl.SSLFactory;

import org.smartfrog.services.jmx.communication.CommunicatorMBean;

/**
 *  HTML adaptor
 *@version        1.0
 */
public interface HtmlAdaptorMBean extends CommunicatorMBean {

    /**
     *  Sets the host attribute of the HtmlAdaptorMBean object
     *
     *@param  host                                 The new host value
     *@exception  java.lang.IllegalStateException  Description of the Exception
     */
    public void setHost(String host) throws java.lang.IllegalStateException;

    /**
     * Sets the object which will post process the XML results. The last value
     * set between the setProcessor and setPostProcessorName will be the
     * valid one
     *
     * @param processor a processor object
     */
    public void setProcessor(ProcessorMBean processor);

    /**
     * Sets the object name which will post process the XML result. The last value
     * set between the setProcessor and setPostProcessorName will be the
     * valid one. The MBean will be verified to be of instance HttpPostProcessor
     *
     * @param processorName a processor object
     */
    public void setProcessorName(ObjectName processorName);

    /**
     * Returns the object being used as a processor
     *
     * @return the processor object
     */
    public ProcessorMBean getProcessor();

    /**
     * Returns the ObjectName of the processor being used
     *
     * @return processor objectname
     */
    public ObjectName getProcessorName();

    /**
     * Authentication Method
     *
     * @return authentication method
     */
    public String getAuthenticationMethod();

    /**
     * Sets the Authentication Method.
     *
     * @param method none/basic/digest
     */
    public void setAuthenticationMethod(String method);


    /**
     * Adds an authorization
     *
     * @param username authorized username
     * @param password authorized password
     */
    public void addAuthorization(String username, String password);

}
