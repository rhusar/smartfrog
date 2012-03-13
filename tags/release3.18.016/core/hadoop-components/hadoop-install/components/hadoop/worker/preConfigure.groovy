package hadoop.worker;


log("Preconfigure for Hadoop Worker")
// do we need to mount an extra filesystem
def userVolumes = sfResolve("userVolumes", new Vector(), false)
def mountDrive = sfResolve("mountDrive", "/dev/sdb", false);
def user = sfResolve("user")
log("Basedir is $destDir, userVolumes = $userVolumes, mountDrive = $mountDrive user=$user")


["hadoop-env.sh", "mapred-site.xml", "core-site.xml", "hdfs-site.xml"].each {
    copy("$it", "$destDir/conf/$it")
}

["mapred-site.xml", "core-site.xml", "hdfs-site.xml"].each {
    parse("$destDir/conf/$it")
}

copy("worker.sh", "$destDir/bin/worker.sh")
exec("chmod +x $destDir/bin/worker.sh")


if (sfResolve("ibrix")) {
//this file is used by IBRIX but it is always stuck in
    copy("getsegmentsperhost.py", "$destDir/getsegmentsperhost.py")
    exec("chmod +x $destDir/getsegmentsperhost.py")
} 
//now create any user volume if needed, as /mnt/data
if (userVolumes.size() == 1) {
    exec("mkfs.ext3 -F $mountDrive")
    exec("mkdir /mnt/data")
    exec("echo $mountDrive /mnt/data  ext3  defaults  0 0 >> /etc/fstab")
    command("mount -a")
    exec("chown -R sup:users /mnt/data/")
}

// set owner for logging directory
// create logging directory
exec("mkdir $destDir/logs")

//exec("chown -R ${user}:users $destDir")