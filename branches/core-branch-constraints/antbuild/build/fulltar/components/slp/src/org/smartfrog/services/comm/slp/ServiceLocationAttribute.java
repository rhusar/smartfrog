/*
Service Location Protocol - SmartFrog components.
 Copyright (C) 1998-2003 Hewlett-Packard Development Company, LP
 Copyright (C) 2004 Glenn Hisdal <ghisdal(a)c2i.net>
 
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
 
 This library was originally developed by Glenn Hisdal at the 
 European Organisation for Nuclear Research (CERN) in Spring 2004. 
 The work was part of a master thesis project for the Norwegian 
 University of Science and Technology (NTNU).
 
 For more information: http://home.c2i.net/ghisdal/slp.html 
*/

package org.smartfrog.services.comm.slp;

import org.smartfrog.services.comm.slp.util.SLPUtil;

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class describes the attributes used in service advertisements.
 *
 * @author Guillaume Mecheneau
 */
public class ServiceLocationAttribute {
    private static String reservedChars = "(),\\!<=>~";
    private String id;
    private Vector values;

    /**
     * Constructor
     *
     * @param id     - The attribute name
     * @param values - Vector of one or more attribute values. Vector contents must be uniform in type and one of Integer,
     *               String, Boolean, or byte[]. If the attribute is a keyword attribute, then values should be null.
     * @throws IllegalArgumentException if the id is null or empty.
     */
    public ServiceLocationAttribute(String id,
                                    Vector values) throws IllegalArgumentException {
        // System.out.println("Creating attribute "+ id + " with value = "+ values);
        if ((id == null) || (id.equals(""))) {
            throw new IllegalArgumentException(" Attribute id empty ");
        }
        this.id = id;
        this.values = values;
    }

    /**
     * A vector of attribute values, or null if the attribute is a keyword attribute. If the attribute is single-valued,
     * then the vector contains only one object.
     *
     * @return a Vector of values for this attribute.
     */
    public Vector getValues() {
        if (values != null) {
            return (Vector) this.values.clone();
        }
        return null;
    }

    /**
     * Return the attribute name.
     *
     * @return the id of this attribute.
     */
    public String getId() {
        return this.id;
    }

    public void setValues(Vector v) {
        values = (Vector) v.clone();
    }


    /**
     * Return true if the object equals this attribute. The object and the attribute are equal if: - they both are
     * ServiceLocationAttribute - the ids are the same - the two Vectors have the same content.
     *
     * @param o the object to compare.
     */
    public boolean equals(Object o) {
        // check class
        if (o instanceof ServiceLocationAttribute) {
            // check attribute id
            ServiceLocationAttribute sla = (ServiceLocationAttribute) o;
            if (this.id == sla.getId()) {
                // check if values is null
                Vector slaValues = sla.getValues();
                if (getValues() == null) {
                    return (slaValues == null);
                } else {
                    // then check values Vector size
                    if (getValues().size() == slaValues.size()) {
                        // check values
                        for (Enumeration e = getValues().elements(); e.hasMoreElements();) {
                            Object value = e.nextElement();
                            if (!slaValues.contains(o)) return false;
                        }
                        return true;
                    }// different sizes
                }//can't be reached
            }//different ids
        }// not the right class
        return false;
    }

    /**
     * A string describing this ServiceLocationAttribute.
     *
     * @return a String describing this attribute.
     */
    public String toString() {
        String res = this.id + "=";
        for (Enumeration e = values.elements(); e.hasMoreElements();) {
            Object o = e.nextElement();
            res += o.toString();// + " of Type " + o.getClass()+ "; " ;
        }
        return res;
    }

    public static String escapeId(String id) {
        return SLPUtil.escapeString(id, reservedChars);
    }

    public static String escapeValue(Object value) {
        String v = value.toString();
        if (v.startsWith("[") && v.endsWith("]")) {
            // remove brackets from string.
            v = v.substring(1, v.length() - 1);
        }
        return SLPUtil.escapeString(v, reservedChars);
    }

}
