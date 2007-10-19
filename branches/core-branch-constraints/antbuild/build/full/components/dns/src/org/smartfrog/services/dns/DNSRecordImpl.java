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

import org.xbill.DNS.DClass;
import org.xbill.DNS.Type;
import org.xbill.DNS.Name;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.Update;
import java.net.InetAddress;
import org.xbill.DNS.Resolver;
import java.util.List;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.TXTRecord;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import java.net.UnknownHostException;
import org.smartfrog.sfcore.common.Context;





/**
 * An abstract class that implements a DNS record to be updated.
 *
 * 
 * 
 */
public abstract class  DNSRecordImpl implements DNSRecord {

    /** The time-to-live value in seconds of this record.*/
    int ttl = 3600;

    /** The dns class of this record. */
    int dClass = DClass.IN;

    /**  The type of this DNS record, i.e., A, PTR, SRV... */
    int dType = Type.A; 
    
    /** A name for this record as it appears in DNS. */
    Name entryName = null;

    /** A unique name for storing this record in the zone context. */
    String uniqueName = null;

    /**  Whether to replace all previous bindings during registration.*/
    boolean replaceAll = false;

    /* Update operations. */
    static final int ADD = 1; 
    static final int DELETE = 2; 
    static final int REPLACE = 3; 

    /** An address type string name. */ 
    public static final String TYPE_A = "A";
    /** A pointer type string name. */ 
    public static final String TYPE_PTR = "PTR";
    /** A service type string name. */ 
    public static final String TYPE_SRV = "SRV";
    /** A text type string name. */ 
    public static final String TYPE_TXT = "TXT";

    /** An integer representing a not initialized value. */
    public static int BAD_INTEGER = -98;


    // all the attribute names in the description...
    public static final String RECORD_TYPE = "recordType";
    public static final String HOST_NAME = "hostName";
    public static final String ADDRESS = "address";
    public static final String PORT = "port";
    public static final String PRIORITY = "priority";
    public static final String WEIGHT = "weight";
    public static final String NAME = "name";
    public static final String ATTRIBUTES = "attributes";
    public static final String SERVICE_NAME = "serviceName";
    public static final String ALIAS = "alias";

    /**
     * Creates a new <code>DNSRecordImpl</code> instance.
     *
     * @param entryName a <code>Name</code> value
     * @param dClass an <code>int</code> value
     * @param dType an <code>int</code> value
     * @param uniqueName a <code>String</code> value
     */
    public DNSRecordImpl(Name entryName, int dClass, int dType,
                         String uniqueName) {

        this.entryName = entryName;
        this.dClass = dClass;
        this.dType = dType;
        this.uniqueName = uniqueName;
    }

    /**
     * Sets the time-to-live value in seconds of this record.
     *
     * @param ttl  The time-to-live value in seconds of this record.
     * @return The old time-to-live value in seconds of this record.
     */
    public int setTTL(int ttl) {

        int old = this.ttl;
        this.ttl = ttl;
        return old;
    }

    /**
     * Gets the time-to-live value in seconds of this record.
     *
     * @return The time-to-live value in seconds of this record.
     */
    public int getTTL() {

        return ttl;
    }

    /**
     * Whether to replace all previous bindings during registration.        
     *
     * @return True  if we  replace all previous bindings during registration.
     */
    public boolean getReplaceAll() {

        return replaceAll;
    }


    /**
     * Sets whether to replace all previous bindings during registration.  
     *
     * @param replaceAll whether to replace all previous bindings during 
     * registration. 
     * @return An old value for that flag.
     */
    public boolean setReplaceAll(boolean replaceAll) {

        boolean old = this.replaceAll;
        this.replaceAll = replaceAll;
        return old;
    }

                        
    /**
     * Gets the class of this DNS record, i.e., IN.
     *
     * @return  the class of this DNS record, i.e., IN.
     */
    public int getClassRecord() {

        return dClass;
    }

    /**
     * Gets the type of this DNS record, i.e., A, PTR, SRV...
     *
     * @return  the type of this DNS record, i.e., A, PTR, SRV...
     */
    public int getType() {
        
        return dType;
    }


    /**
     * Gets the name of this DNS record.
     *
     * @return  the  name of this DNS record.
     */
    public Name getName() {
     
        return entryName;
    }

