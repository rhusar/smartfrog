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

package org.smartfrog;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.Closeable;
import java.io.StringBufferInputStream;
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
 * SFParse provides the utility methods to parse file descriptions and generate the parsing report. The main function
 * looks for a filename(s) on the argument line and parses locally.
 *
 * <P> The main loop of SFParse reads an optionset. It then parses the file(s) and/or prints the status report depending
 * on the options. </P>
 *
 * <b>Warning</b> This class is not thread safe; the static methods used shared static data structures. It is intended
 * to be executed in from Main() methods and not from Java code.
 */
public final class SFParse implements MessageKeys {

    private static ParseOptionSet opts = null;

    private static Vector<Vector<String>> errorReport = null;


    private SFParse() {
    }

    /**
     * Gets language from the URL
     *
     * @param url URL passed to application
     * @return Language string
     * @throws SmartFrogException In case any error while getting the language string
     */
    private static String getLanguageFromUrl(String url) throws
            SmartFrogException {
        int i = url.lastIndexOf('.');

        if (i <= 0) {
            // i.e. it cannot contain no "." or start with the only "."
            throw new SmartFrogException(
                    "unable to source locate language in URL '" + url + "'");
        } else {
            return url.substring(i + 1);
        }
    }

    /**
     * Ascertains whether a file is parseable
     *
     * @param fileUrl the fileurl to be parsed
     * @param rpm     A RawParseModifier on which we call modify(...) passing the raw parsed ComponentDescription, that
     *                is before any parsing phases are carried out, so we have the opportunity to modify it before the
     *                phases are carried out
     * @return success or not
     */
    public static boolean fileParses(String fileUrl, RawParseModifier rpm) {
        ParseResults results = parseFile(fileUrl, rpm, createOptions());
        return extractErrors(results);
    }


    /**
     * Ascertains whether a file is parseable
     *
     * @param fileUrl the fileurl to be parsed
     * @param rpm     A RawParseModifier on which we call modify(...) passing the raw parsed ComponentDescription, that
     *                is before any parsing phases are carried out, so we have the opportunity to modify it before the
     *                phases are carried out
     * @return true if the file contained no errors
     */
    public static boolean parseSingleFile(String fileUrl, RawParseModifier rpm) {
        ParseResults results = parseFile(fileUrl, rpm, createOptions());
        return extractErrors(results);
    }

    /**
     * Extract errors from the results, add it to the ongoing error report (which is created if needed)
     *
     * @param results parsing output
     * @return true if the file contained no errors
     */
    private static boolean extractErrors(ParseResults results) {
        Vector<Vector<String>> errors = results.errors;
        return addErrorsToErrorReport(errors);
    }

    /**
     * Add errors to an error report, demand-creating the error report if necessary
     * @param errors the errors to add
     * @return true if they were added
     */
    private static boolean addErrorsToErrorReport(Vector<Vector<String>> errors) {
        demandCreateErrorReport();
        if (errors.isEmpty()) {
            return true;
        }
        errorReport.addAll(errors);
        return false;
    }

    private static void demandCreateErrorReport() {
        if (errorReport == null) {
            createErrorReport();
        }
    }

    private static void createErrorReport() {
        errorReport = new Vector<Vector<String>>();
    }

    /**
     * Attempts to parse given file, returning resultant component description
     *
     * @param fileUrl the fileurl to be parsed
     * @return ComponentDescription resulting from parse, if file successfully parses, else null
     */
    public static ComponentDescription parseFileToDescription(String fileUrl) {
        return parseFileToDescription(fileUrl, null);
    }


    /**
     * Attempts to parse given file, returning resultant component description. Saves errors to the static variable
     * {@link #errorReport}
     *
     * @param fileUrl the fileurl to be parsed
     * @param rpm     A RawParseModifier on which we call modify(...) passing the raw parsed ComponentDescription, that
     *                is before any parsing phases are carried out, so we have the opportunity to modify it before the
     *                phases are carried out
     * @return ComponentDescription resulting from parse, if file successfully parses, else null
     */
    public static ComponentDescription parseFileToDescription(String fileUrl, RawParseModifier rpm) {
        ParseOptionSet options = createOptions();
        ParseResults results = parseFile(fileUrl, rpm, options);
        return results.hasErrors() ? null : results.cd;
    }

