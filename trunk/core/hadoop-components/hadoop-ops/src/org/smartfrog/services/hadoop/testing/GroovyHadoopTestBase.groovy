package org.smartfrog.services.hadoop.testing

import org.smartfrog.services.hadoop.instances.LocalMRCluster
import org.apache.hadoop.mapred.JobConf
import org.smartfrog.services.hadoop.instances.LocalDFSCluster
import org.smartfrog.services.hadoop.operations.conf.ConfUtils
import org.apache.hadoop.hdfs.server.common.HdfsConstants.StartupOption

/**
 * This is a groovy test base for Hadoop MR jobs
 */
abstract class GroovyHadoopTestBase extends GroovyTestCase implements Closeable {

    LocalMRCluster mrCluster;
    LocalDFSCluster dfsCluster;

    void createMrCluster(int nodes, String fsURI, JobConf conf) {
        mrCluster = LocalMRCluster.createInstance(nodes, fsURI, 1, null, conf)
    }
    
    void createDfsCluster(int nodes, Properties properties) {
        JobConf conf = createClusterJobConf()
        ConfUtils.copyProperties(conf, properties);
        dfsCluster = LocalDFSCluster.createInstance(0, conf,
                nodes, false, true, true, StartupOption.FORMAT, null, null, null);
        
    }

    JobConf createClusterJobConf() {
        return new JobConf();
    }
    
    
    
    @Override
    protected void tearDown() {
        close()
        super.tearDown()
    }

    @Override
    void close() {
        mrCluster?.close()
        mrCluster = null
        dfsCluster?.close()
        dfsCluster=null
    }


}
