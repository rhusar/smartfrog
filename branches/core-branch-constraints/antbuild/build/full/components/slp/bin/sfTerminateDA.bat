@echo off
rem ##############################################################
rem # Service Location Protocol - SmartFrog components.
rem # Copyright (C) 2004 Glenn Hisdal <ghisdal(a)c2i.net>
rem # 
rem # This library is free software; you can redistribute it and/or
rem # modify it under the terms of the GNU Lesser General Public
rem # License as published by the Free Software Foundation; either
rem # version 2.1 of the License, or (at your option) any later version.
rem # 
rem # This library is distributed in the hope that it will be useful,
rem # but WITHOUT ANY WARRANTY; without even the implied warranty of
rem # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
rem # Lesser General Public License for more details.
rem # 
rem # You should have received a copy of the GNU Lesser General Public
rem # License along with this library; if not, write to the Free Software
rem # Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
rem # 
rem # This library was originally developed by Glenn Hisdal at the 
rem # European Organisation for Nuclear Research (CERN) in Spring 2004. 
rem # The work was part of a master thesis project for the Norwegian 
rem # University of Science and Technology (NTNU).
rem # 
rem # For more information: http://home.c2i.net/ghisdal/slp.html 
rem ##############################################################

setlocal
if (%1) == () goto usage

sfTerminate.bat %1 DirectoryAgent
GOTO end

:usage
echo Insufficient arguments to use sfTerminateDA 
echo Usage: sfTerminateDA HostName

:end