    /**
     * Create the options, using the static {@link # opts} variable if present.
     *
     * @return an optionset
     */
    private static ParseOptionSet createOptions() {
        ParseOptionSet options = opts;
        if (options == null) {
            options = new ParseOptionSet(new String[]{"sfParse"});
        }
        return options;
    }

    /**
     * Attempts to parse given file, returning resultant component description
     *
     * @param fileUrl the fileurl to be parsed
     * @param rpm     A RawParseModifier on which we call modify(...) passing the raw parsed ComponentDescription, that
     *                is before any parsing phases are carried out, so we have the opportunity to modify it before the
     *                phases are carried out
     * @param options the options for this parse
     * @return the parse results
     */
    public static ParseResults parseFileToResults(String fileUrl, RawParseModifier rpm, ParseOptionSet options) {
        return parseFile(fileUrl, rpm, options);
    }

    /**
     * Attempts to parse given file, returning resultant component description
     *
     * @param resource the fileurl to be parsed
     * @param rpm     A RawParseModifier on which we call modify(...) passing the raw parsed ComponentDescription, that
     *                is before any parsing phases are carried out, so we have the opportunity to modify it before the
     *                phases are carried out
     * @param options the options for this parse
     * @return the parse results
     */
    public static ParseResults parseResourceToResults(String resource, RawParseModifier rpm, ParseOptionSet options) {
        return parseResource(resource, rpm, options);
    }

    /**
     * Parses the supplied text.
     * 
     * The code uses the (deprecated) {@link StringBufferInputStream}, which is only uses the low-byte of every
     * character in the process. As long as the input is all ASCII, all will be well, but results for high-unicode is
     * "undefined".
     *
     * @param text inline text
     * @param rpm      A RawParseModifier on which we call modify(...) passing the raw parsed ComponentDescription, that
     *                 is before any parsing phases are carried out, so we have the opportunity to modify it before the
     *                 phases are carried out
     * @param options  the options for this parse
     * @return the parse results
     */
    public static ParseResults parseTextToResults(String text, RawParseModifier rpm, ParseOptionSet options) {
        
        StringBufferInputStream is = new StringBufferInputStream(text);
        return parseInputStreamToResults("inline.sf", is, rpm, options);
    }

    /**
     * Parse any open input stream to results
     * @param path real or pretend path of the source -the filetype is picked up from the extension
     * @param is input stream
     * @param rpm      A RawParseModifier on which we call modify(...) passing the raw parsed ComponentDescription, that
     *                 is before any parsing phases are carried out, so we have the opportunity to modify it before the
     *                 phases are carried out
     * @param options  the options for this parse
     * @return the parse results
     */
    public static ParseResults parseInputStreamToResults(String path, InputStream is,
                                                         RawParseModifier rpm, ParseOptionSet options) {
        ParseResults results = new ParseResults();
        try {
            parseInputStream(path, is, results, rpm, options);
        } catch (Exception e) {
            handleParseErrors(e, results, path, options, "\n");
        }
        return results;
    }

    /**
     * Parses a file.
     *
     * @param path the fileurl to be parsed
     * @param rpm     A RawParseModifier on which we call modify(...) passing the raw parsed ComponentDescription, that
     *                is before any parsing phases are carried out, so we have the opportunity to modify it before the
     *                phases are carried out
     * @param options the options for this parse
     * @return the parse results
     */
    private static ParseResults parseFile(String path, RawParseModifier rpm, ParseOptionSet options) {
        ParseResults results = new ParseResults();
        results.addReport("File: " + path + "\n");
        try {
            InputStream is = openFileUrl(path);
            parseInputStream(path, is, results, rpm, options);
            
            if (options.description || options.verbose && !options.quiet) {
                printPhase("sfAsComponentDescription", results.cd.toString());
            }

            results.addReport(", parsed in " + (results.parseDurationMillis) + " millisecs.");

            showConsole(options, results.cd);


        } catch (Exception e) {
            handleParseErrors(e, results, path, options, "<BR><BR>"
            );
        }
        return results;
    }

