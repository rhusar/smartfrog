/** (C) Copyright 1998-2009 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.utils;

import org.smartfrog.sfcore.security.SFSecurity;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.common.ParseOptionSet;
import org.smartfrog.sfcore.common.ExitCodes;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.SFSystem;
import org.smartfrog.Version;
import org.smartfrog.SFParse;

import java.io.File;
import java.io.Writer;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;


/**
 * This class lets you expand out a complete .sf file to a new file; takes a source and a dest. It's really designed to
 * be used from Ant, but there are other uses.
 * <p/>
 * It makes use of {@link SFParse} to do the heavy lifting, then calls {@link ComponentDescription#toString()} to
 * generate the output file
 */
public class SFExpandFully {
    private static final String NAME = "SFExpandFully";

    /**
     * Method invoked to parse the component description in SmartFrog system.
     *
     * @param args command line arguments. Please see the usage to get more details
     */
    public static void main(String[] args) {


        try {
            // Initialize Smart Frog Security
            SFSecurity.initSecurity();

            // Read init properties
            SFSystem.readPropertiesFromIniFile();

            // Read logging properties
            Logger.init();

            // Redirect output streams
            SFSystem.setOutputStreams();


            showVersionInfo();
            //create an option set and set some values up
            ParseOptionSet optionSet = new ParseOptionSet(args);
            optionSet.verbose = false;
            optionSet.showConsole = false;
            //enable stack trace logging
            org.smartfrog.sfcore.common.Logger.logStackTrace = true;

            File srcFile;
            File destFile;
            if (args.length != 3) {
                usage();
                ExitCodes.exitWithError(ExitCodes.EXIT_ERROR_CODE_BAD_ARGS);
            }
            String action = args[0];
            String source = args[1];
            destFile = new File(args[2]);
            SFParse.ParseResults results;
            if("file".equals(action)) {
                srcFile = new File(source);
                if(!srcFile.exists()) {
                    SFSystem.sfLog().out("Source file \""+srcFile+ "\" not found");
                    SFSystem.sfLog().out(NAME + ": FAILED");
                    ExitCodes.exitWithError(ExitCodes.EXIT_ERROR_CODE_GENERAL);
                }
                results = SFParse.parseFileToResults(source, null, optionSet);
            } else {
                //parse a resource
                results = SFParse.parseResourceToResults(source, null, optionSet);
            }

            // If we found errors during parsing we force exit.
            if (results.hasErrors()) {
                SFParse.printTotalReport(results.errors);
                SFSystem.sfLog().out(NAME + ": FAILED");
                ExitCodes.exitWithError(ExitCodes.EXIT_ERROR_CODE_GENERAL);
            } else {
                //save the results to a string and then a file
                ComponentDescription description = results.cd;
                saveCDtoFile(description, destFile);
                SFSystem.sfLog().out(NAME + ": SUCCESSFUL");
                ExitCodes.exitWithError(ExitCodes.EXIT_CODE_SUCCESS);

            }
        } catch (Throwable thr) {
            SFSystem.sfLog().error("Exception " + thr, thr);
            ExitCodes.exitWithError(ExitCodes.EXIT_ERROR_CODE_GENERAL);
        }

    }

    /**
     * Utility method to save a CD to a file
     * @param description CD to save
     * @param destFile destfile
     * @throws SmartFrogException something went wrong
     */
    public static void saveCDtoFile(ComponentDescription description, File destFile) throws SmartFrogException {
        StringBuilder buffer = new StringBuilder();
        buffer.append("sfConfig extends {\n");
        buffer.append(description.toString());
        buffer.append("\n}\n");
        saveTextToFile(destFile, buffer.toString());
    }

    /**
     * Save text to a file
     * @param destFile destfile
     * @param text text to save
     * @throws SmartFrogException failure to write the file
     */
    public static void saveTextToFile(File destFile, String text) throws SmartFrogException {
        Writer wout = null;
        try {
            OutputStream fout;
            fout = new FileOutputStream(destFile);
            wout = new OutputStreamWriter(fout, "UTF-8");
            wout.write(text);
            wout.flush();
            wout.close();
        } catch (IOException ioe) {
            if (wout != null) {
                try {
                    wout.close();
                } catch (IOException e) {
                    //ignored
                }
            }
            throw SmartFrogException.forward("When trying to write to " + destFile, ioe);
        }
    }

    /**
     * Shows the version info of the SmartFrog system.
     */
    private static void showVersionInfo() {
        SFSystem.sfLog().out("\n" + NAME + " - " + Version.versionString());
        SFSystem.sfLog().out(Version.copyright());
        SFSystem.sfLog().out(" ");
    }

    /**
     * Shows the version info of the SmartFrog system.
     */
    private static void usage() {
        SFSystem.sfLog().out("\n" + NAME + " - " + Version.versionString());
        SFSystem.sfLog().out("Usage: (file|resource) source destFile");
        SFSystem.sfLog().out(" ");
        SFSystem.sfLog().out("Parses the source file or resource " +
                "and saves the expanded configuration to a destination file");
    }

}
