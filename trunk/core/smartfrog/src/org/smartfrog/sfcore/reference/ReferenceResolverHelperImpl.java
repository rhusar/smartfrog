package org.smartfrog.sfcore.reference;


import java.util.Vector;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;


 public abstract class ReferenceResolverHelperImpl extends Object  {
    protected ReferenceResolverHelperImpl() {
    }


    /**
     * Returns the complete name for this component from the root of the
     * application and does not throw any exception. If an exception is
     * thrown it will return a new empty reference.
     *
     * @return reference of attribute names to this component or an empty reference
     *
     */
    public abstract Reference sfCompleteNameSafe();


    //
    // ReferenceResolver
    //

    /**
     * Resolves a given reference. Forwards to indexed resolve with index 0
     * and return resulting attribute value.
     *
     * @param r reference to resolve
     *
     * @return resolved reference
     *
     * @throws SmartFrogResolutionException occurred while resolving
     *
     */
    public abstract Object sfResolve(Reference r)
        throws SmartFrogResolutionException;


    /**
     * Resolves given reference starting at given index. This is forwarded to
     * the reference (and on to each reference part).
     *
     * @param r reference to resolve
     * @param index index in reference to start resolving
     *
     * @return resolved reference
     *
     * @throws SmartFrogResolutionException error occurred while resolving
     *
     */
    public abstract Object sfResolve(Reference r, int index)
        throws SmartFrogResolutionException;


//************************************************************************************
    /**
     * Resolves a referencePart given a string. Utility method to auto-convert
     * from string to reference.
     *
     * NOTE: To resolve a reference from a String using a reference cannonical
     * representation it is neccesary to do:
     * "return sfResolve(Reference.fromString(reference));"so that the parser
     * is invoked.
     *
     * @param referencePart stringified reference
     *
     * @return java Object for attribute value
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public Object sfResolve(String referencePart) throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart));
    }


    /**
     * Resolves a reference given a string. Utility method to auto-convert from
     * string to reference. It can use cannonical representations that are
     * resolved by the parser (parse = true).
     *
     * @param reference string field reference
     *
     * @return java Object for attribute value
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public Object sfResolveWithParser(String reference) throws SmartFrogResolutionException {
        return sfResolve(Reference.fromString(reference));
    }


    /**
     * Resolves given reference and gets a java Object.
     * Utility method to resolve an attribute with a java Object value.
     *
     * @param reference reference
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return java Object for attribute value or null if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public Object sfResolve(Reference reference, boolean mandatory)
            throws SmartFrogResolutionException{
        try {
            Object referenceObj = sfResolve(reference, 0);
            return (referenceObj);
        } catch (SmartFrogResolutionException e) {
            if (mandatory) {
                throw e;
            }
        }
        return null;
    }

    /**
     * Resolves a referencePart given a string and gets a java Object.
     *
     * @param referencePart string field reference

     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Reference for attribute value or null if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public Object sfResolve(String referencePart, boolean mandatory)
        throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), mandatory);
    }

    /**
     * Resolves given reference and gets a boolean. Utility method to resolve
     * an attribute with a boolean value.
     *
     * @param reference reference
     * @param defaultValue boolean default value that is returned when
     *        reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return boolean for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public boolean sfResolve(Reference reference, boolean defaultValue,
        boolean mandatory) throws SmartFrogResolutionException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof Boolean) {
                return (((Boolean) referenceObj).booleanValue());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets an int. Utility method to resolve an
     * attribute with an int value.
     *
     * @param reference reference
     * @param defaultValue int default value that is returned when reference is
     *        not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return int for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public int sfResolve(Reference reference, int defaultValue, boolean mandatory)
        throws SmartFrogResolutionException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof Integer) {
                return (((Integer) referenceObj).intValue());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets an int. Utility method to resolve an
     * attribute with an int value.
     *
     * @param reference reference
     * @param defaultValue int default value that is returned when reference is
     *        not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return int for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable or resolved value &lt; minValue or &gt; maxValue
     *
     */
    public int sfResolve(Reference reference, int defaultValue,Integer minValue, Integer maxValue, boolean mandatory)
        throws SmartFrogResolutionException {
        int value = sfResolve(reference, defaultValue, mandatory);
        if ((minValue!=null)&&(value<minValue.intValue()))
            throw new SmartFrogResolutionException(reference, this.sfCompleteNameSafe(), "Error: sfResolved int '"+value+"' < '"+minValue+"'(minValue)");
        else if ((maxValue!=null)&&(value>maxValue.intValue()))
            throw new SmartFrogResolutionException(reference, this.sfCompleteNameSafe(), "Error: sfResolved int '"+value+"' > '"+maxValue+"'(maxValue)");
        else return value;
    }

    /**
     * Resolves given reference and gets an long. Utility method to resolve an
     * attribute with an long value. Int values are "upcasted" to long.
     *
     * @param reference reference
     * @param defaultValue long default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return long for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public long sfResolve(Reference reference, long defaultValue, boolean mandatory)
        throws SmartFrogResolutionException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if ((referenceObj instanceof Long) ||
                    (referenceObj instanceof Integer)) {
                return (((Number) referenceObj).longValue());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }
        return defaultValue;
    }

    /**
     * Resolves given reference and gets an long. Utility method to resolve an
     * attribute with an long value. Int values are "upcasted" to long.
     *
     * @param reference reference
     * @param defaultValue long default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return long for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable or resolved value &lt; minValue or &gt; maxValue
     *
     */
    public long sfResolve(Reference reference, long defaultValue, Long minValue, Long maxValue, boolean mandatory)
        throws SmartFrogResolutionException {
        long value = sfResolve(reference, defaultValue, mandatory);
        if ((minValue!=null)&&(value<minValue.longValue()))
            throw new SmartFrogResolutionException(reference, this.sfCompleteNameSafe(), "Error: sfResolved long '"+value+"' < '"+minValue+"'(minValue)");
        else if ((maxValue!=null)&&(value>maxValue.longValue()))
            throw new SmartFrogResolutionException(reference, this.sfCompleteNameSafe(), "Error: sfResolved long '"+value+"' > '"+maxValue+"'(maxValue)");
        else return value;
    }
    /**
     * Resolves given reference and gets an float. Utility method to resolve an
     * attribute with an float value. Integer values are "upcasted" to float.
     *
     * @param reference reference
     * @param defaultValue float default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return float for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public float sfResolve(Reference reference, float defaultValue, boolean mandatory)
        throws SmartFrogResolutionException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if ((referenceObj instanceof Float) ||
                    (referenceObj instanceof Integer)) {
                return (((Number) referenceObj).floatValue());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }
        return defaultValue;
    }

    /**
     * Resolves given reference and gets an float. Utility method to resolve an
     * attribute with an float value. Int values are "upcasted" to float.
     *
     * @param reference reference
     * @param defaultValue float default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return float for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable or resolved value &lt; minValue or &gt; maxValue
     *
     */
    public float sfResolve(Reference reference, float defaultValue, Float minValue, Float maxValue, boolean mandatory)
        throws SmartFrogResolutionException {
        float value = sfResolve(reference, defaultValue, mandatory);
        if ((minValue!=null)&&(value<minValue.floatValue()))
            throw new SmartFrogResolutionException(reference, this.sfCompleteNameSafe(), "Error: sfResolved float '"+value+"' < '"+minValue+"'(minValue)");
        else if ((maxValue!=null)&&(value>maxValue.floatValue()))
            throw new SmartFrogResolutionException(reference, this.sfCompleteNameSafe(), "Error: sfResolved float '"+value+"' > '"+maxValue+"'(maxValue)");
        else return value;
    }

    /**
     * Resolves given reference and gets an double. Utility method to resolve an
     * attribute with an double value. Integer values are "upcasted" to double.
     *
     * @param reference reference
     * @param defaultValue double default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return double for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public double sfResolve(Reference reference, double defaultValue, boolean mandatory)
        throws SmartFrogResolutionException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if ((referenceObj instanceof Float) ||
                    (referenceObj instanceof Integer)
                    || (referenceObj instanceof Long)
                    || (referenceObj instanceof Double)) {
                return (((Number) referenceObj).doubleValue());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }
        return defaultValue;
    }

    /**
     * Resolves given reference and gets an double. Utility method to resolve an
     * attribute with an double value. Int values are "upcasted" to double.
     *
     * @param reference reference
     * @param defaultValue double default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return double for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable or resolved value &lt; minValue or &gt; maxValue
     *
     */
    public double sfResolve(Reference reference, double defaultValue, Double minValue, Double maxValue, boolean mandatory)
        throws SmartFrogResolutionException {
        double value = sfResolve(reference, defaultValue, mandatory);
        if ((minValue!=null)&&(value<minValue.doubleValue()))
            throw new SmartFrogResolutionException(reference, this.sfCompleteNameSafe(), "Error: sfResolved double '"+value+"' < '"+minValue+"'(minValue)");
        else if ((maxValue!=null)&&(value>maxValue.doubleValue()))
            throw new SmartFrogResolutionException(reference, this.sfCompleteNameSafe(), "Error: sfResolved double '"+value+"' > '"+maxValue+"'(maxValue)");
        else return value;
    }

    /**
     * Resolves given reference. Utility method to resolve an attribute with a
     * String value.
     *
     * @param reference reference
     * @param defaultValue String default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return String for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public String sfResolve(Reference reference, String defaultValue,
        boolean mandatory) throws SmartFrogResolutionException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof String) {
                return (((String) referenceObj).toString());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a Vector. Utility method to resolve an
     * attribute with a Vector value.
     *
     * @param reference reference
     * @param defaultValue Vector default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Vector for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public Vector sfResolve(Reference reference, Vector defaultValue,
        boolean mandatory) throws SmartFrogResolutionException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof Vector) {
                return (((Vector) referenceObj));
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a String[]. Utility method to resolve
     * an attribute with a Vector value and returns a String[].
     *
     * @param reference reference
     * @param defaultValue String[] default value that is returned when
     *        reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return String[] for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public String[] sfResolve(Reference reference, String[] defaultValue,
        boolean mandatory) throws SmartFrogResolutionException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof Vector) {
                String[] array = null;

                if (!(((Vector) referenceObj).isEmpty())) {
                    ((Vector) referenceObj).trimToSize();
                    array = new String[((Vector) referenceObj).size()];
                    ((Vector) referenceObj).copyInto(array);

                    return (array);
                }
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a SmartFrog ComponentDescription.
     * Utility method to resolve an attribute with a SmartFrog
     * ComponentDescription value.
     *
     * @param reference reference
     * @param defaultValue SmartFrog ComponentDescription default value that is
     *        returned when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return SmartFrog ComponentDescription for attribute value or
     *         defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public ComponentDescription sfResolve(Reference reference,
        ComponentDescription defaultValue, boolean mandatory)
        throws SmartFrogResolutionException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof ComponentDescription) {
                return ((ComponentDescription) referenceObj);
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a SmartFrog Reference.
     * Utility method to resolve an attribute with a SmartFrog
     * Reference value.
     *
     * @param reference reference
     * @param defaultValue SmartFrog Reference default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return SmartFrog Reference for attribute value or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public Reference sfResolve(Reference reference, Reference defaultValue,
        boolean mandatory) throws SmartFrogResolutionException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof Reference) {
                return ((Reference) referenceObj);
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a SmartFrog Prim.
     * Utility method to resolve an attribute with a SmartFrog
     * Prim value.
     *
     * @param reference reference
     * @param defaultValue SmartFrog Prim default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return SmartFrog Prim for attribute value or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public Prim sfResolve(Reference reference, Prim defaultValue,
        boolean mandatory) throws SmartFrogResolutionException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof Prim) {
                return ((Prim) referenceObj);
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a SmartFrog Compound.
     * Utility method to resolve an attribute with a SmartFrog
     * Compound value.
     *
     * @param reference reference
     * @param defaultValue SmartFrog Compound default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return SmartFrog Compound for attribute value or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public Compound sfResolve(Reference reference, Compound defaultValue,
        boolean mandatory) throws SmartFrogResolutionException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof Compound) {
                return ((Compound) referenceObj);
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a java.net.InetAddress.
     * Utility method to resolve an attribute with a SmartFrog
     * java.net.InetAddress value.
     *
     * @param reference reference
     * @param defaultValue java.net.InetAddress default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return java.net.InetAddress for attribute value or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public java.net.InetAddress sfResolve(Reference reference,
        java.net.InetAddress defaultValue, boolean mandatory)
        throws SmartFrogResolutionException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof java.net.InetAddress) {
                return ((java.net.InetAddress) referenceObj);
            } else if (referenceObj instanceof String) {
                try {
                    return (java.net.InetAddress.getByName((String) referenceObj));
                } catch (Exception ex) {
                    SmartFrogResolutionException resEx = SmartFrogResolutionException.generic(reference,
                            this.sfCompleteNameSafe(), ex.toString());
                    resEx.put(SmartFrogException.DATA, ex);
                    throw resEx;
                }
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a java Object.
     * Utility method to resolve an attribute with a java Object value.
     *
     * @param reference reference
     * @param defaultValue java Object default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return java Object for attribute value or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public Object sfResolve(Reference reference, Object defaultValue,
        boolean mandatory) throws SmartFrogResolutionException{
        boolean illegalClassType = false;
        try {
            Object referenceObj = sfResolve(reference, 0);
            if ((defaultValue==null) || ( defaultValue.getClass().isAssignableFrom(referenceObj.getClass()))) {
                return (referenceObj);
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe(),referenceObj.getClass().toString(),defaultValue.getClass().toString());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }
        return defaultValue;
    };

    /**
     * Resolves a referencePart given a string and gets a boolean. Utility
     * method to resolve an attribute with a boolean value.
     *
     * @param referencePart string field reference
     * @param defaultValue boolean default value that is returned when
     *        reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return boolean for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public boolean sfResolve(String referencePart, boolean defaultValue,
        boolean mandatory) throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a int. Utility method to
     * resolve an attribute with an int value.
     *
     * @param referencePart string field reference
     * @param defaultValue int default value that is returned when reference is
     *        not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return int for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public int sfResolve(String referencePart, int defaultValue, boolean mandatory)
        throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a int. Utility method to
     * resolve an attribute with an int value.
     *
     * @param referencePart string field reference
     * @param defaultValue int default value that is returned when reference is
     *        not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return int for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable or resolved value &lt; minValue or &gt; maxValue
     *
     */
    public int sfResolve(String referencePart, int defaultValue,Integer minValue,Integer maxValue, boolean mandatory)
        throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, minValue, maxValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a long. Utility method
     * to resolve an attribute with an long value. Int values are upcastted to
     * long.
     *
     * @param referencePart string field reference
     * @param defaultValue long default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return long for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public long sfResolve(String referencePart, long defaultValue, boolean mandatory)
        throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a long. Utility method
     * to resolve an attribute with an long value. Int values are upcastted to
     * long.
     *
     * @param referencePart string field reference
     * @param defaultValue long default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return long for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable or resolved value &lt; minValue or &gt; maxValue
     *
     */
    public long sfResolve(String referencePart, long defaultValue, Long minValue, Long maxValue, boolean mandatory)
        throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, minValue, maxValue, mandatory);
    }
    /**
     * Resolves a referencePart given a string and gets a float. Utility method
     * to resolve an attribute with an float value.
     *
     * @param referencePart string field reference
     * @param defaultValue float default value that is returned when reference is
     *        not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return float for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public float sfResolve(String referencePart, float defaultValue, boolean mandatory)
        throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a float. Utility method
     * to resolve an attribute with an float value.
     *
     * @param referencePart string field reference
     * @param defaultValue float default value that is returned when reference is
     *        not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return float for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable or resolved value &lt; minValue or &gt; maxValue
     *
     */
    public float sfResolve(String referencePart, float defaultValue,Float minValue,Float maxValue, boolean mandatory)
        throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, minValue, maxValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a double. Utility method
     * to resolve an attribute with an double value. Int values are upcasted to
     * double.
     *
     * @param referencePart string field reference
     * @param defaultValue double default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return double for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public double sfResolve(String referencePart, double defaultValue, boolean mandatory)
        throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a double. Utility method
     * to resolve an attribute with an double value. Integer, Long and Float
     * values are upcasted to double.
     *
     * @param referencePart string field reference
     * @param defaultValue double default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return double for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable or resolved value &lt; minValue or &gt; maxValue
     *
     */
    public double sfResolve(String referencePart, double defaultValue, Double minValue, Double maxValue, boolean mandatory)
        throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, minValue, maxValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a String. Utility method
     * to resolve an attribute with a String value.
     *
     * @param referencePart string field reference
     * @param defaultValue String default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return String for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public String sfResolve(String referencePart, String defaultValue,
        boolean mandatory) throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a Vector. Utility method     * to resolve an attribute with a Vector value.
     *
     * @param referencePart string field reference
     * @param defaultValue Vector default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Vector for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public Vector sfResolve(String referencePart, Vector defaultValue,
        boolean mandatory) throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a String[]. Utility
     * method to resolve an attribute with a Vector value and returns a String[]
     *
     *
     * @param referencePart string field reference
     * @param defaultValue String[] default value that is returned when
     *        reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return String[] for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public String[] sfResolve(String referencePart, String[] defaultValue,
        boolean mandatory) throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a SmartFrog
     * ComponentDescription. Utility method to resolve an attribute with a
     * SmartFrog ComponentDescription value.
     *
     * @param referencePart string field reference
     * @param defaultValue SmartFrog ComponentDescription default value that is
     *        returned when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return ComponentDescription for attribute value or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public ComponentDescription sfResolve(String referencePart,
        ComponentDescription defaultValue, boolean mandatory)
        throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a SmartFrog Reference.
     * Utility method to resolve an attribute with a SmartFrog
     * ComponentDescription value.
     *
     * @param referencePart string field reference
     * @param defaultValue SmartFrog Reference default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Reference for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public Reference sfResolve(String referencePart, Reference defaultValue,
        boolean mandatory) throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a SmartFrog Prim.
     * Utility method to resolve an attribute with a SmartFrog
     * ComponentDescription value.
     *
     * @param referencePart string field reference
     * @param defaultValue SmartFrog Prim default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Prim for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public Prim sfResolve(String referencePart, Prim defaultValue,
        boolean mandatory) throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }


    /**
     * Resolves a referencePart given a string and gets a SmartFrog Compound.
     * Utility method to resolve an attribute with a SmartFrog
     * ComponentDescription value.
     *
     * @param referencePart string field reference
     * @param defaultValue SmartFrog Compound default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Compound for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public Compound sfResolve(String referencePart, Compound defaultValue,
        boolean mandatory) throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a SmartFrog Reference.
     * Utility method to resolve an attribute with a java.net.InetAddress
     * value.
     *
     * @param referencePart string field reference
     * @param defaultValue java.net.InetAddress default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Reference for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public java.net.InetAddress sfResolve(String referencePart,
        java.net.InetAddress defaultValue, boolean mandatory)
        throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }



    /**
     * Resolves a referencePart given a string and gets a java Object.
     *
     * @param referencePart string field reference
     * @param defaultValue java Object default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Reference for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     *
     */
    public Object sfResolve(String referencePart,
        Object defaultValue, boolean mandatory)
        throws SmartFrogResolutionException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }
}