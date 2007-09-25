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

package org.smartfrog.sfcore.common;

import java.util.Vector;

import org.smartfrog.Version;
import java.io.LineNumberReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.smartfrog.SFSystem;


/**
 * Parses the SFSystem arguments into an option set. Options are seperated by
 * optionFlagIndicator characters.
 *
 */
public class OptionSet {

    /** Character indicating the start of each option. */
    private static final char optionFlagIndicator = '-';


    /** The name of the option string for headless operation: "{@value}" */
    public static final String OPTION_HEADLESS = "-headless";

    /** The name of the option string for quiet exits: "{@value}" */
    public static final String OPTION_QUIETEXIT = "-quietexit";

    /** Usage string for SFSystem. */
    private static final String USAGE = "\n" +
        " Usage: SFSystem [-a SFACT] [-f SFREF] [-t] [-e] [-d] [-headless]\n" +
        "    or: SFSystem -?\n";

    /** Help string for SFSystem. */
    private static final String HELP = "\n" + Version.copyright() + " - v." +
        Version.versionString()+" - build: "+ Version.buildDate()+ "\n" + " Parameters: " + "\n" +
        "    -a SFACT: SmartFrog Action Descriptor (SFACT),\n"+
        "              which is used to indicate to SmartFrog an action to take.\n" +
        "       ex. Deploy a description - " +  "\n" +
        "           -a counterEx:DEPLOY:org/.../example.sf::localhost:process" + "\n" +
        "       ex. Terminate local sfDaemon - " + "\n" +
        "           -a rootProcess:TERMINATE:::localhost:" + "\n" +
        "\n" +
        "       Format for SFACT: " +"NAME:ACTION:SFREF:SUBREF:HOST:PROCESS\n" +
        "           - NAME: name used by ACTION\n" +
        "              ex. foo\n" +
        "              ex. \"HOST localhost:foo\"\n" +
         "             ex. 'HOST localhost:foo'\n" +
        "          - ACTION: defines the applied action on the named component\n"+
        "                    possible actions: DEPLOY, UPDATE, TERMINATE, DETACH, DETaTERM, PING, PARSE, DIAGNOSTICS, DUMP\n" +
        "          - SFREF: SmartFrog description (if needed) used by ACTION\n" +
        "                   Currently only required by DEPLOY\n"+
        "              ex. /home/sf/foo.sf\n" +
        "              ex. \"c:\\sf\\foo.sf\"\n" +
        "              ex. 'c:\\sf\\foo.sf'\n" +
        "          - SUBREF: component description name used by ACTION. It can be empty\n" +
        "                   Currently only required by DEPLOY\n"+
        "              ex: foo\n" +
        "              ex: \"fist:foo\"\n" +
        "              ex: 'fist:foo'\n" +
        "              note: sfConfig cannot be used with DEPLOY!\n" +
        "          - HOST: host name or IP from where to resolve NAME. It can be empty.\n" +
        "              ex: localhost\n" +
        "              ex: 127.0.0.1\n" +
        "              ex (multiple hosts): [127.0.0.1,\"localhost\"]\n" +
        "          - PROCESS: process name from where to resolve NAME. When empty it assumes rootProcess.\n" +
        "          \n" +
        "          SFACT Examples:\n" +
        "              ex1: Deploy a description in local daemon\n" +
        "                   counterEx:DEPLOY:org/smartfrog/examples/counter/example.sf::localhost:\n" +
        "              ex2. Terminate local sfDaemon\n" +
        "                   rootProcess:TERMINATE:::localhost:\n" +
        "              ex3: Deploy \"counterToSucceed\" from counter/example2.sf\n" +
        "                   counterEx3:DEPLOY:org/smartfrog/examples/counter/example2.sf:\"testLevel1:counterToSucceed\":localhost:\n" +
        "              ex4: Get diagnostics report for \"sfDefault\" component running in remote daemon\n" +
        "                   'rootProcess:sfDefault':DIAGNOSTICS:::remoteHostName:\n" +
        "              ex5. Dump description for local sfDaemon\n" +
        "                   rootProcess:DUMP:::localhost:\n" +
        "\n" +
        "    -f SFREF: file with a set of SmartFrog Action Descriptors (SFACT)" +"\n" +
        "    -t (terminate): Terminate successfull deployments if one of the listed (with -a or -f) deployments failed." + "\n" +
        "    -e (exit): The daemon will terminate after finishing the deployment." + "\n" +
        "    -d or -diagnostics: print information that might be helpful to diagnose or report problems." + "\n" +
        "   " + OPTION_HEADLESS + ": the process will run in headless mode\n" +
        "   " + OPTION_QUIETEXIT + ":  do not set any exit code when the program exits with an error\n"+
        "   -? or -help:  print this help information\n";

    /** Error string for SFSystem. */
    public String errorString = null;

    /** ExitCode for SFSystem. */
    public int exitCode = ExitCodes.EXIT_ERROR_CODE_BAD_ARGS;


    /** Hostname where the description is to be deployed. */
    public String host = null;

    /** Processname where the description is to be deployed. */
    public String subprocess = null;


    /** Vector for configurationDescriptors to be deployed. */
    public Vector cfgDescriptors = new Vector();

