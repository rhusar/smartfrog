
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
    
    

    public static final IPropertyDescriptor[] INPUTDEPENDENCY_ATTRIBUTE_DESCRIPTORS =
    {
    };
    public static final Object[] INPUTDEPENDENCY_DEFAULT_ATTRIBUTE_VALUES = {
       
    };
    
      	 

    
    protected void getPropertyDescriptors(Vector desc){
    	desc.addAll(java.util.Arrays.asList(INPUTDEPENDENCY_ATTRIBUTE_DESCRIPTORS));    	
    	super.getPropertyDescriptors(desc);
    }
    
   
   
    
    private InputDependencyConnectionType() {
        super(NAME,Component.class,Connectors.class,"By","On");
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
   

}

 
       
       
   