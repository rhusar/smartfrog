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

package org.smartfrog.services.jmx.cascading;

import java.util.Set;
import javax.management.ObjectName;
import javax.management.QueryExp;

/**
 *  Description of the Interface
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public interface CascaderMBean {

    /**
     *  Gets the protocol attribute of the CascaderMBean object
     *
     *@return    The protocol value
     */
    public String getProtocol();


    /**
     *  Sets the protocol attribute of the CascaderMBean object
     *
     *@param  prot           The new protocol value
     *@exception  Exception  Description of the Exception
     */
    public void setProtocol(String prot) throws Exception;


    /**
     *  Gets the host attribute of the CascaderMBean object
     *
     *@return    The host value
     */
    public String getHost();


    /**
     *  Sets the host attribute of the CascaderMBean object
     *
     *@param  h              The new host value
     *@exception  Exception  Description of the Exception
     */
    public void setHost(String h) throws Exception;


    /**
     *  Gets the port attribute of the CascaderMBean object
     *
     *@return    The port value
     */
    public int getPort();


    /**
     *  Sets the port attribute of the CascaderMBean object
     *
     *@param  p              The new port value
     *@exception  Exception  Description of the Exception
     */
    public void setPort(int p) throws Exception;


    /**
     *  Gets the resource attribute of the CascaderMBean object
     *
     *@return    The resource value
     */
    public Object getResource();


    /**
     *  Sets the resource attribute of the CascaderMBean object
     *
     *@param  r              The new resource value
     *@exception  Exception  Description of the Exception
     */
    public void setResource(Object r) throws Exception;


    /**
     *  Gets the pattern ObjectName used for filtering MBean names
     *
     *@return    The pattern
     */
    public ObjectName getPattern();


    /**
     *  Sets the pattern attribute of the CascaderMBean object
     *
     *@param  pattern  The new pattern value
     */
    public void setPattern(ObjectName pattern);


    /**
     *  Gets the query used for filtering remote MBeans
     *
     *@return    The query
     */
    public QueryExp getQuery();


    /**
     *  Sets the query attribute of the CascaderMBean object
     *
     *@param  query  The new query value
     */
    public void setQuery(QueryExp query);


    /**
     *  Gets the remoteMBeansCount attribute of the CascaderMBean object
     *
     *@return    The remoteMBeansCount value
     */
    public int getRemoteMBeansCount();


    /**
     *  Gets the remoteMBeans attribute of the CascaderMBean object
     *
     *@return    The remoteMBeans value
     */
    public Set getRemoteMBeans();


    /**
     *  Gets the mBeanServerId attribute of the CascaderMBean object
     *
     *@return    The mBeanServerId value
     */
    public String getMBeanServerId();


    /**
     *  Gets the active attribute of the CascaderMBean object
     *
     *@return    The active value
     */
    public boolean isActive();


    /**
     *  Description of the Method
     */
    public void start();


    /**
     *  Description of the Method
     */
    public void stop();

}
