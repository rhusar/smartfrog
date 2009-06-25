# Switch to a less strict security manager, as Hadoop does not like a full one
export SFSECURITYMANAGER=org.smartfrog.sfcore.security.ExitTrappingSecurityManager
#IPv4 only, please
export SF_OPTS="-Djava.net.preferIPv4Stack=true"