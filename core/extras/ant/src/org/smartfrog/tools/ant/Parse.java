/** (C) Copyright 1998-2006 Hewlett-Packard Development Company, LP

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
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This task parses smartfrog files and validates them. Errors are thrown when
 * appropriate.
 * Valid files must have an sfConfig node to deploy, and all other files included
 * must be on the classpath -either that used in the task declaration, or inline
 * in this use of the task.
 *
 * A single file can be parsed with the file attribute; setting multiple source
 * filesets causes the program to test them all in turn. That is, every file must be
 * individually valid.
 * @ant.task category="SmartFrog" name="sf-parse"
 * <p/>
 * created 20-Feb-2004 16:17:41
 */

public class Parse extends TaskBase implements SysPropertyAdder {

    /**
     * ini file to read in first
     */
    private File iniFile = null;

    /**
     * log a stack trace
     */
    private boolean logStackTrace = false;

    /**
     * an optional file of parser targets
     */
    private File  parserTargetsFile=null;

    /**
     * verbose flag
     */
    private boolean verbose = false;

    /**
     * extra quiet flag
     */
    private boolean quiet = false;

    /**
     * a list of filesets
     */
    private List<FileSet> source = new LinkedList<FileSet>();

    /**
     * parser subprocess
     */
    private Java parser;
    private static final String ERROR_TOO_MANY_FILES =
        "Cannot have a parserTargetsFile and a fileset of files to parse";

    /**
     * Called by the project to let the task initialize properly. The default
     * implementation is a no-op.
     *
     * @throws BuildException if something goes wrong with the build
     */
    public void init() throws BuildException {
        super.init();
        String entryPoint = SmartFrogJVMProperties.PARSER_ENTRY_POINT;
        parser = createJavaTask(entryPoint);
        parser.setFailonerror(true);
        parser.setFork(true);
    }

    /**
     * name a single file for parsing. Exactly equivalent to a nested fileset
     * with a file attribute
     *
     * @param file file to parse
     */
    public void setFile(File file) {
        if (!file.exists()) {
            throw new BuildException("File not found :" + file.toString());
        }
        FileSet fs = new FileSet();
        fs.setFile(file);
        addSource(fs);
    }

    /**
     * Name a file containing a list of parser targets, one per line.
     * This or a fileset is required
     * @param parserTargetsFile file containing files to parse
     */
    public void setParserTargetsFile(File parserTargetsFile) {
        this.parserTargetsFile = parserTargetsFile;
    }

    /**
     * add a fileset to the list of files to parse
     *
     * @param fs source fileset
     */
    public void addSource(FileSet fs) {
        source.add(fs);
    }

    /**
     * get extra verbose output
     *
     * @param verbose true for verbosity
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * get extra quiet output;
     *
     * @param quiet true for quiet
     */
    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    /**
     * an optional ini file to set custom settings.
     *
     * @param iniFile settings file
     */
    public void setIniFile(File iniFile) {
        this.iniFile = iniFile;
    }

    /**
     * execute the task
     *
     * @throws BuildException
     */
    public void execute() throws BuildException {
        File tempFile=null;
        File targetFile;
        int err;
        try {

            tempFile = FileUtils.getFileUtils().createTempFile("parse",
                ".txt", null);
            int filesCount = buildParserTargetsFile(tempFile);

            if(parserTargetsFile!=null) {
                if(filesCount>0) {
                    throw new BuildException(ERROR_TOO_MANY_FILES);
                } else {
                    targetFile=parserTargetsFile;
                }
            } else {
                targetFile = tempFile;
                // Verify we have something interesting
                if (filesCount == 0) {
                    log("No source files");
                    return;
                }

            }

            //now let's configure the parser
            setupClasspath(parser);
            parser.setFork(true);
            //and add various options to it
            parser.createArg().setValue(SmartFrogJVMProperties.PARSER_OPTION_R);
            if (quiet) {
                parser.createArg().setValue(
                        SmartFrogJVMProperties.PARSER_OPTION_QUIET);
            }
            if (verbose) {
                parser.createArg().setValue(
                        SmartFrogJVMProperties.PARSER_OPTION_VERBOSE);
            }
            parser.createArg().setValue(
                    SmartFrogJVMProperties.PARSER_OPTION_FILENAME);
            parser.createArg().setFile(targetFile);

            //run it
            err = parser.executeJava();
        } finally {
            if(tempFile!=null) {
                tempFile.delete();
            }
        }

        //process the results
        switch (err) {
            case 0:
                //success
                break;
            case 69:
            case 1:
                //parse fail
                throw new BuildException("parse failure");
            default:
                //something else
                throw new BuildException("Java application error code " + err);
        }

    }

    private int buildParserTargetsFile(File tempFile) {
        int filesCount=0;

        List<String> files = new LinkedList<String>();
        for (FileSet set : source) {
            DirectoryScanner scanner = set.getDirectoryScanner(getProject());
            String[] included = scanner.getIncludedFiles();
            for (String anIncluded : included) {
                File parsefile = new File(scanner.getBasedir(), anIncluded);
                log("scanning " + parsefile, Project.MSG_VERBOSE);
                files.add(parsefile.toString());
                filesCount++;
            }
        }
        //at this point the files are all scanned.

        //now save them to a file.
        if (filesCount>0) {
            PrintWriter out = null;
            try {
                out = new PrintWriter(new FileOutputStream(tempFile));
                for (String s : files) {
                    out.println(s);
                }
            } catch (IOException e) {
                throw new BuildException("while saving to " + tempFile, e);
            } finally {
                FileUtils.close(out);
            }
        }
        return filesCount;
    }

    /**
     * add a property
     *
     * @param sysproperty system property
     */
    public void addSysproperty(Environment.Variable sysproperty) {
        parser.addSysproperty(sysproperty);
    }

    /**
     * Adds a set of properties as system properties.
     *
     * @param propset set of properties to add
     */
    public void addSyspropertyset(PropertySet propset) {
        parser.addSyspropertyset(propset);
    }

    /**
     * add a property file to the JVM
     *
     * @param propFile property file
     */
    public void addConfiguredPropertyFile(PropertyFile propFile) {
        propFile.addPropertiesToJvm(this);
    }

}

