package org.smartfrog.services.dependencies.modelcheck;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import org.smartfrog.services.dependencies.statemodel.connector.AndConnector;
import org.smartfrog.services.dependencies.statemodel.connector.NXorConnector;
import org.smartfrog.services.dependencies.statemodel.connector.NandConnector;
import org.smartfrog.services.dependencies.statemodel.connector.NorConnector;
import org.smartfrog.services.dependencies.statemodel.connector.OrConnector;
import org.smartfrog.services.dependencies.statemodel.connector.XorConnector;
import org.smartfrog.services.dependencies.statemodel.state.State;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.functions.And;
import org.smartfrog.sfcore.languages.sf.functions.Divide;
import org.smartfrog.sfcore.languages.sf.functions.EQ;
import org.smartfrog.sfcore.languages.sf.functions.GE;
import org.smartfrog.sfcore.languages.sf.functions.GT;
import org.smartfrog.sfcore.languages.sf.functions.Implies;
import org.smartfrog.sfcore.languages.sf.functions.LE;
import org.smartfrog.sfcore.languages.sf.functions.LT;
import org.smartfrog.sfcore.languages.sf.functions.Minus;
import org.smartfrog.sfcore.languages.sf.functions.Mod;
import org.smartfrog.sfcore.languages.sf.functions.NE;
import org.smartfrog.sfcore.languages.sf.functions.NXor;
import org.smartfrog.sfcore.languages.sf.functions.Not;
import org.smartfrog.sfcore.languages.sf.functions.Or;
import org.smartfrog.sfcore.languages.sf.functions.Product;
import org.smartfrog.sfcore.languages.sf.functions.Sum;
import org.smartfrog.sfcore.languages.sf.functions.Xor;
import org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference;
import org.smartfrog.sfcore.languages.sf.sfreference.SFReference;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

public class Resolver {
	private String functionClass = null;
	private HashSet<String> otherComponents = new HashSet<String>();
	
	static public String getFunctionRepresentation(String functionClass) throws SmartFrogResolutionException {
		if (functionClass.equals(And.class.getName())) return " & ";
		else if (functionClass.equals(Not.class.getName())) return "!";
		else if (functionClass.equals(Minus.class.getName())) return " - ";
		else if (functionClass.equals(Divide.class.getName())) return " / ";
		else if (functionClass.equals(Mod.class.getName())) return " mod ";
		else if (functionClass.equals(EQ.class.getName())) return " = ";
		else if (functionClass.equals(NE.class.getName())) return " != ";
		else if (functionClass.equals(GE.class.getName())) return " >= ";
		else if (functionClass.equals(LE.class.getName())) return " <= ";
		else if (functionClass.equals(GT.class.getName())) return " > ";
		else if (functionClass.equals(LT.class.getName())) return " < ";
		else if (functionClass.equals(Sum.class.getName())) return " + ";
		else if (functionClass.equals(Product.class.getName())) return " * ";
		else if (functionClass.equals(Or.class.getName())) return " | ";
		else if (functionClass.equals(Implies.class.getName())) return " -> ";
		else if (functionClass.equals(Xor.class.getName())) return " xor ";
		else if (functionClass.equals(NXor.class.getName())) return " xnor ";
		
		else if (functionClass.equals(State.class.getName())) return " & ";
		else if (functionClass.equals(AndConnector.class.getName())) return " & ";
		else if (functionClass.equals(OrConnector.class.getName())) return " | ";
		else if (functionClass.equals(NandConnector.class.getName())) return " & ";
		else if (functionClass.equals(NorConnector.class.getName())) return " | ";
		else if (functionClass.equals(XorConnector.class.getName())) return " xor ";
		else if (functionClass.equals(NXorConnector.class.getName())) return " xnor ";
		
		throw new SmartFrogResolutionException("Unknown function class for model checker:"+functionClass);
	}
	
