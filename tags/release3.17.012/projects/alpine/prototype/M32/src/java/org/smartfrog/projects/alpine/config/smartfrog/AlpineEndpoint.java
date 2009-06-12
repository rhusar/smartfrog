package org.smartfrog.projects.alpine.config.smartfrog;

import org.smartfrog.projects.alpine.core.ContextConstants;

import java.rmi.Remote;

/**
 * Remote interface for the endpoints
 * All the attributes are references to the declarations in 
 * {@link ContextConstants}. They are there so that even if you dont
 * build the smartfrog stuff, the constants are all there. 
 */
public interface AlpineEndpoint extends Remote {

    /**
     * name of a servlet that we can use to extract information from. 
     */
    public static final String ATTR_SERVLET="servlet";

    /**
     * name of the endpoint
     * {@value}
     */
    public static final String ATTR_NAME= ContextConstants.ATTR_NAME;


    /**
     * Text for a message
     * {@value}
     */
    public static final String ATTR_CONTENT_TYPE= ContextConstants.ATTR_GET_CONTENT_TYPE;
    /**
     * HTML content for a get {@value}
     */
    public static final String ATTR_GET_MESSAGE= ContextConstants.ATTR_GET_MESSAGE;

    /**
     * integer response code for a get {@value}
     */
    public static final String ATTR_GET_RESPONSECODE = ContextConstants.ATTR_GET_RESPONSECODE;

    /**
     * path for the endpoint
     * {@value}
     */
    public static final String ATTR_PATH=ContextConstants.ATTR_PATH;


    /**
     * resource of the WSDL. for ?WSDL operations
     * {@value}
     */
    public static final String ATTR_WSDL= ContextConstants.ATTR_WSDL;

    /**
     * override of factory
     * {@value}
     */
    public static final String ATTR_FACTORY= ContextConstants.ATTR_FACTORY;

    /**
     * Name of a list of handlers
     * {@value}
     */
    public static final String ATTR_HANDLER_LIST= ContextConstants.ATTR_HANDLERS;


}
