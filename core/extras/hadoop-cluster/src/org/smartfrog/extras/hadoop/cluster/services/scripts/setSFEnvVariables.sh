# Switch to a less strict security manager, as Hadoop does not like a full one
export SFSECURITYMANAGER=org.smartfrog.sfcore.security.ExitTrappingSecurityManager