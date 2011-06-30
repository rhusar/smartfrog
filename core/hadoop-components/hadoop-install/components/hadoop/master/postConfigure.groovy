package hadoop.master;
def host = command("hostname -f").text

sfLog().info("Postconfigure for Hadoop Master on $host")

// namenode runs on port 50070
// jobtracker runs on port 50030
// enable access to everyone


command("$destDir/bin/hadoop fs -chmod 777 /user", $destDir).waitFor()
command("$destDir/bin/hadoop fs -chmod 777 /tmp/hadoop-${sfResolve("user")}/mapred", $destDir).waitFor()