/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.tools.ant;

import org.apache.tools.ant.BuildException;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.util.List;
import java.util.Iterator;

/**
 * this contains information pointing to the location of code.
 * It can either be a URL or a file path to a Java file.
 */
public class Codebase {

    /**
     * location of a JAR file
     */
    private String location;
    public static final String ERROR_UNDEFINED_CODEBASE = "Undefined codebase";
    public static final String ERROR_FILE_NOT_FOUND = "Not found :";
    public static final String ERROR_NOT_JAR_FILE = "Not a JAR file: ";

    /**
     * the URL of the JAR file
     *
     * @param url codebase URL
     */
    public void setURL(String url) {
        location = url;
    }

    /**
     * provide a URL. This is for the convenience of programmatic access, not
     * ant build files
     *
     * @param url codebase URL
     */
    public void setURL(URL url) {
        location = url.toExternalForm();
    }

    /**
     * name a JAR file for addition to the path
     * The path must be visible to the server process(es) at this location,
     * which means it is either on a shared filestore, or you are only
     * deploying to a local daemon.
     *
     * @param file file to make a url
     */
    public void setFile(File file) {
        if (!file.exists()) {
            throw new BuildException(ERROR_FILE_NOT_FOUND + file);
        }
        if (file.isDirectory()) {
            throw new BuildException(ERROR_NOT_JAR_FILE + file);
        }
        try {
            setURL(file.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new BuildException(e);
        }
    }

    /**
     * get the location
     *
     * @return the URL of the codebase
     */
    public String getLocation() {
        return location;
    }

    /**
     * take a list of codebase elements and then turn them into a string
     *
     * @param codebases the codebase list
     * @return the codebases as space sparated list
     */
    public static String getCodebaseString(List codebases) {
        StringBuffer results = new StringBuffer();
        Iterator it = codebases.iterator();
        while (it.hasNext()) {
            Codebase codebase = (Codebase) it.next();
            String l = codebase.getLocation();
            if (l == null) {
                throw new BuildException(ERROR_UNDEFINED_CODEBASE);
            }
            results.append(l);
            //space separated options here
            if(it.hasNext()) {
                results.append(' ');
            }
        }
        return new String(results);
    }


}
