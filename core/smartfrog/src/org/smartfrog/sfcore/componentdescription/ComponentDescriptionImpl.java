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
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.reference.ReferenceResolver;
import org.smartfrog.sfcore.security.SFClassLoader;

/**
 * Defines the context class used by Components. Context implementations
 * need to respect the ordering and copying requirements imposed by
 * Components.
 */
public class ComponentDescriptionImpl implements Serializable, Cloneable,
    ComponentDescription, MessageKeys {

    /**
     * Name of default deployer.
     */
    private final String DEFAULT_DEPLOYER =
            "org.smartfrog.sfcore.processcompound.PrimProcessDeployerImpl";

    /**
     * Name of default implementation of ComponentDescription.
     */
    private final String COMPONENT_DESCRIPTION =
        "org.smartfrog.sfcore.componentdescription.ComponentDescription";

    /** Context of attributes (key value pairs). */
    public Context context;

    /** Parent of this description. */
    public ComponentDescription parent;

    /** Whether this description is eager or lazy. */
    public boolean eager;

    /**
     * Constucts ComponentDescriptionImpl with parent component and context.
     *
     * @param parent parent component
     * @param cxt context for description
     * @param eager eager flag
     */
    public ComponentDescriptionImpl(ComponentDescription parent, Context cxt,
        boolean eager) {
        this.context = cxt;
        this.parent = parent;
        this.eager = eager;
    }

    /**
     * Gets the complete name for this description. This gives a reference from
     * the root component to this description. If the parent does not know
     * about this component, the parents complete name is returned.
     *
     * @return complete name for this component
     */
    public Reference getCompleteName() {
        if (parent == null) {
            return new Reference();
        }

        Reference r = parent.getCompleteName();
        Object name = parent.getContext().keyFor(this);

        if (name != null) {
            r.addElement(ReferencePart.here(name));
        }

        return r;
    }

    /**
     * Gets the context for this description.
     *
     * @return context
     *
     * @see #setContext
     */
    public Context getContext() {
        return context;
    }

    /**
     * Sets the context for this description.
     *
     * @param cxt new context
     *
     * @return old context
     *
     * @see #getContext
     */
    public Context setContext(Context cxt) {
        Context oc = context;
        context = cxt;

        return oc;
    }

    /**
     * Gets the parent for this description.
     *
     * @return component parent description
     *
     * @see #setParent
     */
    public ComponentDescription getParent() {
        return parent;
    }

    /**
     * Sets parent for this component.
     *
     * @param p new parent component
     *
     * @return old parent for description
     *
     * @see #getParent
     */
    public ComponentDescription setParent(ComponentDescription p) {
        ComponentDescription op = parent;
        parent = p;

        return op;
    }

    /**
     * Gets the eager flag for description.
     *
     * @return true if description eager, false if lazy
     *
     * @see #setEager
     */
    public boolean getEager() {
        return eager;
    }

    /**
     * Sets eager flag for description.
     *
     * @param e new eager flag
     *
     * @return old eager flag
     *
     * @see #getEager
     */
    public boolean setEager(boolean e) {
        boolean oe = eager;
        eager = e;

        return oe;
    }

    //
    // ReferenceResolver
    //

    /**
     * Resolves a single id in this component description.
     *
     * @param id key to resolve
     *
     * @return context value for id or null if none
     */
    public Object sfResolveId(Object id) {
        return getContext().get(id);
    }

    /**
     * Resolves to the parent of this description.
     *
     * @return parent or null if no parent
     */
    public ReferenceResolver sfResolveParent() {
        return getParent();
    }

    /**
     * Resolve a given reference. Forwards to indexed resolve with index 0.
     *
     * @param r reference to resolve
     *
     * @return resolved reference
     *
     * @throws SmartFrogResolutionException occurred while resolving
     */
    public Object sfResolve(Reference r)
        throws SmartFrogResolutionException {
        return sfResolve(r, 0);
    }

    /**
     * Resolves a refererence starting at given index.
     *
     * @param r reference to resolve
     * @param index index in reference to start to resolve
     *
     * @return Object refernce
     *
     * @throws SmartFrogResolutionException failure while resolving reference
     */
    public Object sfResolve(Reference r, int index)
        throws SmartFrogResolutionException {
        return r.resolve(this, index);
    }

    /**
     * Creates a deep copy of the compiled component.  Parent, eager flag are
     * the same in the copy. Resolvers are blanked in the copy to avoid
     * resolution confusion. Resolution data object reference is copied if it
     * implements the Copying interface, otherwise the pointer is shared with
     * the copy.
     *
     * @return copy of component
     */
    public Object copy() {
        ComponentDescription res = null;
        res = (ComponentDescription) clone();
        res.setContext((Context) context.copy());
        res.setParent(parent);
        res.setEager(eager);

        for (Enumeration e = context.keys(); e.hasMoreElements();) {
            Object value = res.getContext().get(e.nextElement());

            if (value instanceof ComponentDescription) {
                ((ComponentDescription) value).setParent(res);
            }
        }

        return res;
    }

    /**
     * Gets the cloen of the ComponentDescription.
     * @return cloned object
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cex) {
            // should never happen
        }

        return null;
    }

    //
    // ComponentDeployer
    //

    /**
     * Deploy description. Constructs the real deployer using getDeployer
     * method and forwards to it. If name is set, name is resolved on target,
     * the new target deploy resolved and deployment forwarded to the new
     * target
     *
     * @param name name of contained description to deploy (can be null)
     * @param parent parent for deployed component
     * @param params parameters for description
     *
     * @return Reference to component
     *
     * @throws SmartFrogDeploymentException In case failed to forward deployment
     * or deploy
     */
    public Prim deploy(Reference name, Prim parent, Context params)
        throws SmartFrogDeploymentException {
        try {
            // resolve name to description and forward to deployer there
            if (name != null) {
                Object tmp = sfResolve(name);

                if (!(tmp instanceof ComponentDescription)) {
                    SmartFrogResolutionException.notComponent(name, getCompleteName());
                }

                return ((ComponentDescription) tmp).deploy(null, parent, params);
            }
            return getDeployer().deploy(name, parent, params);
        } catch (SmartFrogException sfex){
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(sfex);
        }
    }

    /**
     * Gets the real deployer for this description target. Looks up
     * sfDeployerClass. If not found. PrimProcessDeployerImpl is used. The
     * constructor used is the one taking a compnent description as an
     * argument
     *
     * @return deployer for target
     *
     * @throws SmartFrogException failed to construct target deployer
     * @see org.smartfrog.sfcore.processcompound.PrimProcessDeployerImpl
     */
    protected ComponentDeployer getDeployer() throws SmartFrogException {
        String className = (String) sfResolveId(SmartFrogCoreKeys.SF_DEPLOYER_CLASS);

        if (className == null) {
            className = DEFAULT_DEPLOYER;
        }

        try {
            Class deplClass = SFClassLoader.forName(className);
            Class[] deplConstArgsTypes = { SFClassLoader.
                    forName(COMPONENT_DESCRIPTION) };
            Constructor deplConst = deplClass.
                                getConstructor(deplConstArgsTypes);
            Object[] deplConstArgs = { this };

            return (ComponentDeployer) deplConst.newInstance(deplConstArgs);
        } catch (NoSuchMethodException nsmetexcp) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_METHOD_NOT_FOUND, className, "getConstructor()"),
                nsmetexcp, null, context);
        } catch (ClassNotFoundException cnfexcp) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_CLASS_NOT_FOUND, className), cnfexcp, null, context);
        } catch (InstantiationException instexcp) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_INSTANTIATION_ERROR, className), instexcp, null, context);
        } catch (IllegalAccessException illaexcp) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_ILLEGAL_ACCESS, className, "newInstance()"), illaexcp,
                null, context);
        } catch (InvocationTargetException intarexcp) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_INVOCATION_TARGET, className), intarexcp,
                null, context);
        }
    }

    /**
     * Returns a string representation of the component. This will give a
     * description of the component which is parseable, and deployable
     * again... Unless someone removed attributes which are essential to
     * startup that is. Large description trees should be written out using
     * writeOn since memory for large strings runs out quick!
     *
     * @return string representation of component
     */
    public String toString() {
        StringWriter sw = new StringWriter();

        try {
            writeOn(sw);
        } catch (IOException ioex) {
            // ignore should not happen
        }

        return sw.toString();
    }

    /**
     * Writes this component description on a writer. Used by toString. Should
     * be used instead of toString to write large descriptions to file, since
     * memory can become a problem given the LONG strings created
     *
     * @param ps writer to write on
     *
     * @throws IOException failure while writing
     */
    public void writeOn(Writer ps) throws IOException {
        writeContextOn(ps, 0, context.keys());
    }

    /**
     * Writes the context on a writer.
     *
     * @param ps writer to write on
     * @param indent level
     * @param keys enumeation over the keys of the context to write out
     *
     * @throws IOException failure while writing
     */
    public void writeContextOn(Writer ps, int indent, Enumeration keys)
        throws IOException {
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = context.get(key);
            tabPad(ps, indent);
            writeKeyOn(ps, indent, key);
            ps.write(' ');
            writeValueOn(ps, indent, value);
            ps.write('\n');
        }
    }

    /**
     * Writes given attribute key on a writer.
     *
     * @param ps writer to write on
     * @param indent indent level
     * @param key key to stringify
     *
     * @throws IOException failure while writing
     */
    protected void writeKeyOn(Writer ps, int indent, Object key)
        throws IOException {
        ps.write(key.toString());
    }

    /**
     * Writes a given value on a writer. Recognizes descriptions, strings and
     * vectors of basic values and turns them into string representation.
     * Default is to turn into string using normal toString() call
     *
     * @param ps writer to write on
     * @param indent indent level
     * @param value value to stringify
     *
     * @throws IOException failure while writing
     */
    protected void writeValueOn(Writer ps, int indent, Object value)
        throws IOException {
        if (value instanceof ComponentDescription) {
            ComponentDescription compVal = (ComponentDescription) value;
            ps.write("extends " + (compVal.getEager() ? "" : "LAZY "));

            if (compVal.getContext().size() > 0) {
                ps.write(" {\n");
                compVal.writeContextOn(ps, indent + 1,
                    compVal.getContext().keys());
                tabPad(ps, indent);
                ps.write('}');
            } else {
                ps.write(';');
            }
        } else {
            writeBasicValueOn(ps, indent, value);
            ps.write(';');
        }
    }

    /**
     * Writes a given value on a writer. Recognizes descriptions, strings and
     * vectors of basic values and turns them into string representation.
     * Default is to turn into string using normal toString() call
     *
     * @param ps writer to write on
     * @param indent indent level
     * @param value value to stringify
     *
     * @throws IOException failure while writing
     */
    protected void writeBasicValueOn(Writer ps, int indent, Object value)
        throws IOException {
        if (value instanceof String) {
            ps.write("\"" + value.toString() + "\"");
        } else if (value instanceof Vector) {
            ps.write('[');

            for (Enumeration e = ((Vector) value).elements();
                    e.hasMoreElements();) {
                writeBasicValueOn(ps, indent, e.nextElement());

                if (e.hasMoreElements()) {
                    ps.write(", ");
                }
            }

            ps.write("]");
        } else if (value instanceof Long) {
            ps.write(value.toString() + 'L');
        } else if (value instanceof Double) {
            ps.write(value.toString() + 'D');
        } else {
            ps.write(value.toString());
        }
    }

    /**
     * Internal method to pad out a writer.
     *
     * @param ps writer to tab to
     * @param amount amount to tab
     *
     * @throws IOException failure while writing
     */
    protected void tabPad(Writer ps, int amount) throws IOException {
        for (int i = 0; i < amount; i++)
            ps.write('\t');
    }

    /**
     * Visit every node in the tree, applying an action to that node. The nodes
     * may be visited top-down or bottom-up
     *
     * @param action the action to apply
     * @param topDown true if top-down, false if bottom-up
     *
     * @throws Exception error during applying an action
     */
    public void visit(CDVisitor action, boolean topDown)
        throws Exception {
        Object name;
        Object value;

        if (topDown) {
            action.actOn(this);
        }

        for (Enumeration e = ((Context) context.clone()).keys();
                e.hasMoreElements();) {
            name = e.nextElement();

            if ((value = context.get(name)) instanceof ComponentDescription) {
                ((ComponentDescription) value).visit(action, topDown);
            }
        }

        if (!topDown) {
            action.actOn(this);
        }
    }
}
