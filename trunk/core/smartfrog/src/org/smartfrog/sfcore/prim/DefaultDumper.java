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

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;


public class DefaultDumper implements Dump {

    ComponentDescription cd = null;

    ComponentDescription lastCD =null;

    ComponentDescription lastChild = null;

    Hashtable visiting = new Hashtable();

    public void DefaultDumper (Context context){
         //cd = new ComponentDescriptionImpl(null,context,false);
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
       visiting("");

//       System.out.println("\n *******************************");
//       System.out.println("***"+from.sfCompleteName()+" \n"+state);
//       System.out.println("\n *******************************");
//       System.out.println("------Before------");
//       System.out.println("From \n"+from.sfCompleteName()+"");
//       System.out.println("---");
//       System.out.println("cd\n"+cd);
//       System.out.println("---");
//       System.out.println("lastCD\n"+lastCD);
//       System.out.println("---");
//       System.out.println("lastChild (what we added)\n"+lastChild);
//       System.out.println("--end-Before------");
       Context stateClone =  (Context)((Context)state).clone();
       if (cd==null){
           cd = new ComponentDescriptionImpl(null,(Context)stateClone,false);
           lastCD = cd;
           System.out.println("Created new cd for: "+from.sfCompleteName()+"\n"+cd.toString());
       } else {
           if (lastCD.sfContainsValue(from)){
//               System.out.println("for: "+from.sfCompleteName()+" using lastCD.");
           } else if (lastChild.sfContainsValue(from)) {
               lastCD=lastChild;
//               System.out.println("for: "+from.sfCompleteName()+" using lastChild.");
           } else if  (lastCD.sfParent().sfContainsValue(from)) {
                lastCD=lastCD.sfParent();
//               System.out.println("for: "+from.sfCompleteName()+" using lastParent.");
           } else {
//               System.out.println("I don't know what is going on" + from.sfCompleteName());
           }
           Object key = lastCD.sfAttributeKeyFor(from);
           lastChild = new ComponentDescriptionImpl (lastCD,stateClone,false);
           try {
             lastCD.sfReplaceAttribute(key,lastChild);
             System.out.println("lastChild (what we added) "+"name "+ key +" \n"+lastChild.toString());
           } catch (SmartFrogRuntimeException e) {
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
           }
       }
//       System.out.println("------after------");
//       System.out.println("From \n"+from.sfCompleteName()+"");
//       System.out.println("---");
//       System.out.println("cd\n"+cd);
//       System.out.println("---");
//       System.out.println("lastCD\n"+lastCD);
//       System.out.println("---");
//       System.out.println("lastChild (what we added)\n"+lastChild);
//       System.out.println("---end after----");
//       System.out.println("\n *******************************");
       visited(from.sfCompleteName().toString());
   }


    protected Long visitingLocks = new Long(0);
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
     public String toString( long timeout) throws Exception {
         long endTime = (new Date()).getTime()+timeout;
         Thread.sleep(10);
         synchronized (visitingLocks) {
             //System.out.println("will I wait? #"+visitingLocks);
             while (visitingLocks.longValue()!=0L) {
                     // try to return the String if not visiting logs.
                     // if name in locks => process not ready, pretend not found...
                     if (visitingLocks.longValue()==0L){
                         //System.out.println("No wait #"+visitingLocks);
                         return getStringCD();
                     } else {
                         // not found, wait for leftover timeout
                        long now = (new Date()).getTime();
                        if (now>=endTime) {
                         throw new SmartFrogException("Description creation Timeout");
                        }
                         try {
                             //System.out.println("Waiting: #"+visitingLocks);
                             visitingLocks.wait(endTime-now);
                             //System.out.println("Finished waiting: #"+visitingLocks);
                         } catch (InterruptedException e) {
                             //System.out.println(" Done with waiting (expired): #"+visitingLocks);
                             return getStringCD();
                         }

                     }
            }
        }
        //System.out.println(" no more waiting return actual cd");
        return getStringCD();
    }

     /**
      * Allows a visitor to notify that it is visiting a new node

      * @throws RemoteException if there is any network or remote error
      *
      */
     public void visiting(String name) throws RemoteException {
         // Notify any waiting threads that an attribute was added
         synchronized (visitingLocks) {
             visitingLocks= new Long (visitingLocks.longValue()+1);
             //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Visiting in progress: "+visitingLocks + " "+name);
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
             //System.out.println("------------------------------- Visiting in remaining: "+visitingLocks+ " "+name);
             if (visitingLocks.longValue()==0) {
               // System.out.println("Done all visits");
                visitingLocks.notifyAll();
             }
         }
     }

    protected String getStringCD(){
        return "sfConfig extends {\n" +
          cd.toString()
        + "}";
    }

    /**
     * Returns a string representation of the component. This will give a
     * description of the component which is parseable, and deployable
     * again... Unless someone removed attributes which are essential to
     * startup that is. Large description trees should be written out using
     * writeOn since memory for large strings runs out quick!
     *
     * @return string representation of component
     */
    public String toString() {
       try {
//       System.out.println("------------------");
//       System.out.println("---TO STring before: ---");
//       System.out.println("------------------");
         String cdStr = toString(5*1000L);
//       System.out.println("------------------");
//       System.out.println("---TO STring after: ---");
//       System.out.println("------------------");
//       System.out.println("cd\n"+cd);
//       System.out.println("---");
//       System.out.println("lastCD\n"+lastCD);
//       System.out.println("---");
//       System.out.println("lastChild (what we added)\n"+lastChild);
//       System.out.println("--end--toString---");
         return cdStr;    
       } catch (Exception ex){
           ex.printStackTrace();
           return (ex.toString());
       }
    }

}
