/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.componentdescription;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.Copying;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferenceResolver;
import org.smartfrog.sfcore.reference.ReferenceResolverHelper;

import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

/**
 * Defines the context interface used by Components. Context implementations
 * need to respect the ordering and copying requirements imposed by
 * Components.
 * @see Copying
 */
public interface ComponentDescription extends Copying, ReferenceResolver, ReferenceResolverHelper {

    /**
     * Add an attribute to the component description context. Values should be
     * marshallable types if they are to be referenced remotely at run-time.
     * If an attribute with this name already exists it is <em>not</em>
     * replaced.
     *
     * @param name name of attribute
     * @param value object to be added in context
     *
     * @return value if successfull, null otherwise
     *
     * @throws SmartFrogRuntimeException when name or value are null
     */
    public Object sfAddAttribute(Object name, Object value)throws SmartFrogRuntimeException;

    /**
     * Remove named attribute from component description context. Non present attribute
     * names are ignored.
     *
     * @param name name of attribute to be removed
     *
     * @return the removed value if successfull, null otherwise
     *
     * @throws SmartFrogRuntimeException when name is null
     */
    public Object sfRemoveAttribute(Object name) throws SmartFrogRuntimeException;


    /**
     * Returns the attribute key for a given value.
     *
     * @param value value to look up the key for
     *
     * @return key for given value or null if not found
     *
     */
    public Object sfAttributeKeyFor(Object value);


    /**
     * Replace named attribute in component description context. If attribute is not
     * present it is added to the context.
     *
     * @param name of attribute to replace
     * @param value attribute value to replace or add
     *
     * @return the old value if present, null otherwise
     *
     * @throws SmartFrogRuntimeException when name or value are null
     */
    public Object sfReplaceAttribute(Object name, Object value)
        throws SmartFrogRuntimeException;

    /**
     * Get complete name for this description. This is a reference all the way
     * down from the root of the containment tree.
     *
     * @return reference for complete name of description
     */
    public Reference sfCompleteName();

    /**
     * Gets the context for this description.
     *
     * @return context
     *
     * @see #setContext
     */
    public Context getContext();

    /**
     * Sets the context for this description.
     *
     * @param cxt new context
     *
     * @return old context
     *
     * @see #getContext
     */
    public Context setContext(Context cxt);

    /**
     * Gets the parent for this description.
     *
     * @return component parent description
     *
     * @see #setParent
     */
    public ComponentDescription getParent();

    /**
     * Sets parent for this component.
     *
     * @param parent new parent component
     *
     * @return old parent for description
     *
     * @see #getParent
     */
    public ComponentDescription setParent(ComponentDescription parent);

    /**
     * Gets the eager flag for description.
     *
     * @return true if description eager, false if lazy
     *
     * @see #setEager
     */
    public boolean getEager();

    /**
     * Sets eager flag for description.
     *
     * @param eager new eager flag
     *
     * @return old eager flag
     *
     * @see #getEager
     */
    public boolean setEager(boolean eager);

    /**
     * Returns stringified version of description.
     * Overrides Object.toString.
     *
     * @return string of description
     */
    public String toString();

    /**
     * Write the component description on a writer.
     *
     * @param w writer to write on
     *
     * @exception IOException failure while writing
     */
    public void writeOn(Writer w) throws IOException;

    /**
     * Writes description indented at depth on writer.
     *
     * @param w print writer to use
     * @param depth indent level
     * @param keys enumeation over the keys of the context to write out
     *
     * @exception IOException failure while writing
     */
    public void writeContextOn(Writer w, int depth, Enumeration keys)
        throws IOException;

    /**
     * Visit every node in the tree, applying an action to that node. The nodes
     * may be visited top-down or bottom-up
     *
     * @param action the action to apply
     * @param topDown true if top-down, false if bottom-up
     *
     * @exception Exception error during applying an action
     */
    public void visit(CDVisitor action, boolean topDown)
        throws Exception;
}
