package org.smartfrog.services.groovy.test.system.sources

import org.smartfrog.services.groovy.install.task.GroovyComponentHelper

/**
 * Test in groovy that the helper works
 */
class VFSTest extends GroovyTestCase {
    GroovyComponentHelper helper
    def testdir = ""


    @Override
    protected void setUp() {
        super.setUp()
        String testDir = System.getProperty("test.work.dir")
        helper = new GroovyComponentHelper(testDir, testDir)
    }




    
    void testCopyFileFromHTTP() {
        helper.copy("http://people.apache.org/~rgoers/commons-vfs/apidocs/index.html",
                clean(testdir + "CopyFileFromHTTP/labs.html"))
    }

    void testCopyFileFromFTP() {
        helper.copy("ftp://ftp.cs.princeton.edu/pub/cs126/hello/readme.txt",
                clean(testdir + "CopyFileFromFTP/readme.txt"))
    }

    void NOtestCopyDirectoryFromFTP() {
        helper.copyDir("ftp://ftp.cs.princeton.edu/pub/cs126/hello", clean(testdir + "CopyDirectoryFromFTP"))
    }

    void NOtestExtractDirectoryFromTar() {
        helper.copyDir("tar:ftp://www.mirrorservice.org/sites/ftp.gnu.org/gnu/gzip/gzip-1.2.4.tar!/gzip-1.2.4",
                clean(testdir + "extract"))
    }


    String clean(String filename) {
        File file = new File(filename)
        if (file.isFile()) {
            file.delete()
        } else {
            file.deleteDir()
        }
        return filename;
    }

}

