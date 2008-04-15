/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */


package org.smartfrog.services.hadoop.conf;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.smartfrog.services.hadoop.core.SFHadoopRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

/**
 * This is our extended configuration, which takes a Prim component as a source
 * of information
 */
public class ManagedConfiguration extends JobConf implements PrimSource {

    private Prim source;

    /**
     * A new configuration.
     *
     * @param source source of config information
     */
    public ManagedConfiguration(Prim source) {
        this.source = source;
    }

    /**
     * A new configuration with the same settings cloned from another.
     *
     * @param source source of config information
     * @param other  the configuration from which to clone settings.
     */
    public ManagedConfiguration(Configuration other, Prim source) {
        super(other);
        this.source = source;
    }


    /**
     * A new configuration with the same settings cloned from another.
     *
     * @param conf the configuration from which to clone settings.
     */
    public ManagedConfiguration(Configuration conf) {
        super(conf);
        if (conf instanceof PrimSource) {
            PrimSource primsource = (PrimSource) conf;
            source = primsource.getSource();
        } else {
            throw new SFHadoopRuntimeException(
                    "No Prim source for the configuration");
        }
    }

    /**
     * Return the source
     *
     * @return the source component
     */
    public Prim getSource() {
        return source;
    }

    /**
     * Set the <code>value</code> of the <code>name</code> property.
     *
     * @param name  property name.
     * @param value property value.
     */
    public void set(String name, String value) {
        try {
            source.sfReplaceAttribute(name, value);
        } catch (SmartFrogRuntimeException e) {
            throw new SFHadoopRuntimeException(e);
        } catch (RemoteException e) {
            throw new SFHadoopRuntimeException(e);
        }
    }

    /**
     * Get the value of the <code>name</code> property. If no such property
     * exists, then <code>defaultValue</code> is returned.
     *
     * @param name         property name.
     * @param defaultValue default value.
     *
     * @return property value, or <code>defaultValue</code> if the property
     *         doesn't exist.
     *
     * @throws SFHadoopRuntimeException if things go wrong on SmartFrog
     */
    public String get(String name, String defaultValue) {
        try {
            return source.sfResolve(name, defaultValue, false);
        } catch (SmartFrogResolutionException e) {
            throw new SFHadoopRuntimeException(e);
        } catch (RemoteException e) {
            throw new SFHadoopRuntimeException(e);
        }
    }

    /**
     * Get the value of the <code>name</code> property, without doing <a
     * href="#VariableExpansion">variable expansion</a>.
     *
     * @param name the property name.
     *
     * @return the value of the <code>name</code> property, or null if no such
     *         property exists.
     */
    public String getRaw(String name) {
        return get(name, null);
    }

    /**
     * Get the value of the <code>name</code> property, <code>null</code> if no
     * such property exists.
     * <p/>
     * Values are processed for <a href="#VariableExpansion">variable
     * expansion</a> before being returned.
     *
     * @param name the property name.
     *
     * @return the value of the <code>name</code> property, or null if no such
     *         property exists.
     */
    public String get(String name) {
        return get(name, null);
    }
}
