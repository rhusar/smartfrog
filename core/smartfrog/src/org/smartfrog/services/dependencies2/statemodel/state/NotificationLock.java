package org.smartfrog.services.dependencies2.statemodel.state;


/**

 */
public interface NotificationLock  {
   public void lock();
   public void unlock(boolean notify);
   
   public void notifyStateChange();

   public void threadStarted();
   public void threadStopped();
   
   
}