	static public String getFinalFunctionRepresentation(String functionClass, String prefinal){
		if (functionClass.equals(NandConnector.class.getName()) || functionClass.equals(NorConnector.class.getName())) return " !("+prefinal+") ";
		else return prefinal;
	}
	
	static public String getBooleanRepresentation(boolean bool){
		return (bool?"1":"0");
	}
	
	static public String getBooleanRepresentation(Boolean bool){
		return getBooleanRepresentation(bool.booleanValue());
	}
	
	private String getCompAttrConjoiner(){
		return ".";
	}
	
	public void addInOtherComponents(HashSet<String> baseComponents){
		if (otherComponents==null || baseComponents==null) return;
		Iterator components = otherComponents.iterator();
		while (components.hasNext()) baseComponents.add((String)components.next());
	}
	
    //For Model Checking...    
    public String argumentToMCString(Object arg, ComponentDescription comp) throws SmartFrogResolutionException {
    	if (arg==null) return null;
    	
    	if (arg instanceof SFApplyReference) {
    		Resolver resolver = new Resolver();
    		String result = resolver.toMCString((SFApplyReference)arg, comp);
    		resolver.addInOtherComponents(otherComponents);
    		return result;
    	} else if (arg instanceof Reference){
    		Reference ref = (Reference) arg;
    		
			SFReference.resolutionForceEager=true;
			ReferencePart.maintainResolutionHistory=true;
			Object result = comp.sfResolve(ref);
			ComponentDescription ref_comp = (ComponentDescription) ReferencePart.resolutionParentDescription;
			SFReference.resolutionForceEager=false;
			ReferencePart.maintainResolutionHistory=false;
			
			//Get Component Id for component containing referred to attribute
			Object cpt_id = ref_comp.sfContext().get("sfUniqueComponentID");
			if (cpt_id==null) throw new SmartFrogResolutionException("Reference:"+ref+" in:"+comp+" does not resolve to attribute within orch component");
			String cpt_id_str = (String) cpt_id;
			
			//Get attribute name
			String attr = (String) ref_comp.sfAttributeKeyFor(result);
			
			if (cpt_id_str.equals(ModelCheck.cpt_id)) return attr;  //Actually our own component...
			else {
				otherComponents.add(cpt_id_str);
				return cpt_id_str+getCompAttrConjoiner()+attr;
			}
    	}
    	else if (arg instanceof Boolean) return getBooleanRepresentation(((Boolean)arg).booleanValue());
    	else return arg.toString();
    }
    
    private String toMCString(String functionClass, ComponentDescription comp) throws SmartFrogResolutionException {
    	java.util.Vector<String> args = new java.util.Vector<String>();
    	for (Enumeration e = comp.sfContext().keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            if (key.equals("sfFunctionClass")) continue;
            Object val = comp.sfContext().get(key);
            String arg = argumentToMCString(val, comp);
            args.add(arg);
        }
    	return "("+toMCString(args, functionClass)+")";
    }
    
    private String toMCString(java.util.Vector<String> args, String functionClass) throws SmartFrogResolutionException {
    	String result = "";
    	
    	if (args.size()==1) result=getFunctionRepresentation(functionClass)+args.get(0);
    	else {
	    	for (int i=0;i<args.size();i++){
	    		if (i>0) result+=getFunctionRepresentation(functionClass);
	    		result+=args.get(i);
	    	}
    	}
    	return getFinalFunctionRepresentation(functionClass, result);
    }	
    
    /**
     * 
     */
    private String toMCString(SFApplyReference apr, ComponentDescription parent) throws SmartFrogResolutionException {
        ComponentDescription comp = apr.getComponentDescription();
        comp.setParent(parent);
        
        try {
            functionClass = (String) comp.sfResolveHere("sfFunctionClass");
        } catch (ClassCastException e) {
            throw new SmartFrogFunctionResolutionException("function class is not a string", e);
        }

       if (functionClass == null) {
            throw new SmartFrogFunctionResolutionException("unknown function class ");
        }
       
        return toMCString(functionClass, comp);
    }
}
