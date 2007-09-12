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

package org.smartfrog.sfcore.common;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;

import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.Dump;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;

import java.rmi.RemoteException;
import java.rmi.MarshalledObject;
import java.rmi.server.RemoteObject;
import java.util.Date;
import java.util.Enumeration;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

/**
 * @since 3.11.001
 */

public class DumperCDImpl implements Dumper {

    private Reference rootRef = null;

    private  boolean completed = false;

    private ComponentDescription cd = null;

    private Object visitingLock = new Object(); //Lock
    private Long visiting = new Long(1); //Counter of Visits


    /**
     * Special keys that are created by the runtime and that should be removed to have a deployable description.
     * Value: @value
     */
    private String[] sfKeysToBeRemoved = new String[] {"sfHost", "sfProcess", "sfLog", "sfBootDate","sfParseTime","sfDeployTime","sfTraceDeployLifeCycle","sfTraceStartLifeCycle"};

    /** Default timeout (@value msecs), in large distributed deployments
     * it could need more time to reach the final result*/
    long timeout = (1*30*1000L); //(2*60*1000L);


    public DumperCDImpl(Prim from){
        try {
            java.rmi.server.UnicastRemoteObject.exportObject(this);
            rootRef = from.sfCompleteName();
        } catch (RemoteException e) {
            if (sfLog().isErrorEnabled()) sfLog().error(e);
        }
    }

    public Dump getDumpVisitor(){
        Dump dumpVisitor = new DumpVisitorImpl(this);
        return dumpVisitor;
    }

    public Reference getRootRef(){
       return rootRef;
    }

    /** Method that updates the component description. It creates a component description for context and
     * places it in whereRef named name.
     *
     * @param from Owner of context to place in CD
     * @param stateCopy context for the new component description node
     */
    public void modifyCD(Reference from, Context stateCopy ) throws Exception, RemoteException {
        try {
            //Remove non re-deployable keys
            for (int i=0; i< sfKeysToBeRemoved.length; i++){
                if (stateCopy.sfContainsAttribute(sfKeysToBeRemoved[i])) {
                    try {
                        stateCopy.sfRemoveAttribute(sfKeysToBeRemoved[i]);
                    } catch (SmartFrogContextException e) {
                        if (sfLog().isWarnEnabled()) sfLog().warn(e);
                    }
                }
            }

            //Create new CD if not created yet and inspecting root ref
            if ((cd==null) && rootRef.equals(from)) {
                cd = new ComponentDescriptionImpl(null,stateCopy,false);
                return;
            }

            // searchRef
            Reference relativeRef = (Reference) from.copy();

            // Find relative reference where to place Context
            for (Enumeration enu = rootRef.elements(); enu.hasMoreElements();) {
                relativeRef.removeElement((ReferencePart)enu.nextElement());
            }

            String name = ((HereReferencePart)(relativeRef.lastElement())).getValue().toString();

            relativeRef.removeElement(relativeRef.lastElement());

            //Place stateCopy in the right spot inside cd.
            try {
                ComponentDescription placeHolder = (ComponentDescription)cd.sfResolve(relativeRef);
                ComponentDescription child =new ComponentDescriptionImpl(placeHolder, stateCopy,true);
                placeHolder.sfReplaceAttribute(name, child);
            } catch (SmartFrogException ex) {
                if (sfLog().isErrorEnabled()) sfLog().error(ex);
            }

        } catch (Exception e) {
            if (sfLog().isErrorEnabled()) sfLog().error(e);
            throw e;
        }
    }





