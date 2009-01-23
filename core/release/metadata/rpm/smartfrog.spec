# Copyright (c) 2000-2007, JPackage Project
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
#
# 1. Redistributions of source code must retain the above copyright
#    notice, this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright
#    notice, this list of conditions and the following disclaimer in the
#    documentation and/or other materials provided with the
#    distribution.
# 3. Neither the name of the JPackage Project nor the names of its
#    contributors may be used to endorse or promote products derived
#    from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
# "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
# LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
# A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
# OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
# SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
# LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
# DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
# THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
# OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#

# set the attribute _private_rpm to build a private RPM

# one thing to be aware of is the order that scripts are executed
# -Run %pre of new package
# -Install new files
# -%post of new package
# -%preun of old package
# -Delete any old files not overwritten by newer ones
# -%postun of old package
# what that means is that your %post code runs before the old version is
# uninstalled, and before its %postun runs. So you are in trouble if you need
# to run anything after the old version has uninstalled. 



# TODO: menu entries

# if menu entries are created, define Summary here, and use it in the summary
# tag, and the menu entries' descriptions

%define javadir         %{_datadir}/java
%define section         free

%define approot         %{_datadir}/smartfrog
%define basedir         ${rpm.install.dir}
%define bindir          %{basedir}/bin
%define binsecurity     %{bindir}/security
%define libdir          %{basedir}/lib
%define signedlib       %{basedir}/signedLib
%define docs            %{basedir}/docs
%define jdocs           ${rpm.javadocs.path} 
%define srcdir          %{basedir}/src
%define linkdir         %{basedir}/links
%define examples        %{srcdir}/org/smartfrog/examples
%define rcd             /etc/rc.d
%define smartfrogd      %{rcd}/init.d/${rpm.daemon.name}
%define logdir          ${rpm.log.dir}
%define privatedir      %{basedir}/private


#some shortcuts
%define smartfrog.jar smartfrog-${smartfrog.version}.jar
%define sfExamples.jar sfExamples-${smartfrog.version}.jar
%define sfServices.jar sfServices-${smartfrog.version}.jar

#choose the package name based on the operational mode
%{!?_private_rpm:%define package_name smartfrog}
%{?_private_rpm:%define package_name smartfrog-secure}
%{!?_private_rpm:%define security_text This is an unsigned distribution}
%{?_private_rpm:%define security_text This is a signed distribution with private information in the smartfrog-private rpm}

%{?_private_rpm:%{error: this is a private rpm}}
%{!?_private_rpm:%{error: this is not a private rpm}}



# -----------------------------------------------------------------------------

Summary:        SmartFrog Deployment Framework
Name:           smartfrog
Version:        ${smartfrog.version}
Release:        ${rpm.release.version}
# group, categories from freshmeat.net
Group:          ${rpm.framework}
License:        LGPL
URL:            http://www.smartfrog.org/
Vendor:         ${rpm.vendor}
Packager:       ${rpm.packager}
BuildArch:      noarch
#%{name}-%{version}.tar.gz in the SOURCES dir
Source0:        %{name}-%{version}.tar.gz
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root
Prefix:         ${rpm.prefix}
#Provides: SmartFrog
#Icon: docs/images/frog.gif
# build and runtime requirements here
Requires(rpmlib): rpmlib(CompressedFileNames) <= 3.0.4-1 rpmlib(PayloadFilesHavePrefix) <= 4.0-1

%description
SmartFrog is a technology for describing distributed software systems as
collections of cooperating components, and then activating and managing them.

It was developed at HP Labs in Bristol, in the UK.
SmartFrog consists of a language for describing component collections and
component configuration parameters, and a runtime environment which
activates and manages the components to deliver and maintain running systems.
SmartFrog and its components are implemented in Java.

%{security_text}

The RPM installs smartfrog into
 %{basedir} 
It also adds scripts to /etc/profile.d and /etc/sysconfig 
so that SmartFrog is available on the command line.

In this RPM SmartFrog is configured to log to files 
    /var/log/smartfrog_*.log
with logLevel=3 (INFO) using LogToFileImpl. The GUI is turned off.



# -----------------------------------------------------------------------------

