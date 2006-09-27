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

package org.smartfrog.services.deployapi.system;

import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.sfcore.prim.Prim;
import org.ggf.cddlm.generated.api.CddlmConstants;
import nu.xom.Element;
import nu.xom.Elements;

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

    LifecycleStateEnum(String textName) {
       this.xmlName = textName;
   }

    private final String xmlName;

    public static LifecycleStateEnum extract(String text) {
        return valueOf(text);
    }

    /**
     * Go from XML to types
     * @param grandparent
     * @return
     * @throws
     */
    public static LifecycleStateEnum extract(Element grandparent) {
        Element parent = grandparent.getFirstChildElement("State",
                CddlmConstants.CDL_CMP_TYPES_NAMESPACE);
        if(parent==null) {
            throw FaultRaiser.raiseBadArgumentFault(
                    "No elements under cmp:State element under " + grandparent);
        }
        Elements elements = parent.getChildElements();
        if(elements.size()==0) {
            throw FaultRaiser.raiseBadArgumentFault("No elements under cmp:State "+parent);
        }
        Element child = elements.get(0);
        if(!CddlmConstants.CDL_CMP_TYPES_NAMESPACE.equals(child.getNamespaceURI())) {
            throw FaultRaiser.raiseBadArgumentFault(
                    "First state element is not in the expected namespace "+child);
        }
        String statename=child.getLocalName();
        for(LifecycleStateEnum e: LifecycleStateEnum.values()) {
            if(e.getXmlName().equals(statename)) {
                return e;
            }
        }
        throw FaultRaiser.raiseBadArgumentFault(
                "no state found matching " + child);
    }


    /**
     * Return a cmp:State element containing the local state
     * as a direct child. The name of the local state is defined
     * by the xmlName value of the state.
     * @return
     */
    public SoapElement toCmpState() {
        SoapElement parent = XomHelper.cmpElement("State");
        SoapElement child = XomHelper.cmpElement(xmlName);
        parent.appendChild(child);
        return parent;
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



