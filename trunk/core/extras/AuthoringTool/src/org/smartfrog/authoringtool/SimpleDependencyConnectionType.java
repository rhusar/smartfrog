
package org.smartfrog.authoringtool;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.gems.designer.model.BasicConnectionType;
import org.gems.designer.model.Wire;
import org.gems.designer.model.AttributeValue;
import java.util.Vector;
import org.smartfrog.authoringtool.emf.*;


public class SimpleDependencyConnectionType extends BasicConnectionType {
    public static final String NAME = "SimpleDependency";
    public static final SimpleDependencyConnectionType INSTANCE = 
        new SimpleDependencyConnectionType();
    
    public static final String RELEVANT = "Relevant";
    public static final String ENABLED = "Enabled";
    

    public static final IPropertyDescriptor[] SIMPLEDEPENDENCY_ATTRIBUTE_DESCRIPTORS =
    {
new org.eclipse.ui.views.properties.TextPropertyDescriptor("Relevant","Relevant")


            ,
new org.eclipse.ui.views.properties.TextPropertyDescriptor("Enabled","Enabled")


    };
    public static final Object[] SIMPLEDEPENDENCY_DEFAULT_ATTRIBUTE_VALUES = {
       
            "0"
             ,
            "0"
    };
    
      	 

    
    protected void getPropertyDescriptors(Vector desc){
    	desc.addAll(java.util.Arrays.asList(SIMPLEDEPENDENCY_ATTRIBUTE_DESCRIPTORS));    	
    	super.getPropertyDescriptors(desc);
    }
    
   
   
    
    private SimpleDependencyConnectionType() {
        super(NAME,Component.class,Component.class,"Depends_By","DependOn");
        registerType(SmartfrogProvider.MODEL_ID,this);
    }
    
    protected SimpleDependencyConnectionType(String type) {
        super(type);
        registerType(SmartfrogProvider.MODEL_ID,this);
    }
    
    public String getModelID() {
        return SmartfrogProvider.MODEL_ID;
    }
    
    public void installAttributes(Wire wire) {
    	EMFSimpleDependencyProxy proxy = new EMFSimpleDependencyProxy();
		installAttributes(wire,proxy);
    }
    
     public void installAttributes(Wire wire, EMFSimpleDependencyProxy proxy) {
        AttributeValue[] attributes = proxy.getAttributeValues();
        wire.installAttribute(NAME,attributes[0]);
        for(int i = 0; i < SIMPLEDEPENDENCY_ATTRIBUTE_DESCRIPTORS.length; i++) {
            wire.installAttribute((String)SIMPLEDEPENDENCY_ATTRIBUTE_DESCRIPTORS[i].getId(),attributes[i+1]);
        }
        super.installAttributes(wire);
    }
   

    public static String getRelevant(Wire w){
    	return (String)w.getAttribute(RELEVANT);
    }
    public static String getEnabled(Wire w){
    	return (String)w.getAttribute(ENABLED);
    }
}

 
       
       
   