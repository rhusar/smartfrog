package org.smartfrog.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Dictionary;

/** @noinspection ClassWithTooManyMethods*/
class BundleContextWrapper implements BundleContext, Serializable {
    /**
     * Will be set to null on deserialization. So all calls will throw NPEs,
     * but nobody should be calling. So if they get NPEs they deserve it.
     *
     * This should really not be transmitted by the process compound, but there is
     * no way AFAIK to mark an attribute as "transient" in the SF language. 
     */
    private transient BundleContext real;

    public BundleContextWrapper(BundleContext real) {
        this.real = real;
    }

    //
    // Delegated methods
    //

    public boolean ungetService(ServiceReference serviceReference) {
        return real.ungetService(serviceReference);
    }

    public void removeServiceListener(ServiceListener serviceListener) {
        real.removeServiceListener(serviceListener);
    }

    public void removeFrameworkListener(FrameworkListener frameworkListener) {
        real.removeFrameworkListener(frameworkListener);
    }

    public void removeBundleListener(BundleListener bundleListener) {
        real.removeBundleListener(bundleListener);
    }

    public ServiceRegistration registerService(String[] strings, Object object, Dictionary dictionary) {
        return real.registerService(strings, object, dictionary);
    }

    public ServiceRegistration registerService(String string, Object object, Dictionary dictionary) {
        return real.registerService(string, object, dictionary);
    }

    public Bundle installBundle(String string, InputStream inputStream) throws BundleException {
        return real.installBundle(string, inputStream);
    }

    public Bundle installBundle(String string) throws BundleException {
        return real.installBundle(string);
    }

    public ServiceReference[] getServiceReferences(String string, String filter) throws InvalidSyntaxException {
        return real.getServiceReferences(string,  filter);
    }

    public ServiceReference getServiceReference(String string) {
        return real.getServiceReference(string);
    }

    public Object getService(ServiceReference serviceReference) {
        return real.getService(serviceReference);
    }

    public String getProperty(String string) {
        return real.getProperty(string);
    }

    public File getDataFile(String string) {
        return real.getDataFile(string);
    }

    public Bundle[] getBundles() {
        return real.getBundles();
    }

    public Bundle getBundle(long l) {
        return real.getBundle(l);
    }

    public Bundle getBundle() {
        return real.getBundle();
    }

    public ServiceReference[] getAllServiceReferences(String string, String string1) throws InvalidSyntaxException {
        return real.getAllServiceReferences(string, string1);
    }

    public Filter createFilter(String string) throws InvalidSyntaxException {
        return real.createFilter(string);
    }

    public void addServiceListener(ServiceListener serviceListener, String string) throws InvalidSyntaxException {
        real.addServiceListener(serviceListener, string);
    }

    public void addServiceListener(ServiceListener serviceListener) {
        real.addServiceListener(serviceListener);
    }

    public void addFrameworkListener(FrameworkListener frameworkListener) {
        real.addFrameworkListener(frameworkListener);
    }

    public void addBundleListener(BundleListener bundleListener) {
        real.addBundleListener(bundleListener);
    }
}