%package javadocs
Group:         ${rpm.framework}
Summary:        Javadocs for %{name}
Requires:       %{name} = %{version}-%{release}
#
%description javadocs
Installs the javadocs for the SmartFrog core, services and examples into
%{basedir}/docs/jdocs


%package demo
Group:         ${rpm.framework}
Summary:        Demos for %{name}
Requires:       %{name} = %{version}-%{release}
#
%description demo
Examples for %{name}.

# -----------------------------------------------------------------------------

%package daemon
Group:         ${rpm.framework}
Summary:        init.d and and /etc/ scripts for %{name}
Requires:       %{name} = %{version}-%{release}
#
%description daemon
This package provides the scripts for /etc/rc.d, as a startup daemon.

Running the SmartFrog as a daemon is a security risk unless the daemon
is set up with security, especially if port 3800 is openened in the firewall.
At that point anyone can get a process running as root to run any program they wish.

# -----------------------------------------------------------------------------

%package anubis
Group:         ${rpm.framework}
Summary:        Anubis partition-aware tuple space
Requires:       %{name} = %{version}-%{release}
#
%description anubis
This package provides Anubis, a partition-aware tuple space.

The Anubis SmartFrog components can be used to build fault-tolerant distributed
systems across a set of machines hosted on a single site. Multicast IP is used
as a heartbeat mechanism.


# -----------------------------------------------------------------------------

%package ant
Group:         ${rpm.framework}
Summary:        Ant integration with SmartFrog
Requires:       %{name} = %{version}-%{release}
#
%description ant 
This package contains the JAR file sf-ant.jar, which contains
components that can run Ant tasks and build files during deployment.
Build file logging and failure is integrated into the overall deployment,
so remote errors will be caught and reported. 

The package also contains the Ant ${apache.ant.version} JAR file:
ant-${apache.ant.version}.jar
It does not contain any of the Ant optional libraries, or their dependencies.

# -----------------------------------------------------------------------------

%package csvfiles
Group:         ${rpm.framework}
Summary:        Components to work with CSV files
Requires:       %{name} = %{version}-%{release}
#
%description csvfiles
The csvfiles package contains components that can work with CSV files, turning the data
into attributes of components, or serving them in a form that other components
including XML components can handle.

This enables CSV files (such as those generated and edited in spreadsheets) to be
used as the direct input for deployment/configuration operations. As many
applications act as sources of CSV files, this allows for some interesting
integrations with existing software.

Includes opencsv-${opencsv.version}.jar


# -----------------------------------------------------------------------------

%package database
Group:         ${rpm.framework}
Summary:        The components needed to talk to a database
Requires:       %{name} = %{version}-%{release}
#
%description database
The database package contains the components needed to talk to a database
during deployment, or when terminating a deployment. It has special component templates.
to start and administer MySQL.

This package does not include any JDBC drivers. The appropriate JDBC driver for the
target system must be installed/added to the sfCodeBase attribute of the components,
in order for JDBC connectivity to work.

# -----------------------------------------------------------------------------

%package jmx
Group:         ${rpm.framework}
Summary:        JMX integration with SmartFrog
Requires:       %{name} = %{version}-%{release}
#
%description jmx
The sf-jmx package can integrate JMX MBeans with SmartFrog, and
export SmartFrog components as MBeans.

The MX4J JARs (version ${mx4j.version}) are included:
mx4j-${mx4j.version}.jar
mx4j-remote-${mx4j.version}.jar
mx4j-jmx-${mx4j.version}.jar
mx4j-tools-${mx4j.version}.jar

# -----------------------------------------------------------------------------

%package logging
Group:         ${rpm.framework}
Summary:        SmartFrog logging services
Requires:       %{name} = %{version}-%{release}
#
%description logging
This package integrates SmartFrog with Apache Log4j. It includes the Apache
commons-logging-${commons-logging.version} and log4j-${log4j.version} libraries

# -----------------------------------------------------------------------------

%package networking
Group:         ${rpm.framework}
Summary:        SmartFrog Networking services
Requires:       %{name} = %{version}-%{release}
#
%description networking
SmartFrog networking components, including DNS, FTP, email and SSH support

