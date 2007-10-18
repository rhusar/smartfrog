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


package org.smartfrog.services.filesystem.replacevar;

/**
 *  Title: Will parse a file replacing certain variables(keys) with others(values)
 */

import java.io.*;
import java.util.*;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;

/**
 */
public class ParserVar extends Thread {

   Prim sfObj = null;
   DataParser dataParser=null;

   //Should teminate process if spanned process finishes?
   String status = "stopped";
   long delay = (0 * 1000);
   // 2 seconds

   // Process executed
   Thread process = null;

   LogSF log = null;

   /**
    *  Constructor for the RunProcess object
    *
    */
   public ParserVar(Prim sfObj,DataParser dataParser) {
      String name = "ParserVar";
      try {
         name = sfObj.sfCompleteName()+"_"+name;
      } catch (Exception ex){
        //ignore, not important
      }
      this.log = LogFactory.getLog(name);
      this.setName(name);
      this.sfObj = sfObj;
      this.dataParser=dataParser;
   }
   /**
    *  Sets the sFObj attribute of the RunProcess object
    *
    *@param  sfObj  The new sFObj value
    */
   public void setSfObj(Object sfObj) {
      //System.out.println("setting Prim to terminate...");
      if (sfObj instanceof Prim) {
         this.sfObj = (Prim)sfObj;
         //System.out.println("setted..");
      }
   }

   /**
    *  Sets the delay attribute of the RunProcess object
    *
    *@param  delay  The new delay value
    */
   public void setDelay(long delay) {
      this.delay = delay;
   }

   /**
    *  Gets the status attribute of the RunProcess object
    *
    *@return    The status value
    */
   public String getStatus() {
      return this.status;
   }

   /**
    *  Gets the delay attribute of the RunProcess object
    *
    *@return    The delay value
    */
   public long getDelay() {
      return this.delay;
   }


   /**
    *  Finish
    */
   public void finish() {
      this.status = "stopped";
   }

   TerminationRecord termR;
   /**
    *  Terminate
    */
   public void terminate() {
      if ((this.sfObj != null) && (this.sfObj instanceof PrimImpl)) {
         try {
           termR = (new TerminationRecord("normal", "ParserFile terminated ("+this.dataParser.getFileName()+").", null));
           // Proper termination of a component!
           Runnable terminator = new Runnable() {
	      public void run() {
            if (log.isDebugEnabled()) log.debug ("ReplaceVar terminated.");
		      ((PrimImpl) sfObj).sfTerminate(termR);
	      }
	   };
           new Thread(terminator).start();

         } catch (Exception ex) {
             if (log.isErrorEnabled()) log.error ("Problem during termination > " + ex.getMessage() + "", ex);
         }
        //this.log("ReplaceVar terminated.",2);
      } else {
        if (log.isDebugEnabled()) log.debug ("ReplaceVar: Wrong component to terminate.");
      }
   }

   /**
    *  Detach
    */
   public void detach() {
      if (this.sfObj != null) {
         try {
            this.sfObj.sfDetach();
         } catch (Exception ex) {
            if (log.isErrorEnabled()) log.error ("ParserVar.detach ("+this.dataParser.getFileName()+"):" + ex.getMessage(), ex);
         }
      }
   }


   /**
    *  Main processing method for the RunProcess object
    */
   public void run() {
         status = "running";
         try {
            //this.log("Command Start: "+command,3);
            //this.log("RunProcessInfo > " + this.toString(), 5);
              if (log.isDebugEnabled()) log.debug ("Parsing > " + this.dataParser.getFileName() + " ");
            ReplaceVar replaceVar = new ReplaceVar (dataParser.getFileName(),dataParser.getNewFileName(), false);
            //First: append
            replaceVar.append(dataParser.getDataAppend());
            if (log.isDebugEnabled()) log.debug ("Append. > " + dataParser.getFileName() + "");
            //Second: replace
            replaceVar.setSetting(dataParser.getDataReplace());
            if (log.isDebugEnabled()) log.debug ("Replace. > " + dataParser.getFileName() + "");
            replaceVar.flush(); //This sould be handle in the component. REVIEW. TODO
            if (log.isDebugEnabled()) log.debug ("Finished Parsing > " + dataParser.getFileName() + "");
            Thread.sleep(delay);
         } catch (Exception ex) {
            if (log.isErrorEnabled()) log.error ("Problem parsing > " + ex.getMessage() + "", ex);
         }
         this.finish();
         try {
            if (dataParser.shouldDetach()) detach();
            if (dataParser.shouldTerminate()) terminate();
            if (log.isDebugEnabled()) log.debug ("Terminating > " + dataParser.getFileName() + "");
         } catch (Exception e) {
             if (log.isErrorEnabled()) log.error ("Error during termination:"+ e.getMessage() + "", e);
         }
   }

}
