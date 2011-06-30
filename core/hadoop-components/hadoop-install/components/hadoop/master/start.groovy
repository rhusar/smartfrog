package hadoop.master;

log("Starting Hadoop Master")

def binDir = "${destDir}/bin"
def cmd = "bash $binDir/worker.sh $binDir "
exec("$cmd stop jobtracker", binDir)
exec("$cmd stop tasktracker", binDir)
exec("$cmd stop datanode", binDir)
exec("$cmd stop namenode", binDir)

failIf(exec("$cmd start datanode", binDir), "could not start datanode")
failIf(exec("$cmd start tasktracker", binDir), "could not start tasktracker")
failIf(exec("$cmd start namenode", binDir), "could not start namenode")
failIf(exec("$cmd start jobtracker", binDir), "could not start jobtracker")
log("Started Hadoop Master in ${binDir}")