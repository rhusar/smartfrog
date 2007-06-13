/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.prim;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.compound.Compound;

import java.rmi.RemoteException;
import java.util.*;
import java.io.Writer;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;


public class DefaultDumper implements Dump {

    ComponentDescription cd = null;

    ComponentDescription lastCD =null;

    ComponentDescription lastChild = null;

    Hashtable visiting = new Hashtable();

    protected Long visitingLocks = new Long(1);

    long timeout = (10*60*1000L);


    public void DefaultDumper (Context context){
    }

   /**
     * Components use this methods to dump their state to when requested (using
     * sfDumpState).
     *
     * @param state state of component (application specific)
     * @param from source of this call
     *
     * @throws java.rmi.RemoteException In case of Remote/nework error
     */
    public void dumpState(Object state, Prim from) throws RemoteException {
       Integer numberOfChildren = new Integer(0);
       if (from instanceof Compound) {
          int numberC = 0;
          for (Enumeration e = ((Compound)from).sfChildren(); e.hasMoreElements();) {
            e.nextElement();  
            numberC++;
          }
          numberOfChildren = new Integer (numberC);
       }
       visiting(from.sfCompleteName().toString(),numberOfChildren);
       Context stateClone =  (Context)((Context)state).clone();
       if (cd==null){
           cd = new ComponentDescriptionImpl(null,(Context)stateClone,false);
           lastCD = cd;
       } else {
           if (lastCD.sfContainsValue(from)){
           } else if (lastChild.sfContainsValue(from)) {
               lastCD=lastChild;
           } else if  (lastCD.sfParent().sfContainsValue(from)) {
                lastCD=lastCD.sfParent();
           } else {
               // log warning
           }
           Object key = lastCD.sfAttributeKeyFor(from);
           lastChild = new ComponentDescriptionImpl (lastCD,stateClone,false);
           try {
             lastCD.sfReplaceAttribute(key,lastChild);
           } catch (SmartFrogRuntimeException e) {
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
           }
       }
       visited(from.sfCompleteName().toString());
   }



    /**
      * Tries to get the the String once  the object finished visiting all nodes
      * or until given timeout expires.
      *
      * @param timeout max time to wait in millis
      *
      * @return The string representation of the description
      *
      * @throws Exception attribute not found after timeout
      * @throws RemoteException if there is any network or remote error
     *
      */
     public ComponentDescription getComponentDescription ( long timeout) throws SmartFrogException {
         long endTime = (new Date()).getTime()+timeout;
         synchronized (visitingLocks) {
             while (visitingLocks.longValue()!=0L) {
                     // try to return the String if not visiting logs.
                     // if name in locks => process not ready, pretend not found...
                     if (visitingLocks.longValue()==0L){
                         return cd;
                     } else {
                         // not found, wait for leftover timeout
                        long now = (new Date()).getTime();
                        if (now>=endTime) {
                         throw new SmartFrogException("Description creation Timeout ("+ (timeout/1000) +"sec)");
                        }
                         try {
                             visitingLocks.wait(endTime-now);
                         } catch (InterruptedException e) {
                             return cd;
                         }

                     }
            }
        }
        return cd;
    }

     /**
      * Allows a visitor to notify that it is visiting a new node

      * @throws RemoteException if there is any network or remote error
      *
      */
     public void visiting(String name, Integer numberOfChildren) throws RemoteException {
         // Notify any waiting threads that an attribute was added
         synchronized (visitingLocks) {
             visitingLocks= new Long (visitingLocks.longValue() + numberOfChildren.longValue());
         }
     }

     /**
      * Allows a visitor to notify that it has fishished
      * ready to receive deployment requests.
      *

      * @throws RemoteException if there is any network or remote error
      *
      */
     public void visited(String name) throws RemoteException {
         // Notify any waiting threads that an attribute was added
         synchronized (visitingLocks) {
             visitingLocks= new Long (visitingLocks.longValue()-1);
             if (visitingLocks.longValue()==0) {
                //done with all visits
                visitingLocks.notifyAll();
             }
         }
     }

    protected String getCDAsString(long timeout) {
        try {
            return "sfConfig extends {\n" + getComponentDescription(timeout).toString() + "}";
        } catch (Exception e) {
            return e.getMessage();
        }
    }


    /**
      * Tries to get the the String once the object finished visiting all nodes
      * or until given timeout expires.
      *
      * @param timeout max time to wait in millis
      *
      * @return The string representation of the description
      *
      * @throws Exception attribute not found after timeout
      * @throws RemoteException if there is any network or remote error
     *
      */
     public String toString ( long timeout) throws Exception {
        return getCDAsString(timeout);
    }

    /** This modifies the defult timeout used to
     *  wait for the dump operation to complete.
     *
     * @param timeout
     */
    public void setTimeout(long timeout) {
        this.timeout=timeout;

    }

    /**
     * Returns a string representation of the component. This will give a
     * description of the component which is parseable, and deployable
     * again... Unless someone removed attributes which are essential to
     * startup that is. Large description trees should be written out using
     * writeOn since memory for large strings runs out quick! toString() times out
     *
     * @return string representation of component
     */
    public String toString() {
       try {
         String cdStr = toString(timeout);
         return cdStr;
       } catch (Exception ex){
           ex.printStackTrace();
           return (ex.toString());
       }
    }

    public void toFile(String directory, String fileName){
        Writer out = null;

        try {
            out = new FileWriter(new File(directory, fileName));
            try {
                ((PrettyPrinting)getComponentDescription(timeout)).writeOn(out, 1);
            } catch (SmartFrogException e) {
                out.write(e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

}
