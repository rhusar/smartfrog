package org.smartfrog.services.runcmd;

/**
 *  Title: SFRunCommand Description: Class to run an external process and to
 *  reload it if it crashes! Copyright: Copyright (c) HP Labs Company: HP @
 *  Julio Guijarro & Patrick Goldsack
 *
 *@version    1.0
 */

import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.services.runcmd.InfoProcess;
import org.smartfrog.services.runcmd.OutputStreamIntf;
import org.smartfrog.services.display.PrintErrMsgInt;
import org.smartfrog.services.display.PrintMsgInt;

import java.rmi.*;
import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.io.*;

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 October 2001
 */
public class SFRunCommand extends PrimImpl implements Prim, RunCommandInt {

   final String varOutputMsgTo = "outputMsgTo";
   // Object that implements org.smartfrog.services.display.PrintMsgInt       //sfServices.sfDisplay uses it
   final String varErrorMsgTo = "errorMsgTo";
   final String varStartAttVector = "startAttVector";
   final String varStopAttVector = "stopAttVector";
    // Object that implements org.smartfrog.services.display.PrintErrMsgInt    //sfServices.sfDisplay uses it

   //Printers...
   public PrintMsgInt printMsgImp = null;
   public PrintErrMsgInt printErrMsgImp = null;

   // to pass some of the streams from the spanned process to another objects!
   //TODO: make get methods and change the way RunProcess access to this references!
   //CHECK: check the way RunProcess access to this references to Stream objects this is a quick hack!

   /**
    *  Decides in which phase the external application is run.
    */

   boolean runDuringDeploy = false;

   /**
    *  Description of the Field
    */
   public OutputStreamIntf outputStreamObj = null;
   /**
    *  Description of the Field
    */
   public StreamIntf inputStreamObj = null;
   /**
    *  Description of the Field
    */
   public StreamIntf errorStreamObj = null;

   // Attributes
   /**
    *  Description of the Field
    */
   protected boolean isBatch = false;
   //Path for from were to run the process
   /**
    *  Description of the Field
    */
   protected String workDir = ".";

   /**
    *  Description of the Field
    */
   protected String processId = "";
   //Name of the process
   /**
    *  Description of the Field
    */
   protected String processName = "defaultprocessName";

   /**
    *  Description of the Field
    */
   protected String startCmd = null;
   /**
    *  Description of the Field
    */
   protected Vector startAtt = new Vector();

   /**
    *  Description of the Field
    */
   protected String stopCmd = null;
   /**
    *  Description of the Field
    */
   protected Vector stopAtt = new Vector();
   /**
    *  Description of the Field
    */
   protected String[] envProp = null;

   /**
    *  Description of the Field
    */
   protected boolean autoStart = true;

   // Should the spanned process be restarted automatically.
   /**
    *  Description of the Field
    */
   protected boolean autoReStart = false;
   // delay before a process is restated
   /**
    *  Description of the Field
    */
   protected long reStartDelay = 1 * 1000;
   // 1 Second
   // Indicates if the process should terminate when the spanned process finishes
   /**
    *  Description of the Field
    */
   protected boolean shouldTerminate = false;
   /**
    *  Description of the Field
    */
   protected boolean shouldDetach = false;

   // Process run
   /**
    *  Description of the Field
    */
   protected InfoProcess process = null;

   //SF Attribute names:

   //process
   final String varSFProcessId = "processId";
   final String varSFProcessName = "processName";

   //static final String varSFStartHost="startHost";
   // Is Batch?
   final String varIsBatch = "isBatch";
   final String varRunDuringDeploy = "runDuringDeploy";

   //start
   final String varSFCmdStart = "startCmd";
   final String varSFAttStart = "startAtt";
   //stop
   final String varSFCmdStop = "stopCmd";
   final String varSFAttStop = "stopAtt";
   final String varAutoStart = "autoStart";
   //restart
   final String varAutoReStart = "autoReStart";
   final String varDelayReStart = "delayReStart";
   // Indicates if the process should terminate when the spanned process finishes
   final String varShouldTerminate = "shouldTerminate";
   final String varShouldDetach = "shouldDetach";
   // processWorkingDirectory
   final String varSFWorkDir = "workDir";
   final String varEnvProp = "envProperties";
   // OutPut and Input streams
   final String varOutputStream = "outputStream";
   final String varErrorStream = "errorStream";
   // Level log
   final String varLogger = "logLevel";

