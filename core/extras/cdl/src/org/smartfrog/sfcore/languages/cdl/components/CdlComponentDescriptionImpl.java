/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.cdl.components;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.languages.sf.Phase;
import org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException;
import org.smartfrog.sfcore.languages.cdl.components.CdlComponentDescription;
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.PhaseNames;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import javax.xml.namespace.QName;
import java.util.Vector;
import java.util.Enumeration;

/**
 * This is an extended component description that is used when turning a CDL graph into a smartfrog graph
 *
 * There is a special bit of deviousness here.
 * Every Node that is in the Smartfrog XML namespace {@link Constants#SMARTFROG_NAMESPACE}
 * is registered only under its local name, not the
 * full namespace. So by using sf namespaced elements, we can merge a CDL description into a smartfrog one,
 * without contaminating any local namespaced elements.
 *
 *
 * created 24-Jan-2006 13:34:37
 */

public class CdlComponentDescriptionImpl extends SFComponentDescriptionImpl implements CdlComponentDescription, Phases {

    /** node name */
    private QName qname;

    private ParseContext parseContext;

    public static final String PHASE_BUILD="build";


    public CdlComponentDescriptionImpl(Reference type, SFComponentDescription parent, Context cxt, boolean eager) {
        super(type, parent, cxt, eager);
    }

    public CdlComponentDescriptionImpl(QName name, SFComponentDescription parent) {
        this(name,parent, new ContextImpl(),true);
    }

    /**
     * Create and bind to the parent using the qname
     * @param name
     * @param parent
     * @param cxt
     * @param eager
     */
    public CdlComponentDescriptionImpl(QName name, SFComponentDescription parent, Context cxt, boolean eager) {
        super(null,(SFComponentDescription) parent, cxt, eager);
        qname=name;
    }

    /**
     *
     * Create and bind to the parent using the qname
     * @throws SmartFrogRuntimeException
     */
    public void registerWithParent() throws SmartFrogRuntimeException {
        if(sfParent()!=null) {
            assert qname!=null;
            Object name=qname;
            if(Constants.SMARTFROG_NAMESPACE.equals(qname.getNamespaceURI())) {

                name=qname.getLocalPart();
            }
            sfParent().sfReplaceAttribute(qname,this);
        }
    }

    public QName getQName() {
        return qname;
    }

    public void setQName(QName qname) {
        this.qname = qname;
    }


    /**
     * Returns the clone.
     *
     * @return an Object clone
     */
    public Object clone() {
        return super.clone();
    }

    /**
     * Public method to get the set of phases defined in the component. This
     * will either be the phaseList attribute, or the standard set defined as
     * though the phaseList attribute had been defined phaseList ["type",
     * "place", "sfConfig", "link", "function"]; The attribute is removed to
     * tidy the definition, but the result is cached for later use
     *
     * @return Vector of Phases
     */
    public Vector sfGetPhases() {
        if (phases == null) {
            phases = new Vector();
            phases.add(PHASE_BUILD);
//            phases.add(PhaseNames.TYPE);
//            phases.add(PhaseNames.PLACE);
//            phases.add(PhaseNames.SFCONFIG);
//            phases.add(PhaseNames.LINK);
//            phases.add(PhaseNames.FUNCTION);
//            phases.add(PhaseNames.PHASE_LIST);
        }
        return phases;
    }

    /**
     * Evaluate all the phases required of the description implementing the
     * interface. The list of phases is defined as a default for the language
     * used, or defined somehow as an attribute.
     *
     * @return An instance of Phases that is the result of applying all the
     *         defined phases
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *          error evaluating phases
     */
    public Phases sfResolvePhases() throws SmartFrogException {
        return super.sfResolvePhases();
    }

    /**
     * Evaluate the phase given in the parameter.
     *
     * @param phase the phase to apply
     * @return An instance of Phases that is the result of applying the phase.
     * @throws org.smartfrog.sfcore.common.SmartFrogException error evaluating phases
     */
    public Phases sfResolvePhase(String phase)
            throws SmartFrogException {
        return super.sfResolvePhase(phase);
    }

    /**
     * Evaluate the phases given in the parameter.
     *
     * @param phases a vector of strings defining the names of the
     *               phases
     * @return the resultant Phases object, ready for the
     *         next phase action or convertion into the core ComponentDescription
     * @throws org.smartfrog.sfcore.common.SmartFrogException In case of SmartFrog system error
     */
    public Phases sfResolvePhases(Vector phases)
            throws SmartFrogException {
        SFComponentDescription actOn = this;

        for (Enumeration e = phases.elements(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            try {
                if (name.equals(PhaseNames.TYPE)) {
                    throw notImplemented(PhaseNames.TYPE);
//                    actOn.typeResolve();
                } else if (name.equals(PhaseNames.PLACE)) {
                    throw notImplemented(PhaseNames.PLACE);
                } else if (name.equals(PhaseNames.SFCONFIG)) {
                    actOn = resolveRootNode();
                } else if (name.equals(PhaseNames.LINK)) {
                    throw notImplemented(PhaseNames.LINK);
//                    actOn.linkResolve();
                } else if (name.equals(PhaseNames.PRINT)) {
                    org.smartfrog.SFSystem.sfLog().out(actOn.toString());
                } else {
                    //anything else must be a classname
                    actOn.visit(new Phase(name), false);
                }
            } catch (Throwable thr) {
                throw SmartFrogCompileResolutionException.forward(thr, name);
            }

        }

        return actOn;
    }

    protected CdlComponentDescription resolveRootNode() throws SmartFrogResolutionException {
        Reference system =new Reference(Constants.QNAME_SYSTEM_ELEMENT);
        return (CdlComponentDescription) sfResolve(system);
    }

    /**
     * Convert the Phases (resulting from applying the phases) to a
     * ComponentDescritpion ready for the SmartFrog deployment engine.
     *
     * @return the convertion to a component description
     * @throws org.smartfrog.sfcore.common.SmartFrogCompilationException
     *          error converting phases to a
     *          componentdescription
     */
    public ComponentDescription sfAsComponentDescription() throws SmartFrogCompilationException {
        return this;
    }

    private SmartFrogCompilationException notImplemented(String text) {
        return new SmartFrogCompilationException("not implemented "+text);
    }


}
