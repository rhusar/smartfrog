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
import java.io.LineNumberInputStream;
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
        " Usage: java -D... org.smartfrog.SFSystem [-h HOST_NAME [-p PROCESS_NAME]] [-a URL_DESCRIPTOR] [-f FILE_URL] (-t NAME)* (-d NAME)* (-T NAME)* (-c URL | -n NAME URL)* [-e]\n" +
        "    or: java -D... org.smartfrog.SFSystem -?";

    /** Help string for SFSystem. */
    public String help = "\n" + Version.copyright + " - v." +
        Version.versionString + "\n" + " Parameters: " + "\n" +
        "    -h HOST_NAME:    host on which the root sfDaemon is running" +
        "\n" +
        "    -p PROCESS_NAME: name by which the application should be known\n" +
        "                    in the sfDaemon where it is deployed" + "\n" +
        "                    (-p must be accompanied by -h)" +
        "\n" +
        "    -t NAME:        terminates the application named by 'NAME'" +
        "\n" +
        "    -d NAME:        detaches the component named by 'NAME' from the SmartFrog system and then terminates it" +
        "\n" +
        "    -T NAME:        terminates the component named by 'NAME'" +
        "\n" +
        "    -c URL:         to deploy up the SF text at 'URL' using a random name" +
        "\n" +
        "    -n NAME URL:    to deploy up the SF text at 'URL' using 'NAME' for the name" +
        "\n" +
        "    -a URL_DESCRIPTOR: descriptor of the application template to deploy.\n" +
       "       ex. counterEx:DEPLOY:org/.../example.sf:sfConfig:localhost:process" +
        "\n" +
        "    -f FILE_URL: file url with the ConfigurationDescriptors to deploy" +
        "\n" +

        "    -e:             exit after deployment is finished" + "\n" +
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

    /** Vector for named components given as -d options on the commandline. */
    public Vector detaching = new Vector();

    /** Vector for named components given as -T options on the commandline. */
    public Vector terminating = new Vector();

    /** Vector for applications to be deployed. */
    public Vector configs = new Vector();

    /** Vector for name of the applications to be deployed. */
    public Vector names = new Vector();


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
                        name = args[++i];
                        cfgDescriptors.add(
                            new ConfigurationDescriptor(name,
                                                        null,
                                ConfigurationDescriptor.Action.TERMINATE,
                                                        host,
                                                        subprocess));
                        terminations.add(name);
                        break;
                    case 'd':
                        name = args[++i];
                        cfgDescriptors.add(
                            new ConfigurationDescriptor(name,
                                                        null,
                                ConfigurationDescriptor.Action.DETACH,
                                                        host,
                                                        subprocess));
                        detaching.add(name);
                        break;

                    case 'T':
                        name = args[++i];
                        terminating.add(args[++i]);
                        cfgDescriptors.add(
                             new ConfigurationDescriptor(name,
                                                         null,
                         ConfigurationDescriptor.Action.TERMINATE,
                                                         host,
                                                         subprocess));
                        break;

                    case 'c':
                        url = args[++i];
                        configs.add(url);
                        names.add(null);
                        cfgDescriptors.add(
                            new ConfigurationDescriptor(null,
                                                        url,
                                ConfigurationDescriptor.Action.DEPLOY,
                                                         host,
                                                         subprocess));
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

                    case 'n':
                        if (args[i+1].charAt(0)==optionFlagIndicator) {
                            errorString = "SFSystem help" + help;
                        }
                        else {
                             name = args[++i];
                            if (args[i+1].charAt(0)==optionFlagIndicator)
                                errorString = "SFSystem help" + help;
                                        url = args[++i];
                                        configs.add(url);
                                        names.add(name);
                                        cfgDescriptors.add(
                                            new ConfigurationDescriptor(name,
                                                                        url,
                                                ConfigurationDescriptor.Action.DEPLOY,
                                                                        host,
                                                                        subprocess));
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
                //protects from dodgy shell scripts
                if (!(e instanceof java.lang.ArrayIndexOutOfBoundsException)){
                   errorString = "illegal format for options ";
                }
                Logger.logQuietly(e);
            }
        }

        if (isRemoteSubprocess & !isRemoteDaemon) {
            errorString = "-p option must be accompanied by -h";
        }

        if (errorString != null) {
            errorString += usage;
        }
    }

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
                   if (line.trim().length()>0){
                     //Logger.log(" Reading and Creating: "+line);
                     this.cfgDescriptors.add(new ConfigurationDescriptor(line));
                     //Logger.log(" result: " + this.cfgDescriptors.lastElement().toString());
                   }
               } catch (SmartFrogInitException ex){Logger.log(ex);}
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
