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

import java.util.Iterator;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;


/**
 * Redeployment Persistence model.
 *
 RedeployPersistence is a persistence model for components that re-create
 * their children on recovery. This applies to children found in their original
 * component description only, any children added to the component by other
 * means are not recovered, however all children are considered volatile.
 */
public class RedeployPersistence extends PersistenceModel {

    /**
     * Added to volatileAttrs
     */
    {
        volatileAttrs.add("sfHost");
        volatileAttrs.add("sfLog");
        volatileAttrs.add("sfParent");
        volatileAttrs.add("WOODFROG_WFSFPARENT");
        volatileAttrs.add("WOODFROG_WFSFCHILDREN");
    }


    /**
     * The constructor
     * @param configdata configuration data for the persistence model
     */
    public RedeployPersistence(ComponentDescription configdata) {
    }


    /**
     * {@inheritDoc}
     *
     * The redeploy model needs to retain the component descriptions of its
     * children so they can be redeployed on recovery. This is done by
     * copying the descriptions to another data attribute. The children's
     * attribute names are marked volatile.
     *
     */
    public void initialContext(Context context) throws
            SmartFrogDeploymentException {

        try {
            /**
             * Note: there is no need to assign a parent for the
             * sfPersistedChildrenDescriptions component description because
             * this method is called prior to the deployWith of the prim,
             * so the parent will be set there.
             */
            ComponentDescription children = new ComponentDescriptionImpl(null,
                    new ContextImpl(), false);
            Iterator iter = context.sfAttributes();
            while (iter.hasNext()) {
                Object attr = (String) iter.next();
                Object value = context.get(attr);
                if ((value instanceof ComponentDescription)) {
                    ComponentDescription cd = (ComponentDescription) value;
                    if (cd.getEager()) {
                        volatileAttrs.add(attr);
                        ComponentDescription storedCd = (ComponentDescription)
                                cd.copy();
                        storedCd.sfAddAttribute("wfVersion", "StoredVersion");
                        storedCd.setParent(children);
                        children.sfAddAttribute(attr, storedCd);
                    }
                }
            }
            context.sfAddAttribute("sfPersistedChildrenDescriptions", children);
        } catch (SmartFrogRuntimeException ex) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.
                    forward(
                            "Failure preparing children for persistent storage",
                            ex);
        }
    }


    /**
     * {@inheritDoc}
     *
     * On recovery the retained children component descriptions are copied back
     * to their original attributes. When deployment continues this will lead
     * to new copies of the children being deployed.
     *
     * @param context {@inheritDoc}
     * @throws SmartFrogDeploymentException {@inheritDoc}
     */
    public void recoverContext(Context context) throws
            SmartFrogDeploymentException {

        // System.out.println("  ***** Recovering context ****** ");

        try {
            /**
             * Note: we do not need to set the prim parent of the children
             * component descriptions after copying them back because this
             * method is called before deployWith, and the prim parents are
             * set there.
             */
            ComponentDescription children = (ComponentDescription) context.get(
                    "sfPersistedChildrenDescriptions");
            Iterator aiter = children.sfAttributes();
            Iterator viter = children.sfValues();
            while (aiter.hasNext()) {
                String name = (String) aiter.next();
                ComponentDescription storedCd = (ComponentDescription) viter.
                                                next();
                volatileAttrs.add(name);
                ComponentDescription cd = (ComponentDescription) storedCd.copy();
                cd.sfReplaceAttribute("wfVersion", "Recovered Version");
                context.sfAddAttribute(name, cd);
                // System.out.println(" ***** Adding " + name + " to context *****");
            }
        } catch (SmartFrogContextException ex) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.
                    forward(
                            "Failed reconstructing children descriptions", ex);
        } catch (SmartFrogRuntimeException ex) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.
                    forward(
                            "Failed reconstructing children descriptions", ex);
        }
    }


    /**
     * {@inheritDoc}
     *
     * RedeployPersistence always does a deploy on recovery
     *
     * @return boolean - true
     */
    public boolean redeploy(Prim component) {
        return true;
    }


    /**
     * {@inheritDoc}
     *
     * RedeployPersistence always does a start on recovery
     *
     * @return boolean - true
     */
    public boolean restart(Prim component) {
        return true;
    }


    /**
     * {@inheritDoc}
     *
     * RedeployPersistence deletes the storage on normal termination.
     */
    public boolean leaveTombStone(Prim Component, TerminationRecord tr) {
        if (TerminationRecord.NORMAL.equals(tr.errorType)) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * {@inheritDoc}
     *
     * RedeployPersistence marks children attribute names as volatile when
     * they are added
     *
     */
    public void childAdded(Prim component, Prim child, String attribute) {
        volatileAttrs.add(attribute);
    }


    /**
     * {@inheritDoc}
     *
     * RedeployPersistence marks children attribute names as non-volatile when
     * they are removed
     *
     */
    public void childRemoved(Prim component, Prim child, String attribute) {
        volatileAttrs.remove(attribute);
    }


    /**
     * {@inheritDoc}
     *
     * RedeployPersistence Commit points are before deployWith, after Start
     * and after recover
     *
     */
    public boolean isCommitPoint(Prim component, String point) {
        if ((CommitPoints.PRE_DEPLOY_WITH.equals(point) ||
             CommitPoints.POST_START.equals(point) ||
             CommitPoints.POST_RECOVER.equals(point))) {
            // System.out.println("=== " + point + " is a commit point ===");
            return true;
        } else {
            // System.out.println("=== " + point + " is not a commit point ===");
            return false;
        }
    }


}
