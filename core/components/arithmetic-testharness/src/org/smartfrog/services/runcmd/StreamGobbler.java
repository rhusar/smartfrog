package org.smartfrog.services.runcmd;



/**
 * Title:        SFGui Experiment001
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      HP Labs Bristol
 * @author Julio Guijarro
 * @version 0.001
 */


import java.io.*;
import java.util.*;

import org.smartfrog.services.display.PrintErrMsgInt;
import org.smartfrog.services.display.PrintMsgInt;

public class StreamGobbler extends Thread
{
    InputStream is;
    String type;
    OutputStream os;
    boolean printToOutputStream=false;
    boolean printToOutputMsg=false;
    boolean printToErrMsg=false;
    boolean printToStdOutput=true;
    boolean passType=false;
    PrintErrMsgInt printerError=null;
    PrintMsgInt    printerOutput=null;


    public StreamGobbler(InputStream is, String type)
    {
        this(is, type, null);
    }

    public StreamGobbler(InputStream is, String type, OutputStream redirect)
    {
        this.is = is;
        this.type = type;
        this.os = redirect;
        if (os!=null) {
            this.printToOutputStream=true;
        }

    }

    public StreamGobbler(InputStream is, String type, OutputStream redirect, Object printer)
    {
        this.is = is;
        this.type = type;
        this.os = redirect;
        if (os!=null) {
            this.printToOutputStream=true;
        }
        if (printer!=null) {
           if (printer instanceof PrintMsgInt){
              this.printerOutput=(PrintMsgInt)printer;
              this.printToOutputMsg=true;

           } else if (printer instanceof PrintErrMsgInt) {
              this.printerError=(PrintErrMsgInt)printer;
              this.printToErrMsg=true;
           }
        }

    }


    public void run()
    {
        if (type==null){
          type="GOBBLER";
        }
        try {
		System.out.println("IN RUN STREAMGOBBLER");
            PrintWriter pw = null;
            if (this.os != null)
                pw = new PrintWriter(this.os);

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            boolean  next = true;
            while ( next )
            {
//              try {
                  if ((line = br.readLine()) != null){
                  }else {
                      next = false;
                  }
              //To std Output
              if (printToStdOutput) {
                System.out.println(type + " > " + line);
              }
              if (passType) line=type + " > " + line;
              // To External OutputStream
              if (this.printToOutputStream){
                    pw.println(line);
                    pw.flush();
              }
              // To External Obj implementing printMsgInt or printErrMsgInt
              try {
                  // Print to one or the other
                  if (this.printToOutputMsg) {
                    this.printerOutput.printMsg(line);
                  } else if (this.printToErrMsg) {
                    this.printerError.printErrMsg(line);
                  }
               } catch (Exception ex){
                  //ex.printStackTrace();
               }
//              }
//              catch (IOException ioe) {
//                  System.err.println("gobbler:" + type + " "+ ioe.getMessage());
                  //ioe.printStackTrace();
//              }
            } // while
            // Flush Stream before finishing!
            if (this.printToOutputStream) {
                pw.flush();
            }

        } catch (IOException ioe)
            {
               ioe.printStackTrace();
            }
    }

    public void setPrintToStdOutput (boolean bool){
        this.printToStdOutput=bool;
    }

    //Should add type to printMsgs and Streams ...
    public void setPassType (boolean bool){
        this.passType=bool;
    }
}


