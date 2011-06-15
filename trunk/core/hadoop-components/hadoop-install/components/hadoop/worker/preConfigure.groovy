def host = command("hostname").text

sfLog().info("Preconfigure for Hadoop Slave on $host")

def baseDir = sfResolve("directory")

if (sfResolve("master:cloudera")) {

    sfLog().info("Pre-configuration for cloudera distribution")

    // copy setup files
    copy("hadoop-env.sh", "${baseDir}/conf/hadoop-env.sh")

    files = ["mapred-site.xml", "core-site.xml", "hdfs-site.xml"]
    files.each{
        copy("$it", "${baseDir}/conf/$it")
        parse("${baseDir}/conf/$it")
    }

    copy("slaveScript.sh", "${baseDir}/bin/slaveScript.sh")
    command("chmod +x ${baseDir}/bin/slaveScript.sh")

    copy("getsegmentsperhost.py", "${baseDir}/getsegmentsperhost.py")
    command("chmod +x ${baseDir}/getsegmentsperhost.py")

    // do we need to mount an extra filesystem
    if (sfResolve("userVolumes", new Vector(), false).size() == 1) {
        // assume /dev/sdb
        command("mkfs.ext3 -F /dev/sdb")
        command("mkdir /mnt/data")
        command("echo /dev/sdb             /mnt/data            ext3       defaults    0 0 >> /etc/fstab")
        command("mount -a")
        command("chown -R sup:users /mnt/data/")
    }

    // set owner for logging directory
    // create logging directory
    command("mkdir ${baseDir}/logs")
    command("chown -R sup:users ${baseDir}")
} else {
    def files = ["hadoop-env.sh", "mapred-site.xml", "core-site.xml", "hdfs-site.xml"]
    files.each {
        copy("$it", "$baseDir/conf/$it")
    }

    files = ["mapred-site.xml", "core-site.xml", "hdfs-site.xml"]
    files.each {
        parse("$baseDir/conf/$it")
    }

    copy("slaveScript.sh", "$baseDir/bin/slaveScript.sh")
    command("chmod +x $baseDir/bin/slaveScript.sh")

    copy("getsegmentsperhost.py", "$baseDir/getsegmentsperhost.py")
    command("chmod +x $baseDir/getsegmentsperhost.py")

    // do we need to mount an extra filesystem
    if (sfResolve("userVolumes", new Vector(), false).size() == 1) {
        // assume /dev/sdb
        command("mkfs.ext3 -F /dev/sdb")
        command("mkdir /mnt/data")
        command("echo /dev/sdb  /mnt/data   ext3  defaults  0 0 >> /etc/fstab")
        command("mount -a")
        command("chown -R sup:users /mnt/data/")
    }

    // set owner for logging directory
    // create logging directory
    command("mkdir $baseDir/logs")
    command("chown -R sup:users $baseDir").waitFor()
}