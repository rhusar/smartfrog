/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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

import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This task takes a list of file resources, gets their file value, strips off any leading prefix you have requested,
 * then saves the result to a property or file. 
 * <p/>
 * It can be used to generate text or CSV files listing all ant resources -such as test files-  
 */

public class ListResources extends Task {


    private String property;
    private File destFile;
    private String fileEncoding = "ISO-8859-1";
    private List<ResourceCollection> resourceCollections = new ArrayList<ResourceCollection>();
    private String startFile = "";
    private String endFile = "";
    private String startEntry = "";
    private String endEntry = "\n";
    private String dirSplitter = "/";
    private String prefixToStrip = "";
    private String suffixToStrip = "";

    public ListResources() {
    }


    public void setProperty(String property) {
        this.property = property;
    }

    public void setDestFile(File destFile) {
        this.destFile = destFile;
    }

    public void setStartFile(String startFile) {
        this.startFile = startFile;
    }

    public void setEndFile(String endFile) {
        this.endFile = endFile;
    }

    public void setEndEntry(String endEntry) {
        this.endEntry = endEntry;
    }

    public void setStartEntry(String startEntry) {
        this.startEntry = startEntry;
    }

    public void setDirSplitter(String dirSplitter) {
        this.dirSplitter = dirSplitter;
    }

    public void setPrefixToStrip(String prefixToStrip) {
        this.prefixToStrip = prefixToStrip;
    }

    public void setSuffixToStrip(String suffixToStrip) {
        this.suffixToStrip = suffixToStrip;
    }

    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    public void add(ResourceCollection collection) {
        resourceCollections.add(collection);
    }

    /**
     * Called by the project to let the task do its work. This method may be called more than once, if the task is
     * invoked more than once. For example, if target1 and target2 both depend on target3, then running "ant target1
     * target2" will run all tasks in target3 twice.
     *
     * @throws BuildException if something goes wrong with the build.
     */
    @SuppressWarnings({"RefusedBequest"})
    @Override
    public void execute() throws BuildException {
        StringBuilder buffer = new StringBuilder();
        buffer.append(startFile);
        for (ResourceCollection collection : resourceCollections) {
            listOneCollection(buffer, collection);
        }
        buffer.append(endFile);
        String result = buffer.toString();
        log(result, Project.MSG_VERBOSE);
        if (property != null) {
            getProject().setNewProperty(property, result);
        }
        OutputStreamWriter writer = null;
        try {
            if (destFile != null) {
                writer = new OutputStreamWriter(new FileOutputStream(destFile), fileEncoding);
                writer.write(result);
            }
        } catch (IOException e) {
            throw new BuildException("Failed to write to " + destFile + " : " + e, e);
        } finally {
            FileUtils.close(writer);
        }
    }

    /**
     * List a single fileset
     *
     * @param buffer     buffer to append to
     * @param collection the fileset to list
     * @throws BuildException on any problem
     */
    private void listOneCollection(StringBuilder buffer, ResourceCollection collection) throws BuildException {
        Iterator entries = collection.iterator();
        while (entries.hasNext()) {
            Resource resource = (Resource) entries.next();
            if (!(resource instanceof FileResource)) {
                throw new BuildException("Not a file resource " + resource);
            }
            FileResource fileResource = (FileResource) resource;
            listOneEntry(buffer, fileResource.getFile());
        }
    }

    /**
     * List a single file
     *
     * @param buffer buffer to append to
     * @param entry  the entry to list
     * @throws BuildException on any problem
     */
    private void listOneEntry(StringBuilder buffer, File entry) {
        buffer.append(startEntry);
        String body;
        String filename = entry.getAbsolutePath();
        int prefixLen = prefixToStrip.length();
        if (prefixLen > 0 && filename.startsWith(prefixToStrip)) {
            body = filename.substring(prefixLen);
        } else {
            body = filename;
        }
        int suffixLen = suffixToStrip.length();
        if (suffixLen > 0 && body.endsWith(suffixToStrip)) {
            body = body.substring(0, body.length() - suffixLen);
        }
        buffer.append(body.replace(File.separator, dirSplitter));
        buffer.append(endEntry);
    }
}
