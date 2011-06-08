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

package org.smartfrog.sfcore.common;

import org.smartfrog.SFParse;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.NullIncludeHandler;
import org.smartfrog.sfcore.languages.sf.SFParser;
import org.smartfrog.sfcore.parser.Phases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * This class parses .sf files locally; it is used to load up a {@link ComponentDescription}, with the option of turning
 * errors into an exception. <p/>
 *
 * Most of the content is null/invalid until an attempt to parse is made
 */

public class LocalSmartFrogDescriptor {

    private ParseOptionSet options = new ParseOptionSet();
    private SFParse.ParseResults parsedDescriptor = new SFParse.ParseResults();

    private List<String> errorList = new ArrayList<String>();

    public LocalSmartFrogDescriptor() {
    }

    public SFParse.ParseResults getParsedDescriptor() {
        return parsedDescriptor;
    }

    public void setParsedDescriptor(SFParse.ParseResults parsedDescriptor) {
        this.parsedDescriptor = parsedDescriptor;
    }

    public void clearOptions() {
        options = new ParseOptionSet();
    }

    /**
     * Get options which can be set on the parse
     *
     * @return the options
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
     * Parse from an input stream; parse in a non-empty filename
     *
     * @param filename filename
     * @param is       input stream
     * @return true iff there were no errors
     */
    public boolean parseFromInputStream(String filename,
                                        InputStream is) {
        setParsedDescriptor(SFParse.parseInputStreamToResults(filename, is, null,
                getOptions()));
        return !buildErrorList();
    }

    public ComponentDescription parseWithoutPhasesOrIncludes(Reader is) throws SmartFrogCompilationException {
        Phases phases = new SFParser().sfParse(is, new NullIncludeHandler());
        return phases.sfAsComponentDescription();
    }

    /**
     * Build an error list
     *
     * @return true if there were errors
     */
    private boolean buildErrorList() {
        errorList = new ArrayList<String>();
        if (hasErrors()) {
            for (Iterable<String> vector : getErrors()) {
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
    public SmartFrogCompilationException createExceptionFromErrors() {
        return createExceptionFromErrors("", null);
    }

    /**
     * Create an exception from errors
     * @param message any message before the parser error
     * @param source to include, the sf Description is handy. Can be null
     * @return the exception, or null if there is none
     */
    public SmartFrogCompilationException createExceptionFromErrors(String message, String source) {
        if (!hasErrors()) {
            return null;
        }
        return new SmartFrogCompilationException(message + " "
                + errorList.get(0)
                + ((source == null) ? "" : ("\n" + source)));
    }


    /**
     * Extract and throw an exception if there is one
     *
     * @throws SmartFrogCompilationException if something went wrong with the parse
     */
    public void throwParseExceptionIfNeeded() throws SmartFrogCompilationException {
        SmartFrogCompilationException sfe = createExceptionFromErrors();
        if (sfe != null) {
            throw sfe;
        }
    }

}
