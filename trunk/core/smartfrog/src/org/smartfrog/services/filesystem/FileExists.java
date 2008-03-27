package org.smartfrog.services.filesystem;

import org.smartfrog.sfcore.workflow.conditional.Condition;

/**

 */
public interface FileExists extends Condition, FileIntf {

    /**
     * {@value}
     */
    String ATTR_MIN_SIZE="minSize";

    /**
     *  Boolean to indicate the file can be a directory: {@value}
     */
    String ATTR_CAN_BE_DIR="canBeDirectory";

    /**
     * Boolean to indicate the file can be a file:{@value}
     */
    String ATTR_CAN_BE_FILE = "canBeFile";


}
