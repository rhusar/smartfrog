package org.smartfrog.osgi.logging;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class LogServiceProxy implements LogService {

    private static class LogServiceFallback implements LogService {

        private void fallback(String message) {
            System.out.println(message);
        }

        private void fallbackThrowable(String message, Throwable t) {
            System.out.println(message);
            t.printStackTrace();
        }

        public void log(int i, String string) {
            fallback(string);
        }

        public void log(int i, String string, Throwable throwable) {
            fallbackThrowable(string, throwable);
        }

        public void log(ServiceReference serviceReference, int i, String string) {
            fallback(string);
        }

        public void log(ServiceReference serviceReference, int i, String string, Throwable throwable) {
            fallbackThrowable(string, throwable);
        }
    }

    private LogService logService = new LogServiceFallback();


    public void error(final String s, final Throwable e) {
        log(LOG_ERROR, s, e);
    }

    public void info(final String message) {
        log(LOG_INFO, message);
    }

    public void debug(final String message) {
        log(LOG_DEBUG, message);
    }

    public void setLog(LogService log) {
        logService = log;
    }

    public void unsetLog(LogService log) {
        logService = new LogServiceFallback();
    }

    public void log(int i, String string) {
        logService.log(i, string);
    }

    public void log(int i, String string, Throwable throwable) {
        logService.log(i, string, throwable);
    }

    public void log(ServiceReference serviceReference, int i, String string) {
        logService.log(serviceReference, i, string);
    }

    public void log(ServiceReference serviceReference, int i, String string, Throwable throwable) {
        logService.log(serviceReference, i, string, throwable);
    }

}
