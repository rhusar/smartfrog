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

package org.smartfrog.services.cloudfarmer.client.web.model;

import org.apache.struts.upload.FormFile;
import org.smartfrog.SFParse;
import org.smartfrog.sfcore.common.ParseOptionSet;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * This class parses .sf files locally; it is used to load up the CD prior to deployment.
 *
 * Most of the content is null/invalid until an attempt to parse is made
 */

public class LocalSmartFrogDescriptor {

    private ParseOptionSet options = new ParseOptionSet();
    private SFParse.ParseResults parsedDescriptor = new SFParse.ParseResults();

    List<String> errorList = new ArrayList<String>();


    public LocalSmartFrogDescriptor() {
    }

    public SFParse.ParseResults getParsedDescriptor() {
        return parsedDescriptor;
    }

    public void clearOptions() {
        options = new ParseOptionSet();
    }

    /**
     * Get options which can be set on the parse
     *
     * @return
     */
    public ParseOptionSet getOptions() {
        return options;
    }

    /**
     * Test for the parsed descriptor having errors
     *
     * @return true if there were errors
     */
    public boolean hasErrors() {
        return parsedDescriptor.hasErrors();
    }

    /**
     * Parse the text. Low unicode only, please
     *
     * @param text the text to parse
     * @return true if it parsed
     */
    public boolean parseText(String text) {
        parsedDescriptor = SFParse.parseTextToResults(text, null, options);
        return !buildErrorList();
    }

    /**
     * Parse the resource
     *
     * @param resource the resource to parse
     * @return true if it parsed
     */
    public boolean parseResource(String resource) {
        parsedDescriptor = SFParse.parseResourceToResults(resource, null, options);
        return !buildErrorList();
    }

    /**
     * Define from a file. This includes a check that the file is actually there
     *
     * @param srcFile the file to load
     * @return true iff the file parsed without errors
     * @throws FileNotFoundException if the file is missing
     */
    public boolean parseFile(File srcFile) throws FileNotFoundException {
        String filename = srcFile.getAbsolutePath();
        if (!srcFile.exists()) {
            throw new FileNotFoundException(filename);
        }
        parsedDescriptor = SFParse.parseFileToResults(filename, null, options);
        return !buildErrorList();
    }


    /**
     * Parse a form file, using the supplied filename as the filename if it is non null and ends with .sf
     *
     * @param file the form file to parse
     * @return true iff it it parsed without errors
     * @throws IOException on any failure
     */
    public boolean parseFormFile(FormFile file) throws IOException {
        String filename = file.getFileName();
        if (filename == null || !filename.endsWith(".sf")) {
            filename = "uploaded.sf";
        }
        InputStream is = file.getInputStream();
        parsedDescriptor = SFParse.parseInputStreamToResults(filename, is, null, options);
        return !buildErrorList();
    }

    /**
     * Build an error list
     *
     * @return true if there were errors
     */
    private boolean buildErrorList() {
        errorList = new ArrayList<String>();
        if (hasErrors()) {
            for (Vector<String> vector : getErrors()) {
                for (String error : vector) {
                    errorList.add(error);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * get the unflattened errors
     *
     * @return errors
     */
    public Vector<Vector<String>> getErrors() {
        return parsedDescriptor.errors;
    }

    /**
     * Get the flattened error list
     *
     * @return a list of errors, may be empty
     */
    public List<String> getErrorList() {
        return errorList;
    }

    public Vector<String> getReport() {
        return parsedDescriptor.report;
    }

    public Vector<String> getParsed() {
        return parsedDescriptor.parsed;
    }

    public ComponentDescription getComponentDescription() {
        return parsedDescriptor.cd;
    }

    /**
     * Create an exception from errors
     *
     * @return the exception, or null if there is none
     */
    public SmartFrogException createExceptionFromErrors() {
        if (!hasErrors()) {
            return null;
        }
        SmartFrogException sfe = new SmartFrogCompilationException(errorList.get(0).toString());
        return sfe;
    }

    /**
     * Extract and throw an exception if there is one
     * @throws SmartFrogException if something went wrong with the parse
     */
    public void throwParseExceptionIfNeeded() throws SmartFrogException {
        SmartFrogException sfe = createExceptionFromErrors();
        if (sfe != null) {
            throw sfe;
        }
    }

}
