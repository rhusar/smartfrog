package org.smartfrog.services.runcmd;

/**
 *  Title: SFGui Description: Copyright: Copyright (c) 2001 Company: HP Labs
 *  Bristol
 *
 *@author     Julio Guijarro
 *@version    0.002 Modified to use cmd as String[]
 */

import org.smartfrog.services.runcmd.StreamGobbler;
import java.io.*;
import java.util.*;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

//public class RunProcess implements Runnable {
/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 October 2001
 */
public class RunProcess extends Thread {

   /**
    *  Description of the Field
    */
   public int logger = 2;
   // Default 2
   // 5- info log, 0 - Critical. Use -1 to avoid log
   boolean printStack = true;
   boolean formatMsg = true;

   String nameProcess = "defaultNameProcess";
   String[] command = {"defaultCmd"};
   File workDir = null;
   boolean stop = false;
   boolean reStart = false;
   boolean shouldTerminate = false;
   //Should teminate process if spanned process finishes?
   boolean shouldDetach = false;
   //Should teminate process if spanned process finishes?
   String status = "stopped";
   long delay = (2 * 1000);
   // 2 seconds
   String[] envProp = null;

   // Process Data
   Runtime runtime = Runtime.getRuntime();
   Process subProcess = null;
   // Process executed
   Thread process = null;
   private Prim sfObj = null;

   // Thread that executes the process.

   /**
    *  Constructor for the RunProcess object
    *
    *@param  command  Command to exec
    */
   public RunProcess(String[] command) {
      this.command = command;
   }

   /**
    *  Constructor for the RunProcess object
    *
    *@param  command     Command to exec
    *@param  workDirStr  Working directory for the spanned process
    */
   public RunProcess(String[] command, String workDirStr) {
      this.command = command;
      this.workDir = new File(workDirStr);
   }

   /**
    *  Constructor for the RunProcess object
    *
    *@param  command      Command to exec
    *@param  workDirStr   Working directory for the spanned process
    *@param  NameProcess  Name for the Process
    */
   public RunProcess(String[] command, String workDirStr, String NameProcess) {
      this.command = command;
      this.workDir = new File(workDirStr);
      this.nameProcess = NameProcess;
   }

   /**
    *  Constructor for the RunProcess object
    *
    *@param  command      Command to exec
    *@param  workDirStr   Working directory for the spanned process
    *@param  NameProcess  Name for the Process
    *@param  envProp      Environment properties
    */
   public RunProcess(String[] command, String workDirStr, String NameProcess, String[] envProp) {
      this.command = command;
      this.workDir = new File(workDirStr);
      this.nameProcess = NameProcess;
      this.envProp = envProp;
   }

   /**
    *  Sets the sFObj attribute of the RunProcess object
    *
    *@param  sfObj  The new sFObj value
    */
   public void setSFObj(Object sfObj) {
      //System.out.println("setting Prim to terminate...");
      if (sfObj instanceof Prim) {
         this.sfObj = (Prim)sfObj;
         //System.out.println("setted..");
      }
   }

   /**
    *  Sets the delay attribute of the RunProcess object
    *
    *@param  delay  The new delay value
    */
   public void setDelay(long delay) {
      this.delay = delay;
   }

   /**
    *  Sets the repeat attribute of the RunProcess object
    *
    *@param  repeat  The new repeat value
    */
   public void setReStart(boolean repeat) {
      this.log("reStart=" + repeat, 5);
      this.reStart = repeat;
   }


   /**
    *  Sets the shouldTerminate attribute of the RunProcess object
    *
    *@param  shouldTerminate  The new shouldTerminate value
    */
   public void setShouldTerminate(boolean shouldTerminate) {
      this.log("shouldTerminate=" + shouldTerminate, 5);
      this.shouldTerminate = shouldTerminate;
   }

   /**
    *  Sets the shouldDetach attribute of the RunProcess object
    *
    *@param  shouldDetach  The new shouldDetach value
    */
   public void setShouldDetach(boolean shouldDetach) {
      this.log("shouldDetach=" + shouldDetach, 5);
      this.shouldDetach = shouldDetach;
   }

   /**
    *  Sets the logLevel attribute of the RunProcess object
    *
    *@param  logLevel  The new logLevel value
    */
   public void setLogLevel(int logLevel) {
      this.logger = logLevel;
   }

   /**
    *  Gets the nameProcess attribute of the RunProcess object
    *
    *@return    The nameProcess value
    */
   public String getNameProcess() {
      return this.nameProcess;
   }

