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

package org.smartfrog;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.security.SFSecurity;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.ParseOptionSet;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.languages.sf.predicates.BasePredicate;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.security.SFClassLoader;

/**
 * SFParse provides the utility methods to parse file descriptions and generate
 * the parsing report. The main function looks for a filename(s) on the argument
 * line and parses locally.
 *
 * <P>
 * The main loop of SFParse reads an optionset. It then parses the file(s)
 * and/or prints the status report depending on the options.
 * </P>
 */
public class SFParse implements MessageKeys {

//    static String usageString = "Usage: sfParse [-v][-d] filename";
    static ParseOptionSet opts = null;

    static Vector phases;

    static Vector errorReport = new Vector();

    private SFParse(){
    }

    /**
     * Gets language grom the URL
     *
     * @param url URL passed to application
     *
     * @return Language string
     *
     * @throws Exception In case any error while getting the language string
     */
    private static String getLanguageFromUrl(String url) throws Exception {
        int i = url.lastIndexOf('.');

        if (i <= 0) {
            // i.e. it cannot contain no "." or start with the only "."
            throw new SmartFrogException(
                "unable to source locate language in URL '" + url+"'");
        } else {
            return url.substring(i + 1);
        }
    }

    /**
     * Parses a file.
     *
     * @param fileUrl the fileurl to be parsed
     * @return the parse report
     */

    private static Vector parseFile(String fileUrl) {
        //To calculate how long it takes to parse a description
        Vector report = new Vector();
        report.add("File: "+fileUrl+"\n");
        long parseTime=System.currentTimeMillis();
        try {
            if (opts.description) {
                BasePredicate.keepPredicates = true;
            }

            String language = getLanguageFromUrl(fileUrl);
            //report.add("language: "+language);

            Vector phaseList;

            Phases top;
            InputStream is=null;
            try {
                is = SFClassLoader.getResourceAsStream(fileUrl);
                if (is == null) {
                    String msg = MessageUtil.
                            formatMessage(MSG_URL_TO_PARSE_NOT_FOUND, fileUrl);
                    throw new SmartFrogParseException(msg);
                }
                top = (new SFParser(language)).sfParse(is);
                if (opts.verbose && !opts.quiet) {
                    printPhase("raw", top.toString());
                }
                report.add("   "+"raw phase: OK");
            } catch (Exception ex) {
                //report.add("   "+"raw phase: "+ex.getMessage());
                report.add("   "+ "raw" +" phase: FAILED!");
                throw ex;
            } finally {
                if(is!=null) {
                    try {
                        is.close();
                    } catch (IOException swallowed) {

                    }
                }
            }

            phaseList = top.sfGetPhases();
            String phase;

            for (Enumeration e = phaseList.elements(); e.hasMoreElements(); ) {
                phase = (String) e.nextElement();
                try {
                    if (phase.equals("predicate")&& (opts.description)) {
                       top = top.sfResolvePhase("description");
                       printDescription(top,"stdout_txt");
                    } else {
                        top = top.sfResolvePhase(phase);
                        if (opts.verbose&&!opts.quiet) {
                            printPhase(phase, top.toString());
                        }
                        report.add("   "+phase+" phase: OK");
                    }
                } catch (Exception ex) {
                  //report.add("   "+ phase +" phase: "+ex.getMessage());
                  report.add("   "+ phase +" phase: FAILED!");
                  throw ex;
                }
            }
            parseTime=System.currentTimeMillis()-parseTime;
            //org.smartfrog.sfcore.common.Logger.log(" * "+fileUrl +" parsed in "+ parseTime + " millisecs.");
            report .add(", parsed in "+ (parseTime) + " millisecs.");

        } catch (Exception e) {
            //report.add("Error: "+ e.getMessage());
            //report.add("   "+ phase +" phase: FAILED!");
            SFSystem.sflog().out("ERROR '"+fileUrl+"': \n"+ e+"\n");
            Vector itemError = new Vector();
            itemError.add(fileUrl);
            if (e instanceof SmartFrogException) {
                itemError.add(((SmartFrogException)e).toString("<BR><BR>"));
            }
            else {
                itemError.add(e.toString());
            }
            errorReport.add(itemError);
        }
        return report;
    }

