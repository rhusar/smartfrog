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


package org.smartfrog.services.scripting.javax;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.rmi.RemoteException;

/**
 * scriptable condition
 */
public class JavaxScriptingConditionImpl extends JavaxScriptingImpl implements JavaxScriptingCondition {
    public static final String ERROR_NOT_BOOLEAN = "Return value is not a boolean:";

    private boolean condition;

    public JavaxScriptingConditionImpl() throws RemoteException {
    }

    @Override
    protected void bindAttributes() throws SmartFrogResolutionException, RemoteException {
        condition = sfResolve(ATTR_CONDITION, condition, true);
        super.bindAttributes();
    }

    public boolean isCondition() {
        return condition;
    }

    public void setCondition(boolean condition) throws SmartFrogRuntimeException, RemoteException {
        this.condition = condition;
        sfReplaceAttribute(ATTR_CONDITION, condition);
    }

    public synchronized boolean evaluate() throws RemoteException, SmartFrogException {
        Object result = resolveAndEvaluate(ATTR_SF_CONDITION_RESOURCE, ATTR_SF_CONDITION_CODE);
        return isCondition();

/*
        if (!(result instanceof Boolean)) {
            throw new SmartFrogException(ERROR_NOT_BOOLEAN + result);

        } else {
            return ((Boolean) result);
        }
*/
    }
}
