package org.smartfrog.test.unit.sfcore.utils;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;

/**
 * Created 28-Jul-2010 13:14:41
 */

@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class TestLogging extends TestCase {

    private Log log;

    private void print(String text) {
        System.err.println(text);
    }

    private void printProperty(String name) {
        String propval = System.getProperty(name);
        print(name + " = " + propval);
    }

    private URL findResource(String resource) {
        return getClass().getClassLoader().getResource(resource);
    }

    @Override
    protected void setUp() {
        log = LogFactory.getLog(getClass());
    }

    public void testLoggingProperties() throws Throwable {
        printProperty("org.apache.commons.logging.Log");
        printProperty("org.apache.commons.logging.diagnostics.dest");

        printProperty("log4j.configuration");
        printProperty("log4j.debug");
        printProperty("log4j.defaultInitOverride");
        printProperty("log4j.configurationClass");
        printProperty("log4j.ignoreTCL");
        
        printProperty("org.mortbay.log.class");
    }

    public void testLog4Jlocation() throws Throwable {
        printResourceLocation("log4j.properties");
    }

    public void testCommonsLogginglocation() throws Throwable {
        printResourceLocation("commons-logging.properties");
    }

    private void printResourceLocation(String filename) {
        URL url = findResource(filename);
        print(filename + " is at " + url);
    }


    public void testLogInfo() throws Throwable {
        log.info("Logging at info");
    }

    public void testLogError() throws Throwable {
        log.error("Logging at error");
    }

    public void testLogFatal() throws Throwable {
        log.fatal("Logging at fatal");
    }

    public void testLogDebug() throws Throwable {
        log.debug("Logging at debug");
    }

    public void testLogTrace() throws Throwable {
        log.trace("Logging at trace");
    }

    public void testCommonsLoggingImpl() throws Throwable {
        print("Commons logging back end is " + log.getClass());
    }

    public void testLogErrorEnabled() throws Throwable {
        assertTrue(log.isErrorEnabled());
    }

    public void testLogFatalEnabled() throws Throwable {
        assertTrue(log.isFatalEnabled());
    }

    public void testLogInfoEnabled() throws Throwable {
        assertTrue(log.isInfoEnabled());
    }

}
