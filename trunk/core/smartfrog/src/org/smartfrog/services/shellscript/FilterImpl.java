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

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.Vector;
import java.io.IOException;
import java.io.InputStreamReader;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;

//------------------- FILTER -------------------------------

public class FilterImpl extends Thread {

    //--- BufferFiller
    private class BufferFiller extends Thread {
    private boolean stopRequested = false;

    public BufferFiller() {
      super("BufferFiller(" + name + ")_" + ID);
    }

    public void stopRequest() {
      stopRequested = true;
    }

    public void run() {
      try {
        InputStreamReader iStream = new InputStreamReader(in);
        BufferedReader reader = new BufferedReader(iStream);

        String line = null;

        while (true) {
          try {
            //
            // readLine() (under Linux at least) can take up to 6 minutes
            // to realise that the stream it is reading from has gone away.
            // If we check that there is something to read detect this
            // situation immediately, at the cost of a Thread.sleep()
            //

            //
            // Even if stopRequested is true, we should continue until the
            // stream is empty otherwise the process may block and never exit
            // because it's output buffers are full.
            //

            if (reader.ready()) {
              if ( (line = reader.readLine()) == null) {
                //log.info("run" + ID + " -- no more output to process");
                stopRequested = true;
                continue;
              }

              synchronized (buffer) {
                buffer.addElement(line);
                buffer.notify();
              }
            } else {
              if (stopRequested) {
                break;
              }

              //
              // Nothing to read?  Then wait a little.
              //

              try {
                Thread.sleep(10);
              } catch (InterruptedException e) {
              }
            }
          } catch (IOException e) {
            // log.error("run"+ ID + " -- problem reading output", e);
            stopRequested = true;
          }
        }

        try {
          //log.info(ID + " -- closing input stream");
          reader.close();
          iStream.close();
          in.close();
        } catch (IOException e) {
          //log.error ( "run"+ID + " -- failed to close input stream", e);
        }
      } catch (Throwable t) {
        //log.error(ID+ " -- unexpected exception",t);
      }
      //log.info(ID + " -- stopped");
    }
  }

  // ---- end Buffer Filler

  private Vector buffer = null;
  private InputStream in = null;
  private BufferFiller bufferFiller = null;
  private boolean stopRequested = false;
//  private RunProcess process = null;
  private String name = null;
  private long ID = -1;

  private FilterListener listener = null;

  private LogSF sfLog = LogFactory.sfGetProcessLog(); //Temp log until getting its own.

  private String filters[] = {
     "echo started", //First command
     "echo terminated"
 };


  public FilterImpl( long ID, InputStream in, String name, String filters[], FilterListener listener) {
    super("Filter(" + name + ")_" + ID);
    sfLog = LogFactory.getLog("Filter(" + name + ")_" + ID);
    this.name = name;
    this.listener = listener;

    this.ID = ID;

    this.in = in;
    buffer = new Vector();

    bufferFiller = new BufferFiller();
    bufferFiller.start();
  }

  public void stopRequest() {
    stopRequested = true;

    synchronized (buffer) {
      buffer.notify();
    }
  }

  public void run() {
    try {
      String line = null;

      while (! (buffer.isEmpty() && stopRequested)) {
        if (!buffer.isEmpty()) {
          synchronized (buffer) {
            line = (String) buffer.firstElement();
            buffer.removeElementAt(0);
          }
          // This trace is very useful for debugging but now application output
          if (sfLog.isInfoEnabled()){
            sfLog.info( ID  + " -- " + line );
          }

          //
          // Now actually do the filtering.
          //
          filter(line, this.filters);

        } else {
          synchronized (buffer) {
            try {
              buffer.wait();
            } catch (InterruptedException e) {
              if (sfLog.isErrorEnabled()){
                sfLog.error("run" + ID +" -- interrupted while waiting for more output", e);
              }
            }
//            finally {
//              continue;
//            }
          }
        }
      }
    } catch (Throwable t) {
      if (sfLog.isErrorEnabled()){
        sfLog.error(ID + " -- failed to read input buffer", t);
      }
    }

    try {
      //log.info("run"+ID + " -- stopping buffer filler");
      bufferFiller.stopRequest();
      bufferFiller.join();
    } catch (Exception e) {
      if (sfLog.isErrorEnabled()){
        sfLog.error("run" + ID + " -- problems stooped buffer filler", e);
      }
    }
    if (sfLog.isInfoEnabled()){
      sfLog.info("run" + ID + " -- buffer filler stopped");
    }
  }

  // Compares line with filters[] set
  public void filter(String line, String filters[]) {
      if (listener !=null) {
          listener.line(line);
      }
      for (int i = 0; i<filters.length; ++i) {
          if (line.indexOf(filters[i])==-1) {
              //No match
              continue;
          }
          // it tells you the write filter!
          positiveFilter(line, i);
      }
  }

  public void positiveFilter(String line, int i) {
      if (listener !=null){
          listener.found(line, i);
      }
  }
}
//------------------- end FILTER -------------------------------
