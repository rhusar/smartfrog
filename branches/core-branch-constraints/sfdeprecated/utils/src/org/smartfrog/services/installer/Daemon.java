package org.smartfrog.services.installer;


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
  String user;

  String passwordfile;

  String localfile1;
  
  String localfile2;
  
  String localfile3;
  
  String releasename;

  String emailto;

  String emailfrom;

  String emailserver;

  String javahome;

  /** OS (Windows or Linux) of the daemon.*/
  String os;

  /** A common daemon type.*/
  public final static String TYPE="SFInstaller";

  /** An identifier for a windows machine. */
  public final static String WINDOWS="windows";

  /** An identifier for a linux machine. */
  public final static String LINUX="linux";

  /** An identifier for a root user. */
  public final static String ROOT="root";

  public Daemon(String name,String host, String user, String passwordfile, String localfile1, String localfile2, String localfile3, String releasename, String emailto, String emailfrom, String emailserver, String os, String javahome) {
   
    /*if ((os.equals(WINDOWS) && (!os.equals(LINUX)))) {
       throw new IllegalArgumentException("invalid OS "+os);
    }*/
    if (os.equals(LINUX))
       { 
    	if ((name==null) || (host==null) || (user ==null) || (passwordfile ==null) || (localfile1==null) || (releasename==null) || (emailto==null) || (emailfrom==null) || (emailserver==null) ||(os ==null))
      throw new IllegalArgumentException("invalid inputs "+ name + host + user +					passwordfile + localfile1 + releasename
		     		        + emailto + emailfrom + emailserver 
					+ os);
	 }
	 if (os.equals(WINDOWS))
	 {
	 if ((name==null) || (host==null) || (user == null) || (passwordfile == null) || (localfile1==null) || (localfile2==null) || (localfile3==null) || (releasename==null) || (emailto==null) || (emailfrom==null) || (emailserver==null) ||(os ==null) || (javahome ==null))
      throw new IllegalArgumentException("invalid inputs "+ name + host + user + 					passwordfile + localfile1 + localfile2 + 					localfile3 + releasename + emailto + 
		      			emailfrom + emailserver + os + 
					javahome);
	 }
    this.host = host;
    this.name = name;
    this.user = user;
    this.passwordfile = passwordfile;
    this.localfile1 = localfile1;
    this.localfile2 = localfile2;
    this.localfile3 = localfile3;
    this.releasename = releasename;
    this.emailto = emailto;
    this.emailfrom = emailfrom;
    this.emailserver = emailserver;
    this.type = TYPE;
    this.os = os;
    this.javahome=javahome;
  }
      

  public String getHost() { return host;}

  public String getName() { return name;}

  public String getUser() { return user;}
  
  public String getType() { return type;}
  
  public String getPasswordfile() { return passwordfile;}
  
  public String getLocalfile1() { return localfile1;}
  
  public String getLocalfile2() { return localfile2;}
  
  public String getLocalfile3() { return localfile3;}
  
  public String getReleasename() { return releasename;}
  
  public String getEmailto() { return emailto;}
  
  public String getEmailfrom() { return emailfrom;}
  
  public String getEmailserver() { return emailserver;}
  
  public String getJavahome() { return javahome;}
  
  public boolean isWindows() { return os.equals(WINDOWS);}

  public boolean isLinux() { return os.equals(LINUX);}

  public boolean isRoot() { return user.equals(ROOT);}
}
