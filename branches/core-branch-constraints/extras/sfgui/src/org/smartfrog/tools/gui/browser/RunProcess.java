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

package org.smartfrog.tools.gui.browser;




/**
 * Title:        SFGui
 */

import org.smartfrog.tools.gui.browser.StreamGobbler;
import java.io.*;
import java.util.*;
import org.smartfrog.tools.gui.browser.MainFrame;



//public class RunProcess implements Runnable {
public class RunProcess extends Thread {

  String nameProcess="default";
  String command="defaultCommand";
  File workDir=null;
  boolean stop=false;
  boolean reStart=false;
  String status="stopped";
  long delay = (2*1000); // 2 seconds
  String[] envProp=null;

  // Process Data
  Runtime runtime = Runtime.getRuntime();
  Process subProcess=null; // Process executed
  Thread process=null;     // Thread that executes the process.

  public RunProcess(String command) {
    this.command=command;
  }

  public RunProcess(String command, String workDirStr) {
    this.command=command;
    this.workDir = new File (workDirStr);
  }

  public RunProcess(String command, String workDirStr, String NameProcess) {
    this.command=command;
    this.workDir = new File (workDirStr);
    this.nameProcess = NameProcess;
  }

  public RunProcess(String command, String workDirStr, String NameProcess, String[] envProp) {
    this.command=command;
    this.workDir = new File (workDirStr);
    this.nameProcess = NameProcess;
    this.envProp= envProp;
  }

  public void kill() {
     this.stop=true;
     this.subProcess.destroy();
     this.status="stopped";
  }

  public void clean() {
     this.subProcess.destroy();
     this.status="stopped";
  }

  void log (String message, int severity){
     org.smartfrog.tools.gui.browser.MainFrame.log( "["+this.nameProcess+"] "+message,"RUNProcess",severity);
  }

  public void run() {

    do {
      try {
        //this.log("Command Start: "+command,3);
        this.log("Started > "+ command + "| workdir: "+workDir+" ",2);
        if (workDir!=null){
          subProcess=runtime.exec(command, envProp, workDir );
        }
        else {
          //System.out.println(command);
          subProcess=runtime.exec(command);
        }
        // any error message?
        StreamGobbler errorGobbler = new StreamGobbler(subProcess.getErrorStream(), "["+this.nameProcess+"] "+ "ERR");
        // any output?
        StreamGobbler outputGobbler = new StreamGobbler(subProcess.getInputStream(),"["+this.nameProcess+"] "+ "OUT");
         // kick them off
        errorGobbler.start();
        outputGobbler.start();
        this.log("Process (re)started",3);
        status="running";
        int exitVal=subProcess.waitFor(); // wait until process finishes

        this.log(" Exit Val > "+ exitVal+"",2);
        this.clean();
        if (stop) break;
        Thread.sleep(delay);
      } catch(Exception ex) {
            this.log("Problem starting > " + ex.toString(),5);
            ex.printStackTrace();
            try {
              this.kill();
              Thread.sleep(10*1000);
            } catch (Exception e) { e.printStackTrace(); }
      }
    } while (reStart);
  }

  public String getNameProcess(){
      return this.nameProcess;
  }

  public String getStatus(){
      return this.status;
  }

  public long getDelay(){
      return this.delay;
  }

  public void setDelay(long delay){
      this.delay= delay;
  }

  public void setReStart (boolean repeat){
     this.reStart=repeat;
  }

//  public static void main(String[] args) {
//    RunProcess runProcess1 = new RunProcess("dir");
//  }
}
