/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.xunit.listeners.html;

import org.smartfrog.services.xunit.listeners.xml.AbstractXmlListenerComponent;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.util.Date;

/**
 * This is a listener of tests
 * Implement the {@link org.smartfrog.services.xunit.listeners.xml.XmlListenerFactory} interface and so provide a component
 * for XML logging. Note that we are only a factory; the listening is done by
 * {@link org.smartfrog.services.xunit.listeners.html.OneHostXMLListener }
 */
public class HtmlTestListenerComponent extends AbstractXmlListenerComponent
        implements HtmlTestListenerFactory {

    private Log log;
    private String cssURL;
    private String cssResource;
    private String cssData;
    ComponentHelper helper;

    /**
     * construct a base interface
     *
     * @throws RemoteException
     */
    public HtmlTestListenerComponent() throws RemoteException {
        helper=new ComponentHelper(this);
    }


    /**
     * {@inheritDoc}
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        cssURL=sfResolve(ATTR_CSS_URL,cssURL,false);
        cssResource=sfResolve(ATTR_CSS_RESOURCE,cssResource,false);
        if(cssResource!=null) {
            //load in the data from the class
            cssData=helper.loadResourceToString(cssResource, Charset.forName("UTF-8"));
        }
        cssData= sfResolve(ATTR_CSS_DATA, cssData, false);

    }

    /**
     * {@inheritDoc}
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    protected OneHostXMLListener createNewSingleHostListener(String hostname,
                                                             File destFile,
                                                             String processname, 
                                                             String suitename,
                                                             Date start) throws
            IOException {
        return new OneHostHtmlListener("Test",
                hostname, 
                processname,
                destFile,
                suitename,
                start,
                preamble,
                cssURL,
                cssData);
        
    }


    /**
     * {@inheritDoc}
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        log = helper.getLogger();
    }



}
