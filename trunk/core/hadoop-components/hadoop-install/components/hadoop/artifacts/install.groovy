// At this point, we should have all the files and directories we need


def hadoopVersion = "0.20.203.0"
sfLog().info("Installing  Hadoop ${hadoopVersion} binaries to $destDir")


unpack("$destDir/${hadoopVersion}.tar.gz")

move("$destDir/${hadoopVersion}", "$destDir")