   int logger = -1;
   // 5- info log, 1 - Critical. Use -1 to avoid log
   boolean printStack = true;


   /**
    *  Constructor for the SFRunCommand object
    *
    *@exception  RemoteException  Description of Exception
    */
   public SFRunCommand() throws RemoteException {
      super();
   }

   /**
    *  Sets the logLevel attribute of the SFRunCommand object
    *
    *@param  logLevel  The new logLevel value
    */
   public void setLogLevel(int logLevel) {
      this.logger = logLevel;
   }

   /**
    *  Sets the restart attribute of the SFRunCommand object
    *
    *@param  isRestart  The new restart value
    */
   public void setRestart(boolean isRestart) {
      this.autoReStart = isRestart;
      if (process != null) {
         process.setAutoReStart(this.autoReStart);
      }
   }

    /**
    *  Sets the start attribute of the SFRunCommand object
    *
    *@param  isStart  The new restart value
    */
   public void setStart(boolean isStart) {
      this.autoStart = isStart;
   }


   /**
    *  Sets the delayRestart attribute of the SFRunCommand object
    *
    *@param  delay  The new delayRestart value
    */
   public void setDelayRestart(long delay) {
      if (delay >= 0) {
         this.reStartDelay = delay;
         if (process != null) {
            process.setRestartDelay(delay);
         }
      }
   }

   /**
    *  Sets the shouldTerminate attribute of the SFRunCommand object
    *
    *@param  shouldTerminate  The new shouldTerminate value
    */
   public void setShouldTerminate(boolean shouldTerminate) {
      this.shouldTerminate = shouldTerminate;
      if (process != null) {
         process.setShouldTerminate(this.shouldTerminate);
      }
   }


   /**
    *  Sets the shouldDetach attribute of the SFRunCommand object
    *
    *@param  shouldDetach  The new shouldDetach value
    */
   public void setShouldDetach(boolean shouldDetach) {
      this.shouldDetach = shouldDetach;
      if (process != null) {
         process.setShouldDetach(this.shouldDetach);
      }
   }

   /**
    *  Gets the cmd attribute of the SFRunCommand object
    *
    *@return    The cmd value
    */
   public String getCmd() {
      if (process != null) {
         return process.getCmd();
      } else {
         return "";
      }
   }

   /**
    *  Gets the status attribute of the SFRunCommand object
    *
    *@return    The status value
    */
   public String getStatus() {
      if (process != null) {
         return process.getStatus();
      } else {
         return "";
      }
   }

    public String getProcessName (){
      if (process != null) {
         return process.getProcessName();
      } else {
         return "";
      }
    }

   /**
    *  Description of the Method
    *
    *@exception  Exception  Description of Exception
    */
   public void sfDeploy() throws SmartFrogException, RemoteException {
      log("Deploying...", 3);
      super.sfDeploy();
      readSFAttributes();
      //createProcess();
      if (runDuringDeploy) {
            //process.start();
            { this.start();}
      }
      log("SFdeployed", 3);

   }


   /**
    *  Description of the Method
    *
    *@exception  Exception  Description of Exception
    */
   public void reloadDescription() {
      //Reset values:
      workDir = ".";
      processId = "";
      processName = "defaultprocessName";
      startCmd = null;
      startAtt = new Vector();
      String stopCmd = null;
      stopAtt = new Vector();
      String[] envProp = null;
      autoStart = true;
      autoReStart = false;
      reStartDelay = 1 * 1000;
      shouldTerminate = false;
      shouldDetach = false;

      log("Reloading SF description", 3);
      readSFAttributes();
      //createProcess();
      log("SF description reloaded", 3);
   }



