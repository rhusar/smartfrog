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

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.PhaseAction;
import org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

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
        String valueClass = "anyClass";
        String description = "";

        String errorString = "error in schema (" + schemaDescription + "): ";

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
                     errorString + "binding not valid value for attribute " + name, null, ref, "predicate", null
                    );
        } catch (Throwable e) {
            if (!(e instanceof SmartFrogCompileResolutionException))
                throw new SmartFrogCompileResolutionException(
                     errorString + "error reading binding for attribute " + name, e, ref, "predicate", null
                    );
            else
                throw (SmartFrogCompileResolutionException)e;
        }

        try {
            valueClass = (String) predicate.sfResolve(classRef);
        } catch (Throwable e) {
            throw new SmartFrogCompileResolutionException (
                     errorString + "error reading class for attribute " + name, e, ref, "predicate", null
                    );
        }

        try {
            description = (String) predicate.sfResolve(descriptionRef);
        } catch (Throwable e) {
            description = "";
        }

        try {
            try {
                Object testvalue = attributes.sfResolve(new Reference(ReferencePart.here(name)));
                String testvalueClass = testvalue.getClass().getName();

                if (testvalueClass.equals("org.smartfrog.sfcore.reference.Reference")) {
                    boolean condition =
                        binding.equals("lazy") ||
                        binding.equals("anyBinding") ||
                        valueClass.equals("anyClass") ||
                        (binding.equals("eager") &&  valueClass.equals(testvalueClass));

                    if (!condition)
                        throw new SmartFrogCompileResolutionException (
                               "errorString + (lazy) reference value for non-reference eager attribute " + name + " (" + description + ")", null, ref, "predicate", null
                            );
                } else {
                    if (binding.equals("lazy"))
                        throw new SmartFrogCompileResolutionException (
                               errorString + "non-reference value found for lazy attribute " + name,
                               null, ref, "predicate", null
                            );
                    else if (!(valueClass.equals("anyClass") || valueClass.equals(testvalueClass)))
                        throw new SmartFrogCompileResolutionException (
                               errorString + "wrong class found for attribute " + name + " (" + description + "), expected: " + valueClass + ", found: " + testvalueClass,
                               null, ref, "predicate", null
                            );
                }


            } catch (SmartFrogResolutionException re) {
                if (!optional) {
                    throw new SmartFrogCompileResolutionException (
                     errorString + "non-optional attribute is missing: " + name + " (" + description + ")", null, ref, "predicate", null
                    );
                }
            }
        } catch (Throwable e) {
            if (!(e instanceof SmartFrogCompileResolutionException))
                throw new SmartFrogCompileResolutionException (
                     "error checking attribute " + name + " (" + description + ")", e, ref, "predicate", null
                    );
            else
                throw (SmartFrogCompileResolutionException)e;
        }
    }
    /**
     * Applies predicates.
     * @throws SmartFrogCompileResolutionException if fail to apply predicates.
     */
    protected void doPredicate() throws SmartFrogCompileResolutionException {
        String description = "";

        ref = component.getCompleteName();
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
