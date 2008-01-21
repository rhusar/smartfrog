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
package org.smartfrog.test.unit.projects.alpine.wsa;

import junit.framework.TestCase;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.wsa.AddressDetails;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;

/**
 * created 12-Apr-2006 11:23:43
 */

public abstract class AddressingTestBase extends TestCase {
    protected AddressDetails details;
    public static final String ACTION = "action";
    public static final String TO_URL = "http://example.org/ex?name1=value1&name2=value2";
    protected AlpineEPR epr;
    protected SoapElement referenceParameters;
    protected MessageContext messageContext;
    protected final AlpineEPR emptyEPR = new AlpineEPR();

    /**
     * Constructs a test case with the given name.
     */
    protected AddressingTestBase(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        details = new AddressDetails();
        epr = new AlpineEPR(TO_URL);
        final SoapElement metadata = new SoapElement("metadata", TO_URL, "metadata-value");
        epr.setMetadata(metadata);
        referenceParameters = new SoapElement("param", TO_URL, "param-value");
        epr.setReferenceParameters(referenceParameters);
        details.setTo(epr);
        details.setAction(ACTION);
        details.setMessageID("1");
        details.setRelatesTo("junit");
        details.validate();
        messageContext = new MessageContext();
    }
}
