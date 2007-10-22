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

package org.smartfrog.services.dns;

import java.util.List;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import java.util.Vector;
import java.util.Enumeration;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.Name;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.ARecord;
import java.util.Comparator;
import java.util.TreeSet;
import java.net.InetAddress;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.Type;






/**
 * A set of client methods to locate compatible services using DNS
 * SRV records.
 *
 * 
 */
public class DNSServiceImpl  implements DNSServiceQuery {

    /** A base filter for all the results (or null). */
    DNSServiceFilter baseFilter = null;

    /** A name for the service class. */
    Name serviceClassName = null;

    /** Use the default DNS port number (53). */
    public final static int DEFAULT_DNS_PORT = -1;


    /**
     * Creates a new <code>DNSServiceImpl</code> instance.
     *
     * @param service a <code>String</code> value
     * @param baseFilter a <code>DNSServiceFilter</code> value
     */
    public DNSServiceImpl(String service, DNSServiceFilter baseFilter) {
        
        serviceClassName = DNSBindingIPImpl.toAbsoluteName(service);
        this.baseFilter = baseFilter;
    }


   /**
     * Performs a look-up of the registered service class using 
     * a particular resolver and filtering according to a given 
     * criteria. Matching instances are returned in decreasing
     * priority. Multiple requests could return different results
     * since the available instances or priorities could change 
     * dynamically. It uses  default DNS settings and filters.
     *
     * @return An array of service instances matching the criteria
     * and in decreasing priority or an array of lenght zero if 
     * no services found.
     * @exception DNSException if an error occurs while looking up.
     */
    public DNSServiceInstance[] lookup()
        throws DNSException {

        return lookup(null, null);
    } 

    /**
     * Performs a look-up of the registered service class using 
     * a particular resolver and filtering according to a given 
     * criteria. Matching instances are returned in decreasing
     * priority. Multiple requests could return different results
     * since the available instances or priorities could change 
     * dynamically.
     *
     * @param resol A network connection to resolve the query.
     * @param filter An extra filtering function to be applied to the 
     * results or null if no extra filtering is required.
     * @return An array of service instances matching the criteria
     * and in decreasing priority or an array of lenght zero if 
     * no services found.
     * @exception DNSException if an error occurs while looking up.
     */
    public DNSServiceInstance[] lookup(Resolver resol,
                                       DNSServiceFilter filter)
        throws DNSException {

        Resolver resolver = (resol == null ? getResolver(null) : resol);

        List result = findAllServices(resolver);
        result = filterServices(result, filter);
        result = sortServices(result,
                              DNSServiceInstanceImpl.getComparatorInstance());
        return (DNSServiceInstance[]) result.toArray(new DNSServiceInstance[0]);
    } 


    /**
     * Filters a list of services using the base or the extra filter
     * provided.
     *
     * @param in A list of services to be filtered.
     * @param filter An extra filter to be added.
     * @return A list with all the acceptable services.
     */
    List filterServices(List in, DNSServiceFilter filter) {

        Vector result = new Vector();
        for (Iterator iter = in.iterator(); iter.hasNext();) {
            DNSServiceInstance elem = (DNSServiceInstance) iter.next();
            // Bad if it fails ANY of the filters that are set
            boolean isBAD = ((baseFilter != null) 
                             && (!baseFilter.isAcceptable(elem)));
            isBAD = isBAD || ((filter != null) 
                              && (!filter.isAcceptable(elem)));
            if (!isBAD) {
                result.add(elem);
            }
        }
        return result;
    }

    /**
     * Sorts a list of services instances according to a standard comparator.
     * More important services are first in the list. It will also eliminate
     * duplicates from the service instance input list.
     *
     * @param in A list of services instances to be sorted.
     * @param comp A comparator function.
     * @return A sorted list of services instances.
     */
    List sortServices(List in, Comparator comp) {

        Vector result = new Vector();
        TreeSet sort = new TreeSet(comp);
        sort.addAll(in);
        
        // iterator is  in ascending order (less important at the end) 
        for (Iterator iter = sort.iterator(); iter.hasNext();) {
            result.add(iter.next());
        }
        return result;        
    }

    /**
     * Queries the DNS server for all the registered services with
     * a given name.
     *
     * @param resol A connection to the DNS server.
     * @return A list of service instances.
     * @exception DNSException if an error occurs while doing the look-ups.
     */
    List findAllServices(Resolver resol) 
        throws DNSException {

        try {
            Vector results = new Vector();
            Record[] answers = doLookup(resol, serviceClassName, Type.PTR);
            for (int i=0; i < answers.length; i++) {
                if (answers[i] instanceof PTRRecord) {
                    PTRRecord ptr = (PTRRecord) answers[i];
                    List inst = findOneService(resol, ptr.getTarget());
                    results.addAll(inst);
                }
            }
            return results;
        } catch (Exception e) {
            throw new DNSException("failed services lookup", e);
        }
    }

