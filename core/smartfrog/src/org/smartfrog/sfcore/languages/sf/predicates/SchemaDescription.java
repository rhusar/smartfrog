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

import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.PhaseAction;
import org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

/**
 * Defines the basic schema description implementation.
 */
public class SchemaDescription extends BasePredicate implements PhaseAction {

    Reference ref;
    Reference parentref;

    private String schemaDescription = SmartFrogCoreKeys.SF_SCHEMA_DESCRIPTION;

    private Reference optionalRef = new Reference(ReferencePart.here("optional"));
    private Reference bindingRef = new Reference(ReferencePart.here("binding"));
    private Reference classRef = new Reference(ReferencePart.here("class"));
    private Reference descriptionRef = new Reference(ReferencePart.here("description"));

    /**
     * Describes the attributes of a schema.
     *
     * @param name the attribute name
     * @param predicate component description
     * @param attributes the attributes of component description
     *
     * @throws SmartFrogCompileResolutionException failed to describe attributes
     */
    protected void describeAttribute(Object name, ComponentDescription predicate, ComponentDescription attributes)
                            throws SmartFrogCompileResolutionException {

        boolean optional = true;
        String binding = "anyBinding";
        String valueClass = "anyClass";
        String description = "";

        // get the optionality
        try {
            optional = ((Boolean) predicate.sfResolve(optionalRef)).booleanValue();
        } catch (Throwable e) {
            throw new SmartFrogCompileResolutionException(
                     "error reading optionality for attribute " + name, e, ref, "description", null
                    );
        }


        // get the binding type
        try {
            binding = (String) predicate.sfResolve(bindingRef);
            if (!(binding.equals("lazy") || binding.equals("eager") || binding.equals("anyBinding")))
                throw new SmartFrogCompileResolutionException (
                     "binding not valid value for attribute " + name, null, ref, "description", null
                    );
        } catch (Throwable e) {
            if (!(e instanceof SmartFrogCompileResolutionException))
                throw new SmartFrogCompileResolutionException(
                     "error reading binding for attribute " + name, e, ref, "description", null
                    );
            else
                throw (SmartFrogCompileResolutionException)e;
        }


        // get the class
        try {
            valueClass = (String) predicate.sfResolve(classRef);
        } catch (Throwable e) {
            throw new SmartFrogCompileResolutionException (
                     "error reading class for attribute " + name, e, ref, "description", null
                    );
        }


        // get the description
        try {
            description = (String) predicate.sfResolve(descriptionRef);
        } catch (Throwable e) {
            description = "";
        }

        // get the value, and print the attribute description
        if (!description.equals("")) {
            Object testValue = null;
            try {
                testValue = attributes.sfResolve(new Reference(ReferencePart.here(name)));
            } catch (SmartFrogResolutionException re) {
                if (!optional) {
                    throw new SmartFrogCompileResolutionException (
                         "non-optional attribute is missing: " + name + " (" + description + ")",
                          null, ref, "description", null
                         );
                }
            }

            System.out.print("    ");
            System.out.print(description);
            System.out.print(" (");
            System.out.print(name);
            System.out.print("):    ");
            if (testValue == null)
                System.out.print("--not set--");
            else
                System.out.print(testValue);
            System.out.print("\n");
        }
    }


    /**
     * Applies predicates.
     * @throws SmartFrogCompileResolutionException if error while applying
     * predicates
     */

    protected void doPredicate() throws SmartFrogCompileResolutionException {
        String description = "";

        ref = component.sfCompleteName();
        ComponentDescription parent = (ComponentDescription) component.sfParent();
        parentref = parent.sfCompleteName();

        try {
            description = (String) context.get(schemaDescription);
        } catch (Throwable e) {
            description = "";
        }
        if (description == null) description = "";


        System.out.print("component " + parentref);
        System.out.println(":  " + description + "\n");

        for (Enumeration keys = context.keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = context.get(key);
            try {
                if (!key.equals(schemaDescription))
                    describeAttribute(key, (ComponentDescription)value, parent);
            } catch (Throwable e) {
                e.printStackTrace();
                if (!(e instanceof SmartFrogCompileResolutionException))
                    throw new SmartFrogCompileResolutionException (
                        "unknown error in checking schema (" + description + ")" , e, ref, "description", null
                       );
                else
                    throw (SmartFrogCompileResolutionException)e;
            }
        }
        System.out.println("\n\n\n");

    }

}