    /**
     * Gets a unique name in the zone context for this DNS record.
     *
     * @return   a unique name in the zone context for this DNS record.
     */
    public String getUniqueName() {

        return uniqueName;
    }

    /**
     * Gets an update record that will register this record in a 
     * given zone.
     *
     * @param zone A zone where this record is registered.
     * @return  An update record that will register this record in a 
     * given zone.
     * @exception DNSModifierException if an error occurs while obtaining the
     * update.
     */
    public abstract Update getRegisterUpdate(Name zone)  
        throws DNSModifierException;

    /**
     * Gets an update record that will unregister this record in a 
     * given zone.
     *
     * @param zone A zone where this record is registered.
     * @return  An update record that will unregister this record in a 
     * given zone.
     * @exception DNSModifierException if an error occurs while obtaining the
     * update.
     */
    public abstract Update getUnregisterUpdate(Name zone)  
        throws DNSModifierException;

    /**
     * Validates whether we can look up the record in the DNS server.
     *
     * @param zone  A zone where this record is registered.
     * @param resol A <code>Resolver</code> to contact the DNS server.
     * @return True if we can validate, false otherwise.
     */
    public abstract boolean validLookup(Name zone, Resolver resol);


    /**
     * Constructs an appropriate record from a context.
     *
     * @param ctx A context with the input parameters.
     * @return A new record that uses the context.
     * @exception SmartFrogDeploymentException if an error occurs
     * while creating the DNS record from the context.
     */
    public static DNSRecord newRecord(Context ctx) 
        throws SmartFrogDeploymentException {

        String recordType = DNSComponentImpl.getString(ctx, RECORD_TYPE, null);
        if (recordType == null) {
            throw new SmartFrogDeploymentException("newrecord: no recordType" 
                                                   + ctx);
        }
        if (TYPE_A.equals(recordType)) {
            //TYPE_A
            return newA(getHostNameHelper(ctx), getIPAddressHelper(ctx));
        } else if (TYPE_PTR.equals(recordType)) {
            //TYPE_PTR
            Name alias = getAliasHelper(ctx);
            if (alias == null) {
                return newPTR(getHostNameHelper(ctx), getIPAddressHelper(ctx));
            } else {
                return newPTR(getHostNameHelper(ctx), alias);
            }
        } else if (TYPE_SRV.equals(recordType)) {
            //TYPE_SRV
            String serviceName = DNSComponentImpl.getString(ctx, SERVICE_NAME,
                                                            null);
            if (serviceName == null) {
                throw new SmartFrogDeploymentException("newrecord:no serviceType");
            }
            int port =  DNSComponentImpl.getInteger(ctx, PORT, BAD_INTEGER);
            if (port == BAD_INTEGER) {
                throw new SmartFrogDeploymentException("newrecord:no port num");
            }
            int weight =  DNSComponentImpl.getInteger(ctx, WEIGHT,
                                                      BAD_INTEGER);
            if (weight == BAD_INTEGER) {
                throw new SmartFrogDeploymentException("newrecord:no weight");
            }
            int priority =  DNSComponentImpl.getInteger(ctx, PRIORITY,
                                                        BAD_INTEGER);
            if (priority == BAD_INTEGER) {
                throw new SmartFrogDeploymentException("newrecord:no priority");
            }
            return newSRV(serviceName, priority, weight, port,
                          getHostNameHelper(ctx));
        } else if (TYPE_TXT.equals(recordType)) {
            // TYPE_TXT
            String name = DNSComponentImpl.getString(ctx, NAME, null);
            if (name == null) {
                throw new SmartFrogDeploymentException("newrecord:no txtName");
            }
              
            ComponentDescription attrib = 
                DNSComponentImpl.getCD(ctx, ATTRIBUTES, null);
            if (attrib == null) {
                throw new SmartFrogDeploymentException("newrecord:no attrib");
            }
            return newTXT(name, attrib);
        } else {
            //default
            throw new  SmartFrogDeploymentException("newrecord:bad recordType" 
                                                   + recordType);
        }

    }

    /**
     * Extracts the host name from a context
     *
     * @param ctx A context input. 
     * @return A host name.
     * @exception SmartFrogDeploymentException if an error occurs
     */
    public static String getHostNameHelper(Context ctx) 
        throws SmartFrogDeploymentException {

        String hostName = DNSComponentImpl.getString(ctx, HOST_NAME, null);
        if (hostName == null) {
            throw new SmartFrogDeploymentException("No host name");
        }
        return hostName;
    }

