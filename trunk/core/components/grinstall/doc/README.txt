Author: Benjamin Koenig
State: Basically working, able to deploy Hadoop, no tests yet

This project provides a SmartFrog addon which enable users to deploy diverse applications (i.e. Hadoop, Ganglia)
without having to write Java code or create JAR files. Instead it uses Groovy scripts in the filesystem to 
define actions, and SmartFrog .sf files to provide the configuration/control data.

Key Concepts

* Targets Unix only; assumes the normal Linux toolchain is present.
* Originally written for post-instantiation deployment on virtual machines, it assumes
  the user is running as root, at least for the example Hadoop files.
    
    
 HOW TO BUILD
------------

ant clean
ant package         // creates sf-grinstall.jar (dist/lib)
ant ivy-retrieve    // retrieves 3rdparty jars (build/ivy/lib/runtime)
ant publish         // publishes the JAR and its dependency metadata to the local Ivy cache.

Each VM has to have the installer jar and all its dependencies! You can set SFUSERHOME, SFUSERHOME1, etc.,
to set the classpath of the SmartFrog daemon.

The following files have to be in the classpath:

- sf-grinstall.jar (all daemons)
- 3rdparty jars (all daemons)
- the .sf file of each component you use (only initial daemon)

HOW TO DEPLOY
-------------

You have to write a SmartFrog component description file (see components/example.sf) describing your
application landscape or "Architecture". An "Architecture" is a SmartFrog Compound consisting of several
"Component" subcomponents. The description for a "Component" may contain:

- An "sfProcessHost" attribute which defines the destination host. Querying a service for a deployment destination is
planned but not yet implemented.

- The "directory" attribute defines a directory on the destination host for all source downloads and task files.

- a variable number of ISource subcomponents (see src/org/smartfrog/services/groovy/install/base.sf) which 
takes care of downloading files before installation.

Currently, "installer.download.Source" is the only Java class implementing ISource. 

It wraps the Apache Commons VFS API and you can use it to download the contents of a directory, tar or JAR file, or files individually.
The following file systems are currently supported:

    * FTP
    * Local Files
    * HTTP and HTTPS
    * SFTP (provided jsch is on the classpath, which comes from sf-ssh)
    * Temporary Files
    * Zip, Jar and Tar (uncompressed, tgz or tbz2)
    * gzip and bzip2
    * res
    * ram
    * mime

(see: http://commons.apache.org/vfs/filesystems.html)

- A task file for each step in the installation process. By default the following Groovy scripts are executed in order:

install.groovy, preConfigure.groovy, start.groovy, and postConfigure.groovy

Within task files you can execute sfResolve to retrieve attributes from the component description and use helper
methods which are defined in the Java class "install.task.Helper". You may also resolve remote references by
refering to remote components by their name, e.g.:

sfResolve("master:iBrix")

Some more examples of what you can do in a task file:

def host = command("hostname").text // executes the command "hostname" on the shell and retrieves the output

def baseDir = sfResolve("directory")

sfLog().info("Installing Application on $host")

sfLog().debug("Some VFS functions")

move("$baseDir/example.txt", "$baseDir/example2.txt")

copy("$baseDir/example2.txt", "$baseDir/example3.txt")

delete("$baseDir/example2.txt")

If you have any questions or suggestions for further development, please feel free to contact the smartfrog
mailing list.

Using the components
====================
