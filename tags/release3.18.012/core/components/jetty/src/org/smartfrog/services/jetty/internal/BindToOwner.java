package org.smartfrog.services.jetty.internal;

import org.smartfrog.sfcore.prim.Prim;

/**
 * This binds something deployed under Jetty to the defining Prim, the owner.
 *
 *
 */


public interface BindToOwner {

    /**
     * bind a deployed Jetty component to an owner
     * @param owner  owning Prim
     * @throws Exception on any binding problems
     */
    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    public void bindToOwner(Prim owner) throws Exception;
}