The components use the following bundled libraries:
activation-${activation.version}.jar
commons-net-${commons-net.version}.jar
dnsjava-${dnsjava.version}.jar
jsch-${jsch.version}.jar
mail-${mail.version}.jar
oro-${oro.version}.jar

# -----------------------------------------------------------------------------

%package quartz
Group:         ${rpm.framework}
Summary:        Work scheduling with Quartz
Requires:       %{name} = %{version}-%{release} ,  %{name}-logging
#
%description quartz
Work scheduling. These components can be used to schedule work to a pool of machines,
using Quartz to queue the jobs. A CPU monitor component provides information about the
current system state, using the Unix/Linux vmstat command as a source of information.

Contains the Quartz library version quartz-${quartz.version}.jar.

# -----------------------------------------------------------------------------

%package scripting
Group:         ${rpm.framework}
Summary:       Scripting support
Requires:      %{name} = %{version}-%{release}
#
%description scripting
Scripting support.
Includes BeanShell bsh-${bsh.version}.jar
# -----------------------------------------------------------------------------

%package xunit
Group:         ${rpm.framework}
Summary:       Testing under SmartFrog
Requires:       %{name} = %{version}-%{release} , %{name}-logging
#
%description xunit
The base testing components. This contains the sfunit test components
for testing deployments, and the listeners/reporters for running tests.

# -----------------------------------------------------------------------------

%package junit
Group:         ${rpm.framework}
Summary:        JUnit testing
Requires:       %{name} = %{version}-%{release}  , %{name}-xunit
#
%description junit
This contains the components for running JUnit ${junit.version} tests, and the
junit-${junit.version}.jar.
Prerequisite packages: xunit, Logging.
# -----------------------------------------------------------------------------

%package velocity
Group:         ${rpm.framework}
Summary:        Velocity template engine
Requires:       %{name} = %{version}-%{release}  , %{name}-logging
#
%description velocity

This package provides the Apache Velocity template engine
JAR, and a component that can transform a file through it
during deployment. Velocity can be used to generate
text, HTML or XML files on the fly.

It includes the files
velocity-${velocity.version}.jar
velocity-dep-${velocity.version}.jar
commons-collections-${commons-collections.version}.jar
commons-lang-${commons-lang.version}.jar

Prerequisite packages: Logging.

%package www
Group:         ${rpm.framework}
Summary:        WWW components
Requires:       %{name} = %{version}-%{release} , %{name}-logging
#
%description www
This package contains components to deploy web applications on different
Java web servers, from Jetty ${jetty.version} to JBoss. It also contains a LivenessPage
component that can monitor the health of a remote site.

The bundled libraries are
commons-httpclient-${commons-httpclient.version}.jar
commons-codec-${commons-codec.version}.jar
servlet-api-${servletapi.version}.jar
jetty-${jetty.version}.jar

# -----------------------------------------------------------------------------

%package xml
Group:         ${rpm.framework}
Summary:        XML support
Requires:       %{name} = %{version}-%{release}
#
%description xml
This package provides XML support; components to create
XML files and recent versions of the main Java XML libraries.
The package contains xom-${xom.version}.jar, which includes the Jaxen runtime,
jdom-${jdom.version}, Apache Xerces ${xerces.version} and Apache Xalan ${xalan.version}


# -----------------------------------------------------------------------------

%package xmpp
Group:         ${rpm.framework}
Summary:        XMPP/Jabber support
Requires:       %{name} = %{version}-%{release}
#
%description xmpp
This package provides Jabber support: components to register with a Jabber server and
relay notification methods. These can be used to communicate over long distances,
or track the availability of remote systems.

This package uses smack-${smack.version}.jar for XMPP support.


# -----------------------------------------------------------------------------

%package private-security-keys
Group:         ${rpm.framework}
Summary:        Private Security Keys
Requires:       %{name} = %{version}-%{release}
#
%description private-security-keys
This package provides contains private security keys for use in an installation. All
SmartFrog daemons sharing private keys generated by the same (private) certification
authority trust each other.

Do not install a private keys package except within your own organisation; do not
make a privately generated key package publicly available.

%{security_text}

# -----------------------------------------------------------------------------

