package hadoop.master;

log("Preconfigure for Hadoop Master")

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
    copyAndParseTo("$it", "$destDir/conf/$it")
}

copy("worker.sh", "$destDir/bin/worker.sh")
exec("chmod +x $destDir/bin/worker.sh")

// format namenode
exec("$destDir/bin/hadoop namenode -format")

// export hadoop home
//command("echo \"export HADOOP_HOME=$destDir\" >> /root/.bashrc").waitFor()