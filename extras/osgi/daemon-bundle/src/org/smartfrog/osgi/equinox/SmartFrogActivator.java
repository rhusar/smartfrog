package org.smartfrog.osgi.equinox;

import org.smartfrog.osgi.AbstractActivator;
import org.smartfrog.sfcore.processcompound.SubprocessStarter;

public class SmartFrogActivator extends AbstractActivator {
    protected SubprocessStarter getSubprocessStarter() {
        return new EquinoxSubprocessStarterImpl();
    }
}
