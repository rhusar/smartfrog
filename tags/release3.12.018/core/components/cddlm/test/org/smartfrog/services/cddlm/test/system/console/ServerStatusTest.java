/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.cddlm.test.system.console;

import org.apache.axis.Constants;
import org.apache.axis.types.URI;
import org.cddlm.client.common.ServerBinding;
import org.cddlm.client.console.ConsoleOperation;
import org.cddlm.client.console.ShowServerStatus;
import org.smartfrog.services.cddlm.generated.api.DeployApiConstants;
import org.smartfrog.services.cddlm.generated.api.types.DynamicServerStatusType;
import org.smartfrog.services.cddlm.generated.api.types.ServerInformationType;
import org.smartfrog.services.cddlm.generated.api.types.ServerStatusType;
import org.smartfrog.services.cddlm.generated.api.types.StaticServerStatusType;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Date: 01-Sep-2004 Time: 11:16:43
 */
public class ServerStatusTest extends ConsoleTestBase {

    private ShowServerStatus operation;

    /**
     * get the operation of this test base
     *
     * @return the current operation
     */
    protected ConsoleOperation getOperation() {
        return operation;
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        operation = new ShowServerStatus(getBinding(), getOut());
    }

    public void testStatus() throws RemoteException {
        operation.execute();
        System.out.println(getOutputBuffer());
    }

    public void testLanguages() throws RemoteException {
        StaticServerStatusType staticStatus = getStaticStatus();
        assertTrue("Languages>0",
                staticStatus.getLanguages().getLanguage().length > 0);
    }

    public void testCallbacks() throws RemoteException {
        StaticServerStatusType staticStatus = getStaticStatus();
        assertTrue("Callbacks>0",
                staticStatus.getNotifications().getItem().length > 0);
    }

    public void testOptions() throws RemoteException {
        StaticServerStatusType staticStatus = getStaticStatus();
        assertTrue("Options>0",
                staticStatus.getOptions().getItem().length > 0);
    }

    public void testSmartFrogLanguage() throws RemoteException {
        assertSupportedLanguage(DeployApiConstants.SMARTFROG_NAMESPACE);
    }

    public void testXMLCDLLanguage() throws RemoteException {
        assertSupportedLanguage(DeployApiConstants.XML_CDL_NAMESPACE);
    }

    public void testUnsupportedLanguage() throws RemoteException {
        String languageURI = DeployApiConstants.XPATH_NAMESPACE;
        boolean found = operation.supportsLanguage(languageURI);
        assertFalse("supported :" + languageURI, found);
    }

    public void assertSupportedLanguage(String languageURI)
            throws RemoteException {
        boolean found = operation.supportsLanguage(languageURI);
        assertTrue("not supported :" + languageURI, found);
    }


    public void testServerInfo() throws RemoteException {
        StaticServerStatusType staticStatus = getStaticStatus();
        ServerInformationType serverInfo = staticStatus.getServer();
        assertNotEmpty("name empty", serverInfo.getName());
        assertNotEmpty("location empty", serverInfo.getLocation());
        assertNotEmpty("build empty", serverInfo.getBuild());
        URI home = serverInfo.getHome();
        assertNotNull("home empty", home);
        assertNotNull("UTC offset", serverInfo.getTimezoneUTCOffset());
    }

    private StaticServerStatusType getStaticStatus() throws RemoteException {
        ServerStatusType status = operation.getStatus();
        StaticServerStatusType staticStatus = status.get_static();
        return staticStatus;
    }

    private DynamicServerStatusType getDynamicStatus() throws RemoteException {
        ServerStatusType status = operation.getStatus();
        return status.getDynamic();
    }

    public void testConsoleErrorsArePrinted() throws IOException {
        operation = new ShowServerStatus(bindToInvalidHost(), getOut());
        executeExpectingOutput(Constants.FAULT_SERVER_USER);
    }

    private void executeExpectingOutput(final String search) {
        String text = execute();
        assertInText(text, search);
    }

    private String execute() {
        operation.doExecute();
        String text = getOutputBuffer();
        return text;
    }

    public void testSuccessIsPrinted() throws IOException {
        String text = execute();
        assertInText(text, "Connecting to");
        assertInText(text, "SmartFrog/1.0");
        assertInText(text, DeployApiConstants.CALLBACK_CDDLM_PROTOTYPE);
    }

    public void testInnerMain() throws IOException {
        boolean flag = ShowServerStatus.innerMain(createBoundArguments());
        assertTrue("failed", flag);
    }

    public void testCommandExtraction() throws IOException {
        ServerBinding binding;
        String[] empty = new String[0];
        assertNull(ServerBinding.fromCommandLine(empty));
        binding = ServerBinding.fromCommandLine(createBoundArguments());
        assertNotNull(binding);
        assertEquals(getBinding(), binding);
    }
}
