/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.filesystem.append;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.services.filesystem.FileUsingComponentImpl;
import org.smartfrog.services.filesystem.FileUsingComponent;
import org.smartfrog.services.filesystem.FileSystem;

import java.rmi.RemoteException;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.LineNumberReader;
import java.io.FileReader;

/**
 *
 * Created 24-Apr-2008 15:53:58
 *
 */

public class AppendLinesImpl extends FileUsingComponentImpl implements FileUsingComponent {
    /**
     *   //the list of lines to add
     lines [];
     //the line separator defaults to being platform specific
     lineEnding LAZY PROPERTY line.separator;
     //append those lines that are not in the application?
     addOnlyMissingLines true;
     //updated at runtime to the final number
     linesAdded 0;
     */

    int linesAdded=0;
    private String lineEnding, encoding;
    private boolean addOnlyMissingLines;
    private Vector<String> lines;
    private static final Reference REF_LINES = new Reference("lines");
    private static final Reference REF_MISSING_LINES_ONLY = new Reference("addOnlyMissingLines");
    private static final Reference REF_LINE_ENDING = new Reference("lineEnding");
    private static final Reference REF_ENCODING = new Reference("encoding");

    /**
     * create a new line appender
     * @throws RemoteException from the superclass
     */
    public AppendLinesImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        bind(true,"");
        addOnlyMissingLines=sfResolve(REF_MISSING_LINES_ONLY,true,true);
        lineEnding = sfResolve(REF_LINE_ENDING, "", true);
        encoding = sfResolve(REF_ENCODING, "", true);
        lines= ListUtils.resolveStringList(this, REF_LINES,true);
        appendLines();
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,null,null,null);
    }

    /**
     * Do the work
     * @throws SmartFrogDeploymentException for security and other operational problems, including
     * wrapped IOExceptions
     */
    private void appendLines() throws SmartFrogDeploymentException {
        try {
            if(!getFile().exists()) {
                buildNewFile();
            } else {
                appendExistingFile();
            }
        } catch (IOException e) {
            throw new SmartFrogDeploymentException("When writing to "+getFile(),e,this);
        }
    }

    /**
     * Append lines to an existing file. Filters out existing lines and then
     * only appends new lines; writes nothing if there are no new lines to add.
     * @throws IOException for IO problems
     */
    private void appendExistingFile() throws IOException {
        Vector<String> linesToAdd = addOnlyMissingLines?filterExistingLines():lines;
        if(linesToAdd.size()>0) {
            appendLines(new FileOutputStream(getFile(),true), linesToAdd);
            return;
        }
    }

    /**
     * Filter out the list of lines to add by the existing line list
     * @return a list of lines that are not in the file
     * @throws IOException for IO problems
     */
    private Vector<String> filterExistingLines() throws IOException {
        Set<String> existingLines=loadExistingLines();
        Vector<String> linesToAdd=new Vector<String>(lines.size());
        for (String line : lines) {
            if(!existingLines.contains(line)) {
                linesToAdd.add(line);
            }
        }
        return linesToAdd;
    }


    /**
     * Load the existing lines in a file and return them as a vector
     * @return a set of all the loaded lines
     * @throws IOException for IO problems
     */
    private Set<String> loadExistingLines() throws IOException {
        LineNumberReader reader=null;
        try {
            reader = new LineNumberReader(new FileReader(getFile()));
            Set<String> result=new HashSet<String>();
            String line;
            while((line=reader.readLine())!=null) {
                result.add(line);
            }
            return result;
        } finally {
            FileSystem.close(reader);
        }

    }

    /**
     * Build a new file
     * @throws SmartFrogException
     * @throws RemoteException
     * @throws IOException
     */
    private void buildNewFile() throws SmartFrogDeploymentException, IOException {
        File destFile = getFile();

        //handle the parent directory
        File parentFile = destFile.getParentFile();
        if(parentFile!=null && !parentFile.exists()) {
            try {
                parentFile.mkdirs();
            } catch (SecurityException e) {
                throw new SmartFrogDeploymentException("Unable to create "+parentFile,e,this);
            }
        }
        appendLines(new FileOutputStream(destFile), lines);
    }

    /**
     * Append one or more lines to the output stream. If the number of lines to add is 0, an empty write still takes place
     * so creating a 0 byte file.
     * @param stream stream to work on (this is closed at the end)
     * @param linesToAdd lines to add
     * @throws IOException for io problems.
     */
    private void appendLines(FileOutputStream stream, Vector<String> linesToAdd) throws IOException {
        Writer out=null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(stream,encoding));
            for(String line: linesToAdd) {
                out.write(line);
                out.write(lineEnding);
            }
            out.close();
            out=null;
        } finally {
            FileSystem.close(out);
        }
    }
}