    /**
      * Tries to get the the String once  the object finished visiting all nodes
      * or until given timeout expires.
      *
      * @param waitTimeout max time to wait in millis
      *
      * @return The string representation of the description
      *
      * @throws Exception attribute not found after timeout
      * @throws RemoteException if there is any network or remote error
     *
      */
     public ComponentDescription getComponentDescription ( long waitTimeout) throws SmartFrogException {
         long endTime = (new Date()).getTime()+waitTimeout;
         synchronized (visitingLock) {
             while (visiting.longValue()!=0L) {
                     // try to return the String if not visiting logs.
                     // if name in locks => process not ready, pretend not found...
                     if (visiting.longValue()==0L){
                         return cd;
                     } else {
                         // not found, wait for leftover timeout
                        long now = (new Date()).getTime();
                        if (now>=endTime) {
                          if (sfLog().isInfoEnabled()) sfLog().info("Timeout ("+completed+")");
                          if (completed) {
                              if (sfLog().isWarnEnabled()) sfLog().warn("Description creation Timeout (\"+ (timeout/1000) +\"sec)");
                              return cd;
                          } else {
                             throw new SmartFrogException("Description creation Timeout ("+ (waitTimeout/1000) +"sec)");
                          }
                        }
                         try {
                             visitingLock.wait(endTime-now);
                         } catch (InterruptedException e) {
                             return cd;
                         }

                     }
            }
            return cd;
        }
    }

     /**
      * Allows a visitor to notify that it is visiting a new node

      * @throws RemoteException if there is any network or remote error
      *
      */
     public void visiting(String name, Integer numberOfChildren) throws RemoteException {
         // Notify any waiting threads that an attribute was added
         synchronized (visitingLock) {
             visiting = new Long (visiting.longValue() + numberOfChildren.longValue());
         }
         //if (sfLog().isInfoEnabled()) sfLog().info("Visiting #"+visiting+ " "+name);
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
         synchronized (visitingLock) {
             visiting = new Long (visiting.longValue()-1);
             if (visiting.longValue()==0) {
                //done with all visits
                completed = true;
                visitingLock.notify();
             }
         }
         //if (sfLog().isInfoEnabled()) sfLog().info("Visited #"+visiting+ " "+name);
     }

    /** Get the resulting Component Description in a String format
     *
     * @param waitTimeout to get a valid result
     * @return a ComponentDescription in a deployable String format
     */
    protected String getCDAsString(long waitTimeout) {
        try {
            return "sfConfig extends {\n" + getComponentDescription(waitTimeout).toString() + "}";
        } catch (Exception e) {
            if (sfLog().isWarnEnabled()) sfLog().warn(e);
            return e.getMessage();
        }
    }


    /**
      * Tries to get the the String once the object finished visiting all nodes
      * or until given timeout expires.
      *
      * @param waitTimeout max time to wait in millis
      *
      * @return The string representation of the description
      *
      * @throws Exception attribute not found after timeout
      * @throws RemoteException if there is any network or remote error
     *
      */
     public String toString ( long waitTimeout) throws Exception {
        return getCDAsString(waitTimeout);
    }

    /** This modifies the default timeout used to
     *  wait for the dump operation to complete.
     *
     * @param timeout
     */
    public void setTimeout(long timeout) {
        this.timeout=timeout;

    }

    /** This modifies the default set of sfKeys that are removed from every context.
     * @todo once the visits are started this method should not allow any updates. In any case,
     * the updates are ignored once the visits start
     * @param keysToBeRemoved
     */
    public void sfKeysToBeRemoved (String[] keysToBeRemoved) {
        this.sfKeysToBeRemoved = keysToBeRemoved;

    }

    /** This modifies the default set of sfKeys that are removed from every context.
     *
     * @return String[] with the list of keys that will removed from every copied component description
     */

    public String[] getSFKeysToBeRemoved () {
        return sfKeysToBeRemoved;

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
           if (sfLog().isErrorEnabled()) sfLog().error(ex);
           return (ex.toString());
       }
    }

    public void getCDtoFile(String fileName){
        Writer out = null;

        try {
            out = new FileWriter(new File(fileName));
            out.write("sfConfig ");
            try {
                ComponentDescription componentDescription = getComponentDescription(timeout);
                componentDescription.setEager(true);
                ((PrettyPrinting)componentDescription).writeOn(out, 1);
            } catch (SmartFrogException e) {
                out.write(e.getMessage());
            }
        } catch (IOException e) {
            if (sfLog().isErrorEnabled()) sfLog().error(e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                if (sfLog().isErrorEnabled()) sfLog().error(e);
            }
        }
    }

    /**
     *
     * @return LogSF
     */
    public LogSF sfLog(){
        return LogFactory.sfGetProcessLog();
    }

}
