package org.smartfrog.projects.alpine.config.smartfrog;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 
 */
public interface AlpineEndpoint extends Remote {
    
    /**
     * name of the endpoint
     */ 
    public static final String ATTR_NAME="name";
    
    
    public static final String ATTR_CONTENT_TYPE="getContentType";
    /**
     * HTML content for a get
     */ 
    public static final String ATTR_GET_MESSAGE="getMessage";

    public static final String ATTR_GET_RESPONSE = "getResponse";

    
    /**
     * resource of the WSDL. for ?WSDL operations
     */ 
    public static final String ATTR_WSDL="wsdl";
    
    /**
     * override of factory
     */ 
    public static final String ATTR_FACTORY="factory";
    
    /**
     * Add a handler, or replace an existing one. 
     * @param name unique name for this instance of the handler
     * @param classname
     */ 
    boolean addHandler(String name, String classname) throws RemoteException;
    
    /**
     * Remove a handler
     * @param name
     * @throws RemoteException
     */ 
    boolean removeHandler(String name) throws RemoteException;
}
