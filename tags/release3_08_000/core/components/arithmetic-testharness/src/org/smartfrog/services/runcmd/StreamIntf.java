package org.smartfrog.services.runcmd;

import java.io.*;

/**
 *  Description of the Interface
 *
 *@author     julgui
 *@created    01 November 2001
 */
public interface StreamIntf extends OutputStreamIntf {

    public OutputStream getErrorStream();

    public InputStream getInputStream();

}
