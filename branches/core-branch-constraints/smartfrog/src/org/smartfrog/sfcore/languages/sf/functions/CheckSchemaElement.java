/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/

package org.smartfrog.sfcore.languages.sf.functions;

import org.smartfrog.sfcore.common.SmartFrogAssertionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogLazyResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.languages.sf.sfreference.SFReference;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.security.SFClassLoader;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;



/**
 * Defines the basic schema implementation.
 */
public class CheckSchemaElement extends BaseFunction {

    /**
     * Method to check if a class is compliant with predicate sfClass attribute.
     * It thorws an exception if it failes to check the attributes.
     * @param value the value of the attribute to check
     * @param optional boolean that indicates if the attributes is optional
     * @param binding type of binding for the class
     * @param schemaClass class type that is specified in the schema
     * @param description description for the schema entry
     * @param errorString error string used to prefix error messages
     * @throws SmartFrogAssertionResolutionException failed to check the attributes
     */
    private void checkSchemaClass(Object value, String name,
                                  boolean optional, String binding,
                                  Object schemaClass, String description,
                                  String errorString) throws SmartFrogAssertionResolutionException {
        if (value == null) {
           if (optional) {
               return;
           } else {
               throw new SmartFrogAssertionResolutionException (
                     errorString + "non-optional attribute "+ getNameAndDescription(name,description)+" is missing" , null, null, null
                    );
           }
        }

        // we know value exists...
        try {
            String testvalueClass = value.getClass().getName();

            if (testvalueClass.equals("org.smartfrog.sfcore.languages.sf.sfreference.SFReference") ||
                 testvalueClass.equals("org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference")    ) {
                boolean condition =
                    binding.equals("lazy") ||
                    binding.equals("anyBinding") ||
                    schemaClass.equals("anyClass") ||
                    (binding.equals("eager") &&  isValidClass(schemaClass,value));

                if (!condition)
                    throw new SmartFrogAssertionResolutionException (
                           "errorString + (lazy) reference value for non-reference eager attribute " + getNameAndDescription(name,description), null, null, null
                        );
            } else {
                if (binding.equals("lazy"))
                    throw new SmartFrogAssertionResolutionException (
                           errorString + "non-reference value found for lazy attribute " + getNameAndDescription(name,description)+"",
                           null, null, null
                        );
                  //else if (!(valueClass.equals("anyClass")) && !(SFClassLoader.forName(valueClass).isAssignableFrom(testvalue.getClass())))
                  else if (!(isValidClass(schemaClass,value)))
                    throw new SmartFrogAssertionResolutionException (
                           errorString + "wrong class found for attribute " + getNameAndDescription(name,description)+ ", expected: " + schemaClass + ", found: " + testvalueClass,
                           null, null, null
                        );
            }
        } catch (Throwable e) {
            if (!(e instanceof SmartFrogAssertionResolutionException))
                throw new SmartFrogAssertionResolutionException (
                     "error checking attribute " + getNameAndDescription(name,description), e, null, null
                    );
            else
                throw (SmartFrogAssertionResolutionException)e;
        }
    }

    /**
     * Checks an object class against a schema class(String)
     * or classes (Vector of Strings)
     * @param schemaClass class specified in the schema/predicate definition.
     * It has to be a Vector of Strings or a String. The strings have to be the
     * name of valid existing (codebase or classpath) classes
     * @param foundClassToValidate object which class has to be validated against
     * the predicate
     * @return if the class found is complaint with schema or not.
     */
    private boolean isValidClass (Object schemaClass, Object foundClassToValidate)
       throws java.lang.ClassNotFoundException, SmartFrogAssertionResolutionException {
        if (schemaClass instanceof String ) {
           return isValidClass ((String) schemaClass, foundClassToValidate);
        } else if (schemaClass instanceof Vector ){
            Vector schemaClassV = (Vector) schemaClass;
            for (Enumeration keys = schemaClassV.elements(); keys.hasMoreElements(); ) {
               if (isValidClass(keys.nextElement().toString(),foundClassToValidate)){
                   return true;
               }
            }
        } else{
            throw new SmartFrogAssertionResolutionException (
                      " wrong type in Class schema attribute. Only String or Vector [String,..] are allowed for attribute " +
                        name);
        }
        return false;
    }

    /**
     * Checks if an object class is valid when compared with a class string name
     * @param schemaClass String name for the predicate class
     * @param foundClassToValidate Object which class has to be validated
     * @return true if object class is equal or descedant from schema class.
     * @throws java.lang.ClassNotFoundException
     */
    private boolean isValidClass (String schemaClass, Object foundClassToValidate) throws java.lang.ClassNotFoundException {
        return ((schemaClass.equals("anyClass"))
                ||
                (SFClassLoader.forName(schemaClass).isAssignableFrom(foundClassToValidate.getClass())));
    }

    /**
     * Composes a string using name and description strings
     * @param name for an attribute
     * @param description for the attribute
     * @return string 'name' or 'name(description)'
     */
    private String getNameAndDescription (Object name, String description){
          if (description.equals(""))
              return "'"+name+"'";
          else
              return "'"+name+" ("+description+")"+"'";
    }
    /**
     * Applies predicates.
     * @throws SmartFrogAssertionResolutionException if fail to apply predicates.
     */
    protected Object doFunction() throws SmartFrogAssertionResolutionException {
        String name = (String) context.get("name");

        //System.out.println("checking schema " + name);

        Object value = null;
        Reference attributeRef = new Reference();
        attributeRef.addElement(ReferencePart.parent());
        attributeRef.addElement(ReferencePart.here(name));
        try {
            if (rr != null) {
                value = rr.sfResolve(attributeRef);
            } else if (rrr != null) {
                value = rrr.sfResolve(attributeRef);
            }
        } catch (SmartFrogLazyResolutionException e) {
            value = new SFReference();
            ((Reference)value).setEager(false); // mock up a lazy reference...
        } catch (SmartFrogResolutionException e) {
            // leave as value = null;
        } catch (RemoteException e) {
            return Boolean.FALSE; // treat as a failure
        }
        //System.out.println("checking schema " + name + " with value class " + ((value != null)?value.getClass():null));

        String description = (String) context.get("description");
        if (description == null) description = "";

        String errorString = "error in schema: ";
        //errorString = errorString +
        //   (description.equals("") ? ": ": "(" + description + "): " ) ;

        String binding = (String)context.get("binding");
        if (!(binding.equals("anyBinding") || binding.equals("eager") || binding.equals("lazy") )) {
            throw new SmartFrogAssertionResolutionException (
                     errorString + "binding not valid value for attribute '" + name + "'", null, null, null
            );
        }
        Object schemaClass = context.get("class");
        boolean optional = ((Boolean)context.get("optional")).booleanValue();

        checkSchemaClass(value, name, optional, binding, schemaClass, description, errorString);
        return Boolean.TRUE;
    }

}
