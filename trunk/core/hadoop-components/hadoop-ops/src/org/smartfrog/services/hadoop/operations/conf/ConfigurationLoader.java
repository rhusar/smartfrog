/**
 *
 */

package org.smartfrog.services.hadoop.operations.conf;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.hadoop.operations.core.ClusterBound;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.RemoteToString;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ListUtils;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

/**
 * Class to perform configuration load operations 
 */
public final class ConfigurationLoader {


    private ConfigurationLoader() {
    }

    /**
     * Add the default and site configurations; this is used to push out
     * all HDFS and MapReduce options in by default.
     * It is not an error to attempt to re-add already loaded configurations, as this is ignored.
     * 
     */
    public static void loadExtendedConfigurations() {
        Configuration.addDefaultResource("hdfs-default.xml");
        Configuration.addDefaultResource("hdfs-site.xml");
        Configuration.addDefaultResource("mapred-default.xml");
        Configuration.addDefaultResource("mapred-site.xml");
    }

    /** error string : {@value} */
    public static final String ERROR_NO_CLUSTER_AND_XML =
            "Cannot extend an existing cluster and import XML resources or files";

    /**
     * Load in a configuration from any Prim instance.
     * @param source the configuration source
     * @return the configuration
     * @throws RemoteException network problems
     * @throws SmartFrogException any parsing problems
     */
    public static ManagedConfiguration loadConfiguration(final PrimImpl source)
            throws RemoteException, SmartFrogException {
        boolean debug = source.sfLog().isDebugEnabled();
        //should default values be loaded
        boolean loadDefaults = source.sfResolve(HadoopConfiguration.ATTR_LOAD_DEFAULTS, true, false);
        if (debug) {
            source.sfLog().debug("Load default resources=" + loadDefaults);
        }
        List<String> resources = ListUtils
                .resolveStringList(source, new Reference(HadoopConfiguration.ATTR_RESOURCES), false);
        if (resources == null) {
            resources = new Vector<String>(0);
        }
        Vector<String> files = FileSystem
                .resolveFileList(source, new Reference(HadoopConfiguration.ATTR_FILES), null, false, null);
        ManagedConfiguration conf;
        boolean clusterRequired = source.sfResolve(ClusterBound.ATTR_CLUSTER_REQUIRED, false, false);
        Prim cluster = source.sfResolve(ClusterBound.ATTR_CLUSTER, (Prim) null, clusterRequired);
        if (cluster != null) {
            if (debug) {
                source.sfLog().debug("Loading from cluster reference "
                        + ((RemoteToString) cluster).sfRemoteToString());
            }
            //inheriting a cluster
            if (!resources.isEmpty() || !files.isEmpty()) {
                throw new SmartFrogResolutionException(ERROR_NO_CLUSTER_AND_XML);
            }
            conf = ManagedConfiguration.createConfiguration(source, true, false, loadDefaults);
            conf.copyPropertiesToPrim(source);

        } else {
            //no cluster reference, so create an empty unmanaged configuration and build it up
            //then copy its (name,value) pairs into a new ManagedConfiguration that is
            //bound to us
            Configuration baseConf = new Configuration(loadDefaults);

            //run through all the resources
            for (String resource : resources) {
                loadXmlResource(source, baseConf, resource);
            }

            //run through the filenames
            for (String filename : files) {
                loadXmlFile(source, baseConf, filename);
            }

            //this now creates a baseConf which is full of all our values.
            //the next step is to override with any in-scope attributes.

            conf = new ManagedConfiguration(source);
            conf.copyProperties(source, baseConf);
        }

        //dump it to the log at debug level or if the dump attribute is true, in which
        //case it comes out at INFO level
        boolean toDump = source.sfResolve(HadoopConfiguration.ATTR_DUMP, true, false);
        boolean debugEnabled = source.sfLog().isDebugEnabled();
        if (toDump || debugEnabled) {
            String dump = conf.dump();
            if (toDump) {
                source.sfLog().info(dump);
            }
            if (debugEnabled) {
                source.sfLog().debug(dump);
            }
        }
        conf.validateListedAttributes(source, new Reference(HadoopConfiguration.ATTR_REQUIRED));
        return conf;
    }

    /**
     * Load an XML resource in
     *
     * @param target
     * @param baseConf base configuration
     * @param resource resource name
     * @throws SmartFrogException on any resource load failure
     * @throws RemoteException network problems
     */
    public static void loadXmlResource(final PrimImpl target,
                                       Configuration baseConf,
                                       String resource)
            throws SmartFrogException, RemoteException {
        if (target.sfLog().isDebugEnabled()) {
            target.sfLog().debug("Adding resource " + resource);
        }
        baseConf.addResource(resource);
    }

    /**
     * Load an XML file
     * @param target
     * @param baseConf base configuration
     * @param file file to load
     * @throws SmartFrogException on any resource load failure
     * @throws RemoteException network problems
     */
    public static void loadXmlFile(final PrimImpl target, Configuration baseConf, String file)
            throws SmartFrogException, RemoteException {
        if (target.sfLog().isDebugEnabled()) {
            target.sfLog().debug("Adding file" + file);
        }
        Path path = new Path(file);
        baseConf.addResource(path);
    }
}
