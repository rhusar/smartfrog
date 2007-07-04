package org.smartfrog.osgi.logging;

import org.osgi.service.log.LogService;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogLevel;

/** @noinspection ClassWithTooManyMethods*/
public class LogToOSGiLogServiceImpl implements Log, LogLevel {

    private int logLevel;
    private String prefix;
    private LogService osgiLog;


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
