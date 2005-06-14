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
package org.smartfrog.sfcore.languages.cdl.resolving;

import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.dom.ToplevelList;
import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlRecursiveExtendsException;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.logging.Log;

import javax.xml.namespace.QName;

/**
 * Implement "extends" semantics. This could be implemented in the property list
 * itself, but is kept separate to let us play with alternate
 * algorithms/implementations created 10-Jun-2005 16:28:47
 */

public class ExtendsResolver {

    ExtendsContext stack = new ExtendsContext();

    /**
     * a log
     */
    private Log log = ClassLogger.getLog(this);

    /**
     * parsing context
     */
    private ParseContext parseContext;

    /**
     * Extends support has a parse context
     *
     * @param context
     */
    public ExtendsResolver(ParseContext context) {
        assert context != null;
        this.parseContext = context;
    }


    /**
     * Resolve the extends for an entire document
     *
     * @param document
     *
     * @throws CdlResolutionException
     * @return true iff there was a system element needing resolving
     */
    public boolean resolveExtends(CdlDocument document)
            throws CdlResolutionException {
        ToplevelList system = document.getSystem();
        if (system != null) {
            system.resolveChildExtends(document, this);
            return true;
        } else {
            return false;
        }

    }

    /**
     * Resolve the extends for a single node. The algorithm for resolution is
     * defined in the CDL document specification.
     *
     * @param document
     * @param targetName name of the target
     *
     * @return
     *
     * @throws CdlResolutionException
     */
    public ResolveResult resolveExtends(CdlDocument document,
                                        QName targetName)
            throws CdlResolutionException {
        return resolveExtends(document, lookup(targetName));
    }

    /**
     * Resolve the extends for a single node. The algorithm for resolution is
     * defined in the CDL document specification.
     *
     * @param document
     * @param target
     *
     * @return
     *
     * @throws CdlResolutionException
     */
    public ResolveResult resolveExtends(CdlDocument document,
                                        PropertyList target)
            throws CdlResolutionException {
        QName name = target.getName();
        assert name != null;
        stack.enter(name);
        ResolveResult result;
        try {
            result = innerResolve(document, target);
        } finally {
            stack.exit(name);
        }
        return result;
    }

    /**
     * Inner resolve is for child elements; we do not save our name on the stack
     * as we do not need to.
     *
     * @param document
     * @param target
     *
     * @return
     *
     * @throws CdlResolutionException
     */
    private ResolveResult innerResolve(CdlDocument document,
                                       PropertyList target)
            throws CdlResolutionException {
        ResolveResult result;
        ResolveEnum state = ResolveEnum.ResolvedIncomplete;
        //do the work
        QName extending = target.getExtendsName();
        if (extending == null) {
            //easy outcome: nothing to extend
            state = ResolveEnum.ResolvedNoWorkNeeded;
        } else {
            //something to resolve.
            ResolveResult extended;
            log.debug("Resolving " + target.getName() + " extends " + extending);
            extended = resolveExtends(document, extending);
            if (extended.state == ResolveEnum.ResolvedIncomplete) {
                //if there is something that is unfinished at this level,
                //leave off it for now. though this state should be
                //impossible to reach here.
                log.debug("extended state=" + extended.state);
                //propagate it
            } else {
                //we have now resolved our parent.
                //get on with it
                target.merge(extended.getResolvedPropertyList());
            }
            state = propagate(extended.state);
        }
        result = new ResolveResult(state, target);
        return result;
    }

    private void unimplemented() throws CdlResolutionException {
        throw new CdlResolutionException("unimplemented");
    }

    /**
     * lookup a property in our context
     *
     * @param nodeName
     *
     * @return
     */
    private PropertyList lookup(QName nodeName) {
        return parseContext.prototypeResolve(nodeName);
    }

    /**
     * Propagate resolution
     *
     * @param parent
     *
     * @return
     */
    private ResolveEnum propagate(ResolveEnum parent) {
        if (parent == ResolveEnum.ResolvedComplete) {
            return ResolveEnum.ResolvedComplete;
        }
        if (parent == ResolveEnum.ResolvedIncomplete) {
            return ResolveEnum.ResolvedIncomplete;
        }
        if (parent == ResolveEnum.ResolvedNoWorkNeeded) {
            return ResolveEnum.ResolvedComplete;
        }
        return ResolveEnum.ResolvedLazyLinksRemaining;
    }


}
