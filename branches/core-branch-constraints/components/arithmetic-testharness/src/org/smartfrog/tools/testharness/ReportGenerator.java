package org.smartfrog.tools.testharness;


import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import org.smartfrog.SFSystem;



public class ReportGenerator {

   String fileName="Distributed_TestHarness";
   public static Vector  report = new Vector();
   FileWriter newFile = null;



  public void generateReport()
  {

           System.out.println("VEL :::::::::::::::::::: Report Generator_generateReport()" +  report.size());
          // printItemReportHTML(report);
           try {
              newFile = new FileWriter(fileName+"_report.html");
              newFile.write("<!doctype HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"><html>");
              newFile.write("<body>"+"\n");
              newFile.write("<font color=\"BLUE\" size=\"5\">Distributed Test report<font/>"+"\n");
              newFile.write("<table border=\"1\">"+"\n");
              newFile.write(printTotalReportHTML(report));
              newFile.write("<table/>");
            //  newFile.write("<font color=\"BLUE\" size=\"5\">Error report<font/>"+"\n");
            //  newFile.write("<table border=\"1\">"+"\n");
            //  newFile.write(printTotalReportHTML(errorReport));
            //  newFile.write("<table/>");
              newFile.write("<body/>"+"\n");
              newFile.write("<html/>"+"\n");
              newFile.flush();
              newFile.close();
          //    SFSystem.sflog().out("Report created: "+fileName+"_report.html");
            } catch (IOException e) {
              if (SFSystem.sfLog().isErrorEnabled()){
         //       SFSystem.sflog().error(e);
              }
           }
  }

    /**
    *  Prints the total parsing report in html format.
    *
    *  @param myreport the report to be printed
    *  @return the string form of the parse report
    */
    private static String printTotalReportHTML(Vector myreport){

      StringBuffer reportHTML = new StringBuffer();
       reportHTML.append(printItemReportHTML(myreport));

      for (Enumeration e = myreport.elements(); e.hasMoreElements(); )
      {
        reportHTML.append(printItemReportHTML((Vector) e.nextElement()));
      }

      return reportHTML.toString();
    }

   /**
    *  Print the parsing report for an item in html format.
    *
    *  @param report the report to be printed
    */
    private static String printItemReportHTML(Vector report) {
      StringBuffer st = new StringBuffer("<tr>"+"\n");
      for (Enumeration e = report.elements(); e.hasMoreElements(); ) {
        st.append("<td>"+e.nextElement().toString()+"<td/>"+"\n");
      }
      st.append("<tr/>"+"\n");
      return st.toString();
    }


      public static void main(String[] args) {

          ReportGenerator rg = new ReportGenerator();
          String str= "test1";
  //        ReportGenerator.report.add(str);
//          rg.generateReport();

      }
  }
