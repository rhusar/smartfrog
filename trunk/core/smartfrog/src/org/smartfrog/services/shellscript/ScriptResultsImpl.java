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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import java.lang.reflect.InvocationTargetException;

/**
 */
public class ScriptResultsImpl implements ScriptResults {

    protected boolean resultReady = false;

    protected ComponentDescription result = new ComponentDescriptionImpl(null,  new ContextImpl(), false);

    protected InvocationTargetException exception = null;

    List stdOut = Collections.synchronizedList(new ArrayList());
    List stdErr = Collections.synchronizedList(new ArrayList());

    public ScriptResultsImpl() {
    }

    public boolean resultsReady() {
        return resultReady;
    }

   public synchronized ComponentDescription waitForResults(long timeout) throws SmartFrogException {
      try {
        while (!resultReady) {
          wait(timeout);
        }
        if (exception != null)
          // Will throw , InterruptedException, InvocationTargetException
          throw exception;
        else
          return result;
      } catch (Exception ex) {
          // Will throw , InterruptedException, InvocationTargetException
          throw SmartFrogException.forward(exception);
      }
  }




  public synchronized void ready(Integer code) {
      try {
        result.sfAddAttribute("stdOut", stdOut);
        result.sfAddAttribute("stdErr", stdErr);
        result.sfAddAttribute("code", code);
      } catch (SmartFrogRuntimeException ex) {
        //@Todo add log
        ex.printStackTrace();
      }
      resultReady = true;
      notifyAll();
    }

    synchronized void setException(Throwable e) {
     exception = new InvocationTargetException(e);
     resultReady = true;
     notifyAll();
    }
}
