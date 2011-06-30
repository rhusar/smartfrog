#!/usr/bin/python
"""Friendlier interface to the raw kernel enumerations

Create an KernelInfo object and use it to get the IBRIX
kernel module's list of hosts, filesystems or segments.

Once the reference is found it will print out which segments are attached to which host

"""

from ibrixkernel import *
import sys, socket

info = KernelInfo()

#cache hosts
hosts = {}
for host in info.hosts():
	hosts[host.uuid] = host

for system in info.filesystems():
	for segment in info.segments(system):
		hostname = socket.gethostbyaddr(hosts[segment.owneruuid].ip)[0]
		print "%s Segment %2s on %s (%s)" % (system.fsname,segment.segnum, hosts[segment.owneruuid],hostname)