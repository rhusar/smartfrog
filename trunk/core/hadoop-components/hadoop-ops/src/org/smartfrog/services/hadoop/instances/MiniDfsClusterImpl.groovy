package org.smartfrog.services.hadoop.instances

import java.rmi.RemoteException
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster
import org.apache.hadoop.hdfs.server.common.HdfsConstants.StartupOption
import org.smartfrog.services.hadoop.operations.dfs.HdfsStartupOptionFactory
import org.smartfrog.sfcore.common.SmartFrogException
import org.smartfrog.sfcore.common.SmartFrogResolutionException
import org.smartfrog.sfcore.prim.TerminationRecord

import org.smartfrog.services.scripting.groovy.GRef
import org.smartfrog.services.hadoop.operations.conf.ManagedConfiguration

/**
 * This is a groovy class that can bring up a MiniDFS cluster
 */
class MiniDfsClusterImpl extends MiniClusterImpl {

    public static final String ATTR_NAMENODE_PORT = "namenodePort"
    public static final String ATTR_FORMAT = "format"
    public static final String ATTR_MANAGE_NAME_DFS_DIRS = "manageNameDfsDirs"
    public static final String ATTR_MANAGE_DATA_DFS_DIRS = "manageDataDfsDirs"
    public static final String ATTR_STARTUP_OPTION = "startupOption"
    public static final String ATTR_SIMULATED_CAPACITIES = "simulatedCapacities"
    MiniDFSCluster cluster
    String filesystemPath


    @Override
    synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart()
        int nameNodePort = sfResolve(ATTR_NAMENODE_PORT, 0, true)
        int numDataNodes = sfResolve(ATTR_NODE_COUNT, 0, true)
        boolean format = sfResolve(ATTR_FORMAT, true, true)
        boolean manageNameDfsDirs = sfResolve(ATTR_MANAGE_NAME_DFS_DIRS, true, true)
        boolean manageDataDfsDirs = sfResolve(ATTR_MANAGE_DATA_DFS_DIRS, true, true)
        GRef startupRef = new GRef(ATTR_STARTUP_OPTION)
        StartupOption operation = null
        String startupOption = sfResolve(startupRef, "", true)
        if (startupOption) {
            operation = HdfsStartupOptionFactory.createStartupOption(startupOption)
            if (!operation) {
                throw SmartFrogResolutionException.generic(startupRef,
                        sfCompleteNameSafe(),
                        "Unsupported operation \"${startupOption}\"")
            }
        }
        ManagedConfiguration conf = createAndCacheConfig()
        String[] racks = resolveListToArray(ATTR_RACKS)
        String[] hosts = resolveListToArray(ATTR_HOSTS)
        long[] simulatedCapacities = resolveLongVector(ATTR_SIMULATED_CAPACITIES);

        cluster = new MiniDFSCluster(
                nameNodePort,
                conf,
                numDataNodes,
                format,
                manageNameDfsDirs,
                manageDataDfsDirs,
                operation,
                racks,
                hosts,
                simulatedCapacities)

        filesystemPath = "hdfs://localhost:${cluster.getNameNodePort()}/"
        sfLog().info("MiniDFSCluster is up at $filesystemPath")
        sfReplaceAttribute(ATTR_FILESYSTEM_URI, filesystemPath);
        sfReplaceAttribute(FileSystem.FS_DEFAULT_NAME_KEY, filesystemPath);

    }

    @Override
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status)
        cluster?.shutdown()
        cluster = null
    }

    public URI getFilesystemURI() {
        return org.apache.hadoop.fs.FileSystem.getDefaultUri(getClusterConfig())
    }

}
