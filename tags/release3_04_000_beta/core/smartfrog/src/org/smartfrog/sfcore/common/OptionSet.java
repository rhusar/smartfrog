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
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import java.io.LineNumberReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * Parses the SFSystem arguments into an option set. Options are seperated by
 * optionFlagIndicator characters.
 *
 */
public class OptionSet {

    /** Character indicating the start of each option. */
    protected char optionFlagIndicator = '-';

    /** Length of each option. */
    protected byte optionLength = 1;

    /** Usage string for SFSystem. */
    public String usage = "\n" +
        " Usage: java -D... org.smartfrog.SFSystem [-a SFACT] [-f SFREF] [-e]\n" +
        "    or: java -D... org.smartfrog.SFSystem -?";

    /** Help string for SFSystem. */
    public String help = "\n" + Version.copyright + " - v." +
        Version.versionString + "\n" + " Parameters: " + "\n" +
        "    -a SFACT: SmartFrog Action Descriptor (SFACT),\n"+
        "              which is used to indicate to SmartFrog an action to take.\n" +
        "       ex. Deploy a description - " +  "\n" +
        "           -a counterEx:DEPLOY:org/.../example.sf::localhost:process" + "\n" +
        "       ex. Terminate local sfDaemon - " + "\n" +
        "           -a rootProcess:TERMINATE:::localhost:" + "\n" +
        "\n" +
        "       Format for SFACT: " +"NAME:ACTION:SFREF:SUBREF:HOST:PROCESS\n" +
        "           - NAME: name used by the ACTION to be taken\n" +
        "              ex. foo\n" +
        "              ex. \"HOST localhost:foo\"\n" +
        "          - ACTION: defines the action to be taken on the named component\n"+
        "                    possible actions: DEPLOY, TERMINATE, DETACH, DETaTERM\n" +
        "          - SFREF: SmartFrog description (if needed) to be used by ACTION\n" +
        "                   Currently only required by DEPLOY\n"+
        "              ex. /home/sf/foo.sf\n" +
        "              ex. \"c:\\sf\\foo.sf\"\n" +
        "          - SUBREF: component description name to use by ACTION. It can be empty\n" +
        "                   Currently only required by DEPLOY\n"+
        "              ex: foo\n" +
        "              ex: \"fist:foo\"\n" +
        "              note: sfConfig cannot be use with DEPLOY!\n" +
        "          - HOST: host name or IP from where to resolve NAME. It can be empty.\n" +
        "              ex: localhost\n" +
        "              ex: 127.0.0.1\n" +
        "          - PROCESS: process name from where to resolve NAME. When empty it assumes rootProcess.\n" +
        "          \n" +
        "              ex1: Deploy a description in local daemon\n" +
        "                   counterEx:DEPLOY:org/smartfrog/examples/counter/example.sf::localhost:\n" +
        "              ex2. Terminate local sfDaemon\n" +
        "                   rootProcess:TERMINATE:::localhost:\n" +
        "              ex3: Deploy \"counterToSucceed\" from counter/example2.sf\n" +
        "                   counterEx3:DEPLOY:org/smartfrog/examples/counter/example2.sf:\"testLevel1:counterToSucceed\":localhost:\n" +
        "\n" +
        "    -f SFREF: file with a set of SmartFrog Action Descriptors (SFACT)" +
        "\n" +
        "    -e: The daemon will terminate after finishing the deployment." + "\n" +
        " ";


    /** Error string for SFSystem. */
    public String errorString = null;

    /** Hostname where the description is to be deployed. */
    public String host = null;

    /** Processname where the description is to be deployed. */
    public String subprocess = null;


    /** Vector for configurationDescriptors to be deployed. */
    public Vector cfgDescriptors = new Vector();

    /** Flag indicating the exit status of the application. */
    public boolean exit = false;

    /**
     * Creates an OptionSet from an array of arguments.
     *
     * @param args arguments to create from
     */
    public OptionSet(String[] args) {
        int i = 0;
        String name;
        String url;
        String deployRef;

        while ((i < args.length) & (errorString == null)) {
            try {
                if (args[i].charAt(0) == optionFlagIndicator) {
                    switch (args[i].charAt(1)) {
                    case '?':
                        errorString = "SFSystem help" + help;
                        break;

                    case 'a':
                        try {
                            this.cfgDescriptors.add(new ConfigurationDescriptor(args[++i]));
                        } catch (SmartFrogInitException ex){Logger.log(ex);}
                        break;

                    case 'f':
                        try {
                            this.readCfgDescriptorsFile(args[++i]);
                        } catch (SmartFrogInitException ex){Logger.log(ex);}
                         break;

                    case 'e':
                        exit = true;
                        break;

                    default:
                        errorString = "unknown option " + args[i].charAt(1);
                    }
                } else {
                    errorString = "illegal option format for option " +
                        args[i];
                }

                i++;
            } catch (Exception e) {
                //protects from dodgy shell scripts
                if (!(e instanceof java.lang.ArrayIndexOutOfBoundsException)){
                   errorString = "illegal format for options ";
                }
                Logger.logQuietly(e);
            }
        }


        if (errorString != null) {
            errorString += usage;
        }
    }

    /**
     * Reads all the lines of a file an tries to parse them as SFACT.
     * It ignores empty lines or lines starting with # or double "//"
     * @param fileURL
     * @throws SmartFrogException
     */
    private void readCfgDescriptorsFile(String fileURL) throws SmartFrogException{
        String line;
        LineNumberReader file=null;
        try {
            file =
            new LineNumberReader (
               new BufferedReader(new InputStreamReader(
               (org.smartfrog.SFSystem.getInputStreamForResource(fileURL)))));
            //Loop through each line and add non-blank
            //lines to the Vector
            while ( (line = file.readLine()) != null) {
               try {
                   line=line.trim();
                   if (line.length()>0){
                     //Logger.log(" Reading and Creating: "+line);
                     if (!(line.startsWith("#"))&&!(line.startsWith("//"))){
                       this.cfgDescriptors.add(new ConfigurationDescriptor(line));
                     //Logger.log(" result: " + this.cfgDescriptors.lastElement().toString());
                     } else {
                       //Ignore
                       //Logger.log("line ignored: " + line);
                     }
                   }
               } catch (SmartFrogInitException ex){Logger.logQuietly(ex);}
            }
        }  catch (Exception e) {
            Logger.log(e);
            throw SmartFrogException.forward(e);
        } finally {
            try {
                file.close();
            } catch (Exception ex){Logger.logQuietly(ex);}
        }

    }

}
