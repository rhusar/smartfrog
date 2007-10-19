/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

 */
package org.smartfrog.services.slp;

/**
 * The service type represents a service advertisement's type.
 * They may be of three types :
 * - simple type    : 'service:simpletype' e.g. 'service:http' , 'service:telnet' , 'service:smartfrog.demo'.
 * - abstract type  : 'service:abstract-type-name:concrete-type-name' e.g. 'service:login:telnet'.
 * - any URL scheme : 'http:' for example.
 *
 * @author Guillaume Mecheneau
 */
public class ServiceType implements java.io.Serializable{

  /** The default nmaming authority */
  public static final String IANA = "";
  /** The SLP Service prefix */
  public static final String servicePrefix = "service:";
  private String concreteType = "";
  private String abstractType = "";
  private String simpleType = "";

  String typeName;
  String namingAuthority = IANA;;

  boolean isServiceType = false,isAbstract = false;
/**
 * Create a service type object from the type name.
 * The name may take the form of any valid service type name.
 * Type-name may be  :
 *  simple (ex --> service:http[.na])
 *  abstract (ex--> service:login[.na]:ftp)
 *  or standard url scheme, in which case it is not a service:url
 * Throws: IllegalArgumentException if the name is syntactically incorrect.
 */
  public ServiceType(String typeName) throws IllegalArgumentException{
    this.typeName = typeName;
    String temporaryTypeName = "";
    if (typeName.indexOf(servicePrefix)!=-1) {
      isServiceType = true ;
//      int i = typeName.indexOf(":"); // daft. servicePrefix contains ':'
//      if (i == -1)
//        throw new IllegalArgumentException("Invalid URL Format");
//      else
      temporaryTypeName = typeName.substring(servicePrefix.length());
    } else{
      isServiceType = false;
      return;
    }
    // simple or abstract ?
    isAbstract = (temporaryTypeName.indexOf(":")!=-1);
    // look for a naming authority
    int type_na_separator = temporaryTypeName.indexOf(".");
    if (type_na_separator!=-1){
      String _na = temporaryTypeName.substring(type_na_separator+1);
      if (_na.indexOf(":")!=-1)
        _na = _na.substring(0,_na.indexOf(":"));
      this.namingAuthority = _na;
    }

    // remove naming authority from type name;
    String undefinedType = temporaryTypeName;
    if (!namingAuthority.equals(IANA))
      undefinedType = temporaryTypeName.substring(0,temporaryTypeName.indexOf("."));
//    System.out.println("undefinedType = temporaryTypeName.substring(0,temporaryTypeName.indexOf(\".\"));" + undefinedType);
    // parse abstract and concrete names
    if (isAbstractType()){
      abstractType = (!namingAuthority.equals(IANA)) ?
              temporaryTypeName.substring(0,temporaryTypeName.indexOf(".")):
              temporaryTypeName.substring(0,temporaryTypeName.indexOf(":"));
      concreteType = temporaryTypeName.substring(temporaryTypeName.indexOf(":")+1);
    } else {
      simpleType = undefinedType;
    }
  }

  /**
   *  Return true if the type name came from service: URL.
   *  @return a flag indicating whether the URL string which this object
   *  was constructed with was a Service URL or not.
   */
  public boolean isServiceURL(){
    return isServiceType;
  }

  /**
   * Return true if type name is for an abstract type.
   * @return a flag indicating whether the service type is abstract or not.
   */
  public boolean isAbstractType() {
    return isAbstract;
  }


  /**
   * Return true if naming authority is default.
   * @return a flag indicating whether the naming authority of this service type is the IANA default or not.
   */
  public boolean isNADefault() {
    return namingAuthority.equals(IANA);
  }

  /**
   * The concrete type name without naming authority.
   * @return a String representing the concrete type name.
   */
  public String getConcreteTypeName() {
    if (isAbstractType())
      return concreteType;
    return "";
  }

  /**
   * The principle type name, which is either the abstract type name
   * or the protocol name, without naming authority.
   * @return a String representing the principle type name .
   */
  public String getPrincipleTypeName() {
    if (isAbstractType())
      return abstractType;
    else
      return simpleType;
  }

  /**
   * The fully formatted abstract type name, if it is an abstract type,
   * otherwise the empty string.
   * @return a String representing the abstract type name.
   */
  public String getAbstractTypeName() {
    if (isAbstractType())
      return
//      ServiceType.servicePrefix+
      abstractType;
    return "";
  }

/**
 * The naming authority name. IANA default is the empty String.
 * @return a String representing the naming authority
 */
  public String getNamingAuthority() {
    return namingAuthority;
  }

/**
 * Return true if the parameter is a ServiceType object and the type names match.
 */
  public boolean equals(Object o) {
    if (o instanceof ServiceType)
      return typeName.equals( ((ServiceType) o).typeName);
    return false;
  }

/**
 * Return a hashcode of the service type
 */
  public int hashCode() {
//    int result = 0;
//    if (isServiceType) {
//      result += ServiceType.servicePrefix.hashCode();
//      result += isAbstractType()?
//        (getAbstractTypeName() + getNamingAuthority() + getConcreteTypeName()).hashCode():
//        (getPrincipleTypeName()+ getNamingAuthority()).hashCode() ;
//    } else {
//      // it is a URL, simply return scheme.
//      result = typeName.hashCode();
//    }
//    return result;
    return toString().hashCode();
  }
/**
 * The service type name, as a string, formatted as in the call to the constructor.
 * @return a String representing the service type
 */
  public String toString() {
    String result = "";
    if (isServiceType) {
      result += ServiceType.servicePrefix;
      String naPrefix = getNamingAuthority().equals(this.IANA) ? "" : ".";
      result += isAbstractType()?
        getAbstractTypeName() + naPrefix + getNamingAuthority() +":"+ getConcreteTypeName():
        getPrincipleTypeName()+ naPrefix + getNamingAuthority() ;
    } else {
      // it is a URL, simply return scheme.
      result = typeName;
    }
    return result;
  }
}
