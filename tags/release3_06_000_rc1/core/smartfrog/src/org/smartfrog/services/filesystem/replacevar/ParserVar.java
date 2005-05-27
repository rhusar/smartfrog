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

/**
 */
public class ParserVar extends Thread {

   Prim sfObj = null;
   DataParser dataParser=null;

   // Default 2
   // 5- info log, 0 - Critical. Use -1 to avoid log
   public int logger = 2;
   boolean printStack = false;

   //Should teminate process if spanned process finishes?
   String status = "stopped";
   long delay = (0 * 1000);
   // 2 seconds

   // Process executed
   Thread process = null;
   /**
    *  Constructor for the RunProcess object
    *
    *@param  command  Command to exec
    */
   public ParserVar(Prim sfObj,DataParser dataParser) {
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
    *  Sets the logLevel attribute of the RunProcess object
    *
    *@param  logLevel  The new logLevel value
    */
   public void setLogLevel(int logLevel) {
      this.logger = logLevel;
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
    *  Description of the Method
    */
   public void finish() {
      this.status = "stopped";
   }

   TerminationRecord termR;
   /**
    *  Description of the Method
    */
   public void terminate() {
      if ((this.sfObj != null) && (this.sfObj instanceof PrimImpl)) {
         try {
           termR = (new TerminationRecord("normal", "ParserFile terminated ("+this.dataParser.getFileName()+").", null));
           // Proper termination of a component!
           Runnable terminator = new Runnable() {
	      public void run() {
            log("ReplaceVar terminated.",2);
		      ((PrimImpl) sfObj).sfTerminateWith(termR);
	      }
	   };
           new Thread(terminator).start();

         } catch (Exception ex) {
            System.out.println("ParserVar.terminate:" + ex.getMessage());
            if (printStack) {
               ex.printStackTrace();
            }
         }
        //this.log("ReplaceVar terminated.",2);
      } else {
        this.log("ReplaceVar: Wrong component to terminate.",2);
      }
   }

   /**
    *  Description of the Method
    */
   public void detach() {
      if ((this.sfObj != null) && (this.sfObj instanceof Prim)) {
         try {
            this.sfObj.sfDetach();
         } catch (Exception ex) {
            System.out.println("ParserVar.detach ("+this.dataParser.getFileName()+"):" + ex.getMessage());
            if (printStack) {
               ex.printStackTrace();
            }
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
            this.log("Parsing > " + this.dataParser.getFileName() + " ", 3);
            ReplaceVar replaceVar = new ReplaceVar (dataParser.getFileName(),dataParser.getNewFileName(), false);
            //First: append
            replaceVar.append(dataParser.getDataAppend());
            this.log("Append. > " + dataParser.getFileName() + "", 3);
            //Second: replace
            replaceVar.setSetting(dataParser.getDataReplace());
            this.log("Replace. > " + dataParser.getFileName() + "", 3);
            replaceVar.flush(); //This sould be handle in the component. REVIEW. TODO
            this.log("Finished Parsing > " + dataParser.getFileName() + "", 3);
            Thread.sleep(delay);
         } catch (Exception ex) {
            this.log("Problem parsing > " + ex.getMessage() + "", 1);
            if (printStack) {
               ex.printStackTrace();
            }
         }
         this.finish();
         try {
            if (dataParser.shouldDetach()) detach();
            if (dataParser.shouldTerminate()) terminate();
            this.log("Terminating > " + dataParser.getFileName() + "", 3);
         } catch (Exception e) {
             if (printStack) {
               e.printStackTrace();
             }
         }
   }

   /**
    *  Description of the Method
    *
    *@param  message   Description of Parameter
    *@param  severity  Description of Parameter
    */
   void log(String message, int severity) {
      //System.out.println( "["+this.nameProcess+"] "+message,"RUNProcess",severity);
      try {
         //if (logger != false ) {
         if (logger >= severity) {
            System.out.println("[" + "" + "] " + message + ", ParserVar, " + severity);
         }
      } catch (Exception e) {
         if (printStack) {
            e.printStackTrace();
         }
      }
   }



}
