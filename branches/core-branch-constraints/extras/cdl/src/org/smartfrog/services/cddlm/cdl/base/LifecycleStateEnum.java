/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.cddlm.cdl.base;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;

import java.rmi.RemoteException;

/**
 * enumeration of lifecycle states. This type maps to the cmp: LifeCycleState,
 * which is itself an extension of the Muws state.
 */
public enum LifecycleStateEnum {
    undefined("UndefinedState"),
    instantiated("InstantiatedState"),
    initialized("InitializedState"),
    running("RunningState"),
    failed("FailedState"),
    terminated("TerminatedState");
    public static final String STATE = "State";

    LifecycleStateEnum(String textName) {
       xmlName = textName;
   }

    private final String xmlName;

    /**
     * Convert the XML value into an enumeration entry
     * @param text text to parse
     * @return the enumeration
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException  if we cannot parse
     */
    public static LifecycleStateEnum extract(String text)
        throws CdlXmlParsingException{
        if(undefined.hasXmlName(text)) return undefined;
        if (instantiated.hasXmlName(text)) return instantiated;
        if (initialized.hasXmlName(text)) return initialized;
        if (running.hasXmlName(text)) return running;
        if (failed.hasXmlName(text)) return failed;
        if (terminated.hasXmlName(text)) return terminated;
        throw new CdlXmlParsingException("No CDL state matches ["+text+"]");
    }

    private boolean hasXmlName(String name) {
        return xmlName.equals(name);
    }

    public String getXmlName() {
        return xmlName;
    }


    /**
     * Returns the name of this enum constant, as contained in the declaration.
     * This method may be overridden, though it typically isn't necessary or
     * desirable.  An enum type should override this method when a more
     * "programmer-friendly" string form exists.
     *
     * @return the name of this enum constant
     */
    public String toString() {
        return xmlName;
    }

    /**
     * Infer the state of a prim
     * @param prim, can be null
     * @return the inferres state of the component
     * @throws RemoteException if prim calls failed.
     */
    public static LifecycleStateEnum infer(Prim prim) throws RemoteException {
        if(prim==null) {
            return undefined;
        }
        if(prim.sfIsTerminated()) {
            return terminated;
        }
        if(prim.sfIsStarted()) {
            return running;
        }
        if(prim.sfIsDeployed()) {
            return initialized;
        }
        //anything else? Instantiated
        return instantiated;
    }

}



