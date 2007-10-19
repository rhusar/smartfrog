/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP
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



package org.smartfrog.services.persistence.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

/**
 * <p>PersistenceModel is a abstract base class for all persistence models. It
 * has a static method constructModel(context) that constructs an implementation
 * of this class described in the model config data in context.</p>
 *
 * <p>The PersistenceModel controls the behavior of a recoverable component.
 * Persistence is achieved by storing attributes on stable storage, committing
 * only at certain points in the startup life cycle and when attributes change
 * after the component has started.</p>
 *
 * <p>The persistence model modifies the behavior of the recoverable component
 * by vetoing attributes and commit points and by modifying the component's
 * context before deployment.</p>
 *
 * <p>The model can veto attributes by identifying them as volatile
 * (the isVolatile method). Volatile attributes are not written to persistent
 * storage. Similarly the model can also veto commit points duing the
 * startup life cycle (the isCommitPoint method).</p>
 *
 * <p>Prior to deployWith, the model is given the opportunity to
 * modify the persistent component's context. Two methods are provided to
 * differentiate initial startup and recovery startup (the initialContext
 * and recoverContext methods).</p>
 *
 */
public abstract class PersistenceModel {

    public static String UNKNOWN_CLASS = "unknown persistence model class";
    public static String MODELCLASS_ATTR = "sfPersistenceModelClass";
    public static String MODELCONFIG_ATTR = "sfPersistenceModelConfig";

    /**
     * The set of volatile attributes
     */
    protected Set volatileAttrs = new HashSet();


