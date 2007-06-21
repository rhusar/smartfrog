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
import org.smartfrog.sfcore.common.Dumper;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.prim.Dump;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;

import java.rmi.RemoteException;
import java.util.*;
import java.io.*;


public class DefaultDumper implements Dump, Dumper, Serializable {

    private Reference rootRef = null;

    private ComponentDescription cd = null;

    private Long visitingLock = new Long(1); //Lock
    private Long visitingLocks = new Long(1); //Counter


    /**
     * Special keys that are created by the runtime and that should be removed to have a deployable description.
     * Value: @value
     */
    private String[] sfKeysToBeRemoved = new String[] {"sfHost", "sfProcess", "sfLog", "sfBootDate","sfParseTime","sfDeployTime"};

    /** Default timeout (@value msecs), in large distributed deployments
     * it could need more time to reach the final result*/
    long timeout = (1*30*15*1000L); //(2*60*1000L);


    public DefaultDumper (Prim from){
        try {
            rootRef = from.sfCompleteName();
        } catch (RemoteException e) {
            if (sfLog().isErrorEnabled()) sfLog().error(e);
        }
    }

   /**
     * Components use this methods to dump their state to when requested (using
     * sfDumpState). With this information, this object builds an internal
     * representation of the component and its children.
     * This will result in a description of the component which is parseable, and deployable
     * again... Unless someone removed attributes which are essential to
     * startup that is.
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
       Context stateCopy =  (Context)((Context)state).clone();

       //Remove non desired sf attribute keys
       for (int i=0; i< this.sfKeysToBeRemoved.length; i++){
           if (stateCopy.sfContainsAttribute(sfKeysToBeRemoved[i])) {
               try {
                   stateCopy.sfRemoveAttribute(sfKeysToBeRemoved[i]);
               } catch (SmartFrogContextException e) {
                   if (sfLog().isWarnEnabled()) sfLog().warn(e);
               }
           }
       }

       if (rootRef == from.sfCompleteName()){
           cd = new ComponentDescriptionImpl(null,(Context)stateCopy,false);
           //if (sfLog().isInfoEnabled()) sfLog().info("New CD: "+rootRef+"\n "+from.sfCompleteName());
       } else {
           //if (sfLog().isInfoEnabled()) sfLog().info("From: "+from.sfCompleteName());
           Reference searchRef =  (Reference)from.sfCompleteName().copy();
           for (Enumeration e = rootRef.elements(); e.hasMoreElements();) {
               searchRef.removeElement((ReferencePart)e.nextElement());
           }
           String name = ((HereReferencePart)(searchRef.lastElement())).getValue().toString();
           searchRef.removeElement(searchRef.lastElement());
           modifyCD(searchRef, name, stateCopy);
       }
       //System.out.println("***************************\nFrom: "+from.sfCompleteName()+"\n"+cd+"\n**************************");
       visited(from.sfCompleteName().toString());
   }

    /** Method that updates the component description. It creates a component description for context and
     * places it in whereRef named name.
     *
     * @param whereRef Where in CD to place the new context
     * @param name  attribute name
     * @param contextCopy context for the new component description node
     */
    public void modifyCD(Reference whereRef,String name, Context contextCopy ) {
        try {
            ComponentDescription placeHolder = (ComponentDescription)cd.sfResolve(whereRef);
            ComponentDescription child =new ComponentDescriptionImpl(placeHolder, contextCopy,true);
            placeHolder.sfReplaceAttribute(name, child);
        } catch (SmartFrogException e) {
            if (sfLog().isErrorEnabled()) sfLog().error(e);
        }
    }


    /**
     * Returns a component description representation of the component and its children.
     * This will give a description of the component which is parseable, and deployable
     * again... Unless someone removed attributes which are essential to
     * startup that is.
     *
      * Tries to get the the ComponentDescription once  the object finished visiting all nodes
      * or until a given timeout expires.
      *
      * @param timeout max time to wait in millis
      *
      * @return The component description representing the deployed system
      *
      * @throws Exception operation not completed after timeout
     *
      */
     public ComponentDescription getComponentDescription ( long timeout) throws SmartFrogException {
         long endTime = (new Date()).getTime()+timeout;
         synchronized (visitingLock) {
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
             visitingLocks= new Long (visitingLocks.longValue() + numberOfChildren.longValue());
             //if (sfLog().isInfoEnabled()) sfLog().info("Visiting #"+visitingLocks+ " "+name);
         }
     }

     /**
      * Allows a visitor to notify that it has fishished
      *
      * @throws RemoteException if there is any network or remote error
      *
      */
     public void visited(String name) throws RemoteException {
         // Notify any waiting threads that an attribute was added
         synchronized (visitingLock) {
             visitingLocks= new Long (visitingLocks.longValue()-1);
             if (visitingLocks.longValue()==0) {
                //done with all visits
                visitingLock.notifyAll();
             }
         }
     }

    /**
     * Returns a deployable String representation of the application component description
     * @param timeout
     * @return
     */
    protected String getCDAsString(long timeout) {
        try {
            return "sfConfig extends {\n" + getComponentDescription(timeout).toString() + "}";
        } catch (Exception e) {
            if (sfLog().isWarnEnabled()) sfLog().warn(e);
            return e.getMessage();
        }
    }


    /**
      * Tries to get the the String representation of the coponent description
     *  once the object finished visiting all nodes
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

    /** This modifies the default timeout used to
     *  wait for the dump operation to complete.
     *
     * @param timeout
     */
    public void setTimeout(long timeout) {
        this.timeout=timeout;

    }

    /** This modifies the default set of sfKeys that are removed from every context.
     * @param sfKeysToBeRemoved
     */
    public void sfKeysToBeRemoved (String[] sfKeysToBeRemoved) {
        this.sfKeysToBeRemoved = sfKeysToBeRemoved;

    }

    /**
     * Returns a string representation of the component. This will give a
     * description of the component which is parseable, and deployable
     * again... Unless someone removed attributes which are essential to
     * startup that is. Large description trees should be written out using
     * getCDtoFile since memory for large strings runs out quick! toString() times out
     * by default. See @see setTimeout to change default value.
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

    /**
     * Returns a string representation of the component stored into a file. This will give a
     * description of the component which is parseable, and deployable
     * again... Unless someone removed attributes which are essential to
     * startup that is. This operation times out
     * by default. See @see setTimeout to change default value.
     * It overwrites existing files without warning.
     */

    public void getCDtoFile(String fileName){
        Writer out = null;

        try {
            out = new FileWriter(new File(fileName));
            out.write("sfConfig ");
            try {
                ComponentDescription cd = getComponentDescription(timeout);
                cd.setEager(true);
                ((PrettyPrinting)cd).writeOn(out, 1);
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
     * For logging messages.
     * @return LogSF
     */
    public LogSF sfLog(){
        return LogFactory.sfGetProcessLog();
    }

}
