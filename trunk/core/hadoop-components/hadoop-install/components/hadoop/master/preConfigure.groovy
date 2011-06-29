package hadoop.master;
def host = command("hostname -f").text

sfLog().info("Preconfigure for Hadoop Master on $host")

if (sfResolve("ibrix")) {
    // setup ibrix client
    command("cd $comp.fusionManager.ibrixDir; ./ibrixinit -tc -Ceth0 -i $comp.fusionManager.vm.ipAddress")
    // mount filesystem
    command("cd $comp.fusionManager.ibrixBinDir; ./ibrix_lwmount -a")

    command("cd $comp.fusionManager.ibrixBinDir; ./ibrix_lwmount -f $comp.fusionManager.filesystem -m /$comp.fusionManager.filesystem")

    copy("getsegmentsperhost.py", "$destDir/getsegmentsperhost.py")

    command("chmod +x $destDir/getsegmentsperhost.py")
}

def files = ["hadoop-env.sh", "core-site.xml", "mapred-site.xml", "hdfs-site.xml"]
files.each {
    copy("$it", "$destDir/conf/$it")
}

files = ["core-site.xml", "mapred-site.xml", "hdfs-site.xml"]
files.each {
    parse("$destDir/conf/$it")
}

copy("masterScript.sh", "$destDir/bin/masterScript.sh")
command("chmod +x $destDir/bin/masterScript.sh")

// format namenode
command("$destDir/bin/hadoop namenode -format")

// export hadoop home
command("echo \"export HADOOP_HOME=$destDir\" >> /root/.bashrc").waitFor()