/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.tools.ant;

import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.BuildException;

/**
 *
 * Created 11-Sep-2007 11:34:19
 * @ant.task category="SmartFrog" name="sf-conditional"
 */

public class ConditionalSequence extends Sequential {
	private String ifAttr="",unlessAttr="";

    public void setIf(String arg) {
        ifAttr = arg;
    }

    public void setUnless(String arg) {
        unlessAttr = arg;
    }

    /**
     * Execute all nestedTasks if the conditions permit it
     *
     * @throws BuildException if one of the nested tasks fails.
     */
    public void execute() throws BuildException {
        if((ifAttr.length() == 0 || getProject().getProperty(ifAttr) != null) &&
                ( unlessAttr.length()==0 || getProject().getProperty(unlessAttr) == null)) {
            super.execute();
        }
    }


}
