/** (C) Copyright 1998-2009 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.scripting;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * A remote interface to trigger evaluation of scripts.
 */

public interface RemoteScriptPrim
    extends Remote {
    String ATTR_SF_DEPLOY_CODE = "sfDeployCode";
    String ATTR_SF_START_CODE = "sfStartCode";
    String ATTR_PING_CODE = "sfPingCode";
    String ATTR_SF_TERMINATE_WITH_CODE = "sfTerminateWithCode";
    String ATTR_PORT = "port";
    String ATTR_SF_SCRIPT_CODE_BASE = "sfScriptCodeBase";
    String ATTR_ATTRIBUTES_AS_VARIABLES = "attributesAsVariables";
    String ATTR_LANGUAGE = "language";

    /**
   * Evaluate the String as a beanshell script
   * @param script the script as a string.
   * @throws Exception if the execution of the script fails.
   */
  public Object eval(String script) throws SmartFrogException, RemoteException;

  /**
   * Bind an object to a name in the beanshell interpreter.
   * @param name the name you want the object to be called in the interpreter
   * @param obj the object you want to register in the interpreter.
   */
  public void setRemote(String name, Object obj) throws SmartFrogException,
      RemoteException;

}