package org.smartfrog.services.www.jaxrs.test.testapp;

import org.smartfrog.services.www.jaxrs.JaxRsApplication;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

public abstract class AbstractJaxRsResource {
    @Context
    Application application;

    public Application getApplication() {
        return application;
    }

    public AbstractJaxRsResource() {
    }

    protected JaxRsApplication getJaxRsApplication() {
        return (JaxRsApplication) application;
    }
}