    /**
     * Constructs the persistence model specified by the model data in the context.
     *
     * @param context A SmartFrog context containing the model config data.
     * @return PersistenceModel An implementation of this class.
     * @throws SmartFrogDeploymentException The implementation could not be constructed.
     */
    public static PersistenceModel constructModel(Context context) throws
            SmartFrogDeploymentException {

        /**
         * configObj will be null if the value not present or SFNull if
         * set to null in the SF description.
         * If either are the case construct a NullModel implementation.
         * Otherwise continue with the configured implementation.
         */
        Object configObj = context.get(MODELCONFIG_ATTR);
        if ( configObj == null  || configObj instanceof SFNull ) {
        	// @TODO: log the null model creation.
        	return new NullModel(null);
//            throw new SmartFrogDeploymentException(MODELCONFIG_ATTR +
//                    " is not defined - persistence model required");
        }
        if (!(configObj instanceof ComponentDescription)) {
            throw new SmartFrogDeploymentException(MODELCONFIG_ATTR +
                    " is not a component description - persistence model required");
        }
        ComponentDescription configData = (ComponentDescription) configObj;
        String className = UNKNOWN_CLASS;
        try {
            className = configData.sfResolve(MODELCLASS_ATTR, (String)null, true);
            Class modelClass = Class.forName(className);
            Class[] constparam = new Class[1];
            constparam[0] = ComponentDescription.class;
            Constructor modelConstructor = modelClass.getConstructor(constparam);
            Object[] params = new Object[1];
            params[0] = configObj;
            return (PersistenceModel) modelConstructor.newInstance(params);
        } catch (InvocationTargetException ex) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.
                    forward(
                            "Failed to construct persistence model " +
                            className, ex);
        } catch (IllegalArgumentException ex) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.
                    forward(
                            "Failed to construct persistence model " +
                            className, ex);
        } catch (IllegalAccessException ex) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.
                    forward(
                            "Failed to construct persistence model " +
                            className, ex);
        } catch (InstantiationException ex) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.
                    forward(
                            "Failed to construct persistence model " +
                            className, ex);
        } catch (SecurityException ex) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.
                    forward(
                            "Failed to construct persistence model " +
                            className, ex);
        } catch (NoSuchMethodException ex) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.
                    forward(
                            "Failed to construct persistence model " +
                            className, ex);
        } catch (ClassNotFoundException ex) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.
                    forward(
                            "Failed to construct persistence model " +
                            className, ex);
        } catch (SmartFrogResolutionException ex) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.
                    forward(
                            "Persistence model class name missing", ex);
        }

    }


    /**
     * <p>initialContext is an opportunity to modify the context prior to
     * it being written to stable storage and prior to the component being
     * initialised with the context. It is also an opportunity to initialise
     * the persistence model if needed.</p>
     *
     * <p>Note that this occurs before the component has been initialised, so
     * only the context is available for use.</p>
     *
     * @param context the context of the component being deployed
     * @throws SmartFrogDeploymentException the model could not initialise the
     * context.
     */
    public abstract void initialContext(Context context) throws
            SmartFrogDeploymentException;


    /**
     * <p>recoverContext is an opportunity to modify the context prior to the
     * component being initialised for recovery. It is also an opportunity to
     * initialise the persistence model if needed.</p>
     *
     * <p>Note that modifications made to the context here will not be written
     * back to the stable storage, so it only makes sense to add or modify
     * volitle attributes in this method.</p>
     *
     * @param context the context of the component being recovered
     * @throws SmartFrogDeploymentException the model could not initialise
     * the context.
     */
    public abstract void recoverContext(Context context) throws
            SmartFrogDeploymentException;


    /**
     * <p>Called during recovery and indicates if the component should do an
     * sfDeploy as part of the recovery.</p>
     *
     * @param component the component being recovered
     * @return boolean - true if sfDeploy() should be performed during recovery,
     * false if not.
     */
    public abstract boolean redeploy(Prim component);


    /**
     * <p>Called during recovery and indicates if the component should do an
     * sfStart as part of the recovery.</p>
     *
     * @param component the component being recovered
     * @return boolean - true if sfDeploy() should be performed during recovery,
     * false if not.
     */
    public abstract boolean restart(Prim component);


    /**
     * <p>Called during termination to determine if the stored context should
     * be left behind or deleted.  This is also an opportunity to leave
     * something in the storage if it is going to be left behind, e.g. a
     * note of what happened to the component.</p>
     *
     * @param Component the component being terminated
     * @param tr the termination record
     * @return true indicates that the storage should be retained, false means
     * it should be deleted
     */
    public abstract boolean leaveTombStone(Prim Component, TerminationRecord tr);


    /**
     * <p>Called to indicate that a child is being added to the component. The
     * model may wish to handle children differently to other attributes as they
     * have a life cycle of their own they may perform their own recovery. It is
     * up to the model to decide if it wants to persist these attributes.</p>
     *
     * @param component the component this model applies to.
     * @param child the child being added to the component.
     * @param attribute the attribute name of the child being added.
     */
    public void childAdded(Prim component, Prim child, String attribute) {
        // does nothing by default
    }


    /**
     * <p>Called to indicate that a child is being removed from the component. The
     * model may wish to handle children differently to other attributes as they
     * have a life cycle of their own they may perform their own recovery. It is
     * up to the model to decide if it wants to persist these attributes.</p>
     *
     * @param component the component this model applies to.
     * @param child the child being removed from the component.
     * @param attribute the attribute name of the child being removed.
     */
    public void childRemoved(Prim component, Prim child, String attribute) {
        // does nothing by default
    }


    /**
     * <p>Called to indicate that the given attribute is volatile. The component
     * itself might decide that certain attributes are volatile. This needs
     * to be communicated to the model as it controls persistence.</p>
     *
     * <p>The component should take care only to fiddle with the persistence
     * of attributes it defines itself to avoid confusing the persistence model.</p>
     *
     * @param attr the attribute name
     * @return boolean - true if the attribute was NOT already volatile, false if it was
     */
    public boolean addVolatile(Object attr) {
        return volatileAttrs.add(attr);
    }


    /**
     * <p>Called to indicate that the given attribute is not volatile. The component
     * itself might decide that certain attributes are not volatile. This needs
     * to be communicated to the model as it controls persistence.</p>
     *
     * <p>The component should take care only to fiddle with the persistence
     * of attributes it defines itself to avoid confusing the persistence model.</p>
     *
     * @param attr the attribute name
     * @return boolean - true if the attribuet was volatile, false if it was not
     */
    public boolean removeVolatile(Object attr) {
        return volatileAttrs.remove(attr);
    }


    /**
     * <p>This method is used to determine if an attribute is volatile. Volatile
     * attributes are not written to stable storage.</p>
     *
     * @param attr the attribute name
     * @return boolean - true if the attribute is volatile, false if it is not
     */
    public boolean isVolatile(Object attr) {
        return volatileAttrs.contains(attr);
    }


    /**
     * <p>This method is used to indicate deployment lifecycle commit points.
     * These points determine atomic snap-shots of the components and occur
     * before and after the deployWith, deploy, start, and recover lifecycle
     * transitions.</p>
     *
     * <p>Note that the model may chose to take action, such as updating an
     * attribute to note that the commit point has been reached. Such changes
     * will be committed atomically at this commit point. BUT TAKE CARE - the
     * component has not be initialised at the PRE_DEPLOYWITH commit point.</p>
     *
     * @param component the component
     * @param point the life cycle commit point
     * @return boolean - true if the commit point is used by this model, false
     * it it should be skipped.
     */
    public abstract boolean isCommitPoint(Prim component, String point) throws
            SmartFrogException, RemoteException;
}
