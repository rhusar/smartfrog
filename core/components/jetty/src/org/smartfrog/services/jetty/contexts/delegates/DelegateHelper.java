/**
 *
 */

package org.smartfrog.services.jetty.contexts.delegates;

import org.mortbay.jetty.handler.ContextHandler;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

import javax.servlet.ServletContext;

public class DelegateHelper {
    /**
     * Name of the servlet context attribute that identifies the owning SF component: {@value}
     */
    public static final String ORG_SMARTFROG_SERVICES_JETTY_OWNER = "org.smartfrog.services.jetty.owner";

    public static void setOwnerAttribute(final ContextHandler.SContext scontext, final Prim prim) {
        scontext.setAttribute(ORG_SMARTFROG_SERVICES_JETTY_OWNER, prim);
    }

    public static Prim retrieveOwner(final ServletContext ctx) throws SmartFrogException {
        Object attribute = ctx.getAttribute(ORG_SMARTFROG_SERVICES_JETTY_OWNER);
        if (attribute == null) {
            throw new SmartFrogException("No context attribute "
                    + ORG_SMARTFROG_SERVICES_JETTY_OWNER);
        }
        if (!(attribute instanceof Prim)) {
            throw new SmartFrogException("Not a prim: " +attribute.getClass() + " : " +attribute); 
        }
        return (Prim) attribute;
    }
}
