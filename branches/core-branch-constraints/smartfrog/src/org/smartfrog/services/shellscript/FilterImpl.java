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
    private LogSF sfLog = LogFactory.sfGetProcessLog(); //Temp log until getting its own.

    public BufferFiller() {
      super("BufferFiller-Filter " +"(" + type + ")");
      sfLog = LogFactory.getLog(ID);
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

              // Nothing to read?  Then wait a little.
              try {
                Thread.sleep(10);
              } catch (InterruptedException e) {
              }
            }
          } catch (IOException e) {
              if (sfLog.isErrorEnabled()) {
                  sfLog.error("problem reading output", e);
              }
              stopRequested = true;
          }
        }

        try {
          if (sfLog.isTraceEnabled()){
             sfLog.trace("closing input stream");
          }
          reader.close();
          iStream.close();
          in.close();
        } catch (IOException e) {
            if (sfLog.isErrorEnabled()){
              sfLog.error("failed to close input stream", e);
            }
        }
      } catch (Throwable t) {
          if (sfLog.isErrorEnabled()){
            sfLog.error("unexpected exception",t);
          }
      }
      if (sfLog.isTraceEnabled()){
        sfLog.trace("stopped");
      }

    }
  }

  // ---- end Buffer Filler

  private Vector buffer = null;
  private InputStream in = null;
  private BufferFiller bufferFiller = null;
  private boolean stopRequested = false;
//  private RunProcess process = null;
  private String type = null;
  private String ID = "";

  private FilterListener listener = null;

  private LogSF sfLog = LogFactory.sfGetProcessLog(); //Temp log until getting its own.

  private String filters[] = null;


  public FilterImpl( String ID, InputStream in, String type, String filters[], FilterListener listener) {
    super ("Filter(" + type + ")");
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
          // This trace is very useful for debugging but application output
          if (sfLog.isDebugEnabled()){
            sfLog.debug(line );
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
  protected void filter(String line, String filters[]) {
      if (listener !=null) {
          listener.line(line, getName());
      }

      if (filters==null) return;

      for (int i = 0; i<filters.length; ++i) {
          //sfLog.trace("Comparing: "+ line +", "+filters[i]);
          if (line.indexOf(filters[i])==-1) {
              //No match
              continue;
          }
          // it tells you the write filter!
          positiveFilter(line, i, getName());
      }
  }

  protected void positiveFilter(String line, int filterIndex, String filterName) {
      if (listener !=null){
          listener.found(line, filterIndex, getName());
      }
  }
}
//------------------- end FILTER -------------------------------
