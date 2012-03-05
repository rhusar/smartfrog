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

    /**
     * Join a list of anything together into a string, using the defined separator (Which is not appended to the last element)
     * @param elements list elements
     * @param separator string separator
     * @return a combined list
     */
    static String joinList(List elements, String separator) {
        int size = elements.size();
        if (size == 0) return "";
        StringBuilder builder = new StringBuilder()
        elements.eachWithIndex {  elt, index ->
            builder.append(elt.toString());
            if (index < (size - 1)) {
                builder.append(separator)
            }
        }
        return builder.toString()
    }

    /**
     * Find a jar that contains a class of the same name, if any.
     * It will return a jar file, even if that is not the first thing
     * on the class path that has a class with the same name.
     *
     *
     * This is from Hadoop 0.24 and contains the fix for incomplete uuencode plus some 
     * extra error handling
     * @param my_class the class to find.
     * @return a jar file that contains the class, or null.
     * @throws IOException
     */
    public static String findContainingJar(Class my_class) throws IOException {
        ClassLoader loader = my_class.classLoader;
        if (loader == null) {
            throw new IOException("Class $my_class does not have a classloader!")
        }
        assert loader != null
        assert my_class != null
        String class_file = my_class.name.replaceAll("\\.", "/") + ".class";
        Enumeration<URL> urlEnumeration = loader.getResources(class_file)
        assert urlEnumeration != null

        for (Enumeration itr = urlEnumeration;
        itr.hasMoreElements();) {
            URL url = (URL) itr.nextElement();
            if ("jar".equals(url.protocol)) {
                String toReturn = url.path;
                if (toReturn.startsWith("file:")) {
                    toReturn = toReturn.substring("file:".length());
                }
                // URLDecoder is a misnamed class, since it actually decodes
                // x-www-form-urlencoded MIME type rather than actual
                // URL encoding (which the file path has). Therefore it would
                // decode +s to ' 's which is incorrect (spaces are actually
                // either unencoded or encoded as "%20"). Replace +s first, so
                // that they are kept sacred during the decoding process.
                toReturn = toReturn.replaceAll("\\+", "%2B");
                toReturn = URLDecoder.decode(toReturn, "UTF-8");
                return toReturn.replaceAll("!.*\$", "");
            }
        }
        return null;
    }

}
