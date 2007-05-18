Name: smartfrog
Version: ${smartfrog.version}
Release: 1
Group: Framework
Source: smartfrog.${smartfrog.version}.tar.gz
License: LGPL
URL: http://www.smartfrog.org
Packager: SmartFrog Team
BuildArch: noarch
Summary: SmartFrog framework distribution.
BuildRoot: %{_tmppath}/%{name}-root
Prefix: /opt
Provides: SmartFrog
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

%prep
%setup -c

%install
mkdir -p ${RPM_BUILD_ROOT}/%{prefix}
cd SmartFrog.${smartfrog.version}
cd ..
cp -R SmartFrog.${smartfrog.version} ${RPM_BUILD_ROOT}/%{prefix}

%clean
rm -rf ${RPM_BUILD_ROOT}

%files
%defattr(-,root,root)
%{prefix}/SmartFrog.${smartfrog.version}
