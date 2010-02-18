/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.components.cluster;

import org.smartfrog.services.assertions.AssertComponent;
import org.smartfrog.sfcore.workflow.conditional.Condition;

/**
 * An interface for conditions that may choose to check themselves on startup or liveness. Created 28-May-2008 15:43:54
 */


public interface CheckableCondition extends Condition {
    /**
     * Check on startup {@value}
     */
    String ATTR_CHECK_ON_STARTUP = AssertComponent.ATTR_CHECK_ON_STARTUP;
    /**
     * Check on liveness {@value}
     */
    String ATTR_CHECK_ON_LIVENESS = AssertComponent.ATTR_CHECK_ON_LIVENESS;

}
