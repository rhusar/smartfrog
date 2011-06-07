package org.smartfrog.services.groovy.install.download

import org.smartfrog.services.groovy.install.task.Helper
import java.rmi.RemoteException
import org.smartfrog.sfcore.common.SmartFrogException
import org.smartfrog.sfcore.prim.PrimImpl
import org.smartfrog.services.groovy.install.Component

class Source extends PrimImpl implements ISource {

    private Helper helper

    public Source() throws RemoteException {
        super()
    }

    @Override
    public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
        super.sfDeploy()
        sfLog().debug("Deployed " + this.getClass().getSimpleName())
    }

    @Override
    public synchronized void sfStart() throws RemoteException, SmartFrogException {
        super.sfStart()
        sfLog().debug("Starting " + this.getClass().getSimpleName())
        helper = new Helper((Component)sfParent())
    }

    @Override
    public boolean retrieve() {
        def source = sfResolve("source").toString()
        def destination = sfParent().sfResolve("directory").toString()
        sfLog().info("Copying $source to $destination")
        helper.copy(source, destination)
        return true;
    }
}