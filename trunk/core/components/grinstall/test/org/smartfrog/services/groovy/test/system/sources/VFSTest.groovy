package org.smartfrog.services.groovy.test.system.sources

import org.smartfrog.services.groovy.install.task.GroovyComponentHelper

/**
 * Test in groovy that the helper works
 */
class VFSTest extends GroovyTestCase {

    def testdir = "build/test/files/"
    def helper = new GroovyComponentHelper(null)
    
    void testCopyFileFromHTTP() {
        helper.copy("http://people.apache.org/~rgoers/commons-vfs/apidocs/index.html", testdir + "CopyFileFromHTTP/labs.html")
    }

    void testCopyFileFromFTP() {
        helper.copy("ftp://ftp.cs.princeton.edu/pub/cs126/hello/readme.txt", testdir + "CopyFileFromFTP/readme.txt")
    }

    void NOtestCopyDirectoryFromFTP() {
        helper.copyDir("ftp://ftp.cs.princeton.edu/pub/cs126/hello", testdir + "CopyDirectoryFromFTP")
    }

    void NOtestExtractDirectoryFromTar() {
        helper.copyDir("tar:ftp://www.mirrorservice.org/sites/ftp.gnu.org/gnu/gzip/gzip-1.2.4.tar!/gzip-1.2.4", testdir + "extract")
    }



}

