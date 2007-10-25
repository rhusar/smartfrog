package org.smartfrog.sfcore.reference;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.SFSystem;

import java.lang.reflect.Field;

/**
 * Implements the constant reference part. This part resolves to the value
 * of a static constamt defined in a java class.
 * The string value is the transformed to an object using default the parser.
 * References are not forwarded from here, so
 * having this part in the middle of a reference does NOT make sense!
 *
 */
public class ConstantReferencePart extends ReferencePart {
    /** Base string representation of this part (@value). */
    public static final String CONST = "CONSTANT";


    public String value;

    /**
     * Constructs with a class.fieldname string.
     *
     * @param v value for property
     */
    public ConstantReferencePart(String v) {
        value = v;
    }

    /**
     * Returns a string representation of the reference part.
     * Overrides HereReferencePart.toString.
     * @return stringified reference part
     */
    public String toString() {
        return CONST + ' ' + value.toString();
    }

    /**
     * Returns hashcode of this part. This is the hashCode of the stored value
     * plus the CONST hashcode.
     *
     * @return hash code for part
     */
    public int hashCode() {
        return CONST.hashCode() + value.hashCode();
    }

    /**
     * Compares this reference part with another one. Equality means that the
     * type and value are equal
     *
     * @param refPart to be compared to
     *
     * @return true if equal, false if not
     */
    public boolean equals(Object refPart) {
        return refPart.getClass().equals(this.getClass()) &&
        ((ConstantReferencePart) refPart).value.equals(value);
    }



   private String classpart(String c)  throws SmartFrogResolutionException {
      String cl = "";
      try {
         cl = c.substring(0, c.lastIndexOf('.'));
      } catch (Exception e) {
         throw new SmartFrogResolutionException("illegal class name syntax in Constant Reference Part: " + c);
      }
      return cl;
   }

   private String fieldpart(String f)  throws SmartFrogResolutionException {
      String fl = "";
      try {
         fl = f.substring(f.lastIndexOf('.')+1, f.length());
      } catch (Exception e) {
         throw new SmartFrogResolutionException("illegal class name syntax in Constant Reference Part: " + f);
      }
      return fl;
   }

    /**
     * Resolves this reference part using the reference resolver. The
     * originating reference and index are needed to enable request forwarding
     *
     * @param rr reference resolver
     * @param r reference which this part sits in
     * @param index index of this reference part in r
     *
     * @return the attribute found on resolution
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException if failed to resolve reference
     */
    public Object resolve(ReferenceResolver rr, Reference r, int index)
            throws SmartFrogResolutionException {
        try {
            Class c = SFClassLoader.forName(classpart(value));
            Field f = c.getField(fieldpart(value));
            return f.get(null);
        } catch (Throwable ex) {
            throw (SmartFrogResolutionException) SmartFrogResolutionException.forward(ex.toString(), r, ex);
        }
    }

    /**
     * Resolves this reference part using the remote reference resolver. The
     * originating reference and index are needed to enable request forwarding
     *
     * @param rr reference resolver
     * @param r reference which this part sits in
     * @param index index of this reference part in r
     *
     * @return the attribute found on resolution
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException if failed to resolve reference
     */
    public Object resolve(RemoteReferenceResolver rr, Reference r, int index)
       throws SmartFrogResolutionException {
        try {
            Class c = SFClassLoader.forName(classpart(value));
            Field f = c.getField(fieldpart(value));
            return f.get(null);
        } catch (Throwable ex) {
            throw (SmartFrogResolutionException) SmartFrogResolutionException.forward(ex.toString(), r, ex);
        }
    }
}