%prep
#First, create a user or a group (see SFOS-180) 
USERNAME="${rpm.username}"
GROUPNAME="${rpm.groupname}"

# Mabye create a new group
getent group $${GROUPNAME} > /dev/null
if [ $$? -ne 0 ]; then
  groupadd $${GROUPNAME}> /dev/null 2>&1
  if [ $$? -ne 0 ]; then
    logger -p auth.err -t %{name} $${GROUPNAME} group could not be created
    exit 1
  fi
else
  logger -p auth.info -t %{name} $${GROUPNAME} group already exists
fi

# Maybe create a new user
# Creation of smartfrog user account
# UID value will be fetched from the system
# Any free least numeric number will get assigned to UID
# User deletion is left to the System Administartor
getent passwd $${USERNAME} > /dev/null 2>&1
if [ $$? -ne 0 ]; then
  useradd -g ${GROUPNAME} -s /bin/bash -p "*********" -m $${USERNAME} >> /dev/null
  if [ $$? -ne 0 ]; then
    logger -p auth.err -t %{name} $${USERNAME} user could not be created
    exit 2
  fi
else
  logger -p auth.info -t %{name} $${USERNAME} user already exists
fi

#Now run the big setup
%setup -q -c



# patches here
# remove stuff we'll build, eg. jars, javadocs, extra sources here

# -----------------------------------------------------------------------------

%build
rm -rf $RPM_BUILD_ROOT
pwd
cp -dpr . $RPM_BUILD_ROOT


# -----------------------------------------------------------------------------

%clean
rm -rf $RPM_BUILD_ROOT

# -----------------------------------------------------------------------------

%files javadocs
%defattr(0644,${rpm.username},${rpm.groupname},0755)
%{jdocs}

# -----------------------------------------------------------------------------

%files
%defattr(0644,${rpm.username},${rpm.groupname},0755)


#ROOT directory
%dir %{basedir}
%{basedir}/build.xml
%doc %{basedir}/COPYRIGHT.txt
%doc %{basedir}/LICENSE.txt
%{basedir}/parsertargets
%doc %{basedir}/readme.txt
%{basedir}/smartfrog-version.properties


#Bin directory and beneath
%attr(755, ${rpm.username},${rpm.groupname}) %dir %{bindir}
#these are config files that should be protected
#see http://www-uxsup.csx.cam.ac.uk/~jw35/docs/rpm_config.html for info on this
#option
%config(noreplace) %{bindir}/default.ini
%config(noreplace) %{bindir}/default.sf

