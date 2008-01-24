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
 * This task gives us a conditional sequence, one that only executes if needed.
 * <ol>
 * <li>It runs by default</li>
 * <li>It has <code>if</code> and <code>unless</code> attributes that work as with the if/unless attributes of
 * an Ant target: they check for the presence of a property.
 * <li>It has an iftrue attribute that checks the string value equals true|yes|on</li>
 * <li>It has an iffalse attribute that checks the string value equals any other value than true|yes|on</li>
 * </ol>
 * The task only executes its children if, when either/both of the if and iftrue values evaluate to true, and, if
 * set, the ifFalse attribute evaluates to false.
 * @ant.task category="SmartFrog" name="sf-conditional"
 */

public class ConditionalSequence extends Sequential {
	private String ifAttr="",unlessAttr="";
    private Boolean ifTrue;
    //false is anything but true
    private Boolean ifFalse;

    public void setIf(String arg) {
        ifAttr = arg;
    }

    public void setUnless(String arg) {
        unlessAttr = arg;
    }

    public void setIfTrue(Boolean ifTrue) {
        this.ifTrue = ifTrue;
    }

    public void setIfFalse(Boolean ifFalse) {
        this.ifFalse = ifFalse;
    }

    /**
     * Execute all nestedTasks if the conditions permit it
     *
     * @throws BuildException if one of the nested tasks fails.
     */
    public void execute() throws BuildException {

        //true if unset. if set, must eval to true
        boolean ifTrueTrue = ifTrue == null || ifTrue.booleanValue();
        //true if unset or evals to true
        boolean ifAttrSet = ifAttr.length() == 0 || getProject().getProperty(ifAttr) != null;
        //false unless actually defined
        boolean unlessAttrSet = unlessAttr.length() > 0 && getProject().getProperty(unlessAttr) != null;
        boolean ifFalseFalse = ifFalse != null && !ifFalse.booleanValue();
        if (ifAttrSet && ifTrueTrue && ifFalseFalse && !unlessAttrSet) {
            super.execute();
        }
    }


}