   /**
    *  Description of the Method
    *
    *@exception  Exception  Description of Exception
    */
   public void sfStart() throws SmartFrogException, RemoteException {
      log("Starting...", 3);
      super.sfStart();
      //if ((process != null)&&(this.autoStart)) {
      if (!runDuringDeploy) {
         if ((this.autoStart)) {
            //process.start();
            { this.start();}
         }
      }
      log("SFstarted", 3);
   }



   /**
    *  Description of the Method
    *
    *@param  r  Description of Parameter
    */
   public void sfTerminateWith(TerminationRecord r) {
      try { this.stop(); } catch (Exception e) {}
      try {
            Thread.sleep(this.reStartDelay);
      }
         catch (InterruptedException iex) {
         log( iex.getMessage(), 2);
      }
      log("SFterminateWith " + r.toString(), 3);
      try { this.kill(); } catch (Exception e) {}
      super.sfTerminateWith(r);
   }


   //  External methods

   /**
    *  Description of the Method
    */
   public void start() {
      log("Starting (.start())..."+this.getProcessName(), 3);
      createProcess();
      if (process != null) {
         process.start();
       log("SFstarted (.start()).", 3);
      }
   }


   /**
    *  Description of the Method
    */
   public void stop() {
      if (process != null) {
         process.stop();
      }
   }


   /**
    *  Description of the Method
    */
   public void kill() {
      if (process!=null){
         try {
            process.kill();
         }
         catch (Exception ex) {
         }
         process = null;
      }
   }


   /**
    *  Description of the Method
    *
    *@return    Description of the Returned Value
    */
   public String getNotifierId() {
      return (processName + processId);
   }

   /**
    *  Description of the Method
    *
    *@return    Description of the Returned Value
    */
   public String toString() {
      if (process != null) {
         return process.toString();
      } else {
         return "";
      }
   }


   /**
    *  Log - Writes messages in the standart output
    *
    *@param  severity  Description of Parameter
    *@param  message   Description of Parameter
    */
   private void log(String message, int severity) {
      try {
         //if (logger != false ) {
         if (logger >= severity) {
            //System.out.println("  LOG: Process "+ notifierId()+"  msg:" +  message + ", serverity: "+ severity);
            System.out.println("[" + this.getNotifierId() + "] " + message + ", SFRunCommand, " + severity);
         }
      } catch (Exception e) {
         if (printStack != false) {
            e.printStackTrace();
         }
      }
   }

   /**
    *  Description of the Method
    */
   private void createProcess() {
      // Delete previous process if it exists.
      this.kill();

      String[] cmdGeneral = {"", ""};
      if (isBatch) {
         String osName = System.getProperty("os.name");
         if (osName.equals("Windows 2000") || osName.equals("Windows NT")|| osName.equals("Windows XP")) {
            cmdGeneral[0] = "cmd.exe";
            cmdGeneral[1] = "/C";
         } else if (osName.equals("Windows 95") || osName.equals("Windows 98")) {
            cmdGeneral[0] = "command.exe";
            cmdGeneral[1] = "/C";

         } else {
            // for linux or unix
            cmdGeneral[0] = "bash";
         }
      }
      try {
         String[] globalStartCmd = this.createCmd(cmdGeneral, startCmd, startAtt);
         String[] globalStopCmd = this.createCmd(cmdGeneral, stopCmd, stopAtt);
         process = new InfoProcess(this.getNotifierId(), globalStartCmd, globalStopCmd, workDir, envProp);
         log("InfoProcess: "+this.getNotifierId() + " created.", 5);
         process.setAutoReStart(this.autoReStart);
         process.setRestartDelay(this.reStartDelay);
         process.setShouldTerminate(this.shouldTerminate);
         process.setShouldDetach(this.shouldDetach);
         process.setLogLevel(this.logger);
         if (this.logger >= 5) {
            sfAddAttribute("GlobalStartCmd", this.arrayToString(globalStartCmd));
            sfAddAttribute("GlobalStopCmd", this.arrayToString(globalStopCmd));
         }
//         if (this == null) {
//            System.out.println("This is null");
//         }
         process.setSFObj(this);
      } catch (Exception e) {
         System.err.println("Error creating InfoProcess:" + e.getMessage());
         if (printStack) {
            e.printStackTrace();
         }
      }
   }

