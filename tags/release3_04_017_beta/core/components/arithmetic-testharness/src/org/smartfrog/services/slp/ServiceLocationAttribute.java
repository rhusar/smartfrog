package org.smartfrog.services.slp;

import java.util.*;

/**
 * This class describes the attributes used in service advertisements.
 *
 * @author Guillaume Mecheneau
 */
public class ServiceLocationAttribute {
  private String id;
  private Vector values;

/**
 * Constructor
 * @param id - The attribute name
 * @param values - Vector of one or more attribute values. Vector contents must be uniform in type
 * and one of Integer, String, Boolean, or byte[].
 * If the attribute is a keyword attribute, then values should be null.
 * @throw IllegalArgumentException if the id is null or empty.
 */
  public ServiceLocationAttribute(String id,
                                 Vector values) throws IllegalArgumentException {
   // System.out.println("Creating attribute "+ id + " with value = "+ values);
    if ((id == null) || (id.equals("")))
      throw new IllegalArgumentException(" Attribute id empty ");
    this.id = id;
    this.values = values;
  }

/**
 * A vector of attribute values, or null if the attribute is a keyword attribute.
 * If the attribute is single-valued, then the vector contains only one object.
 * @return a Vector of values for this attribute.
 */
  public Vector getValues(){
    if (values != null)
      return (Vector) this.values.clone();
    return null;
  }

/**
 * Return the attribute name.
 * @ the id of this attribute.
 */
  public String getId() {
    return this.id;
  }


/**
 * Return true if the object equals this attribute. The object and the attribute
 * are equal if:
 * - they both are ServiceLocationAttribute
 * - the ids are the same
 * - the two Vectors have the same content.
 * @param o the object to compare.
 */
  public boolean equals(Object o) {
    // check class
    if (o instanceof ServiceLocationAttribute) {
      // check attribute id
      ServiceLocationAttribute sla = (ServiceLocationAttribute) o;
      if (this.id == sla.getId()){
      // check if values is null
        Vector slaValues = sla.getValues();
        if (getValues() == null) {
          return (slaValues==null);
        } else {
        // then check values Vector size
          if (getValues().size() == slaValues.size()){
            // check values
            for (Enumeration e = getValues().elements() ; e.hasMoreElements() ; ){
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
 * @return a String describing this attribute.
 */
  public String toString(){
    String res = this.id + "=";
    for (Enumeration e = values.elements(); e.hasMoreElements();){
      Object o = e.nextElement();
      res += o.toString();// + " of Type " + o.getClass()+ "; " ;
    }
    return res ;
  }


}
