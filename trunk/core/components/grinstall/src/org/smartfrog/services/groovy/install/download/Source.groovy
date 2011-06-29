package org.smartfrog.services.groovy.install.download

import java.rmi.RemoteException
import org.smartfrog.services.groovy.install.Component
import org.smartfrog.services.groovy.install.task.GroovyComponentHelper
import org.smartfrog.sfcore.common.SmartFrogException
import org.smartfrog.sfcore.prim.PrimImpl

class Source extends PrimImpl implements ISource {

    private GroovyComponentHelper helper

    public Source() throws RemoteException {
        super()
    }

    @Override
    public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
        super.sfDeploy()
        sfLog().debug("Deployed " + this.class.simpleName)
    }

    @Override
    public synchronized void sfStart() throws RemoteException, SmartFrogException {
        super.sfStart()
        sfLog().debug("Starting " + this.class.simpleName)
        helper = new GroovyComponentHelper((Component) sfParent())
    }

    @Override
    public boolean retrieve() {
        def source = sfResolve(ATTR_SOURCE).toString()
        def destination = sfParent().sfResolve(ATTR_DEST_DIR).toString()
        sfLog().info("Copying $source to $destination")
        helper.copy(source, destination)
        return true;
    }
}