package org.smartfrog.services.amazon.ec2;

import org.smartfrog.services.farmer.AbstractClusterFarmer;
import org.smartfrog.services.farmer.ClusterFarmer;
import org.smartfrog.services.farmer.ClusterNode;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.io.IOException;

/**
 * This class implements an EC2 cluster farmer.
 * It is still unimplemented
 */
public class Ec2ClusterFarmerImpl extends AbstractClusterFarmer implements ClusterFarmer {

    public Ec2ClusterFarmerImpl() throws RemoteException {
    }

    @Override
    public ClusterNode[] create(String role, int min, int max) throws IOException, SmartFrogException {
        return new ClusterNode[0];
    }

    @Override
    public void delete(String id) throws IOException, SmartFrogException {
    }

    @Override
    public void delete(String[] nodes) throws IOException, SmartFrogException {
    }

    @Override
    public void delete(ClusterNode[] nodes) throws IOException, SmartFrogException {
    }

    @Override
    public int deleteAllInRole(String role) throws IOException, SmartFrogException {
        return 0;
    }

    @Override
    public ClusterNode[] list() throws IOException, SmartFrogException {
        return new ClusterNode[0];
    }

    @Override
    public ClusterNode[] list(String role) throws IOException, SmartFrogException {
        return new ClusterNode[0];
    }

    @Override
    public ClusterNode lookup(String id) throws IOException, SmartFrogException {
        return null;
    }

    @Override
    public ClusterNode lookupByHostname(String hostname) throws IOException, SmartFrogException {
        return null;
    }
}
