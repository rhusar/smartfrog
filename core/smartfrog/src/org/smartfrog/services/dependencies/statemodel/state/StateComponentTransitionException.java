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
	private int code;
	
	public StateComponentTransitionException(int code){
        this.code = code;
	}
	
	public StateComponentTransitionException(String msg, int code){
		super(msg);
		this.code = code;
	}
	
	public StateComponentTransitionException(String msg){
		this(msg, g_NOCODEAVAILABLE);
	}

    public StateComponentTransitionException(Throwable t) {
        super(t);
    }
	
	public static final int g_NOCODEAVAILABLE=0xFFFF;
	public static final int g_DEPENDENCYVALUEUNRESOLVABLE=0xB;
	public static final int g_UNABLETOAPPLYEFFECTS=0xC;
	public static final int g_COMPONENTNOTENABLED=0xD;
	
	public int getExceptionCode(){ return code; }	
}
