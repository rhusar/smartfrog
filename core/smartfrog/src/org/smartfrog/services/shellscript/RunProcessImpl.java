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

import org.smartfrog.services.shellscript.FilterImpl;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.io.DataOutputStream;
import java.io.IOException;


//------------------- RUNProcess -------------------------------
public class RunProcessImpl  extends Thread implements RunProcess {

    /** Used to format times */
    protected static DateFormat dateFormatter = null;

    static {
      dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS zzz");
    }

    static final int STATE_INACTIVE = 0;
    static final int STATE_STARTING = 1;
    static final int STATE_STARTED = 2;
    static final int STATE_PROCESSING = 3;

    private int state = 0;

    public int getState()
    {
        return state;
    }

    private void setState( int newState )
    {
      if (sfLog.isInfoEnabled()){
        sfLog.info("setState "+ stateToString(state) + " -> " + stateToString(newState));
      }
      state = newState;
    }

    private String stateToString( int state )
    {
        String s = null;

        switch ( state )
        {
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

    private Runtime runtime = Runtime.getRuntime();

    // cmd Data
    private Cmd cmd = new Cmd();

    private long ID = -1;
    private String name = null;

    private FilterImpl stdoutFilter = null;
    private FilterImpl stderrFilter = null;

    private Process process = null;

    /** Data output stream. It will be used to send commands to process*/
    DataOutputStream processDos = null;

    private volatile boolean killRequested = false;

    private LogSF sfLog = LogFactory.sfGetProcessLog(); //Temp log until getting its own.

    // Name can be null, not sure if we still need name.
    public RunProcessImpl(long ID, String name, Cmd cmd) {
        if (name == null) name = "";
        else name = name +"_";

        this.cmd = cmd;
        this.ID = ID;
        this.name = "RunProcess_"+name+ID;
        setName(this.name);
        sfLog = LogFactory.getLog(this.name);
        killRequested = false;
    }

    public void run() {

        // Check that a kill has not been requested even before the application has started.
        if (killRequested==true) {
            setState(STATE_INACTIVE);
            return;
        }

        int exitValue = -9999;

        try {
            synchronized (this) {
                process = runtime.exec(cmd.getCmdArray(), cmd.getEnvp(),
                                       cmd.getFile());

                if (sfLog.isInfoEnabled()){
                   sfLog.info("run" + ID + " -- attaching data output stream");
                }
                processDos = new DataOutputStream(process.getOutputStream());

                replaceFilters(
                  new FilterImpl( ID, process.getInputStream(), "stdout", null, null),
                  new FilterImpl( ID, process.getErrorStream(), "stderr", null, null)
                );
            }

            if (sfLog.isInfoEnabled()){
                  sfLog.info("run"+ ID + " -- waiting for application to exit");
            }

            // process may be null by the time we get here after the synchronized
            // block above if a terminate has been requested while the app was
            // starting or the exec failed, e.g. the FP application is not installed
            // on the node.  This will cause a NullPointerException but there is
            // nothing else to do anyway but tidy up.
            if (process!=null) {
                setState(STATE_PROCESSING);
                exitValue = process.waitFor();
            }

            if (sfLog.isInfoEnabled()){
              sfLog.info("run" + ID + " -- exit = " + exitValue);
            }
        } catch (Throwable t) {
          if (sfLog.isErrorEnabled()){
            sfLog.error("run" + ID + " -- failed to complete execution", t);
          }
        }

        // Transitioning to INACTIVE here may result in a situation where the FP is
        // still tidying up when a startApplication() call is received.  Since a new
        // process is created this should not cause any particular problems but there
        // may be an unexpected ordering of events, e.g. applicationStarted followed
        // by an applicationStopped from a previous FP run.

        int endState = state;

        setState(STATE_INACTIVE);

        if (!killRequested) {
            // If a kill was requested then this is taken care of in kill().
            try {
                stopFilters();

                // Necessary to free O/S resources??
                // If the process was killed, there is no need to do this.
                //
                // If the exec failed, process will already be null.
                if (process!=null) {
                    process.destroy();
                    process = null;
                }

                // If the packet completed normally then now that we have tidied up after
                // ourselves we can send the packetCompleted event.
                if (endState==STATE_PROCESSING&&exitValue==0) {
                    //Done!
                } else {
                  // Something went wrong,
                  String message = null;
                  if (endState == STATE_STARTED) {
                    message ="Application executed, but finished without starting processing";
                  } else if (endState == STATE_STARTING) {
                    message = "Application failed to execute";
                  } else if (exitValue != 0) {
                    message = "Application finished prematurely due to an application/system error";
                  } else {
                    message = "Unexpected application state: " + state;
                  }
                  if (sfLog.isWarnEnabled()) {
                    sfLog.warn("run " + ID + message);
                  }
                }

                // Regardless of what happens, make sure to indicate that the application
                // has stopped.
            } catch (Throwable t) {
              if (sfLog.isErrorEnabled()) {
                sfLog.error("run" + ID + " -- failed to release resources", t);
              }
            }
        }
    }

    public synchronized void replaceFilters(FilterImpl fout, FilterImpl ferr) {
      if (sfLog.isInfoEnabled()){
        sfLog.info("run" + ID + " -- attaching filters");
      }

      stopFilters();

      stdoutFilter = fout;
      stderrFilter = ferr;

      stdoutFilter.start();
      stderrFilter.start();
    }

    private void stopFilters() {
        // Stop filters after process has died so that the process doesn't fill its
        // output buffer(s).
        if (stdoutFilter!=null) {
            stdoutFilter.stopRequest();
        }

        if (stderrFilter!=null) {
            stderrFilter.stopRequest();
        }

        waitForFilters();
        stdoutFilter = null;
        stderrFilter = null;
        if (sfLog.isInfoEnabled()) {
          sfLog.info(ID + "-- Application log for " + ID + " stopped @ "
                     + dateFormatter.format(new Date()) +
                     " -------------------------------------------------");
        }
    }

    private void waitForFilters() {
      if (sfLog.isInfoEnabled()) {
        sfLog.info("waitForFilters" + ID + " -- waiting for filters to stop");
      }
      boolean bothFinished = false;

      while (!bothFinished) {
          if (stdoutFilter!=null) {
              try {
                if (sfLog.isInfoEnabled()) {
                  sfLog.info("waitForFilters" + ID +" -- waiting for stdout filter");
                }
                stdoutFilter.join();
              } catch (InterruptedException e) {
                if (sfLog.isInfoEnabled()) {
                  sfLog.info("waitForFilters" + ID + " -- interrupted while waiting for stdout filter");
                }
                continue;
              }
          }

          if (stderrFilter!=null) {
              try {
                if (sfLog.isInfoEnabled()) {
                  sfLog.info("waitForFilters" + ID +" -- waiting for stderr filter");
                }
                  stderrFilter.join();
              } catch (InterruptedException e) {
                if (sfLog.isInfoEnabled()) {
                  sfLog.info("waitForFilters" + ID +" -- interrupted while waiting for stderr filter");
                }
                continue;
              }
          }

          bothFinished = true;
      }

      if (sfLog.isInfoEnabled()) {
        sfLog.info("waitForFilters" + ID + " -- filters stopped");
      }

    }

    public void kill() {
        // It is possible to request a kill before the process has actually been
        // created.  If this is the case just prevent the run() method from creating
        // the process.
        synchronized (this) {
            if (process==null) {
              if (sfLog.isInfoEnabled()) {
                sfLog.info("kill" + ID + " -- application has not started yet");
              }
              killRequested = true;
            }
        }

        // If the process has already been created then kill it.
        if (!killRequested) {
          if (sfLog.isInfoEnabled()) {
            sfLog.info("kill" + ID + " -- terminating process");
          }
          killRequested = true;
          process.destroy();

          try {
            if (sfLog.isInfoEnabled()) {
              sfLog.info("kill" + ID + " -- waiting for exit");
            }
            process.waitFor();
          } catch (InterruptedException e) {
          }

          if (sfLog.isInfoEnabled()) {
            sfLog.info("kill" + ID+ " -- exit = " + process.exitValue());
          }
          process = null;
          try {
              processDos.close();
          } catch (IOException e) {
          }
          // Wait for filters to stop before issuing Terminated
          stopFilters();
          // notify termination!
          // @todo
          ID = -1;
        }
    }

    /**
      *  Executes the given command.
      *
      * @param  cmd  command to be exceuted
      */
     public void execCommand(String command) {
         if ((command==null)|| killRequested || state!=STATE_PROCESSING ){
           //@TODO thow exception? Log return cause
           return;
         }
         command = command + cmd.getLineSeparator();
         synchronized (processDos) {
           if (processDos != null) {
             try {
               if (sfLog.isTraceEnabled()) {
                 sfLog.trace("Executing: " + cmd);
               }
               processDos.writeBytes(command);
               processDos.flush();
             } catch (IOException ex) {
               if (sfLog.isErrorEnabled()) {
                 sfLog.error(ex);
               }
             }
           } else {
             if (sfLog.isErrorEnabled()) {
               sfLog.error("Error: Stream closed. Process probably terminated.");
             }
           }
         }
     }

}

