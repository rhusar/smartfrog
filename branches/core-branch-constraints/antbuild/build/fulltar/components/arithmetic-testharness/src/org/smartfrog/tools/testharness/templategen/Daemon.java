package org.smartfrog.tools.testharness.templategen;


/** A bean class to store information about a daemon.
 *
 */
public class Daemon {

  /** A host in which this daemon is located.*/
  public String host;

  /** A logical name for this daemon.*/
  public String name;

  /** PrincipalMonitoredDaemon or just plain MonitoredDaemon*/
  public String type;

  /** Smartfrog home directory for this daemon.*/
  public String sfHome;

  /** OS (Windows or Linux) of the daemon.*/
  public String os;

  /** A daemon with special behaviour like hosting a web server.*/
  public static final String PRINCIPAL_TYPE="PrincipalMonitoredDaemon";

  /** A common daemon type.*/
  public static final String NORMAL_TYPE="MonitoredDaemon";

  /** An identifier for a windows machine. */
  public static final String WINDOWS="windows";

  /** An identifier for a linux machine. */
  public static final String LINUX="linux";


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
