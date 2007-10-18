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


import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Vector;
import org.smartfrog.SFSystem;


/**
 * Parses the SFSystem arguments into an option set. Options are seperated by
 * optionFlagIndicator characters.
 *
 */
public class ParseOptionSet {

    /** Character indicating the start of each option. */
    protected char optionFlagIndicator = '-';

    /** Length of each option. */
    protected byte optionLength = 1;

    /** Usage string for SFParse. */
    public String usage = "\n" +
        "* Usage: java org.smartfrog.SFParse [-v] [-q] [-d] [-r] [-R] [-f] filename [-D] \n"+
        "   or: java org.smartfrog.SFParse -?";

    /** Help string for SFSystem. */
    public String helpTxt = "\n* Parameters: " + "\n" +
        "    -v:             verbose, print every parser phase\n" +
        "    -q:             quiet, no phase printed (overwrites verbose)\n" +
        "    -d:             show 'description' parser phase \n" +
        "    -r:             show status parsing report \n" +
        "    -R:             show status parsing report in html file named \n"+
        "                     <filename>_report.html (only works with -f)\n" +
        "    -f filename:    file with a list of SmartFrog descriptions to parse\n" +
        "                    if -f is not present then filename is directly parsed\n"+
        "    -D:             show diagnostics report \n"+
        "    -?:             this help text.\n"+
        " \n"+
        "   Examples: SFParse -r org/smartfrog/examples/counter/example.sf \n";

    /** Error string for SFParse. */
    public String errorString = null;

    /** ExitCode for SFParse. */
    public int exitCode = ExitCodes.EXIT_ERROR_CODE_BAD_ARGS;

    /** Flag indicating the help option. */
    public boolean help =false;

    /** Flag indicating the verbose option. */
    public boolean verbose = false;

    /** Flag indicating the quite option. */
    public boolean quiet = false;

    /** Flag indicating to show the description parser phase or not. */
    public boolean description = false;

    /** Flag indicating to load description from file or not. */
    public boolean loadDescriptionsFromFile = false;

    /** File to be parsed. */
    public String fileName =null;

    /** List of files to be parsed. */
    public Vector filesList=null;

    /** Flag indicating to show status parsing report or not. */
    public boolean statusReport=false;

    /** Flag indicating to show status parsing report in html formal or not. */
    public boolean statusReportHTML=false;

    /** Flag indicating if diagnostics was requested. */
    public boolean diagnostics = false;

    /**
     * Creates an OptionSet from an array of arguments.
     *
     * @param args arguments to create from
     */
    public ParseOptionSet(String[] args) {
       try {
        int i = 0;

        while ((i < args.length) & (errorString == null)) {
            try {
                if ((args[i].charAt(0) == optionFlagIndicator)&& (args[i].length()>1)) {
                    switch (args[i].charAt(1)) {
                    case '?':
                        errorString =  helpTxt;
                        exitCode = ExitCodes.EXIT_CODE_SUCCESS;
                        help=true;
                        break;

                    case 'v':
                        verbose = true;
                        break;

                    case 'q':
                          quiet = true;
                          break;

                    case 'd':
                        description = true;
                        break;
                    case 'r':
                        statusReport = true;
                        break;
                    case 'R':
                        statusReportHTML = true;
                        break;
                    case 'D':
                        diagnostics = true;
                        break;

                    case 'f':
                        loadDescriptionsFromFile = true;
                        break;

                    default:
                        errorString = "unknown option " + args[i].charAt(1);
                    }
                } else {
                    if (i == args.length - 1) //last element
                        fileName = args[i];
                    else {
                        errorString = "unknown option " + args[i];
                    }
                }
                i++;
            } catch (Exception e) {
                errorString = "illegal format for options ";
                exitCode = ExitCodes.EXIT_ERROR_CODE_BAD_ARGS;
                //Logger.log(ex);
                if (SFSystem.sfLog().isErrorEnabled()) {
                    SFSystem.sfLog().error(e);
                }

            }
        }

        if (loadDescriptionsFromFile){
           filesList= loadListOfFiles(fileName);
        }

        if ((errorString != null)||fileName==null) {
            if ((fileName==null) &&(!help)) errorString="no file to parse";
            errorString += usage;
            //System.exit(1);
        }
       } catch (Exception ex){
           //Logger.log(ex);
           if (SFSystem.sfLog().isErrorEnabled()) {
               SFSystem.sfLog().error(ex);
           }
           exitCode = ExitCodes.EXIT_ERROR_CODE_BAD_ARGS;
       }
    }

    /**
     * Loads list from file.
     * @param url String url
     * @return Vector of parsed lines
     */
    private synchronized Vector loadListOfFiles(String url) {
      String thisLine;
      Vector list=new Vector();
        LineNumberReader file=null;
        try {
          //Do not allow other threads to read from the input
          //or write to the output while this is taking place
          file = new LineNumberReader(
              new FileReader(url));
          //Loop through each line and add non-blank
          //lines to the Vector
            while ((thisLine = file.readLine()) != null) {
                String line = thisLine.trim();
                if (line.length() > 0 && line.charAt(0) != '#') {
                    list.add(line);
                }
          }
      } catch (IOException ex) {
         errorString = ex.getMessage();
         //Logger.log(ex);
         if (SFSystem.sfLog().isErrorEnabled()) {
             SFSystem.sfLog().error(ex);
         }
      } finally {
            if(file!=null) {
                try {
                    file.close();
                } catch (IOException e) {
                    SFSystem.sfLog().error(e);
                }
            }
        }
      return list;
   }


   /**
     * Return string representation of context.
     *
     * @return string representation
     */
    public String toString (){
       StringBuffer strb = new StringBuffer();
       strb.append("SFParse options:");
       strb.append("\n - Verbose:       "+ verbose);
       strb.append("\n - Quiet:         "+  quiet);
       strb.append("\n - Description:   "+ description);
       strb.append("\n - File:          "+ fileName);
       strb.append("\n   * load from file:"+ loadDescriptionsFromFile);
       strb.append("\n   * filesList:   "+ filesList);
       strb.append("\n - Help:          "+ help);
       strb.append("\n - Status report: "+ statusReport);
       return strb.toString();
    }
}