    /**
     * Extracts an IP address from a context.
     *
     * @param ctx A context input.
     * @return An ip address.
     * @exception SmartFrogDeploymentException if an error occurs
     */
    public static InetAddress getIPAddressHelper(Context ctx) 
        throws SmartFrogDeploymentException {

        String address =  DNSComponentImpl.getString(ctx, ADDRESS, null);
        if (address == null) {
            throw new SmartFrogDeploymentException("No IP address");
        }     
        try {
            InetAddress ipAddress = InetAddress.getByName(address);
            return ipAddress;
        } catch (UnknownHostException e) {
            throw new SmartFrogDeploymentException("can't resolve "
                                                   + address, e);
        }
    }

    /**
     * Extracts an  alias from a context.
     *
     * @param ctx A context input.
     * @return A name alias or null if there is no alias in the context.
     */
    public static Name getAliasHelper(Context ctx) {

        String alias =  DNSComponentImpl.getString(ctx, ALIAS, null);
        if (alias == null) {
            return null;
        }     
        try {
            return toAbsoluteName(alias);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Factory method to construct an address record.
     *
     * @param hostName A host name for this entry.
     * @param ipAddress An ip address for this entry.
     * @return An address record.
     */
    public static DNSRecord newA(String hostName,
                                 InetAddress ipAddress) {

        return new DNSRecordA(hostName, ipAddress);
    }
                                 
    /**
     * Factory method to construct a reverse lookup PTR record.
     *
     * @param hostName A host name for this entry.
     * @param ipAddress An ip address for this entry.
     * @return A reverse lookup PTR record.
     */
    public static DNSRecord newPTR(String hostName,
                                   InetAddress ipAddress) {

        return new DNSRecordPTR(hostName, ipAddress);
    }

    /**
     * Factory method to construct an alias PTR record.
     *
     * @param hostName A host name for this entry.
     * @param alias An aliased name for this entry.
     * @return An alias PTR record.
     */
    public static DNSRecord newPTR(String hostName,
                                   Name alias) {

        return new DNSRecordPTR(hostName, alias);
    }
    
    /**
     *  Factory method to construct a SRV record.
     *
     * @param serviceName a name for this service.
     * @param priority A priority value.
     * @param weight A weight value.
     * @param port A port number that the service listens to.
     * @param server A host name for the server hosting the service.
     * @return A SRV record.
     */
    public static DNSRecord newSRV(String serviceName,
                                   int priority, int weight, int port,
                                   String server) {

        return new DNSRecordSRV(serviceName, priority, weight, port, server);
    }
    
                                  

    /**
     * Factory method to construct a TXT DNS record.
     *
     * @param nameRecord A name for this record.
     * @param cd A description of attribute/ values in the record.
     * @return A TXT record. 
     */
    public static DNSRecord newTXT(String nameRecord,
                                   ComponentDescription cd) {

        return new DNSRecordTXT(nameRecord, cd);
    }


    /**
     * Creates an update record for the requested operation/ entry.
     *
     * @param zone A zone for the record update.
     * @param entry An entry to be updated.
     * @param op Operation to be performed.
     * @param value Value to be registered/ removed.
     * @return An update to perform the operation.
     * @exception DNSModifierException if an error occurs
     */
    Update createUpdate(Name zone, Name entry, int op, String value) 
        throws DNSModifierException {

        try {
            if (!entry.subdomain(zone))  {
                throw new DNSModifierException("hostName " + entry 
                                               + " not subdomain of zone "
                                               + zone);
            }
            Update update = new Update(zone);
            switch (op) { 
            case DELETE: 
                update.delete(entry, dType, value);
                break;
            case ADD:
                update.add(entry,  dType, ttl,  value);
                break;
            case REPLACE:
                update.replace(entry,  dType, ttl,  value);
                break;
            default:
                throw  new DNSModifierException("invalid op " 
                                                + Integer.toString(op));
            }
            return update;
        } catch (Exception e) {
            if (e instanceof DNSModifierException) {
                throw (DNSModifierException) e;
            } else {
                throw new DNSModifierException("can't create update ", e);
            }
        }
    }


    /**
     * Performs a generic DNS look-up.
     *
     * @param zone A zone for the record look-up.
     * @param entry An entry to be looked-up.
     * @param res A resolver.
     * @param target An expected result.
     * @return True if we validate the look-up fine.
     */
    boolean validateLookup(Name zone, Name entry, Resolver res,
                           Record target) {

        try {
            if (!entry.subdomain(zone))  {
                throw new DNSModifierException("lookup:hostName " + entry 
                                               + " not subdomain of zone "
                                               + zone);
            }
            Lookup lu = new Lookup(entry, dType);
            lu.setResolver(res);
            lu.setCache(null);
            lu.run();
            boolean isOK = false;
            if (lu.getResult() == Lookup.SUCCESSFUL) {
                Record[] answers = lu.getAnswers();
                for (int i=0; i < answers.length; i++) {
                    if (target.equals(answers[i])) {
                        isOK = true;
                    }
                }
            }
            return isOK;
        }  catch (Exception e) {
            // need to log exception
            return false;
        }
    }


    /**
     * Transforms a string into an absolute name (a "." is added if needed)
     *
     * @param str A  name encoded as a string.
     * @return An absolute  name.
     */
    public static Name toAbsoluteName(String str) {
        
        try {
            String input = (str.endsWith(".") ? str : str + ".");
            return Name.fromString(input);
        } catch (Exception e) {
            throw new IllegalArgumentException("toAbsoluteName: can't convert "
                                               + str);
        }
    }  


    /**
     * Reverses an IP address and returns a string that concatenates
     * the address to a zone name eliminating redundant subnets already
     * specified in the zone name.
     *
     * @param addressZone A zone name for reverse look-ups of that IP address.
     * @param addr An IP address to be reversed and partially added
     * to the zone name.
     * @return A string that concatenates
     * the address to a zone name eliminating redundant subnets already
     * specified in the zone name.
     */
    public static String reverseAddress(Name addressZone,
                                        InetAddress addr) {

      	byte[] bytes = addr.getAddress();  
        String[] subNets = new String[4];
        for (int i=0; i<4; i++) {
            subNets[3-i] = Integer.toString((int) (bytes[i] & 0xFF));
        }
        String[] allSuffix = new String[4];
        String[] allPrefix = new String[4];
        for (int i=0; i<4; i++) {
            // compute all possible prefix
            StringBuffer buf1 = new StringBuffer();
            for (int j=0; j<i; j++) {
                buf1.append(subNets[j]);
                buf1.append(".");
            }
            allPrefix[i] = buf1.toString();
            // compute all possible suffix
            StringBuffer buf  = new StringBuffer();
            for (int j=i; j<4; j++) {
                buf.append(subNets[j]);
                buf.append(".");
            } 
            allSuffix[i] = buf.toString();
        }

        String zoneStr = addressZone.toString();
        for (int i=0; i<4; i++) {
            if (zoneStr.startsWith(allSuffix[i])) {
                return (allPrefix[i] + zoneStr);
            }
        }
        
        // a.b.c. + d. + in-addr...  (full concat)
        return allPrefix[3] + allSuffix[3] + zoneStr;
    }


    /**
     * An address type DNS record.
     *
     */
    private static class DNSRecordA extends DNSRecordImpl {

        
        /* An inet address for the record. */
        InetAddress ipAddress = null;

        
        /**
         * Creates a new <code>DNSRecordA</code> instance.
         *
         * @param hostName A host name for this entry.
         * @param ipAddress An ip address for this entry.
         */
        public DNSRecordA(String hostName,
                          InetAddress ipAddress) {

            super(toAbsoluteName(hostName),  DClass.IN, Type.A,
                  hostName+"@"+ipAddress.toString());
            this.ipAddress = ipAddress;
        }

        /**
         * Gets an update record that will register this record in a 
         * given zone.
         *
         * @param zone A zone where this record is registered.
         * @return  An update record that will register this record in a 
         * given zone.
         * @exception DNSModifierException if an error occurs while obtaining the
         * update.
         */
        public  Update getRegisterUpdate(Name zone)  
            throws DNSModifierException {

            int op = (replaceAll ? REPLACE : ADD); 
            return createUpdate(zone, entryName, op,
                                ipAddress.getHostAddress());
        }

        /**
         * Gets an update record that will unregister this record in a 
         * given zone.
         *
         * @param zone A zone where this record is registered.
         * @return  An update record that will unregister this record in a 
         * given zone.
         * @exception DNSModifierException if an error occurs while obtaining the
         * update.
         */
        public  Update getUnregisterUpdate(Name zone)  
            throws DNSModifierException {

            return createUpdate(zone, entryName, DELETE,
                                ipAddress.getHostAddress());
        }

        /**
         * Validates whether we can look up the record in the DNS server.
         *
         * @param zone  A zone where this record is registered.
         * @param resol A <code>Resolver</code> to contact the DNS server.
         * @return True if we can validate, false otherwise.
         */
        public  boolean validLookup(Name zone, Resolver resol) {
            
            return validateLookup(zone, entryName, resol,
                                  new ARecord(entryName, dClass, ttl,
                                              ipAddress));
        }
    }

    /**
     * A pointer type DNS record.
     *
     */
    private static class DNSRecordPTR extends DNSRecordImpl {

        
        /** An inet address for the record. */
        InetAddress ipAddress = null;


        /** A host name  for this record value. */
        Name value = null;

        
        /**
         * Creates a new <code>DNSRecordPTR</code> instance.
         *
         * @param hostName A host name for this entry.
         * @param ipAddress An ip address for this entry.
         */
        public DNSRecordPTR(String hostName,
                            InetAddress ipAddress) {

            super(null,  DClass.IN, Type.PTR,
                  ipAddress.toString() + "@" + hostName);
            value = toAbsoluteName(hostName);
            this.ipAddress = ipAddress;
        }
        
        /**
         * Creates a new <code>DNSRecordPTR</code> instance.
         *
         * @param serviceTypeName A name for this service entry.
         * @param alias A value  name for this record.
         */
        public DNSRecordPTR(String serviceTypeName,
                            Name alias) {

            super(toAbsoluteName(serviceTypeName),  DClass.IN, Type.PTR,
                  serviceTypeName + "@" + alias);
            value = alias;
        }


        /**
         * Gets an update record that will register this record in a 
         * given zone.
         *
         * @param zone A zone where this record is registered.
         * @return  An update record that will register this record in a 
         * given zone.
         * @exception DNSModifierException if an error occurs while obtaining the
         * update.
         */
        public  Update getRegisterUpdate(Name zone)  
            throws DNSModifierException {

            int op = (replaceAll ? REPLACE : ADD); 
            if (ipAddress != null) {
                Name ip = toAbsoluteName(reverseAddress(zone, ipAddress));
                return createUpdate(zone, ip, op, value.toString());
            } else {
                return createUpdate(zone, entryName, op, value.toString());
            }
        }

        /**
         * Gets an update record that will unregister this record in a 
         * given zone.
         *
         * @param zone A zone where this record is registered.
         * @return  An update record that will unregister this record in a 
         * given zone.
         * @exception DNSModifierException if an error occurs while obtaining the
         * update.
         */
        public  Update getUnregisterUpdate(Name zone)  
            throws DNSModifierException {

            if (ipAddress != null) {
                Name ip = toAbsoluteName(reverseAddress(zone, ipAddress));
                return createUpdate(zone, ip, DELETE, value.toString());
            } else {
                return createUpdate(zone, entryName, DELETE, value.toString());
            }
        }

        /**
         * Validates whether we can look up the record in the DNS server.
         *
         * @param zone  A zone where this record is registered.
         * @param resol A <code>Resolver</code> to contact the DNS server.
         * @return True if we can validate, false otherwise.
         */
        public  boolean validLookup(Name zone, Resolver resol) {
            
            if (ipAddress != null) {
                Name ip = toAbsoluteName(reverseAddress(zone, ipAddress));
                return validateLookup(zone, ip, resol,
                                      new PTRRecord(ip, dClass, ttl, value));
            } else {
                return validateLookup(zone, entryName, resol,
                                      new PTRRecord(entryName, dClass, ttl,
                                                    value));
            }
        }
    }

    /**
     * A service type DNS record.
     *
     */
    private static class DNSRecordSRV extends DNSRecordImpl {

   
        /**A priority value */
        int priority;

        /**  A weight value*/
        int weight;

        /** A port number that the service listens to. */
        int port;
        
        /** A host name for the server hosting the service. */
        Name serverName;
        
        /** A String with the added rr fields. */
        String rdataString = null;

        /**
         * Creates a new <code>DNSRecordSRV</code> instance.
         * @param serviceName a name for this service.
         * @param priority A priority value.
         * @param weight A weight value.
         * @param port A port number that the service listens to.
         * @param server A host name for the server hosting the service.
         */
        public DNSRecordSRV(String serviceName,
                            int priority, int weight, int port,
                            String server) {

            super(toAbsoluteName(serviceName),  DClass.IN, Type.SRV,
                  serviceName+"@"+server + ":" + Integer.toString(port));
            this.serverName = toAbsoluteName(server);
            this.priority = priority;
            this.weight = weight;
            this.port = port;
            SRVRecord rec = new SRVRecord(entryName, dClass, ttl,
                                          priority, weight, port, serverName);
            rdataString =  rec.rdataToString();
        }

        /**
         * Gets an update record that will register this record in a 
         * given zone.
         *
         * @param zone A zone where this record is registered.
         * @return  An update record that will register this record in a 
         * given zone.
         * @exception DNSModifierException if an error occurs while obtaining the
         * update.
         */
        public  Update getRegisterUpdate(Name zone)  
            throws DNSModifierException {

            int op = (replaceAll ? REPLACE : ADD); 
            return createUpdate(zone, entryName, op, rdataString);
        }

        /**
         * Gets an update record that will unregister this record in a 
         * given zone.
         *
         * @param zone A zone where this record is registered.
         * @return  An update record that will unregister this record in a 
         * given zone.
         * @exception DNSModifierException if an error occurs while obtaining the
         * update.
         */
        public  Update getUnregisterUpdate(Name zone)  
            throws DNSModifierException {

            
            return createUpdate(zone, entryName, DELETE, rdataString);
        }

        /**
         * Validates whether we can look up the record in the DNS server.
         *
         * @param zone  A zone where this record is registered.
         * @param resol A <code>Resolver</code> to contact the DNS server.
         * @return True if we can validate, false otherwise.
         */
        public  boolean validLookup(Name zone, Resolver resol) {
            
            return validateLookup(zone, entryName, resol,
                                  new SRVRecord(entryName, dClass, ttl,
                                                priority, weight, port,
                                                serverName));
        }
    }

    /**
     * A TXT type DNS record representing a service.
     *
     */
    private static class DNSRecordTXT extends DNSRecordImpl {

        /** A string containing the body of the txt entry. */
        String txtString = null;

        /** A processed list of strings to be included in the record. */
        List allStrings = null;
        
        /**
         * Creates a new <code>DNSRecordTXT</code> instance.
         *
         * @param nameRecord a Name for this record.
         * @param cd A list of attribute/value pairs to be included.
         */
        public DNSRecordTXT(String nameRecord,
                            ComponentDescription cd) {

            super(toAbsoluteName(nameRecord),  DClass.IN, Type.TXT,
                  nameRecord + ":" + cd.toString());
            allStrings = DNSServiceImpl.cdToTXT(cd);
            TXTRecord rec = new TXTRecord(entryName, dClass, ttl, 
                                          allStrings);
            txtString =  rec.rdataToString();
        }

        /**
         * Gets an update record that will register this record in a 
         * given zone.
         *
         * @param zone A zone where this record is registered.
         * @return  An update record that will register this record in a 
         * given zone.
         * @exception DNSModifierException if an error occurs while obtaining the
         * update.
         */
        public  Update getRegisterUpdate(Name zone)  
            throws DNSModifierException {

            int op = (replaceAll ? REPLACE : ADD); 
            return createUpdate(zone, entryName, op, txtString);
        }

        /**
         * Gets an update record that will unregister this record in a 
         * given zone.
         *
         * @param zone A zone where this record is registered.
         * @return  An update record that will unregister this record in a 
         * given zone.
         * @exception DNSModifierException if an error occurs while obtaining the
         * update.
         */
        public  Update getUnregisterUpdate(Name zone)  
            throws DNSModifierException {

            return createUpdate(zone, entryName, DELETE, txtString);
        }

        /**
         * Validates whether we can look up the record in the DNS server.
         *
         * @param zone  A zone where this record is registered.
         * @param resol A <code>Resolver</code> to contact the DNS server.
         * @return True if we can validate, false otherwise.
         */
        public  boolean validLookup(Name zone, Resolver resol) {
            
            return validateLookup(zone, entryName, resol,
                                  new TXTRecord(entryName, dClass, ttl, 
                                                allStrings));

        }
    }

}
