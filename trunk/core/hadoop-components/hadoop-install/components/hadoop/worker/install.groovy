// At this point, we should have all the files and directories we need

def host = command("hostname").text

def hadoopVersion = "0.20.203.0"
def baseDir = sfResolve("directory")
sfLog().info("Installing  Hadoop ${hadoopVersion} Worker on $host in $baseDir")


unpack("$baseDir/${hadoopVersion}.tar.gz")

move("$baseDir/${hadoopVersion}", "$baseDir")