    /**
     * Locates the SRV and TXT DNS records for a service and creates
     * a description of the service instance. Only one service should 
     * be available for a given name (others are ignored).
     *
     * @param resol A network connection to the DNS server to resolve
     *  the query.
     * @param target A name for the service.
     * @return A list containing a service instance description.
     * @exception DNSException if an error occurs
     */
    List findOneService(Resolver resol, Name target) 
        throws DNSException {

        Vector results = new Vector();
        Record[] answersSRV = doLookup(resol, target, Type.SRV);
        Record[] answersTXT = doLookup(resol, target, Type.TXT);
        // only one mapping allowed, otherwise we ignore extra records.
        // We should log if length > 1 though ...
        if ((answersSRV.length >= 1) 
            && (answersTXT.length >= 1)) {
            SRVRecord srv = (SRVRecord) answersSRV[0];
            TXTRecord txt = (TXTRecord) answersTXT[0];
            InetAddress addr = findAddress(resol, srv.getTarget());
            DNSServiceInstance inst = 
                new DNSServiceInstanceImpl(srv, txt, addr);
            results.add(inst);
        }
        return results;                    
    }


    /**
     * Finds an address for a host name. If it cannot find it, just 
     * return null (i.e., no exception). If there are more than one,
     * just return the first one.
     *
     * @param resol A network connection to the DNS server to resolve
     *  the query.
     * @param hostName A name for the host.
     * @return An IP address for the host or null.
     */
    public static InetAddress findAddress(Resolver resol, Name hostName) {

        try {
            Record[] answer = doLookup(resol, hostName, Type.A);
            if ((answer.length > 0) && (answer[0] instanceof ARecord)) {
                return ((ARecord) answer[0]).getAddress();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Performs a DNS lookup bypassing the cache. It silently fails
     * if it cannot do the query (returns lenght 0 array)
     *
     * @param resol A resolver to contact the DNS server.
     * @param name A name for the record.
     * @param type A type for the record.
     * @return A (possibly zero length) array of result records. 
     * @exception DNSException if an error occurs
     */
    public static Record[] doLookup(Resolver resol, Name name,
                                    int type) {
        
        Lookup lu = new Lookup(name, type);
        lu.setResolver(resol);
        // clean up the cache.
        lu.setCache(null);
        lu.run();
        // we ignore the TRY_AGAIN for now... (TCP recommended)
        if (lu.getResult() ==  Lookup.SUCCESSFUL) {
            return lu.getAnswers();
        } else {
            return new Record[0];
        }
    }


    /**
     * Gets a resolver for DNS queries (port 53, use TCP).
     *
     * @param hostName A host name for the DNS server or null 
     * if we use the platform default.
     * @return A resolver for DNS queries.
     * @exception DNSException if an error occurs while getting a resolver
     * to the  name server.
     */
    public static Resolver getResolver(String hostName)
        throws DNSException {
     
        return getResolver(hostName, true, DEFAULT_DNS_PORT);
    } 

    /**
     * Gets a resolver for DNS queries.
     *
     * @param hostName A host name for the DNS server or null 
     * if we use the platform default.
     * @param useTCP Whether to use TCP for the query.
     * @param port Port to use or -1 for the default.
     * @return A resolver for DNS queries.
     * @exception DNSException if an error occurs while getting a resolver
     * to the  name server.
     */
    public static Resolver getResolver(String hostName, boolean useTCP,
                                       int port) 
        throws DNSException {
        
        try {
            SimpleResolver resolver = new SimpleResolver(hostName);
            if (port == DEFAULT_DNS_PORT) {
                resolver.setPort(DNSComponent.DEFAULT_PORT);
            } else {
                resolver.setPort(port);
            }
            if (useTCP) {
                resolver.setTCP(true);
            }        
            return resolver;
        } catch (Exception e) {
            throw new DNSException("Cannot get resolver", e);
        }
    }




    /**
     * Converts a component description into a list of formatted 
     * strings that can be incorporated into a TXT DNS record. Non
     * basic entries in the component description are ignored.
     *
     * @param cd A component description input.
     * @return A list of strings that represent "lines" of a TXT 
     * DNS record.
     */
    public static List cdToTXT(ComponentDescription cd) {

        List resultAll = new Vector();
        System.out.println("cdToTXT: in: " + cd); // DELETE        
        Context context = cd.sfContext();
        for (Enumeration e = context.keys(); e.hasMoreElements();) {
            StringBuffer result  = new StringBuffer();
            // restrict keys to  strings, otherwise class cast exception.
            String name = (String) e.nextElement();
            result.append(name + "=");
            try {
                result.append(valueToString(context.get(name)));
                resultAll.add(result.toString());
            } catch (IllegalArgumentException ex) {
                // just ignore non-basic entries in cd
            }
        }
        System.out.println("cdToTXT: out: " + resultAll); // DELETE        
        return resultAll;
    }

    /**
     * Converts a set of lines from a DNS TXT record into a component
     * description.
     *
     * @param strings A set of lines from a DNS TXT record.
     * @return A component description for that record.
     */
    public static ComponentDescription txtToCD(List strings)
        throws DNSException {
        
        try {
            System.out.println("txtToCD: in: " + strings); // DELETE
            StringBuffer result  = new StringBuffer();
            for (Iterator iter = strings.iterator(); iter.hasNext(); ) {
                
                String line = (String) iter.next();
                result.append(stringToSF1(line));
            }

            System.out.println("txtToCD: out: " + result); // DELETE
            byte[] inBytes = result.toString().getBytes("UTF-8");
            InputStream in = new ByteArrayInputStream(inBytes);
            SFParser parser = new SFParser("sf");
            Phases phases = parser.sfParse(in);
            Vector allPhases = new Vector();
            allPhases.add("type");
            // do we need all these phases?
            allPhases.add("place");
            allPhases.add("link");
            allPhases.add("function");

            phases.sfResolvePhases(allPhases);
            return phases.sfAsComponentDescription();
        } catch (Exception e) {
            if (e instanceof DNSException) {
                throw (DNSException) e;
            } else {
                throw new DNSException("txtToCD" + strings, e);
            }
        }
        
    }


    /**
     * Formats a TXT line into a parseable SF one.
     *
     * @param line A  TXT entry line.
     * @return A parseable SF line.
     */
    static String stringToSF1(String line) {

        String line2 = line.replaceAll("\\\\\"", "\""); // \" by "
        return line2.replaceFirst("=", " ") + ";";
    }


    /**
     * Formats a basic value of a component description into a string.
     *
     * @param obj A basic value inside a component description.
     * @return A (parsable) string representation of this value.
     */
    static String valueToString(Object obj) {

        return canonicalBasic(obj);
    }


    /*  THESE FUNCTIONS ARE DUPLICATED FROM THE NEW SECURITY CODE. 

    THEY SHOULD BE DELETED AND REPLACED BY SFCanonical after 
    the changes are merged.
                
    */
    /**
     * Returns a canonical string of a SF basic type. This is,
     * a String, Float, Long, Integer, Double, Boolean or vector.
     *
     * @param obj An object of a basic type to canonicalize.
     * @return A canonical string of a SF basic type.
     */
    public static String canonicalBasic(Object obj) {

        if (obj instanceof String) {
            // quoted string 
            return canonicalString((String) obj);
        } else if (obj instanceof Number) {
            // integer, long, float or double
            return canonicalNumber((Number) obj);
        } else if (obj instanceof Boolean) {
            // boolean
            return canonicalBoolean((Boolean) obj);
        } else if (obj instanceof Vector) {
            // vector of basic types
            return canonicalVector((Vector) obj);
        } else {
            throw new IllegalArgumentException("canonicalBasic: param" + obj
                                               + " is" + "not a basic type");
        }
    }

    /**
     * Returns a canonical string of a SF vector. All the elements of a
     * SF vector need to be basic types.
     *
     * @param vector A SF vector to be canonicalized.
     * @return  A canonical string of a SF vector.
     */
    public static String canonicalVector(Vector vector) {
    
        StringBuffer result  = new StringBuffer();
        result.append("[");
        for (Iterator iter = vector.iterator(); iter.hasNext();) {
            result.append(canonicalBasic(iter.next()));
            if (iter.hasNext()) {
                result.append(", ");
            }
        }
        result.append("]");
        return result.toString();
    }


    /**
     * Escapes special characters in a String so that they can be parsed back
     * as a SmartFrog "string".
     *
     * @param str A string that we want to escape its special characters.
     * @return The input string with special characters escaped.
     */
    public static String addEscapes(String str) {

        StringBuffer strBuf = new StringBuffer(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
            case '\\': 
                strBuf.append('\\');
                strBuf.append('\\');
                break;
            case '\n':
                strBuf.append('\\');
                strBuf.append('n');
                break;
            case '\t':
                strBuf.append('\\');
                strBuf.append('t');
                break;
            case '\b':
                strBuf.append('\\');
                strBuf.append('b');
                break;
            case '\r':
                strBuf.append('\\');
                strBuf.append('r');
                break;
            case '\f':
                strBuf.append('\\');
                strBuf.append('f');
                break;
            default :
                strBuf.append(c);
            }
        }
        return strBuf.toString();
    }

    /**
     * Returns a canonical string of a SF string, i.e., quoted and with
     * escaped special characters.
     *
     * @param str A string to be canonicalized.
     * @return a  canonical string of a SF string.
     */
    public static String canonicalString(String str) {

        return "\"" + addEscapes(str) + "\"";
    }

    /**
     * Returns a canonical string of a SF boolean.
     *
     * @param bool A boolean flag to be canonicalized.
     * @return A  canonical string of a SF boolean.
     */
    public static String canonicalBoolean(Boolean bool) {

        return (bool.booleanValue() ? "true" : "false");
    }

  
    /**
     * Returns a canonical string of a SF number.
     *
     * @param num A number to be canonicalized.
     * @return A canonical string of a SF number.
     */
    public static String canonicalNumber(Number num) {

        if (num instanceof Long) {
            return num.toString() + "L";
        } else if (num instanceof Integer) {
            return num.toString();
        } else if (num instanceof Float) {
            return num.toString() + "F";
        } else if (num instanceof Double) {
            return num.toString() + "D";
        } else {
            throw new IllegalArgumentException("canonicalNumber: param" + num
                                               + " is "
                                               + "not a valid number type");
        }
    }

}