   /**
    *  Gets the status attribute of the RunProcess object
    *
    *@return    The status value
    */
   public String getStatus() {
      return this.status;
   }

   /**
    *  Gets the delay attribute of the RunProcess object
    *
    *@return    The delay value
    */
   public long getDelay() {
      return this.delay;
   }

   /**
    *  Gets the command attribute of the RunProcess object
    *
    *@return    The command value
    */
   public String getCommand() {
      if (this.command != null) {
         return this.arrayToString(command);
      } else {
         return "";
      }
   }

   /**
    *  Gets the workDir attribute of the RunProcess object
    *
    *@return    The workDir value
    */
   public String getWorkDir() {
      if (this.workDir != null) {
         return workDir.getPath();
      } else {
         return "";
      }
   }

   /**
    *  Gets the envProp attribute of the RunProcess object
    *
    *@return    The envProp value
    */
   public String getEnvProp() {
      if (this.envProp != null) {
         return this.arrayToString(envProp);
      } else {
         return "null";
      }
   }

   /**
    *  Description of the Method
    */
   public void kill() {
      this.stop = true;
      this.subProcess.destroy();
      this.status = "stopped";
   }


   /**
    *  Description of the Method
    */
   public void terminate(String type) {
      if ((this.sfObj != null) && (this.sfObj instanceof Prim)) {
         try {
            this.sfObj.sfTerminate(new TerminationRecord(type, "Spanned process finished:" + this.getNameProcess(), null));
         } catch (Exception ex) {
            if (printStack) {
               ex.printStackTrace();
            }
         }

      }
   }

   /**
    *  Description of the Method
    */
   public void detach() {
      if ((this.sfObj != null) && (this.sfObj instanceof Prim)) {
         try {
            this.sfObj.sfDetach();
         } catch (Exception ex) {
            System.out.println("RunProcess.detach:" + ex.getMessage());
            if (printStack) {
               ex.printStackTrace();
            }
         }
      }
   }


   /**
    *  Description of the Method
    */
   public void clean() {
      this.subProcess.destroy();
      this.status = "stopped";
   }