    /**
     * Parses a resource on the classpath.
     *
     * @param resource the resource to be parsed
     * @param rpm     A RawParseModifier , usually null
     * @param options the options for this parse
     * @return the parse results
     */
    private static ParseResults parseResource(String resource, RawParseModifier rpm, ParseOptionSet options) {
        ParseResults results = new ParseResults();
        try {
            InputStream is = openResource(resource);
            parseInputStream(resource, is, results, rpm, options);
        } catch (Exception e) {
            handleParseErrors(e, results, resource, options, "\n");
        }
        return results;
    }

    /**
     * Parse an input stream (which is closed afterwards) and fill in the report
     * @param path path of the stream, used to determine language
     * @param is input stream
     * @param results results to update
     * @param rpm parse modifier, usually null
     * @param options any options
     * @throws Exception on failure
     */
    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    private static void parseInputStream(String path, InputStream is, ParseResults results,
                                                   RawParseModifier rpm, ParseOptionSet options)
            throws Exception {
        String language = getLanguageFromUrl(path);
        Vector phaseList;
        long parseTime = System.currentTimeMillis();
        Phases top = parseStreamToPhases(results, is, rpm, language, options);

        phaseList = top.sfGetPhases();
        String phase;
        Vector<String> report = results.report;
        for (Object aPhaseList : phaseList) {
            phase = (String) aPhaseList;
            try {
                top = top.sfResolvePhase(phase);
                if (options.verbose && !options.quiet) {
                    printPhase(phase, top.toString());
                }
                report.add("   " + phase + " phase: OK");
            } catch (Exception ex) {
                report.add("   " + phase + " phase: FAILED!");
                throw ex;
            }
        }

        results.cd = top.sfAsComponentDescription();
        results.parseDurationMillis = System.currentTimeMillis() - parseTime;
    }

