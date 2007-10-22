package org.smartfrog.services.deployapi.components;

import java.rmi.Remote;

/**

 */
public interface DeploymentServer extends Remote {

    String ATTR_FILESTORE_DIR = "filestoreDirectory";

    String ATTR_PORT = "port";

    String ATTR_HOSTNAME = "hostname";

    String ATTR_PROTOCOL = "protocol";

    String ATTR_LOCATION = "location";

    String ATTR_CONTEXTPATH = "contextPath";

    String ATTR_SERVICESPATH = "servicesPath";

    String ATTR_SYSTEM_PATH = "systemPath";

    String ATTR_SUBSCRIPTIONS_PATH = "subscriptionsPath";

}
