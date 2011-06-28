def host = command("hostname -f").text

sfLog().info("Postconfigure for Hadoop Master on $host")

// namenode runs on port 50070
// jobtracker runs on port 50030
// enable access to everyone

def baseDir = sfResolve("directory")

command("$baseDir/bin/hadoop fs -chmod 777 /user", "$baseDir").waitFor()
command("$baseDir/bin/hadoop fs -chmod 777 /tmp/hadoop-${sfResolve("userName")}/mapred", "$baseDir").waitFor()