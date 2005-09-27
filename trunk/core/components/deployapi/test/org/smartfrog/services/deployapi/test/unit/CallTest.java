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

import org.smartfrog.services.deployapi.client.ConsoleOperation;
import org.smartfrog.services.deployapi.client.PortalEndpointer;
import org.smartfrog.services.deployapi.client.ApiCall;
import org.smartfrog.services.deployapi.client.SystemEndpointer;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.test.system.ApiTestBase;
import org.apache.axis2.description.OperationDescription;

import java.rmi.RemoteException;

/**
 *this unit test looks at our client impl classes
 */
public class CallTest extends ApiTestBase {

    public CallTest(String name) {
        super(name);
    }


    public void testPortalEndpoint() throws Exception {
        ApiCall call = createPortalCall();
        assertSupportsPortal(call);
    }
    
    public void testSystemEndpoint() throws Exception{
        ApiCall call=createSystemCall();
        assertSupportsService(call);
    }

    /**
     * verify that intermixed construction does corrupt static things
     * @throws Exception
     */
    public void testMixedConstruction() throws Exception {
        ApiCall call = createPortalCall();
        assertSupportsPortal(call);
        call = createSystemCall();
        assertSupportsService(call);
        call = createPortalCall();
        assertSupportsPortal(call);
        call = createSystemCall();
        assertSupportsService(call);
    }

    private void assertSupportsService(ApiCall call) {
        assertHasOperation(call, Constants.API_SYSTEM_OPERATION_INITIALIZE);
        assertHasOperation(call, Constants.API_SYSTEM_OPERATION_ADDFILE);
        assertHasOperation(call, Constants.API_SYSTEM_OPERATION_PING);
        assertHasOperation(call, Constants.API_SYSTEM_OPERATION_RESOLVE);
        assertHasOperation(call, Constants.API_SYSTEM_OPERATION_RUN);
        assertHasOperation(call, Constants.API_SYSTEM_OPERATION_TERMINATE);
        assertSupportsWSRF(call);
        assertSupportsWSNT(call);
    }

    private ApiCall createPortalCall() throws RemoteException {
        ConsoleOperation operation = getOperation();
        PortalEndpointer portal = operation.getPortal();
        ApiCall call = portal.createStub(Constants.API_PORTAL_OPERATION_CREATE);
        return call;
    }

    private ApiCall createSystemCall() throws RemoteException {
        ConsoleOperation operation = getOperation();
        SystemEndpointer systemEndpointer=new SystemEndpointer(EPR_SAMPLE_JOB,"123");
        ApiCall call = systemEndpointer.createStub(Constants.API_SYSTEM_OPERATION_INITIALIZE);
        return call;
    }    

    private void assertSupportsPortal(ApiCall call) {
        assertHasOperation(call, Constants.API_PORTAL_OPERATION_CREATE);
        assertHasOperation(call, Constants.API_PORTAL_OPERATION_LOOKUPSYSTEM);
        assertHasOperation(call, Constants.API_PORTAL_OPERATION_RESOLVE);
        assertSupportsWSRF(call);
        assertSupportsWSNT(call);
    }

    



    private void assertSupportsWSNT(ApiCall call) {
        assertHasOperation(call, Constants.WSRF_OPERATION_GETCURRENTMESSAGE);
        assertHasOperation(call, Constants.WSRF_OPERATION_SUBSCRIBE);
    }

    private void assertSupportsWSRF(ApiCall call) {
        assertHasOperation(call, Constants.WSRF_OPERATION_GETRESOURCEPROPERTY);
        assertHasOperation(call, Constants.WSRF_OPERATION_GETMULTIPLERESOURCEPROPERTIES);
    }

    public void assertHasOperation(ApiCall call, String name) {
        OperationDescription description = call.lookupOperation(name);
        assertNotNull("No operation:"+name);
    }


}
