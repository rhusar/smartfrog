package org.smartfrog.sfcore.reference;


import java.util.Vector;
import java.io.File;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import java.rmi.RemoteException;


 public abstract class RemoteReferenceResolverHelperImpl extends Object  {
    protected RemoteReferenceResolverHelperImpl() {
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
     * @throws RemoteException In case of network/rmi error
     */
    public abstract Object sfResolve(Reference r)
        throws SmartFrogResolutionException, RemoteException;


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
     * @throws RemoteException In case of network/rmi error
     */
    public abstract Object sfResolve(Reference r, int index)
        throws SmartFrogResolutionException, RemoteException;


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
     * @throws RemoteException In case of network/rmi error
     */
    public Object sfResolve(String referencePart) throws SmartFrogResolutionException, RemoteException {
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
     * @throws RemoteException In case of network/rmi error
     */
    public Object sfResolveWithParser(String reference) throws SmartFrogResolutionException, RemoteException {
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
     * @throws RemoteException In case of network/rmi error
     */
    public Object sfResolve(Reference reference, boolean mandatory)
            throws SmartFrogResolutionException, RemoteException{
        try {
            Object referenceObj = sfResolve(reference);
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
     * @return Reference for attribute value, null if SFNull is found or null if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Object sfResolve(String referencePart, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
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
     * @throws RemoteException In case of network/rmi error
     */
    public boolean sfResolve(Reference reference, boolean defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference);

            if (referenceObj instanceof Boolean) {
                return (((Boolean) referenceObj).booleanValue());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe()
                    , referenceObj , referenceObj.getClass().toString()
                    , "boolean");
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
     * @throws RemoteException In case of network/rmi error
     */
    public int sfResolve(Reference reference, int defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference);

            if (referenceObj instanceof Integer) {
                return (((Integer) referenceObj).intValue());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe()
                    , referenceObj , referenceObj.getClass().toString()
                    , "int");
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
     * @throws RemoteException In case of network/rmi error
     */
    public int sfResolve(Reference reference, int defaultValue,Integer minValue, Integer maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
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
     * @throws RemoteException In case of network/rmi error
     */
    public long sfResolve(Reference reference, long defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference);

            if ((referenceObj instanceof Long) ||
                    (referenceObj instanceof Integer)) {
                return (((Number) referenceObj).longValue());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                                    this.sfCompleteNameSafe()
                                    , referenceObj , referenceObj.getClass().toString()
                                    , "long");
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
     * @throws RemoteException In case of network/rmi error
     */
    public long sfResolve(Reference reference, long defaultValue, Long minValue, Long maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
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
     * @throws RemoteException In case of network/rmi error
     */
    public float sfResolve(Reference reference, float defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference);

            if ((referenceObj instanceof Float) ||
                    (referenceObj instanceof Integer)) {
                return (((Number) referenceObj).floatValue());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                                    this.sfCompleteNameSafe()
                                    , referenceObj , referenceObj.getClass().toString()
                                    , "float");
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
     * @throws RemoteException In case of network/rmi error
     */
    public float sfResolve(Reference reference, float defaultValue, Float minValue, Float maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
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
     * @throws RemoteException In case of network/rmi error
     */
    public double sfResolve(Reference reference, double defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference);

            if ((referenceObj instanceof Float) ||
                    (referenceObj instanceof Integer)
                    || (referenceObj instanceof Long)
                    || (referenceObj instanceof Double)) {
                return (((Number) referenceObj).doubleValue());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                                    this.sfCompleteNameSafe()
                                    , referenceObj , referenceObj.getClass().toString()
                                    , "double");
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
     * @throws RemoteException In case of network/rmi error
     */
    public double sfResolve(Reference reference, double defaultValue, Double minValue, Double maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
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
     * @return String for attribute value, null if SFNull is found or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public String sfResolve(Reference reference, String defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference);
            if (referenceObj instanceof SFNull) {return null;}

            if (referenceObj instanceof String) {
                return (((String) referenceObj));
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                                    this.sfCompleteNameSafe()
                                    , referenceObj , referenceObj.getClass().toString()
                                    , "java.lang.String");
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
     * @return Vector for attribute value, null if SFNull is found or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Vector sfResolve(Reference reference, Vector defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference);
            if (referenceObj instanceof SFNull) {return null;}

            if (referenceObj instanceof Vector) {
                return (((Vector) referenceObj));
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                                    this.sfCompleteNameSafe()
                                    , referenceObj , referenceObj.getClass().toString()
                                    , "java.util.Vector");
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
     * @return String[] for attribute value, null if SFNull is found or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public String[] sfResolve(Reference reference, String[] defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference);
            if (referenceObj instanceof SFNull) {return null;}

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
                                    this.sfCompleteNameSafe()
                                    , referenceObj , referenceObj.getClass().toString()
                                    , "java.util.Vector to String[]");
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference. Utility method to resolve an attribute with a
     * String value returning a File
     *
     * @param reference reference
     * @param defaultValue File default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return java.io.File for attribute value, null if SFNull is found or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public File sfResolve(Reference reference, File defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference);
            if (referenceObj instanceof SFNull) {return null;}

            if (referenceObj instanceof String) {
                return new File(((String) referenceObj));
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                                    this.sfCompleteNameSafe()
                                    , referenceObj , referenceObj.getClass().toString()
                                    , "java.io.File");
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
     * @return SmartFrog ComponentDescription for attribute value, null if SFNull is found or
     *         defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public ComponentDescription sfResolve(Reference reference,
        ComponentDescription defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference);
            if (referenceObj instanceof SFNull) {return null;}

            if (referenceObj instanceof ComponentDescription) {
                return ((ComponentDescription) referenceObj);
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                                    this.sfCompleteNameSafe()
                                    , referenceObj , referenceObj.getClass().toString()
                                    , "org.smartfrog.sfcore.componentdescription.ComponentDescription");
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
     * @return SmartFrog Reference for attribute value, null if SFNull is found or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Reference sfResolve(Reference reference, Reference defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference);
            if (referenceObj instanceof SFNull) {return null;}

            if (referenceObj instanceof Reference) {
                return ((Reference) referenceObj);
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                                    this.sfCompleteNameSafe()
                                    , referenceObj , referenceObj.getClass().toString()
                                    , "org.smartfrog.sfcore.reference.Reference");
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
     * @return SmartFrog Prim for attribute value, null if SFNull is found or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Prim sfResolve(Reference reference, Prim defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference);
            if (referenceObj instanceof SFNull) {
                return null;
            }

            if (referenceObj instanceof Prim) {
                return ((Prim) referenceObj);
            }
            //we have an illegal class type
            illegalClassType = true;
            if(referenceObj instanceof ComponentDescription) {
                //special handling for want Prim & got ComponentDescription, for extra end-user diagnostics
                String referenceValueType = referenceObj.getClass().toString();
                SmartFrogResolutionException fault = new SmartFrogResolutionException (reference,
                        sfCompleteNameSafe(),
                        MessageUtil.formatMessage(MessageKeys.MSG_ILLEGAL_CLASS_TYPE_EXPECTING_PRIM_GOT_CD,
                                "org.smartfrog.sfcore.prim.Prim", referenceObj, referenceValueType));
                fault.put(SmartFrogResolutionException.REFERENCE_OBJECT_RESOLVED, referenceObj.toString());
                fault.put(SmartFrogResolutionException.REFERENCE_OBJECT_CLASS_TYPE, referenceValueType);
                fault.put(SmartFrogResolutionException.DEFAULT_OBJECT_CLASS_TYPE, "org.smartfrog.sfcore.prim.Prim");
                throw fault;
            }
            //any other class type
            throw SmartFrogResolutionException.illegalClassType(reference,
                                sfCompleteNameSafe()
                                , referenceObj , referenceObj.getClass().toString()
                                , "org.smartfrog.sfcore.prim.Prim");
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
     * @return SmartFrog Compound for attribute value, null if SFNull is found or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Compound sfResolve(Reference reference, Compound defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference);
            if (referenceObj instanceof SFNull) {return null;}

            if (referenceObj instanceof Compound) {
                return ((Compound) referenceObj);
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                                    this.sfCompleteNameSafe()
                                    , referenceObj , referenceObj.getClass().toString()
                                    , "org.smartfrog.sfcore.compound.Compound");
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
     * @return java.net.InetAddress for attribute value, null if SFNull is found or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public java.net.InetAddress sfResolve(Reference reference,
        java.net.InetAddress defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference);
            if (referenceObj instanceof SFNull) {return null;}

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
                                    this.sfCompleteNameSafe()
                                    , referenceObj , referenceObj.getClass().toString()
                                    , "java.net.InetAddress/java.lang.String");
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a java.net.URL.
     * Utility method to resolve an attribute with a SmartFrog
     * java.net.URL value.
     *
     * @param reference reference
     * @param defaultValue java.net.URL default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return java.net.URL for attribute value, null if SFNull is found or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public java.net.URL sfResolve(Reference reference,
        java.net.URL defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference);
            if (referenceObj instanceof SFNull) {return null;}

            if (referenceObj instanceof java.net.URL) {
                return (java.net.URL)referenceObj;
            } else if (referenceObj instanceof String) {
                try {
                    return new java.net.URL((String) referenceObj);
                } catch (Exception ex) {
                    SmartFrogResolutionException resEx = SmartFrogResolutionException.generic(reference,
                            this.sfCompleteNameSafe(), ex.toString());
                    resEx.put(SmartFrogException.DATA, ex);
                    throw resEx;
                }
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                                    this.sfCompleteNameSafe()
                                    , referenceObj , referenceObj.getClass().toString()
                                    , "java.net.URL/java.lang.String");
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
     * @return java Object for attribute value, null if SFNull is found or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Object sfResolve(Reference reference, Object defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException{
        boolean illegalClassType = false;
        try {
            Object referenceObj = sfResolve(reference);
            if (referenceObj instanceof SFNull) {return null;}
            if ((defaultValue==null) || ( defaultValue.getClass().isAssignableFrom(referenceObj.getClass()))) {
                return (referenceObj);
            } else {
                illegalClassType = true;
                String defaultValueClass="";
                                if (defaultValue!=null){
                                   defaultValueClass=defaultValue.getClass().toString();
                                }
                throw SmartFrogResolutionException.illegalClassType(reference,
                                    this.sfCompleteNameSafe()
                                    , referenceObj , referenceObj.getClass().toString()
                                    , defaultValueClass);

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
     * @throws RemoteException In case of network/rmi error
     */
    public boolean sfResolve(String referencePart, boolean defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
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
     * @throws RemoteException In case of network/rmi error
     */
    public int sfResolve(String referencePart, int defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
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
     * @throws RemoteException In case of network/rmi error
     */
    public int sfResolve(String referencePart, int defaultValue,Integer minValue,Integer maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
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
     * @throws RemoteException In case of network/rmi error
     */
    public long sfResolve(String referencePart, long defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
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
     * @throws RemoteException In case of network/rmi error
     */
    public long sfResolve(String referencePart, long defaultValue, Long minValue, Long maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
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
     * @throws RemoteException In case of network/rmi error
     */
    public float sfResolve(String referencePart, float defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
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
     * @throws RemoteException In case of network/rmi error
     */
    public float sfResolve(String referencePart, float defaultValue,Float minValue,Float maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
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
     * @throws RemoteException In case of network/rmi error
     */
    public double sfResolve(String referencePart, double defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
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
     * @throws RemoteException In case of network/rmi error
     */
    public double sfResolve(String referencePart, double defaultValue, Double minValue, Double maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
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
     * @return String for attribute value, null if SFNull is found or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public String sfResolve(String referencePart, String defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
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
     * @return Vector for attribute value, null if SFNull is found or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Vector sfResolve(String referencePart, Vector defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
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
     * @return String[] for attribute value, null if SFNull is found or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public String[] sfResolve(String referencePart, String[] defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a String and gets a File. Utility method
     * to resolve an attribute with a String value returning a File
     *
     * @param referencePart string field reference
     * @param defaultValue File default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return File for attribute value, null if SFNull is found or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public File sfResolve(String referencePart, File defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
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
     * @return ComponentDescription for attribute value, null if SFNull is found or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public ComponentDescription sfResolve(String referencePart,
        ComponentDescription defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
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
     * @return Reference for attribute value, null if SFNull is found or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Reference sfResolve(String referencePart, Reference defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
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
     * @return Prim for attribute value, null if SFNull is found or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Prim sfResolve(String referencePart, Prim defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
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
     * @return Compound for attribute value, null if SFNull is found or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Compound sfResolve(String referencePart, Compound defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
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
     * @return Reference for attribute value, null if SFNull is found or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public java.net.InetAddress sfResolve(String referencePart,
        java.net.InetAddress defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a SmartFrog Reference.
     * Utility method to resolve an attribute with a java.net.URL
     * value.
     *
     * @param referencePart string field reference
     * @param defaultValue java.net.URL default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Reference for attribute value, null if SFNull is found or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public java.net.URL sfResolve(String referencePart,
        java.net.URL defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
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
     * @return Reference for attribute value, null if SFNull is found or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Object sfResolve(String referencePart,
        Object defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }
}
