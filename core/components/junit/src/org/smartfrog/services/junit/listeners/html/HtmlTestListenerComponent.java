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
package org.smartfrog.services.junit.listeners.html;

import org.smartfrog.services.junit.listeners.xml.AbstractXmlListenerComponent;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.util.Date;

/**
 * This is a listener of tests
 * Implement the {@link org.smartfrog.services.junit.listeners.xml.XmlListenerFactory} interface and so provide a component
 * for XML logging. Note that we are only a factory; the listening is done by
 * {@link org.smartfrog.services.junit.listeners.html.OneHostXMLListener }
 */
public class HtmlTestListenerComponent extends AbstractXmlListenerComponent
        implements HtmlTestListenerFactory {

    private String cssURL;
    private String cssResource;
    private String cssData;
    ComponentHelper helper;

    /**
     * construct a base interface
     *
     * @throws java.rmi.RemoteException
     */
    public HtmlTestListenerComponent() throws RemoteException {
        helper=new ComponentHelper(this);
    }


    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
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
     * Override point; create a new XML listener
     *
     * @param hostname
     * @param destFile
     * @param processname
     * @param suitename
     * @param start
     *
     * @return
     *
     * @throws IOException
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
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        log = helper.getLogger();
    }



}
