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

public interface SFScript {

    /** Reference to shell component.
     String name for attribute. Value {@value}.*/
   final static String ATTR_SHELL = "shell";

    /** Command executed during sfDeploy. They could be Strings or
     Vectors of Strings or component description.
     String name for attribute. Value {@value}.*/
   final static String ATTR_DEPLOY_SCRIPT = "deployScript";

   /** Command executed during sfStart. They could be Strings or
    Vectors of Strings or component description.
   String name for attribute. Value {@value}. */
   final static String ATTR_START_SCRIPT = "startScript";

   /** Exit command executed during sfTerminate. They could be Strings or
    Vectors of Strings or component description.
    String name for attribute. Value {@value}.*/
   final static String ATTR_TERMINATE_SCRIPT = "terminateScript";

   /** This indicates if the component should terminate when exec termainates
    * . String name for attribute. Value {@value}. */
   public final static String ATR_AUTO_TERMINATE = "autoTerminate";

   /** This indicates if the component should redirect the out and err streams of
    * the script to the system stdout and stderr. Value {@value}. */
   public final static String ATR_VERBOSE = "verbose";

}
