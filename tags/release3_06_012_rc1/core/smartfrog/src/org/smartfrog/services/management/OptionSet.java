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

package org.smartfrog.services.management;

import java.util.Vector;


/**
 * Parses the SFSystem arguments into an option set. Options are
 * seperated by optionFlagIndicator characters.
 *
 */
public class OptionSet {
    /** Option flag indicator. */
    protected char optionFlagIndicator = '-';
    /** Option length. */
    protected byte optionLength = 1;
    /** Usage. */
    public String usage = "\n" +
        "Usage: sfManagementConsole  [-h hostname] [-p  portnumber] [-w  windowPosition(C,N,SE,..)] [-r] [-?]\n";
    /** Error string. */
    public String errorString = null;
    /** Flag indicating remote daemon. */
    public boolean isRemoteDaemon = false;
    /** Falg indicating remote sub process. */
    public boolean isRemoteSubprocess = false;
    /** Flag indicating window position. */
    public boolean isWindowPosition = false;
    /** Flag indicating show root process. */
    public boolean showRootProcess = false;
    /** Hostname. */
    public String host = "localhost";
    /** Port number. */
    public int port = 3800;
    /** Window position. */
    public String windowPosition = "C";
    /** Vector of names. */
    public Vector names = new Vector();
    /** Flag indicating exit status. */
    public boolean exit = false;

    /** Constructs an OptionSet from an array of arguments.
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
                        errorString = "SFSystem help";

                        break;

                    case 'h':

                        if (isRemoteDaemon) {
                            errorString = "at most one -h allowed";
                        }

                        isRemoteDaemon = true;
                        host = args[++i];

                        break;

                    case 'p':

                        if (isRemoteSubprocess) {
                            errorString = "at most one -p allowed";
                        }

                        isRemoteSubprocess = true;
                        port = new Integer(args[++i]).intValue();

                        break;

                    case 'w':

                        if (isWindowPosition) {
                            errorString = "at most one -w allowed";
                        }

                        isWindowPosition = true;
                        windowPosition = args[++i];

                        break;

                    case 'r':
                        showRootProcess = true;

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

        //if (isRemoteSubprocess & !isRemoteDaemon) {
        //    errorString = "-p option must be accompanied by -h";
        //}
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
           if (isRemoteDaemon) System.out.println("remote deamon on host " +
   host);
           if (isRemoteSubprocess)  System.out.println("remote subprocess " +
   subprocess);
           if (exit) System.out.println("exit on completion");
           System.out.println("terminations");
           for (Enumeration et = terminations.elements(); et.hasMoreElements();) {
               System.out.println("   " + (String) et.nextElement());
           }
           System.out.println("configurations");
           for (Enumeration ec = configs.elements(); ec.hasMoreElements();) {
               String config = (String) ec.nextElement();
               System.out.println("   " + (String) config +" named " +
       names.get(config));
           }
       }
       public static void main(String[] args) {
           System.out.println("running");
           OptionSet o = new OptionSet(args);
           o.print();
       }
     */
}
