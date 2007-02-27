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
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.PhaseNames;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import javax.xml.namespace.QName;
import java.util.Vector;
import java.util.Enumeration;
import java.rmi.RemoteException;

/**
 * This is an extended component description that is used when turning a CDL graph into a smartfrog graph
 * <p/>
 * There is a special bit of deviousness here.
 * Every Node that is in the Smartfrog XML namespace {@link Constants#XMLNS_SMARTFROG}
 * is registered only under its local name, not the
 * full namespace. So by using sf namespaced elements, we can merge a CDL description into a smartfrog one,
 * without contaminating any local namespaced elements.
 * <p/>
 * <p/>
 * created 24-Jan-2006 13:34:37
 */

public final class CdlComponentDescriptionImpl extends SFComponentDescriptionImpl implements CdlComponentDescription, Phases {

    /**
     * node name
     */
    private QName qname;

    private ParseContext parseContext;

    public static final String PHASE_BUILD = "build";

    public CdlComponentDescriptionImpl(Vector types, SFComponentDescription parent, Context cxt, boolean eager) {
        super(types, parent, cxt, eager);
    }

    public CdlComponentDescriptionImpl(QName name, CdlComponentDescription parent) {
        this(name, parent, new ContextImpl(), true);
    }

    /**
     * Create and bind to the parent using the qname
     *
     * @param name
     * @param parent
     * @param cxt
     * @param eager
     */
    public CdlComponentDescriptionImpl(QName name, CdlComponentDescription parent, Context cxt, boolean eager) {
        super(null, (SFComponentDescription) parent, cxt, eager);
        qname = name;
    }

    /**
     * Create and bind to the parent using the qname
     *
     * @throws SmartFrogRuntimeException
     */
    public void registerWithParent() throws SmartFrogException, RemoteException {
        if (sfParent() != null) {
            assert qname != null;
            CdlComponentDescription cdlParent = (CdlComponentDescription) sfParent();
            cdlParent.replace(qname, this);
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
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *          error evaluating phases
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
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *          In case of SmartFrog system error
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
                throw SmartFrogResolutionException.forward(name, thr);
            }

        }

        return actOn;
    }

    protected CdlComponentDescription resolveRootNode() throws SmartFrogResolutionException {
        Reference system = new Reference(Constants.QNAME_SYSTEM_ELEMENT);
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
        return new SmartFrogCompilationException("not implemented " + text);
    }

    /**
     * Helper operation to do a full resolve of a child thing
     *
     * @param child
     * @param mandatory
     * @return the thing at the end of the link, or null for no match
     * @throws SmartFrogResolutionException if there is no match and mandatory==true
     */
    public Object resolve(QName child, boolean mandatory) throws SmartFrogResolutionException {
        Reference r = new Reference(child);
        return sfResolve(r, mandatory);
    }

    /**
     * Like sfReplace but with some special magic related to stuff in the local namespace, which
     * is turned into non-qname stuff.
     *
     * @param child
     * @param value
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *
     * @throws java.rmi.RemoteException
     */
    public void replace(QName child, Object value) throws SmartFrogException, RemoteException {
        Object name = child;
        if (isSpecialNamespace(child)) {
            name = child.getLocalPart();
        }
        sfReplaceAttribute(name, value);
    }

    /**
     * special logic for stuff to be converted down to a string from a qname
     * @param name
     * @return true if this is in a special smartfrog namespace
     */
    public static boolean isSpecialNamespace(QName name) {
        return name.getNamespaceURI().length()==0;
    }
}
