package org.smartfrog.services.runcmd;

/**
 *  Title: SmartFrog CVS Description: Copyright: Copyright (c) 2001 Company: HP
 *  Labs Bristol
 *
 *@author     Serrano
 *@version    1.0
 */

import org.smartfrog.services.runcmd.RunProcess;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.common.SFNull;

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 October 2001 v 0.02
 */
public class InfoProcess {
   /**
    *  Description of the Field
    */
   public RunProcess process = null;

   private String workDir = null;
   private String processName = null;
   private String host = "127.0.0.1";
   private String[] startCmdAtt = null;
   private String[] stopCmdAtt = null;
   private String[] envProp = null;
   private boolean autoReStart = false;
   private long restartDelay = -1;
   private boolean shouldTerminate = false;
   private boolean shouldDetach = false;
   private String statusInfo = "Created";
   private Prim sfObj = null;
   private int logger = -1;
   // Reference to SF Component


   /**
    *  Constructor for the MngProcess object
    *
    *@param  processName  Description of Parameter
    *@param  workDir      Description of Parameter
    *@param  startCmdAtt  Description of Parameter
    */
   public InfoProcess(String processName, String[] startCmdAtt, String workDir) {
      this.processName = processName;
      this.startCmdAtt = startCmdAtt;
      this.workDir = workDir;
      System.out.println("Infoprocess New...: "+this.processName);
   }

   /**
    *  Constructor for the InfoProcess object
    *
    *@param  processName  Description of Parameter
    *@param  workDir      Description of Parameter
    *@param  envProp      Description of Parameter
    *@param  startCmdAtt  Description of Parameter
    *@param  stopCmdAtt   Description of Parameter
    */
   public InfoProcess(String processName, String[] startCmdAtt, String[] stopCmdAtt, String workDir, String[] envProp) {
      this.workDir = workDir;
      this.processName = processName;
      this.startCmdAtt = startCmdAtt;
      this.stopCmdAtt = stopCmdAtt;
      this.envProp = envProp;
      System.out.println("Infoprocess New...: "+this.processName);
   }


   /**
    *  Sets the autoReStart attribute of the InfoProcess object
    *
    *@param  autoReStart  The new autoReStart value
    */
   public void setAutoReStart(boolean autoReStart) {
      this.autoReStart = autoReStart;
      if (process != null) {
         process.setReStart(this.autoReStart);
      }
   }


   /**
    *  Sets the restartDelay attribute of the InfoProcess object
    *
    *@param  restartDelay  The new restartDelay value
    */
   public void setRestartDelay(long restartDelay) {
      this.restartDelay = restartDelay;
      if (process != null) {
         process.setDelay(this.restartDelay);
      }
   }

   /**
    *  Sets the shouldTerminate attribute of the InfoProcess object
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
    *  Sets the shouldDetach attribute of the InfoProcess object
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
    *  Sets the sFObj attribute of the InfoProcess object
    *
    *@param  sfObj  The new sFObj value
    */
   public void setSFObj(Prim sfObj) {
      this.sfObj = sfObj;
      if (process != null) {
         process.setSFObj(sfObj);
      }
   }

   /**
    *  Gets the processName attribute of the InfoProcess object
    *
    *@return    The processName value
    */
   public String getProcessName() {
      return processName;
   }

   /**
    *  Gets the cmd attribute of the InfoProcess object
    *
    *@return    The cmd value
    */
   public String getCmd() {
      return ("StartCmd: " + arrayToString(startCmdAtt) + "" + " ¦ " + "StopCmd: " + arrayToString(stopCmdAtt) + "");
   }

   /**
    *  Gets the status attribute of the InfoProcess object
    *
    *@return    The status value
    */
   public String getStatus() {
      if (process != null) {
         return (this.statusInfo + "(" + this.process.getStatus() + ")");
      }
      return (this.statusInfo + "(Null)");
   }

   /**
    *  Description of the Method
    */
   public void start() {
      if (this.startCmdAtt == null) {
         System.err.println("InfoProcess: StartCmd=null");
         return;
      }
      System.out.println("process.Start:stating -"+this.getProcessName());
      cleanProcess();
      process = new RunProcess(this.startCmdAtt, this.workDir, this.processName, this.envProp);
      setProcessParam();
      process.start();
      this.statusInfo = "Started";
      System.out.println("process.Start:started -"+this.getProcessName());
   }

   /**
    *  Description of the Method
    */
   public void stop() {
      cleanProcess();
      // Review this for not killing it! Use an auxiliary process.
   
	//System.out.println("process.Stop:stopping =====>"+ this.getProcessName() + " Cmd:"+ this.stopCmdAtt);
//System.out.println("process.Stop:stopping =====>"+ this.getProcessName() + " Cmd:"+ this.getCmd());

 /*     if (this.stopCmdAtt != null) {
         process = new RunProcess(stopCmdAtt, this.workDir, this.processName, this.envProp);
         setProcessParam();
         process.start();
         this.statusInfo = "Stopped";
     // } else {
         //process=new RunProcess(this.startCmd+" "+this.startAtt, this.workDir, this.processName);
*/
         this.statusInfo = "Stopped(no Stop cmd)";
      //}
	System.out.println("Stopcmd depricated");
      	System.out.println("process.Stop:stop end -"+this.getProcessName());
	System.out.println("process.Stop:stop end -"+this.statusInfo);
   }

   /**
    *  Description of the Method
    */
   public void kill() {
      cleanProcess();
      this.statusInfo = "Killed";
   }

   /**
    *  Description of the Method
    *
    *@return    Description of the Returned Value
    */
   public String toString() {
      return ("ProcessInfo: " + this.getProcessName() +
            ", Status: " + this.getStatus() +
            ", StartCmd: " + this.startCmdAtt.toString() +
            ", StopCmd: " + this.stopCmdAtt.toString() +
            ", WorkDir:" + this.workDir +
            ", EnvProp:" + this.envProp.toString());
   }

   /**
    *  Sets the processParam attribute of the InfoProcess object
    */
   private void setProcessParam() {
      process.setReStart(this.autoReStart);
      if (restartDelay >= 0) {
         process.setDelay(this.restartDelay);
      }
      if (this.shouldTerminate != false) {
         process.setShouldTerminate(this.shouldTerminate);
      }
      if (this.shouldDetach != false) {
         process.setShouldDetach(this.shouldDetach);
      }
      if (sfObj != null) {
         process.setSFObj(sfObj);
      }
      process.setLogLevel(logger);
   }


   /**
    *  Description of the Method
    */
   private void cleanProcess() {
      //System.out.println("process.cleanProcess:clean start -"+this.getProcessName());
      if (process != null) {
         if ((process.getStatus()).equals("running")) {
            process.kill();
            //System.out.println("process.cleanProcess:clean Kill -"+this.getProcessName());
         }
         //process.destroy();
         process = null;
         //System.out.println("process.cleanProcess:clean Null -"+this.getProcessName());
      }
      //System.out.println("process.cleanProcess:clean emd -"+this.getProcessName());
   }

   public void setLogLevel (int logger){
      this.logger=logger;
      if (process != null) {
         process.setLogLevel(logger);
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


}
