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
public interface SFScriptExecution extends ScriptExecution {

   /** String name for attribute. Value {@value}. */
   final static String ATR_ID = "ID";
   /** String name for optional attribute. Value {@value}. */
   final static String ATR_NAME = "name";
   /** String name for attribute. Value {@value}. */
   final static String ATR_EXEC = "exec";
   /** String name for attribute. Value {@value}. */
   final static String ATR_SCRIPT = "script";

   /** Command executed during sfStart. They could be Strings or
    Vectors of Strings or component description.
   String name for attribute. Value {@value}. */
   final static String ATTR_START_SCRIPT = "startScript";


   /** Command executed during sfDeploy. They could be Strings or
    Vectors of Strings or component description.
    String name for attribute. Value {@value}.*/
   final static String ATTR_DEPLOY_SCRIPT = "deployScript";


   /** Exit command executed during sfTerminate. They could be Strings or
    Vectors of Strings or component description.
    String name for attribute. Value {@value}.*/
   final static String ATTR_TERMINATE_SCRIPT = "terminateScript";


  /** This indicates if the component should detach when the
   * exec finishes. String name for attribute. Value {@value}. */
   final static String ATR_SHOULD_DETATCH = "shouldDetach";

   /** This indicates if the component should terminate when the
    * exec finishes. String name for attribute. Value {@value}. */
   final static String ATR_SHOULD_TERMINATE = "shouldTerminate";


   // @TODO add extra control for filters: message listeners and outputStreams
//   /** Output message to. */
//   final static String varOutputMsgTo = "outputMsgTo";
//
//   /** Object that implements org.smartfrog.services.display.PrintMsgInt
//    * /sfServices.sfDisplay uses it. */
//   final static String varErrorMsgTo = "errorMsgTo";
//
//   /** Object that implements org.smartfrog.services.display.PrintErrMsgInt
//    * /sfServices.sfDisplay uses it. */
//   final static String varOutputStreamTo = "OutputStreamTo";
//
//   /** Object that implements
//    * org.smartfrog.services.os.runCmd.OutputStreamInt. */
//   final static String varErrorStreamTo = "errorStreamTo";

}
