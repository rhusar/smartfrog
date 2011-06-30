package hadoop.master;

log("Postconfigure for Hadoop Master")


def binDir = "${destDir}/bin"
def cmd = "bash hadoop "
exec("$cmd fs -chmod 777 /user", binDir)
exec("$cmd -chmod 777 /tmp/hadoop-${sfResolve("user")}/mapred", binDir)