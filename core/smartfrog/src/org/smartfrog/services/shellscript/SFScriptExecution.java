/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.shellscript;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;
import java.util.List;

/**
 * Interface that provides the API to the script component, allowing
 * other co-located components to submit script commands.
 *
 * The interface provides for the submission of lines of script and the
 * ability to lock a script component for unique use for a period. This ensures
 * that sequences of script commands will not be interleaved with other
 * script requets to the component.
 *
 * The operational model is asynchronous, in that the execute operation only queues
 * the execute request and does not wait until it is complete. An object implementing
 * the ScriptResult interface is returned, and this can be queried to find if the script has
 * completed and obtain the resultant output, both error and normal.
 *
 * Commands to be executed are passed in as a list the following format.
 * Each element is either a string, in which case it is treated as a command, or
 * a list in which case the command is the space-separated "toString" of its elements.
 *
 */
public interface SFScriptExecution extends  ScriptExecution {

   /** String name for optional attribute "cmd". Value {@value}. */
   final static String ATR_CMD = "cmd";
   /** String name for optional attribute "envp". Value {@value}. */
   final static String ATR_ENVP = "envp";
   /** String name for optional attribute "dir". Value {@value}. */
   final static String ATR_DIR = "dir";

   /** String name for optional attribute "message". Value {@value}. */
   final static String ATR_LINE_SEPARATOR = "lineSeparator";

   /** String name for optional attribute "ID". Value {@value}. */
   final static String ATR_ID = "ID";
   /** String name for optional attribute "name". Value {@value}. */
   final static String ATR_NAME = "name";
}
