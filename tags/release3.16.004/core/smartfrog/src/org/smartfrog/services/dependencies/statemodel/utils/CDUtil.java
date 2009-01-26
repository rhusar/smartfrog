package org.smartfrog.services.dependencies.statemodel.utils;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferenceResolver;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.common.*;

/**
 * CDUtil contains a few utility methods for performing operations on component descriptions.
 * More general versions of these methods are being implemented elsewhere in SmartFrog so it is
 * expected that these will go away.
 *
 * @author not attributable
 * @version 1.0
 */
public class CDUtil {

    /**
     * Make a copy of a component description
     *
     * @param cd ComponentDescription
     * @return ComponentDescription
     */
    static public ComponentDescription copy(ComponentDescription cd) {
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
                ComponentDescription cdValCopy = copy((ComponentDescription)val);
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


    
    /**
     * Same evaluates whether the two attribute values are the same: component descriptions are
     * compared recursively, values that are lazy references are compared after dereferencing, 
     * but the method assumes that the initial object parameters are not lazy references. This 
     * method returns false if there is a resolution exception.
     * 
     * @param obj1
     * @param obj2
     * @return whether two attribute values are the same
     * @throws SmartFrogResolutionException 
     */
    static public boolean same(Object obj1, Object obj2) {
    	try {
			return same0(obj1, obj2);
		} catch (SmartFrogResolutionException e) {
			return false;
		}
    }
    
    
    /**
     * Same evaluates whether the two attribute values are the same: component descriptions are
     * compared recursively, values that are lazy references are compared after dereferencing, 
     * and if the initial object parameters are lazy references they will be resolved against
     * the given reference resolver.
     *
     * @param obj1
     * @param obj2
     * @return whether two attribute values are the same
     * @throws SmartFrogResolutionException 
     */
    static public boolean same(Object obj1, Object obj2, ReferenceResolver rr) throws SmartFrogResolutionException {
    	
    	Object val1 = obj1;
    	Object val2 = obj2;
    	
		if( obj1 instanceof Reference ) {
			val1 = rr.sfResolve( (Reference)obj1 );
		}
		if( obj2 instanceof Reference ) {
			val2 = rr.sfResolve( (Reference)obj2 );
		}
    	
    	if( !(val1 instanceof ComponentDescription) ) {
    		return val1.equals(val2);
    	} else if( val2 instanceof ComponentDescription ) {
    		return Boolean.valueOf( same0(val1, val2) );
    	} else  {
    		return false;
    	}
	}
    
    
    /**
     * This method assumes the component descriptions do not
     * contain any lazy references
     * @param obj1 Object
     * @param obj2 Object
     * @return boolean whether two attribute values are the same
     * @throws SmartFrogResolutionException 
     */
    static public boolean same0(Object obj1, Object obj2) throws SmartFrogResolutionException  {


    	/**
         * If one is a component description and one not, they are not
         * the same
         */
        if( (obj1 instanceof ComponentDescription) !=
            (obj2 instanceof ComponentDescription) ) {
            return false;
        }
        
        
        /**
         * If they are not component descriptions then test for
         * equality (at this point they can not be references)
         */
        if( !(obj1 instanceof ComponentDescription) ) {
            boolean equal = obj1.equals(obj2);
            return equal;
        }


        ComponentDescription cd1 = ((ComponentDescription)obj1); 
        ComponentDescription cd2 = ((ComponentDescription)obj2); 
        Context ctx1 = cd1.sfContext();
        Context ctx2 = cd2.sfContext();


        /**
         * Get the set of all keys
         */
        Set keys = new HashSet();
        keys.addAll( ((ContextImpl)ctx1).keySet() );
        keys.addAll( ((ContextImpl)ctx2).keySet() );
        
        
        /**
         * check the entries
         */
        for (Iterator keyIter = keys.iterator(); keyIter.hasNext();) {
            String k = (String)keyIter.next();
            
            
            /**
             * Ignore anything that begins with sf or schema
             */
            if( k.startsWith("sf") || k.startsWith("schema") ) {
            	continue;
            }
            

            /**
             * If not in both contexts, the descriptions are not the same
             */
            if( !ctx1.containsKey(k) || !ctx2.containsKey(k) ) {
                return false;
            }
            
            System.out.println("checking attributes named " + k + " for equality");
            
            /**
             * get both values
             */
            Object val1 = ctx1.get(k);
            Object val2 = ctx2.get(k);
            
            /**
             * If references then resolve
             */
            if( val1 instanceof Reference ) {
            	val1 = cd1.sfResolve( ( Reference ) val1 );
            }
            if( val2 instanceof Reference ) {
            	val2 = cd2.sfResolve( ( Reference ) val2 );
            }

            /**
             * If the values are not the same the desciptions are not the same
             */
            if( !same0( val1, val2 ) ) {
                return false;
            }
        }

        return true;
    }
}