    /**
     * Parses a list of files.
     *
     * @param list the list of files to be parsed
     */
    private static void parseFiles (Vector list){
          StringBuffer strb = new StringBuffer();
          String file = "";
          Vector report= new Vector();
          //Loop through the vector
         for (int i = 0; i < list.size(); i++) {
             try {
                 strb = new StringBuffer();
                 //Get the current line of text
                 file = list.elementAt(i).toString();
                 //If it's not an empty line
                 if (file.trim().length() > 0) {
                     strb.append(
                         "-----------------------------------------------\n");
                     strb.append("-  Parsing: " + file+"\n");
                     strb.append(
                         "-----------------------------------------------");
                     if (!opts.quiet) SFSystem.sflog().out(strb.toString());
                     report.add(parseFile(file));
                 }
             } catch (Throwable thr){
                 strb = new StringBuffer();
                 strb.append(
                         "-----------------------------------------------");
                 strb.append("-  Error parsing: "+ file+"\n");
                 strb.append("-     "+thr.getMessage()+"\n");
                 strb.append(
                     "-----------------------------------------------");
                 if (!opts.quiet) SFSystem.sflog().err(strb.toString(),thr);
             }
         }
         if (opts.statusReport){
           printTotalReport(report);
         }
         if (opts.statusReportHTML){
           printItemReportHTML(report);
           try {
              FileWriter newFile = new FileWriter(opts.fileName+"_report.html");
              newFile.write("<!doctype HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"><html>");
              newFile.write("<body>"+"\n");
              newFile.write("<font color=\"BLUE\" size=\"5\">Status report<font/>"+"\n");
              newFile.write("<table border=\"1\">"+"\n");
              newFile.write(printTotalReportHTML(report));
              newFile.write("<table/>");
              newFile.write("<font color=\"BLUE\" size=\"5\">Error report<font/>"+"\n");
              newFile.write("<table border=\"1\">"+"\n");
              newFile.write(printTotalReportHTML(errorReport));
              newFile.write("<table/>");
              newFile.write("<body/>"+"\n");
              newFile.write("<html/>"+"\n");
              newFile.flush();
              newFile.close();
              SFSystem.sflog().out("Report created: "+opts.fileName+"_report.html");
            } catch (IOException e) {
              if (SFSystem.sflog().isErrorEnabled()){
                SFSystem.sflog().error(e);
              }
              //Logger.log(e);
            }
         }
    }

    /**
     * Method invoked to parse the component description in SmartFrog system.
     *
     * @param args command line arguments. Please see the usage to get more
     * details
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

            opts = new ParseOptionSet(args);
            //SFSystem.SFSystem.sflog().out(opts.toString());

            showVersionInfo();
            // Read init properties
            //SFSystem.readPropertiesFromIniFile();

            if (opts.errorString != null) {
                if (opts.help) {
                    SFSystem.sflog().out( "Help: \n"+opts.errorString);
                } else {
                    SFSystem.sflog().out( "Error: "+opts.errorString);
                }
                exit();
            }

           if (opts.loadDescriptionsFromFile) {
              parseFiles(opts.filesList);
           } else {
             Vector report = parseFile(opts.fileName);
             if (opts.statusReport){
               printItemReport(report);
             }
           }
           //Added so that ant task detects error during build process.
           // If we found errors during parsing we force exit.
           if (!errorReport.isEmpty()) {
             SFSystem.sflog().out("Error detected. Check report.");
             exit();
           } else {
               SFSystem.sflog().out( "SFParse: SUCCESSFUL");
           }
        } catch (Throwable thr){
//           Logger.log(thr);
           if (SFSystem.sflog().isErrorEnabled()){
             SFSystem.sflog().error(thr);
           }
        }

    }

    /**
     * Exits SFParse.
     */
    private static void exit() {
        SFSystem.sflog().out( "SFParse: FAILED");
        System.exit(-1);
    }

    /**
     * Shows the version info of the SmartFrog system.
     */
    private static void showVersionInfo() {
        SFSystem.sflog().out("\nParser - " +Version.versionString());
        SFSystem.sflog().out(Version.copyright());
        SFSystem.sflog().out(" ");
    }


    /**
     * Prints the phase.
     *
     * @param phaseName the phase name
     * @param result the result to be printed
     */
    private static void printPhase(String phaseName, String result) {
        SFSystem.sflog().out(
            "******************** PHASE " + phaseName +
            " *********************");
        SFSystem.sflog().out(result);
        SFSystem.sflog().out("\n\n\n\n\n");
    }

   /**
    * Special method to print description with a particular presentation format.
    *
    * @param top the phases to be printed
    * @param format the presentation format
    */
   private static void printDescription(Phases top, String format) {
       //@TODO: add different formats like html, xhtml, pdf , txt, ...
       try {
           if (!opts.quiet) {
               if (opts.verbose)
                   SFSystem.sflog().out(
                       "******************** component description *********************");
               if (opts.description) {
                   SFSystem.sflog().out("Description of sfConfig component\n\n");
                   top.sfResolvePhase("description");
                   SFSystem.sflog().out(top.sfAsComponentDescription().toString());
               } else
                   SFSystem.sflog().out(top.sfAsComponentDescription().toString());
           }
       } catch (Exception ex) {
           if (SFSystem.sflog().isErrorEnabled()){
             SFSystem.sflog().error(ex);
           }
           //Logger.log(ex);
       }
   }

   /**
    *  Prints the total parsing report.
    *
    *  @param report the report to be printed
    */
    private static void printTotalReport(Vector report){
      for (Enumeration e = report.elements(); e.hasMoreElements(); ) {
        printItemReport((Vector) e.nextElement());
      }
    }

   /**
    *  Print the parsing report for an item.
    *
    *  @param report the report to be printed
    */
    private static void printItemReport(Vector report) {
      //SFSystem.sflog().out("STATUS REPORT:\n");
      StringBuffer st = new StringBuffer("STATUS REPORT: ");
      for (Enumeration e = report.elements(); e.hasMoreElements(); ) {
        st.append(e.nextElement().toString());
        //SFSystem.sflog().out((String) e.nextElement());
      }
      SFSystem.sflog().out(st.toString());
    }

   /**
    *  Prints the total parsing report in html format.
    *
    *  @param report the report to be printed
    *  @return the string form of the parse report
    */
    private static String printTotalReportHTML(Vector report){
      //StringBuffer reportHTML = new StringBuffer("<!doctype HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"><html>");
      StringBuffer reportHTML = new StringBuffer();
//      reportHTML.append("<body>"+"\n");
//            //Add table
//      reportHTML.append("<table border=\"1\">"+"\n");
      for (Enumeration e = report.elements(); e.hasMoreElements(); ) {
        reportHTML.append(printItemReportHTML((Vector) e.nextElement()));
      }
//      reportHTML.append("<table/>");
//      reportHTML.append("<body/>"+"\n");
//      reportHTML.append("<html/>"+"\n");
      return reportHTML.toString();
    }

   /**
    *  Print the parsing report for an item in html format.
    *
    *  @param report the report to be printed
    */
    private static String printItemReportHTML(Vector report) {
      //SFSystem.sflog().out("STATUS REPORT:\n");
      StringBuffer st = new StringBuffer("<tr>"+"\n");
      for (Enumeration e = report.elements(); e.hasMoreElements(); ) {
        st.append("<td>"+e.nextElement().toString()+"<td/>"+"\n");
      }
      st.append("<tr/>"+"\n");
      return st.toString();
    }


}