   /**
    *  Description of the Method
    */
   private void readSFAttributes() {
      try {
         try {
            outputStreamObj = (OutputStreamIntf)sfResolve(this.varOutputStream);
         } catch (SmartFrogResolutionException e) {
            log(varOutputStream + " not found.", 5);
         } catch (Exception ex){
            ex.printStackTrace();
         }

         try {
            errorStreamObj = (StreamIntf)sfResolve(this.varErrorStream);
         } catch (SmartFrogResolutionException e) {
            log(varErrorStream + " not found.", 5);
         } catch (Exception ex){
            ex.printStackTrace();
         }


         //To redirect output to printer
            try {
                Object obj = sfResolve(varOutputMsgTo);
                //System.out.println("reference to" +varOutputMsgTo+ "found");
                if (obj instanceof PrintMsgInt) {
                    printMsgImp = (PrintMsgInt) obj;
                } else {
                    log("Wrong object in " + varOutputMsgTo, 1);
                }
            } catch (SmartFrogResolutionException e) {
                log(varOutputMsgTo + " not found.", 5);
            }

            //To redirect error to printer
            try {
                Object obj = sfResolve(varErrorMsgTo);
                //System.out.println("reference to" +varErrorMsgTo+ "found");
                if (obj instanceof PrintErrMsgInt) {
                    printErrMsgImp = (PrintErrMsgInt) obj;
                } else {
                    log("Wrong object in " + varErrorMsgTo, 1);
                }
            } catch (SmartFrogResolutionException e) {
                log(varErrorMsgTo + " not found.", 5);
            }


         try {
            this.logger = ((Integer)sfResolve(varLogger)).intValue();
         } catch (SmartFrogResolutionException e) {
            log(varLogger + " not found.", 5);
         }

         //Mandatory
         processName = (String)sfResolve(varSFProcessName);

         //     // Not mandatory
         try {
            processId = (String)sfResolve(varSFProcessId);
         } catch (SmartFrogResolutionException e) {
            log(varSFProcessId + " not found.", 5);
         }

         try {
            startCmd = (String)sfResolve(varSFCmdStart);
         } catch (SmartFrogResolutionException e) {
            log(varSFCmdStart + " not found.", 5);
         }

         try {
            stopCmd = (String)sfResolve(varSFCmdStop);
         } catch (SmartFrogResolutionException e) {
            log(varSFCmdStop + " not found.", 5);
         }

//         try {
//            startAtt = (String)sfResolve(varSFAttStart);
//         } catch (SmartFrogResolutionException e) {
//            log(varSFAttStart + " not found.",5);
//         }
//         try {
//            stopAtt = (String)sfResolve(varSFAttStop);
//         } catch (SmartFrogResolutionException e) {
//            log( varSFAttStop + " not found.",5);
//         }
         readCmdAttributes();

         // Just in case someone decides to overwrite startAtt and stopAtt created with readCmdAttributes!
         // Optional attributes.
       try {
          startAtt = sfResolve(varStopAttVector, startAtt, false);
          stopAtt = sfResolve(varStopAttVector, stopAtt, false);
       } catch (SmartFrogResolutionException e) {
          log ("readSFAttributes: Failed to read optional attribute: " + e.toString(),5);
          throw e;
       }


         try {
            workDir = (String)sfResolve(varSFWorkDir);
         } catch (SmartFrogResolutionException e) {
            log(varSFWorkDir + " not found.", 5);
         }

         try {
            Object reStartProcessObj = sfResolve(varAutoReStart);
            if (reStartProcessObj instanceof Boolean) {
               this.setRestart(((Boolean)reStartProcessObj).booleanValue());
            } else if (reStartProcessObj instanceof String) {
               // String format (Deprecapeted)
               String reStartProcessStr = (String) reStartProcessObj;
               if (reStartProcessStr.equals("true")) {
                  this.setRestart(true);
                  log("Automatic Restarting ENABLED!", 5);
               } else {
                  //log( "Automatic Restarting process NOT aproved in deployment!",5);
               }
            }
         } catch (SmartFrogResolutionException e) {
            log(varAutoReStart + " not found.", 5);
         }

         try {
            Object startProcessObj = sfResolve(varAutoStart);
            if (startProcessObj instanceof Boolean) {
               this.setStart(((Boolean)startProcessObj).booleanValue());
            } else if (startProcessObj instanceof String) {
               // String format (Deprecapeted)
               String startProcessStr = (String) startProcessObj;
               if (startProcessStr.equals("true")) {
                  this.setStart(true);
                  log("Automatic Starting ENABLED!", 5);
               } else {
                  //log( "Automatic Restarting process NOT aproved in deployment!",5);
               }
            }
         } catch (SmartFrogResolutionException e) {
            log(varAutoStart + " not found.", 5);
         }

         try {
            Object shouldTerminateObj = sfResolve(varShouldTerminate);
            if (shouldTerminateObj instanceof Boolean) {
               this.setShouldTerminate(((Boolean)shouldTerminateObj).booleanValue());
            } else if (shouldTerminateObj instanceof String) {
               // String format (Deprecapeted)
               String shouldTerminateStr = (String) shouldTerminateObj;
               if (shouldTerminateStr.equals("true")) {
                  this.setShouldTerminate(true);
                  log("Component will TERMINATE with termination of spanned process!", 5);
               } else {
               }
            }
         } catch (SmartFrogResolutionException e) {
            log(varShouldTerminate + " not found.", 5);
         }
         try {
            Object shouldDetachObj = sfResolve(varShouldDetach);
            if (shouldDetachObj instanceof Boolean) {
               this.setShouldDetach(((Boolean)shouldDetachObj).booleanValue());
            } else if (shouldDetachObj instanceof String) {
               // String format (Deprecapeted)
               String shouldDetachStr = (String) shouldDetachObj;
               if (shouldDetachStr.equals("true")) {
                  this.setShouldDetach(true);
                  log("Component will DETACH with termination of spanned process!", 5);
               } else {
               }
            }
         } catch (SmartFrogResolutionException e) {
            log(varShouldDetach + " not found.", 5);
         }

         try {
            this.setDelayRestart(((Integer)sfResolve(varDelayReStart)).longValue());
         } catch (SmartFrogResolutionException ex) {
            log(varDelayReStart + " not found.", 5);
         }

         try {
            Object isBatchObj = sfResolve(varIsBatch);
            if (isBatchObj instanceof Boolean) {
               this.isBatch=(((Boolean)isBatchObj).booleanValue());
            } else if (isBatchObj instanceof String) {
               // String format (Deprecapeted)
               String isBatchStr = (String) isBatchObj;
               if (isBatchStr.equals("true")) {
                  this.isBatch = true;
               }
            }
         } catch (SmartFrogResolutionException e) {
            log(varIsBatch + " not found.", 5);
         }

         try {
            Object runDeployObj = sfResolve(varRunDuringDeploy);
            if (runDeployObj instanceof Boolean) {
               this.runDuringDeploy=(((Boolean)runDeployObj).booleanValue());
            } else if (runDeployObj instanceof String) {
               // String format (Deprecapeted)
               String runDeployStr = (String) runDeployObj;
               if (runDeployStr.equals("true")) {
                  this.runDuringDeploy = true;
               }
            }
         } catch (SmartFrogResolutionException e) {
            log(varRunDuringDeploy + " not found.", 5);
         }

         // Reading environment variables
         try {
            Vector envPropVector = (Vector)sfResolve(varEnvProp);
            int index = 0;
            if (!envPropVector.isEmpty()) {
               envPropVector.trimToSize();
               envProp = new String[envPropVector.size()];
               envPropVector.copyInto(envProp);
//               Iterator iter = envPropVector.iterator();
//               int i = 0;
//               while (iter.hasNext()) {
//                  envProp[i++] = (String)iter.next();
//               }
               //end envProp
            }
            //log(5, "Vector found:" + varEnvProp +", "+this.notifierId());
         } catch (SmartFrogResolutionException e) {
            log(varEnvProp + " not found.", 5);
         }
      } catch (Exception e) {
         System.err.println("Error reading SF attributes: " + e.getMessage());
         e.printStackTrace();
      }
   }

