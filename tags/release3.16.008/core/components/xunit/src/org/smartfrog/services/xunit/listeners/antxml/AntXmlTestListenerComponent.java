/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

*/

package org.smartfrog.services.xunit.listeners.antxml;

import org.smartfrog.services.xunit.listeners.xml.AbstractXmlListenerComponent;
import org.smartfrog.services.xunit.listeners.xml.FileListener;
import org.smartfrog.services.xunit.listeners.xml.XmlListenerFactory;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;
/*
 * 
 * We want to create something like 
 * <testsuite errors="0" failures="0" hostname="k2" 
    name="org.smartfrog.something.Test" 
    tests="2" time="0.176" timestamp="2007-12-09T19:03:05">
  <properties>
    <property name="env.DESKTOP_SESSION" value="gnome" />
    <property name="file.encoding.pkg" value="sun.io" />
  </properties>
   <testcase classname="org.apache.tools.ant.CaseTest" name="testCaseSensitivity" time="0.146" />
  <testcase classname="org.apache.tools.ant.CaseTest" name="testTaskCase" time="0.011" />
  <system-out><![CDATA[]]></system-out>
  <system-err><![CDATA[]]></system-err>
 * 
 */

/**
 * This is a listener of tests
 * Implement the {@link XmlListenerFactory} interface and so provide a component
 * for XML logging. Note that we are only a factory
 * 
 */

public class AntXmlTestListenerComponent
        extends AbstractXmlListenerComponent implements XmlListenerFactory {

    /**
     * construct a base interface
     *
     * @throws RemoteException as its parent can
     */
    public AntXmlTestListenerComponent() throws RemoteException {
    }


    /**
     * {@inheritDoc}
     * @throws IOException for problems
     */
    protected FileListener createNewSingleHostListener(String hostname,
                                                      File destFile,
                                                      String processname,
                                                      String suitename,
                                                      Date start)
            throws IOException {
        return new AntXmlListener(hostname,
                destFile,
                processname,
                suitename,
                start);
    }


}
