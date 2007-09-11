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

package org.smartfrog.sfcore.logging;

import java.lang.reflect.Method;
/**
 *  Generic Action thread launcher
 */
public class ActionThread implements Runnable {

   /**
    *  Action object in where to invoke method in a separate thread
    */
   private Log[] actions;

   /**
    *  Method to invoke
    */
   private Method method;

   /**
    *  Arguments of the method to invoke
    */
   private Object[] args;


   /**
    *  Action launched constructor and executor
    *
    *@param  action  Action object in where to invoke method in a separate thread
    *@param  method  method to invoke
    *@param  args    Arguments of the method to invoke
    */
   ActionThread(Log[] action, Method method, Object[] args) {
      this.actions = action;
      this.method = method;
      this.args = args;
      Thread thread = new Thread(this);
      thread.start();
   }

    /**
     * Run method for Action thread
     */
   public void run() {
   }


    /**
     * Thread body launches method with arguments given from object specified
     * @param actionmethod  method to invoke
     * @return ActionThread
     */
   public ActionThread executeAction(Method actionmethod){
       this.method=actionmethod;
       return executeAction();
   }

   /**
    *  Thread body launches method with arguments given from object specified
    *  @return ActionThread
    */
   public ActionThread executeAction() {
      synchronized (actions) {
          for (Log action : actions) {
              try {
                  method.invoke(action, args);
              } catch (Exception e) {
                  System.err.println("ActionThread - Error launching thread:\n" + e);
                  e.printStackTrace();
              }
          }
      }
      return this;
   }

    /**
     * Set the Action object
     * @param actions Action object in where to invoke method in a separate thread
     * @return ActionThread
     */
   public ActionThread setAction(Log[] actions){
       this.actions = actions;
       return this;
   }

    /**
     * Set the method to invoke
     * @param method method to invoke
     * @param args Action object in where to invoke method in a separate thread
     * @return ActionThread
     */
   public ActionThread setMethod(Method method, Object[] args){
       this.method = method;
       this.args=args;
       return this;
   }

}
