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
    *  Methos to invoke
    */
   private Method method;

   /**
    *  Arguments of the method to invoke
    */
   private Object[] args;


   /**
    *  Action launched constructor and executor
    *
    *@param  action  Description of the Parameter
    *@param  method  Description of the Parameter
    *@param  args    Description of the Parameter
    */
   ActionThread(Log[] action, Method method, Object[] args) {
      this.actions = action;
      this.method = method;
      this.args = args;
      Thread thread = new Thread(this);
      thread.start();
   }


   public void run() {
   }



   public ActionThread executeAction(Method method){
       this.method=method;
       return executeAction();
   }

   /**
    *  Thread body launches method with arguments given from object specified
    */
   public ActionThread executeAction() {
      synchronized (actions) {
          for (int i = 0; i<actions.length; i++) {
              try {
                  method.invoke(actions[i], args);
              } catch (Exception e) {
                 System.err.println("ActionThread - Error launching thread:\n"+e);
                 e.printStackTrace();
              }
          }
      }
      return this;
   }


   public ActionThread setAction(Log[] actions){
       this.actions = actions;
       return this;
   }

   public ActionThread setMethod(Method method, Object[] args){
       this.method = method;
       this.args=args;
       return this;
   }

}
