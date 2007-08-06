package org.smartfrog.osgi.logging;

import org.osgi.service.log.LogService;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogLevel;

/**
 * A SmartFrog {@link Log} implementation that logs to the OSGi Log Service.
 * The Log Service only has 4 log levels, whereas the {@link Log} interface has 5.
 * The following level conversion rules are used:
 * <table>
 * <th>
 *  <td>SmartFrog log level</td>
 *  <td>OSGi log level</td>
 * </th>
 * <tr>
 *  <td><code>TRACE</code></td>
 *  <td><code>DEBUG</code></td>
 * </tr>
 * <tr>
 *  <td><code>DEBUG</code></td>
 *  <td><code>DEBUG</code></td>
 * </tr>
 * <tr>
 *  <td><code>INFO</code></td>
 *  <td><code>INFO</code></td>
 * </tr>
 * <tr>
 *  <td><code>WARN</code></td>
 *  <td><code>WARN</code></td>
 * </tr>
 * <tr>
 *  <td><code>ERROR</code></td>
 *  <td><code>ERROR</code></td>
 * </tr>
 * <tr>
 *  <td><code>FATAL</code></td>
 *  <td><code>ERROR</code></td>
 * </tr>
 * </table>
 * @noinspection ClassWithTooManyMethods
 */
public class LogToOSGiLogServiceImpl implements Log, LogLevel {

    private int logLevel;
    private String prefix;
    private LogService osgiLog;


    /**
     * Creates a new logger to the OSGi log service.
     * Called through reflection by {@link org.smartfrog.sfcore.logging.LogImpl}, so needs to have these parameters.
     * @param name Name of the logger, used as prefix in log messages.
     * @param configuration Unused (needed because of reflective access).
     * @param initialLevel Initial logging level.
     * @throws SmartFrogResolutionException
     */
    public LogToOSGiLogServiceImpl(String name, ComponentDescription configuration, Integer initialLevel) throws SmartFrogResolutionException {
        prefix = "[" + name + "] ";

        logLevel = initialLevel.intValue();
        osgiLog = LogServiceProxy.getInstance();
    }

    public void debug(Object message) {
        osgiLog.log(LogService.LOG_DEBUG, message.toString());
    }

    public void debug(Object message, Throwable t) {
        osgiLog.log(LogService.LOG_DEBUG, prefix + message.toString());
    }

    public void error(Object message) {
        osgiLog.log(LogService.LOG_ERROR, prefix + message.toString());
    }

    public void error(Object message, Throwable t) {
        osgiLog.log(LogService.LOG_ERROR, prefix + message.toString(), t);
    }

    public void fatal(Object message) {
        osgiLog.log(LogService.LOG_ERROR, prefix + message.toString());
    }

    public void fatal(Object message, Throwable t) {
        osgiLog.log(LogService.LOG_ERROR, prefix + message.toString(), t);
    }

    public void info(Object message) {
        osgiLog.log(LogService.LOG_INFO, prefix + message.toString());
    }

    public void info(Object message, Throwable t) {
        osgiLog.log(LogService.LOG_INFO, prefix + message.toString(), t);
    }

    public void trace(Object message) {
        osgiLog.log(LogService.LOG_DEBUG, prefix + message.toString());
    }

    public void trace(Object message, Throwable t) {
        osgiLog.log(LogService.LOG_DEBUG, prefix + message.toString(), t);
    }

    public void warn(Object message) {
        osgiLog.log(LogService.LOG_WARNING, prefix + message.toString());
    }

    public void warn(Object message, Throwable t) {
        osgiLog.log(LogService.LOG_WARNING, prefix + message.toString(), t);
    }

    public boolean isDebugEnabled() {
        return isLevelEnabled(LOG_LEVEL_DEBUG);
    }

    public boolean isErrorEnabled() {
        return isLevelEnabled(LOG_LEVEL_ERROR);
    }

    public boolean isFatalEnabled() {
        return isLevelEnabled(LOG_LEVEL_FATAL);
    }

    public boolean isInfoEnabled() {
        return isLevelEnabled(LOG_LEVEL_INFO);
    }

    public boolean isTraceEnabled() {
        return isLevelEnabled(LOG_LEVEL_TRACE);
    }

    public boolean isWarnEnabled() {
        return isLevelEnabled(LOG_LEVEL_WARN);
    }

    public int getLevel() {
        return logLevel;
    }

    public boolean isLevelEnabled(int logLevel) {
        return logLevel >= this.logLevel;
    }

    public void setLevel(int currentLogLevel) {
        logLevel = currentLogLevel;
    }
}
