package org.smartfrog.tools.testharness.templategen;


/** A bean class to store information about a daemon.
 *
 */
public class RemoteDaemon {

  /** A host in which this daemon is located.*/
  String host;

  /** A logical name for this daemon.*/
  String user;

  /** PrincipalMonitoredDaemon or just plain MonitoredDaemon*/
  String type;

  /** Smartfrog home directory for this daemon.*/
  String password;

  /** OS (Windows or Linux) of the daemon.*/
  String os;

  /** An identifier for a windows machine. */
  public final static String WINDOWS="windows";

  /** An identifier for a linux machine. */
  public final static String LINUX="linux";


  public RemoteDaemon(String host, String user, String password, String os) {
    
    if ((user==null) || (host==null) || (password ==null) ||(os ==null))
      throw new IllegalArgumentException("invalid inputs "+user+host+
                                         password+os);
    if (!((os.equals(WINDOWS) || (os.equals(LINUX))))) {
       throw new IllegalArgumentException("invalid OS "+os);
    }
    this.host = host;
    this.user = user;
    this.password = password;
    this.os = os;
  }
      
  public String getHost() { return host;}

  public String getType() { return type;}

  public String getUser() { return user;}
  
  public String getPassword() { return password;}

  public boolean isWindows() { return os.equals(WINDOWS);}

  public boolean isLinux() { return os.equals(LINUX);}

}
