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

import junit.framework.TestCase;
import org.cddlm.client.common.Constants;
import org.cddlm.client.common.ServerBinding;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

/**
 * base class for tests Date: 01-Sep-2004 Time: 10:52:46
 */
public abstract class ConsoleTestBase extends TestCase {

    public static final String PREFIX = "target.";

    public static final String HOST = PREFIX + "hostname";

    public static final String PORT = PREFIX + "port";

    public static final String PATH = PREFIX + "path";

    private ServerBinding binding;

    private StringWriter outputWriter = new StringWriter();

    private PrintWriter out = new PrintWriter(outputWriter, true);

    /**
     * extract info from the JVM, or use defaults, to set up our binding to the
     * world.
     *
     * @throws IOException
     */
    void bind() throws IOException {

        String host = getTestProperty(HOST, Constants.DEFAULT_HOST);
        String path = getTestProperty(PATH, Constants.DEFAULT_PATH);
        String portName = getTestProperty(PORT, null);
        int port = Constants.DEFAULT_SERVICE_PORT;
        if (portName != null) {
            port = Integer.parseInt(portName);
        }
        URL url = new URL(Constants.DEFAULT_PROTOCOL, host, port, path);
        binding = new ServerBinding();
        binding.setUrl(url);
    }

    /**
     * create a server bound to an invalid host
     * @return
     * @throws IOException
     */
    protected ServerBinding bindToInvalidHost()  throws IOException {
        URL url=new URL(Constants.DEFAULT_PROTOCOL,
                "invalid-host",
                Constants.DEFAULT_SERVICE_PORT,
                Constants.DEFAULT_PATH);
        return new ServerBinding(url);
    }

    /**
     * get any test property; these are (currently) extracted from the JVM
     * props
     *
     * @param property
     * @param defaultValue
     * @return
     */
    public static String getTestProperty(String property, String defaultValue) {
        return System.getProperty(property, defaultValue);
    }

    /**
     * get a mandatory property for the test,
     *
     * @param property
     * @return
     * @throws RuntimeException if the property was not found
     */
    public static String getRequiredTestProperty(String property) {
        String result = getTestProperty(property, null);
        if (result == null) {
            throw new RuntimeException("Property " + property + " was not set");
        }
        return result;
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        bind();
    }

    public PrintWriter getOut() {
        return out;
    }

    /**
     * get the output buffer
     *
     * @return
     */
    public String getOutputBuffer() {
        out.flush();
        return outputWriter.getBuffer().toString();
    }

    public ServerBinding getBinding() {
        return binding;
    }

    protected static void assertNotEmpty(String message, String value) {
        assertTrue(message, value != null);
        assertTrue(message, value.length() > 0);
    }

}
