/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.deployapi.test.unit;

import org.apache.axis2.addressing.EndpointReference;
import static org.smartfrog.services.deployapi.binding.EprHelper.*;
import org.ggf.xbeans.cddlm.wsrf.wsa2003.EndpointReferenceType;

/**

 */
public class WsaTest extends UnitTestBase {

    public WsaTest(String name) {
        super(name);
    }

    public void testWsaRoundTrip() throws Exception {
        assertRoundTripWorks(EPR_LOCAL_PORTAL);
    }

    public void testWsaJobRoundTrip() throws Exception {
        assertRoundTripWorks(EPR_SAMPLE_JOB);
    }

    private static void assertRoundTripWorks(EndpointReference epr) {
        EndpointReferenceType wsa = EPRToWsa2003(epr);
        EndpointReference back = Wsa2003ToEPR(wsa);
        String text="expected:<"+stringify(epr)+"> but was:<"+stringify(back)+">";
        assertTrue(text,compareEndpoints(epr,back));
    }

    public void testWsa2004RoundTrip() throws Exception {
        assertRoundTripWorks2004(EPR_LOCAL_PORTAL);
    }

    private static void assertRoundTripWorks2004(EndpointReference epr) {
        org.ggf.xbeans.cddlm.wsrf.wsa2004.EndpointReferenceType wsa2004 = EPRToWsa2004(epr);
        EndpointReference back = Wsa2004ToEPR(wsa2004);
        String text = "expected:<" +
                stringify(epr) +
                "> but was:<" +
                stringify(back) +
                ">";
        assertTrue(text, compareEndpoints(epr, back));
    }


}
