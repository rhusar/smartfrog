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

package org.smartfrog.services.dependencies.statemodel.state;

import org.smartfrog.sfcore.common.SmartFrogException;

public class StateComponentTransitionException extends SmartFrogException {

    public enum StateComponentExceptionCode {
        GENERIC,
        COMPONENT_NOTRUNNING,
        CURRENTACTION_ONGOING,

        FAILEDTO_ACQUIRELOCK,
        FAILEDTO_EXECUTETRANSITIONSCRIPT,
        FAILEDTO_FINDSINGLEDEPENDENCYENABLED,
        FAILEDTO_GETNAMEDENABLEDTRANSITION,
        FAILEDTO_HANDLEDPES,
        FAILEDTO_RESOLVETRANSITIONEFFECTS,
        FAILEDTO_WRITEEVENTLOGBUTISPRESENT


    }

    private StateComponentExceptionCode code;
    public StateComponentTransitionException(final StateComponentExceptionCode code, final String msg, Throwable t) {
        super(msg, t);
        this.code = code;
    }

    public StateComponentTransitionException(final StateComponentExceptionCode code, final Throwable t) {
        super(t);
        this.code = code;
    }

    public StateComponentTransitionException(final StateComponentExceptionCode code) {
        super();
        this.code = code;
    }

    public StateComponentTransitionException(final Throwable t) {
        super(t);
        this.code = StateComponentExceptionCode.GENERIC;
    }

    /*public static final int g_DEPENDENCYVALUEUNRESOLVABLE
	public static final int g_DEPENDENCYVALUEUNRESOLVABLE=0xB;
	public static final int g_UNABLETOAPPLYEFFECTS=0xC;
	public static final int g_COMPONENTNOTENABLED=0xD;*/
	
	public StateComponentExceptionCode getExceptionCode(){ return code; }
}
