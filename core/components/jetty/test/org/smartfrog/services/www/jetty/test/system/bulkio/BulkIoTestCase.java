package org.smartfrog.services.www.jetty.test.system.bulkio;

import org.smartfrog.services.www.jetty.test.system.JettyTestBase;

public abstract class BulkIoTestCase extends JettyTestBase {

    public static final String BULKIO = "/org/smartfrog/services/www/jetty/test/system/bulkio/";
    public static final String TEST_BULKIO_SIZE = "test.bulkio.size";

    public static final long MB = 1;
    public static final long GB = 1024L * MB;
    public static long SMALL = 8 * MB;
    public static long MEDIUM = 128 * MB;
    public static long LARGE = 1 * GB;
    public static long VERY_LARGE = 5L * GB;

    public BulkIoTestCase(String name) {
        super(name);
    }

    public void expectBulkIoRun(String filename, long size) throws Throwable {
        System.setProperty(TEST_BULKIO_SIZE, Long.toString(size));
        expectSuccessfulTestRun(BULKIO, filename);
    }

    protected abstract String getFile();

    public void testBulkIoSmall() throws Throwable {
        expectBulkIoRun(getFile(), SMALL);
    }

    public void testBulkIoMedium() throws Throwable {
        expectBulkIoRun(getFile(), MEDIUM);
    }

    public void testBulkIoLarge() throws Throwable {
        expectBulkIoRun(getFile(), LARGE);
    }

    public void testBulkIoVeryLarge() throws Throwable {
        expectBulkIoRun(getFile(), VERY_LARGE);
    }


}