sfLog().info("Installing  Hadoop Worker in $destDir")

def hadoopVersion = sfResolve("hadoopVersion")
def hadoopFile = "hadoop-${hadoopVersion}"
def hadoopTar = "${hadoopFile}.tar.gz"
sfLog().info("unpacking Hadoop ${hadoopTar} binaries to $destDir")


unpack("$destDir/${hadoopFile}.tar.gz").waitFor()

move("$destDir/${hadoopFile}", "$destDir")
