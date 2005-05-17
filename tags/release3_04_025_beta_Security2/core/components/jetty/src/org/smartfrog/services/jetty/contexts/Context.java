package org.smartfrog.services.jetty.contexts;

import java.rmi.RemoteException;
import java.rmi.Remote;
import org.mortbay.http.HttpServer;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.smartfrog.sfcore.reference.Reference;
/**
 * An interface for adding contexts to jetty server.
 * @author Ritu Sabharwal
 */

public interface Context extends Remote {
  
 
 public void addcontext(String contextPath, String webApp, boolean requestId)
	 throws RemoteException;

 public void addcontext(String contextPath, String resourceBase, String 
   classPath) throws RemoteException;
 
}