%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/smartfrog
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/setSFDefaultProperties
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/setSFDynamicClassLoadingProperties
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/setSFProperties
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/setSFSecurityProperties
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfDaemon
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfDetachAndTerminate
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfDiag
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfDiagnostics
#%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfGui
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfManagementConsole
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfParse
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfPing
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfRun
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfStart
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfStop
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfStopDaemon
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfTerminate
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfUpdate
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfVersion
#%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/
%{bindir}/*.bat
#bin/metadata
%{bindir}/metadata
#bin/security
%dir %{bindir}/security
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/smartfrog
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfDaemon
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfDetachAndTerminate
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfManagementConsole
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfParse
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfRun
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfStart
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfStop
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfStopDaemon
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfTerminate
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfUpdate
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfVersion
%{binsecurity}/*.bat

#now the files in the lib directory...use ant library versions to 
#include version numbers
%dir %{libdir}
%{libdir}/smartfrog-${smartfrog.version}.jar
%{libdir}/sfExamples-${smartfrog.version}.jar
%{libdir}/sfServices-${smartfrog.version}.jar


#the links directory 
%attr(755, ${rpm.username},${rpm.groupname}) %dir %{basedir}/links
%{linkdir}/smartfrog.jar
%{linkdir}/sfExamples.jar
%{linkdir}/sfServices.jar


#other directories
%{basedir}/testCA




#the log output directory
#this is no longer world writeable, as the logging can fall back gracefully now 
%attr(755, ${rpm.username},${rpm.groupname}) ${rpm.log.dir}

#and the shell scripts, which belong to root
#these are not executable, because they are meant to be sourced
%attr(0644, root,root) /etc/profile.d/smartfrog.sh
%attr(0644, root,root) /etc/profile.d/smartfrog.csh
%attr(0755, root,root) ${rpm.etc.dir}


%docdir %{docs}
%{docs}
%doc %{basedir}/src.zip
# -----------------------------------------------------------------------------
# RPM Security section.
# When secure RPMs are created. then signedLib is a symlink and not a directory
# -----------------------------------------------------------------------------

#the signedLib which used to be a directory, but which in the RPMs is a symbolic link
#
%{basedir}/signedLib

# some switches; still experimenting with those
#%{!?_private_rpm:%dir %{signedlib}}
#%{?_private_rpm:%{signedlib}}

# -----------------------------------------------------------------------------
# this is the private dir unless the build says otherwise
%{!?_private_rpm:%{privatedir}}

# -----------------------------------------------------------------------------
# the security keys file only has content in it when the build property says so
%files private-security-keys
%{?_private_rpm:%{privatedir}}
#uncomment this to force in a host. It is here more as a development utility than
#anything anyone should need
#%{?_private_rpm:%{privatedir}/host1}



%post  private-security-keys
#on a private installation, we crank back the security rights to be restricted to the user for which the RPM
#is targeted, with permissions as set at built time, ideally to something restrictive
%{?_private_rpm:chmod ${rpm.private.hosts.permissions} %{privatedir}/host*}
%{?_private_rpm:chown ${rpm.username} %{privatedir}/host*}



%files demo
%defattr(0644,${rpm.username},${rpm.username},0755)
%{srcdir}

# -----------------------------------------------------------------------------
# the daemon is set up to autorun
%post daemon

if [ -x /usr/lib/lsb/install_initd ]; then
# this is the SuSE/LSB executable; not found in ubuntu without the LSB deb
# installed
  /usr/lib/lsb/install_initd /etc/init.d/${rpm.daemon.name}
elif [ -x /sbin/chkconfig ]; then
# found in RHEL, Fedora platforms 
  /sbin/chkconfig --add ${rpm.daemon.name}
else
#no explicit support (yet!). Will include debian systems without LSB
   for i in 2 3 4 5; do
        ln -sf /etc/init.d/${rpm.daemon.name} /etc/rc.d/rc${i}.d/S${rpm.daemon.start.number}${rpm.daemon.name}
   done
   for i in 1 6; do
        ln -sf /etc/init.d/${rpm.daemon.name} /etc/rc.d/rc${i}.d/K${rpm.daemon.stop.number}${rpm.daemon.name}
   done
fi


%preun daemon
# shut down the daemon before the uninstallation
%{smartfrogd} stop

#we have to run these before uninstalling the files, because 
#chkconfig and install_initd both read (different) comment headers
#in the initd script
if [ "$1" = "0" ] ; then
  if [ -x /usr/lib/lsb/remove_initd ]; then
    /usr/lib/lsb/install_initd /etc/init.d/${rpm.daemon.name}
  elif [ -x /sbin/chkconfig ]; then
    /sbin/chkconfig --del ${rpm.daemon.name} || echo "trouble shutting down the daemon"
  else
    rm -f /etc/rc.d/rc?.d/???${rpm.daemon.name}
  fi
fi






%files daemon
#and the etc stuff
%defattr(0644,root,root,0755)
%attr(755, root,root) /etc/rc.d/init.d/${rpm.daemon.name}
%attr(0644,root,root) /etc/sysconfig/smartfrog


%files ant

%{libdir}/sf-ant-${smartfrog.version}.jar
%{libdir}/ant-${apache.ant.version}.jar
%{libdir}/ant-launcher-${apache.ant.version}.jar
%{linkdir}/sf-ant.jar
%{linkdir}/ant.jar
%{linkdir}/ant-launcher.jar


%files anubis

%{libdir}/sf-anubis-${smartfrog.version}.jar
%{linkdir}/sf-anubis.jar

%files csvfiles

%{libdir}/sf-csvfiles-${smartfrog.version}.jar
%{libdir}/opencsv-${opencsv.version}.jar
%{linkdir}/sf-csvfiles.jar
%{linkdir}/opencsv.jar

%files database

%{libdir}/sf-database-${smartfrog.version}.jar
%{linkdir}/sf-database.jar

%files jmx

%{libdir}/sf-jmx-${smartfrog.version}.jar
%{libdir}/mx4j-${mx4j.version}.jar
%{libdir}/mx4j-remote-${mx4j.version}.jar
%{libdir}/mx4j-jmx-${mx4j.version}.jar
%{libdir}/mx4j-tools-${mx4j.version}.jar
%{linkdir}/sf-jmx.jar
%{linkdir}/mx4j.jar
%{linkdir}/mx4j-remote.jar
%{linkdir}/mx4j-jmx.jar
%{linkdir}/mx4j-tools.jar


%files logging

%{libdir}/sf-loggingservices-${smartfrog.version}.jar
%{libdir}/commons-logging-${commons-logging.version}.jar
%{libdir}/log4j-${log4j.version}.jar
%{linkdir}/sf-loggingservices.jar
%{linkdir}/commons-logging.jar
%{linkdir}/log4j.jar



%files networking

%{libdir}/sf-dns-${smartfrog.version}.jar
%{libdir}/sf-emailer-${smartfrog.version}.jar
%{libdir}/sf-net-${smartfrog.version}.jar
%{libdir}/sf-ssh-${smartfrog.version}.jar
%{libdir}/dnsjava-${dnsjava.version}.jar
%{libdir}/mail-${mail.version}.jar
%{libdir}/activation-${activation.version}.jar
%{libdir}/commons-net-${commons-net.version}.jar
%{libdir}/oro-${oro.version}.jar
%{libdir}/jsch-${jsch.version}.jar


%{linkdir}/sf-dns.jar
%{linkdir}/sf-emailer.jar
%{linkdir}/sf-net.jar
%{linkdir}/sf-ssh.jar
%{linkdir}/dnsjava.jar
%{linkdir}/mail.jar
%{linkdir}/activation.jar
%{linkdir}/commons-net.jar
%{linkdir}/oro.jar
%{linkdir}/jsch.jar

%files quartz

%{libdir}/sf-quartz-${smartfrog.version}.jar
%{libdir}/quartz-${quartz.version}.jar

%{linkdir}/sf-quartz.jar
%{linkdir}/quartz.jar


%files scripting
%{libdir}/sf-scripting-${smartfrog.version}.jar
%{libdir}/bsh-${bsh.version}.jar

%{linkdir}/sf-scripting.jar
%{linkdir}/bsh.jar

%files xunit
%{libdir}/sf-xunit-${smartfrog.version}.jar

%{linkdir}/sf-xunit.jar

%files junit
%{libdir}/sf-junit-${smartfrog.version}.jar
%{libdir}/junit-${junit.version}.jar

%{linkdir}/sf-junit.jar
%{linkdir}/junit.jar


%files velocity
%{libdir}/sf-velocity-${smartfrog.version}.jar
%{libdir}/velocity-${velocity.version}.jar
%{libdir}/velocity-dep-${velocity.version}.jar
%{libdir}/commons-collections-${commons-collections.version}.jar
%{libdir}/commons-lang-${commons-lang.version}.jar

%{linkdir}/sf-velocity.jar
%{linkdir}/velocity.jar
%{linkdir}/velocity-dep.jar
%{linkdir}/commons-collections.jar
%{linkdir}/commons-lang.jar

%files www
%{libdir}/sf-www-${smartfrog.version}.jar
%{libdir}/sf-jetty-${smartfrog.version}.jar
%{libdir}/jetty-${jetty.version}.jar
%{libdir}/jetty-util-${jetty.version}.jar
%{libdir}/servlet-api-${servletapi.version}.jar
%{libdir}/commons-codec-${commons-codec.version}.jar
%{libdir}/commons-httpclient-${commons-httpclient.version}.jar

%{linkdir}/sf-www.jar
%{linkdir}/sf-jetty.jar
%{linkdir}/servlet-api.jar
%{linkdir}/jetty.jar
%{linkdir}/jetty-util.jar
%{linkdir}/commons-codec.jar
%{linkdir}/commons-httpclient.jar

%files xml
%{libdir}/sf-xml-${smartfrog.version}.jar
%{libdir}/jdom-${jdom.version}.jar
%{libdir}/xom-${xom.version}.jar
%{libdir}/xmlParserAPIs-${xerces.version}.jar
%{libdir}/xercesImpl-${xerces.version}.jar
%{libdir}/xalan-${xalan.version}.jar

%{linkdir}/sf-xml.jar
%{linkdir}/jdom.jar
%{linkdir}/xom.jar
%{linkdir}/xmlParserAPIs.jar
%{linkdir}/xercesImpl.jar
%{linkdir}/xalan.jar

%files xmpp
%{libdir}/sf-xmpp-${smartfrog.version}.jar
%{libdir}/smack-${smack.version}.jar

%{linkdir}/sf-xmpp.jar
%{linkdir}/smack.jar


# -----------------------------------------------------------------------------

# to get the date, run:   date +"%a %b %d %Y"
%changelog
* Fri Sep 26 2008 Steve Loughran <smartfrog@hpl.hp.com> 3.12.043-1.el4 changes to the security model so that signedLib is a symlink.
* Tue Sep 16 2008 Steve Loughran <smartfrog@hpl.hp.com> 3.12.042-2.el4 changes to the security model so that signedLib is a symlink.
* Mon May 12 2008 Steve Loughran <smartfrog@hpl.hp.com> 3.12.027-2.el4
- add velocity template
* Thu Jan 24 2008 Steve Loughran <smartfrog@hpl.hp.com> 3.12.018-2.el4
- add ability to generate signed RPM files
* Mon Dec 03 2007 Steve Loughran <smartfrog@hpl.hp.com> 3.12.013-1.el4
- add the javadocs RPM
- remove og-w permissions from the log directory 
* Wed Nov 21 2007 Steve Loughran <smartfrog@hpl.hp.com> 3.12.011-1.el4
- add the ant, database, jmx, junit,networking, quartz, scrpting, www, xml, xmpp,
  xunit RPMs.
* Wed Oct 24 2007 Steve Loughran <smartfrog@hpl.hp.com> 3.12.008-1.el4
- use RHEL-specific distribution tags
- change permissions on profile.d scripts
- set up symbolic links using rpmbuild instead of custom post install scripts.
* Mon Sep 17 2007 Steve Loughran <smartfrog@hpl.hp.com> 3.12.003-1
- all cleanup is skipped during upgrades, so that rpm --upgrade should now work properly.
- link removal is moved to the pre-uninstall phase, so that chkconfig and install_initd have the
  daemon file (with metadata in its comments) to work on
- lib dir is explicitly listed with permissions
- chkconfig is used where present (RHEL and Fedora systems)
- /usr/lib/lsb/install_initd is used where present (SuSE systems, and others
  with Linux Standard Base installed) 
* Wed Jul 25 2007 Steve Loughran <smartfrog@hpl.hp.com> 3.11.0005-1
- daemon RPM now runs "smartfrogd stop" before uninstalling
- smartfrog RPM tries to terminate any running smartfrog process before uninstalling
- anubis RPM provides the anubis JAR
- logging RPM provides logging services and dependent JARs
- links without version information added to the dir /opt/smartfrog/links subdirectory for each JAR.
* Fri Jul 20 2007 Steve Loughran <smartfrog@hpl.hp.com> 3.11.003-5
- daemon RPM now runs "smartfrogd shutdown" before uninstalling 
* Tue Jul 03 2007 Steve Loughran <smartfrog@hpl.hp.com> 3.11.001-4
- moved scripts to smartfrog.rpm
- moved directories
* Fri Jun 22 2007 Steve Loughran <smartfrog@hpl.hp.com> 3.11.001-3
- fixing permissions of the log directory; creating a new user on demand
* Tue May 22 2007 Steve Loughran <smartfrog@hpl.hp.com> 3.11.000-1
- Built from contributions and the JPackage template


# install statements
#%install
#mkdir -p ${RPM_BUILD_ROOT}/%{prefix}
#cd SmartFrog.${smartfrog.version}
#cd ..
#cp -R SmartFrog.${smartfrog.version} ${RPM_BUILD_ROOT}/%{prefix}

#%clean
#rm -rf ${RPM_BUILD_ROOT}




