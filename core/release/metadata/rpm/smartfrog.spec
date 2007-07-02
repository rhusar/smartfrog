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
%define examples        %{srcdir}/org/smartfrog/examples
%define rcd             /etc/rc.d
%define initsmartfrog   %{rcd}/init.d/hpsmartfrog
%define logdir          ${rpm.log.dir}
#this is some other log directory that gets picked up by logtofileimpl
#see http://jira.smartfrog.org/jira/browse/SFOS-235
%define logdir2         /tmp/sflogs

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

#In this RPM SmartFrog is configured to log to files /var/log/smartfrog_*.log with logLevel=3 (INFO)
#using LogToFileImpl. The GUI is turned off.

# -----------------------------------------------------------------------------

#%package manual
#Group:          Documentation
#Summary:        Manual for %{name}
#
#%description manual
#Documentation for %{name}.

# -----------------------------------------------------------------------------

#%package javadoc
#Group:          Documentation
#Summary:        Javadoc for %{name}
#
#%description javadoc
#Javadoc for %{name}.

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
This package provides the scripts for /etc/rc.d, /etc/profile.d and /etc/sysconfig for SmartFrog to be available on the command line and as a startup daemon. 

Running the SmartFrog as a daemon is a security risk unless the daemon is set up with security, especially if port 3800 is openened in the firewall. At that point anyone can get a process running as root to run any program they wish.


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

#after installing create a log directory that is world writeable, so that people running the init.d
#daemon by hand don't need to be root (SFOS-173)
mkdir -p %{logdir}
chmod a+wx %{logdir}
chgrp ${rpm.groupname} %{logdir}
chown ${rpm.username} %{logdir}
mkdir -p %{logdir2}
chmod a+wx %{logdir2}
chgrp ${rpm.groupname} %{logdir2}
chown ${rpm.username} %{logdir2}

%postun
#at uninstall time, we delete all logs
rm -rf %{logdir}
rm -rf %{logdir2}

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

#these are config files that should be protected
%config %{bindir}/default.ini
%config %{bindir}/default.sf

%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/smartfrog
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/setSFDefaultProperties
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/setSFDynamicClassLoadingProperties
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/setSFProperties
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/setSFSecurityProperties
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfDaemon
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfDetachAndTerminate
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfDiag
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfDiagnostics
%attr(755, ${rpm.username},${rpm.groupname}) %{bindir}/sfGui
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
#%attr(755, -, -) %{bindir}/
%{bindir}/*.bat
#bin/metadata
%{bindir}/metadata
#bin/security
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/smartfrog
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfDaemon
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfDetachAndTerminate
#%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfDiag
#%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfDiagnostics
#%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfGui
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfManagementConsole
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfParse
#%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfPing
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfRun
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfStart
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfStop
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfStopDaemon
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfTerminate
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfUpdate
%attr(755, ${rpm.username},${rpm.groupname}) %{binsecurity}/sfVersion
%{binsecurity}/*.bat

#now the files in the lib directory...use ant library versions to include version numbers
%{libdir}

#other directories
%{basedir}/testCA
%{basedir}/private
%{basedir}/signedLib



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
%defattr(0644,root,root,0755)
#%{_datadir}/%{name}-%{version}
%{srcdir}

# -----------------------------------------------------------------------------
#after installing, we set symlinks
%post daemon
rm -f %{rcd}/rc0.d/K60hpsmartfrog
rm -f %{rcd}/rc1.d/K60hpsmartfrog
rm -f %{rcd}/rc2.d/S60hpsmartfrog
rm -f %{rcd}/rc3.d/S60hpsmartfrog
rm -f %{rcd}/rc4.d/S60hpsmartfrog
rm -f %{rcd}/rc5.d/S60hpsmartfrog
rm -f %{rcd}/rc6.d/S60hpsmartfrog

ln -s %{initsmartfrog} %{rcd}/rc0.d/K60hpsmartfrog
ln -s %{initsmartfrog} %{rcd}/rc1.d/K60hpsmartfrog
ln -s %{initsmartfrog} %{rcd}/rc2.d/S60hpsmartfrog
ln -s %{initsmartfrog} %{rcd}/rc3.d/S60hpsmartfrog
ln -s %{initsmartfrog} %{rcd}/rc4.d/S60hpsmartfrog
ln -s %{initsmartfrog} %{rcd}/rc5.d/S60hpsmartfrog
ln -s %{initsmartfrog} %{rcd}/rc6.d/S60hpsmartfrog

# -----------------------------------------------------------------------------
# at uninstall time, we blow away the symlinks
%postun daemon
rm %{rcd}/rc0.d/K60hpsmartfrog
rm %{rcd}/rc1.d/K60hpsmartfrog
rm %{rcd}/rc2.d/S60hpsmartfrog
rm %{rcd}/rc3.d/S60hpsmartfrog
rm %{rcd}/rc4.d/S60hpsmartfrog
rm %{rcd}/rc5.d/S60hpsmartfrog
rm %{rcd}/rc6.d/S60hpsmartfrog

%files daemon
#and the etc stuff
%defattr(0644,root,root,0755)
%attr(755, root,root) /etc/rc.d/init.d/hpsmartfrog
%attr(755, root,root) /etc/profile.d/smartfrog.sh
%attr(755, root,root) /etc/profile.d/smartfrog.csh
/etc/sysconfig/smartfrog
# -----------------------------------------------------------------------------

%changelog
# to get the date, run:   date +"%a %b %d %y"

* Fri Jun 22 2007 Steve Loughran <steve_l@users.sourceforge.net> 3.11.0000-3
- fixing permissions of the log directory; creating a new user on demand
* Tue May 22 2007 Steve Loughran <steve_l@users.sourceforge.net> 3.11.0000-1
- Built from CERN contributions and the JPackage template


# CERN install statements
#%install
#mkdir -p ${RPM_BUILD_ROOT}/%{prefix}
#cd SmartFrog.${smartfrog.version}
#cd ..
#cp -R SmartFrog.${smartfrog.version} ${RPM_BUILD_ROOT}/%{prefix}

#%clean
#rm -rf ${RPM_BUILD_ROOT}

#%files
#%defattr(-,root,root)
#%{prefix}/SmartFrog.${smartfrog.version}



