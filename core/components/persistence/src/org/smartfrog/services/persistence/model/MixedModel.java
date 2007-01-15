package org.smartfrog.services.persistence.model;
//com.hp.ov.cc.statemodel.recoverablestatecomponents.model;

import java.util.Enumeration;
import java.util.Iterator;

import org.smartfrog.services.persistence.model.CommitPoints;
import org.smartfrog.services.persistence.model.RedeployPersistence;
import org.smartfrog.services.persistence.storage.Storage;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;


/**
 * MixedModel should be used in conjunction with MixedRecoveryDeployer
 * as this one knows how to do deployment from storage details.
 */
public class MixedModel extends RedeployPersistence {

    /**
     * The constructor
     * @param configdata configuration data for the persistence model
     */
    public MixedModel( ComponentDescription configdata ) {
        super(configdata);
    }



    /**
     * {@inheritDoc}
     *
     * The mixed model needs to retain the component descriptions of its
     * children so they can be redeployed on recovery. This is done by
     * copying the descriptions to another data attribute. The children's
     * attribute names are marked volatile.
     * If the child is
     * recoverable its description is its storage details, if not it it
     * the description itself.
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
                        ComponentDescription storedCd;
                        ComponentDescription storage = cd.sfResolve(Storage.CONFIG_DATA,(ComponentDescription)null, false );
                        if( storage == null ) {
                            storedCd = ( ComponentDescription )cd.copy();
                        } else {
                            storedCd = copyCD(storage);
                        }
                        storedCd.sfAddAttribute("wfVersion", "StoredVersion");
                        storedCd.setParent(children);
                        storedCd.setEager(true);
                        children.sfAddAttribute(attr, storedCd);
                    }
                }
            }
            context.sfAddAttribute("sfPersistedChildrenDescriptions", children);
        } catch (SmartFrogRuntimeException ex) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.
                    forward("Failure preparing children for persistent storage", ex);
        }
    }
    
    
    
    
    private ComponentDescription copyCD(ComponentDescription cd) {
        Context ctx = cd.sfContext();
        Context ctxCopy = new ContextImpl();
        ComponentDescription cdCopy = new ComponentDescriptionImpl(null, ctxCopy, cd.getEager());
        for(Enumeration keys = ctx.keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object val = ctx.get(key);
            if( val instanceof Reference ) {
                try {
                    val = cd.sfResolve( ( Reference ) val );
                } catch ( SmartFrogResolutionException ex ) {
                    ctx.put(key, "****failed to dereference*****");
                    continue;
                }
            }
            if( val instanceof ComponentDescription ) {
                ComponentDescription cdValCopy = copyCD((ComponentDescription)val);
                cdValCopy.setParent(cdCopy);
                ctxCopy.put(key, cdValCopy);
                continue;
            }
            ctxCopy.put(key, val);
        }
        cdCopy.setParent(null);
        cdCopy.setPrimParent(null);
        return cdCopy;
    }


}
