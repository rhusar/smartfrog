package org.smartfrog.services.sfinstaller;


/**
 * A bean class to store information about a daemon.
 *
 * @author Ritu Sabharwal
 */
public class Daemon {

    /**
     * A host in which this daemon will be located.
     */
    String host;

    /**
     * A logical name for this daemon.
     */
    String name;

    /**
     * The type of workflow for starting the daemon.
     */
    String type;

    /**
     * The user for starting the daemon.
     */
    String user;

    /**
     * The transfer mechanism for tranfering files to remote node.
     */
    String transfertype;

    /**
     * The login mechanism for starting daemon on remote node.
     */
    String logintype;

    /**
     * A file for reading the passowrd.
     */
    String password;

    /**
     * The release file to be transfered.
     */
    String localfile1;

    /**
     * The javaservice executable to be tranfered for windows machine.
     */
    String localfile2;

    /**
     * The javaservice wrapper for starting the daemon for windows machine.
     */
    String localfile3;

    /**
     * A keyfile for remote host for enabling security
     */
    String keyfile;

    /**
     * A security properties file for remote host for enabling security
     */
    String secproperties;

    /**
     * A signed Smartfrog jar file for remote host for enabling security
     */
    String smartfrogjar;

    /**
     * A signed Smartfrog services jar file for remote host for enabling security
     */
    String servicesjar;

    /**
     * A signed Smartfrog examples jar file for remote host for enabling security
     */
    String examplesjar;

    /**
     * Release name to be installed on remote node.
     */
    String releasename;

    /**
     * Java home on remote node for windows machine.
     */
    String javahome;

    /**
     * OS (Windows or Linux) of the daemon.
     */
    String os;

    /**
     * Installation directory on remote node .
     */
    String installdir;

    String emailto;

    String emailfrom;

    String emailserver;

    /**
     * A common workflow type for staring the daemon.
     */
    public final static String TYPE = "Sequence";

    /**
     * An identifier for a windows machine.
     */
    public final static String WINDOWS = "windows";

    /**
     * An identifier for a linux machine.
     */
    public final static String LINUX = "linux";

    /**
     * An identifier for a root user.
     */
    public final static String ROOT = "root";

    /**
     * An identifier for ftp type.
     */
    public final static String FTP = "ftp";

    /**
     * An identifier for a scp type.
     */
    public final static String SCP = "scp";

    /**
     * An identifier for a telnet type.
     */
    public final static String TELNET = "telnet";

    /**
     * An identifier for a ssh type.
     */
    public final static String SSH = "ssh";

    /**
     * Class Constructor.
     *
     * @param name          logical name for the daemon
     * @param os            OS (Windows or Linux) for the daemon
     * @param host          hostname
     * @param transfertype  Transfertype
     * @param logintype     LoginType
     * @param user          username for logging in
     * @param password      password
     * @param localfile1    release  file
     * @param localfile2    java service executable
     * @param localfile3    javaservice wrapper fpr startinf daemon
     * @param keyfile       Security key file
     * @param secproperties Security properties file
     * @param smartfrogjar  Signed smartfrog jar file
     * @param servicesjar   Signed smartfrog services jar file
     * @param examplesjar   Signed smartfrog examples jar file
     * @param releasename   Release name
     * @param javahome      Java home on remote node
     * @param installdir    installation directory on remote node
     * @param emailto       Comma separated email addresses for to
     * @param emailfrom     email address from which mail is being send
     * @param emailserver   SMTP Server used to send emails over SMTP protocol
     */
    public Daemon(String name, String os, String host, String transfertype, String logintype, String user, String password, String localfile1, String localfile2, String localfile3, String keyfile, String secproperties, String smartfrogjar, String servicesjar, String examplesjar, String releasename, String javahome, String installdir, String emailto, String emailfrom, String emailserver) {

        require("name", name);
        require("os", os);
        require("host", host);
        require("transfertype", transfertype);
        require("logintype", logintype);
        require("user", user);
        require("password", password);
        require("localfile1", localfile1);
        require("releasename", releasename);
        require("javahome", javahome);
        require("installdir", installdir);
        require("emailto", emailto);
        require("emailfrom", emailfrom);
        require("emailserver", emailserver);

        if (os.equals(WINDOWS)) {
            require("localfile2", localfile2);
            require("localfile3", localfile3);
        }
        this.name = name;
        this.os = os;
        this.host = host;
        this.transfertype = transfertype;
        this.logintype = logintype;
        this.user = user;
        this.password = password;
        this.localfile1 = localfile1;
        this.localfile2 = localfile2;
        this.localfile3 = localfile3;
        this.keyfile = keyfile;
        this.secproperties = secproperties;
        this.smartfrogjar = smartfrogjar;
        this.servicesjar = servicesjar;
        this.examplesjar = examplesjar;
        this.releasename = releasename;
        this.type = TYPE;
        this.javahome = javahome;
        this.installdir = installdir;
        this.emailto = emailto;
        this.emailfrom = emailfrom;
        this.emailserver = emailserver;
    }

    /**
     * Check that a value is not null
     *
     * @param name  name of the argument
     * @param value current value
     * @throws IllegalArgumentException if there an invalid value
     */
    public void require(String name, String value) {
        if (value == null) {
            throw new IllegalArgumentException("No value supplied to required argument " + name);
        }
    }

    public String getHost() {
        return host;
    }

    public String getName() {
        return name;
    }

    public String getUser() {
        return user;
    }

    public String getType() {
        return type;
    }

    public String getPassword() {
        return password;
    }

    public String getLocalfile1() {
        return localfile1;
    }

    public String getLocalfile2() {
        return localfile2;
    }

    public String getLocalfile3() {
        return localfile3;
    }

    public String getKeyfile() {
        return keyfile;
    }

    public String getSecproperties() {
        return secproperties;
    }

    public String getSmartfrogjar() {
        return smartfrogjar;
    }

    public String getServicesjar() {
        return servicesjar;
    }

    public String getExamplesjar() {
        return examplesjar;
    }

    public String getReleasename() {
        return releasename;
    }

    public String getJavahome() {
        return javahome;
    }

    public String getInstalldir() {
        return installdir;
    }

    public String getEmailto() {
        return emailto;
    }

    public String getEmailfrom() {
        return emailfrom;
    }

    public String getEmailserver() {
        return emailserver;
    }

    public boolean isWindows() {
        return os.equals(WINDOWS);
    }

    public boolean isLinux() {
        return os.equals(LINUX);
    }

    public boolean isRoot() {
        return user.equals(ROOT);
    }

    public boolean isFtp() {
        return transfertype.equals(FTP);
    }

    public boolean isScp() {
        return transfertype.equals(SCP);
    }

    public boolean isTelnet() {
        return logintype.equals(TELNET);}

  public boolean isSsh() { return logintype.equals(SSH);}
}
