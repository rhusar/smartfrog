log("Installing  Hadoop Worker in $destDir")

def hadoopVersion = sfResolve("hadoopVersion")
def hadoopFile = "hadoop-${hadoopVersion}"
log("unpacking Hadoop ${hadoopFile} to $destDir")


unpack("$destDir/${hadoopFile}.tar.gz").waitFor()

move("$destDir/${hadoopFile}", "$destDir")
