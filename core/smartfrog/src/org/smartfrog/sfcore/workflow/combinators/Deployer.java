package org.smartfrog.sfcore.workflow.combinators;

import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.util.Vector;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.CreateNewChildThread;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import java.util.Enumeration;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.parser.Phases;
import java.io.InputStream;
import org.smartfrog.sfcore.parser.SFParser;
import java.util.Iterator;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import java.util.Map;

/**
 *
 */
public class Deployer  extends Run implements Compound {

    /** Reference to myName for the new component.
     String name for attribute. Value {@value} or asName.*/
    final static String ATTR_NAME = "newComponentName";

    /** Reference to parent for the new deployment.
     String name for attribute. Value {@value} or parent.*/
    final static String ATTR_PARENT = "newComponentParent";

    /** Reference to compund component used to drive the new deployment.
     String name for attribute. Value {@value} or parent.*/
    final static String ATTR_DEPLOYER = "newComponentDeployer";

    /** Reference to #codebase.
     String name for attribute. Value {@value}.*/
    final static String ATTR_HCODEBASE = "newComponentCodebase";

    /** Reference to myDescription (it could be a .sf file for the text itself).
     String name for attribute. Value {@value}.*/
    final static String ATTR_DESCRIPTION = "newComponentDescription";

    /** Reference to extra attributes that will be added to the new component CD
     String name for attribute. Value {@value}.*/
    final static String ATTR_EXTRA_ATTRIBUTES = "newComponentExtraAtributes";

    String newComponentCodebase = null;
    String newComponentDescription = null;
    Vector newComponentExtraAtributes = null;

    Compound newComponentDeployer = null;

    ComponentDescription newComponentCD = null;

    /**
     * Constructs Deployer.
     *
     * @throws java.rmi.RemoteException In case of network or RMI failure.
     */
    public Deployer() throws java.rmi.RemoteException {
        super();
    }

    /**
     * Reads the basic configuration of the component and deploys.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogDeploymentException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        newComponentCD = getComponentDescription();
    }

    protected void readSFAttributes() throws  RemoteException, SmartFrogResolutionException {
        // It will by default use the same used in Run if available
        super.readSFAttributes();
        parent =  sfResolve(ATTR_PARENT, parent, false);
        asName =  sfResolve(ATTR_NAME, asName, false);

        newComponentDeployer =  sfResolve(ATTR_DEPLOYER, newComponentDeployer, false);

        newComponentDescription = sfResolve(ATTR_DESCRIPTION,newComponentDescription,true);
        newComponentCodebase = sfResolve(ATTR_HCODEBASE,newComponentCodebase,false);
        newComponentExtraAtributes = sfResolve(ATTR_EXTRA_ATTRIBUTES,newComponentExtraAtributes,false);
    }

    protected Prim createNewChild() throws SmartFrogDeploymentException, RemoteException {
        Prim comp;
        if (newComponentDeployer!=null) {
            comp = newComponentDeployer.sfCreateNewChild(asName,parent,newComponentCD,null);
        } else {
            //Same semantics as RUN
            if (parent!=null) {
                comp = parent.sfCreateNewChild(asName, newComponentCD, null);
            } else {
                comp = sfCreateNewApp(asName, newComponentCD, null);
            }
        }
        return comp;
    }

    protected ComponentDescription getComponentDescription() throws
        SmartFrogDeploymentException, SmartFrogException {
        Phases phases = null;
        try {
            InputStream descriptionStream = org.smartfrog.SFSystem.getInputStreamForResource(newComponentDescription);
            SFParser parser = new SFParser(SFParser.getLanguageFromUrl(newComponentDescription));
            phases = parser.sfParse(descriptionStream,newComponentCodebase);
            addAttributesToCD(newComponentExtraAtributes, phases);
        } catch (Exception e) {
             sfLog().err("", e);
             throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(e);
        }
        phases = phases.sfResolvePhases();
        return phases.sfAsComponentDescription();
    }

    //Placement done by the compiler
    private void addAttributesToCD(Vector attributes, Phases phases) throws SmartFrogResolutionException,
            SmartFrogRuntimeException {
        if (attributes != null) {
            if (sfLog().isTraceEnabled())  sfLog().trace("Attribute Replacement Started\n");
            for (Iterator e = attributes.iterator(); e.hasNext();) {
                Vector attribute_value = (Vector)e.next();
                String key = (String) attribute_value.elementAt(0);
                Object value = attribute_value.elementAt(1);
                Reference keyRef = Reference.fromString(key.toString());
                if (sfLog().isTraceEnabled())  sfLog().trace("Attribute : KeyRef:" + keyRef.toString() + "  ; value:" + value.toString());
                phases.sfReplaceAttribute(keyRef, value);
            }
        }
    }

    //Placement done by the compiler
    private void addAttributesToCD(Map attributes, Phases phases) throws SmartFrogResolutionException,
            SmartFrogRuntimeException {
        if (attributes != null) {
            sfLog().info("Attribute Replacement Started\n");
            for (Iterator e = attributes.keySet().iterator(); e.hasNext();) {
                Object key = e.next();
                Object value = attributes.get(key);
                Reference keyRef = Reference.fromString(key.toString());
                sfLog().info("Attribute : KeyRef:" + keyRef.toString() + "  ; value:" + value.toString());
                phases.sfReplaceAttribute(keyRef, value);
            }
        }
    }

}
