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

package org.smartfrog.sfcore.languages.sf.predicates;

import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.PhaseAction;
import org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.security.SFClassLoader;



/**
 * Defines the basic schema implementation.
 */
public class Schema extends BasePredicate implements PhaseAction {

    Reference ref;

    /**
     * Schema description attribute.
     */
    private String schemaDescription = "sfSchemaDescription";

    /**
     * Optional reference.
     */
    private Reference optionalRef = new Reference(ReferencePart.here("optional"));
    /**
     * Binding reference.
     */
    private Reference bindingRef = new Reference(ReferencePart.here("binding"));

    /**
     * Class reference.
     */
    private Reference classRef = new Reference(ReferencePart.here("class"));
    /**
     * Description reference.
     */
    private Reference descriptionRef = new Reference(ReferencePart.here("description"));

    /**
     * Resolves the attributes for a specific schemadescription.
     *
     * @param name the name attribute
     * @param predicate component description
     * @param attributes attributes of component description
     * @param schemaDescription schema description
     *
     * @throws SmartFrogCompileResolutionException failed to check the
     * attributes
     */
    protected void checkAttribute(Object name, ComponentDescription predicate, ComponentDescription attributes, String schemaDescription)
                            throws SmartFrogCompileResolutionException {
        boolean optional = true;
        String binding = "anyBinding";
        Object valueClass = "anyClass";
        String description = "";

        String errorString = "error in schema";
        errorString = errorString +
           (schemaDescription.equals("") ? ": ": "(" + schemaDescription + "): " ) ;
        try {
            optional = ((Boolean) predicate.sfResolve(optionalRef)).booleanValue();
        } catch (Throwable e) {
            throw new SmartFrogCompileResolutionException(
                     errorString + "error reading optionality for attribute " + name, e, ref, "predicate", null
                    );
        }

        try {
            binding = (String) predicate.sfResolve(bindingRef);
            if (!(binding.equals("lazy") || binding.equals("eager") || binding.equals("anyBinding")))
                throw new SmartFrogCompileResolutionException (
                     errorString + "binding not valid value for attribute '" + name+"'", null, ref, "predicate", null
                    );
        } catch (Throwable e) {
            if (!(e instanceof SmartFrogCompileResolutionException))
                throw new SmartFrogCompileResolutionException(
                     errorString + "error reading binding for attribute '" + name+"'", e, ref, "predicate", null
                    );
            else
                throw (SmartFrogCompileResolutionException)e;
        }

        try {
            valueClass =  predicate.sfResolve(classRef);
        } catch (Throwable e) {
            throw new SmartFrogCompileResolutionException (
                     errorString + "error reading class for attribute '" + name+"'", e, ref, "predicate", null
                    );
        }

        try {
            description = (String) predicate.sfResolve(descriptionRef);
        } catch (Throwable e) {
            description = "";
        }

        checkSchemaClass(name,attributes, optional, binding,
                             valueClass, description, errorString);
    }

