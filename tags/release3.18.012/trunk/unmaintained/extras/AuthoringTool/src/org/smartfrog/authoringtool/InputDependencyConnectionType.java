
package org.smartfrog.authoringtool;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.gems.designer.model.BasicConnectionType;
import org.gems.designer.model.Wire;
import org.gems.designer.model.AttributeValue;
import java.util.Vector;
import org.smartfrog.authoringtool.emf.*;


public class InputDependencyConnectionType extends BasicConnectionType {
    public static final String NAME = "InputDependency";
    public static final InputDependencyConnectionType INSTANCE = 
        new InputDependencyConnectionType();
    
    public static final String RELEVANT = "Relevant";
    public static final String ENABLED = "Enabled";
    public static final String INPUT_DEPENDENCY_NAME = "Input_Dependency_Name";
    

    public static final IPropertyDescriptor[] INPUTDEPENDENCY_ATTRIBUTE_DESCRIPTORS =
    {
new org.eclipse.ui.views.properties.TextPropertyDescriptor("Relevant","Relevant")


            ,
new org.eclipse.ui.views.properties.TextPropertyDescriptor("Enabled","Enabled")


            ,
new org.eclipse.ui.views.properties.TextPropertyDescriptor("Input_Dependency_Name","Input_Dependency_Name")


    };
    public static final Object[] INPUTDEPENDENCY_DEFAULT_ATTRIBUTE_VALUES = {
       
            "0"
             ,
            "0"
             ,
            "0"
    };
    
      	 

    
    protected void getPropertyDescriptors(Vector desc){
    	desc.addAll(java.util.Arrays.asList(INPUTDEPENDENCY_ATTRIBUTE_DESCRIPTORS));    	
    	super.getPropertyDescriptors(desc);
    }
    
   
   
    
    private InputDependencyConnectionType() {
        super(NAME,Component.class,Connectors.class,"Component_Dependent_Source","Component_Depends_On_Connector");
        registerType(SmartfrogProvider.MODEL_ID,this);
    }
    
    protected InputDependencyConnectionType(String type) {
        super(type);
        registerType(SmartfrogProvider.MODEL_ID,this);
    }
    
    public String getModelID() {
        return SmartfrogProvider.MODEL_ID;
    }
    
    public void installAttributes(Wire wire) {
    	EMFInputDependencyProxy proxy = new EMFInputDependencyProxy();
		installAttributes(wire,proxy);
    }
    
     public void installAttributes(Wire wire, EMFInputDependencyProxy proxy) {
        AttributeValue[] attributes = proxy.getAttributeValues();
        wire.installAttribute(NAME,attributes[0]);
        for(int i = 0; i < INPUTDEPENDENCY_ATTRIBUTE_DESCRIPTORS.length; i++) {
            wire.installAttribute((String)INPUTDEPENDENCY_ATTRIBUTE_DESCRIPTORS[i].getId(),attributes[i+1]);
        }
        super.installAttributes(wire);
    }
   

    public static String getRelevant(Wire w){
    	return (String)w.getAttribute(RELEVANT);
    }
    public static String getEnabled(Wire w){
    	return (String)w.getAttribute(ENABLED);
    }
    public static String getInput_Dependency_Name(Wire w){
    	return (String)w.getAttribute(INPUT_DEPENDENCY_NAME);
    }
}

 
       
       
   