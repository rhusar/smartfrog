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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.File;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This task parses smartfrog files and validates them. Errors are thrown when appropriate
 * @author steve loughran
 *         created 20-Feb-2004 16:17:41
 */

public class Parse extends Task {

    /**
     * ini file to read in first
     */
    private File iniFile=null;

    /**
     * log a stack trace
     */
    private boolean logStackTrace=false;


    private Path classpath;

    private Reference classpathRef;

    private boolean verbose=false;

    private boolean quiet=false;

    /**
     *  a list of filesets
     */
    private List source = new LinkedList();


    /**
     * add a fileset to the list of files to parse
     *
     * @param fs
     */
    public void addSource(FileSet fs) {
        source.add(fs);
    }

    /**
     * the classpath to run the parser
     * @param classpath
     */
    public void setClasspath(Path classpath) {
        this.classpath = classpath;
    }

    /**
     * a reference to the classpath to use to run smartfrog
     * @param classpathRef
     */
    public void setClasspathRef(Reference classpathRef) {
        this.classpathRef = classpathRef;
    }

    /**
     * get extra verbose output
     * @param verbose
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * get extra quiet output;
     * @param quiet
     */
    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    /**
     * an optional ini file to set custom settings.
     * @param iniFile
     */
    public void setIniFile(File iniFile) {
        this.iniFile = iniFile;
    }

    /**
     * execute the task
     * @throws BuildException
     */
    public void execute() throws BuildException {

        List files=new LinkedList();
        Iterator src=source.iterator();
        while (src.hasNext()) {
            FileSet set = (FileSet) src.next();
            DirectoryScanner scanner = set.getDirectoryScanner(getProject());
            String[] included=scanner.getIncludedFiles();
            for(int i=0;i<included.length;i++) {
                log("scanning "+ included[i],Project.MSG_VERBOSE);
                files.add(included[i]);
            }
        }
        //at this point the files are all scanned. Verify we have something interesting
        if(files.isEmpty()) {
            log("No source files");
            return;
        }

        //now save them to a file.
        File tempFile=FileUtils.newFileUtils().createTempFile("sfp","txt",null);
        PrintWriter out=null;
        try {
            out = new PrintWriter(new FileOutputStream(tempFile));
            src=files.iterator();
            while (src.hasNext()) {
                String s = (String) src.next();
                out.println(s);
            }
        } catch (IOException e) {
            throw new BuildException("while saving to "+tempFile,e);
        } finally {
            if(out!=null) {
                try {
                    out.close();
                } catch (Exception swallowed) {

                }
            }
        }

        //now lets create the Java statement
        Java java=(Java)getProject().createTask("java");
        java.setFailonerror(true);
        java.setFork(true);
        java.setClassname("org.smartfrog.sfParse");
        if(classpath!=null) {
            java.setClasspath(classpath);
        }
        if(classpathRef!=null) {
            java.setClasspathRef(classpathRef);
        }
        //and add various options to it
        java.createArg().setValue("-r");
        if(quiet) {
            java.createArg().setValue("-q");
        }
        if(verbose ) {
            java.createArg().setValue("-v");
        }
        java.createArg().setValue("-f");
        java.createArg().setFile(tempFile);

        //run it
        int err=java.executeJava();

        //process the results
        switch(err) {
            case 0:
                //success
                break;
            case -1:
                //parse fail
                throw new BuildException("parse failure");
            default:
                //something else
                throw new BuildException("Spawning Java application return error code "+err);
        }

    }
}