    /**
     * Method to check if a class is compliant with predicate sfClass attribute.
     * It thorws an exception if it failes to check the attributes.
     * @param name the name attribute
     * @param attributes attributes of component description
     * @param optional boolean that indicates if the attributes is optional
     * @param binding type of binding for the class
     * @param schemaClass class type that is specified in the schema
     * @param description description for the schema entry
     * @param errorString error string used to prefix error messages
     * @throws SmartFrogCompileResolutionException failed to check the attributes
     */
    private void checkSchemaClass(Object name, ComponentDescription attributes,
                                  boolean optional, String binding,
                                  Object schemaClass, String description,
                                  String errorString) throws SmartFrogCompileResolutionException {
        try {
            try {

                Object testValue = attributes.sfResolve(new Reference(ReferencePart.here(name)));
                String testvalueClass = testValue.getClass().getName();

                if (testvalueClass.equals("org.smartfrog.sfcore.reference.Reference")) {
                    boolean condition =
                        binding.equals("lazy") ||
                        binding.equals("anyBinding") ||
                        schemaClass.equals("anyClass") ||
                        (binding.equals("eager") &&  isValidClass(schemaClass,testValue));

                    if (!condition)
                        throw new SmartFrogCompileResolutionException (
                               "errorString + (lazy) reference value for non-reference eager attribute " + getNameAndDescription(name,description), null, ref, "predicate", null
                            );
                } else {
                    if (binding.equals("lazy"))
                        throw new SmartFrogCompileResolutionException (
                               errorString + "non-reference value found for lazy attribute " + getNameAndDescription(name,description)+"",
                               null, ref, "predicate", null
                            );
                      //else if (!(valueClass.equals("anyClass")) && !(SFClassLoader.forName(valueClass).isAssignableFrom(testvalue.getClass())))
                      else if (!(isValidClass(schemaClass,testValue)))
                        throw new SmartFrogCompileResolutionException (
                               errorString + "wrong class found for attribute " + getNameAndDescription(name,description)+ ", expected: " + schemaClass + ", found: " + testvalueClass,
                               null, ref, "predicate", null
                            );
                }

            } catch (SmartFrogResolutionException re) {
                if (!optional) {
                    throw new SmartFrogCompileResolutionException (
                     errorString + "non-optional attribute "+ getNameAndDescription(name,description)+" is missing" , null, ref, "predicate", null
                    );
                }
            }
        } catch (Throwable e) {
            if (!(e instanceof SmartFrogCompileResolutionException))
                throw new SmartFrogCompileResolutionException (
                     "error checking attribute " + getNameAndDescription(name,description), e, ref, "predicate", null
                    );
            else
                throw (SmartFrogCompileResolutionException)e;
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
     * @throws java.lang.ClassNotFoundExceptionx
     */
    protected boolean isValidClass (Object schemaClass, Object foundClassToValidate)
       throws java.lang.ClassNotFoundException, SmartFrogException {
        if (schemaClass instanceof String ) {
           return isValidClass ((String) schemaClass, foundClassToValidate);
        } else if (schemaClass instanceof String ){
            Vector schemaClassV = (Vector) schemaClass;
            for (Enumeration keys = schemaClassV.elements(); keys.hasMoreElements(); ) {
               if (isValidClass(keys.nextElement().toString(),foundClassToValidate)){
                   return true;
               }
            }
        } else{
            throw new SmartFrogException (
                      " wrong type in Class schema attribute. Only String or Vector [String,..] are allowed");
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
    protected boolean isValidClass (String schemaClass, Object foundClassToValidate) throws java.lang.ClassNotFoundException {
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
    protected String getNameAndDescription (Object name, String description){
          if (description.equals(""))
              return "'"+name+"'";
          else
              return "'"+name+" ("+description+")"+"'";
    }
    /**
     * Applies predicates.
     * @throws SmartFrogCompileResolutionException if fail to apply predicates.
     */
    protected void doPredicate() throws SmartFrogCompileResolutionException {
        String description = "";

        ref = component.sfCompleteName();
        ComponentDescription parent = (ComponentDescription) component.getParent();


        try {
            description = (String) context.get(schemaDescription);
        } catch (Throwable e) {
            description = "";
        }
        if (description == null) description = "";


        for (Enumeration keys = context.keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = context.get(key);
            try {
                if (!key.equals(schemaDescription) && !key.toString().startsWith("phase")) {
                    if (value instanceof ComponentDescription)
                        checkAttribute(key, (ComponentDescription)value, parent, description);
                    else
                        throw new SmartFrogCompileResolutionException (
                                 "schema attribute " + key + " is not appropriate for the schema " + description, null, ref, "predicate", null
                                 );
                }
            } catch (Throwable e) {
                e.printStackTrace();
                if (!(e instanceof SmartFrogCompileResolutionException))
                    throw new SmartFrogCompileResolutionException (
                        "unknown error in checking schema (" + description + ")" , e, ref, "predicate", null
                       );
                else
                    throw (SmartFrogCompileResolutionException)e;
            }
        }
    }

}