   /**
    *  Read all Start Attributes and Stop Attributes in Vectors If and attribute
    *  name ends with 'b' then it is added to the previous one!
    */
   private void readCmdAttributes() {
      Object key = null;
      String auxString = "";
      //System.out.println("reading Cmd Attributes...");
      for (Enumeration e = sfContext().keys(); e.hasMoreElements(); ) {
         key = e.nextElement();
         if (key instanceof String) {
            try {
               if (((String)key).startsWith(varSFAttStart)) {
                  if (((String)key).endsWith("b")) {
                     // To concatenate two parameters
                     // for parameters like ATTa=ATTb
                     //startAtt = startAtt + "" + sfResolve((String)key); // former code when startAtt was a String
                     auxString = (String)startAtt.lastElement() + (sfResolve((String)key)).toString();
                     startAtt.remove(startAtt.size() - 1);
                     startAtt.add(auxString);
                  } else {
                     startAtt.add((sfResolve((String)key)).toString());
                  }
               } else if (((String)key).startsWith(varSFAttStop)) {
                  if (((String)key).endsWith("b")) {
                     // To concatenate two parameters
                     // for parameters like ATTa=ATTb
                     auxString = stopAtt.lastElement() + (sfResolve((String)key)).toString();
                     stopAtt.remove(stopAtt.size() - 1);
                     stopAtt.add(auxString);
                     // for parameters like ATTa=ATTb
                  } else {
                     stopAtt.add((sfResolve((String)key)).toString());
                  }
               }
            } catch (Exception ex) {
               ex.printStackTrace();
            }
         }
      }

   }

