
package org.smartfrog.authoringtool;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.gems.designer.model.BasicConnectionType;
import org.gems.designer.model.Wire;
import org.gems.designer.model.AttributeValue;
import java.util.Vector;
import org.smartfrog.authoringtool.emf.*;


public class EMFSimpleDependencyProxy {
    	private SimpleDependencyConnection conn_;
    	private AttributeValue[] attributeValues_;
    	
    	
    	
    	public EMFSimpleDependencyProxy(){
    		conn_ = org.smartfrog.authoringtool.emf.impl.SmartfrogFactoryImpl.eINSTANCE.createSimpleDependencyConnection();
    		init();
    	}
    	
    	public EMFSimpleDependencyProxy(SimpleDependencyConnection con){
    		conn_ = con;
    		init();
    	}
    	
    	protected void init(){
    	   
       	   org.gems.designer.model.AttributeValueFactory factory = 
       new org.gems.designer.model.AttributeValueFactory(AttributeValidators.getInstance());
       
org.gems.designer.model.AttributeValue[] attributes = {
};



       	   attributeValues_ = new AttributeValue[0 + 1];
       	   attributeValues_[0] = new AttributeValue() {
        	public Object getPropertyValue() {return "EMF";}		
			public Object getValue() {return conn_;}		
			public void setValue(Object value) {}		
			};
       	   
    	}
    	
    	public AttributeValue[] getAttributeValues(){
    		return attributeValues_;
    	}
}

 
       
       
   