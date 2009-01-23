
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
    	
    	
       	private class RelevantAttributeValue implements AttributeValue{
        	private AttributeValue value_;
        	
			public RelevantAttributeValue(AttributeValue val){
				value_ = val;
				if(conn_ != null)
				   
				   value_.setValue(conn_.getRelevant());
				   
			}
			
			public Object getPropertyValue() {return value_.getPropertyValue();}		
			public Object getValue() {return value_.getValue();}		
			public void setValue(Object value) {
				value_.setValue(value);
				Object val = value_.getValue();
				conn_.setRelevant((String)val);
			}		
		}
       	private class EnabledAttributeValue implements AttributeValue{
        	private AttributeValue value_;
        	
			public EnabledAttributeValue(AttributeValue val){
				value_ = val;
				if(conn_ != null)
				   
				   value_.setValue(conn_.getEnabled());
				   
			}
			
			public Object getPropertyValue() {return value_.getPropertyValue();}		
			public Object getValue() {return value_.getValue();}		
			public void setValue(Object value) {
				value_.setValue(value);
				Object val = value_.getValue();
				conn_.setEnabled((String)val);
			}		
		}
       	private class Dependency_NameAttributeValue implements AttributeValue{
        	private AttributeValue value_;
        	
			public Dependency_NameAttributeValue(AttributeValue val){
				value_ = val;
				if(conn_ != null)
				   
				   value_.setValue(conn_.getDependency_Name());
				   
			}
			
			public Object getPropertyValue() {return value_.getPropertyValue();}		
			public Object getValue() {return value_.getValue();}		
			public void setValue(Object value) {
				value_.setValue(value);
				Object val = value_.getValue();
				conn_.setDependency_Name((String)val);
			}		
		}
    	
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
  factory.getStringValue("Relevant","0"),
  factory.getStringValue("Enabled","0"),
  factory.getStringValue("Dependency_Name","0")
};



       	   attributeValues_ = new AttributeValue[3 + 1];
       	   attributeValues_[0] = new AttributeValue() {
        	public Object getPropertyValue() {return "EMF";}		
			public Object getValue() {return conn_;}		
			public void setValue(Object value) {}		
			};
       	   
       	   
       	   attributeValues_[1]= new RelevantAttributeValue(attributes[0]);
       	  
       	   
       	   
       	   attributeValues_[2]= new EnabledAttributeValue(attributes[1]);
       	  
       	   
       	   
       	   attributeValues_[3]= new Dependency_NameAttributeValue(attributes[2]);
       	  
       	   
    	}
    	
    	public AttributeValue[] getAttributeValues(){
    		return attributeValues_;
    	}
}

 
       
       
   