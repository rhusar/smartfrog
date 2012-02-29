log("Installing  Hadoop Worker in $destDir")

def hadoopVersion = sfResolve("hadoopVersion")
def expandTar = sfResolve("expandTar")
def hadoopBaseName = "hadoop-${hadoopVersion}"
if(expandTar) {
    log("Expanding  Hadoop ${hadoopBaseName} binaries to $destDir")
    failIf(unpack("$destDir/${hadoopBaseName}.tar.gz"), "could not expand the Hadoop tar file")
    move("$destDir/${hadoopBaseName}", "$destDir")
} else {
    log("Tar file expansion is skipped")
}

