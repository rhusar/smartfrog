
package org.smartfrog.authoringtool;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.gems.designer.model.BasicConnectionType;
import org.gems.designer.model.Wire;
import org.gems.designer.model.AttributeValue;
import java.util.Vector;
import org.smartfrog.authoringtool.emf.*;


public class OutputDependencyConnectionType extends BasicConnectionType {
    public static final String NAME = "OutputDependency";
    public static final OutputDependencyConnectionType INSTANCE = 
        new OutputDependencyConnectionType();
    
    public static final String RELEVANT = "Relevant";
    public static final String ENABLED = "Enabled";
    public static final String OUTPUT_DEPENDENCY_NAME = "Output_Dependency_Name";
    

    public static final IPropertyDescriptor[] OUTPUTDEPENDENCY_ATTRIBUTE_DESCRIPTORS =
    {
new org.eclipse.ui.views.properties.TextPropertyDescriptor("Relevant","Relevant")


            ,
new org.eclipse.ui.views.properties.TextPropertyDescriptor("Enabled","Enabled")


            ,
new org.eclipse.ui.views.properties.TextPropertyDescriptor("Output_Dependency_Name","Output_Dependency_Name")


    };
    public static final Object[] OUTPUTDEPENDENCY_DEFAULT_ATTRIBUTE_VALUES = {
       
            "0"
             ,
            "0"
             ,
            "0"
    };
    
      	 

    
    protected void getPropertyDescriptors(Vector desc){
    	desc.addAll(java.util.Arrays.asList(OUTPUTDEPENDENCY_ATTRIBUTE_DESCRIPTORS));    	
    	super.getPropertyDescriptors(desc);
    }
    
   
   
    
    private OutputDependencyConnectionType() {
        super(NAME,Connectors.class,Component.class,"Connector_Dependent_Source","Connector_Depend_On_Component");
        registerType(SmartfrogProvider.MODEL_ID,this);
    }
    
    protected OutputDependencyConnectionType(String type) {
        super(type);
        registerType(SmartfrogProvider.MODEL_ID,this);
    }
    
    public String getModelID() {
        return SmartfrogProvider.MODEL_ID;
    }
    
    public void installAttributes(Wire wire) {
    	EMFOutputDependencyProxy proxy = new EMFOutputDependencyProxy();
		installAttributes(wire,proxy);
    }
    
     public void installAttributes(Wire wire, EMFOutputDependencyProxy proxy) {
        AttributeValue[] attributes = proxy.getAttributeValues();
        wire.installAttribute(NAME,attributes[0]);
        for(int i = 0; i < OUTPUTDEPENDENCY_ATTRIBUTE_DESCRIPTORS.length; i++) {
            wire.installAttribute((String)OUTPUTDEPENDENCY_ATTRIBUTE_DESCRIPTORS[i].getId(),attributes[i+1]);
        }
        super.installAttributes(wire);
    }
   

    public static String getRelevant(Wire w){
    	return (String)w.getAttribute(RELEVANT);
    }
    public static String getEnabled(Wire w){
    	return (String)w.getAttribute(ENABLED);
    }
    public static String getOutput_Dependency_Name(Wire w){
    	return (String)w.getAttribute(OUTPUT_DEPENDENCY_NAME);
    }
}

 
       
       
   