   /**
    *  Description of the Method
    *
    *@param  cmdGeneral  Description of Parameter
    *@param  cmdStr      Description of Parameter
    *@param  attributes  Description of Parameter
    *@return             Description of the Returned Value
    */
   private String[] createCmd(String[] cmdGeneral, String cmdStr, Vector attributes) {
      String[] cmd = null;
      try {
         if (attributes == null) {
            attributes = new Vector();
            attributes.add("");
         }
         int additionalParam = 3;

         int i = 0;
         cmd = new String[attributes.size() + additionalParam];
         //Cleaning empty args in the array
         if ((cmdGeneral[0] != null) && (!(cmdGeneral[0].equals(""))) && (!(cmdGeneral[0].equals(" ")))) {
            cmd[i++] = cmdGeneral[0];
         }
         if ((cmdGeneral[1] != null) && (!(cmdGeneral[1].equals(""))) && (!(cmdGeneral[1].equals(" ")))) {
            cmd[i++] = cmdGeneral[1];
         }
         if ((cmdStr != null) && (!(cmdStr.equals(""))) && (!(cmdStr.equals(" ")))) {
            cmd[i++] = cmdStr;
         }

         if (!attributes.isEmpty()) {
            attributes.trimToSize();
            Iterator iter = attributes.iterator();
            while (iter.hasNext()) {
               String temp = (String)iter.next();
               if ((temp != null) && (!(temp.equals(""))) && (!(temp.equals(" ")))) {
                  cmd[i++] = temp;
               }
            }
            //end envProp
         }
         //Cleaning end empty args
         String[] result = new String[i];
         for (int j = 0; j < i; j++) {
            result[j] = cmd[j];
         }
         cmd = result;
         log("CreatedCmd:" + arrayToString(cmd), 5);
      } catch (Exception e) {
         log("SFRunCommand.createCmd:Error creating Cmd.(" + e.getMessage() + ")", 5);
         e.printStackTrace();
      }
      return cmd;
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

   public InfoProcess getInfoProcess(){
      return process;
   }

}
//end

