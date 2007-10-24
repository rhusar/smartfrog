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

# TODO: menu entries

# if menu entries are created, define Summary here, and use it in the summary
# tag, and the menu entries' descriptions

%define javadir         %{_datadir}/java
%define javadocdir      %{_datadir}/javadoc
%define section         free

%define approot         %{_datadir}/smartfrog
%define basedir         ${rpm.install.dir}
%define bindir          %{basedir}/bin
%define binsecurity     %{bindir}/security
%define libdir          %{basedir}/lib
%define docs            %{basedir}/docs
%define srcdir          %{basedir}/src
%define linkdir         %{basedir}/links
%define examples        %{srcdir}/org/smartfrog/examples
%define rcd             /etc/rc.d
%define smartfrogd      %{rcd}/init.d/${rpm.daemon.name}
%define logdir          ${rpm.log.dir}

#some shortcuts
%define smartfrog.jar smartfrog-${smartfrog.version}.jar
%define sfExamples.jar sfExamples-${smartfrog.version}.jar
%define sfServices.jar sfServices-${smartfrog.version}.jar

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
Source0: %{name}-%{version}.tar.gz 
# add patches, if any, here
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root
#BuildRoot:      %{basedir}
Prefix: ${rpm.prefix}
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

This RPM installs smartfrog into 
 %{basedir} 
It also adds scripts to /etc/profile.d and /etc/sysconfig 
so that SmartFrog is available on the command line.

In this RPM SmartFrog is configured to log to files 
    /var/log/smartfrog_*.log
with logLevel=3 (INFO) using LogToFileImpl. The GUI is turned off.



# -----------------------------------------------------------------------------

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

%package logging
Group:         ${rpm.framework}
Summary:        SmartFrog logging services
Requires:       %{name} = %{version}-%{release}
#
%description logging
This package integrates SmartFrog with Apache Log4j. It includes the Apache
commons-logging-${commons-logging.version} and log4j-${log4j.version} libraries


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
#ls -l $RPM_BUILD_ROOT/usr/share




# jar
#install -d $RPM_BUILD_ROOT%{javadir}
# install jars to $RPM_BUILD_ROOT%{javadir}/ (as %{name}-%{version}.jar)
#(cd $RPM_BUILD_ROOT%{javadir} && for jar in *-%{version}.jar; do ln -sf ${jar} `echo $jar| sed  "s|-%{version}||g"`; done)

# javadoc
#install -d $RPM_BUILD_ROOT%{javadocdir}/%{name}-%{version}/
# cp -pr javadocs to $RPM_BUILD_ROOT%{javadocdir}/%{name}-%{version}/

# demo
#install -d $RPM_BUILD_ROOT%{_datadir}/%{name}-%{version}
# cp demos to $RPM_BUILD_ROOT%{_datadir}/%{name}-%{version}/

# -----------------------------------------------------------------------------


%post 




%preun
#about to uninstall, but all the files are already present
#%{bindir}/smartfrog -a rootProcess:TERMINATE:::localhost: -e -quietexit

#%postun
#at uninstall time, we delete 
#the links
#if [ "$1" = "0" ] ; then

#fi

# -----------------------------------------------------------------------------

%clean
rm -rf $RPM_BUILD_ROOT

# -----------------------------------------------------------------------------

%files
%defattr(0644,${rpm.username},${rpm.groupname},0755)


#ROOT directory
%dir %{basedir}
%{basedir}/build.xml
%doc %{basedir}/changes.txt
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
%{basedir}/private
%{basedir}/signedLib

#the log output directory
#this is world writeable, so that anyone can run SmartFrog
%attr(777, ${rpm.username},${rpm.groupname}) ${rpm.log.dir}

#and the shell scripts, which belong to root
#these are not executable, because they are meant to be sourced
%attr(0644, root,root) /etc/profile.d/smartfrog.sh
%attr(0644, root,root) /etc/profile.d/smartfrog.csh
%attr(755, root,root) ${rpm.etc.dir}

#%doc # add docs here
#%{javadir}/*

#%files manual
#%defattr(0644,root,root,0755)

%docdir %{docs}
%{docs}
%doc %{basedir}/src.zip

# %dir %{docs}
# %dir %{docs}/images
# %dir %{docs}/skin
# %dir %{docs}/components
# %dir %{docs}/openOfficeEmbeddedImage

#%files javadoc
#%defattr(0644,root,root,0755)
#%{javadocdir}/%{name}-%{version}

%files demo
%defattr(0644,${rpm.username},${rpm.username},0755)
#%{_datadir}/%{name}-%{version}
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


%files anubis

%{libdir}/sf-anubis-${smartfrog.version}.jar
%{linkdir}/sf-anubis.jar


%files logging

%{libdir}/sf-loggingservices-${smartfrog.version}.jar
%{libdir}/commons-logging-${commons-logging.version}.jar
%{libdir}/log4j-${log4j.version}.jar

%{linkdir}/sf-loggingservices.jar
%{linkdir}/commons-logging.jar
%{linkdir}/log4j.jar


# -----------------------------------------------------------------------------

%changelog
# to get the date, run:   date +"%a %b %d %Y"
* Wed Oct 24 2007 Steve Loughran <smartfrog@hpl.hp.com> 3.12.008-1.el4
- use RHEL-specific distribution tags
- change permissions on profile.d scripts
- set up symbolic links using rpmbuild instead of custom post install scripts.
* Mon Sep 17 2007 Steve Loughran <steve_l@users.sourceforge.net> 3.12.003-1
- all cleanup is skipped during upgrades, so that rpm --upgrade should now work properly.
- link removal is moved to the pre-uninstall phase, so that chkconfig and install_initd have the
  daemon file (with metadata in its comments) to work on
- lib dir is explicitly listed with permissions
- chkconfig is used where present (RHEL and Fedora systems)
- /usr/lib/lsb/install_initd is used where present (SuSE systems, and others
  with Linux Standard Base installed) 
* Wed Jul 25 2007 Steve Loughran <steve_l@users.sourceforge.net> 3.11.0005-1
- daemon RPM now runs "smartfrogd stop" before uninstalling
- smartfrog RPM tries to terminate any running smartfrog process before uninstalling
- anubis RPM provides the anubis JAR
- logging RPM provides logging services and dependent JARs
- links without version information added to the dir /opt/smartfrog/links subdirectory for each JAR.
* Fri Jul 20 2007 Steve Loughran <steve_l@users.sourceforge.net> 3.11.003-5
- daemon RPM now runs "smartfrogd shutdown" before uninstalling 
* Tue Jul 03 2007 Steve Loughran <steve_l@users.sourceforge.net> 3.11.001-4
- moved scripts to smartfrog.rpm
- moved directories
* Fri Jun 22 2007 Steve Loughran <steve_l@users.sourceforge.net> 3.11.001-3
- fixing permissions of the log directory; creating a new user on demand
* Tue May 22 2007 Steve Loughran <steve_l@users.sourceforge.net> 3.11.000-1
- Built from contributions and the JPackage template


# install statements
#%install
#mkdir -p ${RPM_BUILD_ROOT}/%{prefix}
#cd SmartFrog.${smartfrog.version}
#cd ..
#cp -R SmartFrog.${smartfrog.version} ${RPM_BUILD_ROOT}/%{prefix}

#%clean
#rm -rf ${RPM_BUILD_ROOT}