   /**
    *  Main processing method for the RunProcess object
    */
   public void run() {
      String terminationType = "normal";
      do {
         try {
            //this.log("Command Start: "+command,3);
            this.log("RunProcessInfo > " + this.toString(), 5);
            this.log("Started "+"[" + this.nameProcess + "]> " + this.getCommand() + "| workdir: " + this.getWorkDir() + " ", 2);

            subProcess = runtime.exec(command, envProp, workDir);

            //TODO: make get methods and change the way RunProcess access to this references!  Accesing to variables directly.
            //CHECK: check the way RunProcess access to this references to Stream objects this is a quick hack! Accesing to variables directly

            StreamGobbler outputGobbler;
            // any output?
            //this.log("RunProcess > " + "Creating gobblers for new process, "); // + (((SFRunCommand)this.sfObj).outputStreamObj).toString() +", " + this.toString(), 5);
            if ((this.sfObj instanceof SFRunCommand) && (((SFRunCommand)this.sfObj).outputStreamObj != null)) {
               outputGobbler = new StreamGobbler(subProcess.getInputStream(), "[" + this.nameProcess + "] " + "OUT", ((SFRunCommand)this.sfObj).outputStreamObj.getOutputStream(), ((SFRunCommand)this.sfObj).printMsgImp);
               this.log(" RunProcess "+"[" + this.nameProcess + "]> " + "gobbler (OUTPUT) redirected.", 5);
            } else {
               outputGobbler = new StreamGobbler(subProcess.getInputStream(), "[" + this.nameProcess + "] " + "OUT", null, ((SFRunCommand)this.sfObj).printMsgImp);
               this.log(" RunProcess "+"[" + this.nameProcess + "]> " + "gobbler (OUTPUT) created (not redirected).", 5);
            }
            // any error message?
            //StreamGobbler errorGobbler = new StreamGobbler(subProcess.getErrorStream(), "[" + this.nameProcess + "] " + "ERR");
            StreamGobbler errorGobbler;
            if ((this.sfObj instanceof SFRunCommand) && (((SFRunCommand)this.sfObj).errorStreamObj != null)) {
               errorGobbler = new StreamGobbler(subProcess.getErrorStream(), "[" + this.nameProcess + "] " + "ERR", ((SFRunCommand)this.sfObj).outputStreamObj.getOutputStream(),((SFRunCommand)this.sfObj).printErrMsgImp);
               this.log(" RunProcess "+"[" + this.nameProcess + "]> " + "gobbler (ERROR) redirected.", 5);
            } else {
               errorGobbler = new StreamGobbler(subProcess.getErrorStream(), "[" + this.nameProcess + "] " + "ERR", null, ((SFRunCommand)this.sfObj).printErrMsgImp);
               this.log(" RunProcess "+"[" + this.nameProcess + "]> " + "gobbler (ERROR) created (not redirected).", 5);
            }
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
            this.log("Process (re)started,"+"[" + this.nameProcess + "]> ", 3);
            status = "running";
            int exitVal = subProcess.waitFor();
            // wait until process finishes

            this.log("Exit Val "+"[" + this.nameProcess + "]>"+ exitVal + "", 2);

            this.clean();

            if(exitVal == 0) {
                terminationType = "normal";
                if ((this.sfObj instanceof SFRunCommand) && (((SFRunCommand)this.sfObj).printMsgImp != null)) {
                   ((SFRunCommand)this.sfObj).printMsgImp.printMsg("NORMAL Termination. "+"Exit Val "+"[" + this.nameProcess + "]>"+ exitVal + "");
                }
            } else {
                terminationType = "abnormal";
                if ((this.sfObj instanceof SFRunCommand) && (((SFRunCommand)this.sfObj).printErrMsgImp != null)) {
                  ((SFRunCommand)this.sfObj).printErrMsgImp.printErrMsg("ABNORMAL Termination. "+"Exit Val "+"[" + this.nameProcess + "]>"+ exitVal + "");
                }
            }

            if (stop) {
               break;
            }

            Thread.sleep(delay);
         } catch (Exception ex) {
            terminationType = "abnormal";
            this.log("Problem starting "+"[" + this.nameProcess + "]> " + ex.getMessage() + ", " + this.toString(), 1);
            if ((this.sfObj instanceof SFRunCommand) && (((SFRunCommand)this.sfObj).printErrMsgImp != null)) {
              try {
                ((SFRunCommand)this.sfObj).printErrMsgImp.printErrMsg("ABNORMAL Termination. "+""+"[" + this.nameProcess + "]>"+ "Problem starting application: "+ ex.getMessage() + "");
              } catch (Exception exc) {
              }
            } else {
               System.err.println("ABNORMAL Termination. "+""+"[" + this.nameProcess + "]>"+ "Problem starting application: "+ ex.getMessage() + "");
            }
            if (printStack) {
               ex.printStackTrace();
            }
            try {
               this.kill();
               Thread.sleep(10 * 1000);
            } catch (Exception e) {
               //e.printStackTrace();
            }
         }
      } while (reStart);
      if (shouldTerminate) {
        // Do a propper terminate of the whole lot!!!
        if (shouldDetach) detach();
           terminate(terminationType);
        } else if (shouldDetach) {
           detach();
        }
   }


   /**
    *  Description of the Method
    *
    *@return    Description of the Returned Value
    */
   public String toString() {
      return ("RunProcess: " + this.getNameProcess() +
            ", Status: " + this.getStatus() +
            ", Command: " + this.getCommand() +
            ", WorkDir:" + this.getWorkDir() +
            ", EnvProp:" + this.getEnvProp());
   }

   /**
    *  Description of the Method
    *
    *@param  message   Description of Parameter
    *@param  severity  Description of Parameter
    */
   void log(String message, int severity) {
      //System.out.println( "["+this.nameProcess+"] "+message,"RUNProcess",severity);
      try {
         //if (logger != false ) {
         if (logger >= severity) {
            message = "[" + this.nameProcess + "] " + message;
            if (formatMsg) message = formatMsg(message);
            System.out.println("[" + this.nameProcess + "] " + message + ", RUNProcess, " + severity);
         }
      } catch (Exception e) {
         if (printStack) {
            e.printStackTrace();
         }
      }
   }


   /**
    *  Description of the Method
    *
    *@param  array  Description of Parameter
    *@return        Description of the Returned Value
    */
   private String arrayToString(String[] array) {
      int i = 0;
      StringBuffer stringB = new StringBuffer();
      while (i < array.length) {
         stringB.append(array[i++]);
         stringB.append(" ");
      }
      return stringB.toString();
   }

    /**
    *  Description of the Method
    *
    *@param  msg  Description of Parameter
    *@return      Description of the Returned Value
    */
   private String formatMsg(String msg) {
      msg = "[" + (new java.text.SimpleDateFormat("HH:mm:ss.SSS dd/MM/yy").format(new java.util.Date(System.currentTimeMillis()))) + "] " + msg;
      return msg;
   }

//  public static void main(String[] args) {
//    RunProcess runProcess1 = new RunProcess("dir");
//  }
}
