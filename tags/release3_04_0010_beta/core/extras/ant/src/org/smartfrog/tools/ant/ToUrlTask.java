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
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * This task takes a file and turns it into a URL, which it then assigns
 * to a property. This is a way of getting file: URLs into an inline
 * SmartFrog deployment descriptor.
 *
 * nested filesets are supported; if present, these are turned into the
 * url with the given separator between them (default = " ").
 * @ant.task category="SmartFrog" name="sf-tourl"
 */

public class ToUrlTask extends Task {

    /**
     * name of the property to set
     */
    private String property;

    /**
     * name of a file to turn into a URL
     */
    private File file;

    /**
     * separator char
     */
    private String separator=" ";

    /**
     * filesets of nested files to add to this url
     */
    private List filesets = new LinkedList();

    /**
     * set the name of a property to fill with the URL
     *
     * @param property
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * the name of a file to be converted into a URL
     *
     * @param file
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * a fileset of jar files to sign
     *
     * @param fileset
     */
    public void addFileSet(FileSet fileset) {
        filesets.add(fileset);
    }

    /**
     * set the separator for the multi-url option.
     * @param separator
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * convert the filesets to urls.
     * @return null for no files
     */
    public String filesetsToURL() {
        if(filesets.isEmpty()) {
            return "";
        }
        int count=0;
        StringBuffer urls=new StringBuffer();
        ListIterator list=filesets.listIterator();
        while (list.hasNext()) {
            FileSet set = (FileSet) list.next();
            DirectoryScanner scanner = set.getDirectoryScanner(getProject());
            String[] files=scanner.getIncludedFiles();
            for(int i=0;i<files.length;i++) {
                File f=new File(scanner.getBasedir(), files[i]);
                String asUrl = toURL(f);
                urls.append(asUrl);
                log(asUrl,Project.MSG_DEBUG);
                urls.append(separator);
                count++;
            }
        }
        //at this point there is one trailing space to remove, if the list is not empty.
        if(count>0) {
            urls.delete(urls.length()-separator.length(),urls.length());
            return new String(urls);
        } else {
            return "";
        }
    }

    /**
     * Create the url
     *
     * @throws org.apache.tools.ant.BuildException
     *          if something goes wrong with the build
     */
    public void execute() throws BuildException {
        validate();
        //now exit here if the property is already set
        if (getProject().getProperty(property) != null) {
            return;
        }
        String url;
        String filesetURL= filesetsToURL();
        if(file!=null) {
            url = toURL(file);
            //and add any files if also defined
            if(filesetURL.length()>0) {
                url=url+separator+filesetURL;
            }
        } else {
            url=filesetURL;
        }
        log("Setting " + property + " to URL " + url, Project.MSG_VERBOSE);
        getProject().setNewProperty(property, url);
    }

    private void validate() {
        //validation
        if (property == null) {
            throw new BuildException("No property defined");
        }
        if (file == null && filesets.isEmpty()) {
            throw new BuildException("No files defined");
        }
    }

    /**
     * convert a file to a URL;
     * @throws BuildException if the file would not convert
     * @param file
     * @return
     */
    private String toURL(File file) {
        String url;
        try {
            //create the URL
            url = file.toURI().toURL().toExternalForm();
            //set the property
        } catch (MalformedURLException e) {
            throw new BuildException("Could not convert " + file, e);
        }
        return url;
    }

    /**
     * convert a string representation of a filename to a URL
     * @param filename
     * @return
     */
    private String toURL(String filename) {
        return toURL(new File(filename));
    }
}
