package org.smartfrog.sfcore.logging;

//import org.smartfrog.sfcore.common.SmartFrogException;


/**
 * A simple logging interface abstracting logging APIs based in Apache Jakarta
 * logging.
 *
 */
public interface LogLevel {

    /**
     * <p> Set logging level. </p>
     *
     * @param currentLogLevel new logging level
     */
    public void setLevel(int currentLogLevel);

    /**
     * <p> Get logging level. </p>
     */
    public int getLevel();


}