    /**
     * Handle parsing errors by adding more details to the report
     * @param thrown what went wrong
     * @param results the results to add to
     * @param path path of the source
     * @param options verbosity options.
     * @param split Message separator (ex. "\n");
     */
    private static void handleParseErrors(Throwable thrown, ParseResults results, String path,
                                          ParseOptionSet options, String split) {
        SFSystem.sfLog().err("'" + path + "': \n" + thrown + "\n", thrown);
        Vector<String> itemError = new Vector<String>();
        itemError.add(path);
        boolean printStack;
        if (thrown instanceof SmartFrogException) {
            itemError.add(((SmartFrogException) thrown).toString(split));
            printStack = options.verbose; 
        } else {
            printStack = true;
            itemError.add(thrown.toString());
        }
        //print the stack if verbose is set, or it is a non-SF exception (i.e. internal problems)
        if (printStack) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            thrown.printStackTrace(pw);
            pw.flush();
            pw.close();
            itemError.add(sw.toString());
        }
        results.errors.add(itemError);
    }

    public static Phases parseStreamToPhases(ParseResults results,
                                             InputStream is,
                                             RawParseModifier rpm,
                                             String language, ParseOptionSet options) throws Exception {
        Phases top;
        try {
            top = (new SFParser(language)).sfParse(is);
            if (options.verbose && !options.quiet) {
                printPhase("raw", top.toString());
            }
            results.addReport("   " + "raw phase: OK");
        } catch (Exception ex) {
            results.addReport("   " + "raw phase: FAILED!");
            throw ex;
        } finally {
            closeQuietly(is);
        }

        if (rpm != null) {
            rpm.modify(top);
        }
        return top;
    }

    private static InputStream openFileUrl(String fileUrl) throws SmartFrogParseException {
        InputStream is = null;
        is = SFClassLoader.getResourceAsStream(fileUrl);
        if (is == null) {
            String msg = MessageUtil.
                    formatMessage(MSG_URL_TO_PARSE_NOT_FOUND, fileUrl);
            throw new SmartFrogParseException(msg);
        }
        return is;
    }

    private static InputStream openResource(String resource) throws SmartFrogParseException {
        InputStream is = null;
        is = SFClassLoader.getResourceAsStream(resource);
        if (is == null) {
            String msg = MessageUtil.
                    formatMessage(MSG_URL_TO_PARSE_NOT_FOUND, resource);
            throw new SmartFrogParseException(msg);
        }
        return is;
    }


    public static void closeQuietly(Closeable is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }
    }


    /**
     * Optionally show a console
     *
     * @param options              options to look at
     * @param componentDescription the CD to display
     * @throws Exception
     */
    private static void showConsole(ParseOptionSet options, ComponentDescription componentDescription)
            throws Exception {
        if (options.showConsole) {
            SFDeployDisplay.startParserConsole("ParseConsole", 440, 600, "N", componentDescription, true);
            Thread.sleep(3600 * 100);
        }
    }

    /**
     * Parses a list of files, return the results.
     * This also adds the errors to the error reports, which is not reset before the run begins (for backwards
     * compatibility)
     * @param filenames the list of files to be parsed
     * @param options parser options
     * @return the list of parse results
     */
    private static Vector<ParseResults> parseFiles(Vector<String> filenames, ParseOptionSet options) {
        StringBuffer strb;
        Vector<Vector<String>> report = new Vector<Vector<String>>();
        Vector<ParseResults> parseResults = new Vector<ParseResults>(filenames.size());
         //reset the error report
        createErrorReport();
        Vector<Vector<String>> errors =  new Vector<Vector<String>>();
        //Loop through the vector
        for (String file : filenames) {
            try {
                strb = new StringBuffer();
                //If it's not an empty line
                if (file.trim().length() > 0) {
                    strb.append("-----------------------------------------------\n")
                            .append("-  Parsing: ")
                            .append(file)
                            .append("\n")
                            .append("-----------------------------------------------");
                    if (!options.quiet) {
                        SFSystem.sfLog().out(strb.toString());
                    }
                    ParseResults results = parseFile(file, null, options);
                    parseResults.add(results);
                    errors.addAll(results.errors);
                    Vector<String> output = results.report;
                    report.add(output);
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
                if (!options.quiet) {
                    SFSystem.sfLog().err(strb.toString(), thr);
                }
            }
        }
        if (options.statusReport) {
            printTotalReport(report);
        }
        if (options.statusReportHTML) {
            printTotalReportHTML(report);
            FileWriter newFile = null;
            try {
                String destFile = options.fileName + "_report.html";
                newFile = new FileWriter(destFile);
                newFile.write("<!doctype HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"><html>");
                newFile.write("<body>" + "\n");
                newFile.write("<font color=\"BLUE\" size=\"5\">Status report<font/>" + "\n");
                newFile.write("<table border=\"1\">" + "\n");
                newFile.write(printTotalReportHTML(report));
                newFile.write("<table/>");
                newFile.write("<font color=\"BLUE\" size=\"5\">Error report<font/>" + "\n");
                newFile.write("<table border=\"1\">" + "\n");
                newFile.write(printTotalReportHTML(errors));
                newFile.write("<table/>");
                newFile.write("<body/>" + "\n");
                newFile.write("<html/>" + "\n");
                newFile.flush();
                newFile.close();
                newFile = null;
                SFSystem.sfLog().out("Report created: " + destFile);
            } catch (IOException e) {
                SFSystem.sfLog().error(e, e);
            } finally {
                if (newFile != null) {
                    try {
                        newFile.close();
                    } catch (IOException ignored) {

                    }
                }
            }
        }
        addErrorsToErrorReport(errors);
        return parseResults;
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
            ParseOptionSet optionSet = new ParseOptionSet(args);
            //stack trace flag comes from the verbose option
            Logger.logStackTrace = optionSet.verbose;



            showDiagnostics(optionSet);

            if (optionSet.errorString != null) {
                if (optionSet.help) {
                    SFSystem.sfLog().out("Help: \n" + optionSet.errorString);
                    ExitCodes.exitWithError(ExitCodes.EXIT_CODE_SUCCESS);
                } else {
                    SFSystem.sfLog().out("Error: " + optionSet.errorString);
                    ExitCodes.exitWithError(ExitCodes.EXIT_ERROR_CODE_BAD_ARGS);
                }
            }

            if (optionSet.loadDescriptionsFromFile) {
                parseFiles(optionSet.filesList, optionSet);
            } else {
                ParseResults results = parseFile(optionSet.fileName, null, optionSet);
                extractErrors(results);
                if (optionSet.statusReport) {
                    printItemReport(results.report);
                }
            }
            // If we found errors during parsing we force exit.
            if (!errorReport.isEmpty()) {
                SFSystem.sfLog().out("Error detected. Check report.");
                SFSystem.sfLog().out("SFParse: FAILED");
                ExitCodes.exitWithError(ExitCodes.EXIT_ERROR_CODE_GENERAL);
            } else {
                SFSystem.sfLog().out("SFParse: SUCCESSFUL");
                ExitCodes.exitWithError(ExitCodes.EXIT_CODE_SUCCESS);
            }
        } catch (Throwable thr) {
            SFSystem.sfLog().error("Exception "+ thr, thr);
        }

    }

    /**
     * Shows the version info of the SmartFrog system.
     */
    private static void showVersionInfo() {
        SFSystem.sfLog().out("\nParser - " + Version.versionString());
        SFSystem.sfLog().out(Version.copyright());
        SFSystem.sfLog().out(" ");
    }


    /**
     * Prints the phase.
     *
     * @param phaseName the phase name
     * @param result    the result to be printed
     */
    private static void printPhase(String phaseName, String result) {
        SFSystem.sfLog().out(
                "******************** PHASE " + phaseName +
                        " *********************");
        SFSystem.sfLog().out(result);
        SFSystem.sfLog().out("\n\n\n\n\n");
    }

    /**
     * Prints the total parsing report.
     *
     * @param report the report to be printed
     */
    public static void printTotalReport(Vector<Vector<String>> report) {
        for (Vector<String> entry : report) {
            printItemReport(entry);
        }
    }

    /**
     * Print the parsing report for an item.
     *
     * @param report the report to be printed
     */
    public static void printItemReport(Vector<String> report) {
        StringBuilder st = new StringBuilder("STATUS REPORT: ");
        for (String line : report) {
            st.append(line);
        }
        SFSystem.sfLog().out(st.toString());
    }

    /**
     * Prints the total parsing report in html format.
     *
     * @param reportList the report to be printed
     * @return the string form of the parse report
     */
    public static String printTotalReportHTML(Vector<Vector<String>> reportList) {
        StringBuilder reportHTML = new StringBuilder();
        for (Vector<String> entry : reportList) {
            reportHTML.append(printItemReportHTML(entry));
        }
        return reportHTML.toString();
    }

    /**
     * Print the parsing report for an item in html format.
     *
     * @param report the report to be printed
     * @return the output as a table row
     */
    public static String printItemReportHTML(Vector<String> report) {
        StringBuilder st = new StringBuilder("<tr>" + "\n");
        for (String line : report) {
            st.append("<td>").append(line).append("<td/>\n");
        }
        st.append("<tr/>\n");
        return st.toString();
    }

    /**
     * Shows diagnostics report
     *
     * @param options OptionSet
     */
    private static void showDiagnostics(ParseOptionSet options) {
        if (options.diagnostics) {
            StringBuffer report = new StringBuffer();
            Diagnostics.doReport(report);
            SFSystem.sfLog().out(report.toString());
        }
    }

    public interface RawParseModifier {
        public void modify(ComponentDescription cd);
    }

    /**
     * This is the class to use
     */
    public static class ParseResults {
        public ComponentDescription cd;
        public Vector<String> parsed;
        public Vector<Vector<String>> errors = new Vector<Vector<String>>();
        public Vector<String> report = new Vector<String>();
        public long parseDurationMillis;

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public void addReport(String reportEntry) {
            report.add(reportEntry);
        }
    }
}
