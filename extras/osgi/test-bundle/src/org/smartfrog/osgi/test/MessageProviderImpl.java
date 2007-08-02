package org.smartfrog.osgi.test;

import org.smartfrog.sfcore.prim.PrimImpl;

public class MessageProviderImpl extends PrimImpl implements MessageProvider {
    public Object getMessage() {
        return new TriggersRemoteClassLoading();
    }

    public String toString() {
        return getMessage().toString();
    }
}
