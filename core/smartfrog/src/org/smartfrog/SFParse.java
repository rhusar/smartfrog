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
import java.util.Vector;

import org.smartfrog.services.management.SFDeployDisplay;
import org.smartfrog.sfcore.common.ExitCodes;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.ParseOptionSet;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.Diagnostics;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.security.SFSecurity;

/**
 * SFParse provides the utility methods to parse file descriptions and generate
 * the parsing report. The main function looks for a filename(s) on the argument
 * line and parses locally.
 *
 * <P>
 * The main loop of SFParse reads an optionset. It then parses the file(s)
 * and/or prints the status report depending on the options.
 * </P>
 * 
 * <b>Warning</b> This class is not thread safe; the static methods used shared static data structures.
 * It is intended to be executed in from Main() methods and not from Java code.
 */
public class SFParse implements MessageKeys {

//    static String usageString = "Usage: sfParse [-v][-d] filename";
    private static ParseOptionSet opts = null;

    private static Vector<Vector<String>> errorReport = null;
    
    private static ComponentDescription cd = null;
    
    
    private SFParse(){
    }

    /**
     * Gets language from the URL
     *
     * @param url URL passed to application
     *
     * @return Language string
     *
     * @throws SmartFrogException In case any error while getting the language string
     */
    private static String getLanguageFromUrl(String url) throws
            SmartFrogException {
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
     * Ascertains whether a file is parseable
     * @param fileUrl the fileurl to be parsed
     * @param rpm A RawParseModifier on which we call modify(...) passing the raw parsed ComponentDescription, 
     * that is before any parsing phases are carried out, so we have the opportunity to modify it before the phases are carried out
     * @return success or not
     */
    public static boolean fileParses(String fileUrl, RawParseModifier rpm) {
        if (opts == null) {
            opts = new ParseOptionSet(new String[]{"sfParse"});
        }
        createErrorReport();
        parseFile(fileUrl,rpm);
    	return !errorReport.isEmpty();
    }

    private static void createErrorReport() {
        errorReport= new Vector<Vector<String>>();
    }

    /**
     * Attempts to parse given file, returning resultant component description
     * @param fileUrl the fileurl to be parsed
     * @return ComponentDescription resulting from parse, if file successfully parses, else null
     */
    public static ComponentDescription parseFileToDescription(String fileUrl) { 
    	return parseFileToDescription(fileUrl, null);
    }
    
    
    /**
     * Attempts to parse given file, returning resultant component description
     * @param fileUrl the fileurl to be parsed
     * @param rpm A RawParseModifier on which we call modify(...) passing the raw parsed ComponentDescription, 
     * that is before any parsing phases are carried out, so we have the opportunity to modify it before the phases are carried out
     * @return ComponentDescription resulting from parse, if file successfully parses, else null
     */
    public static ComponentDescription parseFileToDescription(String fileUrl, RawParseModifier rpm) { 
    	boolean parses = fileParses(fileUrl, rpm);
        return parses? cd: null;
    }
    
    /**
     * Parses a file.
     *
     * @param fileUrl the fileurl to be parsed
     * @return the parse report
     */
    private static Vector<String> parseFile(String fileUrl) {
    	return parseFile(fileUrl, null);
    }
    
    /**
     * Parses a file.
     *
     * @param fileUrl the fileurl to be parsed
     * @param rpm A RawParseModifier on which we call modify(...) passing the raw parsed ComponentDescription, 
     * that is before any parsing phases are carried out, so we have the opportunity to modify it before the phases are carried out
     * @return the parse report
     */
    private static Vector<String> parseFile(String fileUrl, RawParseModifier rpm) {
        //To calculate how long it takes to parse a description
        Vector<String> report = new Vector<String>();
        report.add("File: "+fileUrl+"\n");
        long parseTime=System.currentTimeMillis();
        try {

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
                report.add("   "+ "raw" +" phase: FAILED!");
                throw ex;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ignored) {
                    }
                }
            }

            if (rpm != null) {
                rpm.modify((top));
            }

            phaseList = top.sfGetPhases();
            String phase;

