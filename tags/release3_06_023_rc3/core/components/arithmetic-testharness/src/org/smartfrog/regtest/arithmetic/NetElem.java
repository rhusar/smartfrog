package org.smartfrog.regtest.arithmetic;

import java.rmi.*;

public interface NetElem {
    String ATTR_OUTPUTS = "outputs";
    String ATTR_MAX_INVOCATIONS = "maxInvocations";

    public void doit(String who, int value);
}
