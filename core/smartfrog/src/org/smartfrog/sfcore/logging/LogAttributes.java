package org.smartfrog.sfcore.logging;

/**
 * A simple interface to modify logging attributes for all subset of loggers
 * when possible.
 *
 */
public interface LogAttributes {


    // ----------- Logging Properties

    /**
     * <p> To change debug logging status </p>
     *
     * @param status new status value
     * @return previous status value
     */
    public boolean setDebug(boolean status);


    /**
     * <p> To change error logging status </p>
     *
     * @param status new status value
     * @return previous status value
     */
    public boolean setError(boolean status);


    /**
     * <p> To change fatal logging status </p>
     *
     * @param status new status value
     * @return previous status value
     */
    public boolean setFatal(boolean status);


    /**
     * <p> To change info logging status </p>
     *
     * @param status new status value
     * @return previous status value
     */
    public boolean setInfo(boolean status);


    /**
     * <p> To change trace logging status </p>
     *
     * @param status new status value
     * @return previous status value
     */
    public boolean setTrace(boolean status);

    /**
     * <p> To change warn logging status </p>
     *
     * @param status new status value
     * @return previous status value
     */
    public boolean setWarn(boolean status);


    /**
     * <p> To change a property in all registered loggers </p>
     *
     * @param status new status value
     * @return if it was sucessfull applaying this attribute to any of the registered loggers
     */
    public boolean setAttribute (Object name, Object value);


}
