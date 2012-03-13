package org.smartfrog.services.www.jaxrs.test.testapp;

import org.smartfrog.services.www.jaxrs.JaxRsApplication;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

public abstract class AbstractJaxRsResource {
    @Context
    public Application application;

    private Log log;

    public Application getApplication() {
        return application;
    }

    public AbstractJaxRsResource() {
    }

    protected JaxRsApplication getJaxRsApplication() {
        return (JaxRsApplication) application;
    }

    protected synchronized Log getLog() {
        if (log == null) {
            JaxRsApplication app = getJaxRsApplication();
            if (app != null) {
                log = app.getLog();
            }
            if (log == null) {
                log = LogFactory.sfGetProcessLog();
            }
        }
        return log;
    }
}