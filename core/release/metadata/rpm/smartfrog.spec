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
# TODO: shell scripts
# TODO: server stuff, init scripts etc

# if menu entries are created, define Summary here, and use it in the summary
# tag, and the menu entries' descriptions

%define javadir         %{_datadir}/java
%define javadocdir      %{_datadir}/javadoc
%define section         free
%define approot         %{_datadir}/smartfrog
%define basedir         %{prefix}/smartfrog
%define bindir          %{basedir}/bin
%define binsecurity     %{bindir}/security
%define libdir          %{basedir}/lib
%define docdir          %{basedir}/doc


# -----------------------------------------------------------------------------

Summary:        SmartFrog Deployment Framework
Name:           smartfrog
Version:        ${smartfrog.version}
Release:        ${rpm.release.version}
# group, categories from freshmeat.net
Group:          ${rpm.framework}
License:        LGPL
URL:            http://www.smartfrog.org/
Packager:       SmartFrog Team
BuildArch:      noarch
#%{name}-%{version}.tar.gz in the SOURCES dir
#Source0:
# add patches, if any, here
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root
Prefix: /opt
Provides: SmartFrog
# build and runtime requirements here

%description
SmartFrog is a technology for describing distributed software systems as
collections of cooperating components, and then activating and managing them.

It was developed at HP Labs in Bristol, in the UK.
SmartFrog consists of a language for describing component collections and
component configuration parameters, and a runtime environment which
activates and manages the components to deliver and maintain running systems.
SmartFrog and its components are implemented in Java.

In this RPM SmartFrog is configured to log to files /var/log/smartfrog_*.log with logLevel=3 (INFO)
using LogToFileImpl. The GUI is turned off.

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

#%package demo
#Group:          # same as main package
#Summary:        Demos for %{name}
#Requires:       %{name} = %{version}-%{release}
#
#%description demo
#Demonstrations and samples for %{name}.

# -----------------------------------------------------------------------------

%prep
rm -rf $RPM_BUILD_ROOT
#%setup -q

# patches here
# remove stuff we'll build, eg. jars, javadocs, extra sources here

# -----------------------------------------------------------------------------

%build
# yep

# -----------------------------------------------------------------------------

%install
rm -rf $RPM_BUILD_ROOT

# jar
install -d $RPM_BUILD_ROOT%{javadir}
# install jars to $RPM_BUILD_ROOT%{javadir}/ (as %{name}-%{version}.jar)
(cd $RPM_BUILD_ROOT%{javadir} && for jar in *-%{version}.jar; do ln -sf ${jar} `echo $jar| sed  "s|-%{version}||g"`; done)

# javadoc
#install -d $RPM_BUILD_ROOT%{javadocdir}/%{name}-%{version}/
# cp -pr javadocs to $RPM_BUILD_ROOT%{javadocdir}/%{name}-%{version}/

# demo
#install -d $RPM_BUILD_ROOT%{_datadir}/%{name}-%{version}
# cp demos to $RPM_BUILD_ROOT%{_datadir}/%{name}-%{version}/

# -----------------------------------------------------------------------------

%clean
rm -rf $RPM_BUILD_ROOT

# -----------------------------------------------------------------------------

%files
%defattr(0644,-,-,0755)

#ROOT directory
%{basedir}/build.xml
%{basedir}/changes.txt
%{basedir}/COPYRIGHT.txt
%{basedir}/LICENSE.txt
%{basedir}/parsertargets
%{basedir}/readme.txt
%{basedir}/smartfrog-version.properties

#these are config files that should be protected
%config %{bindir}/default.ini
%config %{bindir}/default.sf

%attr(755, -, -) %{bindir}/smartfrog
%attr(755, -, -) %{bindir}/setSFDefaultProperties
%attr(755, -, -) %{bindir}/setSFDynamicClassLoadingProperties
%attr(755, -, -) %{bindir}/setSFProperties
%attr(755, -, -) %{bindir}/setSFSecurityProperties
%attr(755, -, -) %{bindir}/sfDaemon
%attr(755, -, -) %{bindir}/sfDetachAndTerminate
%attr(755, -, -) %{bindir}/sfDiag
%attr(755, -, -) %{bindir}/sfDiagnostics
%attr(755, -, -) %{bindir}/sfGui
%attr(755, -, -) %{bindir}/sfManagementConsole
%attr(755, -, -) %{bindir}/sfParse
%attr(755, -, -) %{bindir}/sfPing
%attr(755, -, -) %{bindir}/sfRun
%attr(755, -, -) %{bindir}/sfStart
%attr(755, -, -) %{bindir}/sfStop
%attr(755, -, -) %{bindir}/sfStopDaemon
%attr(755, -, -) %{bindir}/sfTerminate
%attr(755, -, -) %{bindir}/sfUpdate
%attr(755, -, -) %{bindir}/sfVersion
#%attr(755, -, -) %{bindir}/

%attr(755, -, -) %{binsecurity}/smartfrog
%attr(755, -, -) %{binsecurity}/sfDaemon
%attr(755, -, -) %{binsecurity}/sfDetachAndTerminate
%attr(755, -, -) %{binsecurity}/sfDiag
%attr(755, -, -) %{binsecurity}/sfDiagnostics
%attr(755, -, -) %{binsecurity}/sfGui
%attr(755, -, -) %{binsecurity}/sfManagementConsole
%attr(755, -, -) %{binsecurity}/sfParse
%attr(755, -, -) %{binsecurity}/sfPing
%attr(755, -, -) %{binsecurity}/sfRun
%attr(755, -, -) %{binsecurity}/sfStart
%attr(755, -, -) %{binsecurity}/sfStop
%attr(755, -, -) %{binsecurity}/sfStopDaemon
%attr(755, -, -) %{binsecurity}/sfTerminate
%attr(755, -, -) %{binsecurity}/sfUpdate
%attr(755, -, -) %{binsecurity}/sfVersion


#now the files in the lib directory...use ant library versions to include version numbers
%{libdir}/log4j-${log4.version}.jar
%{libdir}/sfExamples-${smartfrog.version}.jar
%{libdir}/sfServices-${smartfrog.version}.jar
%{libdir}/sf-tasks-${smartfrog.version}.jar
%{libdir}/smartfrog-${smartfrog.version}.jar


%doc # add docs here
#%{javadir}/*

#%files manual
#%defattr(0644,root,root,0755)
#%doc # add manual docs here

#%files javadoc
#%defattr(0644,root,root,0755)
#%{javadocdir}/%{name}-%{version}

#%files demo
#%defattr(0644,root,root,0755)
#%{_datadir}/%{name}-%{version}

# -----------------------------------------------------------------------------

%changelog
# to get the date, run:   date +"%a %b %d %y"

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



