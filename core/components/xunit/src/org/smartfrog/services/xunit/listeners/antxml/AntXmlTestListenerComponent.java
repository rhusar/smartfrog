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
        return new SingleProcessAntXmlListener(hostname,
                destFile,
                processname,
                suitename,
                start);
    }


}
