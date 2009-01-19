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

package org.smartfrog.sfcore.reference;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;
import java.io.File;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;


/**
 * This makes a reference resolver interface available for remotable objects.
 * ReferenceResolver can not extend from Remote since RMI would then try to
 * marshal a stub to component descriptions on the wire, thinking the
 * description is remotable because it indirectly inherits Remote. Interfaces
 * or classes that need to offer reference resolution and be serializable
 * should implement ReferenceResolver while remotable classes or interfaces
 * should implement RemoteReferenceResolver
 *
 */
public interface RemoteReferenceResolverHelper extends Remote {

    /**
     * Useful method since references are generally built up from strings. This
     * will translate the string into a reference with a single part and
     * resolve it. If the reference is illegal (ie. not parseable) a
     * illegal reference resolution exception is thrown
     *
     * @param referencePart string representation of reference to resolve
     *
     * @return the resolved object
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public Object sfResolve(String referencePart) throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a reference given a string. Utility method to auto-convert from
     * string to reference. It can use cannonical representations for reference
     * that are resolved by the parser.
     *
     * @param reference string field reference
     *
     * @return the resolved object
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public Object sfResolveWithParser(String reference) throws SmartFrogResolutionException, RemoteException;

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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public Object sfResolve(Reference reference, boolean mandatory)
            throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a reference given a string and gets an Object. Utility method
     * to resolve an attribute with a Object value and returns a Object.
     *
     * @param referencePart string field reference with single part

     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return java Object for attribute value or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public Object sfResolve(String referencePart, boolean mandatory)
             throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a reference and gets a boolean. Utility method to resolve an
     * attribute with a boolean value.
     *
     * @param reference reference
     * @param defaultValue boolean default value that is returned when
     *        reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return boolean for attribute value or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public boolean sfResolve(Reference reference, boolean defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a given reference and gets an int. Utility method to
     * resolve an attribute with an int value.
     *
     * @param reference reference
     * @param defaultValue int default value that is returned when reference is
     *        not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return int for attribute value or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public int sfResolve(Reference reference, int defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;
    /**
     * Resolves a given reference and gets an int. Utility method to
     * resolve an attribute with an int value.
     *
     * @param reference reference
     * @param defaultValue int default value that is returned when reference is
     *        not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     * @param minValue allowed (included).
     * @param maxValue allowd (included).
     *
     * @return int for attribute value or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable or resolve value &lt;minValue or resolveValue
     * &gt;maxValue
     * @throws RemoteException if there is any network/rmi error
     */
    public int sfResolve(Reference reference, int defaultValue, Integer minValue, Integer maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves given reference and gets a long. Utility method to resolve an
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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */

    public long sfResolve(Reference reference, long defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves given reference and gets a long. Utility method to resolve an
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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable or resolve value &lt;minValue or resolveValue
     * &gt;maxValue
     * @throws RemoteException if there is any network/rmi error
     */

    public long sfResolve(Reference reference, long defaultValue, Long minValue, Long maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves given reference and gets a float. Utility method to resolve an
     * attribute with an float value. Int values are "upcasted" to float.
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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */

    public float sfResolve(Reference reference, float defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves given reference and gets a float. Utility method to resolve an
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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable or resolve value &lt;minValue or resolveValue
     * &gt;maxValue
     * @throws RemoteException if there is any network/rmi error
     */

    public float sfResolve(Reference reference, float defaultValue, Float minValue, Float maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves given reference and gets a double. Utility method to resolve an
     * attribute with an double value. Int, Long and Float values are "upcasted" to double.
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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable or resolve value &lt;minValue or resolveValue
     * &gt;maxValue
     * @throws RemoteException if there is any network/rmi error
     */

    public double sfResolve(Reference reference, double defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves given reference and gets a double. Utility method to resolve an
     * attribute with an double value. Int, Long and float values are "upcasted" to double.
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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable or resolve value &lt;minValue or resolveValue
     * &gt;maxValue
     * @throws RemoteException if there is any network/rmi error
     */

    public double sfResolve(Reference reference, double defaultValue, Double minValue, Double maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a given reference. Utility method to resolve an
     * attribute with a String value.
     *
     * @param reference reference
     * @param defaultValue String default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return String for attribute value, null if SFNull is found or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public String sfResolve(Reference reference, String defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a given reference and gets a Vector. Utility method to
     * resolve an attribute with a Vector value.
     *
     * @param reference reference
     * @param defaultValue Vector default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return Vector for attribute value, null if SFNull is found or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public Vector sfResolve(Reference reference, Vector defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a given reference and gets a String[]. Utility method
     * to resolve an attribute with a Vector value and returns a String[].
     *
     * @param reference reference
     * @param defaultValue String[] default value that is returned when
     *        reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return String[] for attribute value, null if SFNull is found or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public String[] sfResolve(Reference reference, String[] defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;
  /**
   * Resolves a given reference. Utility method to resolve an
   * attribute with a String value returning a File
   *
   * @param reference reference
   * @param defaultValue File default value that is returned when reference
   *        is not found and it is not mandatory
   * @param mandatory boolean that indicates if this attribute must be
   *        present in the description. If it is mandatory and not found it
   *        triggers a ResolutionException
   *
   * @return java.io.File for attribute value, null if SFNull is found or null if not found
   *
   * @throws SmartFrogResolutionException if invalid reference of reference
   * not resolvable
   * @throws RemoteException if there is any network/rmi error
   */
  public File sfResolve(Reference reference, File defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;
    /**
     * Resolves given reference and gets a java.net.InetAddress.
     * Utility method to resolve an attribute with a
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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public java.net.InetAddress sfResolve(Reference reference,
        java.net.InetAddress defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException ;

    /**
     * Resolves given reference and gets a java.net.URL.
     * Utility method to resolve an attribute with a
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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public java.net.URL sfResolve(Reference reference,
        java.net.URL defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException ;

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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public ComponentDescription sfResolve(Reference reference,
        ComponentDescription defaultValue, boolean mandatory)
            throws SmartFrogResolutionException, RemoteException;

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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public Reference sfResolve(Reference reference, Reference defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;

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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public Prim sfResolve(Reference reference, Prim defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;

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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public Compound sfResolve(Reference reference, Compound defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a given reference  and gets a String[]. Utility method
     * to resolve an attribute with a Vector value and returns a String[].
     *
     * @param reference reference
     * @param defaultValue String[] default value that is returned when
     *        reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return String[] for attribute value, null if SFNull is found or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public Object sfResolve(Reference reference, Object defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a reference given a string and gets a boolean. Utility method
     * to resolve an attribute with a boolean value.
     *
     * @param referencePart string field reference with single part
     * @param defaultValue boolean default value that is returned when
     *        reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return boolean for attribute value or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public boolean sfResolve(String referencePart, boolean defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a reference given a string and gets an int. Utility method to
     * resolve an attribute with an int value.
     *
     * @param referencePart string field reference with single part
     * @param defaultValue int default value that is returned when reference is
     *        not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return int for attribute value or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public int sfResolve(String referencePart, int defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a reference given a string and gets an int. Utility method to
     * resolve an attribute with an int value.
     *
     * @param referencePart string field reference with single part
     * @param defaultValue int default value that is returned when reference is
     *        not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return int for attribute value or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable or resolve value &lt;minValue or resolveValue
     * &gt;maxValue
     * @throws RemoteException if there is any network/rmi error
     */
    public int sfResolve(String referencePart, int defaultValue, Integer minValue, Integer maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a referencePart given a string and gets a long. Utility method to
     * resolve an attribute with an long value. Int values are upcastted to
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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public long sfResolve(String referencePart, long defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a referencePart given a string and gets a long. Utility method to
     * resolve an attribute with an long value. Int values are upcastted to
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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable or resolve value &lt;minValue or resolveValue
     * &gt;maxValue
     * @throws RemoteException if there is any network/rmi error
     */
    public long sfResolve(String referencePart, long defaultValue, Long minValue, Long maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;
    /**
     * Resolves a reference given a string and gets an float. Utility method to
     * resolve an attribute with an float value. Int is "upcasted" to float.
     *
     * @param referencePart string field reference with single part
     * @param defaultValue float default value that is returned when reference
     * is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return float for attribute value or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable or resolve value &lt;minValue or resolveValue
     * &gt;maxValue
     * @throws RemoteException if there is any network/rmi error
     */
    public float sfResolve(String referencePart, float defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a reference given a string and gets an float. Utility method to
     * resolve an attribute with an float value. Int is "upcasted to float.
     *
     * @param referencePart string field reference with single part
     * @param defaultValue float default value that is returned when reference
     * is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return float for attribute value or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable or resolve value &lt;minValue or resolveValue
     * &gt;maxValue
     * @throws RemoteException if there is any network/rmi error
     */
    public float sfResolve(String referencePart, float defaultValue, Float minValue, Float maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a referencePart given a string and gets a double. Utility
     * method to  resolve an attribute with an double value. Int, Long and
     * Float values are upcasted to double.
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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable or resolve value &lt;minValue or resolveValue
     * &gt;maxValue
     * @throws RemoteException if there is any network/rmi error
     */
    public double sfResolve(String referencePart, double defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a referencePart given a string and gets a double. Utility
     * method to  resolve an attribute with an double value. Int, Long and
     * Float values are upcasted to double.
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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable or resolve value &lt;minValue or resolveValue
     * &gt;maxValue
     * @throws RemoteException if there is any network/rmi error
     */
    public double sfResolve(String referencePart, double defaultValue, Double minValue, Double maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a reference given a String and gets a String. Utility method to
     * resolve an attribute with a String value.
     *
     * @param referencePart string field reference with single part
     * @param defaultValue String default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return String for attribute value, null if SFNull is found or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     *
     * @throws RemoteException if there is any network/rmi error
     */
    public String sfResolve(String referencePart, String defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a reference given a string and gets Vector. Utility method to
     * resolve an attribute with a Vector value.
     *
     * @param referencePart string field reference with single part
     * @param defaultValue Vector default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return Vector for attribute value, null if SFNull is found or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public Vector sfResolve(String referencePart, Vector defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a reference given a string and gets a String[]. Utility method
     * to resolve an attribute with a Vector value and returns a String[].
     *
     * @param referencePart string field reference with single part
     * @param defaultValue String[] default value that is returned when
     *        reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return String[] for attribute value, null if SFNull is found or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public String[] sfResolve(String referencePart, String[] defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;

  /**
   * Resolves a reference given a String and gets a File. Utility method to
   * resolve an attribute with a String value returning a File.
   *
   * @param referencePart string field reference with single part
   * @param defaultValue File default value that is returned when reference
   *        is not found and it is not mandatory
   * @param mandatory boolean that indicates if this attribute must be
   *        present in the description. If it is mandatory and not found it
   *        triggers a ResolutionException
   *
   * @return File for attribute value, null if SFNull is found or null if not found
   *
   * @throws SmartFrogResolutionException if invalid reference of reference
   * not resolvable
   *
   * @throws RemoteException if there is any network/rmi error
   */
  public File sfResolve(String referencePart, File defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;

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
     * @return java.net.InetAddress for attribute value, null if SFNull is found or defaultValue if not
     * found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public java.net.InetAddress sfResolve(String referencePart,
        java.net.InetAddress defaultValue, boolean mandatory)
            throws SmartFrogResolutionException, RemoteException;

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
     * @return java.net.URL for attribute value, null if SFNull is found or defaultValue if not
     * found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public java.net.URL sfResolve(String referencePart,
        java.net.URL defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public ComponentDescription sfResolve(String referencePart,
        ComponentDescription defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a referencePart given a string and gets a SmartFrog Reference.
     * Utility method to resolve an attribute with a SmartFrog Reference value.
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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public Reference sfResolve(String referencePart, Reference defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a referencePart given a string and gets a SmartFrog Prim.
     * Utility method to resolve an attribute with a SmartFrog Prim value.
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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public Prim sfResolve(String referencePart, Prim defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolves a referencePart given a string and gets a SmartFrog Compound.
     * Utility method to resolve an attribute with a SmartFrog Compound value.
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
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public Compound sfResolve(String referencePart, Compound defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;


    /**
     * Resolves a reference given a string and gets an Object. Utility method
     * to resolve an attribute with a Object value and returns a Object.
     *
     * @param referencePart string field reference with single part
     * @param defaultValue any java Object default value that is returned when
     *        reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return java Object for attribute value, null if SFNull is found or null if not found
     *
     * @throws SmartFrogResolutionException if invalid reference of reference
     * not resolvable
     * @throws RemoteException if there is any network/rmi error
     */
    public Object sfResolve(String referencePart, Object defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException;

}
