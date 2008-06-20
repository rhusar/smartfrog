
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
    
    

    public static final IPropertyDescriptor[] OUTPUTDEPENDENCY_ATTRIBUTE_DESCRIPTORS =
    {
    };
    public static final Object[] OUTPUTDEPENDENCY_DEFAULT_ATTRIBUTE_VALUES = {
       
    };
    
      	 

    
    protected void getPropertyDescriptors(Vector desc){
    	desc.addAll(java.util.Arrays.asList(OUTPUTDEPENDENCY_ATTRIBUTE_DESCRIPTORS));    	
    	super.getPropertyDescriptors(desc);
    }
    
   
   
    
    private OutputDependencyConnectionType() {
        super(NAME,Connectors.class,Component.class,"By","On");
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
   

}

 
       
       
   