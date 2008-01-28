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
    private boolean stopWorkerRequested = false;
    private LogSF log = LogFactory.sfGetProcessLog(); //Temp log until getting its own.

    public BufferFiller() {
      super("BufferFiller-Filter " +"(" + type + ")");
      log = LogFactory.getLog(ID);
    }

    public void stopRequest() {
      stopWorkerRequested = true;
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
            // Even if stopRequested is true, we should continue until the
            // stream is empty otherwise the process may block and never exit
            // because it's output buffers are full.
            //

            if (reader.ready()) {
              if ( (line = reader.readLine()) == null) {
                //log.info("run" + ID + " -- no more output to process");
                stopWorkerRequested = true;
                continue;
              }

              synchronized (buffer) {
                buffer.addElement(line);
                buffer.notify();
              }
            } else {
              if (stopWorkerRequested) {
                break;
              }

              // Nothing to read?  Then wait a little.
              try {
                Thread.sleep(10);
              } catch (InterruptedException e) {
              }
            }
          } catch (IOException e) {
              if (log.isErrorEnabled()) {
                  log.error("problem reading output", e);
              }
              stopWorkerRequested = true;
          }
        }

        try {
          if (log.isTraceEnabled()){
             log.trace("closing input stream");
          }
          reader.close();
          iStream.close();
          in.close();
        } catch (IOException e) {
            if (log.isErrorEnabled()){
              log.error("failed to close input stream", e);
            }
        }
      } catch (Throwable t) {
          if (log.isErrorEnabled()){
            log.error("unexpected exception",t);
          }
      }
      if (log.isTraceEnabled()){
        log.trace("stopped");
      }

    }
  }

  // ---- end Buffer Filler

  private Vector buffer = null;
  private InputStream in = null;
  private BufferFiller bufferFiller = null;
  private boolean stopRequested = false;
  // decides if to pass postives to the listener.line() interface in addition to the listener.found() call
  private boolean passPositives = false;

//  private RunProcess process = null;
  private String type = null;
  private String ID = "";

  private FilterListener listener = null;

  private LogSF sfLog = LogFactory.sfGetProcessLog(); //Temp log until getting its own.

  private String filters[] = null;


    /**
     *
     * @param ID filter ID
     * @param in stream input
     * @param type type of filter, use for logging mainly.
     * @param filters  data to be search in inputstream. Positives are sent to the listener.found() interface
     * @param listener  listener to process read lines and found positives
     * @param passPositives decides if to pass postives to the listener.line() interface in addition to the listener.found() call
     */

  public FilterImpl( String ID, InputStream in, String type, String filters[], FilterListener listener, boolean passPositives) {

    super ("Filter(" + type + ")");

    this.passPositives = passPositives;

    this.type = type;
    //this.type = "Filter "+ ID+ "(" + type + ")" ;
//    this.setName(this.type);
    sfLog = LogFactory.getLog(ID);

    this.listener = listener;
    this.filters=filters;

    this.in = in;
    buffer = new Vector();

    bufferFiller = new BufferFiller();
    bufferFiller.start();
  }

  public synchronized InputStream getInputStream(){
      return in;
  }

  public synchronized InputStream setInputStream(InputStream is){
      InputStream oldIn = this.in;
      if (bufferFiller!=null){
          stopBufferFiller();
          this.in = is;
          bufferFiller = new BufferFiller();
          bufferFiller.start();
      } else {
          this.in = is;
      }
      return in;
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
          // This trace is very useful for debugging  application output
          if (sfLog.isTraceEnabled()){
            sfLog.trace(line );
          }

          // Now actually do the filtering.
          filter(line, this.filters);

        } else {
          synchronized (buffer) {
            try {
              buffer.wait();
            } catch (InterruptedException e) {
              if (sfLog.isErrorEnabled()){
                sfLog.error("interrupted while waiting for more output", e);
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
        sfLog.error("failed to read input buffer", t);
      }
    }

    stopBufferFiller();

  }

  private void stopBufferFiller() {
      try {
          //log.info("run"+ID + " -- stopping buffer filler");
          bufferFiller.stopRequest();
          bufferFiller.join();
          if (sfLog.isDebugEnabled()) {
              sfLog.debug("buffer filler stopped");
          }
      } catch (Exception e) {
          if (sfLog.isErrorEnabled()) {
              sfLog.error("problems stoped buffer filler", e);
          }
      }
  }

  // Compares line with filters[] set
  protected synchronized void filter(String line, String lineFilters[]) {
      if (listener == null) return;
      if (lineFilters!=null) {
          for (int i = 0; i<lineFilters.length; ++i) {
              //sfLog.trace("Comparing: "+ line +", "+filters[i]);

              if (line.indexOf(lineFilters[i])==-1) {
                  //No match
                  listener.line(line, getName());
                  continue;
              }
              positiveFilter(line, i, getName());
          }
      }
  }

  protected synchronized void positiveFilter(String line, int filterIndex, String filterName) {
      if (listener == null) return;
      if ((passPositives)) {
         listener.line(line, getName());
      }
      listener.found(line, filterIndex, getName());
  }
}
//------------------- end FILTER -------------------------------
