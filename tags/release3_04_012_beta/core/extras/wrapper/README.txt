Wrapper
=======

This is a wrapper component to host SmartFrog under the Java Service Wrapper:
http://wrapper.tanukisoftware.org/doc/english/introduction.html

With this module you can configure SmartFrog to automatically start when an
NT service starts, or to act as a Unix daemon.


Starting SmartFrog
==================

To run SmartFrog as a service, you will have to

1. get the jar files you need into a directory that bin/wrapper.conf looks at.
   The ant task "fetch-libraries" pulls in everything from the smartfrog
   dist/lib directory, to aid this process.

2. configure wrapper.conf to load the JAR files you need

3. configure the command line of the daemon to run your process, or modify bin/default.sf

4. add security settings to wrapper.conf

5. modify bin/default.in with any SmartFrog tuning parameters

6. Unix: make sfservice an executable if it is not already, using chmod 



Unix Daemon Notes
=================

The version of the wrapper here is for Linux/x86 only. Please download
a platform-specific version via the web site listed above, if this is
not suitable.


NT Service Notes
================

If you enable the wrapper to interact with the desktop, you can bring up the
SmartFrog GUI. But in doing so you permit privilege escalation: anyone who can
get at the GUI can (maybe) escalate privilege to run code with the authority
and rights of the service. This is due to a fundamental design flaw in the Windows
architecture, and is not fixable in the Java or service wrapper.

The service is currently configured to depend upon no other services. It
is quite likely that the service has some implicit dependencies on the
network working, and so on services such as DNS Client. You may want to
explicity modify the wrapper.conf file to add these dependencies.


Choreographing Network Bootstraps
=================================

It is actually a fun little problem: how do you boot up an interconnected
set of systems in a order that works. Often an NT cluster depends on the
domain controller for mutual authentication, a shared network fileserver may be
needed by the middle or back end servers, etc etc. SmartFrog itself aims to
manage a lot of the choreography of the back end components, but there is still
the challenge of bringing up an entire cluster properly when the power is suddenly
removed and reapplied to the stack. What you really need to do is tell NT services
that they depend upon services on other machines, or on the network itself.

The best trick we have found is that by tuning the 'time to display operating systems'
pause of a windows bootup screen, you can hard code enough of a delay to ensure
that servers come up after the Primary Domain Controller is booted up. Except
that can be quite a large delay if the PDC needs to check the filesystem before continuing.
Give the PDC a small amount of memory, and a minimal amount of disk storage if
you want it to boot fast.

Users may want to consider enhancing the wrapper code here to force a delay until
a remote system is 'reachable', with reachable defined as Java 1.5's
InetAddress.isReachable() returning true, or as an http URL resolving correctly.
We could then spin for a few minutes waiting for smartfrog to boot up.

Alternatively: add this to the SmartFrog component set, so that the boot phase can
be declared in the SmartFrog language. It may already be possible to do this, using
the workflow components.