package org.smartfrog.sfcore.logging;

/**
 * A simple logging interface abstracting logging APIs based in Apache Jakarta
 * logging. It is not remoted.
 *
 */
public interface Log {


    // ----------------------------------------------------- Logging Properties

    /**
     * <p> Is debug logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than debug. </p>
     *
     * @return boolean true if debug logging currently enabled
     */
    boolean isDebugEnabled();


    /**
     * <p> Is error logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than error. </p>
     *
     * @return boolean true if error logging currently enabled
     */
    boolean isErrorEnabled();


    /**
     * <p> Is fatal logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than fatal. </p>
     * @return boolean true if fatal logging is currently enabled
     */
    boolean isFatalEnabled();


    /**
     * <p> Is info logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than info. </p>
     *
     * @return boolean true if info logging currently enabled
     */
    boolean isInfoEnabled();


    /**
     * <p> Is trace logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than trace. </p>
     *
     * @return boolean true if trace logging currently enabled
     */
    boolean isTraceEnabled();


    /**
     * <p> Is warning logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than warn. </p>
     *
     * @return boolean true if warn logging currently enabled
     */
    boolean isWarnEnabled();


    // -------------------------------------------------------- Logging Methods

    /**
     * <p> Log a message with trace log level. </p>
     *
     * @param message log this message
     */
    void trace(Object message);


    /**
     * <p> Log an error with trace log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    void trace(Object message, Throwable t);


    /**
     * <p> Log a message with debug log level. </p>
     *
     * @param message log this message
     */
    void debug(Object message);


    /**
     * <p> Log an error with debug log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    void debug(Object message, Throwable t);


    /**
     * <p> Log a message with info log level. </p>
     *
     * @param message log this message
     */
    void info(Object message);


    /**
     * <p> Log an error with info log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    void info(Object message, Throwable t);


    /**
     * <p> Log a message with warn log level. </p>
     *
     * @param message log this message
     */
    void warn(Object message);


    /**
     * <p> Log an error with warn log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    void warn(Object message, Throwable t);


    /**
     * <p> Log a message with error log level. </p>
     *
     * @param message log this message
     */
    void error(Object message);


    /**
     * <p> Log an error with error log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    void error(Object message, Throwable t);


    /**
     * <p> Log a message with fatal log level. </p>
     *
     * @param message log this message
     */
    void fatal(Object message);


    /**
     * <p> Log an error with fatal log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    void fatal(Object message, Throwable t);


}
