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


package org.smartfrog.services.shellscript;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

import org.smartfrog.services.shellscript.RunProcessImpl;
import java.text.DateFormat;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;


public class ScriptRunImpl  extends ScriptRun {

  private RunProcessImpl runProcess = null;

  private int state = STATE_INACTIVE;
  private long ID = -1;

  private String hostName = null;

  /** Used to format times */
  protected static DateFormat dateFormatter = null;

  static {
    dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS zzz");
  }

  java.lang.Runtime runtime = null;

  private LogSF sfLog = LogFactory.sfGetProcessLog(); //Temp log until getting its own.

  public ScriptRunImpl (long ID, Cmd cmd) {

    super(ID);

    try {
      hostName = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      if (sfLog.isErrorEnabled()){
        sfLog.error("scriptRun" + "Can't resolve local hostname, using 'localhost'");
      }
      hostName = "localhost";
    }

    //@TODO Review Log naming.
    sfLog = LogFactory.getLog("ScriptRun_"+hostName+"_"+ID);

    runtime = Runtime.getRuntime();

  }

  public synchronized void stop() {
    if (runProcess != null) {
      runProcess.kill();
      runProcess = null;
      ID = -1;
    }
  }

  public int getState() {
    return state;
  }

  private void setState(int newState) {
    if (sfLog.isInfoEnabled()){
      sfLog.info("setState " + getState(state) + " -> " + getState(newState));
    }
    state = newState;
  }

  public String getState(int state) {
    String s = null;

    switch (state) {
      case STATE_INACTIVE:
        s = "INACTIVE";
        break;

      case STATE_STARTING:
        s = "STARTING";
        break;

      case STATE_STARTED:
        s = "STARTED";
        break;

      case STATE_PROCESSING:
        s = "PROCESSING";
        break;

      default:
        s = "UNKNOWN";
        break;
    }

    return s;
  }

  public synchronized void startScriptRunner ( long ID,String name,Cmd cmd) throws
      SecurityException, IOException {

    if (state != STATE_INACTIVE) {
      if (sfLog.isErrorEnabled()){
        sfLog.error("FP is already processing packet " + ID);
      }
      return;
    }

    this.ID = ID;

    try {
      runProcess = new RunProcessImpl (ID, name, cmd);

      setState(STATE_STARTING);
      runProcess.start();
    } catch (Throwable t) {
      if (sfLog.isWarnEnabled()){
        sfLog.warn("startApplication: " + " Couldn't create command-line: " + t.getMessage(), t);
      }
      runProcess = null;
    }
  }

  public synchronized void stopScriptRunner(long ID) {
    String message = null;

    if (this.ID != ID) {
      message = "It is not processing  ID: " + ID;
    }
    if (message != null) {
      if (sfLog.isTraceEnabled()){
        sfLog.trace("stopApplication " + ID + " -- " + message + ", ignoring request");
      }
    } else if (runProcess == null) {
      if (sfLog.isTraceEnabled()){
        sfLog.trace("stopApplication " + ID + " -- no process to stop!");
      }
    } else {
      runProcess.kill();
      runProcess = null;
      this.ID = -1;
    }
  }

}
