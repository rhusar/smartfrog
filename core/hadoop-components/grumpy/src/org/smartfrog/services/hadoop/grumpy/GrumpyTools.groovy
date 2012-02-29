package org.smartfrog.services.hadoop.grumpy

import groovy.util.logging.Commons
import org.apache.commons.logging.Log

@Commons
class GrumpyTools {

    static int dumpDir(Log dumpLog, File dir) {
        if (!dir.exists()) {
            dumpLog.warn("Not found: ${dir}");
            return -1;
        }
        if (!dir.isDirectory()) {
            dumpLog.warn("Not a directory: ${dir}");
            return -1;
        }
        int count = 0;
        dir.eachFile { file ->
            count++
            dumpFile(dumpLog, file)
        }
        return count;
    }

    static dumpFile(Log dumpLog, File file) {
        dumpLog.info("File : ${file} of size ${file.length()}")
    }

    static String convertToUrl(File file) {
        return file.toURI().toString();
    }

    /**
     * Clean up the output directory
     * @param dir
     * @return
     */
    static def deleteDirectoryTree(File dir) {
        if (dir.exists()) {
            if (dir.isDirectory()) {
                log.info("Cleaning up " + dir)
                //delete the children
                dir.eachFile { file ->
                    log.info("deleting " + file)
                    file.delete()
                }
                dir.delete()
            } else {
                throw new IOException("Not a directory: ${dir}")
            }
        } else {
            //not found, do nothing
            log.debug("No output dir yet")
        }
    }
}