            for (Object aPhaseList : phaseList) {
                phase = (String) aPhaseList;
                try {
                    top = top.sfResolvePhase(phase);
                    if (opts.verbose && !opts.quiet) {
                        printPhase(phase, top.toString());
                    }
                    report.add("   " + phase + " phase: OK");
                } catch (Exception ex) {
                    //report.add("   "+ phase +" phase: "+ex.getMessage());
                    report.add("   " + phase + " phase: FAILED!");
                    throw ex;
                }
            }

            cd = top.sfAsComponentDescription();

            if ((opts.description) || (opts.verbose && !opts.quiet)) {
                printPhase("sfAsComponentDescription", cd.toString());
            }

            parseTime=System.currentTimeMillis()-parseTime;
            report.add(", parsed in "+ (parseTime) + " millisecs.");

            showConsole(cd);


        } catch (Exception e) {
            //report.add("Error: "+ e.getMessage());
            //report.add("   "+ phase +" phase: FAILED!");
            SFSystem.sfLog().err("'"+fileUrl+"': \n"+ e+"\n",e);
            Vector<String> itemError = new Vector<String>();
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

    private static void showConsole(ComponentDescription componentDescription) throws Exception {
        if (opts.showConsole) {
            SFDeployDisplay.starParserConsole("ParseConsole", 440, 600, "N", componentDescription, true);
            Thread.sleep(3600 * 100);
        }
    }

    /**
     * Parses a list of files.
     *
     * @param list the list of files to be parsed
     */
    private static void parseFiles(Vector<String> list) {
        StringBuffer strb;
        Vector<Vector<String>> report = new Vector<Vector<String>>();
        //Loop through the vector
        for (String file : list) {
            try {
                strb = new StringBuffer();
                //If it's not an empty line
                if (file.trim().length() > 0) {
                    strb.append("-----------------------------------------------\n")
                            .append("-  Parsing: ")
                            .append(file)
                            .append("\n")
                            .append("-----------------------------------------------");
                    if (!opts.quiet) SFSystem.sfLog().out(strb.toString());
                    report.add(parseFile(file));
                }
            } catch (Throwable thr) {
                strb = new StringBuffer();
                strb.append("-----------------------------------------------")
                        .append("-  Error parsing: ")
                        .append(file)
                        .append("\n")
                        .append("-     ")
                        .append(thr.getMessage())
                        .append("\n")
                        .append("-----------------------------------------------");
                if (!opts.quiet) SFSystem.sfLog().err(strb.toString(), thr);
            }
        }
        if (opts.statusReport) {
            printTotalReport(report);
        }
        if (opts.statusReportHTML) {
            printTotalReportHTML(report);
            FileWriter newFile = null;
            try {
                newFile = new FileWriter(opts.fileName + "_report.html");
                newFile.write("<!doctype HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"><html>");
                newFile.write("<body>" + "\n");
                newFile.write("<font color=\"BLUE\" size=\"5\">Status report<font/>" + "\n");
                newFile.write("<table border=\"1\">" + "\n");
                newFile.write(printTotalReportHTML(report));
                newFile.write("<table/>");
                newFile.write("<font color=\"BLUE\" size=\"5\">Error report<font/>" + "\n");
                newFile.write("<table border=\"1\">" + "\n");
                newFile.write(printTotalReportHTML(errorReport));
                newFile.write("<table/>");
                newFile.write("<body/>" + "\n");
                newFile.write("<html/>" + "\n");
                newFile.flush();
                newFile.close();
                newFile = null;
                SFSystem.sfLog().out("Report created: " + opts.fileName + "_report.html");
            } catch (IOException e) {
                SFSystem.sfLog().error(e);
            } finally {
                if (newFile!=null) {
                    try {
                        newFile.close();
                    } catch (IOException ignored) {

                    }
                }
            }
        }
    }

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
            opts = new ParseOptionSet(args);
            createErrorReport();

            showDiagnostics(opts);

            if (opts.errorString != null) {
                if (opts.help) {
                    SFSystem.sfLog().out("Help: \n" + opts.errorString);
                    ExitCodes.exitWithError(ExitCodes.EXIT_CODE_SUCCESS);
                } else {
                    SFSystem.sfLog().out("Error: " + opts.errorString);
                    ExitCodes.exitWithError(ExitCodes.EXIT_CODE_SUCCESS);
                }
                exit();
            }

            if (opts.loadDescriptionsFromFile) {
                parseFiles(opts.filesList);
            } else {
                Vector<String> report = parseFile(opts.fileName);
                if (opts.statusReport) {
                    printItemReport(report);
                }
            }
            //Added so that ant task detects error during build process.
            // If we found errors during parsing we force exit.
            if (!errorReport.isEmpty()) {
                SFSystem.sfLog().out("Error detected. Check report.");
                exit();
            } else {
                SFSystem.sfLog().out("SFParse: SUCCESSFUL");
                ExitCodes.exitWithError(ExitCodes.EXIT_CODE_SUCCESS);
            }
        } catch (Throwable thr) {
            SFSystem.sfLog().error(thr);
        }

    }

    /**
     * Exits SFParse.
     */
    private static void exit() {
        SFSystem.sfLog().out( "SFParse: FAILED");
        ExitCodes.exitWithError(ExitCodes.EXIT_ERROR_CODE_GENERAL);
    }

    /**
     * Shows the version info of the SmartFrog system.
     */
    private static void showVersionInfo() {
        SFSystem.sfLog().out("\nParser - " +Version.versionString());
        SFSystem.sfLog().out(Version.copyright());
        SFSystem.sfLog().out(" ");
    }


    /**
     * Prints the phase.
     *
     * @param phaseName the phase name
     * @param result the result to be printed
     */
    private static void printPhase(String phaseName, String result) {
        SFSystem.sfLog().out(
            "******************** PHASE " + phaseName +
            " *********************");
        SFSystem.sfLog().out(result);
        SFSystem.sfLog().out("\n\n\n\n\n");
    }

   /**
    *  Prints the total parsing report.
    *
    *  @param report the report to be printed
    */
    private static void printTotalReport(Vector<Vector<String>> report){
       for (Vector<String> entry : report) {
           printItemReport(entry);
       }
    }

   /**
    *  Print the parsing report for an item.
    *
    *  @param report the report to be printed
    */
    private static void printItemReport(Vector<String> report) {
       StringBuilder st = new StringBuilder("STATUS REPORT: ");
       for (Object line : report) {
           st.append(line.toString()).append("<td/>\n");
       }
       SFSystem.sfLog().out(st.toString());
   }

   /**
    *  Prints the total parsing report in html format.
    *
    *  @param report the report to be printed
    *  @return the string form of the parse report
    */
    private static String printTotalReportHTML(Vector<Vector<String>> report){
      //StringBuffer reportHTML = new StringBuffer("<!doctype HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"><html>");
       StringBuilder reportHTML = new StringBuilder();
//      reportHTML.append("<body>"+"\n");
//            //Add table
//      reportHTML.append("<table border=\"1\">"+"\n");
       for(Vector<String> entry:report) {
           reportHTML.append(printItemReportHTML(entry));
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
    * @return the output as a table row
    */
    private static String printItemReportHTML(Vector<String> report) {
       //SFSystem.sfLog().out("STATUS REPORT:\n");
       StringBuilder st = new StringBuilder("<tr>" + "\n");
       for (String line : report) {
           st.append("<td>").append(line).append("<td/>\n");
       }
       st.append("<tr/>" + "\n");
       return st.toString();
   }

    /**
     * Shows diagnostics report
     * @param options OptionSet
     */
    private static void showDiagnostics(ParseOptionSet options) {
      if (options.diagnostics){
        StringBuffer report = new StringBuffer();
        Diagnostics.doReport(report);
        SFSystem.sfLog().out(report.toString());
      }
    }
    
    public static boolean isVerboseOptSet(){
        return opts != null && opts.verbose;
    }
    
    public interface RawParseModifier {
    	public void modify(ComponentDescription cd);
    }

    private static class ParseResults {
        ComponentDescription cd;
        Vector<String> parsed;
        Vector<Vector<String>> errors = new Vector<Vector<String>>();
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
    }
}
