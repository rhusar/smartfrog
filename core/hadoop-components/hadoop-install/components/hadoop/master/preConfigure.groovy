def host = command("hostname -f").text

sfLog().info("Preconfigure for Hadoop Master on $host")

def baseDir = sfResolve("directory")

if (sfResolve("ibrix")) {
    // setup ibrix client
    command("cd $comp.fusionManager.ibrixDir; ./ibrixinit -tc -Ceth0 -i $comp.fusionManager.vm.ipAddress")
    // mount filesystem
    command("cd $comp.fusionManager.ibrixBinDir; ./ibrix_lwmount -a")

    command("cd $comp.fusionManager.ibrixBinDir; ./ibrix_lwmount -f $comp.fusionManager.filesystem -m /$comp.fusionManager.filesystem")

    copy("getsegmentsperhost.py", "$baseDir/getsegmentsperhost.py")

    command("chmod +x $baseDir/getsegmentsperhost.py")
}

def files = ["hadoop-env.sh", "core-site.xml", "mapred-site.xml", "hdfs-site.xml"]
files.each {
    copy("$it", "$baseDir/conf/$it")
}

files = ["core-site.xml", "mapred-site.xml", "hdfs-site.xml"]
files.each {
    parse("$baseDir/conf/$it")
}

copy("masterScript.sh", "$baseDir/bin/masterScript.sh")
command("chmod +x $baseDir/bin/masterScript.sh")

// format namenode
command("$baseDir/bin/hadoop namenode -format")

// export hadoop home
command("echo \"export HADOOP_HOME=$baseDir\" >> /root/.bashrc").waitFor()