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
        " Usage: java -D... org.smartfrog.SFSystem [-h HOST_NAME [-p PROCESS_NAME]] (-t NAME)* (-c URL | -n NAME URL)* [-e]\n" +
        "    or: java -D... org.smartfrog.SFSystem -?";

    /** Help string for SFSystem. */
    public String help = "\n" + Version.copyright + " - v." +
        Version.versionString + "\n" + " Parameters: " + "\n" +
        "    -h HOST_NAME:    host on which the root sfDaemon is running" +
        "\n" +
        "    -p PROCESS_NAME: name by which the application should be known\n" +
        "                    in the sfDaemon where it is deployed" + "\n" +
        "                    (-p must be accompanied by -h)" + "\n" +
        "    -t NAME:        terminates the application named by 'NAME'" +
        "\n" +
        "    -c URL:         to deploy up the SF text at 'URL' using a random name" +
        "\n" +
        "    -n NAME URL:    to deploy up the SF text at 'URL' using 'NAME' for the name" +
        "\n" + "    -e:             exit after deployment is finished" + "\n" +
        " To stop sfDaemon use: -h HOST_NAME -t rootProcess";

    /** Error string for SFSystem. */
    public String errorString = null;

    /** Flag indicating whether daemon is remote or not. */
    public boolean isRemoteDaemon = false;

    /** Flag indicating whether subprocess is remote or not. */
    public boolean isRemoteSubprocess = false;

    /** Hostname where the description is to be deployed. */
    public String host = null;

    /** Processname where the description is to be deployed. */
    public String subprocess = null;

    /** Vector for named applications given as -t options on the commandline. */
    public Vector terminations = new Vector();

    /** Vector for applications to be deployed. */
    public Vector configs = new Vector();

    /** Vector for name of the applications to be deployed. */
    public Vector names = new Vector();

    /** Flag indicating the exit status of the application. */
    public boolean exit = false;

    /**
     * Creates an OptionSet from an array of arguments.
     *
     * @param args arguments to create from
     */
    public OptionSet(String[] args) {
        int i = 0;

        while ((i < args.length) & (errorString == null)) {
            try {
                if (args[i].charAt(0) == optionFlagIndicator) {
                    switch (args[i].charAt(1)) {
                    case '?':
                        errorString = "SFSystem help" + help;

                        break;

                    case 'h':

                        if (isRemoteDaemon) {
                            errorString = "at most one -h allowed";
                        }

                        isRemoteDaemon = true;
                        if (args[i+1].charAt(0)==optionFlagIndicator)
                        {
                            errorString = "SFSystem help" + help;
                        }
                        else {
                            host = args[++i];
                        }

                        break;

                    case 'p':

                        if (isRemoteSubprocess) {
                            errorString = "at most one -p allowed";
                        }

                        isRemoteSubprocess = true;
                        subprocess = args[++i];

                        break;

                    case 't':
                        terminations.add(args[++i]);

                        break;

                    case 'c':
                        configs.add(args[++i]);
                        names.add(null);

                    break;

                    case 'n':

                        if (args[i+1].charAt(0)==optionFlagIndicator) {
                            errorString = "SFSystem help" + help;
                        }
                        else {
                            String name = args[++i];
                            if (args[i+1].charAt(0)==optionFlagIndicator)
                                errorString = "SFSystem help" + help;
                                        configs.add(args[++i]);
                                        names.add(name);
                        }

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
                errorString = "illegal format for options ";
            }
        }

        if (isRemoteSubprocess & !isRemoteDaemon) {
            errorString = "-p option must be accompanied by -h";
        }

        if (errorString != null) {
            errorString += usage;
        }
    }

    /*
       public void print () {
            if (errorString != null) {
                System.out.println(errorString);
                return;
           }
           if (isRemoteDaemon) System.out.println("remote deamon on host " + host);
           if (isRemoteSubprocess)  System.out.println("remote subprocess " + subprocess);
           if (exit) System.out.println("exit on completion");
           System.out.println("terminations");
           for (Enumeration et = terminations.elements(); et.hasMoreElements();) {
               System.out.println("   " + (String) et.nextElement());
           }
           System.out.println("configurations");
           for (Enumeration ec = configs.elements(); ec.hasMoreElements();) {
               String config = (String) ec.nextElement();
               System.out.println("   " + (String) config +" named " + names.get(config));
           }
       }
       public static void main(String[] args) {
           System.out.println("running");
           OptionSet o = new OptionSet(args);
           o.print();
       }
     */
}