   /** Terminate sucessful deployments in case of a deployment failure. */
    public boolean terminateOnDeploymentFailure = false;

    /** Flag indicating the exit status of the application. */
    public boolean exit = false;

    /** Flag indicating if diagnostics was requested. */
    public boolean diagnostics = false;

    /**
     * was headless operation requested
     */
    public boolean headless = false;

    /**
     * was -quietexit requested
     */
    public boolean noExitCode = false;

    /**
     * Creates an OptionSet from an array of arguments.
     *
     * @param args arguments to create from
     */
    public OptionSet(String[] args) {
        int i = 0;

        while ((i < args.length) && (errorString == null)) {
            try {
                String currentArg = args[i];
                if("-a".equals(currentArg)) {
                    //deploy an application
                    try {
                        this.cfgDescriptors.add(new ConfigurationDescriptor(args[++i]));
                    } catch (SmartFrogInitException ex) {
                        exitCode = ExitCodes.EXIT_ERROR_CODE_BAD_ARGS;
                        //Logger.log(ex);
                        if (SFSystem.sfLog().isErrorEnabled()) {
                            SFSystem.sfLog().error(ex.getMessage(), ex);
                        }
                    }
                } else if ("-f".equals(currentArg)) {
                    try {
                        cfgDescriptors = readCfgDescriptorsFile(args[++i]);
                    } catch (SmartFrogInitException ex) {
                        exitCode = ExitCodes.EXIT_ERROR_CODE_BAD_ARGS;
                        //Logger.log(ex);
                        if (SFSystem.sfLog().isErrorEnabled()) {
                            SFSystem.sfLog().error(ex.getMessage(), ex);
                        }
                    }
                } else if ("-d".equals(currentArg) || "-diagnostics".equals(currentArg)) {
                    //diagnostics
                    diagnostics = true;
                } else if ("-t".equals(currentArg)) {
                    this.terminateOnDeploymentFailure = true;
                } else if ("-e".equals(currentArg)) {
                    //exit after the operation(s)
                    exit = true;
                } else if (OPTION_HEADLESS.equals(currentArg)) {
                    //headless mode
                    headless = true;
                } else if (OPTION_QUIETEXIT.equals(currentArg)) {
                    //quiet exit requested
                    noExitCode = true;
                } else if ("-?".equals(currentArg) || "-help".equals(currentArg)
                || "--help".equals(currentArg)) {
                    //help string
                    errorString = "SFSystem help" + HELP;
                    exitCode = ExitCodes.EXIT_CODE_SUCCESS;
                } else if (currentArg.charAt(0) == '-') {
                    errorString = "unknown option " + currentArg.charAt(1);
                    exitCode = ExitCodes.EXIT_ERROR_CODE_BAD_ARGS;
                } else {
                    errorString = "illegal option format for option " + currentArg;
                    exitCode = ExitCodes.EXIT_ERROR_CODE_BAD_ARGS;
                }
                i++;
            } catch (Exception e) {
                exitCode = ExitCodes.EXIT_ERROR_CODE_BAD_ARGS;
                //protects from dodgy shell scripts
                if (!(e instanceof java.lang.ArrayIndexOutOfBoundsException)){
                   errorString = "illegal format for options \n";
            	   errorString += e.getMessage() + "\n";
                }
              SFSystem.sfLog().ignore(e);
            }
        }


        if (errorString != null) {
            errorString += USAGE;
        }
    }

    /**
     * Reads all the lines of a file an tries to parse them as SFACT.
     * It ignores empty lines or lines starting with # or double "//"
     * @param fileURL file to be read
     * @throws SmartFrogException if failed to read
     * @return the parsed file
     */
    public static Vector readCfgDescriptorsFile(String fileURL) throws SmartFrogException{
        String line;
        LineNumberReader file=null;
        /** Vector for configurationDescriptors to be deployed. */
        Vector cfgDescriptors = new Vector();
        try {
            file = new LineNumberReader (new BufferedReader(new InputStreamReader(
               (org.smartfrog.SFSystem.getInputStreamForResource(fileURL)))));
            //Loop through each line and add non-blank
            //lines to the Vector
            while ( (line = file.readLine()) != null) {
               try {
                   line=line.trim();
                   if (line.length()>0){
                     //Logger.log(" Reading and Creating: "+line);
                     if (!(line.startsWith("#"))&&!(line.startsWith("//"))){
                       cfgDescriptors.add(new ConfigurationDescriptor(line));
                     //Logger.log(" result: " + this.cfgDescriptors.lastElement().toString());
                     } else {
                       //Ignore
                       //Logger.log("line ignored: " + line);
                     }
                   }
               } catch (SmartFrogInitException ex){
                 if (SFSystem.sfLog().isIgnoreEnabled()) {
                   SFSystem.sfLog().ignore("While reading in "+fileURL,ex);
                 }

               }
            }
        }  catch (Exception e) {
            SFSystem.sfLog().trace(e);
            throw SmartFrogException.forward(e);
        } finally {
            try {
                if(file!=null) {
                	file.close();
                }
            } catch (Exception ex) {
                SFSystem.sfLog().ignore(ex);
            }
        }
        return cfgDescriptors;
    }
}
