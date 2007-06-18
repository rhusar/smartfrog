package org.smartfrog.tools.testharness.templategen;


/** A bean class to store information about a daemon.
 *
 */
public class Daemon {

  /** A host in which this daemon is located.*/
  String host;

  /** A logical name for this daemon.*/
  String name;

  /** PrincipalMonitoredDaemon or just plain MonitoredDaemon*/
  String type;

  /** Smartfrog home directory for this daemon.*/
  String sfHome;

  /** OS (Windows or Linux) of the daemon.*/
  String os;

  /** A daemon with special behaviour like hosting a web server.*/
  public final static String PRINCIPAL_TYPE="PrincipalMonitoredDaemon";

  /** A common daemon type.*/
  public final static String NORMAL_TYPE="MonitoredDaemon";

  /** An identifier for a windows machine. */
  public final static String WINDOWS="windows";

  /** An identifier for a linux machine. */
  public final static String LINUX="linux";


  public Daemon(String name,String host, String sfHome, String os,
                boolean isPrincipal) {
    
    if ((name==null) || (host==null) || (sfHome ==null) ||(os ==null))
      throw new IllegalArgumentException("invalid inputs "+name+host+
                                         sfHome+os);
    if (!((os.equals(WINDOWS) || (os.equals(LINUX))))) {
       throw new IllegalArgumentException("invalid OS "+os);
    }
    this.host = host;
    this.name = name;
    this.sfHome = sfHome;
    this.type = ((isPrincipal) ? PRINCIPAL_TYPE : NORMAL_TYPE);
    this.os = os;
  }
      

  public String getHost() { return host;}

  public String getName() { return name;}

  public String getType() { return type;}

  public String getSfHome() { return sfHome;}

  public boolean isWindows() { return os.equals(WINDOWS);}

  public boolean isLinux() { return os.equals(LINUX);}


}
