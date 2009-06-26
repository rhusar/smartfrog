package org.smartfrog.services.longhaul.server;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * This application defines the rest server
 */
public class LonghaulRestServer extends Application {

    /** {@inheritDoc */
    @Override
    public Set<Class<?>> getClasses() {
        return null;
        /*
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(Applications.class);
        return classes;*/
    }


    /** {@inheritDoc */
    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<Object>();
        singletons.add(new Applications());
        return singletons;
    }
}
