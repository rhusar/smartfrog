def host = command("hostname").text


sfLog().info("Preconfigure for Hadoop Worker on $host")
def baseDir = sfResolve("directory")
// do we need to mount an extra filesystem
def userVolumes = sfResolve("userVolumes", new Vector(), false)
def mountDrive = sfResolve("mountDrive", "/dev/sdb", false);
sfLog().info("Basedir is $baseDir, userVolumes = $userVolumes, mountDrive = $mountDrive")


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


if (sfResolve("ibrix")) {
//this file is used by IBRIX but it is always stuck in
    copy("getsegmentsperhost.py", "$baseDir/getsegmentsperhost.py")
    command("chmod +x $baseDir/getsegmentsperhost.py")
} 
//now create any user volume if needed, as /mnt/data
if (userVolumes.size() == 1) {
    command("mkfs.ext3 -F $mountDrive")
    command("mkdir /mnt/data")
    command("echo $mountDrive /mnt/data   ext3  defaults  0 0 >> /etc/fstab")
    command("mount -a")
    command("chown -R sup:users /mnt/data/")
}

// set owner for logging directory
// create logging directory
command("mkdir $baseDir/logs")
command("chown -R sup:users $baseDir").waitFor()