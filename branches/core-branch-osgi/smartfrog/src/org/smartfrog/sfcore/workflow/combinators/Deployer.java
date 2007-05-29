/** (C) Copyright 1998-2006 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.workflow.combinators;

import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.util.Vector;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.parser.Phases;
import java.io.InputStream;
import org.smartfrog.sfcore.parser.SFParser;
import java.util.Iterator;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import java.util.Map;

/**
 *
 */
public class Deployer  extends Run implements Compound {

    /** Reference to #codebase.
     String name for attribute. Value {@value}.*/
    final static String ATTR_HCODEBASE = "newComponentCodebase";

    /** Reference to myDescription (it could be a .sf file for the text itself).
     String name for attribute. Value {@value}.*/
    final static String ATTR_DESCRIPTION = "newComponentDescription";

    /** Reference to extra attributes that will be added to the new component CD
     String name for attribute. Value {@value}.*/
    final static String ATTR_EXTRA_ATTRIBUTES = "newComponentExtraAtributes";

    String newComponentCodebase = null;
    String newComponentDescription = null;
    Vector newComponentExtraAtributes = null;

    /**
     * Constructs Deployer.
     *
     * @throws java.rmi.RemoteException In case of network or RMI failure.
     */
    public Deployer() throws java.rmi.RemoteException {
        super();
    }

    protected void readSFAttributes() throws  RemoteException, SmartFrogResolutionException {
        // It will by default use the same used in Run if available
        super.readSFAttributes();

        newComponentDescription = sfResolve(ATTR_DESCRIPTION,newComponentDescription,true);
        newComponentCodebase = sfResolve(ATTR_HCODEBASE,newComponentCodebase,false);
        newComponentExtraAtributes = sfResolve(ATTR_EXTRA_ATTRIBUTES,newComponentExtraAtributes,false);
    }


    protected ComponentDescription getComponentDescription() throws SmartFrogException {
        Phases phases = null;
        try {
            InputStream descriptionStream = org.smartfrog.SFSystem.getInputStreamForResource(newComponentDescription);
            SFParser parser = new SFParser(SFParser.getLanguageFromUrl(newComponentDescription));
            phases = parser.sfParse(descriptionStream,newComponentCodebase);
            addAttributesToCD(newComponentExtraAtributes, phases);
        } catch (Exception e) {
             sfLog().err("", e);
             throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(e);
        }
        phases = phases.sfResolvePhases();
        return phases.sfAsComponentDescription();
    }

    //Placement done by the compiler
    private void addAttributesToCD(Vector attributes, Phases phases) throws SmartFrogResolutionException,
            SmartFrogRuntimeException {
        if (attributes != null) {
            if (sfLog().isTraceEnabled())  sfLog().trace("Attribute Replacement Started\n");
            for (Iterator e = attributes.iterator(); e.hasNext();) {
                Vector attribute_value = (Vector)e.next();
                String key = (String) attribute_value.elementAt(0);
                Object value = attribute_value.elementAt(1);
                Reference keyRef = Reference.fromString(key.toString());
                if (sfLog().isTraceEnabled())  sfLog().trace("Attribute : KeyRef:" + keyRef.toString() + "  ; value:" + value.toString());
                phases.sfReplaceAttribute(keyRef, value);
            }
        }
    }

    //Placement done by the compiler
    private void addAttributesToCD(Map attributes, Phases phases) throws SmartFrogResolutionException,
            SmartFrogRuntimeException {
        if (attributes != null) {
            sfLog().info("Attribute Replacement Started\n");
            for (Iterator e = attributes.keySet().iterator(); e.hasNext();) {
                Object key = e.next();
                Object value = attributes.get(key);
                Reference keyRef = Reference.fromString(key.toString());
                sfLog().info("Attribute : KeyRef:" + keyRef.toString() + "  ; value:" + value.toString());
                phases.sfReplaceAttribute(keyRef, value);
            }
        }
    }

}
